/* DB anlegen */
use seq_test

/* Collection anlegen */
db.createCollection("counters")

/* Collection initialisieren */
db.counters.insert({_id:"productid",sequence_value:0})

/*Sequence anlegen*/
db.system.js.save(
   {
     _id : "getNextSequenceValue" , 
     value: function getNextSequenceValue(sequenceName){
			var sequenceDocument = db.counters.findAndModify({
						query:{_id: sequenceName },
						update: {$inc:{sequence_value:1}},
						new:true
					});
			return sequenceDocument.sequence_value;
		}
   }
)

/* JS-Function laden!!!!!!*/
db.loadServerScripts()


/*Test der Sequence*/
db.eval( "getNextSequenceValue( 'productid' )" )

/* Insert der Datensaetze */
db.counters.insert({
	"_id": getNextSequenceValue('productid'),
	"product_name": "Apple iPhone",
	"category": "mobiles"})
	
db.counters.insert({
	"_id": getNextSequenceValue('productid'),
	"product_name": "Samsung Galaxy",
	"category": "mobiles"})	
	

/* Select der Datensaetze */
db.counters.find({})
{ "_id" : "productid", "sequence_value" : 2 }
{ "_id" : 1, "product_name" : "Apple iPhone", "category" : "mobiles" }
{ "_id" : 2, "product_name" : "Samsung Galaxy", "category" : "mobiles" }
> 	
	
db.counters.find({_id:1});
{ "_id" : 1, "product_name" : "Apple iPhone", "category" : "mobiles" }
> db.counters.find({_id:2});
{ "_id" : 2, "product_name" : "Samsung Galaxy", "category" : "mobiles" }
>

/* Like Suche*/
db.counters.find({product_name: /Sa*/});
{ "_id" : 2, "product_name" : "Samsung Galaxy", "category" : "mobiles" }
>





db.users.insert({name: 'paulo'})
db.users.insert({name: 'patric'})
db.users.insert({name: 'pedro'})

db.users.find({name: /a/})  //like '%a%'

out: paulo, patric

db.users.find({name: /^pa/}) //like 'pa%' 

out: paulo, patric

db.users.find({name: /ro$/}) //like '%ro'

out: pedro



/*  Text Search,
 *  https://docs.mongodb.org/manual/tutorial/text-search-in-aggregation/ 
 *  https://docs.mongodb.org/manual/reference/operator/query/text/
 *  https://metabroadcast.com/blog/mongodb-full-text-search  
 */
/* DB mit folgendem Parameter-Set starten:*/

#!/bin/bash
# Startet MongoDB
# WW

bin/mongod --httpinterface --rest --setParameter textSearchEnabled=true
	
/* Index anlegen */
db.txt.createIndex( { txt: "text" } )	

/* Text finden */
> db.txt.find({ $text: { $search: "Linux" }})
{ "_id" : ObjectId("56b43f1e976713224c5ba066"), "txt" : "I am a Linux-Fan!" }
> 

/* Index / Indizes abfragen */
> db.txt.getIndices()
[
	{
		"v" : 1,
		"key" : {
			"_id" : 1
		},
		"name" : "_id_",
		"ns" : "dbtext.txt"
	},
	{
		"v" : 1,
		"key" : {
			"_fts" : "text",
			"_ftsx" : 1
		},
		"name" : "txt_text",
		"ns" : "dbtext.txt",
		"weights" : {
			"txt" : 1
		},
		"default_language" : "english",
		"language_override" : "language",
		"textIndexVersion" : 3
	}
]
> 