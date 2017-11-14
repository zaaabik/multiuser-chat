package org.suai.zabik.chat.database;


import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import org.bson.Document;
import org.suai.zabik.chat.Server;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Mongo implements Database {
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
    private static final String HOST = "localhost";
    private static final String GRADES_COLLECTION = "grades";
    public Mongo() {
        try (MongoClient mongoClient = new MongoClient(HOST, 27017)) {
            MongoDatabase db = mongoClient.getDatabase("test");
            MongoCollection<Document> collection = db.getCollection(GRADES_COLLECTION);

            collection.createIndex(new BasicDBObject("name", 1), new IndexOptions().unique(true));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            LOGGER.log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public String getAllGrades(String name) {
        List<String> result = new LinkedList<>();
        try (MongoClient mongoClient = new MongoClient(HOST, 27017)) {
            MongoDatabase db = mongoClient.getDatabase("test");
            MongoCollection<Document> collection = db.getCollection(GRADES_COLLECTION);

            FindIterable<Document> find = collection.find(new Document("name", name));
            MongoCursor<Document> cursor = find.iterator();

            while (cursor.hasNext()) {
                Document curDocument = cursor.next();
                Set<String> keys = curDocument.keySet();
                keys.stream().forEach(fieldName -> {
                    if (!fieldName.equals("name") && !fieldName.equals("_id")) {
                        result.add(fieldName + " " + curDocument.get(fieldName).toString());
                    }
                });
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            LOGGER.log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
        }
        return String.join(",", result);
    }

    @Override
    public String getAllStudents() {
        List<String> allStudents = new LinkedList<>();
        try (MongoClient mongoClient = new MongoClient(HOST, 27017)) {
            MongoDatabase db = mongoClient.getDatabase("test");
            MongoCollection<Document> collection = db.getCollection(GRADES_COLLECTION);

            FindIterable<Document> find = collection.find();
            MongoCursor<Document> cursor = find.iterator();

            while (cursor.hasNext()) {
                Document curDocument = cursor.next();
                allStudents.add(curDocument.get("name").toString());
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            LOGGER.log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
        }
        return String.join(",", allStudents);
    }

    @Override
    public void addStudent(String studentName) {
        try (MongoClient mongoClient = new MongoClient(HOST, 27017)) {
            MongoDatabase db = mongoClient.getDatabase("test");
            MongoCollection<Document> collection = db.getCollection(GRADES_COLLECTION);

            BasicDBObject document = new BasicDBObject();
            document.put("name", studentName);

            collection.insertOne(new Document(document));

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            LOGGER.log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public void setExamGrade(String userName, String examName, int grade) {
        try (MongoClient mongoClient = new MongoClient(HOST, 27017)) {
            MongoDatabase db = mongoClient.getDatabase("test");
            MongoCollection<Document> collection = db.getCollection(GRADES_COLLECTION);

            BasicDBObject searchQuery = new BasicDBObject();
            searchQuery.put("name", userName);

            BasicDBObject doc = new BasicDBObject();
            doc.append(examName, grade);

            collection.updateOne(searchQuery, new BasicDBObject().append("$set", doc));

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            LOGGER.log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
        }

    }
}
