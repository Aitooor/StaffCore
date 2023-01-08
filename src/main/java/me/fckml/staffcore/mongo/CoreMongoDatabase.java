package me.fckml.staffcore.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Data;
import lombok.Getter;
import org.bson.Document;

@Data
public class CoreMongoDatabase {

    @Getter
    private static CoreMongoDatabase instance;

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;

    private MongoCollection<Document> grants;
    private MongoCollection<Document> ranks;
    private MongoCollection<Document> tags;
    private MongoCollection<Document> profiles;
    private MongoCollection<Document> punishments;

    public CoreMongoDatabase() {
        instance = this;

        this.mongoClient = new MongoClient(new ServerAddress("127.0.0.1", 27017));
        this.mongoDatabase = this.mongoClient.getDatabase("StaffCore");

        this.grants = this.mongoDatabase.getCollection("grants");
        this.ranks = this.mongoDatabase.getCollection("ranks");
        this.tags = this.mongoDatabase.getCollection("tags");
        this.profiles = this.mongoDatabase.getCollection("profiles");
        this.punishments = this.mongoDatabase.getCollection("punishments");
    }
}