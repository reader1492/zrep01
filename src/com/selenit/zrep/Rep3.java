// Valerii Zinovev, Perm, 13 aug 2017
// “ретий отчет (–езюме проекта)
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

public class Rep3 {
	//  онстанты
	private static final String CR = "</w:t></w:r><w:proofErr w:type=\"spellEnd\" /></w:p>" +
		    "<w:p w14:paraId=\"13CA829D\" w14:textId=\"7807F61F\" w:rsidR=\"00EB7BCB\" w:rsidRPr=\"00676B6B\" " +
		    "w:rsidRDefault=\"00BD1753\" w:rsidP=\"00580745\"><w:pPr><w:rPr><w:color w:val=\"auto\" /></w:rPr></w:pPr>" +
		    "<w:proofErr w:type=\"spellStart\" /><w:r><w:rPr><w:color w:val=\"auto\" /></w:rPr><w:t>";
	
	public static List<String> build(List<String> aBody, String aParam) {
		String x, y;
		Float f;
		int count;
		try {
			// ќткрываем базу
		    MongoClient mongo = new MongoClient(Report.rep_bd_server , Report.rep_bd_port);
		    DB db = mongo.getDB(Report.rep_bd_name);
		    
		    // ----------------------- ѕо коллекции project --------------------------------------
		    DBCollection dbCollection = db.getCollection("project");
		    BasicDBObject idQuery = new BasicDBObject(Report.parent_name, aParam);
		    DBObject dbObject = dbCollection.findOne(idQuery);
		    
		    // “ип заемщика (cred_init)
		    boolean cred_init = Report.legal(dbObject, "data.credit.cred_ent") != "ѕроектна€ организаци€";
            // »нициатор проекта (proj_init)
		    String proj_init = Report.legal(dbObject, "data.credit.who_init");
		    boolean proj_init_direct = Report.legal(dbObject, "data.credit.who_init.direct").trim().length() > 0;
		    boolean proj_init_subjectrf = Report.legal(dbObject, "data.credit.who_init.subjectrf").trim().length() > 0;
		    boolean proj_init_unions = Report.legal(dbObject, "data.credit.who_init.unions").trim().length() > 0;
		    String proj_init_subjectrf_name = Report.exam(dbObject, "data.credit.who_init.subjectrf.name");
		    String proj_init_unions_name = Report.legal(dbObject, "data.credit.who_init.unions.name");
		    
            // (хѕроект) »м€ проекта
		    Report.zReplaceAll(aBody, "хѕроект", Report.exam(dbObject, "data.info.project_name"));
            // (хќбща€—уммаѕроекта) ќбща€ сумма проекта
		    Report.zReplaceAll(aBody, "хќбща€—уммаѕроекта", Report.exam(dbObject, "data.sum.sum_total"));
            // (хѕ»–) ѕроектна€ и разрешительна€ документаци€
		    Report.zReplaceAll(aBody, "хѕ»–", Report.exam(dbObject, "data.sum.sum_doc"));
            // (хќборудование) ќборудование
		    Report.zReplaceAll(aBody, "хќборудование", Report.exam(dbObject, "data.sum.sum_equipment"));
            // (х—ћ–) —ћ–
		    Report.zReplaceAll(aBody, "х—ћ–", Report.exam(dbObject, "data.sum.sum_CMP"));
            // (хќборотные—редства) ќборотные средства
		    Report.zReplaceAll(aBody, "хќборотные—редства", Report.exam(dbObject, "data.sum.sum_working_capital"));
            // (хЌƒ—) Ќƒ—
		    Report.zReplaceAll(aBody, "хЌƒ—", Report.exam(dbObject, "data.sum.sum_nds"));
		    // (х—об—р–уб, х—об—рѕроц, хƒолг‘–уб, хƒолг‘ѕроц) —труктура финансировани€ проекта (8-11)
		    float invest = Report.zfloat(Report.legal(dbObject, "data.invest_struct.invest_sum"));
		    x = Report.legal(dbObject, "data.invest_struct.invest_own_percent");
		    f = invest * Report.zfloat(Report.legal(dbObject, "data.invest_struct.invest_own_percent")) / 100000;
		    Report.zReplaceAll(aBody, "х—об—р–уб", Float.toString(f));
		    f = Report.zfloat(Report.legal(dbObject, "data.invest_struct.invest_own_percent"));
		    Report.zReplaceAll(aBody, "х—об—рѕроц", Float.toString(f));
		    f = invest * Report.zfloat(Report.legal(dbObject, "data.invest_struct.invest_debt_percent")) / 100000;
		    Report.zReplaceAll(aBody, "хƒолг‘–уб", Float.toString(f));
		    f = Report.zfloat(Report.legal(dbObject, "data.invest_struct.invest_debt_percent"));
		    Report.zReplaceAll(aBody, "хƒолг‘ѕроц", Float.toString(f));
            // (х—умма)  редит - сумма (12)
		    Report.zReplaceAll(aBody, "х—умма", Report.exam(dbObject, "data.credit.sum"));
            // (х—рок)  редит - срок (13)
		    Report.zReplaceAll(aBody, "х—рок", Report.exam(dbObject, "data.credit.term"));
            // (хѕроц—тав)  редит - процентна€ ставка (14)
		    Report.zReplaceAll(aBody, "хѕроц—тав", Report.exam(dbObject, "data.credit.percent"));
            // (хЅанк)  редит - Ѕанк (15)
		    Report.zReplaceAll(aBody, "хЅанк", Report.legal(dbObject, "data.credit.bank_name"));
            // (х—татус–ассмотрени€)  редит - —татус рассмотрени€ (16)
		    Report.zReplaceAll(aBody, "х—татус–ассмотрени€", Report.legal(dbObject, "data.credit.status_bank"));
            // (х¬ид) √аранти€ ћ—ѕ - ¬ид (17)
		    Report.zReplaceAll(aBody, "х¬ид", Report.exam(dbObject, "data.credit.garant_vid"));
            // (х—умма) √аранти€ ћ—ѕ - —умма (18)
		    Report.zReplaceAll(aBody, "х—умма", Report.exam(dbObject, "data.credit.garant_sum"));
            // (хѕрочее) √аранти€ ћ—ѕ - ѕрочее (19)
		    Report.zReplaceAll(aBody, "хѕрочее", Report.exam(dbObject, "data.credit.garant_other"));
            // (хѕредпологаемоеќбеспечение, х—труктураќбеспечени€) ѕредлагаемое обеспечение (20-21)
		    Report.zReplaceAll(aBody, "хѕредпологаемоеќбеспечение", Report.exam(dbObject, "data.credit.provision"));
		    Report.zReplaceAll(aBody, "х—труктураќбеспечени€", Report.exam(dbObject, "data.credit.provision_struct"));
            // (х¬идѕроекта) ¬ид ѕроекта (56)
		    Report.zReplaceAll(aBody, "х¬идѕроекта", Report.exam(dbObject, "data.info.project_type"));
            // (х÷ельѕроекта) ÷ель проекта (57)
		    Report.zReplaceAll(aBody, "х÷ельѕроекта", Report.exam(dbObject, "data.info.project_goal"));
            // (х—рокиѕроекта) —роки проекта / график реализации (58)
		    Report.zReplaceAll(aBody, "х—рокиѕроекта", Report.exam(dbObject, "data.info.project_plan"));
            // (х лючевыеЁкономическиеѕоказателиѕроекта)  лючевые экономические показатели проекта (59)
		    Report.zReplaceAll(aBody, "х лючевыеЁкономическиеѕоказателиѕроекта", Report.exam(dbObject, "data.info.project_keys"));
            // (хЌаименоаниеѕродукта) ѕродукт - Ќаименование / технические и экономические характеристики (60)
		    Report.zReplaceAll(aBody, "хЌаименоаниеѕродукта", Report.exam(dbObject, "data.product.product_name"));
            // (х”никальныйѕродукт) ”никальный / типовой продукт (61)
		    Report.zReplaceAll(aBody, "х”никальныйѕродукт", Report.exam(dbObject, "data.product.product_unique"));
            // (хјналогиЌа÷елевом–ынке) ѕеречень аналогов на целевом рынке / конкурентные преимущества / основные конкуренты (62)
		    Report.zReplaceAll(aBody, "хјналогиЌа÷елевом–ынке", Report.exam(dbObject, "data.product.product_analog"));
            // (х—труктура—ебистоимости) —труктура себестоимости / ключевые факторы, вли€ющие на себестоимость (63)
		    Report.zReplaceAll(aBody, "х—труктура—ебистоимости", Report.exam(dbObject, "data.product.product_struct"));
            // (хƒол€»мпорта¬—ебистоимости) ƒол€ импорта в себестоимости (64)
		    Report.zReplaceAll(aBody, "хƒол€»мпорта¬—ебистоимости", Report.exam(dbObject, "data.product.product_import"));
            // (х—тепеньЌовизны“ехнологии) —тепень новизны и сложности используемой технологии (65)
		    Report.zReplaceAll(aBody, "х—тепеньЌовизны“ехнологии", Report.exam(dbObject, "data.tech.tech_new"));
            // (хЌаличие“ехнологическойЁкспертизы) Ќаличие технологической экспертизы. (66)
		    Report.zReplaceAll(aBody, "хЌаличие“ехнологическойЁкспертизы", Report.exam(dbObject, "data.tech.tech_exp"));
            // (хћестонахождение”частка) ”часток - ћестонахождение (адрес) (67)
		    Report.zReplaceAll(aBody, "хћестонахождение”частка", Report.exam(dbObject, "data.tech.tech_loc_address"));
            // (хёридический—татус”частка) ”часток - ћестонахождение (адрес) (68)
		    Report.zReplaceAll(aBody, "хёридический—татус”частка", Report.exam(dbObject, "data.tech.tech_loc_status"));
            // (х»меюща€с€»нфраструктура) »меюща€с€ инфраструктура (69)
		    Report.zReplaceAll(aBody, "х»меюща€с€»нфраструктура", Report.exam(dbObject, "data.tech.inf_current"));
            // (хЌеобходима€»нфраструктура) Ќеобходима€ инфраструктура (70)
		    Report.zReplaceAll(aBody, "хЌеобходима€»нфраструктура", Report.exam(dbObject, "data.tech.inf_future"));
            // (х»сточник»нформацииќ–ынке) »сточник информации о рынке (71)
		    Report.zReplaceAll(aBody, "х»сточник»нформацииќ–ынке", Report.exam(dbObject, "data.market.market_info"));
            // (х÷елевой–ынок, х≈мкость–ынка, х лючевые»гроки) ÷елевой рынок: локальный / экспорт (регионы / емкость / ключевые игроки / прочее) (72-74)
		    Report.zReplaceAll(aBody, "х÷елевой–ынок", Report.exam(dbObject, "data.market.market_goal"));
		    Report.zReplaceAll(aBody, "х≈мкость–ынка", Report.exam(dbObject, "data.market.market_emkost"));
		    Report.zReplaceAll(aBody, "х лючевые»гроки", Report.exam(dbObject, "data.market.market_key"));
            // (хƒол€ѕроекта¬÷елевом–ыночном—егменте) ƒол€ проекта в целевом рыночном сегменте, % (75)
		    Report.zReplaceAll(aBody, "хƒол€ѕроекта¬÷елевом–ыночном—егменте", Report.exam(dbObject, "data.market.market_percent"));
            // (хћаркетинговое»сследование) Ќаличие маркетингового исследовани€ (76)
		    Report.zReplaceAll(aBody, "хћаркетинговое»сследование", Report.exam(dbObject, "data.market.market_project"));
            // (х—писокѕокупателей) —писок/ предварительные договора / комфортные письма / коммерческие предложени€ (77)
		    Report.zReplaceAll(aBody, "х—писокѕокупателей", Report.exam(dbObject, "data.market.market_list"));
            // (хѕланируемый√рафик–еализации) ѕланируемый график реализации (78)
		    Report.zReplaceAll(aBody, "хѕланируемый√рафик–еализации", Report.exam(dbObject, "data.market.market_plan"));
            // (хѕоставщик—ырь€) (ѕоставщик сырь€)—писок / предварительные договора / комфортные письма (79)
		    Report.zReplaceAll(aBody, "хѕоставщик—ырь€", Report.exam(dbObject, "data.other.other_raw"));
            // (х√енподр€дчик) (ѕоставщик/и оборудовани€)—писок / предварительные договора / комфортные письма (80)
		    Report.zReplaceAll(aBody, "хѕоставщикќборудовани€", Report.exam(dbObject, "data.other.other_equipment"));
            // (х√енподр€дчик) (—ћ– / генподр€дчик)Ќаименование / опыт реализации подобных проектов (81)
		    Report.zReplaceAll(aBody, "х√енподр€дчик", Report.exam(dbObject, "data.other.other_CMP"));
            // (х—татусƒокументации»Ёкспертизы) (ѕроектно-разрешительна€ документаци€ и экспертиза) (82)
		    Report.zReplaceAll(aBody, "х—татусƒокументации»Ёкспертизы", Report.exam(dbObject, "data.other.other_doc"));
            // (хƒокументации»Ёкспертизы) (ѕроектно-разрешительна€ документаци€ и экспертиза)јвтор (83)
		    Report.zReplaceAll(aBody, "хƒокументации»Ёкспертизы", Report.exam(dbObject, "data.other.other_autor"));
            // (хƒатаѕолучени€–азрешени€Ќа—троительство) (–азрешение на строительство)ƒата получени€ (84)
		    Report.zReplaceAll(aBody, "хƒатаѕолучени€–азрешени€Ќа—троительство", Report.exam(dbObject, "data.other.other_building"));
            // (х¬ыручка) ¬ыручка (85)
		    Report.zReplaceAll(aBody, "х¬ыручка", Report.exam(dbObject, "data.indicators.indic_receipt"));
            // (xEBITDA) EBITDA (86)
		    Report.zReplaceAll(aBody, "xEBITDA", Report.exam(dbObject, "data.indicators.indic_receipt"));
            // (х„иста€ѕрибыль) „иста€ прибыль (87)
		    Report.zReplaceAll(aBody, "х„иста€ѕрибыль", Report.exam(dbObject, "data.indicators.indic_receipt"));
            // (88 - 141)“аблица прогнозных показателей
		    DBObject data = dbObject == null? null: (DBObject)dbObject.get("data");
		    BasicDBList prognosisList = data == null? null: (BasicDBList)data.get("prognosis");

		    if (prognosisList == null) count = 0; else count = prognosisList.size();
		    for (int i = 0; i < count; i++) {
		    	if (Report.legal(prognosisList, i, "year").equals("2016")) {
				    Report.zReplaceAll(aBody, "х¬ыр16", Report.exam(prognosisList, i, "prognosis_receipts"));
				    Report.zReplaceAll(aBody, "х≈¬16", Report.exam(prognosisList, i, "prognosis_EBITDA"));
				    Report.zReplaceAll(aBody, "х–≈¬16", Report.exam(prognosisList, i, "prognosis_rent_EBITDA"));
				    Report.zReplaceAll(aBody, "х„ѕ16", Report.exam(prognosisList, i, "prognosis_clear"));
				    Report.zReplaceAll(aBody, "хƒ—16", Report.exam(prognosisList, i, "prognosis_money"));
				    Report.zReplaceAll(aBody, "хќƒѕ16", Report.exam(prognosisList, i, "prognosis_op_money"));
				    Report.zReplaceAll(aBody, "х»нв16", Report.exam(prognosisList, i, "prognosis_invest"));
				    Report.zReplaceAll(aBody, "хƒѕ»16", Report.exam(prognosisList, i, "prognosis_post_invest"));
				    Report.zReplaceAll(aBody, "хƒѕ‘16", Report.exam(prognosisList, i, "prognosis_post_finans"));
		    	}
		    	if (Report.legal(prognosisList, i, "year").equals("2017")) {
				    Report.zReplaceAll(aBody, "х¬ыр17", Report.exam(prognosisList, i, "prognosis_receipts"));
				    Report.zReplaceAll(aBody, "х≈¬17", Report.exam(prognosisList, i, "prognosis_EBITDA"));
				    Report.zReplaceAll(aBody, "х–≈¬17", Report.exam(prognosisList, i, "prognosis_rent_EBITDA"));
				    Report.zReplaceAll(aBody, "х„ѕ17", Report.exam(prognosisList, i, "prognosis_clear"));
				    Report.zReplaceAll(aBody, "хƒ—17", Report.exam(prognosisList, i, "prognosis_money"));
				    Report.zReplaceAll(aBody, "хќƒѕ17", Report.exam(prognosisList, i, "prognosis_op_money"));
				    Report.zReplaceAll(aBody, "х»нв17", Report.exam(prognosisList, i, "prognosis_invest"));
				    Report.zReplaceAll(aBody, "хƒѕ»17", Report.exam(prognosisList, i, "prognosis_post_invest"));
				    Report.zReplaceAll(aBody, "хƒѕ‘17", Report.exam(prognosisList, i, "prognosis_post_finans"));
		    	}
		    	if (Report.legal(prognosisList, i, "year").equals("2018")) {
				    Report.zReplaceAll(aBody, "х¬ыр18", Report.exam(prognosisList, i, "prognosis_receipts"));
				    Report.zReplaceAll(aBody, "х≈¬18", Report.exam(prognosisList, i, "prognosis_EBITDA"));
				    Report.zReplaceAll(aBody, "х–≈¬18", Report.exam(prognosisList, i, "prognosis_rent_EBITDA"));
				    Report.zReplaceAll(aBody, "х„ѕ18", Report.exam(prognosisList, i, "prognosis_clear"));
				    Report.zReplaceAll(aBody, "хƒ—18", Report.exam(prognosisList, i, "prognosis_money"));
				    Report.zReplaceAll(aBody, "хќƒѕ18", Report.exam(prognosisList, i, "prognosis_op_money"));
				    Report.zReplaceAll(aBody, "х»нв18", Report.exam(prognosisList, i, "prognosis_invest"));
				    Report.zReplaceAll(aBody, "хƒѕ»18", Report.exam(prognosisList, i, "prognosis_post_invest"));
				    Report.zReplaceAll(aBody, "хƒѕ‘18", Report.exam(prognosisList, i, "prognosis_post_finans"));
		    	}
		    	if (Report.legal(prognosisList, i, "year").equals("2019")) {
				    Report.zReplaceAll(aBody, "х¬ыр19", Report.exam(prognosisList, i, "prognosis_receipts"));
				    Report.zReplaceAll(aBody, "х≈¬19", Report.exam(prognosisList, i, "prognosis_EBITDA"));
				    Report.zReplaceAll(aBody, "х–≈¬19", Report.exam(prognosisList, i, "prognosis_rent_EBITDA"));
				    Report.zReplaceAll(aBody, "х„ѕ19", Report.exam(prognosisList, i, "prognosis_clear"));
				    Report.zReplaceAll(aBody, "хƒ—19", Report.exam(prognosisList, i, "prognosis_money"));
				    Report.zReplaceAll(aBody, "хќƒѕ19", Report.exam(prognosisList, i, "prognosis_op_money"));
				    Report.zReplaceAll(aBody, "х»нв19", Report.exam(prognosisList, i, "prognosis_invest"));
				    Report.zReplaceAll(aBody, "хƒѕ»19", Report.exam(prognosisList, i, "prognosis_post_invest"));
				    Report.zReplaceAll(aBody, "хƒѕ‘19", Report.exam(prognosisList, i, "prognosis_post_finans"));
		    	}
		    	if (Report.legal(prognosisList, i, "year").equals("2020")) {
				    Report.zReplaceAll(aBody, "х¬ыр20", Report.exam(prognosisList, i, "prognosis_receipts"));
				    Report.zReplaceAll(aBody, "х≈¬20", Report.exam(prognosisList, i, "prognosis_EBITDA"));
				    Report.zReplaceAll(aBody, "х–≈¬20", Report.exam(prognosisList, i, "prognosis_rent_EBITDA"));
				    Report.zReplaceAll(aBody, "х„ѕ20", Report.exam(prognosisList, i, "prognosis_clear"));
				    Report.zReplaceAll(aBody, "хƒ—20", Report.exam(prognosisList, i, "prognosis_money"));
				    Report.zReplaceAll(aBody, "хќƒѕ20", Report.exam(prognosisList, i, "prognosis_op_money"));
				    Report.zReplaceAll(aBody, "х»нв20", Report.exam(prognosisList, i, "prognosis_invest"));
				    Report.zReplaceAll(aBody, "хƒѕ»20", Report.exam(prognosisList, i, "prognosis_post_invest"));
				    Report.zReplaceAll(aBody, "хƒѕ‘20", Report.exam(prognosisList, i, "prognosis_post_finans"));
		    	}
		    	if (Report.legal(prognosisList, i, "year").equals("2021")) {
				    Report.zReplaceAll(aBody, "х¬ыр21", Report.exam(prognosisList, i, "prognosis_receipts"));
				    Report.zReplaceAll(aBody, "х≈¬21", Report.exam(prognosisList, i, "prognosis_EBITDA"));
				    Report.zReplaceAll(aBody, "х–≈¬21", Report.exam(prognosisList, i, "prognosis_rent_EBITDA"));
				    Report.zReplaceAll(aBody, "х„ѕ21", Report.exam(prognosisList, i, "prognosis_clear"));
				    Report.zReplaceAll(aBody, "хƒ—21", Report.exam(prognosisList, i, "prognosis_money"));
				    Report.zReplaceAll(aBody, "хќƒѕ21", Report.exam(prognosisList, i, "prognosis_op_money"));
				    Report.zReplaceAll(aBody, "х»нв21", Report.exam(prognosisList, i, "prognosis_invest"));
				    Report.zReplaceAll(aBody, "хƒѕ»21", Report.exam(prognosisList, i, "prognosis_post_invest"));
				    Report.zReplaceAll(aBody, "хƒѕ‘21", Report.exam(prognosisList, i, "prognosis_post_finans"));
		    	}
		    }
		    // «аполнить таблицу прогнозов
		    Report.zReplaceAll(aBody, "х¬ыр16", Report.question_marks);
		    Report.zReplaceAll(aBody, "х≈¬16", Report.question_marks);
		    Report.zReplaceAll(aBody, "х–≈¬16", Report.question_marks);
		    Report.zReplaceAll(aBody, "х„ѕ16", Report.question_marks);
		    Report.zReplaceAll(aBody, "хƒ—16", Report.question_marks);
		    Report.zReplaceAll(aBody, "хќƒѕ16", Report.question_marks);
		    Report.zReplaceAll(aBody, "х»нв16", Report.question_marks);
		    Report.zReplaceAll(aBody, "хƒѕ»16", Report.question_marks);
		    Report.zReplaceAll(aBody, "хƒѕ‘16", Report.question_marks);
		    
		    Report.zReplaceAll(aBody, "х¬ыр17", Report.question_marks);
		    Report.zReplaceAll(aBody, "х≈¬17", Report.question_marks);
		    Report.zReplaceAll(aBody, "х–≈¬17", Report.question_marks);
		    Report.zReplaceAll(aBody, "х„ѕ17", Report.question_marks);
		    Report.zReplaceAll(aBody, "хƒ—17", Report.question_marks);
		    Report.zReplaceAll(aBody, "хќƒѕ17", Report.question_marks);
		    Report.zReplaceAll(aBody, "х»нв17", Report.question_marks);
		    Report.zReplaceAll(aBody, "хƒѕ»17", Report.question_marks);
		    Report.zReplaceAll(aBody, "хƒѕ‘17", Report.question_marks);
		    
		    Report.zReplaceAll(aBody, "х¬ыр18", Report.question_marks);
		    Report.zReplaceAll(aBody, "х≈¬18", Report.question_marks);
		    Report.zReplaceAll(aBody, "х–≈¬18", Report.question_marks);
		    Report.zReplaceAll(aBody, "х„ѕ18", Report.question_marks);
		    Report.zReplaceAll(aBody, "хƒ—18", Report.question_marks);
		    Report.zReplaceAll(aBody, "хќƒѕ18", Report.question_marks);
		    Report.zReplaceAll(aBody, "х»нв18", Report.question_marks);
		    Report.zReplaceAll(aBody, "хƒѕ»18", Report.question_marks);
		    Report.zReplaceAll(aBody, "хƒѕ‘18", Report.question_marks);

		    Report.zReplaceAll(aBody, "х¬ыр19", Report.question_marks);
		    Report.zReplaceAll(aBody, "х≈¬19", Report.question_marks);
		    Report.zReplaceAll(aBody, "х–≈¬19", Report.question_marks);
		    Report.zReplaceAll(aBody, "х„ѕ19", Report.question_marks);
		    Report.zReplaceAll(aBody, "хƒ—19", Report.question_marks);
		    Report.zReplaceAll(aBody, "хќƒѕ19", Report.question_marks);
		    Report.zReplaceAll(aBody, "х»нв19", Report.question_marks);
		    Report.zReplaceAll(aBody, "хƒѕ»19", Report.question_marks);
		    Report.zReplaceAll(aBody, "хƒѕ‘19", Report.question_marks);
		    
		    Report.zReplaceAll(aBody, "х¬ыр20", Report.question_marks);
		    Report.zReplaceAll(aBody, "х≈¬20", Report.question_marks);
		    Report.zReplaceAll(aBody, "х–≈¬20", Report.question_marks);
		    Report.zReplaceAll(aBody, "х„ѕ20", Report.question_marks);
		    Report.zReplaceAll(aBody, "хƒ—20", Report.question_marks);
		    Report.zReplaceAll(aBody, "хќƒѕ20", Report.question_marks);
		    Report.zReplaceAll(aBody, "х»нв20", Report.question_marks);
		    Report.zReplaceAll(aBody, "хƒѕ»20", Report.question_marks);
		    Report.zReplaceAll(aBody, "хƒѕ‘20", Report.question_marks);
		    
		    Report.zReplaceAll(aBody, "х¬ыр21", Report.question_marks);
		    Report.zReplaceAll(aBody, "х≈¬21", Report.question_marks);
		    Report.zReplaceAll(aBody, "х–≈¬21", Report.question_marks);
		    Report.zReplaceAll(aBody, "х„ѕ21", Report.question_marks);
		    Report.zReplaceAll(aBody, "хƒ—21", Report.question_marks);
		    Report.zReplaceAll(aBody, "хќƒѕ21", Report.question_marks);
		    Report.zReplaceAll(aBody, "х»нв21", Report.question_marks);
		    Report.zReplaceAll(aBody, "хƒѕ»21", Report.question_marks);
		    Report.zReplaceAll(aBody, "хƒѕ‘21", Report.question_marks);
		    
            // (хЌѕ¬) NRV (142)
		    Report.zReplaceAll(aBody, "хЌѕ¬", Report.exam(dbObject, "data.indicators.indic_nrv"));
            // (х»––) IRR (143)
		    Report.zReplaceAll(aBody, "х»––", Report.exam(dbObject, "data.indicators.indic_irr"));
            // (хѕрочие»ндикаторы) ѕрочие (144)
		    Report.zReplaceAll(aBody, "хѕрочие»ндикаторы", Report.exam(dbObject, "data.indicators.indic_other"));
            // (хЁкологическиејспекты) Ёкологические аспекты / риски (145)
		    Report.zReplaceAll(aBody, "хЁкологическиејспекты", Report.exam(dbObject, "data.other.other_ec_risk"));
            // (хЌеобходимостьЌаличиеЁкологическойЁкспертизы) Ќеобходимость / наличие экологической экспертизы (146)
		    Report.zReplaceAll(aBody, "хЌеобходимостьЌаличиеЁкологическойЁкспертизы", 
		    		Report.exam(dbObject, "data.other.other_ec_need"));
            // App1 (148-236) ќбоснование бюджета проекта.
		    int c1 = 0, c2 = 0, c3 = 0, c4 = 0, c5 = 0;
            // ѕ–ќ≈ “Ќќ-–ј«–≈Ў»“≈Ћ№Ќјя ƒќ ”ћ≈Ќ“ј÷»я
		    // (148-154) -------------------------------------------------------
		    Report.zReplaceAll(aBody, "х—–«пдƒог", 
		    		Report.legal(dbObject, "data.app1.design_permitting_doc.project_doc.terms"));
		    Report.zReplaceAll(aBody, "х«ƒ«пдƒог", 
		    		Report.legal(dbObject, "data.app1.design_permitting_doc.project_doc.contracts"));
		    
		    x = Report.legal(dbObject, "data.app1.design_permitting_doc.project_doc.sum_total");
		    c1 += Report.zint(x);
		    Report.zReplaceAll(aBody, "х—¬«пдƒог", x);
		    
		    x = Report.legal(dbObject, "data.app1.design_permitting_doc.project_doc.sum_own");
		    c2 += Report.zint(x);
		    Report.zReplaceAll(aBody, "х——«пдƒог", x);
		    
		    x = Report.legal(dbObject, "data.app1.design_permitting_doc.project_doc.sum_creditor");
		    c3 += Report.zint(x);
		    Report.zReplaceAll(aBody, "х— «пдƒог", x);
		    
		    x = Report.legal(dbObject, "data.app1.design_permitting_doc.project_doc.sum_rest_own");
		    c4 += Report.zint(x);
		    Report.zReplaceAll(aBody, "хќ—«пдƒог", x);
		    
		    x = Report.legal(dbObject, "data.app1.design_permitting_doc.project_doc.sum_rest_creditor");
		    c5 += Report.zint(x);
		    Report.zReplaceAll(aBody, "хќ «пдƒог", x);
		    // (155-161) -------------------------------------------------------
		    Report.zReplaceAll(aBody, "х—–Ёксп", 
		    		Report.legal(dbObject, "data.app1.design_permitting_doc.project_exp.terms"));
		    Report.zReplaceAll(aBody, "х«ƒЁксп", 
		    		Report.legal(dbObject, "data.app1.design_permitting_doc.project_exp.contracts"));
		    
		    x = Report.legal(dbObject, "data.app1.design_permitting_doc.project_exp.sum_total");
		    c1 += Report.zint(x);
		    Report.zReplaceAll(aBody, "х—¬Ёксп", x);
		    
		    x = Report.legal(dbObject, "data.app1.design_permitting_doc.project_exp.sum_own");
		    c2 += Report.zint(x);
		    Report.zReplaceAll(aBody, "х——Ёксп", x);
		    
		    x = Report.legal(dbObject, "data.app1.design_permitting_doc.project_exp.sum_creditor");
		    c3 += Report.zint(x);
		    Report.zReplaceAll(aBody, "х— Ёксп", x);
		    
		    x = Report.legal(dbObject, "data.app1.design_permitting_doc.project_exp.sum_rest_own");
		    c4 += Report.zint(x);
		    Report.zReplaceAll(aBody, "хќ—Ёксп", x);
		    
		    x = Report.legal(dbObject, "data.app1.design_permitting_doc.project_exp.sum_rest_creditor");
		    c5 += Report.zint(x);
		    Report.zReplaceAll(aBody, "хќ Ёксп", x);
		    // (162-168) -------------------------------------------------------
		    Report.zReplaceAll(aBody, "х—––аз—тр", 
		    		Report.legal(dbObject, "data.app1.design_permitting_doc.project_permitt.terms"));
		    Report.zReplaceAll(aBody, "х«ƒ–аз—тр", 
		    		Report.legal(dbObject, "data.app1.design_permitting_doc.project_permitt.contracts"));
		    
		    x = Report.legal(dbObject, "data.app1.design_permitting_doc.project_permitt.sum_total");
		    c1 += Report.zint(x);
		    Report.zReplaceAll(aBody, "х—¬–аз—тр", x);
		    
		    x = Report.legal(dbObject, "data.app1.design_permitting_doc.project_permitt.sum_own");
		    c2 += Report.zint(x);
		    Report.zReplaceAll(aBody, "х——–аз—тр", x);
		    
		    x = Report.legal(dbObject, "data.app1.design_permitting_doc.project_permitt.sum_creditor");
		    c3 += Report.zint(x);
		    Report.zReplaceAll(aBody, "х— –аз—тр", x);
		    
		    x = Report.legal(dbObject, "data.app1.design_permitting_doc.project_permitt.sum_rest_own");
		    c4 += Report.zint(x);
		    Report.zReplaceAll(aBody, "хќ—–аз—тр", x);
		    
		    x = Report.legal(dbObject, "data.app1.design_permitting_doc.project_permitt.sum_rest_creditor");
		    c5 += Report.zint(x);
		    Report.zReplaceAll(aBody, "хќ –аз—тр", x);
		    
            // »Ќ‘–ј—“–” “”–ј
		    // (169-175) -------------------------------------------------------
		    Report.zReplaceAll(aBody, "х—–«акƒог", 
		    		Report.legal(dbObject, "data.app1.infrastructure.сonclusion_dog.terms"));
		    Report.zReplaceAll(aBody, "х«ƒ«акƒог", 
		    		Report.legal(dbObject, "data.app1.infrastructure.сonclusion_dog.contracts"));
		    
		    x = Report.legal(dbObject, "data.app1.infrastructure.сonclusion_dog.sum_total");
		    c1 += Report.zint(x);
		    Report.zReplaceAll(aBody, "х—¬«акƒог", x);
		    
		    x = Report.legal(dbObject, "data.app1.infrastructure.сonclusion_dog.sum_own");
		    c2 += Report.zint(x);
		    Report.zReplaceAll(aBody, "х——«акƒог", x);
		    
		    x = Report.legal(dbObject, "data.app1.infrastructure.сonclusion_dog.sum_creditor");
		    c3 += Report.zint(x);
		    Report.zReplaceAll(aBody, "х— «акƒог", x);
		    
		    x = Report.legal(dbObject, "data.app1.infrastructure.сonclusion_dog.sum_rest_own");
		    c4 += Report.zint(x);
		    Report.zReplaceAll(aBody, "хќ—«акƒог", x);
		    
		    x = Report.legal(dbObject, "data.app1.infrastructure.сonclusion_dog.sum_rest_creditor");
		    c5 += Report.zint(x);
		    Report.zReplaceAll(aBody, "хќ «акƒог", x);
		    // (176-182) -------------------------------------------------------
		    Report.zReplaceAll(aBody, "х—–¬о»нф", 
		    		Report.legal(dbObject, "data.app1.infrastructure.сonclusion_inf.terms"));
		    Report.zReplaceAll(aBody, "х«ƒ¬о»нф", 
		    		Report.legal(dbObject, "data.app1.infrastructure.сonclusion_inf.contracts"));
		    
		    x = Report.legal(dbObject, "data.app1.infrastructure.сonclusion_inf.sum_total");
		    c1 += Report.zint(x);
		    Report.zReplaceAll(aBody, "х—¬¬о»нф", x);
		    
		    x = Report.legal(dbObject, "data.app1.infrastructure.сonclusion_inf.sum_own");
		    c2 += Report.zint(x);
		    Report.zReplaceAll(aBody, "х——¬о»нф", x);
		    
		    x = Report.legal(dbObject, "data.app1.infrastructure.сonclusion_inf.sum_creditor");
		    c3 += Report.zint(x);
		    Report.zReplaceAll(aBody, "х— ¬о»нф", x);
		    
		    x = Report.legal(dbObject, "data.app1.infrastructure.сonclusion_inf.sum_rest_own");
		    c4 += Report.zint(x);
		    Report.zReplaceAll(aBody, "хќ—¬о»нф", x);
		    
		    x = Report.legal(dbObject, "data.app1.infrastructure.сonclusion_inf.sum_rest_creditor");
		    c5 += Report.zint(x);
		    Report.zReplaceAll(aBody, "хќ ¬о»нф", x);
		    
            // —ћ–
		    // (183-189) -------------------------------------------------------
		    Report.zReplaceAll(aBody, "х—–«а—ћ–", 
		    		Report.legal(dbObject, "data.app1.cmp.сonclusion_gen.terms"));
		    Report.zReplaceAll(aBody, "х«ƒ«а—ћ–", 
		    		Report.legal(dbObject, "data.app1.cmp.сonclusion_gen.contracts"));
		    
		    x = Report.legal(dbObject, "data.app1.cmp.сonclusion_gen.sum_total");
		    c1 += Report.zint(x);
		    Report.zReplaceAll(aBody, "х—¬«а—ћ–", x);
		    
		    x = Report.legal(dbObject, "data.app1.cmp.сonclusion_gen.sum_own");
		    c2 += Report.zint(x);
		    Report.zReplaceAll(aBody, "х——«а—ћ–", x);
		    
		    x = Report.legal(dbObject, "data.app1.cmp.сonclusion_gen.sum_creditor");
		    c3 += Report.zint(x);
		    Report.zReplaceAll(aBody, "х— «а—ћ–", x);
		    
		    x = Report.legal(dbObject, "data.app1.cmp.сonclusion_gen.sum_rest_own");
		    c4 += Report.zint(x);
		    Report.zReplaceAll(aBody, "хќ—«а—ћ–", x);
		    
		    x = Report.legal(dbObject, "data.app1.cmp.сonclusion_gen.sum_rest_creditor");
		    c5 += Report.zint(x);
		    Report.zReplaceAll(aBody, "хќ «а—ћ–", x);
		    // (190-196) -------------------------------------------------------
		    Report.zReplaceAll(aBody, "х—––б—ћ–", 
		    		Report.legal(dbObject, "data.app1.cmp.сonclusion_build.terms"));
		    Report.zReplaceAll(aBody, "х«ƒ–б—ћ–", 
		    		Report.legal(dbObject, "data.app1.cmp.сonclusion_build.contracts"));
		    
		    x = Report.legal(dbObject, "data.app1.cmp.сonclusion_build.sum_total");
		    c1 += Report.zint(x);
		    Report.zReplaceAll(aBody, "х—¬–б—ћ–", x);
		    
		    x = Report.legal(dbObject, "data.app1.cmp.сonclusion_build.sum_own");
		    c2 += Report.zint(x);
		    Report.zReplaceAll(aBody, "х——–б—ћ–", x);
		    
		    x = Report.legal(dbObject, "data.app1.cmp.сonclusion_build.sum_creditor");
		    c3 += Report.zint(x);
		    Report.zReplaceAll(aBody, "х— –б—ћ–", x);
		    
		    x = Report.legal(dbObject, "data.app1.cmp.сonclusion_build.sum_rest_own");
		    c4 += Report.zint(x);
		    Report.zReplaceAll(aBody, "хќ—–б—ћ–", x);
		    
		    x = Report.legal(dbObject, "data.app1.cmp.сonclusion_build.sum_rest_creditor");
		    c5 += Report.zint(x);
		    Report.zReplaceAll(aBody, "хќ –б—ћ–", x);
		    
            // ќ—Ќќ¬Ќќ≈ ќЅќ–”ƒќ¬јЌ»≈
		    // (197-203) -------------------------------------------------------
		    Report.zReplaceAll(aBody, "х—–ѕќбќќ", 
		    		Report.legal(dbObject, "data.app1.main_equipment.equipment.terms"));
		    Report.zReplaceAll(aBody, "х«ƒѕќбќќ", 
		    		Report.legal(dbObject, "data.app1.main_equipment.equipment.contracts"));
		    
		    x = Report.legal(dbObject, "data.app1.main_equipment.equipment.sum_total");
		    c1 += Report.zint(x);
		    Report.zReplaceAll(aBody, "х—¬ѕќбќќ", x);
		    
		    x = Report.legal(dbObject, "data.app1.main_equipment.equipment.sum_own");
		    c2 += Report.zint(x);
		    Report.zReplaceAll(aBody, "х——ѕќбќќ", x);
		    
		    x = Report.legal(dbObject, "data.app1.main_equipment.equipment.sum_creditor");
		    c3 += Report.zint(x);
		    Report.zReplaceAll(aBody, "х— ѕќбќќ", x);
		    
		    x = Report.legal(dbObject, "data.app1.main_equipment.equipment.sum_rest_own");
		    c4 += Report.zint(x);
		    Report.zReplaceAll(aBody, "хќ—ѕќбќќ", x);
		    
		    x = Report.legal(dbObject, "data.app1.main_equipment.equipment.sum_rest_creditor");
		    c5 += Report.zint(x);
		    Report.zReplaceAll(aBody, "хќ ѕќбќќ", x);
		    // (204-210) -------------------------------------------------------
		    Report.zReplaceAll(aBody, "х—––абќќ", 
		    		Report.legal(dbObject, "data.app1.main_equipment.installation.terms"));
		    Report.zReplaceAll(aBody, "х«ƒ–абќќ", 
		    		Report.legal(dbObject, "data.app1.main_equipment.installation.contracts"));
		    
		    x = Report.legal(dbObject, "data.app1.main_equipment.installation.sum_total");
		    c1 += Report.zint(x);
		    Report.zReplaceAll(aBody, "х—¬–абќќ", x);
		    
		    x = Report.legal(dbObject, "data.app1.main_equipment.installation.sum_own");
		    c2 += Report.zint(x);
		    Report.zReplaceAll(aBody, "х——–абќќ", x);
		    
		    x = Report.legal(dbObject, "data.app1.main_equipment.installation.sum_creditor");
		    c3 += Report.zint(x);
		    Report.zReplaceAll(aBody, "х— –абќќ", x);
		    
		    x = Report.legal(dbObject, "data.app1.main_equipment.installation.sum_rest_own");
		    c4 += Report.zint(x);
		    Report.zReplaceAll(aBody, "хќ—–абќќ", x);
		    
		    x = Report.legal(dbObject, "data.app1.main_equipment.installation.sum_rest_creditor");
		    c5 += Report.zint(x);
		    Report.zReplaceAll(aBody, "хќ –абќќ", x);

		    // ¬—ѕќћќ√ј“≈Ћ№Ќќ≈ ќЅќ–”ƒќ¬јЌ»≈
		    // (211-217) -------------------------------------------------------
		    Report.zReplaceAll(aBody, "х—–ѕќб¬ќ", 
		    		Report.legal(dbObject, "data.app1.add_equipment.equipment.terms"));
		    Report.zReplaceAll(aBody, "х«ƒѕќб¬ќ", 
		    		Report.legal(dbObject, "data.app1.add_equipment.equipment.contracts"));
		    
		    x = Report.legal(dbObject, "data.app1.add_equipment.equipment.sum_total");
		    c1 += Report.zint(x);
		    Report.zReplaceAll(aBody, "х—¬ѕќб¬ќ", x);
		    
		    x = Report.legal(dbObject, "data.app1.add_equipment.equipment.sum_own");
		    c2 += Report.zint(x);
		    Report.zReplaceAll(aBody, "х——ѕќб¬ќ", x);
		    
		    x = Report.legal(dbObject, "data.app1.add_equipment.equipment.sum_creditor");
		    c3 += Report.zint(x);
		    Report.zReplaceAll(aBody, "х— ѕќб¬ќ", x);
		    
		    x = Report.legal(dbObject, "data.app1.add_equipment.equipment.sum_rest_own");
		    c4 += Report.zint(x);
		    Report.zReplaceAll(aBody, "хќ—ѕќб¬ќ", x);
		    
		    x = Report.legal(dbObject, "data.app1.add_equipment.equipment.sum_rest_creditor");
		    c5 += Report.zint(x);
		    Report.zReplaceAll(aBody, "хќ ѕќб¬ќ", x);
		    // (218-224) -------------------------------------------------------
		    Report.zReplaceAll(aBody, "х—––аб¬ќ", 
		    		Report.legal(dbObject, "data.app1.add_equipment.installation.terms"));
		    Report.zReplaceAll(aBody, "х«ƒ–аб¬ќ", 
		    		Report.legal(dbObject, "data.app1.add_equipment.installation.contracts"));
		    
		    x = Report.legal(dbObject, "data.app1.add_equipment.installation.sum_total");
		    c1 += Report.zint(x);
		    Report.zReplaceAll(aBody, "х—¬–аб¬ќ", x);
		    
		    x = Report.legal(dbObject, "data.app1.add_equipment.installation.sum_own");
		    c2 += Report.zint(x);
		    Report.zReplaceAll(aBody, "х——–аб¬ќ", x);
		    
		    x = Report.legal(dbObject, "data.app1.add_equipment.installation.sum_creditor");
		    c3 += Report.zint(x);
		    Report.zReplaceAll(aBody, "х— –аб¬ќ", x);
		    
		    x = Report.legal(dbObject, "data.app1.add_equipment.installation.sum_rest_own");
		    c4 += Report.zint(x);
		    Report.zReplaceAll(aBody, "хќ—–аб¬ќ", x);
		    
		    x = Report.legal(dbObject, "data.app1.add_equipment.installation.sum_rest_creditor");
		    c5 += Report.zint(x);
		    Report.zReplaceAll(aBody, "хќ –аб¬ќ", x);
		    
            // —џ–№≈ » ћј“≈–»јЋџ
		    // (225-231) -------------------------------------------------------
		    Report.zReplaceAll(aBody, "х—–ƒогѕ—", 
		    		Report.legal(dbObject, "data.app1.raw_materials.terms"));
		    Report.zReplaceAll(aBody, "х«ƒƒогѕ—", 
		    		Report.legal(dbObject, "data.app1.raw_materials.contracts"));
		    
		    x = Report.legal(dbObject, "data.app1.raw_materials.sum_total");
		    c1 += Report.zint(x);
		    Report.zReplaceAll(aBody, "х—¬ƒогѕ—", x);
		    
		    x = Report.legal(dbObject, "data.app1.raw_materials.sum_own");
		    c2 += Report.zint(x);
		    Report.zReplaceAll(aBody, "х——ƒогѕ—", x);
		    
		    x = Report.legal(dbObject, "data.app1.raw_materials.sum_creditor");
		    c3 += Report.zint(x);
		    Report.zReplaceAll(aBody, "х— ƒогѕ—", x);
		    
		    x = Report.legal(dbObject, "data.app1.raw_materials.sum_rest_own");
		    c4 += Report.zint(x);
		    Report.zReplaceAll(aBody, "хќ—ƒогѕ—", x);
		    
		    x = Report.legal(dbObject, "data.app1.raw_materials.sum_rest_creditor");
		    c5 += Report.zint(x);
		    Report.zReplaceAll(aBody, "хќ ƒогѕ—", x);
		    
            // ¬—≈√ќ
		    Report.zReplaceAll(aBody, "х—¬—ум", Integer.toString(c1));
		    Report.zReplaceAll(aBody, "х———ум", Integer.toString(c2));
		    Report.zReplaceAll(aBody, "х— —ум", Integer.toString(c3));
		    Report.zReplaceAll(aBody, "хќ——ум", Integer.toString(c4));
		    Report.zReplaceAll(aBody, "хќ —ум", Integer.toString(c5));
		    
            // App3 (148-236) ќбоснование бюджета проекта.
		    BasicDBList app3List = data == null? null: (BasicDBList)data.get("app3");

		    if (app3List == null) count = 0; else count = app3List.size();
		    for (int i = 0; i < count; i++) {
		    	x = Report.legal(app3List, i, "year");
		    	if (x.equals("¬ натуральном выражении, едениц в год")) {
		    		Report.zReplaceAll(aBody, "хЌ¬≈мк", Report.legal(app3List, i, "app3_market_volume"));
		    		Report.zReplaceAll(aBody, "хЌ¬ќб", Report.legal(app3List, i, "app3_product_volume"));
		    		Report.zReplaceAll(aBody, "хЌ¬ѕотенц", Report.legal(app3List, i, "app3_potential_volume"));
		    		Report.zReplaceAll(aBody, "хЌ¬ѕлан", Report.legal(app3List, i, "app3_plan_volume"));
		    	}
		    	else if (x.equals("¬ денежном выражении, млн. в год")) {
		    		Report.zReplaceAll(aBody, "хƒ¬≈мк", Report.legal(app3List, i, "app3_market_volume"));
		    		Report.zReplaceAll(aBody, "хƒ¬ќб", Report.legal(app3List, i, "app3_product_volume"));
		    		Report.zReplaceAll(aBody, "хƒ¬ѕотенц", Report.legal(app3List, i, "app3_potential_volume"));
		    		Report.zReplaceAll(aBody, "хƒ¬ѕлан", Report.legal(app3List, i, "app3_plan_volume"));
		    	}
		    	else if (x.equals("»сточники информации и (или) подтверждающие документы")) {
		    		Report.zReplaceAll(aBody, "х»»≈мк", Report.legal(app3List, i, "app3_market_volume"));
		    		Report.zReplaceAll(aBody, "х»»ќб", Report.legal(app3List, i, "app3_product_volume"));
		    		Report.zReplaceAll(aBody, "х»»ѕотенц", Report.legal(app3List, i, "app3_potential_volume"));
		    		Report.zReplaceAll(aBody, "х»»ѕлан", Report.legal(app3List, i, "app3_plan_volume"));
		    	}
		    }
		    // ƒополнить таблицу app3
    		Report.zReplaceAll(aBody, "хЌ¬≈мк", "");
    		Report.zReplaceAll(aBody, "хЌ¬ќб", "");
    		Report.zReplaceAll(aBody, "хЌ¬ѕотенц", "");
    		Report.zReplaceAll(aBody, "хЌ¬ѕлан", "");
		    
    		Report.zReplaceAll(aBody, "хƒ¬≈мк", "");
    		Report.zReplaceAll(aBody, "хƒ¬ќб", "");
    		Report.zReplaceAll(aBody, "хƒ¬ѕотенц", "");
    		Report.zReplaceAll(aBody, "хƒ¬ѕлан", "");
		    
    		Report.zReplaceAll(aBody, "х»»≈мк", "");
    		Report.zReplaceAll(aBody, "х»»ќб", "");
    		Report.zReplaceAll(aBody, "х»»ѕотенц", "");
    		Report.zReplaceAll(aBody, "х»»ѕлан", "");
    		
            // (хƒ¬≈мк) ќЅўјя ≈ћ ќ—“№ –џЌ ј ѕ–ќƒ” ÷»» - ¬ денежном выражении, млн. в год (268)
    		f = Report.zfloat(Report.legal(dbObject, "data.market.market_emkost")) / 1000;
    		Report.zReplaceAll(aBody, "хƒ¬≈мк", Float.toString(f));
            // (х»»≈мк) ќЅўјя ≈ћ ќ—“№ –џЌ ј ѕ–ќƒ” ÷»» - »сточники информации и (или) подтверждающие документы (272)
    		Report.zReplaceAll(aBody, "х»»≈мк", Report.legal(dbObject, "data.market.market_info"));
    		
		    //---------------------- ѕо коллекции enterprise ------------------------------------
		    dbCollection = db.getCollection("enterprise");
		    idQuery = new BasicDBObject(Report.parent_name, aParam);
		    dbObject = dbCollection.findOne(idQuery);

            String enterpriseName = Report.exam(dbObject, "data.enterprise_data.name");
            String enterpriseActivity = Report.exam(dbObject, "data.enterprise_data.activity");
            String enterpriseReg = Report.legal(dbObject, "data.enterprise_data.address_reg");
            enterpriseReg =  enterpriseReg + (enterpriseReg.trim().length() == 0? "": ", ") +
            		Report.legal(dbObject, "data.enterprise_data.date_reg");
            
            // (хƒата¬ключени€в–еестр) —татус субъекта ћ—ѕ (1)
		    Report.zReplaceAll(aBody, "хƒата¬ключени€в–еестр", Report.exam(dbObject, "data.enterprise_data.status_msp"));
            // (х—труктура—обственности) —труктура собственности (25)
	    	String benif = "";
	    	int und;
		    data = dbObject == null? null: (DBObject)dbObject.get("data");
	    	BasicDBList ownList = data == null? null: (BasicDBList)data.get("enterprise_owners");
		    if (ownList == null) count = 0; else count = ownList.size();
	    	for (int i = 0; i < count; i++) {
	    		try {
	    			und = ((BasicDBObject)ownList.get(i)).getInt("own_percent");
	    		} catch (Exception  ex) {
	    			und = 0;
	    		}
	    		if (Report.legal(ownList, i, "owner.urlico").trim().length() > 0) {	// ёрлицо
	    			benif += Report.exam(ownList, i, "owner.urlico.name") + " - " + Integer.toString(und) + "%" + CR;
	    			continue;
	    		}
	    		if (Report.legal(ownList, i, "owner.beneficiar").trim().length() > 0) {	// ‘излицо
	    			benif += Report.exam(ownList, i, "owner.beneficiar.fio") + " - " + Integer.toString(und) + "%" + CR;
	    			continue;
	    		}
	    	}
	    	Report.zReplaceAll(aBody, "х—труктура—обственности", benif);
            // (х»стори€–азвити€) »стори€ развити€ (26)
		    Report.zReplaceAll(aBody, "х»стори€–азвити€", Report.exam(dbObject, "data.enterprise_data.history"));
            // (27-28) ¬ыручка / „иста€ прибыль за последний финансовый год +
            // (30-44) »сторические финансовые показатели за последние три года
	    	Calendar c = new GregorianCalendar();
	    	String m1Year = String.valueOf(c.get(Calendar.YEAR)-1);
	    	String m2Year = String.valueOf(c.get(Calendar.YEAR)-2);
	    	String m3Year = String.valueOf(c.get(Calendar.YEAR)-3);
	    	BasicDBList dohodList = data == null? null: (BasicDBList)data.get("enterprise_dohod");
	    	if (dohodList == null) count = 0; else count = dohodList.size();
	    	for (int i = 0; i < count; i++) {
	    		x = Report.legal(dohodList, i, "year");
	    		if (x.equals(m3Year)) {
	    		    Report.zReplaceAll(aBody, "х¬ыр2014", Report.exam(dohodList, i, "proceeds_comp"));
	    		    Report.zReplaceAll(aBody, "хќѕ2014", Report.exam(dohodList, i, "proceeds_oper"));
	    		    Report.zReplaceAll(aBody, "х–ќѕ2014", Report.exam(dohodList, i, "proceeds_rent"));
	    		    Report.zReplaceAll(aBody, "х„ѕ2014", Report.exam(dohodList, i, "proceeds_clear"));
	    		    Report.zReplaceAll(aBody, "хƒ—Ё2014", Report.exam(dohodList, i, "proceeds_money"));
	    		}
	    		else if (x.equals(m2Year)) {
	    		    Report.zReplaceAll(aBody, "х¬ыр2015", Report.exam(dohodList, i, "proceeds_comp"));
	    		    Report.zReplaceAll(aBody, "хќѕ2015", Report.exam(dohodList, i, "proceeds_oper"));
	    		    Report.zReplaceAll(aBody, "х–ќѕ2015", Report.exam(dohodList, i, "proceeds_rent"));
	    		    Report.zReplaceAll(aBody, "х„ѕ2015", Report.exam(dohodList, i, "proceeds_clear"));
	    		    Report.zReplaceAll(aBody, "хƒ—Ё2015", Report.exam(dohodList, i, "proceeds_money"));
	    		}
	    		else if (x.equals(m1Year)) {
	    		    Report.zReplaceAll(aBody, "х¬ыр2016", Report.exam(dohodList, i, "proceeds_comp"));
	    		    Report.zReplaceAll(aBody, "хќѕ2016", Report.exam(dohodList, i, "proceeds_oper"));
	    		    Report.zReplaceAll(aBody, "х–ќѕ2016", Report.exam(dohodList, i, "proceeds_rent"));
	    		    Report.zReplaceAll(aBody, "х„ѕ2016", Report.exam(dohodList, i, "proceeds_clear"));
	    		    Report.zReplaceAll(aBody, "хƒ—Ё2016", Report.exam(dohodList, i, "proceeds_money"));
	    		}
	    	}
	    	// «аполнить незап.места в таблице финансовых показателей
		    Report.zReplaceAll(aBody, "х¬ыр2014", Report.question_marks);
		    Report.zReplaceAll(aBody, "хќѕ2014", Report.question_marks);
		    Report.zReplaceAll(aBody, "х–ќѕ2014", Report.question_marks);
		    Report.zReplaceAll(aBody, "х„ѕ2014", Report.question_marks);
		    Report.zReplaceAll(aBody, "хƒ—Ё2014", Report.question_marks);
		    
		    Report.zReplaceAll(aBody, "х¬ыр2015", Report.question_marks);
		    Report.zReplaceAll(aBody, "хќѕ2015", Report.question_marks);
		    Report.zReplaceAll(aBody, "х–ќѕ2015", Report.question_marks);
		    Report.zReplaceAll(aBody, "х„ѕ2015", Report.question_marks);
		    Report.zReplaceAll(aBody, "хƒ—Ё2015", Report.question_marks);
		    
		    Report.zReplaceAll(aBody, "х¬ыр2016", Report.question_marks);
		    Report.zReplaceAll(aBody, "хќѕ2016", Report.question_marks);
		    Report.zReplaceAll(aBody, "х–ќѕ2016", Report.question_marks);
		    Report.zReplaceAll(aBody, "х„ѕ2016", Report.question_marks);
		    Report.zReplaceAll(aBody, "хƒ—Ё2016", Report.question_marks);
		    
            // (х»нформаци€ќ“екущих редитах) »нформаци€ о текущих кредитах (29)
		    Report.zReplaceAll(aBody, "х»нформаци€ќ“екущих редитах", Report.exam(dbObject, "data.enterprise_data.cur_credits"));
            // (х“ек„исл—отр) “екуща€ численность сотрудников (45)
		    Report.zReplaceAll(aBody, "х“ек„исл—отр", Report.exam(dbObject, "data.enterprise_data.cur_staff"));
            // (хЌаличиеЌалоговой«адолженности) Ќаличие неурегулированной просроченной налоговой задолженности с учетом √руппы лиц (46)
		    Report.zReplaceAll(aBody, "хЌаличиеЌалоговой«адолженности", Report.exam(dbObject, "data.enterprise_data.tax_debt"));
            // (хћенеджмент) ћенеджмент/ (147)
		    String management = "";
		    BasicDBList mngrList = data == null? null: (BasicDBList)data.get("group_of_people");
		    if (mngrList == null) count = 0; else count = mngrList.size();
		    for (int i = 0; i < count; i++) {
		    	management += Report.legal(mngrList, i, "high_manager") + ", " +
		    			Report.legal(mngrList, i, "position") + ", " +
		    			Report.legal(mngrList, i, "phone") + CR;
		    }
		    Report.zReplaceAll(aBody, "хћенеджмент", management);
            // (237 - 263) ѕриложение 2 - —труктура √руппы лиц
		    String[][] prsn = new String[9][3];
		    for (int i = 0; i < 9; i++) for (int j = 0; j < 3; j++) prsn[i][j] = "";
	    	BasicDBList groupList = data == null? null: (BasicDBList)data.get("group_of_people");
	    	if (groupList == null) count = 0; else count = Math.min(groupList.size(), 9);
	    	for (int i = 0; i < count; i++) {
	    		prsn[i][0] = Report.legal(groupList, i, "compain_name");
	    		prsn[i][1] = Report.legal(groupList, i, "participant");
	    		prsn[i][2] = Report.legal(groupList, i, "percent");
	    	}
		    Report.zReplaceAll(aBody, "х омп1", prsn[0][0]);
		    Report.zReplaceAll(aBody, "х¬лад1", prsn[0][1]);
		    Report.zReplaceAll(aBody, "хƒол€1", prsn[0][2]);
	    	
		    Report.zReplaceAll(aBody, "х омп2", prsn[1][0]);
		    Report.zReplaceAll(aBody, "х¬лад2", prsn[1][1]);
		    Report.zReplaceAll(aBody, "хƒол€2", prsn[1][2]);
	    	
		    Report.zReplaceAll(aBody, "х омп3", prsn[2][0]);
		    Report.zReplaceAll(aBody, "х¬лад3", prsn[2][1]);
		    Report.zReplaceAll(aBody, "хƒол€3", prsn[2][2]);
	    	
		    Report.zReplaceAll(aBody, "х омп4", prsn[3][0]);
		    Report.zReplaceAll(aBody, "х¬лад4", prsn[3][1]);
		    Report.zReplaceAll(aBody, "хƒол€4", prsn[3][2]);
	    	
		    Report.zReplaceAll(aBody, "х омп5", prsn[4][0]);
		    Report.zReplaceAll(aBody, "х¬лад5", prsn[4][1]);
		    Report.zReplaceAll(aBody, "хƒол€5", prsn[4][2]);
	    	
		    Report.zReplaceAll(aBody, "х омп6", prsn[5][0]);
		    Report.zReplaceAll(aBody, "х¬лад6", prsn[5][1]);
		    Report.zReplaceAll(aBody, "хƒол€6", prsn[5][2]);
	    	
		    Report.zReplaceAll(aBody, "х омп7", prsn[6][0]);
		    Report.zReplaceAll(aBody, "х¬лад7", prsn[6][1]);
		    Report.zReplaceAll(aBody, "хƒол€7", prsn[6][2]);
	    	
		    Report.zReplaceAll(aBody, "х омп8", prsn[7][0]);
		    Report.zReplaceAll(aBody, "х¬лад8", prsn[7][1]);
		    Report.zReplaceAll(aBody, "хƒол€8", prsn[7][2]);
	    	
		    Report.zReplaceAll(aBody, "х омп9", prsn[8][0]);
		    Report.zReplaceAll(aBody, "х¬лад9", prsn[8][1]);
		    Report.zReplaceAll(aBody, "хƒол€9", prsn[8][2]);
	    	
		    //---------------------- ѕо коллекции project_ent ------------------------------------
		    dbCollection = db.getCollection("project_ent");
		    idQuery = new BasicDBObject(Report.parent_name, aParam);
		    dbObject = dbCollection.findOne(idQuery);

		    if (cred_init) { // (47-56)
			    Report.zReplaceAll(aBody, "хѕ Ќаименование", "");				// (47)
			    Report.zReplaceAll(aBody, "хѕ ƒатаћесто–ег", "");				// (48)
			    Report.zReplaceAll(aBody, "хѕ ÷елевой¬идƒе€тельности", "");		// (49)
			    Report.zReplaceAll(aBody, "хѕ —труктура—обственности", "");		// (50)
			    Report.zReplaceAll(aBody, "хѕ “екуща€¬ыручка", "");				// (51)
			    Report.zReplaceAll(aBody, "х„иста€ѕрибыль", "");				// (52)
			    Report.zReplaceAll(aBody, "хѕ »нформаци€ќ“екущих редитах", "");	// (53)
			    Report.zReplaceAll(aBody, "хѕ “ек—отр", "");					// (54)
			    Report.zReplaceAll(aBody, "хѕ ÷ел—отр", "");					// (55)
		    }
		    else {
                // (хѕ Ќаименование) Ќаименование проектной организации (47)
			    Report.zReplaceAll(aBody, "хѕ Ќаименование", Report.legal(dbObject, "data.enterprise_data.name"));
                // (хѕ ƒатаћесто–ег) ƒата и место регистрации проектной организации (48)
			    x = Report.legal(dbObject, "data.enterprise_data.address_reg");
			    Report.zReplaceAll(aBody, "хѕ ƒатаћесто–ег", 
			    		x + (x.trim().length() == 0? "": ", ") + Report.legal(dbObject, "data.enterprise_data.date_reg"));
                // (хѕ ÷елевой¬идƒе€тельности) ÷елевой вид де€тельности проектной организации (49)
			    Report.zReplaceAll(aBody, "хѕ ÷елевой¬идƒе€тельности", Report.legal(dbObject, "data.enterprise_data.activity"));
                // (хѕ —труктура—обственности) —труктура собственности (50)
		    	benif = "";
			    data = dbObject == null? null: (DBObject)dbObject.get("data");
		    	ownList = data == null? null: (BasicDBList)data.get("enterprise_owners");
			    if (ownList == null) count = 0; else count = ownList.size();
		    	for (int i = 0; i < count; i++) {
		    		try {
		    			und = ((BasicDBObject)ownList.get(i)).getInt("own_percent");
		    		} catch (Exception  ex) {
		    			und = 0;
		    		}
		    		if (Report.legal(ownList, i, "owner.urlico").trim().length() > 0) {	// ёрлицо
		    			benif += Report.exam(ownList, i, "owner.urlico.name") + " - " + Integer.toString(und) + "%" + CR;
		    			continue;
		    		}
		    		if (Report.legal(ownList, i, "owner.beneficiar").trim().length() > 0) {	// ‘излицо
		    			benif += Report.exam(ownList, i, "owner.beneficiar.fio") + " - " + Integer.toString(und) + "%" + CR;
		    			continue;
		    		}
		    	}
		    	Report.zReplaceAll(aBody, "хѕ —труктура—обственности", benif);
                // (хѕ “екуща€¬ыручка, х„иста€ѕрибыль) “екуща€ выручка / „иста€ прибыль (если применимо) дл€ проектной организации (51-52)
                //ddt_3.checkList[51].param = util.exam(doc.data.enterprise_dohod[i].proceeds_comp);
                //ddt_3.checkList[52].param = util.exam(doc.data.enterprise_dohod[i].proceeds_clear);
		    	dohodList = data == null? null: (BasicDBList)data.get("enterprise_dohod");
		    	if (dohodList == null) count = 0; else count = dohodList.size();
		    	for (int i = 0; i < count; i++) {
		    		x = Report.legal(dohodList, i, "year");
		    		if (x.equals("2016")) {
		    			Report.zReplaceAll(aBody, "хѕ “екуща€¬ыручка", Report.exam(dohodList, i, "proceeds_comp"));		    			
		    			Report.zReplaceAll(aBody, "х„иста€ѕрибыль", Report.exam(dohodList, i, "proceeds_clear"));		    			
		    		}
		    	}
    			Report.zReplaceAll(aBody, "хѕ “екуща€¬ыручка", Report.question_marks);		    			
    			Report.zReplaceAll(aBody, "х„иста€ѕрибыль", Report.question_marks);		    			
		    	
                // (хЌаименование, хƒатаћесто–ег, хќсновной¬идƒе€тельности) —понсор (инициатор) проекта  (22-24)
    			if (proj_init_direct) {   // ѕр€мое обращение инициатора
    				Report.zReplaceAll(aBody, "хЌаименование", enterpriseName);	
    				Report.zReplaceAll(aBody, "хƒатаћесто–ег", enterpriseReg);	
    				Report.zReplaceAll(aBody, "хќсновной¬идƒе€тельности", enterpriseActivity);	
    			}
    			else if (proj_init_subjectrf) {   // —”ЅЏ≈ “ –‘
    				Report.zReplaceAll(aBody, "хЌаименование", proj_init_subjectrf_name);	
    				Report.zReplaceAll(aBody, "хƒатаћесто–ег", enterpriseReg);	
    				Report.zReplaceAll(aBody, "хќсновной¬идƒе€тельности", "");	
    			}
    			else if (proj_init_unions) {   // —”ЅЏ≈ “ –‘
    				Report.zReplaceAll(aBody, "хЌаименование", proj_init_unions_name);	
    				Report.zReplaceAll(aBody, "хƒатаћесто–ег", enterpriseReg);	
    				Report.zReplaceAll(aBody, "хќсновной¬идƒе€тельности", "");	
    			}
    			else {
    				Report.zReplaceAll(aBody, "хЌаименование", Report.question_marks);	
    				Report.zReplaceAll(aBody, "хƒатаћесто–ег", Report.question_marks);	
    				Report.zReplaceAll(aBody, "хќсновной¬идƒе€тельности", Report.question_marks);	
    				
    			}
		    }
    		
		} catch (Exception  ex) {
			// ќшибка
		    return new ArrayList<String>(){{
		    	add("error");
		    	add(ex.toString());
		    }};
		}
		return aBody;
	}
}
