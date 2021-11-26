package com.example.mobileapk;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;


public class RegisterActivity extends AppCompatActivity {

    Button btn_reg;
    EditText et_login, et_passw, et_passwr, et_mail, et_name, et_surname;
    TextView tv_reg;
    CheckBox chb_reg;
    String currentDate;
    ArrayList<String> parametry, logins, mails;
    Intent i_main;
    String error;
    String Appid = "application-0-tfcfh";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_login = findViewById(R.id.et_login);
        et_passw = findViewById(R.id.et_password);
        et_passwr = findViewById(R.id.et_passwordr);
        et_mail = findViewById(R.id.et_mail);
        et_name = findViewById(R.id.et_name);
        et_surname = findViewById(R.id.et_surname);
        chb_reg = findViewById(R.id.chb_reg);
        btn_reg = findViewById(R.id.btn_register);
        parametry = new ArrayList<String>();
        logins = new ArrayList<String>();
        mails = new ArrayList<String>();
        tv_reg = findViewById(R.id.tv_reg);
        tv_reg.setMovementMethod(LinkMovementMethod.getInstance());
        error = "";
        i_main = new Intent(this, MainActivity.class);
        Realm.init(this);
    }

    public void onClickRegister(View view) {

        if (sprawdzLogin(et_login.getText().toString()) && sprawdzHaslo(et_passw.getText().toString(), et_passwr.getText().toString()) && sprwadzMail(et_mail.getText().toString()) && sprawdzImie(et_name.getText().toString()) && sprawdzNazwisko(et_surname.getText().toString())) {
            if (chb_reg.isChecked()) {
                error = "";
                currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                //do poprawy
                App app = new App(new AppConfiguration.Builder(Appid).build());
                Credentials apiKeyCredentials = Credentials.apiKey("H4cVO8qT8q8cehZVoI3QRsiN17XXY2QZZQ0wSDvcAZZck8KZNFL6UuVCdlob5nz2");
                AtomicReference<io.realm.mongodb.User> user = new AtomicReference<io.realm.mongodb.User>();
                app.loginAsync(apiKeyCredentials, it -> {
                    if (it.isSuccess()) {
                        System.out.println("AUTH " + "Successfully authenticated using an API Key.");
                        user.set(app.currentUser());
                    } else {
                        System.out.println("AUTH" + it.getError().toString());
                    }
                });
                MongoClient mongoClient = app.currentUser().getMongoClient("mongodb-atlas");
                MongoDatabase mongoDatabase = mongoClient.getDatabase("messanger");
                CodecRegistry pojoCodecRegistry = fromRegistries(AppConfiguration.DEFAULT_BSON_CODEC_REGISTRY, fromProviders(PojoCodecProvider.builder().automatic(true).build()));
                MongoCollection<UserObject> mongoCollection = mongoDatabase.getCollection("users", UserObject.class).withCodecRegistry(pojoCodecRegistry);
                System.out.println("EXAMPLE" + "Successfully instantiated the MongoDB collection handle");
                mongoCollection.insertOne(new UserObject(et_login.getText().toString(), hash(et_passw.getText().toString()), et_mail.getText().toString(), et_name.getText().toString(), et_surname.getText().toString(), currentDate)).getAsync(task -> {
                    if (task.isSuccess()) {
                        System.out.println("EXAMPLE " + "successfully inserted a document with id: " + task.get().getInsertedId());
                    } else {
                        System.out.println("EXAMPLE " + "failed to insert documents with: " + task.getError().getErrorMessage());
                    }
                });


            } else {
                error = "Zaakceptuj regulamin!";
            }
        }

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        if (error != "") {
            Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
            System.out.println("ERROR: " + error);
        }
        else {
            Toast.makeText(getApplicationContext(), "Gratulacje !!! Rejestracja pomyślna.", Toast.LENGTH_LONG).show();
            System.out.println("Gratulacje !!! Rejestracja pomyślna.");
            startActivity(i_main);
        }
    }

    public boolean sprawdzLogin(String log) {
        boolean zwroc = true;
        if (log.length() > 2) {
            for (String s : logins) {
                if (log.equals(s)) {
                    zwroc = false;
                    error = "Login jest już użyty!";
                    break;
                }
            }
        } else {
            error = "Login powinien mieć długość przynajmniej 3 znaków!";
            zwroc = false;
        }

        for (int i = 0; i < log.length(); i++) {
            if (!(Character.isLetterOrDigit(log.charAt(i)))) {
                zwroc = false;
                error = "Login może składać się tylko z liter i cyfr!";
                break;
            }
        }
        return zwroc;
    }

    public boolean sprawdzHaslo(String h1, String h2) {
//https://java2blog.com/validate-password-java/
        boolean zwroc = false;
        boolean duza_litera = true;
        boolean mala_litera = true;
        for (int i = 0; i < h1.length(); i++) {
            if (Character.isUpperCase(h1.charAt(i))) duza_litera = false;
            if (Character.isLowerCase(h1.charAt(i))) mala_litera = false;
        }
        if (h1.length() < 8) {
            error = "Hasło powinno posiadać minimum 8 znaków!";
        } else if (duza_litera || mala_litera) {
            error = "Użyj dużych i małych liter w haśle!";
        } else {
            if (h1.equals(h2)) {
                zwroc = true;
            } else {
                error = "Podane hasła nie są takie same!";
                zwroc = false;
            }
        }
        return zwroc;
    }

    public boolean sprwadzMail(String mail) {
        boolean zwroc = true;
        Pattern pattern = Pattern.compile("^.+@.+\\..+$");
        Matcher matcher = pattern.matcher(mail);
        zwroc = matcher.matches();
        if (!zwroc) {
            error = "Błędny adres e-mail!";
        } else {
            for (String s : mails) {
                if (mail.equals(s)) {
                    zwroc = false;
                    error = "Adres e-mail jest już użyty!";
                    break;
                }
            }
        }
        return zwroc;
    }

    public boolean sprawdzImie(String imie) {

        boolean zwroc = true;
        if (imie.length() < 3) {
            zwroc = false;
            error = "Imię jest za krótkie!";
        } else if (!Character.isUpperCase(imie.charAt(0))) {
            zwroc = false;
            error = "Pierwsza litera powinna być duża!";
        } else {
            for (int i = 0; i < imie.length(); i++) {
                if (!Character.isLetter(imie.charAt(i))) {
                    zwroc = false;
                    error = "Imię składa się tylko z liter!";
                }
            }
        }
        return zwroc;
    }

    public boolean sprawdzNazwisko(String nazwisko) {

        boolean zwroc = true;
        if (nazwisko.length() < 3) {
            zwroc = false;
            error = "Nazwisko jest za krótkie!";
        } else if (!Character.isUpperCase(nazwisko.charAt(0))) {
            zwroc = false;
            //Toasty.warning(getApplicationContext(), "Pierwsza litera powinna być duża!", Toast.LENGTH_LONG, true).show();
            error = "Pierwsza litera powinna być duża!";
        } else {
            for (int i = 0; i < nazwisko.length(); i++) {
                if (!Character.isLetter(nazwisko.charAt(i))) {
                    zwroc = false;
                    error = "Nazwisko składa się tylko z liter!";
                }
            }
        }
        return zwroc;
    }

    public String hash(String haslo) {
        return BCrypt.hashpw(haslo, BCrypt.gensalt(10));
    }
}