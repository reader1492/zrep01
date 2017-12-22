// Valerii Zinovev, Perm, 04 dec 2017
// ��������� ����� (����� �� ���� �������)
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
			List<String> aBody1,		// ������ ����� �������
			List<String> aBody2,		// ������ ����� �������
			String[] aIDs				// pers_request_id
			) throws Exception {
		
		String x, y;
		int count;
		Float f;
		try {
			// ��������� ����
		    MongoClient mongo = new MongoClient(Report.rep_bd_server , Report.rep_bd_port);
		    DB db = mongo.getDB(Report.rep_bd_name);
		    
		    for (int iID = 0; iID < aIDs.length; iID++) {
		    	String suffix = String.format("%02d", iID + 1);
		    	
			    //---------------- �� ���������  person_request -------------------
			    DBCollection dbCollection = db.getCollection("person_request");
			    BasicDBObject idQuery = new BasicDBObject(Report.parent_name, aIDs[iID]);
			    DBObject dbObject = dbCollection.findOne(idQuery);
			    
	            // (6) ���� �������
			    //Report.zReplaceAll(aBody1, "����" + suffix, Report.exam(dbObject, "data.goal"));
			    // (4+) ���������� � �������, ������� �������
			    String okved = Report.legal(dbObject, "data.OKVED_project");
			    
		    	//---------------- �� ��������� enterprise ------------------------
		    	dbCollection = db.getCollection("enterprise");
		    	idQuery = new BasicDBObject(Report.parent_name, aIDs[iID]);	// ������
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
		    		Report.zReplaceAll(aBody1, "���" + suffix, "-");
		    	}
		    	else {	
				    for (int i = 0; i < count; i++) {
				    	Report.zReplaceAll(aBody1, "���"+ suffix, 
					    	Report.legal(managerList, i, "fio") + "," +
					    	Report.legal(managerList, i, "email") + "," +
				    		Report.legal(managerList, i, "phone") + ";");
				    	break;
				    }
		    	}
		    
		    	// ----------------------- �� ��������� project --------------------------------------
		    	dbCollection = db.getCollection("project");
		    	idQuery = new BasicDBObject(Report.parent_name, aIDs[iID]);		// ������
		    	dbObject = dbCollection.findOne(idQuery);
		    
		    	// (0) ������ -������� ��
		    	Report.zReplaceAll(aBody1, "����������" + suffix, Report.exam(dbObject, "data.credit.region_rf"));
		    	// (1-3) �������� ������
		    	if (Report.legal(dbObject, "data.credit.who_init.direct").trim().length() > 0) { // ������ ��������� ����������
		    		Report.zReplaceAll(aBody1, "������� ��" + suffix, "������ ��������� ����������");
		    		Report.zReplaceAll(aBody1, "��������" + suffix, enterpriseName);
		    		Report.zReplaceAll(aBody2, "11111111" + suffix, enterpriseINN);
		    	}
		    	else if (Report.legal(dbObject, "data.credit.who_init.subjectrf").trim().length() > 0) {	// ������� ��
		    		Report.zReplaceAll(aBody1, "������� ��" + suffix, "������� ��");
		    		Report.zReplaceAll(aBody1, "��������" + suffix, Report.exam(dbObject, "data.credit.who_init.subjectrf.name"));
		    		Report.zReplaceAll(aBody2, "11111111" + suffix, Report.exam(dbObject, "data.credit.who_init.subjectrf.inn"));
		    	}
		    	else if (Report.legal(dbObject, "data.credit.who_init.unions").trim().length() > 0) {	// �����������
		    		Report.zReplaceAll(aBody1, "������� ��" + suffix, "�����������");
		    		Report.zReplaceAll(aBody1, "��������" + suffix, Report.exam(dbObject, "data.credit.who_init.unions.name"));
		    		Report.zReplaceAll(aBody2, "11111111" + suffix, Report.exam(dbObject, "data.credit.who_init.unions.inn"));
		    	}
		    	else {
		    		Report.zReplaceAll(aBody1, "������� ��" + suffix, Report.question_marks);
		    		Report.zReplaceAll(aBody1, "��������" + suffix, Report.question_marks);
		    		Report.zReplaceAll(aBody2, "11111111" + suffix, "0");
		    	}
		    	// (4)���������� � �������, ������� �������
		    	Report.zReplaceAll(aBody1, "������" + suffix, Report.exam(dbObject, "data.info.project_goal") + "," + okved);
	            // (6) ���� �������
			    Report.zReplaceAll(aBody1, "����" + suffix, Report.exam(dbObject, "data.credit.credit_goal"));
		    	// (7) ����������� ����� ������� ���. ���.
		    	x = Report.legal(dbObject, "data.sum.sum_total");
		    	String sum_total = x.trim().length() == 0? "0": Integer.toString((int)(Float.parseFloat(x) / 1000));
		    	Report.zReplaceAll(aBody2, "777" + suffix, sum_total);
		    	// (8) ����������� ����� �������, ���. ���.
		    	x = Report.legal(dbObject, "data.credit.sum");
		    	String credit_sum = x.trim().length() == 0? "0": Integer.toString((int)(Float.parseFloat(x) / 1000));
		    	Report.zReplaceAll(aBody2, "888" + suffix, credit_sum);
		    	// (9) ����������� ����� ��������  ���. ���.
		    	x = Report.legal(dbObject, "data.credit.garant_sum");
		    	String garant_sum = x.trim().length() == 0? "0": Integer.toString((int)(Float.parseFloat(x) / 1000));
		    	Report.zReplaceAll(aBody2, "999" + suffix, garant_sum);
		    	// (10) ������� ������ ���������� �������
		    	Report.zReplaceAll(aBody1, "������" + suffix, Report.exam(dbObject, "data.info.project_status"));
		    	// (11) ������������ ������������ ����� (� ������ ������������ ������ � �����)
		    	Report.zReplaceAll(aBody1, "����" + suffix, Report.exam(dbObject, "data.credit.bank_name"));
		    	// (12) �������� ��������� � ����������� ����� (���, ���, ��. �����)
		    	Report.zReplaceAll(aBody1, "�������" + suffix, Report.exam(dbObject, "data.credit.contact_bank"));
		    	// (13) ������ ������������ ��������� ������ � ����� (� ������ ������������ ������ � �����)
		    	Report.zReplaceAll(aBody1, "������" + suffix, Report.exam(dbObject, "data.credit.status_bank"));
		    	// (14) ��������� ��� ��������� �� ���������� (������� � ������)
		    	Report.zReplaceAll(aBody1, "���������" + suffix, Report.exam(dbObject, "data.info.project_corp_support"));
		    }
		    for (int i = aIDs.length; i <= 50; i++) {
		    	String suffix = String.format("%02d", i);
		    	Report.zReplaceAll(aBody1, "����������" + suffix, " ");	// 0
	    		Report.zReplaceAll(aBody1, "������� ��" + suffix, " ");				// 1
	    		Report.zReplaceAll(aBody1, "��������" + suffix, " ");				// 2
	    		Report.zReplaceAll(aBody2, "11111111" + suffix, " ");				// 3
		    	Report.zReplaceAll(aBody1, "������" + suffix, " ");					// 4
	    		Report.zReplaceAll(aBody1, "���" + suffix, " ");					// 5
		    	Report.zReplaceAll(aBody1, "����" + suffix, " ");					// 6
		    	Report.zReplaceAll(aBody2, "777" + suffix, " ");					// 7
		    	Report.zReplaceAll(aBody2, "888" + suffix, " ");					// 8
		    	Report.zReplaceAll(aBody2, "999" + suffix, " ");					// 9
		    	Report.zReplaceAll(aBody1, "������" + suffix, " ");					// 10
		    	Report.zReplaceAll(aBody1, "����" + suffix, " ");					// 11
		    	Report.zReplaceAll(aBody1, "�������" + suffix, " ");				// 12
		    	Report.zReplaceAll(aBody1, "������" + suffix, " ");					// 13
		    	Report.zReplaceAll(aBody1, "���������" + suffix, " ");	// 14
		    }
		} catch (Exception  ex) {
			// ������
			return ex.toString();
		}
		return "";
	}
}
