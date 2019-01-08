package com.example.yugantar.wartube;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;


import java.util.ArrayList;

import java.util.Collections;
import java.util.List;

import static android.support.constraint.Constraints.TAG;


public class FragmentPosts extends Fragment {
    Context context;
    List<Post> postList = new ArrayList<>();
    RecyclerView recyclerView;
    EditText editText_post;
    Button postButton;
    DatabaseReference databaseReference1,databaseReference2;
    FirebaseUser firebaseUser;
    ProgressBar progressBar;
    TextView textView;
    private String name;
    ImageView imageView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = inflater.getContext();
        View view = inflater.inflate(R.layout.activity_fragment_posts, null);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        textView = (TextView) view.findViewById(R.id.textViewpewdiepie);
        imageView=view.findViewById(R.id.imgIcon);

        //We need a database reference instance to read and write inside the database
        //Path is the branch of our json tree where we need to write the data


        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        String userId=firebaseUser.getUid();


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



        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //To remove the virtual keyboard on pressing the post button
                InputMethodManager inputManager = (InputMethodManager)
                        getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow((null == getActivity().getCurrentFocus()) ? null : getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                String post = editText_post.getText().toString().trim();



                //To remove the text writtrn in the edit text box
                editText_post.setText("");
                if (!TextUtils.isEmpty(post)) {   //This is used to get the user id
                    String id=databaseReference1.push().getKey();
                    Post post1= new Post(post,name,id);
                    //Set value method is used to write the data inside the database
                    //Using custom Java class(Post) will automatically ensures that data is written inside the
                    //database in nested tree form
                    //setvalue() method overwrites the data written at the specified location
                    //taking an object at once
                    databaseReference1.child(id).setValue(post1);


                    Toast.makeText(context, "Post added", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Post cannot be empty", Toast.LENGTH_SHORT).show();
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



}