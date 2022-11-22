package com.sweng411.smashrun;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Protocol;
import okhttp3.OkHttpClient;

//gVN65rU@c5tc
public class MainActivity extends AppCompatActivity {

    private  Toolbar myToolbar;
    private DrawerLayout myDrawerLayout;
    private BottomNavigationView bottomNavigationView;
    private HomeFragment homeFragment;
    private static List<Protocol> protocols = new ArrayList<>();
    private static OkHttpClient okHttpClient;

    private static SharedPreferences sharedPref;
    private static boolean listLoaded = false;
    private static String jsonString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = getSharedPreferences("com.sweng411.smashrun", Context.MODE_PRIVATE);
        setContentView(R.layout.activity_main);

        protocols.add(Protocol.HTTP_1_1);
        okHttpClient = new OkHttpClient.Builder().protocols(protocols).build();

        //Toolbar and Navigation Drawer
        myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        myDrawerLayout = findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, myDrawerLayout, myToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        myDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        homeFragment = new HomeFragment();
        //Bottom Navigation
        bottomNavigationView = findViewById(R.id.bottom_nav_view);
        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, homeFragment).commit();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottomNavHome:
                        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new HomeFragment()).commit();
                        return true;
                    case R.id.bottomNavRuns:
                        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new RunsFragment()).commit();
                        return true;
                    case R.id.bottomNavList:
                        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new ListFragment()).commit();
                        return true;
                    case R.id.bottomNavBadges:
                        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new BadgesFragment()).commit();
                        return true;
                }
                return false;
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        String token = sharedPref.getString("token", "");
        Log.d("Auth", "Token from Main: " + token);

        if(token == "") {
            sendToLoginActivity();
        }

    }

    private void sendToLoginActivity() {
        //To send user to Login Activity
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }

    @SuppressWarnings("deprecation")
    public static void clearCookies(Context context)
    {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Log.d("Logout", "Using clearCookies code for API >=" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else
        {
            Log.d("Logout", "Using clearCookies code for API <" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieSyncManager cookieSyncMngr=CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager=CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }

    public static SharedPreferences getSharedPref() {
        return sharedPref;
    }

    public static OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }


    //Managing ListFragment data, probably not the best way to do this
    public static void setListLoaded(boolean listLoaded) {
        MainActivity.listLoaded = listLoaded;
    }
    public static boolean isListLoaded() {
        return listLoaded;
    }
    public static void setJsonString(String jsonString) {
        MainActivity.jsonString = jsonString;
    }
    public static String getJsonString() {
        return jsonString;
    }


}







