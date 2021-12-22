package com.example.mobileapk;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;
import io.realm.mongodb.mongo.iterable.MongoCursor;

public class ChatActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private Intent i_back, intent;
    private SessionManager sessionManager;
    private EditText et_message;
    private String send_to;
    private RecyclerView recycler_messages;
    private int maxDisplayWidth;
    String Appid = "application-0-tfcfh";
    Boolean scrollingToBottom;
    Thread thread;
    Toolbar mActionBarToolbar;
    Parcelable recyclerViewState;
    Runnable r;
    boolean off=false;
    AtomicBoolean firstRefresh= new AtomicBoolean(false);

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        utworzRecycleView();
        setKeyboardListener();
        ustawToolbar();
        sessionManager = new SessionManager(getApplicationContext());
        et_message = findViewById(R.id.editTextTextPersonName3);
        intent = getIntent();
        mActionBarToolbar = (Toolbar) findViewById(R.id.myToolbar);
        getSupportActionBar().setTitle(intent.getStringExtra("name"));
        send_to = intent.getStringExtra("id");


        Handler handler = new Handler();
        r = new Runnable() {
            public void run() {
                if(off) return;
                RefreshMessage();
                handler.postDelayed(this, 1000);
            }
        };
        r.run();
    }


    public void onBackPressed() {
        super.onBackPressed();
        off=true;
    }

    public void ustawToolbar() {
        toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_bar_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i_back = new Intent(getApplicationContext(), LoggedActivity.class);
                startActivity(i_back);
            }
        });
    }

    public void setKeyboardListener() {
        recycler_messages.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                recycler_messages.getWindowVisibleDisplayFrame(r);
                int screenHeight = recycler_messages.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;
                if (keypadHeight > screenHeight * 0.15) {
                    // keyboard is opened
                    if (!scrollingToBottom) {
                        scrollingToBottom = true;
                        recycler_messages.scrollToPosition(recycler_messages.getAdapter().getItemCount() - 1);
                    }
                } else {
                    // keyboard is closed
                    scrollingToBottom = false;
                }
            }
        });
    }

    public void utworzRecycleView() {
        //display size
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        maxDisplayWidth = (int) (displayMetrics.widthPixels * 0.8);
        //stworzenie pustego recyclerview przed aktualizacja
        recycler_messages = findViewById(R.id.recyclerView);
        recycler_messages.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recycler_messages.setAdapter(new Adapter_message(new ArrayList<String>(), new ArrayList<String>(), "", maxDisplayWidth));
    }


    public void onClickSend(View v) {
        sendMessage(et_message.getText().toString(), send_to);
    }

    void sendMessage(String message, String send_to) {

        et_message.setText("");
        String from = sessionManager.preferences.getString("KEY_ID", "");

        App app = new App(new AppConfiguration.Builder(Appid).build());
        MongoClient mongoClient = app.currentUser().getMongoClient("mongodb-atlas");
        MongoDatabase mongoDatabase = mongoClient.getDatabase("messanger");
        CodecRegistry pojoCodecRegistry = fromRegistries(AppConfiguration.DEFAULT_BSON_CODEC_REGISTRY, fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        MongoCollection<MessageObject> mongoCollection = mongoDatabase.getCollection("messages", MessageObject.class).withCodecRegistry(pojoCodecRegistry);
        System.out.println("EXAMPLE" + " Successfully instantiated the MongoDB collection handle");
        mongoCollection.insertOne(new MessageObject(from, send_to, message)).getAsync(task -> {
            if (task.isSuccess()) {
                System.out.println("EXAMPLE " + "successfully inserted a document with id: " + task.get().getInsertedId());
            } else {
                System.out.println("EXAMPLE " + "failed to insert documents with: " + task.getError().getErrorMessage());
            }
        });

    }





    void RefreshMessage() {

        String id_user = sessionManager.preferences.getString("KEY_ID", "");
        ArrayList<String> messages = new ArrayList<>();
        ArrayList<String> id_send_from= new ArrayList<>();

        App app = new App(new AppConfiguration.Builder(Appid).build());
        MongoClient mongoClient = app.currentUser().getMongoClient("mongodb-atlas");
        MongoDatabase mongoDatabase = mongoClient.getDatabase("messanger");
        CodecRegistry pojoCodecRegistry = fromRegistries(AppConfiguration.DEFAULT_BSON_CODEC_REGISTRY, fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        MongoCollection<MessageObject> mongoCollection = mongoDatabase.getCollection("messages", MessageObject.class).withCodecRegistry(pojoCodecRegistry);

        Document queryFilter = new Document("$or", Arrays.asList(

                    new Document("$and",
                            Arrays.asList(
                                new Document("send_to",id_user),
                                new Document("send_from",send_to)
                    )),

                    new Document("$and",
                            Arrays.asList(
                                new Document("send_to",send_to),
                                new Document("send_from",id_user)
                    ))
                ));


        RealmResultTask<MongoCursor<MessageObject>> findTask = mongoCollection.find(queryFilter).iterator();

        findTask.getAsync(task -> {
                if (task.isSuccess()) {

                    MongoCursor<MessageObject> results = task.get();
                    while (results.hasNext()) {
                        MessageObject message = results.next();
                        messages.add(message.getMessage_content());
                        id_send_from.add(message.getSend_from());
                    }
                    LinearLayoutManager myLayoutManager = (LinearLayoutManager) recycler_messages.getLayoutManager();
                    recyclerViewState = recycler_messages.getLayoutManager().onSaveInstanceState();

                    if (myLayoutManager.findLastVisibleItemPosition() == messages.size() - 1 || myLayoutManager.findLastVisibleItemPosition() == messages.size() - 2 || firstRefresh.get() ==false) {
                        recycler_messages.setAdapter(new Adapter_message(messages, id_send_from, id_user, maxDisplayWidth));
                        recycler_messages.scrollToPosition(messages.size() - 1);
                        firstRefresh.set(true);

                    } else {
                        recycler_messages.setAdapter(new Adapter_message(messages, id_send_from, id_user, maxDisplayWidth));
                        recycler_messages.getLayoutManager().onRestoreInstanceState(recyclerViewState);
                    }
                } else {
                    Log.e("WYSZUKIWANIE", "failed to find documents with: ", task.getError());
                }
        });
    }
}