package com.example.mobileapk;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.Button;
import android.view.View;

import java.util.concurrent.atomic.AtomicReference;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoDatabase;


public class MainActivity extends AppCompatActivity {

    Button btn_reg, btn_log;
    Intent i_reg, i_log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_reg = findViewById(R.id.btn_register);
        i_reg = new Intent(this, RegisterActivity.class);

        btn_log = findViewById(R.id.btn_log);
        i_log = new Intent(this, LoginActivity.class);


//wylogowanie
//        user.get().logOutAsync( result -> {
//            if (result.isSuccess()) {
//                System.out.println("AUTH " + "Successfully logged out.");
//            } else {
//                System.out.println("AUTH "+ result.getError().toString());
//            }
//        });

    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.bt_reg:
                startActivity(i_reg);
                break;
            case R.id.btn_log:
                startActivity(i_log);
                break;
            default: break;
        }
    }
}