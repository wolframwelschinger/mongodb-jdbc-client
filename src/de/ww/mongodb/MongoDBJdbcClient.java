package de.ww.mongodb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;

/**
 * Simple JDBC-Client for MongoDB
 * @author Wolfram Welschinger
 *
 */
public class MongoDBJdbcClient {
	
	private final String DB_NAME = "wwtest";
	private final String COLLECTION_NAME = "mycol";
	private final String MONGO_DB_HOST = "localhost";
	private final int MONGO_DB_PORT = 27017;
	
	MongoClient mongoClient = null;
	DB db = null;
	DBCollection coll = null;
	
	private Logger logger = (Logger) LogManager.getLogger(this.getClass().getName());;
	
	public MongoDBJdbcClient(){
		super();
		mongoClient = new MongoClient(MONGO_DB_HOST, MONGO_DB_PORT);
		db = mongoClient.getDB(DB_NAME);
		
		if (!db.collectionExists(COLLECTION_NAME)){
			this.coll = db.createCollection(COLLECTION_NAME, null);
			logger.debug("Collection " + COLLECTION_NAME + " wurde angelegt.");
		} else {
			logger.debug("Collection " + COLLECTION_NAME + " existiert bereits.");
			coll = db.getCollection(COLLECTION_NAME);
		}
	}

	/**
	 * Inserts a test document
	 */
	public void insertSingleDocument(){
		BasicDBObject doc = new BasicDBObject("title", "MongoDB").append("description", "database")
				.append("likes", 100).append("url", "http://www.tutorialspoint.com/mongodb/")
				.append("by", "tutorials point");
		coll.insert(doc);
		logger.debug("Document inserted successfully");		
	}
	
	/**
	 * Finds all documents of a specified collection
	 */
	public void findAllDocuments(){
		logger.debug("Find all documents...");
		DBCursor cursor = this.coll.find();
		if (null != cursor){
			int i = 1;
			while (cursor.hasNext()) {
				logger.debug("Document " + i + ":");
				logger.debug(cursor.next());
				i++;
			}				
		} else {
			logger.debug("No documents found!");
		}
	
	}
	
	/**
	 * Finds documents by search pattern 
	 * @see http://www.mkyong.com/mongodb/java-mongodb-query-document/
	 * @param pattern HashMap<String, Object> of criterias
	 */
	public void findDocumentByAndQuery(HashMap pattern){
		
		BasicDBObject andQuery = new BasicDBObject();
		List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
		
		for (Object key : pattern.keySet()){
			logger.debug("Criteria: Key=" + key + ", value=" + pattern.get(key));
			obj.add(new BasicDBObject( (String)key, pattern.get(key) ));
		}
		
		andQuery.put("$and", obj);		
		
		logger.debug("andQuery: " + obj);
		
		DBCursor cursor = db.getCollection("wwtest").find(andQuery); //coll.find(andQuery);
		while (cursor.hasNext()) {
			StringBuilder sb = new StringBuilder();
			DBObject currentObj = cursor.next();
			for (String key : currentObj.keySet()){
				sb.append("    " + key + " = " + currentObj.get(key) + "\n");
			}
			logger.debug("-- document found:\n" + sb.toString());
			sb = null;
		}		
		
	}
	
	/**
	 * Adds an new Document
	 * @param pattern HashMap<String, Object> with keys and values to add to the new document
	 *  @return true, if the document(s) could be added
	 */
	public boolean addNewDocument(HashMap pattern){
		BasicDBObject newDoc = new BasicDBObject();
		for (Object key : pattern.keySet()){
			logger.debug("Criteria: Key=" + key + ", value=" + pattern.get(key));
			newDoc.append( (String)key, pattern.get(key) );
		}
		
		WriteResult result = this.coll.insert(newDoc);
		logger.debug("New document inserted successfully: " 
				+ "wasAcknowledged=" + result.wasAcknowledged()
				+ ", isUpdateOfExisting=" + result.isUpdateOfExisting()
				+ ", getUpsertedId=" + result.getUpsertedId()
				+ ", hashcode=" + result.hashCode()
				);
		return result.wasAcknowledged();
	}
	
	/**
	 * Removes on ore more documents
	 * @param documentToRemove query or document that specifies the document(s) to remove
	 * @return true, if the document(s) could be removed 
	 */
	public boolean removeDocument(BasicDBObject documentToRemove){
		WriteResult result = coll.remove(documentToRemove);
		logger.debug(result.wasAcknowledged()? "Document was removed." : "Document couldn't be removed.");
		return result.wasAcknowledged();
	}
	
	/**
	 * Updates a document
	 * @param documentToUpdate Query that describes the document to be updated
	 * @param update new version of the document
	 * @return true, if the document was updated successfully
	 */
	public boolean updateDocument(BasicDBObject documentToUpdate, BasicDBObject update){
		WriteResult result = coll.update(documentToUpdate, update);
		logger.debug(result.wasAcknowledged()? "Document was updated." : "Document couldn't be updated.");
		return result.wasAcknowledged();
	}
	
	/**
	 * Closes the JDBC-Connection
	 */
	public void closeConnection(){
		this.mongoClient.close();
		logger.debug("Connection closed.");
	}
	
	public static void main(String args[]) {
		try {
			
			System.out.println("=== MongoDB JDBC Client ===");
			
			MongoDBJdbcClient client = new MongoDBJdbcClient();
			
			//Find all documents
			client.findAllDocuments();
			
			//Do a pattern based query
			HashMap<String, Object> pattern = new HashMap<String, Object>();
			pattern.put("likes", Integer.valueOf(100));
			pattern.put("title", "MongoDB");
			pattern.put("by", "tutorials point");
			client.findDocumentByAndQuery(pattern);
			
			//Insert a new document
			HashMap<String, Object> newDoc = new HashMap<String, Object>();
			newDoc.put("title", "Wolfram's JDBC Client");
			newDoc.put("description", "Java application");
			newDoc.put("by", "Wolfram Welschinger");
			newDoc.put("version", "1.1");
			client.addNewDocument(newDoc);
			
			//Find all documents
			client.findAllDocuments();

			//Find remove one and then find all documents
			//client.removeDocument(new BasicDBObject().append("version", "1.0"));
			//client.findAllDocuments();
			
			//Dokument aktualisieren
			client.updateDocument(new BasicDBObject().append("version", "1.0")
					, new BasicDBObject().append("version", "1.0.1")
					);
			client.findAllDocuments();
			
			/*
			//db.getCollection("wwtest").update(queryDBObject, updateDBObject);
			
			*/
			
			client.closeConnection();
			System.out.println("=== MongoDB JDBC Client closed ===");
			
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}

}
