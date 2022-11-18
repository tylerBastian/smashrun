package com.sweng411.smashrun;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebStorage;
import android.widget.Button;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {

    Button logout;

    private  Toolbar myToolbar;
    private DrawerLayout myDrawerLayout;
    private BottomNavigationView bottomNavigationView;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = getSharedPreferences("com.sweng411.smashrun", Context.MODE_PRIVATE);

        setContentView(R.layout.activity_main);

        logout = (Button)findViewById(R.id.logout);
        //To logout from the application
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("token", "");
                editor.putString("auth", "");
                editor.apply();
                //This is to make it so you have to re input your login stuff on their webpage, mostly for testing purposes
                clearCookies(view.getContext());
                sendToLoginActivity();
            }
        });

        myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        myDrawerLayout = findViewById(R.id.drawer_layout);

        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, myDrawerLayout, myToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        myDrawerLayout.addDrawerListener(toggle);

        toggle.syncState();

        bottomNavigationView = findViewById(R.id.bottom_nav_view);



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

}