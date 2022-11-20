package com.sweng411.smashrun;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Protocol;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

//gVN65rU@c5tc
public class MainActivity extends AppCompatActivity {

    private Button logout;
    private Button get_test;

    private  Toolbar myToolbar;
    private DrawerLayout myDrawerLayout;
    private BottomNavigationView bottomNavigationView;
    private HomeFragment homeFragment = new HomeFragment();
    private RunsFragment runsFragment = new RunsFragment();
    private ListFragment listFragment = new ListFragment();
    private BadgesFragment badgesFragment = new BadgesFragment();
    private static List<Protocol> protocols = new ArrayList<>();
    private static OkHttpClient okHttpClient;

    private static SharedPreferences sharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = getSharedPreferences("com.sweng411.smashrun", Context.MODE_PRIVATE);
        setContentView(R.layout.activity_main);

        //List<Protocol>protocols = new ArrayList<Protocol>();
        protocols.add(Protocol.HTTP_1_1);
        okHttpClient = new OkHttpClient.Builder().protocols(protocols).build();

        //logout = (Button)findViewById(R.id.logout);
        //get_test = (Button)findViewById(R.id.get_test);
        //To logout from the application
//        logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                SharedPreferences.Editor editor = sharedPref.edit();
//                editor.putString("token", "");
//                editor.putString("auth", "");
//                editor.apply();
//                //This is to make it so you have to re input your login stuff on their webpage, mostly for testing purposes
//                clearCookies(view.getContext());
//                sendToLoginActivity();
//            }
//        });

        //This is to test the get request
//        get_test.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                getTest();
//            }
//        });



        myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        myDrawerLayout = findViewById(R.id.drawer_layout);

        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, myDrawerLayout, myToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        myDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        bottomNavigationView = findViewById(R.id.bottom_nav_view);
        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, homeFragment).commit();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
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


//    public void getTest(){
//        String url = "https://api.smashrun.com/v1/my/activities";
//        String token = sharedPref.getString("token", "");
//        String auth = sharedPref.getString("auth", "");
//        String response = "";
//        okhttp3.Request request = new okhttp3.Request.Builder()
//                .url(url)
//                .addHeader("Authorization", "Bearer " + token)
//                //.addHeader("Authorization", "Basic " + auth)
//                .build();
//        Log.d("built request", "success");
//        Log.d("request", request.toString());
//        okHttpClient.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                e.printStackTrace();
//                Log.d("onFailure", "failure");
//            }
//
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                Log.d("onResponse", "entered");
//                if (response.isSuccessful()) {
//                    Log.d("onResponse", "success");
//                    final String myResponse = response.body().string();
//                    Log.d("Response", myResponse);
//
//                    MainActivity.this.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Log.d("Response", myResponse);
//                        }
//                    });
//                }
//            }
//        });
//
//    }
        //return response;

}







