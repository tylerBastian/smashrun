package com.sweng411.smashrun;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

public class MainActivity extends AppCompatActivity {
    FirebaseUser currentUser;//used to store current user of account
    FirebaseAuth mAuth;//Used for firebase authentication
    Button logout;

    private  Toolbar myToolbar;
    private DrawerLayout myDrawerLayout;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        logout = (Button)findViewById(R.id.logout);
        //To logout from the application
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                //After logging out send user to Login Activity to login again
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
        //Check if user has already signed in if not send user to Login Activity
        if(currentUser==null)
        {
            sendToLoginActivity();
        }
    }

    private void sendToLoginActivity() {
        //To send user to Login Activity
        Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(loginIntent);
    }
}