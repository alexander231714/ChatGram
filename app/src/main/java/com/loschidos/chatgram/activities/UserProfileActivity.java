package com.loschidos.chatgram.activities;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.loschidos.chatgram.R;
import com.loschidos.chatgram.adapters.MyPostsAdapter;
import com.loschidos.chatgram.models.Post;
import com.loschidos.chatgram.providers.AuthProvider;
import com.loschidos.chatgram.providers.PostProvider;
import com.loschidos.chatgram.providers.UserProvider;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {
    LinearLayout mLinerLayoutEditProfile;
    TextView mTextViewUserName, mTextViewPhone, mTextViewEmail, mTextViewPost;
    ImageView mImageCover;
    CircleImageView mImageProfile;
    UserProvider mUserProvider;
    AuthProvider mAuthProvider;
    PostProvider mPostProvider;
    String mExtraidUser;
    MyPostsAdapter mAdapter ;
    RecyclerView mRecyclerView;
    TextView mTextViewPostExist;
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mLinerLayoutEditProfile = findViewById(R.id.linerLayoutEditProfile);
        mTextViewEmail = findViewById(R.id.TextViewEmail);
        mTextViewUserName = findViewById(R.id.TextViewUsername);
        mTextViewPhone = findViewById(R.id.TextViewPhone);
        mTextViewPost = findViewById(R.id.TextViewPost);
        mImageProfile = findViewById(R.id.CircleImageProfile);
        mTextViewPostExist = findViewById(R.id.textViewPostExist);
        mImageCover = findViewById(R.id.ImageViewCover);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = findViewById(R.id.recyclerViewMyPost);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(UserProfileActivity.this);
        mRecyclerView.setLayoutManager(linearLayoutManager);


        mPostProvider =new PostProvider();
        mUserProvider = new UserProvider();
        mAuthProvider = new AuthProvider();

        mExtraidUser= getIntent().getStringExtra("IdUser");



        getUser();
        getPostNumber();
        checkIfExistPost();
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = mPostProvider.getPostByUser(mExtraidUser);
        FirestoreRecyclerOptions<Post> options =
                new FirestoreRecyclerOptions.Builder<Post>()
                        .setQuery(query, Post.class )
                        .build();

        mAdapter = new MyPostsAdapter(options, UserProfileActivity.this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    private void checkIfExistPost() {
        mPostProvider.getPostByUser(mExtraidUser).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                int numberPost = queryDocumentSnapshots.size();
                if(numberPost >0){
                    mTextViewPostExist.setText("Publicaciones ");
                    mTextViewPostExist.setTextColor(Color.BLUE);
                }else {
                    mTextViewPostExist.setText("No hay Publicaciones ");
                    mTextViewPostExist.setTextColor(Color.GRAY);
                }
            }
        });
    }

    private void getPostNumber(){
        mPostProvider.getPostByUser(mExtraidUser).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int PostNum = queryDocumentSnapshots.size();
                mTextViewPost.setText(String.valueOf(PostNum));
            }
        });
    }
    private void getUser() {
        mUserProvider.getUser(mExtraidUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("email")) {
                        String email = documentSnapshot.getString("email");
                        mTextViewEmail.setText(email);
                    }
                }

                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("telefono")) {
                        String phone = documentSnapshot.getString("telefono");
                        mTextViewPhone.setText(phone);
                    }
                }

                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("username")) {
                        String username = documentSnapshot.getString("username");
                        mTextViewUserName.setText(username);
                    }
                }

                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("image_profile")) {
                        String imageProfile = documentSnapshot.getString("image_profile");
                        if (imageProfile != null) {
                            if (!imageProfile.isEmpty()) {
                                Picasso.with(UserProfileActivity.this).load(imageProfile).into(mImageProfile);
                            }
                        }
                    }
                }

                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("image_cover")) {
                        String imageCover = documentSnapshot.getString("image_cover");
                        if (imageCover != null) {
                            if (!imageCover.isEmpty()) {
                                Picasso.with(UserProfileActivity.this).load(imageCover).into(mImageCover);
                            }
                        }
                    }
                }

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return true;
    }
}
