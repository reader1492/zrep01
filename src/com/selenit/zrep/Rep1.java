// Valerii Zinovev, Perm, 11 aug 2017
// Первый отчет (Чек-лист)
package com.selenit.zrep;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class Rep1 {
	public static List<String> build(List<String> aBody, String aParam) {
		String x, y;
		int count = 0;
		try {
			// Открываем базу
		    MongoClient mongo = new MongoClient(Report.rep_bd_server , Report.rep_bd_port);
		    DB db = mongo.getDB(Report.rep_bd_name);
		    
		    // По коллекции enterprise
		    DBCollection dbCollection = db.getCollection("enterprise");
		    BasicDBObject idQuery = new BasicDBObject(Report.parent_name, aParam);
		    DBObject dbObject = dbCollection.findOne(idQuery);
		    
		    // (хИнициаторПроекта) Название компании
		    String mainCompName = Report.exam(dbObject, "data.enterprise_data.name");
		    Report.zReplaceAll(aBody, "хИнициаторПроекта", "\"" + mainCompName + "\"");
		    // (хИНН) ИНН компании
		    DBObject data = dbObject == null? null: (DBObject)dbObject.get("data");
		    BasicDBList companyList = data == null? null: (BasicDBList)data.get("enterprise_group_company");
		    if (companyList == null) count = 0; else count = companyList.size();
		    for (int i = 0; i < count; i++) {
		    	if (companyList.get(i) == null) continue;
		    	if (((BasicDBObject)companyList.get(i)).getString("compaign_name").equals(mainCompName)) {
		    		Report.zReplaceAll(aBody, "хИНН", 
		    				((BasicDBObject)companyList.get(i)).getString("compaign_inn"));
		    		break;
		    	}
		    }
		    Report.zReplaceAll(aBody, "хИНН", Report.question_marks); 
		    // (хСТКап) Соответствие требованиям по структуре уставного (складочного) капитала
		    Report.zReplaceAll(aBody, "хСТКап", 
		    		Report.exam3(dbObject, "data.enterprise_data.bool_demand"));
		    // (хВыруч) Выручка – не более 2 млрд. руб.
		    Calendar c = new GregorianCalendar();
		    String lastYear = String.valueOf(c.get(Calendar.YEAR)-1);
		    BasicDBList dohodList = data == null? null: (BasicDBList)data.get("enterprise_dohod");
		    if (dohodList == null) count = 0; else count = dohodList.size();
		    for (int i = 0; i < count; i++) {
		    	if (((BasicDBObject)dohodList.get(i)).getString("year").equals(lastYear)) {
		    		x = ((BasicDBObject)dohodList.get(i)).getString("proceeds_comp");
		    		Report.zReplaceAll(aBody, "хВыруч", 
		    				x == null?"??????" : Integer.valueOf(x) <= 2000000? "ДА": "НЕТ");
		    		break;
		    	}
		    }
		    Report.zReplaceAll(aBody, "хВыруч", Report.question_marks); // Если не нашли
		    // (хЧисл) Численность – не более 250 чел.
		    x = Report.exam(dbObject, "data.enterprise_data.cur_staff");
		    Report.zReplaceAll(aBody, "хЧисл", 
		    		x.equals("??????")?x : Integer.valueOf(x) <= 250? "ДА": "НЕТ");
		    // (хРусРег) Регистрация бизнеса на территории Российской Федерации
		    Report.zReplaceAll(aBody, "хРусРег", 
		    		Report.exam3(dbObject, "data.enterprise_data.rezident"));
		    // (хДолги) Отсутствие просроченной задолженности по налогам, сборам и т.п.
		    Report.zReplaceAll(aBody, "хДолги", 
		    		Report.exam3(dbObject, "data.enterprise_data.bool_tax_debt"));
		    // (хБанкро) Не применяются процедуры несостоятельности (банкротства) к инициатору проекта
		    Report.zReplaceAll(aBody, "хБанкро", 
		    		Report.exam3(dbObject, "data.enterprise_data.bool_bankrupt"));
		    // (хНетКр) Отсутствие отрицательной кредитной истории в кредитных организациях
		    Report.zReplaceAll(aBody, "хНетКр", 
		    		Report.exam3(dbObject, "data.enterprise_data.bool_cred"));
		    
		    // По коллекции project
		    dbCollection = db.getCollection("project");
		    idQuery = new BasicDBObject(Report.parent_name, aParam);
		    dbObject = dbCollection.findOne(idQuery);
		    
            // (хЦель) Цель кредита соответствуют трем критериям
		    Report.zReplaceAll(aBody, "хЦель", 
		    		Report.exam3(dbObject, "data.info.project_resume"));
		    // (хСумГар) Сумма гарантии – более 200 млн. руб.
		    x = Report.exam(dbObject, "data.credit.garant_sum");
		    Report.zReplaceAll(aBody, "хСумГар", 
		    		x.equals("??????")? x: Integer.valueOf(x) > 200000? "ДА": "НЕТ");
		    // (хСумКр) Сумма кредита ≥ сумма гарантии/0,7
		    x = Report.exam(dbObject, "data.credit.sum");
		    y = Report.exam(dbObject, "data.credit.garant_sum");
		    Report.zReplaceAll(aBody, "хСумКр", 
		    		x.equals("??????") || y.equals("??????")? "??????": 
		    			Float.parseFloat(x) >= 0.7 * Float.parseFloat(y)? "ДА": "НЕТ");
		    // (хСХ, хОбрПр, хЭГВ, хСтроит, хТрансп, хИЗамещ, хПрНапр) Приоритетные направления
		    x = Report.legal(dbObject, "data.info.project_vid");
		    Report.zReplaceAll(aBody, "хСХ", 
		    		x.equals("??????")? x:
		    		x.equals("Сельское хозяйство (в т.ч производство с/х продукции)")? "ДА": "НЕТ");
		    Report.zReplaceAll(aBody, "хОбрПр", 
		    		x.equals("??????")? x:
		    		x.equals("Обрабатывающее производство  (в т.ч. производство пищевых продуктов)")? "ДА": "НЕТ");
		    Report.zReplaceAll(aBody, "хЭГВ", 
		    		x.equals("??????")? x:
		    		x.equals("Производство и распределение электроэнергии, газа и воды")? "ДА": "НЕТ");
		    Report.zReplaceAll(aBody, "хСтроит", 
		    		x.equals("??????")? x:
		    		x.equals("Строительство")? "ДА": "НЕТ");
		    Report.zReplaceAll(aBody, "хТрансп", 
		    		x.equals("??????")? x:
		    		x.equals("Транспорт и связь")? "ДА": "НЕТ");
		    Report.zReplaceAll(aBody, "хИЗамещ", 
		    		x.equals("??????")? x:
		    		x.equals("Производство и реализация импортозамещающей продукции")? "ДА": "НЕТ");
		    Report.zReplaceAll(aBody, "хПрНапр", 
		    		x.equals("??????")? x:
		    		x.equals("Приоритетные направления развития науки, технологий и техники в Российской Федерации и направления развития критических технологий Российской Федерации в соответствии с Указом Президента Российской Федерации от 07.07.2011 № 899")?
		    				"ДА": "НЕТ");
		    // (хПоддП) Поддержка проекта со стороны региона, профильных федеральных органов исполнительной власти 
		    Report.zReplaceAll(aBody, "хПоддП", Report.exam3(dbObject, "data.info.support"));
		    // (хПрефП) Преференциальные/комфортные письма в рамках проекта
		    Report.zReplaceAll(aBody, "хПрефП", Report.exam(dbObject, "data.info.perfletters"));
		    // (хОтПрав) Оказание прочих видов поддержки проекта
		    Report.zReplaceAll(aBody, "хОтПрав", Report.exam(dbObject, "data.info.other_support"));
		    // (хКрОп) Кр-е описание проекта, тек. статус реализации проекта, инф. об инициаторах проекта
		    Report.zReplaceAll(aBody, "хКрОп", Report.exam3(dbObject, "data.info.project_resume"));
		    //  (хЭкОб) Экономическое обоснование проекта, описание финансовой модели и результата проекта
		    Report.zReplaceAll(aBody, "хЭкОб", Report.exam3(dbObject, "data.info.rationale"));
		    // (хАнСб) Анализ сбыта, данные о покупателях/маркетинговое исследование рынка
		    Report.zReplaceAll(aBody, "хАнСб", Report.exam3(dbObject, "data.info.sales_analysis"));
		    // (хПодряд) Данные о поставщиках/подрядчиках
		    Report.zReplaceAll(aBody, "хПодряд", Report.exam3(dbObject, "data.info.suppliers"));
		    // (хЭтапы) Этапы реализации
		    x = Report.exam(dbObject, "data.info.stages");
		    Report.zReplaceAll(aBody, "хЭтапы", 
		    		x.equals("??????") || x.trim().length() == 0? "НЕТ": "ДА");
		    // (хСмета) Смета проекта
		    Report.zReplaceAll(aBody, "хСмета", Report.exam3(dbObject, "data.info.estimate"));
		    // (хИсточ) Источники и структура финансирования каждого этапа
		    Report.zReplaceAll(aBody, "хИсточ", Report.exam3(dbObject, "data.info.sources_and_structure"));
		    // (хПОпыт) Данные о наличии профессионального опыта менеджеров/бен-ров в сфере реализуемого проекта
		    Report.zReplaceAll(aBody, "хПОпыт", Report.exam3(dbObject, "data.info.professional_experience"));
		    // (хСВОТ) Конкурентный/SWOT –анализ
		    Report.zReplaceAll(aBody, "хСВОТ", Report.exam3(dbObject, "data.info.SWOT"));
		    // (хДоля) Доля собственного участия в проекте не менее 20%
		    x = Report.exam(dbObject, "data.info.percentage_of_participation");
		    Report.zReplaceAll(aBody, "хДоля", 
		    		x.equals("??????")? x: Integer.valueOf(x) >= 20? "ДА": "НЕТ");
		    // (хРДок) Наличие и/р документации по строительству сооружений в рамках реализуемого проекта
		    Report.zReplaceAll(aBody, "хРДок", Report.exam3(dbObject, "data.info.source_doc"));
		    // (хТЭксп) Наличие технологической и технической экспертизы проекта
		    Report.zReplaceAll(aBody, "хТЭксп", Report.exam3(dbObject, "data.info.tech_expertise"));
		    // (хТДеят) Наличие информации о текущей деятельности инициатора проекта
		    Report.zReplaceAll(aBody, "хТДеят", Report.exam3(dbObject, "data.info.information"));
		    
		} catch (Exception  ex) {
			// Ошибка
			return new ArrayList<String>(){{
				add("error");
				add(ex.toString());
			}};
		}
		return aBody;
	}

}
