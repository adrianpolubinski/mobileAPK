package com.example.mobileapk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.material.navigation.NavigationView;

import es.dmoral.toasty.Toasty;
import io.realm.internal.annotations.ObjectServer;

public class LoggedActivity extends AppCompatActivity {

    SessionManager sessionManager;
    FragmentManager fragmentManager;
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged);
        sessionManager = new SessionManager(this);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView = (NavigationView) findViewById(R.id.drawer_nv);
        setNavigationListener(navigationView);

        // uzupełnienie fragmentu na start
        fragmentManager = getSupportFragmentManager();
        try {
            fragmentManager.beginTransaction().replace(R.id.drawer_linear, AllUsersFragment.class.newInstance()).commit();
            getSupportActionBar().setTitle("Użytkownicy");
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        Fragment fragment = null;
        Class fragmentClass = null;
        switch (menuItem.getItemId()) {
            case R.id.item1:
                fragmentClass = AllUsersFragment.class;
                break;
            case R.id.item2:
                break;
            case R.id.item3:
                fragmentClass = AboutUsFragment.class;
                break;
            case R.id.item4:
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
            fragmentManager.beginTransaction().replace(R.id.drawer_linear, fragment).commit();
            menuItem.setChecked(true);
            getSupportActionBar().setTitle(menuItem.getTitle());
            drawerLayout.closeDrawers();
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}