package com.example.mobileapk;
import static com.example.mobileapk.LoggedActivity.convert;
import static com.example.mobileapk.LoggedActivity.drawableToBitmap;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.codecs.pojo.PropertyCodecRegistry;

import java.util.ArrayList;
import java.util.List;

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
    ArrayList<String> avatars;
    RecyclerView person_recycler;
    Context context;
    ImageView iv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_all_users, container, false);
        context = v.getContext();
        sessionManager = new SessionManager(context);
        person_recycler = v.findViewById(R.id.recyclerView);
        person_recycler.setLayoutManager(new LinearLayoutManager(context));
        osoby = new ArrayList<>();
        avatars = new ArrayList<>();
        // Repair: E/RecyclerView: No adapter attached; skipping layout
        person_recycler.setAdapter(new Adapter_person(osoby));

        iv = new ImageView(getContext());

        if(isOnline()){
            refreshUsers();
        }
        else{
            ArrayList<RecyclerView> adapter = PrefConf.readListAvatars(getContext());
            person_recycler = adapter.get(0);
        }

        return v;
    }

    public boolean isOnline() {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected()){
                return true;
        }
        else{
            return false;
        }
    }

    void refreshUsers() {

        App app = new App(new AppConfiguration.Builder(Appid).build());

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
                    UserObject user = results.next();
                    if(sessionManager.preferences.getString("KEY_ID","").equals(user.getId().toString()))
                        continue;
                    osoby.add(user);
                }








                person_recycler.setAdapter(new Adapter_person(osoby));
                ArrayList<RecyclerView> list = new ArrayList<>();
                list.add(person_recycler);
                PrefConf.writeListAvatars(getContext(), list);
            } else {
                Log.e("WYSZUKIWANIE", "failed to find documents with: ", task.getError());
            }
        });






    }
}