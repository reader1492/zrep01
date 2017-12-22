// Valerii Zinovev, Perm, 11 aug 2017
// Второй отчет (Заявка на получение независимой гарантии)
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

public class Rep2 {
	// Константы
	private static final String CR = "</w:t></w:r><w:proofErr w:type=\"spellEnd\" /></w:p>" +
		    "<w:p w14:paraId=\"13CA829D\" w14:textId=\"7807F61F\" w:rsidR=\"00EB7BCB\" w:rsidRPr=\"00676B6B\" " +
		    "w:rsidRDefault=\"00BD1753\" w:rsidP=\"00580745\"><w:pPr><w:rPr><w:color w:val=\"auto\" /></w:rPr></w:pPr>" +
		    "<w:proofErr w:type=\"spellStart\" /><w:r><w:rPr><w:color w:val=\"auto\" /></w:rPr><w:t>";
	private static final String startBNF = "СВЕДЕНИЯ О БЕНЕФИЦИАРНОМ ВЛАДЕЛЬЦЕ";
	private static final String finishBNF = "владельца)";
	private static final int c66 = 66;					// Количество параметров для бенифициаров
	
	public static List<String> build(List<String> aBody, String aParam) {
		String x, y;
		int count = 0;
		try {
			// Открываем базу
		    MongoClient mongo = new MongoClient(Report.rep_bd_server , Report.rep_bd_port);
		    DB db = mongo.getDB(Report.rep_bd_name);
		    
		    // ----------------------- По коллекции project --------------------------------------
		    DBCollection dbCollection = db.getCollection("project");
		    BasicDBObject idQuery = new BasicDBObject(Report.parent_name, aParam);
		    DBObject dbObject = dbCollection.findOne(idQuery);
		    
		    // (хВидГарантии) Вид гарантии
		    Report.zReplaceAll(aBody, "хВидГарантии", Report.exam(dbObject, "data.credit.garant_vid"));
		    // (хСуммаГарантии) Сумма независимой гарантии
		    Report.zReplaceAll(aBody, "хСуммаГарантии", Report.exam(dbObject, "data.credit.garant_sum"));
		    // (хСрокКредита) Срок независимой гарантии
		    Report.zReplaceAll(aBody, "хСрокКредита", 
		    		Report.exam(dbObject, "doc.data.credit.garant_term") + " месяцев");
		    // (хПереодичностьУплаты) Периодичность уплаты
		    Report.zReplaceAll(aBody, "хПереодичностьУплаты", Report.exam(dbObject, "data.credit.pay_period"));
		    // (хСуммаКредита) Сумма кредита
		    Report.zReplaceAll(aBody, "хСуммаКредита", Report.exam(dbObject, "data.credit.sum"));
		    // (хСрокКредита) Срок кредита
		    Report.zReplaceAll(aBody, "хСрокКредита", 
		    		Report.exam(dbObject, "data.credit.term") + " месяцев");
		    // (хПланируемаяДатаЗаключения) Пред.дата заключения
		    Report.zReplaceAll(aBody, "хПланируемаяДатаЗаключения", Report.exam(dbObject, "data.credit.garant_date"));
		    // (хСтруктураОбеспеченияРГО) Структура предоставляемого обеспечения
		    Report.zReplaceAll(aBody, "хСтруктураОбеспеченияРГО", Report.exam(dbObject, "data.credit.provision_struct"));
		    // (хСтруктураОбеспеченияКорп) Структура предоставляемого обеспечения регрессных требований
		    Report.zReplaceAll(aBody, "хСтруктураОбеспеченияКорп", Report.exam(dbObject, "data.credit.regres_struct"));
		    // (хБанкПартерНазвание) Банк-партнер, предоставляющий  кредит
		    Report.zReplaceAll(aBody, "хБанкПартерНазвание", Report.exam(dbObject, "data.credit.bank_name"));
		    // (хБанкПартерКонтакт) Контактное лицо в Банке-партнере
		    Report.zReplaceAll(aBody, "хБанкПартерКонтакт", Report.exam(dbObject, "data.credit.contact_bank"));
		    // (хРегГарантияНазвание) Региональная гарантийная организация, участвующая в проекте
		    Report.zReplaceAll(aBody, "хРегГарантияНазвание", Report.exam(dbObject, "data.credit.reg_garant"));
		    // (хРегГарантияКонтакт) Контактное лицо в Региональной гарантийной организации
		    x = Report.legal(dbObject, "data.credit.contact_garant.position");
		    y = Report.legal(dbObject, "data.credit.contact_garant.telephone");
		    Report.zReplaceAll(aBody, "хРегГарантияКонтакт", 
		    		x + (y.trim().length() == 0? "": ", " + y));
		    // (хЦельПроекта) Цель проекта
		    Report.zReplaceAll(aBody, "хЦельПроекта", Report.exam(dbObject, "data.info.project_goal"));
		    // (хЭтапыРеализации) Этапы проекта
		    Report.zReplaceAll(aBody, "хЭтапыРеализации", Report.exam(dbObject, "data.info.stages"));
		    // (хСрокиРеализации) Сроки реализации
		    Report.zReplaceAll(aBody, "хСрокиРеализации", Report.exam(dbObject, "data.info.project_plan"));
		    // (хОписаниеПродукции) Описание продукции проекта
		    Report.zReplaceAll(aBody, "хОписаниеПродукции", Report.exam(dbObject, "data.product.product_name"));
		    // (хМинСт-хМаксСт) Общая стоимость проекта
		    x = Report.legal(dbObject, "data.sum.sum_min");
		    y = Report.legal(dbObject, "data.sum.sum_max");
		    Report.zReplaceAll(aBody, "хМинСт", 
		    		x.trim().length() == 0? "??????": String.valueOf(Integer.valueOf(x)/1000));
		    Report.zReplaceAll(aBody, "хМаксСт", 
		    		y.trim().length() == 0? "??????": String.valueOf(Integer.valueOf(y)/1000));
		    //  (хРабМеста) Рабочие места
		    Report.zReplaceAll(aBody, "хРабМеста", 
		    		Report.exam(dbObject, "data.other.other_staff_count") + CR +
		    		Report.exam(dbObject, "data.other.other_staff_text"));
		    // Тип заемщика (cred_init)
		    boolean cred_init = Report.legal(dbObject, "data.credit.cred_ent") != "Проектная организация";
		    
		    //---------------------- По коллекции enterprise ------------------------------------
		    dbCollection = db.getCollection("enterprise");
		    idQuery = new BasicDBObject(Report.parent_name, aParam);
		    dbObject = dbCollection.findOne(idQuery);
		    
		    // (хНазваниеОрганизации) Название компании
		    Report.zReplaceAll(aBody, "хНазваниеОрганизации", Report.exam(dbObject, "data.enterprise_data.name"));
		    // (хРуководительОрганизации) В лице
		    DBObject data = dbObject == null? null: (DBObject)dbObject.get("data");
		    BasicDBList managerList = data == null? null: (BasicDBList)data.get("enterprise_manager");
		    if (managerList == null) count = 0; else count = managerList.size();
		    for (int i = 0; i < count; i++) {
		    	try {
		    		//PetrovichDeclinationMaker maker = PetrovichDeclinationMaker.getInstance();
		    		//y = ((BasicDBObject)managerList.get(i)).getString("position");
		    		y = Report.legal(managerList, i, "position");
		    		if (y == null) continue;
		    		if (y.toUpperCase().contains("ДИРЕКТОР")) {
		    			/*String[] array = ((BasicDBObject)managerList.get(i)).getString("high_manager").split("\\s+");
		    			x = ((BasicDBObject)managerList.get(i)).getString("position") + " " +
		    					array[0] + " " + array[1] + " " + array[2];*/
		    			x = Report.legal(managerList, i, "position") + " " +
		    					Report.legal(managerList, i, "high_manager");
		    			break;
		    		}
		    	} catch (Exception  ex) {
		    		x = Report.legal(dbObject, "");
		    		break;
		    	}
		    }
		    Report.zReplaceAll(aBody, "хРуководительОрганизации", x);
		    // (хУчредительныйДокумент) Действующего на основании
		    Report.zReplaceAll(aBody, "хУчредительныйДокумент", Report.exam(dbObject, "data.enterprise_data.doc"));
		    // (хГруппаКомпанийНазвание) Принадлежность к группе компаний
		    if (cred_init) {
		    	String groupComp = "";
		    	BasicDBList companyList = data == null? null: (BasicDBList)data.get("enterprise_group_company");
			    if (companyList == null) count = 0; else count = companyList.size();
		    	for (int i = 0; i < count; i++) {
		    		groupComp += Report.exam(companyList, i, "compaign_name") + CR +
		    				Report.exam(companyList, i, "compaign_inn") + CR +
		    				Report.exam(companyList, i, "compaign_ogrn");
		    	}
		    	Report.zReplaceAll(aBody, "хГруппаКомпанийНазвание", groupComp);
		    }
		    // (хГруппаКомпанийБенифициар) Сведения о бенефициарном(ых) владельце(ах)
	    	int benifCount = 0;
		    if (cred_init) {
		    	String benif = "";
		    	int und;
		    	BasicDBList ownList = data == null? null: (BasicDBList)data.get("enterprise_owners");
			    if (ownList == null) count = 0; else count = ownList.size();
		    	for (int i = 0; i < count; i++) {
		    		try {
		    			x = Report.legal(ownList, i, "own_percent");
		    			und = x.trim().length() == 0? 0: Integer.parseInt(x);
		    		} catch (Exception  ex) {
		    			und = 0;
		    		}
		    		if (und < Report.benif_percent) continue;
		    		benifCount++;
		    		BasicDBObject benificiar = 
		    				(BasicDBObject)((BasicDBObject)((BasicDBObject)ownList.get(i)).get("owner")).get("beneficiar");
		    		if (benificiar != null) {
		    			x = benificiar.getString("fio");
		    			if (x == null || x.trim().length() == 0) continue;
		    			benif += x + " - " + String.valueOf(und) + "%" + CR;
		    			continue;
		    		}
		    	}
		    	Report.zReplaceAll(aBody, "хГруппаКомпанийБенифициар", benif);
		    }
		    // (хПрГод,хЭтГод,хВПрГод,хВЭтГод,хЧПрГод,хЧЭтГод) Выручка и персонал за минувшие два года
		    if (cred_init) {
		    	Calendar c = new GregorianCalendar();
		    	String m2Year = String.valueOf(c.get(Calendar.YEAR)-2);
		    	String m1Year = String.valueOf(c.get(Calendar.YEAR)-1);
		    	Report.zReplaceAll(aBody, "хПрГод", m2Year);
		    	Report.zReplaceAll(aBody, "хЭтГод", m1Year);
		    	Report.zReplaceAll(aBody, "хВПрГод", "0");
		    	Report.zReplaceAll(aBody, "хВЭтГод", "0");
		    	BasicDBList dohodList = data == null? null: (BasicDBList)data.get("enterprise_dohod");
			    if (dohodList == null) count = 0; else count = dohodList.size();
		    	for (int i = 0; i < count; i++) {
		    		if (((BasicDBObject)dohodList.get(i)).getString("year").equals(m2Year)) {
		    			x = ((BasicDBObject)dohodList.get(i)).getString("proceeds_comp");
		    			Report.zReplaceAll(aBody, "хВПрГод", x == null?"0": x);
		    			int j = ((BasicDBObject)dohodList.get(i)).getInt("proceeds_empscount");
		    			Report.zReplaceAll(aBody, "хЧПрГод", String.valueOf(j));
		    		}
		    		if (((BasicDBObject)dohodList.get(i)).getString("year").equals(m1Year)) {
		    			x = ((BasicDBObject)dohodList.get(i)).getString("proceeds_comp");
		    			Report.zReplaceAll(aBody, "хВЭтГод", x == null?"0": x);
		    			int j = ((BasicDBObject)dohodList.get(i)).getInt("proceeds_empscount");
		    			Report.zReplaceAll(aBody, "хЧЭтГод", String.valueOf(j));
		    		}
		    	}
		    }
		    // (хОснВидДЗаемщика) Основной вид деятельности Заемщика
		    if (cred_init) {
		    	Report.zReplaceAll(aBody, "хОснВидДЗаемщика", Report.exam(dbObject, "data.enterprise_data.activity"));
		    }
		    // (хМестоРегЗаемщика) Место регистрации заемщика
		    if (cred_init) {
		    	Report.zReplaceAll(aBody, "хМестоРегЗаемщика", Report.exam(dbObject, "data.enterprise_data.address_reg"));
		    }
		    // (хАдресОграновУправления) Адрес местонахождения постоянно действующих органов управления
		    if (cred_init) {
		    	Report.zReplaceAll(aBody, "хАдресОграновУправления", Report.exam(dbObject, "data.enterprise_data.address_org"));
		    }
		    // (хОКАТОЗаемщика) ОКАТО Заемщика
		    if (cred_init) {
		    	Report.zReplaceAll(aBody, "хОКАТОЗаемщика", Report.exam(dbObject, "data.enterprise_data.okato"));
		    }
		    // (хОКПОЗаемщика) ОКПО заемщика
		    Report.zReplaceAll(aBody, "хОКПОЗаемщика", Report.exam(dbObject, "data.enterprise_data.okpo"));
		    // (хКонтактЗаемщика) Контактное лицо
		    if (cred_init) {
		    	BasicDBList contactList = data == null? null: (BasicDBList)data.get("enterprise_manager");
			    if (contactList == null) count = 0; else count = contactList.size();
		    	for (int i = 0; i < count; i++) {
		    		x = Report.legal(contactList, i, "contact");
		    		if (x.equals("true")) {
		    			x = Report.legal(contactList, i, "high_manager");
		    			y = Report.legal(contactList, i, "phone");
		    			Report.zReplaceAll(aBody, "хКонтактЗаемщика",
		    					x + (y.trim().length() == 0? "": ", " + y));
		    			break;
		    		}
		    	}
		    }
		    // Сформируем массив данных по бенифициарам
		    String[][] arr = new String[benifCount][c66];
		    int bc = 0, und = 0;
		    BasicDBList ownList = data == null? null: (BasicDBList)data.get("enterprise_owners");
		    if (ownList == null) count = 0; else count = ownList.size();
		    for (int i = 0; i < count; i++) {
		    	try {
		    		und = ((BasicDBObject)ownList.get(i)).getInt("own_percent");
		    	} catch (Exception  ex) {
		    		und = 0;
		    	}
		    	if (und < Report.benif_percent) continue;
		    	BasicDBObject benificiar = 
		    			(BasicDBObject)((BasicDBObject)((BasicDBObject)ownList.get(i)).get("owner")).get("beneficiar");
		    	if (benificiar == null) continue;
		    	for (int j = 0; j < c66; j++) arr[bc][j] = "";
		    	try {
		    		// Общие данные
		    		x = benificiar.getString("fio");
		    		String[] fio = x.split("\\s+");
		    		int cnt = fio.length;
		    		arr[bc][0] = cnt < 1? "": fio[0];
		    		arr[bc][1] = cnt < 2? "": fio[1];
		    		arr[bc][2] = cnt < 3? "": fio[2];
		    		arr[bc][3] = Report.legal(ownList, i, "beneficiar.inn");
		    		arr[bc][4] = Report.legal(ownList, i, "beneficiar.birthday");
		    		arr[bc][5] = Report.legal(ownList, i, "beneficiar.birthloc");
		    		// Резидентство
		    		if (Report.legal(ownList, i, "beneficiar.rezident.rezident").trim().length() != 0) {	// Резидент
		    			arr[bc][6] = "V";
		    			arr[bc][7] = " ";
		    			arr[bc][8] = " ";
		    			// Российский паспорт
		    			arr[bc][9] = Report.legal(ownList, i, "beneficiar.rezident.rezident.passport.doc_type");
		    			arr[bc][10] = Report.legal(ownList, i, "beneficiar.rezident.rezident.passport.seria");
		    			arr[bc][11] = Report.legal(ownList, i, "beneficiar.rezident.rezident.passport.num_doc");
		    			arr[bc][12] = Report.legal(ownList, i, "beneficiar.rezident.rezident.passport.begin_date");
		    			arr[bc][13] = Report.legal(ownList, i, "beneficiar.rezident.rezident.passport.dep_name");
		    			y = Report.legal(ownList, i, "beneficiar.rezident.rezident.passport.dep_code");
		    			arr[bc][14] = y.substring(0, 1);
		    			arr[bc][15] = y.substring(1, 2);
		    			arr[bc][16] = y.substring(2, 3);
		    			arr[bc][17] = y.substring(3, 4);
		    			arr[bc][18] = y.substring(4, 5);
		    			arr[bc][19] = y.substring(5, 6);
		    			arr[bc][20] = y.substring(6, 7);
		    		}
		    		else if (Report.legal(ownList, i, "beneficiar.rezident.notrezident").trim().length() != 0) { // Нерезидент
		    			arr[bc][6] = " ";
		    			arr[bc][7] = "V";
		    			arr[bc][8] = "V";
		    			// Иностранный паспорт
		    			arr[bc][21] = Report.legal(ownList, i, "beneficiar.rezident.notrezident.passport.doc_type");
		    			arr[bc][22] = Report.legal(ownList, i, "beneficiar.rezident.notrezident.passport.num_doc");
		    			arr[bc][23] = Report.legal(ownList, i, "beneficiar.rezident.notrezident.passport.begin_date");
		    			arr[bc][24] = Report.legal(ownList, i, "beneficiar.rezident.notrezident.passport.dep_name");
		    			arr[bc][25] = Report.legal(ownList, i, "beneficiar.rezident.notrezident.passport.end_date");
		    			// Миграционная карта
		    			arr[bc][26] = Report.legal(ownList, i, "beneficiar.rezident.notrezident.migrant.card_num");
		    			arr[bc][27] = Report.legal(ownList, i, "beneficiar.rezident.notrezident.migrant.begin_date");
		    			arr[bc][28] = Report.legal(ownList, i, "beneficiar.rezident.notrezident.migrant.end_date");
		    			// Виза
		    			arr[bc][29] = Report.legal(ownList, i, "beneficiar.rezident.notrezident.visa.doc_type");
		    			arr[bc][30] = Report.legal(ownList, i, "beneficiar.rezident.notrezident.visa.seria");
		    			arr[bc][31] = Report.legal(ownList, i, "beneficiar.rezident.notrezident.visa.doc_num");
		    			arr[bc][32] = Report.legal(ownList, i, "beneficiar.rezident.notrezident.visa.begin_date");
		    			arr[bc][33] = Report.legal(ownList, i, "beneficiar.rezident.notrezident.visa.end_date");
		    			// Адрес проживания
		    			arr[bc][34] = Report.legal(ownList, i, "beneficiar.rezident.notrezident.address.country");
		    			arr[bc][35] = Report.legal(ownList, i, "beneficiar.rezident.notrezident.address.town_loc");
		    			arr[bc][36] = Report.legal(ownList, i, "beneficiar.rezident.notrezident.address.street_loc");
		    			arr[bc][37] = Report.legal(ownList, i, "beneficiar.rezident.notrezident.address.home_loc");
		    			arr[bc][38] = Report.legal(ownList, i, "beneficiar.rezident.notrezident.address.building_loc");
		    			arr[bc][39] = Report.legal(ownList, i, "beneficiar.rezident.notrezident.address.apartment_loc");
		    		}
		    		if (Report.legal(ownList, i, "beneficiar.rezident.rezident.address").trim().length() != 0) {
		    			// Адрес есть
		    			Object rez = Report.getObject(benificiar, "rezident.rezident");
		    			BasicDBList addrList = (BasicDBList)((DBObject)rez).get("address");
					    if (addrList == null) count = 0; else count = addrList.size();
		    			for (int j = 0; j < count; j++) {
		    				y = Report.legal(addrList, j, "addr_type_loc");
		    				if (y.equals("Регистрация")) {
		    					arr[bc][40] = Report.legal(addrList, j, "region_loc");	
		    					arr[bc][41] = Report.legal(addrList, j, "district_loc");	
		    					arr[bc][42] = Report.legal(addrList, j, "town_loc");	
		    					arr[bc][43] = Report.legal(addrList, j, "street_loc");	
		    					arr[bc][44] = Report.legal(addrList, j, "home_loc");	
		    					arr[bc][45] = Report.legal(addrList, j, "building_loc");	
		    					arr[bc][46] = Report.legal(addrList, j, "apartment_loc");	
		    				} else if (y.equals("Фактическое")) {
		    					arr[bc][47] = Report.legal(addrList, j, "region_loc");	
		    					arr[bc][48] = Report.legal(addrList, j, "district_loc");	
		    					arr[bc][49] = Report.legal(addrList, j, "town_loc");	
		    					arr[bc][50] = Report.legal(addrList, j, "street_loc");	
		    					arr[bc][51] = Report.legal(addrList, j, "home_loc");	
		    					arr[bc][52] = Report.legal(addrList, j, "building_loc");	
		    					arr[bc][53] = Report.legal(addrList, j, "apartment_loc");	
		    				}
		    			}
		    		}
		    		// Должностное лицо
		    		if (Report.legal(ownList, i, "beneficiar.official.nochin").trim().length() != 0) {
    					arr[bc][62] = "__";	
    					arr[bc][63] = "V";	
    					arr[bc][64] = "";	
		    		} else if (Report.legal(ownList, i, "beneficiar.official.chin").trim().length() != 0) {
    					arr[bc][62] = "V";	
    					arr[bc][63] = "__";	
    					arr[bc][64] = "";	
		    		} else {
		    			arr[bc][62] = arr[bc][63] = arr[bc][64] = "";
		    		}
		    	} catch (Exception ex) {
		    		
		    	}
		    	bc++;
		    }
		    aBody = f09replace(aBody, arr, benifCount);

		    //---------------------- По коллекции project_ent ------------------------------------
		    dbCollection = db.getCollection("project_ent");
		    idQuery = new BasicDBObject(Report.parent_name, aParam);
		    dbObject = dbCollection.findOne(idQuery);
		    
		    data = dbObject == null? null: (DBObject)dbObject.get("data");
		    
		    // (хГруппаКомпанийНазвание) Принадлежность к группе компаний
		    if (!cred_init) {
		    	String groupComp = "";
		    	BasicDBList companyList = data == null? null: (BasicDBList)data.get("enterprise_group_company");
			    if (companyList == null) count = 0; else count = companyList.size();
		    	for (int i = 0; i < count; i++) {
		    		groupComp += ((BasicDBObject)companyList.get(i)).getString("compaign_name") + CR +
		    				((BasicDBObject)companyList.get(i)).getString("compaign_inn") + CR +
		    				((BasicDBObject)companyList.get(i)).getString("compaign_ogrn");
		    	}
		    	Report.zReplaceAll(aBody, "хГруппаКомпанийНазвание", groupComp);
		    }
		    // (хГруппаКомпанийБенифициар) Сведения о бенефициарном(ых) владельце(ах)
	    	benifCount = 0;
		    if (!cred_init) {
		    	String benif = "";
		    	ownList = data == null? null: (BasicDBList)data.get("enterprise_owners");
			    if (ownList == null) count = 0; else count = ownList.size();
		    	for (int i = 0; i < count; i++) {
		    		try {
		    			und = ((BasicDBObject)ownList.get(i)).getInt("own_percent");
		    		} catch (Exception  ex) {
		    			und = 0;
		    		}
		    		if (und < Report.benif_percent) continue;
		    		benifCount++;
		    		BasicDBObject benificiar = 
		    				(BasicDBObject)((BasicDBObject)((BasicDBObject)ownList.get(i)).get("owner")).get("beneficiar");
		    		if (benificiar != null) {
		    			x = benificiar.getString("fio");
		    			if (x == null || x.trim().length() == 0) continue;
		    			benif += x + " - " + String.valueOf(und) + "%" + CR;
		    			continue;
		    		}
		    	}
		    	Report.zReplaceAll(aBody, "хГруппаКомпанийБенифициар", benif);
		    }
		    // (хПрГод,хЭтГод,хВПрГод,хВЭтГод,хЧПрГод,хЧЭтГод) Выручка и персонал за минувшие два года
		    if (!cred_init) {
		    	Calendar c = new GregorianCalendar();
		    	String m2Year = String.valueOf(c.get(Calendar.YEAR)-2);
		    	String m1Year = String.valueOf(c.get(Calendar.YEAR)-1);
		    	Report.zReplaceAll(aBody, "хПрГод", m2Year);
		    	Report.zReplaceAll(aBody, "хЭтГод", m1Year);
		    	Report.zReplaceAll(aBody, "хВПрГод", "0");
		    	Report.zReplaceAll(aBody, "хВЭтГод", "0");
		    	BasicDBList dohodList = data == null? null: (BasicDBList)data.get("enterprise_dohod");
			    if (dohodList == null) count = 0; else count = dohodList.size();
		    	for (int i = 0; i < count; i++) {
		    		if (((BasicDBObject)dohodList.get(i)).getString("year").equals(m2Year)) {
		    			x = ((BasicDBObject)dohodList.get(i)).getString("proceeds_comp");
		    			Report.zReplaceAll(aBody, "хВПрГод", x == null?"0": x);
		    			int j = ((BasicDBObject)dohodList.get(i)).getInt("proceeds_empscount");
		    			Report.zReplaceAll(aBody, "хЧПрГод", String.valueOf(j));
		    		}
		    		if (((BasicDBObject)dohodList.get(i)).getString("year").equals(m1Year)) {
		    			x = ((BasicDBObject)dohodList.get(i)).getString("proceeds_comp");
		    			Report.zReplaceAll(aBody, "хВЭтГод", x == null?"0": x);
		    			int j = ((BasicDBObject)dohodList.get(i)).getInt("proceeds_empscount");
		    			Report.zReplaceAll(aBody, "хЧЭтГод", String.valueOf(j));
		    		}
		    	}
		    }
		    // (хОснВидДЗаемщика) Основной вид деятельности Заемщика
		    if (!cred_init) {
		    	Report.zReplaceAll(aBody, "хОснВидДЗаемщика", Report.exam(dbObject, "data.enterprise_data.activity"));
		    }
		    // (хМестоРегЗаемщика) Место регистрации заемщика
		    if (!cred_init) {
		    	Report.zReplaceAll(aBody, "хМестоРегЗаемщика", Report.exam(dbObject, "data.enterprise_data.address_reg"));
		    }
		    // (хАдресОграновУправления) Адрес местонахождения постоянно действующих органов управления
		    if (!cred_init) {
		    	Report.zReplaceAll(aBody, "хАдресОграновУправления", Report.exam(dbObject, "data.enterprise_data.address_org"));
		    }
		    // (хОКАТОЗаемщика) ОКАТО Заемщика
		    if (!cred_init) {
		    	Report.zReplaceAll(aBody, "хОКАТОЗаемщика", Report.exam(dbObject, "data.enterprise_data.okato"));
		    }
		    // (хОКПОЗаемщика) ОКПО заемщика
		    if (!cred_init) {
		    	Report.zReplaceAll(aBody, "хОКПОЗаемщика", Report.exam(dbObject, "data.enterprise_data.okpo"));
		    }
		    // (хКонтактЗаемщика) Контактное лицо
		    if (!cred_init) {
		    	BasicDBList contactList = data == null? null: (BasicDBList)data.get("enterprise_manager");
			    if (contactList == null) count = 0; else count = contactList.size();
		    	for (int i = 0; i < count; i++) {
		    		if (((BasicDBObject)contactList.get(i)).getBoolean("contact", false)) {
		    			x = ((BasicDBObject)contactList.get(i)).getString("high_manager");
		    			y = ((BasicDBObject)contactList.get(i)).getString("phone");
		    			Report.zReplaceAll(aBody, "хКонтактЗаемщика",
		    					x + (y.trim().length() == 0? "": ", " + y));
		    			break;
		    		}
		    	}
		    }
		} catch (Exception  ex) {
			// Ошибка
			return new ArrayList<String>(){{
				add("error");
				add(ex.toString());
			}};
		}
		return aBody;
	}
	// Заполнение таблиц бенифициаров
	private static List<String> f09replace (List<String> input, String[][] matrixArray, int count) {
		if (count == 0) {
			count = 1;
			matrixArray = new String[1][c66];
			for (int i = 0; i < c66; i++) matrixArray[0][i] = "";
		}
		String inputString = ListToString(input);
	    String result = "";            // Нарастающий итог
	    String buffer = inputString;   // Убывающий остаток
	    String benifTemplate = "";     // Рабочее тело (серединка)
	    int startPos, finishPos, conts;
	    String bt = "";
	    // Получить шаблон для заполнения данными по бенифициарам
	    for (int i = 0; i < 10; i++) {
	        startPos = buffer.indexOf(startBNF);
	        startPos = buffer.lastIndexOf("<w:p ", startPos);
	        startPos = buffer.lastIndexOf("<w:p ", startPos-1);
	        startPos = buffer.lastIndexOf("<w:p ", startPos-1);
	        //s2 = s2 - s1 + 17; s3 = s3 - s1 + 17; s1 = 17;
	        finishPos = buffer.indexOf(finishBNF, startPos);
	        finishPos = buffer.indexOf("<w:p ", finishPos);
	        if (i < count) {               // Если эта таблица актуальна
	        	// Нарастим результат
	        	result = result + buffer.substring(0, startPos);
	        	// Получим рабочее тело
	        	benifTemplate = buffer.substring(startPos, finishPos);
	        	// Проставим в теле параметры
	        	for (int j = 0; j < c66; j++) if (matrixArray [i][j] == null) matrixArray [i][j] = "";
	        	//bt = benifTemplate;
	        	bt = benifTemplate
	                    .replace("хФамилия", matrixArray[i][0])
	                    .replace("хИмя", matrixArray[i][1])
	                    .replace("хОтчество", matrixArray[i][2])
	                    .replace("хИНН", matrixArray[i][3])
	                    .replace("хДатаРождения", matrixArray[i][4])
	                    .replace("хМестоРождения", matrixArray[i][5])
	                    .replace("Х1", matrixArray[i][6])
	                    .replace("Х2", matrixArray[i][7])
	                    .replace("Х3", matrixArray[i][8])
	                    .replace("хВидДокументаРФ", matrixArray[i][9])
	                    .replace("хСерияДокРФ", matrixArray[i][10])
	                    .replace("хНомерДокРФ", matrixArray[i][11])
	                    .replace("хДатаВыдДокРФ", matrixArray[i][12])
	                    .replace("хНаименованиеОрганаРФ", matrixArray[i][13])
	                    .replace("Х4", matrixArray[i][14])
	                    .replace("Х5", matrixArray[i][15])
	                    .replace("Х6", matrixArray[i][16])
	                    .replace("Х7", matrixArray[i][17])
	                    .replace("Х8", matrixArray[i][18])
	                    .replace("Х9", matrixArray[i][19])
	                    .replace("Х0", matrixArray[i][20])
	                    .replace("хВидДокументаБГ", matrixArray[i][21])
	                    .replace("хНомерДокБГ", matrixArray[i][22])
	                    .replace("хДатаВыдДокБГ", matrixArray[i][23])
	                    .replace("хНаименованиеОрганаБГ", matrixArray[i][24])
	                    .replace("хСрокДействияБГ", matrixArray[i][25])
	                    .replace("хНомерМК", matrixArray[i][26])
	                    .replace("хНачалоСрокаМК", matrixArray[i][27])
	                    .replace("хКонецСрокаМК", matrixArray[i][28])
	                    .replace("хВидДокументаИГ", matrixArray[i][29])
	                    .replace("хСерияДокИГ", matrixArray[i][30])
	                    .replace("хНомерДокИГ", matrixArray[i][31])
	                    .replace("хНачалоСрокаИГ", matrixArray[i][32])
	                    .replace("хКонецСрокаИГ", matrixArray[i][33])
	                    .replace("хСтранаИГ", matrixArray[i][34])
	                    .replace("хГородИГ", matrixArray[i][35])
	                    .replace("хУлицаИГ", matrixArray[i][36])
	                    .replace("хДомИГ", matrixArray[i][37])
	                    .replace("хКорпИГ", matrixArray[i][38])
	                    .replace("хКвИГ", matrixArray[i][39])
	                    .replace("хОбластьМЖ", matrixArray[i][40]) // 10
	                    .replace("хРайонМЖ", matrixArray[i][41])
	                    .replace("хГородМЖ", matrixArray[i][42])
	                    .replace("хУлицаМЖ", matrixArray[i][43])
	                    .replace("хДомМЖ", matrixArray[i][44])
	                    .replace("хКорпМЖ", matrixArray[i][45])
	                    .replace("хКвМЖ", matrixArray[i][46])
	                    .replace("хОбластьМП", matrixArray[i][47])
	                    .replace("хРайонМП", matrixArray[i][48])
	                    .replace("хГородМП", matrixArray[i][49])
	                    .replace("хУлицаМП", matrixArray[i][50])
	                    .replace("хДомМП", matrixArray[i][51])
	                    .replace("хКорпМП", matrixArray[i][52])
	                    .replace("хКвМП", matrixArray[i][53])

	                    .replace("хИндексПЧ", matrixArray[i][54])
	                    .replace("хОбластьПЧ", matrixArray[i][55])
	                    .replace("хРайонПЧ", matrixArray[i][56])
	                    .replace("хГородПЧ", matrixArray[i][57])
	                    .replace("хУлицаПЧ", matrixArray[i][58])
	                    .replace("хДомПЧ", matrixArray[i][59])
	                    .replace("хКорпПЧ", matrixArray[i][60])
	                    .replace("хКвПЧ", matrixArray[i][61])

	                    .replace("хДоЛ", matrixArray[i][62])
	                    .replace("хНеДол", matrixArray[i][63])
	                    .replace("хДолИнфо", matrixArray[i][64])
	                    .replace("хКонтакт", matrixArray[i][65])
	        			;
	        	// Добавим тело к результату
	        	result = result + bt;   //benifTemplate;
	        }
	        // Отрежем хвост
	        buffer = buffer.substring(finishPos);
	    }
	    // Присобачим хвост к результату
	    return StringToList(result + buffer);
	}
	// List<String> to String
	private static String ListToString(List<String> aList) {
		String res = "";
		for (int i = 0; i < aList.size(); i++) {
			res += aList.get(i) + "ů";
		}
		return res.substring(0, res.length()-1);
	}
	// String to List<String>
	private static List<String> StringToList(String aStr) {
		String[] res = aStr.split("ů");
		List<String> abc = new ArrayList<String>(res.length);
		for (int i = 0; i < res.length; i++) {
			abc.add(res[i]);
		}
		return abc;
	}
	
}
