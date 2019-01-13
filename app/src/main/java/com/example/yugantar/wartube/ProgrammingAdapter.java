package com.example.yugantar.wartube;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import java.util.List;

public class ProgrammingAdapter extends RecyclerView.Adapter<ProgrammingAdapter.ProgrammingViewHolder> {
    private Context context;
    private List<Post> postList;
    private Boolean mProcessLike=false;
    private DatabaseReference mDatabaseReferenceLike;


    public ProgrammingAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public ProgrammingAdapter.ProgrammingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater=LayoutInflater.from(context);//Layout inflater converts layout XML file into its corresponding View Object
        View view =inflater.inflate(R.layout.item_list,null);
        return new ProgrammingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProgrammingAdapter.ProgrammingViewHolder programmingViewHolder, int i){

        Post post=postList.get(i);
        final String post_key=post.getId();
        programmingViewHolder.txtTitle.setText(post.getPost().trim());
        programmingViewHolder.textView.setText(post.getName().trim());
        programmingViewHolder.setLikeBtn(post_key);

        //Setting user image
        StorageReference storageReference=FirebaseStorage.getInstance().getReference("profile_images");
        final FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        storageReference.child(post.getName().trim()+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if(uri!=null) {
                    Glide.with(context).load(uri).into(programmingViewHolder.imageView);
                }
                else {
                    Glide.with(context).load(R.drawable.ic_portrait_black_24dp).into(programmingViewHolder.imageView);
                }
            }
        });

        //Setting the image that the users have uploaded on the corresponding post
        storageReference.child(post.getName().trim()+post.getId()+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
               if(uri!=null){
                   Glide.with(context).load(uri).into(programmingViewHolder.imagePost);
               }
            }
        });

        //Setting the date on the post
        if(post.getDate()!=null){
            programmingViewHolder.date.setText(post.getDate());
        }

        mDatabaseReferenceLike=FirebaseDatabase.getInstance().getReference().child("Likes");
        mDatabaseReferenceLike.keepSynced(true);

//        //setting comment on the post
//        programmingViewHolder.imagePost.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

        //Setting likes on the post
        programmingViewHolder.thums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProcessLike=true;



                mDatabaseReferenceLike.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(mProcessLike) {

                            if (dataSnapshot.child(post_key).hasChild(firebaseUser.getUid())) {
                                mDatabaseReferenceLike.child(post_key).child(firebaseUser.getUid()).removeValue();
                                mProcessLike = false;
                            } else {
                                mDatabaseReferenceLike.child(post_key).child(firebaseUser.getUid()).setValue("Random");
                                mProcessLike = false;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });





    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ProgrammingViewHolder extends RecyclerView.ViewHolder {

        TextView txtTitle,textView,tv,date;
        ImageView imageView,imagePost,comment;
        ImageButton thums;
        DatabaseReference databaseReference;
        FirebaseUser firebaseUser;

        public ProgrammingViewHolder( View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.anonymous);
            txtTitle=itemView.findViewById(R.id.txtTitle);
            imageView=itemView.findViewById(R.id.imgIcon);
            imagePost=itemView.findViewById(R.id.imgPost);
            thums=itemView.findViewById(R.id.thums);
            //TODO:add comments in the posts
            comment=itemView.findViewById(R.id.postComment);
            tv=itemView.findViewById(R.id.likes);
            date=itemView.findViewById(R.id.Date);
            databaseReference=FirebaseDatabase.getInstance().getReference().child("Likes");
            firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
            databaseReference.keepSynced(true);
        }




        public void setLikeBtn(final String post_key){

       databaseReference.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
             if(dataSnapshot.child(post_key).hasChild(firebaseUser.getUid())){
                thums.setImageResource(R.drawable.ic_thumb_up_black_24dp);
             }else {
                 thums.setImageResource(R.drawable.thump_up);
             }

               DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();

               databaseReference.child("Likes").child(post_key).addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                       int likes=(int)dataSnapshot.getChildrenCount();
                       tv.setText(Integer.toString(likes));

                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError) {

                   }
               });
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });

        }
    }
}
