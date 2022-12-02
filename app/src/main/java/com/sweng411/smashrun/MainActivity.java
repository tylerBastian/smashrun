package com.sweng411.smashrun;

import static java.lang.Thread.sleep;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Protocol;
import okhttp3.OkHttpClient;
import okhttp3.Response;

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
    private static String allActivitiesJsonString;
    private static String yearlyStatsJsonString;
    private boolean activitiesLoaded = false;
    private boolean yearlyStatsLoaded = false;
    private static Calendar calendar;
    private static int year;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        sharedPref = getSharedPreferences("com.sweng411.smashrun", Context.MODE_PRIVATE);
        setContentView(R.layout.activity_main);

        protocols.add(Protocol.HTTP_1_1);
        okHttpClient = new OkHttpClient.Builder().protocols(protocols).build();


        //Call api, load activities into json string


    }

    @Override
    protected void onStart() {
        super.onStart();
        String token = sharedPref.getString("token", "");
        Log.d("Auth", "Token from Main: " + token);

        if (token.equals("")) {
            sendToLoginActivity();
        }

        getAllActivities();
        getYearlyStats();

        //Toolbar and Navigation Drawer
        myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        myDrawerLayout = findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, myDrawerLayout, myToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        myDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //Inflate menu so side navigation drawer buttons can be referenced in onCreate of initial launch
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        getMenuInflater().inflate(R.menu.side_nav_drawer, menu);
        menu.findItem(R.id.logout_drawer).setEnabled(true);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.logout_drawer:
                        logout(findViewById(R.id.logout_drawer));
                        break;
                }
                return true;
            }
        });

        navigationView.getMenu().findItem(R.id.logout_drawer).setOnMenuItemClickListener(menuItem -> {
            logout(findViewById(myDrawerLayout.getId()));
            return true;
        });


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
                    case R.id.bottomNavAddRun:
                        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new AddRunFragment()).commit();
                        return true;
                    case R.id.bottomNavBadges:
                        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new BadgesFragment()).commit();
                        return true;
                    case R.id.bottomNavProfile:
                        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new ProfileFragment()).commit();
                        return true;
                }
                return false;
            }
        });

    }



    private void sendToLoginActivity() {
        Log.d("Main", "Sending To Login Activity");
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


    private void getAllActivities(){
        String url = "https://api.smashrun.com/v1/my/activities";
        String token = getSharedPref().getString("token", "");
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        Log.d("built request", "success");
        Log.d("request", request.toString());

        getOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                Log.d("onFailure", "failure");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d("onResponse", "entered");
                if (response.isSuccessful()) {
                    Log.d("onResponse", "success");
                    setAllActivitiesJsonString(response.body().string());
                    activitiesLoaded = true;
                    Log.d("Response", getAllActivitiesJsonString());
                    Log.d("returnString", getAllActivitiesJsonString());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("Response", getAllActivitiesJsonString() + " in run");
                        }
                    });
                }
            }
        });
    }

    //Get yearly stats
    private void getYearlyStats(){
        String url = "https://api.smashrun.com/v1/my/stats/" + String.valueOf(getYear());
        String token = getSharedPref().getString("token", "");
        String auth = getSharedPref().getString("auth", "");
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .build();
        Log.d("built request", "success");
        Log.d("request", request.toString());
        getOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                Log.d("onFailure", "failure");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d("onResponse", "entered");
                if (response.isSuccessful()) {
                    Log.d("onResponse", "success");
                    setYearlyStatsJsonString(response.body().string());
                    yearlyStatsLoaded = true;
                    Log.d("Response", getYearlyStatsJsonString());
                    Log.d("returnString", getYearlyStatsJsonString());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("Response", getAllActivitiesJsonString() + " in run");
                        }
                    });
                }
            }
        });
    }


    //Managing ListFragment data, probably not the best way to do this
    public static void setListLoaded(boolean listLoaded) {
        MainActivity.listLoaded = listLoaded;
    }
    public static boolean isListLoaded() {
        return listLoaded;
    }
    public static void setAllActivitiesJsonString(String allActivitiesJsonString) {
        MainActivity.allActivitiesJsonString = allActivitiesJsonString;
    }
    public static void setYearlyStatsJsonString(String yearlyStatsJsonString) {
        MainActivity.yearlyStatsJsonString = yearlyStatsJsonString;
    }
    public static String getAllActivitiesJsonString() {
        return allActivitiesJsonString;
    }
    public static String getYearlyStatsJsonString() {
        return yearlyStatsJsonString;
    }

    public static int getYear() {
        return year;
    }

    public void logout(View view) {
        SharedPreferences.Editor editor = getSharedPref().edit();
        editor.putString("token", "");
        editor.putString("auth", "");
        editor.apply();
        //This is to make it so you have to re input your login stuff on their webpage, mostly for testing purposes
        clearCookies(view.getContext());
        sendToLoginActivity();
    }


}







