package com.example.mobileapk;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;

public class LoginActivity extends AppCompatActivity {

    String Appid = "application-0-tfcfh";
    EditText et_login, et_passw;
    SessionManager sessionManager;
    ProgressBar pb;
    TextView tv_progress;
    Intent i_logged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        i_logged = new Intent(this, LoggedActivity.class);
        et_login = findViewById(R.id.editTextTextPersonName);
        et_passw = findViewById(R.id.editTextTextPassword2);
        pb = findViewById(R.id.progressBar);
        tv_progress = findViewById(R.id.textView3);

        //fast login
        et_login.setText("tester4");
        et_passw.setText("Tester4.");

        Realm.init(this);
        sessionManager = new SessionManager(getApplicationContext());
    }


    public void onClick(View view) {

        closeKeyboard();
        tv_progress.setTextColor(getResources().getColor(R.color.black));
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        pb.setVisibility(View.VISIBLE);
        pb.setProgress(20);
        tv_progress.setText("Przygotowanie do podłączenia z bazą danych.");
        tv_progress.setVisibility(View.VISIBLE);


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

        tv_progress.setText("Przygotowanie zapytania.");
        pb.setProgress(40);

        Document queryFilter  = new Document("login", et_login.getText().toString());
        mongoCollection.findOne(queryFilter).getAsync(task -> {

            tv_progress.setText("Sprawdzanie czy istnieje uzytkownik.");
            pb.setProgress(60);

            if (task.isSuccess() && task.get()!=null) {
                UserObject person = task.get();
                if(BCrypt.checkpw(et_passw.getText().toString(), person.getPassword())){
                    tv_progress.setText("Finalizowanie");
                    pb.setProgress(100);
                    sessionManager.createLoginSession(person.getId().toString(), person.getLogin(), person.getUserName().getName(), person.getUserName().getSurname(), person.getAvatar());
                    Toasty.success(getApplicationContext(), "Logowanie pomyślne!", Toast.LENGTH_SHORT, true).show();
                    pb.setVisibility(View.INVISIBLE);
                    tv_progress.setVisibility(View.INVISIBLE);
                    startActivity(i_logged);

                }
                else {
                    Toasty.error(getApplicationContext(), "Podano złe hasło!", Toast.LENGTH_LONG, true).show();
                    pb.setVisibility(View.INVISIBLE);
                    tv_progress.setVisibility(View.INVISIBLE);
                }
            } else {
                Toasty.warning(getApplicationContext(), "Taki użytkownik nie istnieje.", Toast.LENGTH_SHORT, true).show();
                pb.setVisibility(View.INVISIBLE);
                tv_progress.setVisibility(View.INVISIBLE);
            }
        });

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}