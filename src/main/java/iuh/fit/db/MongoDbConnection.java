package iuh.fit.db;


import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoDbConnection {
    private static MongoDbConnection instance;
    private final MongoClient mongoClient;
    private final MongoDatabase database;

    private MongoDbConnection() {
        // 1. Cấu hình CodecRegistry để MongoDB driver hiểu được các Java POJO (Entity)
        CodecRegistry pojoCodecRegistry = fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build())
        );

        // 2. Thiết lập MongoClientSettings kết hợp URI và CodecRegistry
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new com.mongodb.ConnectionString("mongodb://localhost:27017"))
                .codecRegistry(pojoCodecRegistry)
                .build();

        // 3. Khởi tạo Client và Database
        this.mongoClient = MongoClients.create(settings);
        this.database = mongoClient.getDatabase("QuizAppDB");
    }

    public static synchronized MongoDbConnection getInstance() {
        if (instance == null) {
            instance = new MongoDbConnection();
        }
        return instance;
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}
