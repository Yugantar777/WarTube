package com.example.yugantar.wartube;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Profile_View extends AppCompatActivity {

    ImageView imageView2;
    TextView textView1, textView2;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference2;
    StorageReference storageReference;
    private String name;
    Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_view);

        imageView2 = findViewById(R.id.image_profile);
        textView1 = findViewById(R.id.profile_name);
        textView2 = findViewById(R.id.profile_email);
        context=getApplicationContext();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = firebaseUser.getUid();
        final String getEmail = firebaseUser.getEmail().trim();
        databaseReference2 = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    Users users = dataSnapshot.getValue(Users.class);
                    name = users.getName().trim();

                    textView1.setText(name);

                    storageReference=FirebaseStorage.getInstance().getReference("profile_images");
                    storageReference.child(name+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            if (uri != null) {
                                Glide.with(context).load(uri).into(imageView2);
                            } else {
                                Glide.with(context).load(R.drawable.ic_portrait_black_24dp).into(imageView2);
                            }
                        }
                    });

                } else {

                    name = "Anonymous";
                    textView1.setText(name);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        textView2.setText(getEmail);




    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, SecondActivity.class);
        startActivity(intent);
        finish();
    }
}
