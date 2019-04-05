package com.example.yugantar.wartube;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.yugantar.wartube.activity.FragmentStats;



public class SecondActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    SharedPreferences sharedPreferences;
    MainActivity obj=new MainActivity();
    static int i = 0;
    ImageView imageView;

    String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alternate);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(this);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {



                switch (menuItem.getItemId()) {

                    case R.id.nav_logout: {

                        Intent intent = new Intent(SecondActivity.this, MainActivity.class);
                        sharedPreferences.edit().remove(getString(R.string.Email_for_SharedPreferences)).apply();
                        startActivity(intent);
                        finish();
                    }
                    break;

                    case R.id.nav_profile:{
                        Intent intent=new Intent(SecondActivity.this,Profile_Activity.class);
                        startActivity(intent);
                        finish();
                    }
                    break;

//                    case R.id.nav_stop:{
//                        if(obj.mediaPlayer.isPlaying()) {
//                            obj.mediaPlayer.pause();
//                        }
//                        else if(obj.mediaPlayer==null){
//                            Toast.makeText(SecondActivity.this, "No Music Playing", Toast.LENGTH_SHORT).show();
//                        }
//
//                    }
//                    break;
//
//                    case R.id.nav_play:{
//                        if(!obj.mediaPlayer.isPlaying()) {
//                            obj.mediaPlayer.start();
//                        }
//                        else if(obj.mediaPlayer==null){
//                            Toast.makeText(SecondActivity.this,"I/O",Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                    break;

                    case R.id.nav_view:{
                        Intent intent=new Intent(SecondActivity.this,Profile_View.class);
                        startActivity(intent);
                    }
                    break;


                }
                drawer.closeDrawer(GravityCompat.START);
                //Means we have selected a view
                return true;
            }
        });


        BottomNavigationView navigation = findViewById(R.id.navigation2);
        navigation.setOnNavigationItemSelectedListener(SecondActivity.this);

        FragmentPosts fragmentPosts = new FragmentPosts();
        FragmentLoad(fragmentPosts);

    }



    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(!drawer.isDrawerOpen(GravityCompat.START) && i<1){
                Toast.makeText(SecondActivity.this, "Press again to exit", Toast.LENGTH_SHORT).show();
                i++;
                }
                else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;
        switch (menuItem.getItemId()) {
            case R.id.navigation_posts2:
                fragment = new FragmentPosts();
                break;
            case R.id.navigation_stats2:
                fragment = new FragmentStats();
                break;
        }
        return FragmentLoad(fragment);
    }

    //This method will set the default fragment and also set the fragment depending upon the user clicking the bottomnavigation view
    private boolean FragmentLoad(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).commit();
            return true;
        }

        return false;
    }



}
