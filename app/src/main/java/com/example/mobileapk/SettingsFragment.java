package com.example.mobileapk;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import static io.realm.Realm.getApplicationContext;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import es.dmoral.toasty.Toasty;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;

public class SettingsFragment extends Fragment {
    String Appid = "application-0-tfcfh";
    boolean stare_haslo = false;

    Button zmiendane, zmienhaslo, zmien;
    LinearLayout dane, haslo;
    EditText imie, nazwisko, starehaslo, nowehaslo, haslor;
    boolean otwarty;
    String error;
    SessionManager sessionManager;
    MongoCollection<UserObject> mongoCollection;

    public SettingsFragment() {
    }

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        sessionManager = new SessionManager(getApplicationContext());

        otwarty = false;

        // buttony
        zmiendane = (Button) v.findViewById(R.id.bt_zmiendane);
        zmienhaslo = (Button) v.findViewById(R.id.bt_zmienhaslo);
        zmien = (Button) v.findViewById(R.id.btn_chagne);

        // layout
        dane = v.findViewById(R.id.ll_data);
        haslo = v.findViewById(R.id.ll_password);

        // pola dla zmiany danych
        imie = v.findViewById(R.id.et_new_name);
        nazwisko = v.findViewById(R.id.et_new_surname);

        // pola zmiany hasła
        starehaslo = v.findViewById(R.id.et_old_password);
        nowehaslo = v.findViewById(R.id.et_new_password);
        haslor = v.findViewById(R.id.et_new_password_r);

        // pokaż formularz zmiany danych
        zmiendane.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (zmien.getVisibility() == View.INVISIBLE) zmien.setVisibility(View.VISIBLE);

                if (!otwarty) {
                    haslo.setVisibility(View.INVISIBLE);

                    ViewGroup.LayoutParams params = dane.getLayoutParams();
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    dane.setLayoutParams(params);

                    params = haslo.getLayoutParams();
                    params.height = 0;
                    haslo.setLayoutParams(params);
                }

                dane.setVisibility(View.VISIBLE);
                otwarty = true;

                zmien.setVisibility(View.VISIBLE);
            }
        });

        // pokaż formularz zmiany hasła
        zmienhaslo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (zmien.getVisibility() == View.INVISIBLE) zmien.setVisibility(View.VISIBLE);

                ViewGroup.LayoutParams params = dane.getLayoutParams();
                params.height = 0;
                dane.setLayoutParams(params);
                if (otwarty) {
                    dane.setVisibility(View.INVISIBLE);
                    otwarty = false;

                    params = haslo.getLayoutParams();
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    haslo.setLayoutParams(params);
                }
                haslo.setVisibility(View.VISIBLE);
            }
        });

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
        mongoCollection = mongoDatabase.getCollection("users", UserObject.class).withCodecRegistry(pojoCodecRegistry);

        //zmien dane
        zmien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (otwarty) {
                    if (sprawdzDane()) {
                        String login = sessionManager.pobierzLogin();
                        Document queryFilter = new Document("login", login);
                        Document updateDocument = new Document("$set", new Document("userName", new UserName(imie.getText().toString(), nazwisko.getText().toString())));
                        mongoCollection.updateOne(queryFilter, updateDocument).getAsync(task -> {
                            if (task.isSuccess()) {
                                long count = task.get().getModifiedCount();
                                if (count == 1) {
                                    Log.v("EXAMPLE", "successfully updated a document.");
                                    sessionManager.editor.putString("KEY_IMIE",imie.getText().toString());
                                    sessionManager.editor.putString("KEY_NAZWISKO",nazwisko.getText().toString());
                                    Toasty.success(getApplicationContext(), "Dane poprawne!", Toast.LENGTH_LONG, true).show();
                                } else {
                                    Log.v("EXAMPLE", "did not update a document.");
                                }
                            } else {
                                Log.e("EXAMPLE", "failed to update document with: ", task.getError());
                            }
                        });
                    } else {
                        Toasty.warning(getApplicationContext(), error, Toast.LENGTH_LONG, true).show();
                    }
                } else {
                    sprawdzHasla();
                }
            }
        });
        return v;
    }

    public boolean sprawdzDane() {

        boolean wynik = false;

        if (sprawdzImie(imie.getText().toString()) && sprawdzNazwisko(nazwisko.getText().toString())) {
            wynik = true;
        }
        return wynik;
    }

    public void sprawdzHasla() {

        Document queryFilter = new Document("login", sessionManager.pobierzLogin());
        mongoCollection.findOne(queryFilter).getAsync(task -> {
            if (task.isSuccess() && task.get() != null) {
                UserObject person = task.get();
                if (BCrypt.checkpw(starehaslo.getText().toString(), person.getPassword())) {
                    if (sprawdzHaslo(nowehaslo.getText().toString(), haslor.getText().toString())) {
                        String login = sessionManager.pobierzLogin();
                        Document queryFilter1 = new Document("login", login);
                        Document updateDocument = new Document("$set", new Document("password", hash(nowehaslo.getText().toString())));
                        mongoCollection.updateOne(queryFilter1, updateDocument).getAsync(task1 -> {
                            if (task1.isSuccess()) {
                                long count = task1.get().getModifiedCount();
                                if (count == 1) {
                                    Log.v("EXAMPLE", "successfully updated a document.");
                                    Toasty.success(getApplicationContext(), "Dane poprawne!", Toast.LENGTH_LONG, true).show();
                                } else {
                                    Log.v("EXAMPLE", "did not update a document.");
                                }
                            } else {
                                Log.e("EXAMPLE", "failed to update document with: ", task1.getError());
                            }
                        });
                    } else {
                        Toasty.warning(getApplicationContext(), error, Toast.LENGTH_LONG, true).show();
                    }
                } else {
                    Toasty.warning(getApplicationContext(), "Stare hasło niepoprawne", Toast.LENGTH_LONG, true).show();
                }
            }
        });
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

    public boolean sprawdzHaslo(String h1, String h2) {
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

    public String hash(String haslo) {
        return BCrypt.hashpw(haslo, BCrypt.gensalt(10));
    }
}