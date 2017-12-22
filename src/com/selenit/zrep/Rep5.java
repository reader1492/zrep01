// Valerii Zinovev, Perm, 04 dec 2017
// Четвертый отчет (Отчет по всем заявкам)
package com.selenit.zrep;

import java.util.List;

import org.apache.log4j.Logger;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class Rep5 {
	// Logging
	private final static Logger logger = Logger.getLogger(repDB.class);
	
	public static String build(
			List<String> aBody1,		// Первая часть шаблона
			List<String> aBody2,		// Вторая часть шаблона
			String[] aIDs				// pers_request_id
			) throws Exception {
		
		String x, y;
		int count;
		Float f;
		try {
			// Открываем базу
		    MongoClient mongo = new MongoClient(Report.rep_bd_server , Report.rep_bd_port);
		    DB db = mongo.getDB(Report.rep_bd_name);
		    
		    for (int iID = 0; iID < aIDs.length; iID++) {
		    	String suffix = String.format("%02d", iID + 1);
		    	
			    //---------------- По коллекции  person_request -------------------
			    DBCollection dbCollection = db.getCollection("person_request");
			    BasicDBObject idQuery = new BasicDBObject(Report.parent_name, aIDs[iID]);
			    DBObject dbObject = dbCollection.findOne(idQuery);
			    
	            // (6) Цель кредита
			    //Report.zReplaceAll(aBody1, "ЦЕЛЬ" + suffix, Report.exam(dbObject, "data.goal"));
			    // (4+) Информация о проекте, отрасль проекта
			    String okved = Report.legal(dbObject, "data.OKVED_project");
			    
		    	//---------------- По коллекции enterprise ------------------------
		    	dbCollection = db.getCollection("enterprise");
		    	idQuery = new BasicDBObject(Report.parent_name, aIDs[iID]);	// Замена
		    	dbObject = dbCollection.findOne(idQuery);
		    
		    	// Значения на будущее
		    	String enterpriseName = Report.exam(dbObject, "data.enterprise_data.name");
		    	x = Report.legal(dbObject, "data.enterprise_data.inn");
		    	String enterpriseINN = x.trim().length() == 0? "0": x;
		    
		    	// (5) Контакты потенциального заемщика/инвестора проекта (фио, тел, эл. почта)
		    	DBObject data = dbObject == null? null: (DBObject)dbObject.get("data");
		    	BasicDBList managerList = data == null? null: (BasicDBList)data.get("enterprise_contacts");
		    	if (managerList == null) count = 0; else count = managerList.size();
		    	if (count == 0) {
		    		Report.zReplaceAll(aBody1, "ФИО" + suffix, "-");
		    	}
		    	else {	
				    for (int i = 0; i < count; i++) {
				    	Report.zReplaceAll(aBody1, "ФИО"+ suffix, 
					    	Report.legal(managerList, i, "fio") + "," +
					    	Report.legal(managerList, i, "email") + "," +
				    		Report.legal(managerList, i, "phone") + ";");
				    	break;
				    }
		    	}
		    
		    	// ----------------------- По коллекции project --------------------------------------
		    	dbCollection = db.getCollection("project");
		    	idQuery = new BasicDBObject(Report.parent_name, aIDs[iID]);		// Замена
		    	dbObject = dbCollection.findOne(idQuery);
		    
		    	// (0) Регион -субъект РФ
		    	Report.zReplaceAll(aBody1, "Республика" + suffix, Report.exam(dbObject, "data.credit.region_rf"));
		    	// (1-3) Источник заявки
		    	if (Report.legal(dbObject, "data.credit.who_init.direct").trim().length() > 0) { // ПРЯМОЕ ОБРАЩЕНИЕ ИНИЦИАТОРА
		    		Report.zReplaceAll(aBody1, "СУБЪЕКТ РФ" + suffix, "ПРЯМОЕ ОБРАЩЕНИЕ ИНИЦИАТОРА");
		    		Report.zReplaceAll(aBody1, "КОМПАНИЯ" + suffix, enterpriseName);
		    		Report.zReplaceAll(aBody2, "11111111" + suffix, enterpriseINN);
		    	}
		    	else if (Report.legal(dbObject, "data.credit.who_init.subjectrf").trim().length() > 0) {	// СУБЪЕКТ РФ
		    		Report.zReplaceAll(aBody1, "СУБЪЕКТ РФ" + suffix, "СУБЪЕКТ РФ");
		    		Report.zReplaceAll(aBody1, "КОМПАНИЯ" + suffix, Report.exam(dbObject, "data.credit.who_init.subjectrf.name"));
		    		Report.zReplaceAll(aBody2, "11111111" + suffix, Report.exam(dbObject, "data.credit.who_init.subjectrf.inn"));
		    	}
		    	else if (Report.legal(dbObject, "data.credit.who_init.unions").trim().length() > 0) {	// ОБЪЕДИНЕНИЯ
		    		Report.zReplaceAll(aBody1, "СУБЪЕКТ РФ" + suffix, "ОБЪЕДИНЕНИЯ");
		    		Report.zReplaceAll(aBody1, "КОМПАНИЯ" + suffix, Report.exam(dbObject, "data.credit.who_init.unions.name"));
		    		Report.zReplaceAll(aBody2, "11111111" + suffix, Report.exam(dbObject, "data.credit.who_init.unions.inn"));
		    	}
		    	else {
		    		Report.zReplaceAll(aBody1, "СУБЪЕКТ РФ" + suffix, Report.question_marks);
		    		Report.zReplaceAll(aBody1, "КОМПАНИЯ" + suffix, Report.question_marks);
		    		Report.zReplaceAll(aBody2, "11111111" + suffix, "0");
		    	}
		    	// (4)Информация о проекте, отрасль проекта
		    	Report.zReplaceAll(aBody1, "ПРОЕКТ" + suffix, Report.exam(dbObject, "data.info.project_goal") + "," + okved);
	            // (6) Цель кредита
			    Report.zReplaceAll(aBody1, "ЦЕЛЬ" + suffix, Report.exam(dbObject, "data.credit.credit_goal"));
		    	// (7) Планируемая сумма проекта млн. руб.
		    	x = Report.legal(dbObject, "data.sum.sum_total");
		    	String sum_total = x.trim().length() == 0? "0": Integer.toString((int)(Float.parseFloat(x) / 1000));
		    	Report.zReplaceAll(aBody2, "777" + suffix, sum_total);
		    	// (8) Планируемая сумма кредита, млн. руб.
		    	x = Report.legal(dbObject, "data.credit.sum");
		    	String credit_sum = x.trim().length() == 0? "0": Integer.toString((int)(Float.parseFloat(x) / 1000));
		    	Report.zReplaceAll(aBody2, "888" + suffix, credit_sum);
		    	// (9) Планируемая сумма гарантии  млн. руб.
		    	x = Report.legal(dbObject, "data.credit.garant_sum");
		    	String garant_sum = x.trim().length() == 0? "0": Integer.toString((int)(Float.parseFloat(x) / 1000));
		    	Report.zReplaceAll(aBody2, "999" + suffix, garant_sum);
		    	// (10) Текущий статус проработки проекта
		    	Report.zReplaceAll(aBody1, "СТАТУС" + suffix, Report.exam(dbObject, "data.info.project_status"));
		    	// (11) Наименование кредитующего Банка (в случае рассмотрения заявки в Банке)
		    	Report.zReplaceAll(aBody1, "БАНК" + suffix, Report.exam(dbObject, "data.credit.bank_name"));
		    	// (12) Контакты менеджера в кредитующем Банке (фио, тел, эл. почта)
		    	Report.zReplaceAll(aBody1, "КОНТАКТ" + suffix, Report.exam(dbObject, "data.credit.contact_bank"));
		    	// (13) Статус рассмотрения кредитной заявки в Банке (в случае рассмотрения заявки в Банке)
		    	Report.zReplaceAll(aBody1, "ЗАЯВКА" + suffix, Report.exam(dbObject, "data.credit.status_bank"));
		    	// (14) Требуемый вид поддержки от Корпорации (выбрать в ячейке)
		    	Report.zReplaceAll(aBody1, "Поддержка" + suffix, Report.exam(dbObject, "data.info.project_corp_support"));
		    }
		    for (int i = aIDs.length; i <= 50; i++) {
		    	String suffix = String.format("%02d", i);
		    	Report.zReplaceAll(aBody1, "Республика" + suffix, " ");	// 0
	    		Report.zReplaceAll(aBody1, "СУБЪЕКТ РФ" + suffix, " ");				// 1
	    		Report.zReplaceAll(aBody1, "КОМПАНИЯ" + suffix, " ");				// 2
	    		Report.zReplaceAll(aBody2, "11111111" + suffix, " ");				// 3
		    	Report.zReplaceAll(aBody1, "ПРОЕКТ" + suffix, " ");					// 4
	    		Report.zReplaceAll(aBody1, "ФИО" + suffix, " ");					// 5
		    	Report.zReplaceAll(aBody1, "ЦЕЛЬ" + suffix, " ");					// 6
		    	Report.zReplaceAll(aBody2, "777" + suffix, " ");					// 7
		    	Report.zReplaceAll(aBody2, "888" + suffix, " ");					// 8
		    	Report.zReplaceAll(aBody2, "999" + suffix, " ");					// 9
		    	Report.zReplaceAll(aBody1, "СТАТУС" + suffix, " ");					// 10
		    	Report.zReplaceAll(aBody1, "БАНК" + suffix, " ");					// 11
		    	Report.zReplaceAll(aBody1, "КОНТАКТ" + suffix, " ");				// 12
		    	Report.zReplaceAll(aBody1, "ЗАЯВКА" + suffix, " ");					// 13
		    	Report.zReplaceAll(aBody1, "Поддержка" + suffix, " ");	// 14
		    }
		} catch (Exception  ex) {
			// Ошибка
			return ex.toString();
		}
		return "";
	}
}
