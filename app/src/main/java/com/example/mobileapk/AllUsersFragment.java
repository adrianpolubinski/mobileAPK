package com.example.mobileapk;


import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;
import io.realm.mongodb.mongo.iterable.MongoCursor;

public class AllUsersFragment extends Fragment {

    String Appid = "application-0-tfcfh";
    SessionManager sessionManager;
    ArrayList<UserObject> osoby;
    RecyclerView person_recycler;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_all_users, container, false);
        context = v.getContext();
        sessionManager = new SessionManager(context);
        person_recycler = v.findViewById(R.id.recyclerView);
        person_recycler.setLayoutManager(new LinearLayoutManager(context));
        osoby = new ArrayList<>();
        person_recycler.setAdapter(new Adapter_person(osoby));
        RefreshUsers();
        return v;
    }

    void RefreshUsers() {

        App app = new App(new AppConfiguration.Builder(Appid).build());
        Credentials apiKeyCredentials = Credentials.apiKey("H4cVO8qT8q8cehZVoI3QRsiN17XXY2QZZQ0wSDvcAZZck8KZNFL6UuVCdlob5nz2");
        app.loginAsync(apiKeyCredentials, it -> {
            if (it.isSuccess()) {
                Log.v("BAZA DANYCH", "Udane logowanie za pomocą api KEY.");
            } else {
                Log.e("BAZA DANYCH", "Wystąpił problem z logowaniem za pomocą api KEY.");
            }
        });

        MongoClient mongoClient = app.currentUser().getMongoClient("mongodb-atlas");
        MongoDatabase mongoDatabase = mongoClient.getDatabase("messanger");
        CodecRegistry pojoCodecRegistry = fromRegistries(AppConfiguration.DEFAULT_BSON_CODEC_REGISTRY, fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        MongoCollection<UserObject> mongoCollection = mongoDatabase.getCollection("users", UserObject.class).withCodecRegistry(pojoCodecRegistry);


        Document queryFilter = new Document();
        RealmResultTask<MongoCursor<UserObject>> findTask = mongoCollection.find(queryFilter).iterator();
        findTask.getAsync(task -> {
            if (task.isSuccess()) {
                MongoCursor<UserObject> results = task.get();
                while (results.hasNext()) {
//                    Log.v("EXAMPLE", results.next().toString());
                    osoby.add(results.next());
                }
            } else {
                Log.e("EXAMPLE", "failed to find documents with: ", task.getError());
            }
        });

        person_recycler.setAdapter(new Adapter_person(osoby));
    }
}