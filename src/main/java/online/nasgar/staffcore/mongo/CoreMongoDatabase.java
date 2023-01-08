package online.nasgar.staffcore.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Data;
import lombok.Getter;
import online.nasgar.staffcore.StaffCore;
import online.nasgar.staffcore.utils.config.ConfigFile;
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

    ConfigFile configFile = StaffCore.getInstance().getConfigFile();
    String ip = configFile.getString("MONGO.IP");
    int port = configFile.getInteger("MONGO.PORT");
    String user = configFile.getString("MONGO.USER");
    String password = configFile.getString("MONGO.PASSWORD");
    String database = configFile.getString("MONGO.DATABASE");

    public CoreMongoDatabase() {
        instance = this;

        this.mongoClient = new MongoClient(new ServerAddress(ip, port),
                MongoCredential.createCredential(
                        user, database, password.toCharArray()),
                MongoClientOptions.builder().build());
        this.mongoDatabase = this.mongoClient.getDatabase(database);

        this.grants = this.mongoDatabase.getCollection("grants");
        this.ranks = this.mongoDatabase.getCollection("ranks");
        this.tags = this.mongoDatabase.getCollection("tags");
        this.profiles = this.mongoDatabase.getCollection("profiles");
        this.punishments = this.mongoDatabase.getCollection("punishments");
    }
}