package com.example.mobileapk;

import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import androidx.appcompat.app.AppCompatActivity;
import static com.mongodb.client.model.Filters.*;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.model.Filters;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicReference;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;
import io.realm.mongodb.mongo.options.FindOptions;
import io.realm.mongodb.sync.ClientResetRequiredError;
import io.realm.mongodb.sync.SyncConfiguration;
import io.realm.mongodb.sync.SyncSession;

public class LoginActivity extends AppCompatActivity {

    private static final String PARTITION = "partitionValue";
    //    private static final String PARTITION = "";
    String Appid = "application-0-tfcfh";
    EditText et_login, et_passw;

    App app;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_login = findViewById(R.id.editTextTextPersonName);
        et_passw = findViewById(R.id.editTextTextPersonName);
        Realm.init(this);
    }


    public void onClick(View view) {

        App app = new App(new AppConfiguration.Builder(Appid).build());
        Credentials apiKeyCredentials = Credentials.apiKey("H4cVO8qT8q8cehZVoI3QRsiN17XXY2QZZQ0wSDvcAZZck8KZNFL6UuVCdlob5nz2");
        AtomicReference<User> user = new AtomicReference<io.realm.mongodb.User>();
        app.loginAsync(apiKeyCredentials, it -> {
            if (it.isSuccess()) {
                user.set(app.currentUser());
            } else {
                System.out.println("AUTH" + it.getError().toString());
            }
        });
        MongoClient mongoClient = app.currentUser().getMongoClient("mongodb-atlas");
        MongoDatabase mongoDatabase = mongoClient.getDatabase("messanger");
        CodecRegistry pojoCodecRegistry = fromRegistries(AppConfiguration.DEFAULT_BSON_CODEC_REGISTRY, fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        MongoCollection<UserObject> mongoCollection = mongoDatabase.getCollection("users", UserObject.class).withCodecRegistry(pojoCodecRegistry);



        Document queryFilter  = new Document("login", et_login.getText().toString());

        mongoCollection.findOne(queryFilter).getAsync(task -> {
            if (task.isSuccess()) {
                UserObject result = task.get();
                Log.v("BAZA DANYCH", "successfully found a document: " + result.getSurname());


            } else {
                Log.e("BAZA DANYCH", "failed to find document with: ", task.getError());
            }
        });

    }
}