// Valerii Zinovev, Perm, 5 dec 2017
// Получение данных из базы данных (first)
// Работа с БД first
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

	// Константы для базы данных
	public static final String first_db_server = "localhost";	// Сервер
	public static final int first_db_port = 27017;				// Порт
	public static final String first_db_name = "first";			// БД
	
	public static final String first_db_collection_person_request = "person_request";
	
	public static final String first_db_id_from_inn = "pers_request_id";
	public static final String first_db_id_from_person_request = "user_createid";
	
	// Работа с MongoDB
	MongoClient mMongo = null;
	MongoDatabase mMongoDatabase = null;
	
	// Конструктор без авторизации
	public repDB(
			String aDBServer,			// Имя сервера
			int aDBPort,				// Номер порта
			String aDBName				// Имя базы данных
			) throws Exception {
		mMongo = new MongoClient( aDBServer , aDBPort );
		mMongoDatabase = mMongo.getDatabase(aDBName);
	}
	
	// Конструктор c авторизацией
	public repDB(
			String aDBServer,			// Имя сервера
			int aDBPort,				// Номер порта
			String aDBName,				// Имя базы данных
			String aUser,				// Пользователь
			String aPassword			// Пароль
			) throws Exception {
		MongoCredential credential =  
				MongoCredential.createCredential(aUser, aDBName, aPassword.toCharArray());
		mMongo = new MongoClient(new ServerAddress(aDBServer , aDBPort ), Arrays.asList(credential));
		mMongoDatabase = mMongo.getDatabase(aDBName);
	}
	
	// Деструктор
	public void close() throws Exception {
		if (mMongo == null) mMongo.close();
	}
	
	// Получить документ по _id из user_createid (pers_request_id в остальных коллекциях)
	// Возвращает элемент коллекции person_request
	// Или null, если в коллекции person_request такого _id нет.
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
	
	// Получить ИНН из БД first
	// С ключом user_createid
	// Если ключ не дает результата - возвращает пустую строку
	public String getInn (
			String aUser_CreateId
			) throws Exception {
		Document request = this.getById(aUser_CreateId);
		if (request == null) return "";		// Failed: id not found
		
		String inn = request.get("data", Document.class).getString("INN");
		return inn;
	}
}
