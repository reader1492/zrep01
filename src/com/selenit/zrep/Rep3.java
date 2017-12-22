// Valerii Zinovev, Perm, 13 aug 2017
// ������ ����� (������ �������)
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
	// ���������
	private static final String CR = "</w:t></w:r><w:proofErr w:type=\"spellEnd\" /></w:p>" +
		    "<w:p w14:paraId=\"13CA829D\" w14:textId=\"7807F61F\" w:rsidR=\"00EB7BCB\" w:rsidRPr=\"00676B6B\" " +
		    "w:rsidRDefault=\"00BD1753\" w:rsidP=\"00580745\"><w:pPr><w:rPr><w:color w:val=\"auto\" /></w:rPr></w:pPr>" +
		    "<w:proofErr w:type=\"spellStart\" /><w:r><w:rPr><w:color w:val=\"auto\" /></w:rPr><w:t>";
	
	public static List<String> build(List<String> aBody, String aParam) {
		String x, y;
		Float f;
		int count;
		try {
			// ��������� ����
		    MongoClient mongo = new MongoClient(Report.rep_bd_server , Report.rep_bd_port);
		    DB db = mongo.getDB(Report.rep_bd_name);
		    
		    // ----------------------- �� ��������� project --------------------------------------
		    DBCollection dbCollection = db.getCollection("project");
		    BasicDBObject idQuery = new BasicDBObject(Report.parent_name, aParam);
		    DBObject dbObject = dbCollection.findOne(idQuery);
		    
		    // ��� �������� (cred_init)
		    boolean cred_init = Report.legal(dbObject, "data.credit.cred_ent") != "��������� �����������";
            // ��������� ������� (proj_init)
		    String proj_init = Report.legal(dbObject, "data.credit.who_init");
		    boolean proj_init_direct = Report.legal(dbObject, "data.credit.who_init.direct").trim().length() > 0;
		    boolean proj_init_subjectrf = Report.legal(dbObject, "data.credit.who_init.subjectrf").trim().length() > 0;
		    boolean proj_init_unions = Report.legal(dbObject, "data.credit.who_init.unions").trim().length() > 0;
		    String proj_init_subjectrf_name = Report.exam(dbObject, "data.credit.who_init.subjectrf.name");
		    String proj_init_unions_name = Report.legal(dbObject, "data.credit.who_init.unions.name");
		    
            // (�������) ��� �������
		    Report.zReplaceAll(aBody, "�������", Report.exam(dbObject, "data.info.project_name"));
            // (������������������) ����� ����� �������
		    Report.zReplaceAll(aBody, "������������������", Report.exam(dbObject, "data.sum.sum_total"));
            // (����) ��������� � �������������� ������������
		    Report.zReplaceAll(aBody, "����", Report.exam(dbObject, "data.sum.sum_doc"));
            // (�������������) ������������
		    Report.zReplaceAll(aBody, "�������������", Report.exam(dbObject, "data.sum.sum_equipment"));
            // (����) ���
		    Report.zReplaceAll(aBody, "����", Report.exam(dbObject, "data.sum.sum_CMP"));
            // (������������������) ��������� ��������
		    Report.zReplaceAll(aBody, "������������������", Report.exam(dbObject, "data.sum.sum_working_capital"));
            // (����) ���
		    Report.zReplaceAll(aBody, "����", Report.exam(dbObject, "data.sum.sum_nds"));
		    // (���������, ����������, ���������, ����������) ��������� �������������� ������� (8-11)
		    float invest = Report.zfloat(Report.legal(dbObject, "data.invest_struct.invest_sum"));
		    x = Report.legal(dbObject, "data.invest_struct.invest_own_percent");
		    f = invest * Report.zfloat(Report.legal(dbObject, "data.invest_struct.invest_own_percent")) / 100000;
		    Report.zReplaceAll(aBody, "���������", Float.toString(f));
		    f = Report.zfloat(Report.legal(dbObject, "data.invest_struct.invest_own_percent"));
		    Report.zReplaceAll(aBody, "����������", Float.toString(f));
		    f = invest * Report.zfloat(Report.legal(dbObject, "data.invest_struct.invest_debt_percent")) / 100000;
		    Report.zReplaceAll(aBody, "���������", Float.toString(f));
		    f = Report.zfloat(Report.legal(dbObject, "data.invest_struct.invest_debt_percent"));
		    Report.zReplaceAll(aBody, "����������", Float.toString(f));
            // (������) ������ - ����� (12)
		    Report.zReplaceAll(aBody, "������", Report.exam(dbObject, "data.credit.sum"));
            // (�����) ������ - ���� (13)
		    Report.zReplaceAll(aBody, "�����", Report.exam(dbObject, "data.credit.term"));
            // (���������) ������ - ���������� ������ (14)
		    Report.zReplaceAll(aBody, "���������", Report.exam(dbObject, "data.credit.percent"));
            // (�����) ������ - ���� (15)
		    Report.zReplaceAll(aBody, "�����", Report.legal(dbObject, "data.credit.bank_name"));
            // (�������������������) ������ - ������ ������������ (16)
		    Report.zReplaceAll(aBody, "�������������������", Report.legal(dbObject, "data.credit.status_bank"));
            // (����) �������� ��� - ��� (17)
		    Report.zReplaceAll(aBody, "����", Report.exam(dbObject, "data.credit.garant_vid"));
            // (������) �������� ��� - ����� (18)
		    Report.zReplaceAll(aBody, "������", Report.exam(dbObject, "data.credit.garant_sum"));
            // (�������) �������� ��� - ������ (19)
		    Report.zReplaceAll(aBody, "�������", Report.exam(dbObject, "data.credit.garant_other"));
            // (��������������������������, ���������������������) ������������ ����������� (20-21)
		    Report.zReplaceAll(aBody, "��������������������������", Report.exam(dbObject, "data.credit.provision"));
		    Report.zReplaceAll(aBody, "���������������������", Report.exam(dbObject, "data.credit.provision_struct"));
            // (�����������) ��� ������� (56)
		    Report.zReplaceAll(aBody, "�����������", Report.exam(dbObject, "data.info.project_type"));
            // (������������) ���� ������� (57)
		    Report.zReplaceAll(aBody, "������������", Report.exam(dbObject, "data.info.project_goal"));
            // (�������������) ����� ������� / ������ ���������� (58)
		    Report.zReplaceAll(aBody, "�������������", Report.exam(dbObject, "data.info.project_plan"));
            // (���������������������������������������) �������� ������������� ���������� ������� (59)
		    Report.zReplaceAll(aBody, "���������������������������������������", Report.exam(dbObject, "data.info.project_keys"));
            // (��������������������) ������� - ������������ / ����������� � ������������� �������������� (60)
		    Report.zReplaceAll(aBody, "��������������������", Report.exam(dbObject, "data.product.product_name"));
            // (������������������) ���������� / ������� ������� (61)
		    Report.zReplaceAll(aBody, "������������������", Report.exam(dbObject, "data.product.product_unique"));
            // (����������������������) �������� �������� �� ������� ����� / ������������ ������������ / �������� ���������� (62)
		    Report.zReplaceAll(aBody, "����������������������", Report.exam(dbObject, "data.product.product_analog"));
            // (�����������������������) ��������� ������������� / �������� �������, �������� �� ������������� (63)
		    Report.zReplaceAll(aBody, "�����������������������", Report.exam(dbObject, "data.product.product_struct"));
            // (��������������������������) ���� ������� � ������������� (64)
		    Report.zReplaceAll(aBody, "��������������������������", Report.exam(dbObject, "data.product.product_import"));
            // (�������������������������) ������� ������� � ��������� ������������ ���������� (65)
		    Report.zReplaceAll(aBody, "�������������������������", Report.exam(dbObject, "data.tech.tech_new"));
            // (���������������������������������) ������� ��������������� ����������. (66)
		    Report.zReplaceAll(aBody, "���������������������������������", Report.exam(dbObject, "data.tech.tech_exp"));
            // (�����������������������) ������� - ��������������� (�����) (67)
		    Report.zReplaceAll(aBody, "�����������������������", Report.exam(dbObject, "data.tech.tech_loc_address"));
            // (�������������������������) ������� - ��������������� (�����) (68)
		    Report.zReplaceAll(aBody, "�������������������������", Report.exam(dbObject, "data.tech.tech_loc_status"));
            // (������������������������) ��������� �������������� (69)
		    Report.zReplaceAll(aBody, "������������������������", Report.exam(dbObject, "data.tech.inf_current"));
            // (��������������������������) ����������� �������������� (70)
		    Report.zReplaceAll(aBody, "��������������������������", Report.exam(dbObject, "data.tech.inf_future"));
            // (�������������������������) �������� ���������� � ����� (71)
		    Report.zReplaceAll(aBody, "�������������������������", Report.exam(dbObject, "data.market.market_info"));
            // (�������������, �������������, ���������������) ������� �����: ��������� / ������� (������� / ������� / �������� ������ / ������) (72-74)
		    Report.zReplaceAll(aBody, "�������������", Report.exam(dbObject, "data.market.market_goal"));
		    Report.zReplaceAll(aBody, "�������������", Report.exam(dbObject, "data.market.market_emkost"));
		    Report.zReplaceAll(aBody, "���������������", Report.exam(dbObject, "data.market.market_key"));
            // (������������������������������������) ���� ������� � ������� �������� ��������, % (75)
		    Report.zReplaceAll(aBody, "������������������������������������", Report.exam(dbObject, "data.market.market_percent"));
            // (��������������������������) ������� �������������� ������������ (76)
		    Report.zReplaceAll(aBody, "��������������������������", Report.exam(dbObject, "data.market.market_project"));
            // (������������������) ������/ ��������������� �������� / ���������� ������ / ������������ ����������� (77)
		    Report.zReplaceAll(aBody, "������������������", Report.exam(dbObject, "data.market.market_list"));
            // (����������������������������) ����������� ������ ���������� (78)
		    Report.zReplaceAll(aBody, "����������������������������", Report.exam(dbObject, "data.market.market_plan"));
            // (���������������) (��������� �����)������ / ��������������� �������� / ���������� ������ (79)
		    Report.zReplaceAll(aBody, "���������������", Report.exam(dbObject, "data.other.other_raw"));
            // (�������������) (���������/� ������������)������ / ��������������� �������� / ���������� ������ (80)
		    Report.zReplaceAll(aBody, "����������������������", Report.exam(dbObject, "data.other.other_equipment"));
            // (�������������) (��� / ������������)������������ / ���� ���������� �������� �������� (81)
		    Report.zReplaceAll(aBody, "�������������", Report.exam(dbObject, "data.other.other_CMP"));
            // (������������������������������) (��������-�������������� ������������ � ����������) (82)
		    Report.zReplaceAll(aBody, "������������������������������", Report.exam(dbObject, "data.other.other_doc"));
            // (������������������������) (��������-�������������� ������������ � ����������)����� (83)
		    Report.zReplaceAll(aBody, "������������������������", Report.exam(dbObject, "data.other.other_autor"));
            // (���������������������������������������) (���������� �� �������������)���� ��������� (84)
		    Report.zReplaceAll(aBody, "���������������������������������������", Report.exam(dbObject, "data.other.other_building"));
            // (��������) ������� (85)
		    Report.zReplaceAll(aBody, "��������", Report.exam(dbObject, "data.indicators.indic_receipt"));
            // (xEBITDA) EBITDA (86)
		    Report.zReplaceAll(aBody, "xEBITDA", Report.exam(dbObject, "data.indicators.indic_receipt"));
            // (��������������) ������ ������� (87)
		    Report.zReplaceAll(aBody, "��������������", Report.exam(dbObject, "data.indicators.indic_receipt"));
            // (88 - 141)������� ���������� �����������
		    DBObject data = dbObject == null? null: (DBObject)dbObject.get("data");
		    BasicDBList prognosisList = data == null? null: (BasicDBList)data.get("prognosis");

		    if (prognosisList == null) count = 0; else count = prognosisList.size();
		    for (int i = 0; i < count; i++) {
		    	if (Report.legal(prognosisList, i, "year").equals("2016")) {
				    Report.zReplaceAll(aBody, "����16", Report.exam(prognosisList, i, "prognosis_receipts"));
				    Report.zReplaceAll(aBody, "���16", Report.exam(prognosisList, i, "prognosis_EBITDA"));
				    Report.zReplaceAll(aBody, "����16", Report.exam(prognosisList, i, "prognosis_rent_EBITDA"));
				    Report.zReplaceAll(aBody, "���16", Report.exam(prognosisList, i, "prognosis_clear"));
				    Report.zReplaceAll(aBody, "���16", Report.exam(prognosisList, i, "prognosis_money"));
				    Report.zReplaceAll(aBody, "����16", Report.exam(prognosisList, i, "prognosis_op_money"));
				    Report.zReplaceAll(aBody, "����16", Report.exam(prognosisList, i, "prognosis_invest"));
				    Report.zReplaceAll(aBody, "����16", Report.exam(prognosisList, i, "prognosis_post_invest"));
				    Report.zReplaceAll(aBody, "����16", Report.exam(prognosisList, i, "prognosis_post_finans"));
		    	}
		    	if (Report.legal(prognosisList, i, "year").equals("2017")) {
				    Report.zReplaceAll(aBody, "����17", Report.exam(prognosisList, i, "prognosis_receipts"));
				    Report.zReplaceAll(aBody, "���17", Report.exam(prognosisList, i, "prognosis_EBITDA"));
				    Report.zReplaceAll(aBody, "����17", Report.exam(prognosisList, i, "prognosis_rent_EBITDA"));
				    Report.zReplaceAll(aBody, "���17", Report.exam(prognosisList, i, "prognosis_clear"));
				    Report.zReplaceAll(aBody, "���17", Report.exam(prognosisList, i, "prognosis_money"));
				    Report.zReplaceAll(aBody, "����17", Report.exam(prognosisList, i, "prognosis_op_money"));
				    Report.zReplaceAll(aBody, "����17", Report.exam(prognosisList, i, "prognosis_invest"));
				    Report.zReplaceAll(aBody, "����17", Report.exam(prognosisList, i, "prognosis_post_invest"));
				    Report.zReplaceAll(aBody, "����17", Report.exam(prognosisList, i, "prognosis_post_finans"));
		    	}
		    	if (Report.legal(prognosisList, i, "year").equals("2018")) {
				    Report.zReplaceAll(aBody, "����18", Report.exam(prognosisList, i, "prognosis_receipts"));
				    Report.zReplaceAll(aBody, "���18", Report.exam(prognosisList, i, "prognosis_EBITDA"));
				    Report.zReplaceAll(aBody, "����18", Report.exam(prognosisList, i, "prognosis_rent_EBITDA"));
				    Report.zReplaceAll(aBody, "���18", Report.exam(prognosisList, i, "prognosis_clear"));
				    Report.zReplaceAll(aBody, "���18", Report.exam(prognosisList, i, "prognosis_money"));
				    Report.zReplaceAll(aBody, "����18", Report.exam(prognosisList, i, "prognosis_op_money"));
				    Report.zReplaceAll(aBody, "����18", Report.exam(prognosisList, i, "prognosis_invest"));
				    Report.zReplaceAll(aBody, "����18", Report.exam(prognosisList, i, "prognosis_post_invest"));
				    Report.zReplaceAll(aBody, "����18", Report.exam(prognosisList, i, "prognosis_post_finans"));
		    	}
		    	if (Report.legal(prognosisList, i, "year").equals("2019")) {
				    Report.zReplaceAll(aBody, "����19", Report.exam(prognosisList, i, "prognosis_receipts"));
				    Report.zReplaceAll(aBody, "���19", Report.exam(prognosisList, i, "prognosis_EBITDA"));
				    Report.zReplaceAll(aBody, "����19", Report.exam(prognosisList, i, "prognosis_rent_EBITDA"));
				    Report.zReplaceAll(aBody, "���19", Report.exam(prognosisList, i, "prognosis_clear"));
				    Report.zReplaceAll(aBody, "���19", Report.exam(prognosisList, i, "prognosis_money"));
				    Report.zReplaceAll(aBody, "����19", Report.exam(prognosisList, i, "prognosis_op_money"));
				    Report.zReplaceAll(aBody, "����19", Report.exam(prognosisList, i, "prognosis_invest"));
				    Report.zReplaceAll(aBody, "����19", Report.exam(prognosisList, i, "prognosis_post_invest"));
				    Report.zReplaceAll(aBody, "����19", Report.exam(prognosisList, i, "prognosis_post_finans"));
		    	}
		    	if (Report.legal(prognosisList, i, "year").equals("2020")) {
				    Report.zReplaceAll(aBody, "����20", Report.exam(prognosisList, i, "prognosis_receipts"));
				    Report.zReplaceAll(aBody, "���20", Report.exam(prognosisList, i, "prognosis_EBITDA"));
				    Report.zReplaceAll(aBody, "����20", Report.exam(prognosisList, i, "prognosis_rent_EBITDA"));
				    Report.zReplaceAll(aBody, "���20", Report.exam(prognosisList, i, "prognosis_clear"));
				    Report.zReplaceAll(aBody, "���20", Report.exam(prognosisList, i, "prognosis_money"));
				    Report.zReplaceAll(aBody, "����20", Report.exam(prognosisList, i, "prognosis_op_money"));
				    Report.zReplaceAll(aBody, "����20", Report.exam(prognosisList, i, "prognosis_invest"));
				    Report.zReplaceAll(aBody, "����20", Report.exam(prognosisList, i, "prognosis_post_invest"));
				    Report.zReplaceAll(aBody, "����20", Report.exam(prognosisList, i, "prognosis_post_finans"));
		    	}
		    	if (Report.legal(prognosisList, i, "year").equals("2021")) {
				    Report.zReplaceAll(aBody, "����21", Report.exam(prognosisList, i, "prognosis_receipts"));
				    Report.zReplaceAll(aBody, "���21", Report.exam(prognosisList, i, "prognosis_EBITDA"));
				    Report.zReplaceAll(aBody, "����21", Report.exam(prognosisList, i, "prognosis_rent_EBITDA"));
				    Report.zReplaceAll(aBody, "���21", Report.exam(prognosisList, i, "prognosis_clear"));
				    Report.zReplaceAll(aBody, "���21", Report.exam(prognosisList, i, "prognosis_money"));
				    Report.zReplaceAll(aBody, "����21", Report.exam(prognosisList, i, "prognosis_op_money"));
				    Report.zReplaceAll(aBody, "����21", Report.exam(prognosisList, i, "prognosis_invest"));
				    Report.zReplaceAll(aBody, "����21", Report.exam(prognosisList, i, "prognosis_post_invest"));
				    Report.zReplaceAll(aBody, "����21", Report.exam(prognosisList, i, "prognosis_post_finans"));
		    	}
		    }
		    // ��������� ������� ���������
		    Report.zReplaceAll(aBody, "����16", Report.question_marks);
		    Report.zReplaceAll(aBody, "���16", Report.question_marks);
		    Report.zReplaceAll(aBody, "����16", Report.question_marks);
		    Report.zReplaceAll(aBody, "���16", Report.question_marks);
		    Report.zReplaceAll(aBody, "���16", Report.question_marks);
		    Report.zReplaceAll(aBody, "����16", Report.question_marks);
		    Report.zReplaceAll(aBody, "����16", Report.question_marks);
		    Report.zReplaceAll(aBody, "����16", Report.question_marks);
		    Report.zReplaceAll(aBody, "����16", Report.question_marks);
		    
		    Report.zReplaceAll(aBody, "����17", Report.question_marks);
		    Report.zReplaceAll(aBody, "���17", Report.question_marks);
		    Report.zReplaceAll(aBody, "����17", Report.question_marks);
		    Report.zReplaceAll(aBody, "���17", Report.question_marks);
		    Report.zReplaceAll(aBody, "���17", Report.question_marks);
		    Report.zReplaceAll(aBody, "����17", Report.question_marks);
		    Report.zReplaceAll(aBody, "����17", Report.question_marks);
		    Report.zReplaceAll(aBody, "����17", Report.question_marks);
		    Report.zReplaceAll(aBody, "����17", Report.question_marks);
		    
		    Report.zReplaceAll(aBody, "����18", Report.question_marks);
		    Report.zReplaceAll(aBody, "���18", Report.question_marks);
		    Report.zReplaceAll(aBody, "����18", Report.question_marks);
		    Report.zReplaceAll(aBody, "���18", Report.question_marks);
		    Report.zReplaceAll(aBody, "���18", Report.question_marks);
		    Report.zReplaceAll(aBody, "����18", Report.question_marks);
		    Report.zReplaceAll(aBody, "����18", Report.question_marks);
		    Report.zReplaceAll(aBody, "����18", Report.question_marks);
		    Report.zReplaceAll(aBody, "����18", Report.question_marks);

		    Report.zReplaceAll(aBody, "����19", Report.question_marks);
		    Report.zReplaceAll(aBody, "���19", Report.question_marks);
		    Report.zReplaceAll(aBody, "����19", Report.question_marks);
		    Report.zReplaceAll(aBody, "���19", Report.question_marks);
		    Report.zReplaceAll(aBody, "���19", Report.question_marks);
		    Report.zReplaceAll(aBody, "����19", Report.question_marks);
		    Report.zReplaceAll(aBody, "����19", Report.question_marks);
		    Report.zReplaceAll(aBody, "����19", Report.question_marks);
		    Report.zReplaceAll(aBody, "����19", Report.question_marks);
		    
		    Report.zReplaceAll(aBody, "����20", Report.question_marks);
		    Report.zReplaceAll(aBody, "���20", Report.question_marks);
		    Report.zReplaceAll(aBody, "����20", Report.question_marks);
		    Report.zReplaceAll(aBody, "���20", Report.question_marks);
		    Report.zReplaceAll(aBody, "���20", Report.question_marks);
		    Report.zReplaceAll(aBody, "����20", Report.question_marks);
		    Report.zReplaceAll(aBody, "����20", Report.question_marks);
		    Report.zReplaceAll(aBody, "����20", Report.question_marks);
		    Report.zReplaceAll(aBody, "����20", Report.question_marks);
		    
		    Report.zReplaceAll(aBody, "����21", Report.question_marks);
		    Report.zReplaceAll(aBody, "���21", Report.question_marks);
		    Report.zReplaceAll(aBody, "����21", Report.question_marks);
		    Report.zReplaceAll(aBody, "���21", Report.question_marks);
		    Report.zReplaceAll(aBody, "���21", Report.question_marks);
		    Report.zReplaceAll(aBody, "����21", Report.question_marks);
		    Report.zReplaceAll(aBody, "����21", Report.question_marks);
		    Report.zReplaceAll(aBody, "����21", Report.question_marks);
		    Report.zReplaceAll(aBody, "����21", Report.question_marks);
		    
            // (����) NRV (142)
		    Report.zReplaceAll(aBody, "����", Report.exam(dbObject, "data.indicators.indic_nrv"));
            // (����) IRR (143)
		    Report.zReplaceAll(aBody, "����", Report.exam(dbObject, "data.indicators.indic_irr"));
            // (�����������������) ������ (144)
		    Report.zReplaceAll(aBody, "�����������������", Report.exam(dbObject, "data.indicators.indic_other"));
            // (���������������������) ������������� ������� / ����� (145)
		    Report.zReplaceAll(aBody, "���������������������", Report.exam(dbObject, "data.other.other_ec_risk"));
            // (��������������������������������������������) ������������� / ������� ������������� ���������� (146)
		    Report.zReplaceAll(aBody, "��������������������������������������������", 
		    		Report.exam(dbObject, "data.other.other_ec_need"));
            // App1 (148-236) ����������� ������� �������.
		    int c1 = 0, c2 = 0, c3 = 0, c4 = 0, c5 = 0;
            // ��������-�������������� ������������
		    // (148-154) -------------------------------------------------------
		    Report.zReplaceAll(aBody, "���������", 
		    		Report.legal(dbObject, "data.app1.design_permitting_doc.project_doc.terms"));
		    Report.zReplaceAll(aBody, "���������", 
		    		Report.legal(dbObject, "data.app1.design_permitting_doc.project_doc.contracts"));
		    
		    x = Report.legal(dbObject, "data.app1.design_permitting_doc.project_doc.sum_total");
		    c1 += Report.zint(x);
		    Report.zReplaceAll(aBody, "���������", x);
		    
		    x = Report.legal(dbObject, "data.app1.design_permitting_doc.project_doc.sum_own");
		    c2 += Report.zint(x);
		    Report.zReplaceAll(aBody, "���������", x);
		    
		    x = Report.legal(dbObject, "data.app1.design_permitting_doc.project_doc.sum_creditor");
		    c3 += Report.zint(x);
		    Report.zReplaceAll(aBody, "���������", x);
		    
		    x = Report.legal(dbObject, "data.app1.design_permitting_doc.project_doc.sum_rest_own");
		    c4 += Report.zint(x);
		    Report.zReplaceAll(aBody, "���������", x);
		    
		    x = Report.legal(dbObject, "data.app1.design_permitting_doc.project_doc.sum_rest_creditor");
		    c5 += Report.zint(x);
		    Report.zReplaceAll(aBody, "���������", x);
		    // (155-161) -------------------------------------------------------
		    Report.zReplaceAll(aBody, "�������", 
		    		Report.legal(dbObject, "data.app1.design_permitting_doc.project_exp.terms"));
		    Report.zReplaceAll(aBody, "�������", 
		    		Report.legal(dbObject, "data.app1.design_permitting_doc.project_exp.contracts"));
		    
		    x = Report.legal(dbObject, "data.app1.design_permitting_doc.project_exp.sum_total");
		    c1 += Report.zint(x);
		    Report.zReplaceAll(aBody, "�������", x);
		    
		    x = Report.legal(dbObject, "data.app1.design_permitting_doc.project_exp.sum_own");
		    c2 += Report.zint(x);
		    Report.zReplaceAll(aBody, "�������", x);
		    
		    x = Report.legal(dbObject, "data.app1.design_permitting_doc.project_exp.sum_creditor");
		    c3 += Report.zint(x);
		    Report.zReplaceAll(aBody, "�������", x);
		    
		    x = Report.legal(dbObject, "data.app1.design_permitting_doc.project_exp.sum_rest_own");
		    c4 += Report.zint(x);
		    Report.zReplaceAll(aBody, "�������", x);
		    
		    x = Report.legal(dbObject, "data.app1.design_permitting_doc.project_exp.sum_rest_creditor");
		    c5 += Report.zint(x);
		    Report.zReplaceAll(aBody, "�������", x);
		    // (162-168) -------------------------------------------------------
		    Report.zReplaceAll(aBody, "���������", 
		    		Report.legal(dbObject, "data.app1.design_permitting_doc.project_permitt.terms"));
		    Report.zReplaceAll(aBody, "���������", 
		    		Report.legal(dbObject, "data.app1.design_permitting_doc.project_permitt.contracts"));
		    
		    x = Report.legal(dbObject, "data.app1.design_permitting_doc.project_permitt.sum_total");
		    c1 += Report.zint(x);
		    Report.zReplaceAll(aBody, "���������", x);
		    
		    x = Report.legal(dbObject, "data.app1.design_permitting_doc.project_permitt.sum_own");
		    c2 += Report.zint(x);
		    Report.zReplaceAll(aBody, "���������", x);
		    
		    x = Report.legal(dbObject, "data.app1.design_permitting_doc.project_permitt.sum_creditor");
		    c3 += Report.zint(x);
		    Report.zReplaceAll(aBody, "���������", x);
		    
		    x = Report.legal(dbObject, "data.app1.design_permitting_doc.project_permitt.sum_rest_own");
		    c4 += Report.zint(x);
		    Report.zReplaceAll(aBody, "���������", x);
		    
		    x = Report.legal(dbObject, "data.app1.design_permitting_doc.project_permitt.sum_rest_creditor");
		    c5 += Report.zint(x);
		    Report.zReplaceAll(aBody, "���������", x);
		    
            // ��������������
		    // (169-175) -------------------------------------------------------
		    Report.zReplaceAll(aBody, "���������", 
		    		Report.legal(dbObject, "data.app1.infrastructure.�onclusion_dog.terms"));
		    Report.zReplaceAll(aBody, "���������", 
		    		Report.legal(dbObject, "data.app1.infrastructure.�onclusion_dog.contracts"));
		    
		    x = Report.legal(dbObject, "data.app1.infrastructure.�onclusion_dog.sum_total");
		    c1 += Report.zint(x);
		    Report.zReplaceAll(aBody, "���������", x);
		    
		    x = Report.legal(dbObject, "data.app1.infrastructure.�onclusion_dog.sum_own");
		    c2 += Report.zint(x);
		    Report.zReplaceAll(aBody, "���������", x);
		    
		    x = Report.legal(dbObject, "data.app1.infrastructure.�onclusion_dog.sum_creditor");
		    c3 += Report.zint(x);
		    Report.zReplaceAll(aBody, "���������", x);
		    
		    x = Report.legal(dbObject, "data.app1.infrastructure.�onclusion_dog.sum_rest_own");
		    c4 += Report.zint(x);
		    Report.zReplaceAll(aBody, "���������", x);
		    
		    x = Report.legal(dbObject, "data.app1.infrastructure.�onclusion_dog.sum_rest_creditor");
		    c5 += Report.zint(x);
		    Report.zReplaceAll(aBody, "���������", x);
		    // (176-182) -------------------------------------------------------
		    Report.zReplaceAll(aBody, "��������", 
		    		Report.legal(dbObject, "data.app1.infrastructure.�onclusion_inf.terms"));
		    Report.zReplaceAll(aBody, "��������", 
		    		Report.legal(dbObject, "data.app1.infrastructure.�onclusion_inf.contracts"));
		    
		    x = Report.legal(dbObject, "data.app1.infrastructure.�onclusion_inf.sum_total");
		    c1 += Report.zint(x);
		    Report.zReplaceAll(aBody, "��������", x);
		    
		    x = Report.legal(dbObject, "data.app1.infrastructure.�onclusion_inf.sum_own");
		    c2 += Report.zint(x);
		    Report.zReplaceAll(aBody, "��������", x);
		    
		    x = Report.legal(dbObject, "data.app1.infrastructure.�onclusion_inf.sum_creditor");
		    c3 += Report.zint(x);
		    Report.zReplaceAll(aBody, "��������", x);
		    
		    x = Report.legal(dbObject, "data.app1.infrastructure.�onclusion_inf.sum_rest_own");
		    c4 += Report.zint(x);
		    Report.zReplaceAll(aBody, "��������", x);
		    
		    x = Report.legal(dbObject, "data.app1.infrastructure.�onclusion_inf.sum_rest_creditor");
		    c5 += Report.zint(x);
		    Report.zReplaceAll(aBody, "��������", x);
		    
            // ���
		    // (183-189) -------------------------------------------------------
		    Report.zReplaceAll(aBody, "��������", 
		    		Report.legal(dbObject, "data.app1.cmp.�onclusion_gen.terms"));
		    Report.zReplaceAll(aBody, "��������", 
		    		Report.legal(dbObject, "data.app1.cmp.�onclusion_gen.contracts"));
		    
		    x = Report.legal(dbObject, "data.app1.cmp.�onclusion_gen.sum_total");
		    c1 += Report.zint(x);
		    Report.zReplaceAll(aBody, "��������", x);
		    
		    x = Report.legal(dbObject, "data.app1.cmp.�onclusion_gen.sum_own");
		    c2 += Report.zint(x);
		    Report.zReplaceAll(aBody, "��������", x);
		    
		    x = Report.legal(dbObject, "data.app1.cmp.�onclusion_gen.sum_creditor");
		    c3 += Report.zint(x);
		    Report.zReplaceAll(aBody, "��������", x);
		    
		    x = Report.legal(dbObject, "data.app1.cmp.�onclusion_gen.sum_rest_own");
		    c4 += Report.zint(x);
		    Report.zReplaceAll(aBody, "��������", x);
		    
		    x = Report.legal(dbObject, "data.app1.cmp.�onclusion_gen.sum_rest_creditor");
		    c5 += Report.zint(x);
		    Report.zReplaceAll(aBody, "��������", x);
		    // (190-196) -------------------------------------------------------
		    Report.zReplaceAll(aBody, "��������", 
		    		Report.legal(dbObject, "data.app1.cmp.�onclusion_build.terms"));
		    Report.zReplaceAll(aBody, "��������", 
		    		Report.legal(dbObject, "data.app1.cmp.�onclusion_build.contracts"));
		    
		    x = Report.legal(dbObject, "data.app1.cmp.�onclusion_build.sum_total");
		    c1 += Report.zint(x);
		    Report.zReplaceAll(aBody, "��������", x);
		    
		    x = Report.legal(dbObject, "data.app1.cmp.�onclusion_build.sum_own");
		    c2 += Report.zint(x);
		    Report.zReplaceAll(aBody, "��������", x);
		    
		    x = Report.legal(dbObject, "data.app1.cmp.�onclusion_build.sum_creditor");
		    c3 += Report.zint(x);
		    Report.zReplaceAll(aBody, "��������", x);
		    
		    x = Report.legal(dbObject, "data.app1.cmp.�onclusion_build.sum_rest_own");
		    c4 += Report.zint(x);
		    Report.zReplaceAll(aBody, "��������", x);
		    
		    x = Report.legal(dbObject, "data.app1.cmp.�onclusion_build.sum_rest_creditor");
		    c5 += Report.zint(x);
		    Report.zReplaceAll(aBody, "��������", x);
		    
            // �������� ������������
		    // (197-203) -------------------------------------------------------
		    Report.zReplaceAll(aBody, "��������", 
		    		Report.legal(dbObject, "data.app1.main_equipment.equipment.terms"));
		    Report.zReplaceAll(aBody, "��������", 
		    		Report.legal(dbObject, "data.app1.main_equipment.equipment.contracts"));
		    
		    x = Report.legal(dbObject, "data.app1.main_equipment.equipment.sum_total");
		    c1 += Report.zint(x);
		    Report.zReplaceAll(aBody, "��������", x);
		    
		    x = Report.legal(dbObject, "data.app1.main_equipment.equipment.sum_own");
		    c2 += Report.zint(x);
		    Report.zReplaceAll(aBody, "��������", x);
		    
		    x = Report.legal(dbObject, "data.app1.main_equipment.equipment.sum_creditor");
		    c3 += Report.zint(x);
		    Report.zReplaceAll(aBody, "��������", x);
		    
		    x = Report.legal(dbObject, "data.app1.main_equipment.equipment.sum_rest_own");
		    c4 += Report.zint(x);
		    Report.zReplaceAll(aBody, "��������", x);
		    
		    x = Report.legal(dbObject, "data.app1.main_equipment.equipment.sum_rest_creditor");
		    c5 += Report.zint(x);
		    Report.zReplaceAll(aBody, "��������", x);
		    // (204-210) -------------------------------------------------------
		    Report.zReplaceAll(aBody, "��������", 
		    		Report.legal(dbObject, "data.app1.main_equipment.installation.terms"));
		    Report.zReplaceAll(aBody, "��������", 
		    		Report.legal(dbObject, "data.app1.main_equipment.installation.contracts"));
		    
		    x = Report.legal(dbObject, "data.app1.main_equipment.installation.sum_total");
		    c1 += Report.zint(x);
		    Report.zReplaceAll(aBody, "��������", x);
		    
		    x = Report.legal(dbObject, "data.app1.main_equipment.installation.sum_own");
		    c2 += Report.zint(x);
		    Report.zReplaceAll(aBody, "��������", x);
		    
		    x = Report.legal(dbObject, "data.app1.main_equipment.installation.sum_creditor");
		    c3 += Report.zint(x);
		    Report.zReplaceAll(aBody, "��������", x);
		    
		    x = Report.legal(dbObject, "data.app1.main_equipment.installation.sum_rest_own");
		    c4 += Report.zint(x);
		    Report.zReplaceAll(aBody, "��������", x);
		    
		    x = Report.legal(dbObject, "data.app1.main_equipment.installation.sum_rest_creditor");
		    c5 += Report.zint(x);
		    Report.zReplaceAll(aBody, "��������", x);

		    // ��������������� ������������
		    // (211-217) -------------------------------------------------------
		    Report.zReplaceAll(aBody, "��������", 
		    		Report.legal(dbObject, "data.app1.add_equipment.equipment.terms"));
		    Report.zReplaceAll(aBody, "��������", 
		    		Report.legal(dbObject, "data.app1.add_equipment.equipment.contracts"));
		    
		    x = Report.legal(dbObject, "data.app1.add_equipment.equipment.sum_total");
		    c1 += Report.zint(x);
		    Report.zReplaceAll(aBody, "��������", x);
		    
		    x = Report.legal(dbObject, "data.app1.add_equipment.equipment.sum_own");
		    c2 += Report.zint(x);
		    Report.zReplaceAll(aBody, "��������", x);
		    
		    x = Report.legal(dbObject, "data.app1.add_equipment.equipment.sum_creditor");
		    c3 += Report.zint(x);
		    Report.zReplaceAll(aBody, "��������", x);
		    
		    x = Report.legal(dbObject, "data.app1.add_equipment.equipment.sum_rest_own");
		    c4 += Report.zint(x);
		    Report.zReplaceAll(aBody, "��������", x);
		    
		    x = Report.legal(dbObject, "data.app1.add_equipment.equipment.sum_rest_creditor");
		    c5 += Report.zint(x);
		    Report.zReplaceAll(aBody, "��������", x);
		    // (218-224) -------------------------------------------------------
		    Report.zReplaceAll(aBody, "��������", 
		    		Report.legal(dbObject, "data.app1.add_equipment.installation.terms"));
		    Report.zReplaceAll(aBody, "��������", 
		    		Report.legal(dbObject, "data.app1.add_equipment.installation.contracts"));
		    
		    x = Report.legal(dbObject, "data.app1.add_equipment.installation.sum_total");
		    c1 += Report.zint(x);
		    Report.zReplaceAll(aBody, "��������", x);
		    
		    x = Report.legal(dbObject, "data.app1.add_equipment.installation.sum_own");
		    c2 += Report.zint(x);
		    Report.zReplaceAll(aBody, "��������", x);
		    
		    x = Report.legal(dbObject, "data.app1.add_equipment.installation.sum_creditor");
		    c3 += Report.zint(x);
		    Report.zReplaceAll(aBody, "��������", x);
		    
		    x = Report.legal(dbObject, "data.app1.add_equipment.installation.sum_rest_own");
		    c4 += Report.zint(x);
		    Report.zReplaceAll(aBody, "��������", x);
		    
		    x = Report.legal(dbObject, "data.app1.add_equipment.installation.sum_rest_creditor");
		    c5 += Report.zint(x);
		    Report.zReplaceAll(aBody, "��������", x);
		    
            // ����� � ���������
		    // (225-231) -------------------------------------------------------
		    Report.zReplaceAll(aBody, "��������", 
		    		Report.legal(dbObject, "data.app1.raw_materials.terms"));
		    Report.zReplaceAll(aBody, "��������", 
		    		Report.legal(dbObject, "data.app1.raw_materials.contracts"));
		    
		    x = Report.legal(dbObject, "data.app1.raw_materials.sum_total");
		    c1 += Report.zint(x);
		    Report.zReplaceAll(aBody, "��������", x);
		    
		    x = Report.legal(dbObject, "data.app1.raw_materials.sum_own");
		    c2 += Report.zint(x);
		    Report.zReplaceAll(aBody, "��������", x);
		    
		    x = Report.legal(dbObject, "data.app1.raw_materials.sum_creditor");
		    c3 += Report.zint(x);
		    Report.zReplaceAll(aBody, "��������", x);
		    
		    x = Report.legal(dbObject, "data.app1.raw_materials.sum_rest_own");
		    c4 += Report.zint(x);
		    Report.zReplaceAll(aBody, "��������", x);
		    
		    x = Report.legal(dbObject, "data.app1.raw_materials.sum_rest_creditor");
		    c5 += Report.zint(x);
		    Report.zReplaceAll(aBody, "��������", x);
		    
            // �����
		    Report.zReplaceAll(aBody, "������", Integer.toString(c1));
		    Report.zReplaceAll(aBody, "������", Integer.toString(c2));
		    Report.zReplaceAll(aBody, "������", Integer.toString(c3));
		    Report.zReplaceAll(aBody, "������", Integer.toString(c4));
		    Report.zReplaceAll(aBody, "������", Integer.toString(c5));
		    
            // App3 (148-236) ����������� ������� �������.
		    BasicDBList app3List = data == null? null: (BasicDBList)data.get("app3");

		    if (app3List == null) count = 0; else count = app3List.size();
		    for (int i = 0; i < count; i++) {
		    	x = Report.legal(app3List, i, "year");
		    	if (x.equals("� ����������� ���������, ������ � ���")) {
		    		Report.zReplaceAll(aBody, "������", Report.legal(app3List, i, "app3_market_volume"));
		    		Report.zReplaceAll(aBody, "�����", Report.legal(app3List, i, "app3_product_volume"));
		    		Report.zReplaceAll(aBody, "���������", Report.legal(app3List, i, "app3_potential_volume"));
		    		Report.zReplaceAll(aBody, "�������", Report.legal(app3List, i, "app3_plan_volume"));
		    	}
		    	else if (x.equals("� �������� ���������, ���. � ���")) {
		    		Report.zReplaceAll(aBody, "������", Report.legal(app3List, i, "app3_market_volume"));
		    		Report.zReplaceAll(aBody, "�����", Report.legal(app3List, i, "app3_product_volume"));
		    		Report.zReplaceAll(aBody, "���������", Report.legal(app3List, i, "app3_potential_volume"));
		    		Report.zReplaceAll(aBody, "�������", Report.legal(app3List, i, "app3_plan_volume"));
		    	}
		    	else if (x.equals("��������� ���������� � (���) �������������� ���������")) {
		    		Report.zReplaceAll(aBody, "������", Report.legal(app3List, i, "app3_market_volume"));
		    		Report.zReplaceAll(aBody, "�����", Report.legal(app3List, i, "app3_product_volume"));
		    		Report.zReplaceAll(aBody, "���������", Report.legal(app3List, i, "app3_potential_volume"));
		    		Report.zReplaceAll(aBody, "�������", Report.legal(app3List, i, "app3_plan_volume"));
		    	}
		    }
		    // ��������� ������� app3
    		Report.zReplaceAll(aBody, "������", "");
    		Report.zReplaceAll(aBody, "�����", "");
    		Report.zReplaceAll(aBody, "���������", "");
    		Report.zReplaceAll(aBody, "�������", "");
		    
    		Report.zReplaceAll(aBody, "������", "");
    		Report.zReplaceAll(aBody, "�����", "");
    		Report.zReplaceAll(aBody, "���������", "");
    		Report.zReplaceAll(aBody, "�������", "");
		    
    		Report.zReplaceAll(aBody, "������", "");
    		Report.zReplaceAll(aBody, "�����", "");
    		Report.zReplaceAll(aBody, "���������", "");
    		Report.zReplaceAll(aBody, "�������", "");
    		
            // (������) ����� ������� ����� ��������� - � �������� ���������, ���. � ��� (268)
    		f = Report.zfloat(Report.legal(dbObject, "data.market.market_emkost")) / 1000;
    		Report.zReplaceAll(aBody, "������", Float.toString(f));
            // (������) ����� ������� ����� ��������� - ��������� ���������� � (���) �������������� ��������� (272)
    		Report.zReplaceAll(aBody, "������", Report.legal(dbObject, "data.market.market_info"));
    		
		    //---------------------- �� ��������� enterprise ------------------------------------
		    dbCollection = db.getCollection("enterprise");
		    idQuery = new BasicDBObject(Report.parent_name, aParam);
		    dbObject = dbCollection.findOne(idQuery);

            String enterpriseName = Report.exam(dbObject, "data.enterprise_data.name");
            String enterpriseActivity = Report.exam(dbObject, "data.enterprise_data.activity");
            String enterpriseReg = Report.legal(dbObject, "data.enterprise_data.address_reg");
            enterpriseReg =  enterpriseReg + (enterpriseReg.trim().length() == 0? "": ", ") +
            		Report.legal(dbObject, "data.enterprise_data.date_reg");
            
            // (���������������������) ������ �������� ��� (1)
		    Report.zReplaceAll(aBody, "���������������������", Report.exam(dbObject, "data.enterprise_data.status_msp"));
            // (�����������������������) ��������� ������������� (25)
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
	    		if (Report.legal(ownList, i, "owner.urlico").trim().length() > 0) {	// ������
	    			benif += Report.exam(ownList, i, "owner.urlico.name") + " - " + Integer.toString(und) + "%" + CR;
	    			continue;
	    		}
	    		if (Report.legal(ownList, i, "owner.beneficiar").trim().length() > 0) {	// �������
	    			benif += Report.exam(ownList, i, "owner.beneficiar.fio") + " - " + Integer.toString(und) + "%" + CR;
	    			continue;
	    		}
	    	}
	    	Report.zReplaceAll(aBody, "�����������������������", benif);
            // (����������������) ������� �������� (26)
		    Report.zReplaceAll(aBody, "����������������", Report.exam(dbObject, "data.enterprise_data.history"));
            // (27-28) ������� / ������ ������� �� ��������� ���������� ��� +
            // (30-44) ������������ ���������� ���������� �� ��������� ��� ����
	    	Calendar c = new GregorianCalendar();
	    	String m1Year = String.valueOf(c.get(Calendar.YEAR)-1);
	    	String m2Year = String.valueOf(c.get(Calendar.YEAR)-2);
	    	String m3Year = String.valueOf(c.get(Calendar.YEAR)-3);
	    	BasicDBList dohodList = data == null? null: (BasicDBList)data.get("enterprise_dohod");
	    	if (dohodList == null) count = 0; else count = dohodList.size();
	    	for (int i = 0; i < count; i++) {
	    		x = Report.legal(dohodList, i, "year");
	    		if (x.equals(m3Year)) {
	    		    Report.zReplaceAll(aBody, "����2014", Report.exam(dohodList, i, "proceeds_comp"));
	    		    Report.zReplaceAll(aBody, "���2014", Report.exam(dohodList, i, "proceeds_oper"));
	    		    Report.zReplaceAll(aBody, "����2014", Report.exam(dohodList, i, "proceeds_rent"));
	    		    Report.zReplaceAll(aBody, "���2014", Report.exam(dohodList, i, "proceeds_clear"));
	    		    Report.zReplaceAll(aBody, "����2014", Report.exam(dohodList, i, "proceeds_money"));
	    		}
	    		else if (x.equals(m2Year)) {
	    		    Report.zReplaceAll(aBody, "����2015", Report.exam(dohodList, i, "proceeds_comp"));
	    		    Report.zReplaceAll(aBody, "���2015", Report.exam(dohodList, i, "proceeds_oper"));
	    		    Report.zReplaceAll(aBody, "����2015", Report.exam(dohodList, i, "proceeds_rent"));
	    		    Report.zReplaceAll(aBody, "���2015", Report.exam(dohodList, i, "proceeds_clear"));
	    		    Report.zReplaceAll(aBody, "����2015", Report.exam(dohodList, i, "proceeds_money"));
	    		}
	    		else if (x.equals(m1Year)) {
	    		    Report.zReplaceAll(aBody, "����2016", Report.exam(dohodList, i, "proceeds_comp"));
	    		    Report.zReplaceAll(aBody, "���2016", Report.exam(dohodList, i, "proceeds_oper"));
	    		    Report.zReplaceAll(aBody, "����2016", Report.exam(dohodList, i, "proceeds_rent"));
	    		    Report.zReplaceAll(aBody, "���2016", Report.exam(dohodList, i, "proceeds_clear"));
	    		    Report.zReplaceAll(aBody, "����2016", Report.exam(dohodList, i, "proceeds_money"));
	    		}
	    	}
	    	// ��������� �����.����� � ������� ���������� �����������
		    Report.zReplaceAll(aBody, "����2014", Report.question_marks);
		    Report.zReplaceAll(aBody, "���2014", Report.question_marks);
		    Report.zReplaceAll(aBody, "����2014", Report.question_marks);
		    Report.zReplaceAll(aBody, "���2014", Report.question_marks);
		    Report.zReplaceAll(aBody, "����2014", Report.question_marks);
		    
		    Report.zReplaceAll(aBody, "����2015", Report.question_marks);
		    Report.zReplaceAll(aBody, "���2015", Report.question_marks);
		    Report.zReplaceAll(aBody, "����2015", Report.question_marks);
		    Report.zReplaceAll(aBody, "���2015", Report.question_marks);
		    Report.zReplaceAll(aBody, "����2015", Report.question_marks);
		    
		    Report.zReplaceAll(aBody, "����2016", Report.question_marks);
		    Report.zReplaceAll(aBody, "���2016", Report.question_marks);
		    Report.zReplaceAll(aBody, "����2016", Report.question_marks);
		    Report.zReplaceAll(aBody, "���2016", Report.question_marks);
		    Report.zReplaceAll(aBody, "����2016", Report.question_marks);
		    
            // (���������������������������) ���������� � ������� �������� (29)
		    Report.zReplaceAll(aBody, "���������������������������", Report.exam(dbObject, "data.enterprise_data.cur_credits"));
            // (������������) ������� ����������� ����������� (45)
		    Report.zReplaceAll(aBody, "������������", Report.exam(dbObject, "data.enterprise_data.cur_staff"));
            // (������������������������������) ������� ����������������� ������������ ��������� ������������� � ������ ������ ��� (46)
		    Report.zReplaceAll(aBody, "������������������������������", Report.exam(dbObject, "data.enterprise_data.tax_debt"));
            // (�����������) ����������/ (147)
		    String management = "";
		    BasicDBList mngrList = data == null? null: (BasicDBList)data.get("group_of_people");
		    if (mngrList == null) count = 0; else count = mngrList.size();
		    for (int i = 0; i < count; i++) {
		    	management += Report.legal(mngrList, i, "high_manager") + ", " +
		    			Report.legal(mngrList, i, "position") + ", " +
		    			Report.legal(mngrList, i, "phone") + CR;
		    }
		    Report.zReplaceAll(aBody, "�����������", management);
            // (237 - 263) ���������� 2 - ��������� ������ ���
		    String[][] prsn = new String[9][3];
		    for (int i = 0; i < 9; i++) for (int j = 0; j < 3; j++) prsn[i][j] = "";
	    	BasicDBList groupList = data == null? null: (BasicDBList)data.get("group_of_people");
	    	if (groupList == null) count = 0; else count = Math.min(groupList.size(), 9);
	    	for (int i = 0; i < count; i++) {
	    		prsn[i][0] = Report.legal(groupList, i, "compain_name");
	    		prsn[i][1] = Report.legal(groupList, i, "participant");
	    		prsn[i][2] = Report.legal(groupList, i, "percent");
	    	}
		    Report.zReplaceAll(aBody, "�����1", prsn[0][0]);
		    Report.zReplaceAll(aBody, "�����1", prsn[0][1]);
		    Report.zReplaceAll(aBody, "�����1", prsn[0][2]);
	    	
		    Report.zReplaceAll(aBody, "�����2", prsn[1][0]);
		    Report.zReplaceAll(aBody, "�����2", prsn[1][1]);
		    Report.zReplaceAll(aBody, "�����2", prsn[1][2]);
	    	
		    Report.zReplaceAll(aBody, "�����3", prsn[2][0]);
		    Report.zReplaceAll(aBody, "�����3", prsn[2][1]);
		    Report.zReplaceAll(aBody, "�����3", prsn[2][2]);
	    	
		    Report.zReplaceAll(aBody, "�����4", prsn[3][0]);
		    Report.zReplaceAll(aBody, "�����4", prsn[3][1]);
		    Report.zReplaceAll(aBody, "�����4", prsn[3][2]);
	    	
		    Report.zReplaceAll(aBody, "�����5", prsn[4][0]);
		    Report.zReplaceAll(aBody, "�����5", prsn[4][1]);
		    Report.zReplaceAll(aBody, "�����5", prsn[4][2]);
	    	
		    Report.zReplaceAll(aBody, "�����6", prsn[5][0]);
		    Report.zReplaceAll(aBody, "�����6", prsn[5][1]);
		    Report.zReplaceAll(aBody, "�����6", prsn[5][2]);
	    	
		    Report.zReplaceAll(aBody, "�����7", prsn[6][0]);
		    Report.zReplaceAll(aBody, "�����7", prsn[6][1]);
		    Report.zReplaceAll(aBody, "�����7", prsn[6][2]);
	    	
		    Report.zReplaceAll(aBody, "�����8", prsn[7][0]);
		    Report.zReplaceAll(aBody, "�����8", prsn[7][1]);
		    Report.zReplaceAll(aBody, "�����8", prsn[7][2]);
	    	
		    Report.zReplaceAll(aBody, "�����9", prsn[8][0]);
		    Report.zReplaceAll(aBody, "�����9", prsn[8][1]);
		    Report.zReplaceAll(aBody, "�����9", prsn[8][2]);
	    	
		    //---------------------- �� ��������� project_ent ------------------------------------
		    dbCollection = db.getCollection("project_ent");
		    idQuery = new BasicDBObject(Report.parent_name, aParam);
		    dbObject = dbCollection.findOne(idQuery);

		    if (cred_init) { // (47-56)
			    Report.zReplaceAll(aBody, "���������������", "");				// (47)
			    Report.zReplaceAll(aBody, "���������������", "");				// (48)
			    Report.zReplaceAll(aBody, "�������������������������", "");		// (49)
			    Report.zReplaceAll(aBody, "�������������������������", "");		// (50)
			    Report.zReplaceAll(aBody, "�����������������", "");				// (51)
			    Report.zReplaceAll(aBody, "��������������", "");				// (52)
			    Report.zReplaceAll(aBody, "�����������������������������", "");	// (53)
			    Report.zReplaceAll(aBody, "����������", "");					// (54)
			    Report.zReplaceAll(aBody, "����������", "");					// (55)
		    }
		    else {
                // (���������������) ������������ ��������� ����������� (47)
			    Report.zReplaceAll(aBody, "���������������", Report.legal(dbObject, "data.enterprise_data.name"));
                // (���������������) ���� � ����� ����������� ��������� ����������� (48)
			    x = Report.legal(dbObject, "data.enterprise_data.address_reg");
			    Report.zReplaceAll(aBody, "���������������", 
			    		x + (x.trim().length() == 0? "": ", ") + Report.legal(dbObject, "data.enterprise_data.date_reg"));
                // (�������������������������) ������� ��� ������������ ��������� ����������� (49)
			    Report.zReplaceAll(aBody, "�������������������������", Report.legal(dbObject, "data.enterprise_data.activity"));
                // (�������������������������) ��������� ������������� (50)
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
		    		if (Report.legal(ownList, i, "owner.urlico").trim().length() > 0) {	// ������
		    			benif += Report.exam(ownList, i, "owner.urlico.name") + " - " + Integer.toString(und) + "%" + CR;
		    			continue;
		    		}
		    		if (Report.legal(ownList, i, "owner.beneficiar").trim().length() > 0) {	// �������
		    			benif += Report.exam(ownList, i, "owner.beneficiar.fio") + " - " + Integer.toString(und) + "%" + CR;
		    			continue;
		    		}
		    	}
		    	Report.zReplaceAll(aBody, "�������������������������", benif);
                // (�����������������, ��������������) ������� ������� / ������ ������� (���� ���������) ��� ��������� ����������� (51-52)
                //ddt_3.checkList[51].param = util.exam(doc.data.enterprise_dohod[i].proceeds_comp);
                //ddt_3.checkList[52].param = util.exam(doc.data.enterprise_dohod[i].proceeds_clear);
		    	dohodList = data == null? null: (BasicDBList)data.get("enterprise_dohod");
		    	if (dohodList == null) count = 0; else count = dohodList.size();
		    	for (int i = 0; i < count; i++) {
		    		x = Report.legal(dohodList, i, "year");
		    		if (x.equals("2016")) {
		    			Report.zReplaceAll(aBody, "�����������������", Report.exam(dohodList, i, "proceeds_comp"));		    			
		    			Report.zReplaceAll(aBody, "��������������", Report.exam(dohodList, i, "proceeds_clear"));		    			
		    		}
		    	}
    			Report.zReplaceAll(aBody, "�����������������", Report.question_marks);		    			
    			Report.zReplaceAll(aBody, "��������������", Report.question_marks);		    			
		    	
                // (�������������, �������������, ������������������������) ������� (���������) �������  (22-24)
    			if (proj_init_direct) {   // ������ ��������� ����������
    				Report.zReplaceAll(aBody, "�������������", enterpriseName);	
    				Report.zReplaceAll(aBody, "�������������", enterpriseReg);	
    				Report.zReplaceAll(aBody, "������������������������", enterpriseActivity);	
    			}
    			else if (proj_init_subjectrf) {   // ������� ��
    				Report.zReplaceAll(aBody, "�������������", proj_init_subjectrf_name);	
    				Report.zReplaceAll(aBody, "�������������", enterpriseReg);	
    				Report.zReplaceAll(aBody, "������������������������", "");	
    			}
    			else if (proj_init_unions) {   // ������� ��
    				Report.zReplaceAll(aBody, "�������������", proj_init_unions_name);	
    				Report.zReplaceAll(aBody, "�������������", enterpriseReg);	
    				Report.zReplaceAll(aBody, "������������������������", "");	
    			}
    			else {
    				Report.zReplaceAll(aBody, "�������������", Report.question_marks);	
    				Report.zReplaceAll(aBody, "�������������", Report.question_marks);	
    				Report.zReplaceAll(aBody, "������������������������", Report.question_marks);	
    				
    			}
		    }
    		
		} catch (Exception  ex) {
			// ������
		    return new ArrayList<String>(){{
		    	add("error");
		    	add(ex.toString());
		    }};
		}
		return aBody;
	}
}
