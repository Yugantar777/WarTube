package com.example.yugantar.wartube;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class FragmentPosts extends Fragment {
    Context context;
    List<Post> postList = new ArrayList<>();
    RecyclerView recyclerView;
    EditText editText_post;
    Button postButton;
    DatabaseReference databaseReference1,databaseReference2;
    FirebaseUser firebaseUser;
    StorageReference storageReference;
    ProgressBar progressBar;
    TextView textView;
    private String name;
    ImageView imageView,postImage,imageSelect;
    final int PICK_IMAGE=101;
    private Uri imageUri;
    private String CHANNEL_ID = "general";
    NotificationCompat.Builder mBuilder;
    int notificationId = 101;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = inflater.getContext();
        View view = inflater.inflate(R.layout.activity_fragment_posts, null);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        textView = (TextView) view.findViewById(R.id.textViewpewdiepie);
        imageView=view.findViewById(R.id.imgIcon);
        postImage=view.findViewById(R.id.postImage);
        imageSelect=view.findViewById(R.id.imageSelect);
        storageReference = FirebaseStorage.getInstance().getReference("profile_images");

        //Custom Notification
        mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("War Tube")
                .setContentText("One new Post added")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "War Tube";
            String description = "general";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        //We need a database reference instance to read and write inside the database
        //Path is the branch of our json tree where we need to write the data


        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        final String userId=firebaseUser.getUid();


        databaseReference2=FirebaseDatabase.getInstance().getReference("Users").child(userId);


        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Users users = dataSnapshot.getValue(Users.class);
                        name = users.getName().trim();
                }
                else {
                    name="Anonymous";

                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        //Firebase push notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel("My_Notifications","My_Notifications", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }

        FirebaseMessaging.getInstance().subscribeToTopic("general")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg;
                        if (!task.isSuccessful()) {
                            msg ="Failed";
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                        }

                    }
                });



        databaseReference1 = FirebaseDatabase.getInstance().getReference("Posts");

        editText_post = (EditText) view.findViewById(R.id.editText_post);
        postButton = (Button) view.findViewById(R.id.postButton);
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);


        imageSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });






        postButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                //To remove the virtual keyboard on pressing the post button
                InputMethodManager inputManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow((null == getActivity().getCurrentFocus()) ? null : getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                String post = editText_post.getText().toString().trim();




                //To remove the text writtrn in the edit text box

                if (!TextUtils.isEmpty(post) && imageUri!=null) {

                    progressBar.setVisibility(View.VISIBLE);
                    //This is used to get the user id
                   final String id=databaseReference1.push().getKey();

                    Date today = new Date();
                    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
                    String dateToStr = format.format(today);
                    final Post post1= new Post(post,name,id,dateToStr);



                        if(imageUri!=null){
                            storageReference.child(name+id+".jpg").putFile(imageUri)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                            databaseReference1.child(id).setValue(post1);
                                            Toast.makeText(context,"Post added",Toast.LENGTH_SHORT).show();
                                            editText_post.setText("");
                                            progressBar.setVisibility(View.INVISIBLE);
                                            postImage.setImageResource(0);

                                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

                                            // notificationId is a unique int for each notification that you must define
                                            notificationManager.notify(notificationId, mBuilder.build());
                                        }
                                    });
                        }

                    //Set value method is used to write the data inside the database
                    //Using custom Java class(Post) will automatically ensures that data is written inside the
                    //database in nested tree form
                    //setvalue() method overwrites the data written at the specified location
                    //taking an object at once


                } else {
                    Toast.makeText(context, "Add both Text and Image in the Post", Toast.LENGTH_SHORT).show();
                    editText_post.setText("");
                    postImage.setImageResource(0);
                }
            }
        });


        //To read data from the database and set on the recycler view
        getdata();

        return view;
    }

    public void getdata() {
        progressBar.setVisibility(View.VISIBLE);
        //ValueEventListener is used to read data and listen for changes at a path
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            //On data change method is used to read static snapshot of the contents at a given
            //path,as they existed at the time of event
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    postList.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Post post = postSnapshot.getValue(Post.class);
                        postList.add(post);
                    }

                Collections.reverse(postList);

                ProgrammingAdapter programmingAdapter = new ProgrammingAdapter(context, postList);
                recyclerView.setAdapter(programmingAdapter);
                progressBar.setVisibility(View.GONE);

            }

            @Override

            //onCancelled method is called when data retrieving is failed
            public void onCancelled(@NonNull DatabaseError databaseError) {

                // Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                Toast.makeText(context, "Error in fetching data", Toast.LENGTH_SHORT).show();
            }

        });

    }

    public void openGallery()
    {
        Intent gallery=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery,PICK_IMAGE);
    }
    //This method will be called when we pick our file
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==PICK_IMAGE && data!=null && data.getData()!=null){
            imageUri=data.getData();
            postImage.setImageURI(imageUri);


        }
    }





}