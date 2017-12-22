// Valerii Zinovev, Perm, 13 aug 2017
// ��������� ����� (������ �������)
package com.selenit.zrep;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class Rep4 {
	public static String build(List<String> aBody1, List<String> aBody2, String aParam) {
		String x, y;
		int count;
		Float f;
		try {
			// ��������� ����
		    MongoClient mongo = new MongoClient(Report.rep_bd_server , Report.rep_bd_port);
		    DB db = mongo.getDB(Report.rep_bd_name);
		    
		    //---------------- �� ���������  person_request -------------------
		    DBCollection dbCollection = db.getCollection("person_request");
		    BasicDBObject idQuery = new BasicDBObject(Report.parent_name, aParam);
		    DBObject dbObject = dbCollection.findOne(idQuery);
		    
            // (6) ���� �������
		    //Report.zReplaceAll(aBody1, "����", Report.exam(dbObject, "data.goal"));
		    // (4+) ���������� � �������, ������� �������
		    String okved = Report.legal(dbObject, "data.OKVED_project");
		    
		    //---------------- �� ��������� enterprise ------------------------
		    dbCollection = db.getCollection("enterprise");
		    idQuery = new BasicDBObject(Report.parent_name, aParam);
		    dbObject = dbCollection.findOne(idQuery);
		    
		    // �������� �� �������
		    String enterpriseName = Report.exam(dbObject, "data.enterprise_data.name");
		    x = Report.legal(dbObject, "data.enterprise_data.inn");
		    String enterpriseINN = x.trim().length() == 0? "0": x;
		    
            // (5) �������� �������������� ��������/��������� ������� (���, ���, ��. �����)
		    DBObject data = dbObject == null? null: (DBObject)dbObject.get("data");
		    BasicDBList managerList = data == null? null: (BasicDBList)data.get("enterprise_contacts");
		    if (managerList == null) count = 0; else count = managerList.size();
	    	if (count == 0) {
	    		Report.zReplaceAll(aBody1, "���", "-");
	    	}
	    	else {	
	    		for (int i = 0; i < count; i++) {
	    			Report.zReplaceAll(aBody1, "���", 
				    		//Report.zReplaceAll(aBody1, "������� �������� �����  + 7 903 358 55 45 shatikova@yahting.biz", 
	    					Report.legal(managerList, i, "fio") + "," +
	    					Report.legal(managerList, i, "email") + "," +
	    					Report.legal(managerList, i, "phone") + ";");
	    			break;
	    		}
	    	}
		    
		    // ----------------------- �� ��������� project --------------------------------------
		    dbCollection = db.getCollection("project");
		    idQuery = new BasicDBObject(Report.parent_name, aParam);
		    dbObject = dbCollection.findOne(idQuery);
		    
            // (0) ������ -������� ��
		    Report.zReplaceAll(aBody1, "��������� ����������", Report.exam(dbObject, "data.credit.region_rf"));
            // (1-3) �������� ������
		    if (Report.legal(dbObject, "data.credit.who_init.direct").trim().length() > 0) { // ������ ��������� ����������
		    	Report.zReplaceAll(aBody1, "������� ��", "������ ��������� ����������");
		    	Report.zReplaceAll(aBody1, "��������", enterpriseName);
		    	Report.zReplaceAll(aBody2, "1111111111", enterpriseINN);
		    }
		    else if (Report.legal(dbObject, "data.credit.who_init.subjectrf").trim().length() > 0) {	// ������� ��
		    	Report.zReplaceAll(aBody1, "������� ��", "������� ��");
		    	Report.zReplaceAll(aBody1, "��������", Report.exam(dbObject, "data.credit.who_init.subjectrf.name"));
		    	Report.zReplaceAll(aBody2, "1111111111", Report.exam(dbObject, "data.credit.who_init.subjectrf.inn"));
		    }
		    else if (Report.legal(dbObject, "data.credit.who_init.unions").trim().length() > 0) {	// �����������
		    	Report.zReplaceAll(aBody1, "������� ��", "�����������");
		    	Report.zReplaceAll(aBody1, "��������", Report.exam(dbObject, "data.credit.who_init.unions.name"));
		    	Report.zReplaceAll(aBody2, "1111111111", Report.exam(dbObject, "data.credit.who_init.unions.inn"));
		    }
		    else {
		    	Report.zReplaceAll(aBody1, "������� ��", Report.question_marks);
		    	Report.zReplaceAll(aBody1, "��������", Report.question_marks);
		    	Report.zReplaceAll(aBody2, "1111111111", "0");
		    }
            // (4++) ���������� � �������, ������� �������
		    Report.zReplaceAll(aBody1, "������", Report.exam(dbObject, "data.info.project_goal") + "," + okved);
            // (6) ���� �������
		    Report.zReplaceAll(aBody1, "����", Report.exam(dbObject, "data.credit.credit_goal"));
            // (7) ����������� ����� ������� ���. ���.
		    x = Report.legal(dbObject, "data.sum.sum_total");
		    String sum_total = x.trim().length() == 0? "0": Integer.toString((int)(Float.parseFloat(x) / 1000));
		    Report.zReplaceAll(aBody2, "777", sum_total);
            // (8) ����������� ����� �������, ���. ���.
		    x = Report.legal(dbObject, "data.credit.sum");
		    String credit_sum = x.trim().length() == 0? "0": Integer.toString((int)(Float.parseFloat(x) / 1000));
		    Report.zReplaceAll(aBody2, "888", credit_sum);
            // (9) ����������� ����� ��������  ���. ���.
		    x = Report.legal(dbObject, "data.credit.garant_sum");
		    String garant_sum = x.trim().length() == 0? "0": Integer.toString((int)(Float.parseFloat(x) / 1000));
		    Report.zReplaceAll(aBody2, "999", garant_sum);
            // (10) ������� ������ ���������� �������
		    Report.zReplaceAll(aBody1, "������", Report.exam(dbObject, "data.info.project_status"));
            // (11) ������������ ������������ ����� (� ������ ������������ ������ � �����)
		    Report.zReplaceAll(aBody1, "����", Report.exam(dbObject, "data.credit.bank_name"));
            // (12) �������� ��������� � ����������� ����� (���, ���, ��. �����)
		    Report.zReplaceAll(aBody1, "�������", Report.exam(dbObject, "data.credit.contact_bank"));
            // (13) ������ ������������ ��������� ������ � ����� (� ������ ������������ ������ � �����)
		    Report.zReplaceAll(aBody1, "������", Report.exam(dbObject, "data.credit.status_bank"));
            // (14) ��������� ��� ��������� �� ���������� (������� � ������)
		    Report.zReplaceAll(aBody1, "����������� ���������", Report.exam(dbObject, "data.info.project_corp_support"));

		
		} catch (Exception  ex) {
			// ������
			return ex.toString();
		}
		return "";
	}
}
