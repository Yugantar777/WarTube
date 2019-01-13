package com.example.yugantar.wartube;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yugantar.wartube.activity.FragmentStats;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    static MediaPlayer mediaPlayer;
    static int count = 0;
    static int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
        if (count == 0) {
            mediaPlayer = MediaPlayer.create(this, R.raw.bitch_lasagna_2);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
        count++;


        //We have to set a default fragment for our frame layout which is the fragment "Stats" in this case

        FragmentStats fragmentStats = new FragmentStats();
        FragmentLoad(fragmentStats);
    }

    @Override
    public void onBackPressed() {
        if (i < 1) {
            Toast.makeText(MainActivity.this, "Press again to exit", Toast.LENGTH_SHORT).show();
            i++;
        } else {
            super.onBackPressed();
        }
    }

    //This method will set the default fragment and also set the fragment depending upon the user clicking the icons
    private boolean FragmentLoad(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).commit();
            return true;
        }

        return false;
    }

    // This method calls the method for displaying fragment depending upon user's clicking the icons on the bottom navigation bar
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        Fragment fragment = null;

        switch (menuItem.getItemId()) {
            case R.id.navigation_stats:
                fragment = new FragmentStats();
                break;
            case R.id.navigation_posts: {
                Intent intent = new Intent(this, Login.class);
                startActivity(intent);
                }
            break;

        }
        //Calls the FragmentLoad method for displaying or setting the fragment
        return FragmentLoad(fragment);
    }


}
