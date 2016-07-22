package de.ww.mongodb;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;

import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.QueryBuilder;

import de.ww.mongodb.text.AnalyzedDBObject;
import de.ww.mongodb.text.AnalyzedDBObject.Condition;


public class MongoDBJDBCTextSearch {

	public static void main(String args[]) {
		try {
			// To connect to mongodb server
			MongoClient mongoClient = new MongoClient("localhost", 27017); // Now
																			// connect
																			// to
																			// your
																			// databases
			DB db = mongoClient.getDB("dbtext");
			System.out.println("Connect to database successfully");
			// boolean auth = db.authenticate(myUserName, myPassword);
			// System.out.println("Authentication: " + auth);

			DBCollection coll = db.getCollection("txt");
			
			System.out.println("Abfrage...");
			
			System.out.println("Count: " + coll.count());
			
			System.out.println(coll == null ? "Null!" : "Nicht null!");
			
			DBCursor cursor = coll.find();      //coll.find();
			int i = 1;
			while (cursor.hasNext()) {
				System.out.println("Inserted Document: " + i);
				System.out.println(cursor.next());
				i++;
			}

			//http://www.jayway.com/2010/11/14/full-text-search-with-mongodb-and-lucene-analyzers/!!!!!!!!!!!!!
			
			//Funzt nicht... :-(
			//db.getCollection("txt").ensureIndex(new BasicDBObject("txt", new Integer(1)));
			
			String text = "I would like to use MongoDB for FULL text search";
			Analyzer analyzer = new org.apache.lucene.analysis.KeywordAnalyzer();
			DBObject object = new AnalyzedDBObject(analyzer)
					   .appendAndAnalyzeFullText("indexed_text", text)
					   .append("text", text);
			db.getCollection("txt").save(object);


			
			//http://stackoverflow.com/questions/15879109/how-to-execute-full-text-search-command-in-mongodb-with-java-driver
			// --> http://www.allanbank.com/mongodb-async-driver/ --> http://www.allanbank.com/mongodb-async-driver/download.html
			//mongodb java text search
//			String collectionName = "txt"; String textToSearchFor = "I am a Linux-Fan!";
//			final DBObject textSearchCommand = new BasicDBObject();
//		    textSearchCommand.put("$text", collectionName);
//		    textSearchCommand.put("$search", textToSearchFor);
//		    textSearchCommand.put("$laguage", "german");
//		    textSearchCommand.put("$caseSensitive", Boolean.TRUE);
//		    textSearchCommand.put("$diacriticSensitive", Boolean.TRUE);
//		    final CommandResult commandResult1 = db.command(textSearchCommand);
//			
//		    for (String key :commandResult1.keySet()){
//		    	Object dbO = commandResult1.get(key);
//		    	System.out.println("********  " + dbO);
//		    }
		    
		    
//			DBCursor cursor2 = db.getCollection("txt").find(textSearchCommand); //coll.find(andQuery);
//			while (cursor2.hasNext()) {
//				StringBuilder sb = new StringBuilder();
//				DBObject currentObj = cursor2.next();
//				for (String key : currentObj.keySet()){
//					sb.append("    " + key + " = " + currentObj.get(key) + "\n");
//				}
//				System.out.println("2. >>>>>>>>> WW Query:\n" + sb.toString());
//				sb = null;
//			}
		    
		    
		    
		    System.out.println("\nTextsearch...");
		    ///http://stackoverflow.com/questions/29149719/mongodb-text-index-using-java-driver
		    DBObject q = QueryBuilder.start().text("MacBook").get();
		    System.out.println(q);
		    
		    final CommandResult commandResult = db.command(q);
			
		    for (String key :commandResult.keySet()){
		    	Object dbO = commandResult.get(key);
		    	System.out.println(">>>> " + dbO.toString());
		    }		    
		    
		    System.out.println("Textsearch Ende");
		
		    
		    
		    
			//QueryBuilder
			//https://metabroadcast.com/blog/mongodb-full-text-search
			
//			DBObject command = BasicDBObjectBuilder
//                    .start("text", "txt")
//                    .append("search", query.getTerm())
//                    .get();
//			
//			CommandResult result =  db.command(command);
//			BasicDBList results = (BasicDBList) result.get("results");
//			        
//			for(Object o : results) {
//			  DBObject dbo = (DBObject) ((DBObject) o).get("obj");
//			  String id = (String) dbo.get("_ID");
//			            
//			  System.out.println(id);
//			}
//			
			
			
			//Funzt nicht:
//			DBObject textQuery = new BasicDBObject("$search", "Linux"); 
//			DBCursor textCursor = coll.find("{ $text: { $search: \"Linux\" }}");
//			i = 1;
//			while (textCursor.hasNext()) {
//				System.out.println("Inserted Document: " + i);
//				System.out.println(cursor.next());
//				i++;
//			}
			
			System.out.println("Ende.");
			
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}

}
