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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

//gVN65rU@c5tc
public class MainActivity extends AppCompatActivity {

    private  Toolbar myToolbar;
    private DrawerLayout myDrawerLayout;
    private BottomNavigationView bottomNavigationView;
    private static SharedPreferences sharedPref;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = getSharedPreferences("com.sweng411.smashrun", Context.MODE_PRIVATE);
        setContentView(R.layout.activity_main);

        //Call api, load activities into json string


    }

    @Override
    protected void onStart() {
        super.onStart();
        String token = sharedPref.getString("token", "");
        Log.d("Auth", "Token from Main: " + token);

        if (token.equals("")) {
            SendToLoginActivity();
        }

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


        //Bottom Navigation
        bottomNavigationView = findViewById(R.id.bottom_nav_view);
        //Initial Fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new HomeFragment()).commit();

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
                        getSupportFragmentManager().beginTransaction().add(R.id.nav_host_fragment, new AddRunFragment()).commit();
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



    private void SendToLoginActivity() {
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


    public void logout(View view) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("token", "");
        editor.putString("auth", "");
        editor.apply();
        //This is to make it so you have to re input your login stuff on their webpage, mostly for testing purposes
        clearCookies(view.getContext());
        SendToLoginActivity();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the main_menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refreshBtn:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}







