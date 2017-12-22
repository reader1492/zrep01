// Valerii Zinovev, Perm, 5 dec 2017
// ��������� ������ �� ���� ������ (first)
// ������ � �� first
package com.selenit.zrep;

import static com.mongodb.client.model.Filters.eq;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

public class repDB {
	
	// Logging
	private final static Logger logger = Logger.getLogger(repDB.class);

	// ��������� ��� ���� ������
	public static final String first_db_server = "localhost";	// ������
	public static final int first_db_port = 27017;				// ����
	public static final String first_db_name = "first";			// ��
	
	public static final String first_db_collection_person_request = "person_request";
	
	public static final String first_db_id_from_inn = "pers_request_id";
	public static final String first_db_id_from_person_request = "user_createid";
	
	// ������ � MongoDB
	MongoClient mMongo = null;
	MongoDatabase mMongoDatabase = null;
	
	// ����������� ��� �����������
	public repDB(
			String aDBServer,			// ��� �������
			int aDBPort,				// ����� �����
			String aDBName				// ��� ���� ������
			) throws Exception {
		mMongo = new MongoClient( aDBServer , aDBPort );
		mMongoDatabase = mMongo.getDatabase(aDBName);
	}
	
	// ����������� c ������������
	public repDB(
			String aDBServer,			// ��� �������
			int aDBPort,				// ����� �����
			String aDBName,				// ��� ���� ������
			String aUser,				// ������������
			String aPassword			// ������
			) throws Exception {
		MongoCredential credential =  
				MongoCredential.createCredential(aUser, aDBName, aPassword.toCharArray());
		mMongo = new MongoClient(new ServerAddress(aDBServer , aDBPort ), Arrays.asList(credential));
		mMongoDatabase = mMongo.getDatabase(aDBName);
	}
	
	// ����������
	public void close() throws Exception {
		if (mMongo == null) mMongo.close();
	}
	
	// �������� �������� �� _id �� user_createid (pers_request_id � ��������� ����������)
	// ���������� ������� ��������� person_request
	// ��� null, ���� � ��������� person_request ������ _id ���.
	public Document getById (String aId) throws Exception {
        Document res = null;
        try {
        	res = mMongoDatabase.getCollection(first_db_collection_person_request)
        			.find(eq("_id", aId)).first();
			//.find(eq("_id", new ObjectId(aId))).first();
        } catch (Exception e) {
        	res = null;
        }
		return res;
	}
	
	// �������� ��� �� �� first
	// � ������ user_createid
	// ���� ���� �� ���� ���������� - ���������� ������ ������
	public String getInn (
			String aUser_CreateId
			) throws Exception {
		Document request = this.getById(aUser_CreateId);
		if (request == null) return "";		// Failed: id not found
		
		String inn = request.get("data", Document.class).getString("INN");
		return inn;
	}
}
