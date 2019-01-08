package com.example.yugantar.wartube;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Signup extends AppCompatActivity implements View.OnClickListener {
    EditText editTextEmail,editTextPassword,editTextUsername;
    Button button;
    ProgressBar progressBar;
    SharedPreferences sharedPreferences;
    ImageView imageView;
    static final int PICK_IMAGE=100;
    Uri imageUri;
     StorageReference storageReference;
     DatabaseReference databaseReference;
     FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    Uri image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editTextEmail=(EditText)findViewById(R.id.editTextEmail);
        editTextPassword=(EditText)findViewById(R.id.editTextPassword);
        editTextUsername=(EditText) findViewById(R.id.editTextUsername);
        button=(Button)findViewById(R.id.button);
        imageView=findViewById(R.id.image);
        button.setOnClickListener(this);
        progressBar=(ProgressBar)findViewById(R.id.progressbar);

        storageReference=FirebaseStorage.getInstance().getReference("profile_images");


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mAuth = FirebaseAuth.getInstance();
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

    public void GotoLogin(View view) {
        Intent intent=new Intent(this,Login.class);
        startActivity(intent);
        finish();
    }



    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.button)
        {
            registerUser();
        }

    }

    private void registerUser() {


        //To remove the virtual keyboard on pressing the Signup button
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        final String email=editTextEmail.getText().toString().trim();
        final String password=editTextPassword.getText().toString().trim();
        final String name=editTextUsername.getText().toString().trim();

        //Shared Preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.Email_for_SharedPreferences), email);
        editor.apply();




        if(email.isEmpty()){
            editTextEmail.setError("Email address is required");
            editTextEmail.requestFocus();//So that it will show error on the email field
            return;//To get out of the method if the validation fails
        }

        if(! Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Please enter a valid Email");
            editTextEmail.requestFocus();
            return;
        }

        if(password.isEmpty()){
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();//So that it will show error on the password field
            return;
        }

        if(password.length()<6){
            editTextPassword.setError("Minimum length of the password should be 6");
            editTextPassword.requestFocus();
            return;
        }

        if(name.isEmpty()){
            editTextUsername.setError("Username is Required");
            editTextUsername.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                     if(task.isSuccessful())
                     {
                         firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
                         String userId=firebaseUser.getUid();
                         //UserId is working fine
                         UsersSet(name,userId);

                         }
                     else
                     {
                         progressBar.setVisibility(View.GONE);
                         if(task.getException() instanceof FirebaseAuthUserCollisionException){
                             Toast.makeText(getBaseContext(),"Email already registered",Toast.LENGTH_SHORT).show();
                         }
                         else{
                             Toast.makeText(getBaseContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                         }
                     }

                    }
                });






    }

    public void UsersSet(final String names, final String userId){
        databaseReference=FirebaseDatabase.getInstance().getReference();
        if (imageUri !=null) {
            final StorageReference storageReference1 = storageReference.child(names+ ".jpg");
            storageReference1.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });


        }

        Users users=new Users(names);
        databaseReference.child("Users").child(userId).setValue(users);
        progressBar.setVisibility(View.GONE);
        Toast.makeText(getApplicationContext(),"User registration successful",Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(Signup.this,SecondActivity.class);
        startActivity(intent);
        finish();

    }

}