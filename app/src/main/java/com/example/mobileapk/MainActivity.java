package com.example.mobileapk;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;
import io.realm.mongodb.sync.SyncConfiguration;


public class MainActivity extends AppCompatActivity {

    String Appid = "application-0-aectw";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Realm.init(this);
        App app = new App(new AppConfiguration.Builder(Appid).build());


        Credentials apiKeyCredentials = Credentials.apiKey("8Kx8G1jaatqQylngnJCCVooBTRcxVWixCRbfw0iolg6RvXT6ARY7qgnFbwFF0ipG");
        AtomicReference<User> user = new AtomicReference<User>();
        app.loginAsync(apiKeyCredentials, it -> {
            if (it.isSuccess()) {
                System.out.println("AUTH " + "Successfully authenticated using an API Key.");
                user.set(app.currentUser());
            } else {
                System.out.println("AUTH" + it.getError().toString());
            }
        });

        User user2 = app.currentUser();
        MongoClient mongoClient = user2.getMongoClient("mongodb-atlas");
        MongoDatabase mongoDatabase = mongoClient.getDatabase("messenger");


        CodecRegistry pojoCodecRegistry = fromRegistries(AppConfiguration.DEFAULT_BSON_CODEC_REGISTRY, fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        MongoCollection<Plant> mongoCollection = mongoDatabase.getCollection("plants", Plant.class).withCodecRegistry(pojoCodecRegistry);
        System.out.println("EXAMPLE" + "Successfully instantiated the MongoDB collection handle");

        //stworzenie obiektu do dodania
        Plant plant = new Plant(
                new ObjectId(),
                "lily of the valley",
                "full",
                "white",
                "perennial",
                "Store 47");

        //dodanie do bazy
        mongoCollection.insertOne(plant).getAsync(task -> {
            if (task.isSuccess()) {
                System.out.println("EXAMPLE" + "successfully inserted a document with id: " + task.get().getInsertedId());
            } else {
                System.out.println("EXAMPLE" + "failed to insert documents with: " + task.getError().getErrorMessage());
            }
        });

//wylogowanie
//        user.get().logOutAsync( result -> {
//            if (result.isSuccess()) {
//                System.out.println("AUTH " + "Successfully logged out.");
//            } else {
//                System.out.println("AUTH "+ result.getError().toString());
//            }
//        });

    }
}

//key realm
//application-0-aectw

//haslo admina
//hR8VzD0vRWmS7EWX