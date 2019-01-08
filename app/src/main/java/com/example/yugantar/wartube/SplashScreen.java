package com.example.yugantar.wartube;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;



public class SplashScreen extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Handler handler=new Handler();
        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(this);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!sharedPreferences.getString(getString(R.string.Email_for_SharedPreferences),"").isEmpty()) {
                    startActivity(new Intent(SplashScreen.this, SecondActivity.class));
//                    Log.d("HHHHHHHHHHHHHHHHHHHHH",sharedPreferences.getString(getString(R.string.Email_for_SharedPreferences),""));
                    finish();
                }
                else {

//                    Log.d("KKKKKKKKKKK",sharedPreferences.getString(getString(R.string.Email_for_SharedPreferences),"")+"123");
                    startActivity(new Intent(SplashScreen.this,MainActivity.class));
                    finish();
                }
                 }
        },3000);
    }
}
