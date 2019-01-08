package com.example.yugantar.wartube;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Profile_Activity extends AppCompatActivity {
    ImageView imageView;
    Uri imageUri;
    Button button;
    EditText textName;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    String userId;
    ProgressBar progressBar;
//    FragmentPosts obj=new FragmentPosts();
    static final int PICK_IMAGE=100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imageView=findViewById(R.id.imageView);
        button=findViewById(R.id.buttonSave);
        progressBar=findViewById(R.id.progressbar);
        textName=(EditText) findViewById(R.id.textName);

        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        userId=firebaseUser.getUid();
        storageReference=FirebaseStorage.getInstance().getReference("profile_images");

        databaseReference=FirebaseDatabase.getInstance().getReference("Users").child(userId);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });





        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (imageUri!=null) {
                    final StorageReference storageReference1 = storageReference.child(textName.getText().toString()+".jpg");
                             progressBar.setVisibility(View.VISIBLE);
                    storageReference1.putFile(imageUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Users users=new Users(textName.getText().toString().trim());
                                        databaseReference.setValue(users);
                                        progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(Profile_Activity.this,"Successful",Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnCanceledListener(new OnCanceledListener() {
                                @Override
                                public void onCanceled() {
                                  Toast.makeText(Profile_Activity.this,"Unsuccessful",Toast.LENGTH_SHORT).show();
                                }
                            });

                }
            }
        });



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(this,SecondActivity.class);
        startActivity(intent);
        finish();
    }

    public void openGallery()
    {
        Intent gallery=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery,PICK_IMAGE);
    }
    //This method will be called when we pick our file
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK && requestCode==PICK_IMAGE && data!=null && data.getData()!=null){
            imageUri=data.getData();
            imageView.setImageURI(imageUri);


        }
    }

    public String getFileExtension(Uri uri){
        ContentResolver cR=getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}
