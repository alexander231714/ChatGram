package com.loschidos.chatgram.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loschidos.chatgram.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {
    LinearLayout mLinerLayoutEditProfile;

    TextView mTextViewUserName, mTextViewPhone, mTextViewEmail, mTextViewPost;
    ImageView mImageCover;
    CircleImageView mImageProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
    }
}