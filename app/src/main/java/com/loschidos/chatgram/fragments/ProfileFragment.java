package com.loschidos.chatgram.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.loschidos.chatgram.R;
import com.loschidos.chatgram.activities.EditProfileActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    LinearLayout mLinerLayoutEditProfile;
    View mView;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_profile,container, false);
        mLinerLayoutEditProfile = mView.findViewById(R.id.linerLayoutEditProfile);

        mLinerLayoutEditProfile.setOnClickListener(view -> goToEditProfile());
        return mView;
    }

   private void goToEditProfile(){
       Intent intent = new Intent(getContext(), EditProfileActivity.class);
       startActivity(intent);
       Log.d("DEBUG", "Actividad EditProfileActivity iniciada");
   }
}