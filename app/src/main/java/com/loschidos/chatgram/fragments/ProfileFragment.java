package com.loschidos.chatgram.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.loschidos.chatgram.R;
import com.loschidos.chatgram.activities.EditProfileActivity;
import com.loschidos.chatgram.providers.AuthProvider;
import com.loschidos.chatgram.providers.PostProvider;
import com.loschidos.chatgram.providers.UserProvider;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    LinearLayout mLinerLayoutEditProfile;
    View mView;
    UserProvider mUserProvider;
    AuthProvider mAuthProvider;

    TextView mTextViewUserName;
    TextView mTextViewPhone;
    TextView mTextViewEmail;
    TextView mTextViewPost;
    ImageView mImageCover;
    CircleImageView mImageProfile;

    PostProvider mPostProvider;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_profile,container, false);
        mLinerLayoutEditProfile = mView.findViewById(R.id.linerLayoutEditProfile);
        mTextViewEmail = mView.findViewById(R.id.TextViewEmail);
        mTextViewUserName = mView.findViewById(R.id.TextViewUsername);
        mTextViewPhone = mView.findViewById(R.id.TextViewPhone);
        mTextViewPost = mView.findViewById(R.id.TextViewPost);
        mImageProfile = mView.findViewById(R.id.CircleImageProfile);
        mImageCover = mView.findViewById(R.id.ImageViewCover);

        mLinerLayoutEditProfile.setOnClickListener(view -> goToEditProfile());
        mPostProvider =new PostProvider();
        mUserProvider = new UserProvider();
        mAuthProvider = new AuthProvider();
        getUser();
        getPostNumber();
        return mView;
    }

   private void goToEditProfile(){
       Intent intent = new Intent(getContext(), EditProfileActivity.class);
       startActivity(intent);
       Log.d("DEBUG", "Actividad EditProfileActivity iniciada");
   }

   private void getPostNumber(){
mPostProvider.getPostByUser(mAuthProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
    @Override
    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
        int PostNum = queryDocumentSnapshots.size();
        mTextViewPost.setText(String.valueOf(PostNum));
    }
});
   }

   private void getUser(){
    mUserProvider.getUser(mAuthProvider.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
        @Override
        public void onSuccess(DocumentSnapshot documentSnapshot) {
            if(documentSnapshot.exists()){
                if(documentSnapshot.contains("email")){
                    String email = documentSnapshot.getString("email");
                    mTextViewEmail.setText(email);
                }
            }

            if(documentSnapshot.exists()){
                if(documentSnapshot.contains("telefono")){
                    String phone = documentSnapshot.getString("telefono");
                    mTextViewPhone.setText(phone);
                }
            }

            if(documentSnapshot.exists()){
                if(documentSnapshot.contains("username")){
                    String username = documentSnapshot.getString("username");
                    mTextViewUserName.setText(username);
                }
            }

            if(documentSnapshot.exists()){
                if(documentSnapshot.contains("image_profile")){
                    String imageProfile = documentSnapshot.getString("image_profile");
                    if(imageProfile != null){
                        if(!imageProfile.isEmpty()){
                            Picasso.with(getContext()).load(imageProfile).into(mImageProfile);
                        }
                    }
                }
            }

            if(documentSnapshot.exists()){
                if(documentSnapshot.contains("image_cover")){
                    String imageCover = documentSnapshot.getString("image_cover");
                    if(imageCover != null){
                        if(!imageCover.isEmpty()){
                            Picasso.with(getContext()).load(imageCover).into(mImageCover);
                        }
                    }
                }
            }

        }
    });
   }
}