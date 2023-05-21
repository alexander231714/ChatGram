package com.loschidos.chatgram.activities;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.loschidos.chatgram.R;
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

    CircleImageView mCircleImageViewBack;

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
        mImageCover = findViewById(R.id.ImageViewCover);
        mCircleImageViewBack =findViewById(R.id.circleImageBack);


        mPostProvider =new PostProvider();
        mUserProvider = new UserProvider();
        mAuthProvider = new AuthProvider();

        mExtraidUser= getIntent().getStringExtra("idUser");

        getUser();
        getPostNumber();
        mCircleImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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

}
