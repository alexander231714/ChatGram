package com.loschidos.chatgram.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.loschidos.chatgram.R;
import com.loschidos.chatgram.adapters.SliderAdapter;
import com.loschidos.chatgram.models.SliderItem;
import com.loschidos.chatgram.providers.PostProvider;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;


public class PostDetailActivity extends AppCompatActivity {

    SliderView mSliderView;
    SliderAdapter mSliderAdapter;
    List<SliderItem>mSliderItems = new ArrayList<>();
    PostProvider mPostProvider;
    String mExtraPostId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        mSliderView=findViewById(R.id.imageSlider);
        mPostProvider = new PostProvider();

        mExtraPostId = getIntent().getStringExtra("id");

        getPost();
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
                    instanceSlider();
                }
            }
        });
    }
}