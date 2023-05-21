package com.loschidos.chatgram.activities;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.loschidos.chatgram.R;
import com.loschidos.chatgram.adapters.SliderAdapter;
import com.loschidos.chatgram.models.SliderItem;
import com.loschidos.chatgram.providers.PostProvider;
import com.loschidos.chatgram.providers.UserProvider;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class PostDetailActivity extends AppCompatActivity {

    SliderView mSliderView;
    SliderAdapter mSliderAdapter;
    List<SliderItem>mSliderItems = new ArrayList<>();
    PostProvider mPostProvider;
    UserProvider mUserProvider;
    String mExtraPostId;
    TextView mTextViewTitle, mTextViewDescription, mTextViewUsername, mTextViewPhone, mTextViewNameCategory;
    ImageView mImageViewCategory;
    CircleImageView mCircleImageViewProfile;
    Button mButtonShowProfile;
    CircleImageView mCircleImageViewBack;
    String midUser = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        mSliderView=findViewById(R.id.imageSlider);

        mTextViewTitle=findViewById(R.id.textViewTitle);
        mTextViewDescription=findViewById(R.id.textViewDescrition);
        mTextViewUsername=findViewById(R.id.TextViewUsername);
        mTextViewPhone=findViewById(R.id.TextViewPhone);
        mTextViewNameCategory=findViewById(R.id.textViewNameCategory);

        mImageViewCategory=findViewById(R.id.imageViewCategory);

        mCircleImageViewProfile=findViewById(R.id.circleImageProfile);

        mButtonShowProfile=findViewById(R.id.btnShowProfile);
        mCircleImageViewBack =findViewById(R.id.circleImageBack);

        mPostProvider = new PostProvider();
        mUserProvider = new UserProvider();

        mExtraPostId = getIntent().getStringExtra("id");

        getPost();


        mCircleImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mButtonShowProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToShowProfile();
            }
        });

    }

    private void goToShowProfile() {
        if(!midUser.equals("")){
            Intent intent = new Intent(PostDetailActivity.this, UserProfileActivity.class);
            intent.putExtra("idUser",midUser);
            startActivity(intent);
        }
else {
            Toast.makeText(this, "El ID del usuario aun no se carga", Toast.LENGTH_SHORT).show();
        }
    }

    private  void  instanceSlider(){
        mSliderAdapter= new SliderAdapter(PostDetailActivity.this, mSliderItems);
        mSliderView.setSliderAdapter(mSliderAdapter);
        mSliderView.setIndicatorAnimation(IndicatorAnimationType.THIN_WORM);
        mSliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        mSliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        mSliderView.setIndicatorSelectedColor(Color.WHITE);
        mSliderView.setIndicatorUnselectedColor(Color.GRAY);
        mSliderView.setScrollTimeInSec(2);
        mSliderView.setAutoCycle(true);
        mSliderView.startAutoCycle();
    }

    private void getPost(){
        mPostProvider.getPostById(mExtraPostId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("img1")){
                        String img1 = documentSnapshot.getString("img1");
                        SliderItem item = new SliderItem();
                        item.setImageUrl(img1);
                        mSliderItems.add(item);
                    }
                    if(documentSnapshot.contains("img2")){
                        String img2 = documentSnapshot.getString("img2");
                        SliderItem item = new SliderItem();
                        item.setImageUrl(img2);
                        mSliderItems.add(item);
                    }
                    if(documentSnapshot.contains("titulo")){
                        String titulo = documentSnapshot.getString("titulo");
                        mTextViewTitle.setText(titulo.toUpperCase());
                    }
                    if(documentSnapshot.contains("descripcion")){
                        String descripcion = documentSnapshot.getString("descripcion");
                        mTextViewDescription.setText(descripcion);
                    }
                    if(documentSnapshot.contains("categoria")){
                        String categoria = documentSnapshot.getString("categoria");
                        mTextViewNameCategory.setText(categoria);

                        if(categoria.equals("DEPORTE")){
                            mImageViewCategory.setImageResource(R.drawable.deportes);
                        }
                        else if(categoria.equals("JUEGOS")){
                            mImageViewCategory.setImageResource(R.drawable.juegos);
                        }
                        else if(categoria.equals("MUSICA")){
                            mImageViewCategory.setImageResource(R.drawable.musica);
                        }
                        else if(categoria.equals("PELICULAS")){
                            mImageViewCategory.setImageResource(R.drawable.pelis);
                        }
                    }
                    if(documentSnapshot.contains("idUsuario")){
                       midUser = documentSnapshot.getString("idUsuario");
                        getUserInfo(midUser);
                    }
                    instanceSlider();
                }
            }
        });
    }

    private void getUserInfo(String idUsuario) {
        mUserProvider.getUser(idUsuario).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("username")){
                        String username = documentSnapshot.getString("username");
                        mTextViewUsername.setText(username);
                    }
                    if(documentSnapshot.contains("telefono")){
                        String telefono = documentSnapshot.getString("telefono");
                        mTextViewPhone.setText(telefono);
                    }
                    if(documentSnapshot.contains("image_profile")){
                        String imageProfile = documentSnapshot.getString("image_profile");
                        Picasso.with(PostDetailActivity.this).load(imageProfile).into(mCircleImageViewProfile);
                    }
                }
            }
        });
    }
}