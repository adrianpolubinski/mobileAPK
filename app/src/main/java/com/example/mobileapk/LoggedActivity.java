package com.example.mobileapk;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.style.UpdateAppearance;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.navigation.NavigationView;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import es.dmoral.toasty.Toasty;
import io.realm.internal.annotations.ObjectServer;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.Statement;
import android.util.Base64;


public class LoggedActivity extends AppCompatActivity {

    String Appid = "application-0-tfcfh";
    public DrawerLayout mDrawer;
    public NavigationView nvDrawer;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    private CharSequence toolbarName;
    SessionManager sessionManager;
    ImageView iv;
    FragmentManager fragmentManager;

    TextView tv_podpis;
    MenuItem aktualneMenuBoczne;
    boolean wroc;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged);
        wroc=false;
        sessionManager = new SessionManager(this);
        fragmentManager = getSupportFragmentManager();
        toolbar = findViewById(R.id.myToolbar);

        setSupportActionBar(toolbar);

        try {
            fragmentManager.beginTransaction().replace(R.id.frameLayout, AllUsersFragment.class.newInstance()).commit();
            getSupportActionBar().setTitle("Użytkownicy");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (savedInstanceState != null)
            getSupportActionBar().setTitle(savedInstanceState.getCharSequence("toolbarName"));
        mDrawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        nvDrawer = (NavigationView) findViewById(R.id.navigationView);
        setNavigationListener(nvDrawer);


        drawerToggle = setupDrawerToggle();
        drawerToggle.syncState();

        //animacja  hamburgerka
        mDrawer.addDrawerListener(drawerToggle);


        //ustawienie avatara
        iv = nvDrawer.getHeaderView(0).findViewById(R.id.avatar);



        tv_podpis = (TextView) nvDrawer.getHeaderView(0).findViewById(R.id.tv_podpis);
        tv_podpis.setText(sessionManager.preferences.getString("KEY_IMIE", "IMIĘ") + " " + sessionManager.preferences.getString("KEY_NAZWISKO", ""));

        if (sessionManager.loadNightModeState() == true) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        if(isOnline()){
        Glide.with(this).load(sessionManager.preferences.getString("KEY_AVATAR","https://pogadankowo.refy.pl/avatars/default.png"))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false; // important to return false so the error placeholder can be placed
                    }

                    @SuppressLint("CheckResult")
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        String imageBase64 = convert(drawableToBitmap(resource));
                        sessionManager.cacheAvatar(imageBase64);
                        return false;
                    }
                })
                .into(iv);
        } else{
            Bitmap bm = convert(sessionManager.preferences.getString("KEY_CACHE_AVATAR",""));
            iv.setImageBitmap(bm);
        }



    }

    public boolean isOnline() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected()){
            return true;
        }
        else{
            return false;
        }
    }



    public static Bitmap convert(String base64Str) throws IllegalArgumentException
    {
        byte[] decodedBytes = Base64.decode(
                base64Str.substring(base64Str.indexOf(",")  + 1),
                Base64.DEFAULT
        );

        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public static String convert(Bitmap bitmap)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        } else  {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.toolbar_menu_view, menu);
        return true;
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Fragment fragment;
        Class fragmentClass;
        switch (item.getItemId()) {
            case R.id.toolbar_first_fragment:
                fragmentClass = AboutUsFragment.class;
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit();
                    aktualneMenuBoczne.setChecked(false);
                    getSupportActionBar().setTitle("Informacje");
                    wroc=true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            default:
                break;
        }

        return true;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onBackPressed() {
        if(wroc) {
            mDrawer.closeDrawers();
            Fragment fragment;
            Class fragmentClass;
            switch (aktualneMenuBoczne.getItemId()) {
                case R.id.nav_first_fragment:
                    fragmentClass = AllUsersFragment.class;
                    break;
                case R.id.nav_second_fragment:
                    fragmentClass = AllUsersFragment.class;
                    break;
                case R.id.nav_third_fragment:
                    fragmentClass = AllUsersFragment.class;
                    break;
                default:
                    fragmentClass = AllUsersFragment.class;
            }
            try {
                fragment = (Fragment) fragmentClass.newInstance();
                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit();
                aktualneMenuBoczne.setChecked(true);
                getSupportActionBar().setTitle(aktualneMenuBoczne.getTitle());
            } catch (Exception e) {
                e.printStackTrace();
            }
            wroc=false;
        }
        else
            mDrawer.closeDrawers();

    }

    private void setNavigationListener(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });

    }


    public void selectDrawerItem(MenuItem menuItem) {
        if(wroc) {wroc=!wroc;}
        Fragment fragment = null;
        Class fragmentClass=null;
        aktualneMenuBoczne=menuItem;
        switch (menuItem.getItemId()) {
            case R.id.nav_first_fragment:
                fragmentClass = AllUsersFragment.class;
                break;
            case R.id.nav_second_fragment:
                fragmentClass = AboutUsFragment.class;
                break;
            case R.id.nav_third_fragment:
                fragmentClass = MotywFragment.class;
                break;
            case R.id.nav_fifth_fragment:
                fragmentClass = SettingsFragment.class;
                break;
            case R.id.nav_fourth_fragment:
                fragmentClass = null;
                Toasty.info(this, "Wylogowano Pomyślnie !", Toasty.LENGTH_SHORT).show();
                sessionManager.logoutUser();
                break;
            default:
                fragmentClass = AllUsersFragment.class;
        }

        if (fragmentClass != null) {
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //ustawienie fragmentu
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit();
            menuItem.setChecked(true);
            toolbarName = menuItem.getTitle();
            getSupportActionBar().setTitle(menuItem.getTitle());
            mDrawer.closeDrawers();
        }
        else
            mDrawer.closeDrawers();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence("toolbarName", toolbarName);
    }
    // onClick imageView
    public void onClick(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @SuppressLint("CheckResult")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                Toasty.warning(getApplicationContext(), "Problem z odczytaniem danych!", Toasty.LENGTH_LONG, true);
            } else {
                new UploadAvatar().execute(data.getData());
                updateAvatar();

            }
        }
    }



    private class UploadAvatar extends AsyncTask<Uri, Void, Boolean> {
        InputStream zdjecie,zdjecieFTP;

        String ftp_host, ftp_login, ftp_haslo, ftp_path, ftp_rozszerzenie, login;
        FTPClient ftp;
        @Override
        protected Boolean doInBackground(Uri... inputStreams) {

            try {
                zdjecie = getApplicationContext().getContentResolver().openInputStream(inputStreams[0]);
                zdjecieFTP = getApplicationContext().getContentResolver().openInputStream(inputStreams[0]);
                ftp_host = "pogadankowo.refy.pl";
                ftp_login = "pogadankowo@refy.pl";
                ftp_haslo = "Pogadankowo@123";
                ftp_path = "avatars/";
                ftp_rozszerzenie = ".png";
                login = sessionManager.preferences.getString("KEY_LOGIN", "");

                ftp = new FTPClient();
                ftp.connect(ftp_host);
                ftp.login(ftp_login, ftp_haslo);
                ftp.setFileType(FTP.BINARY_FILE_TYPE);
                ftp.enterLocalPassiveMode();
                ftp.storeFile(ftp_path + login + ftp_rozszerzenie, zdjecieFTP);

                ftp.logout();
                ftp.disconnect();

                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean wynik) {
            super.onPostExecute(wynik);
            if (wynik) {
                iv.setImageBitmap(BitmapFactory.decodeStream(zdjecie));
                Toasty.success(getApplicationContext(), "Zmiana avatara pomyślna.", Toasty.LENGTH_LONG, true).show();
            } else {
                Toasty.error(getApplicationContext(), "Wystąpił błąd przy załadowaniu obrazka!", Toasty.LENGTH_LONG, true).show();
            }
        }
    }

    void updateAvatar(){
        App app = new App(new AppConfiguration.Builder(Appid).build());

        MongoClient mongoClient = app.currentUser().getMongoClient("mongodb-atlas");
        MongoDatabase mongoDatabase = mongoClient.getDatabase("messanger");
        CodecRegistry pojoCodecRegistry = fromRegistries(AppConfiguration.DEFAULT_BSON_CODEC_REGISTRY, fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        MongoCollection<UserObject> mongoCollection = mongoDatabase.getCollection("users", UserObject.class).withCodecRegistry(pojoCodecRegistry);

        String login =  sessionManager.preferences.getString("KEY_LOGIN", "");
        Document queryFilter = new Document("login", login);
        Document updateDocument = new Document("$set", new Document("avatar", "https://pogadankowo.refy.pl/avatars/"+login+".png"));
        mongoCollection.updateOne(queryFilter, updateDocument).getAsync(task -> {
            if (task.isSuccess()) {
                long count = task.get().getModifiedCount();
                if (count == 1) {
                    Log.v("EXAMPLE", "successfully updated a document.");
                } else {
                    Log.v("EXAMPLE", "did not update a document.");
                }
            } else {
                Log.e("EXAMPLE", "failed to update document with: ", task.getError());
            }
        });

    }


}