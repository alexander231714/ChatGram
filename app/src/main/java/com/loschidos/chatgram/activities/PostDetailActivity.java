package com.loschidos.chatgram.activities;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.loschidos.chatgram.R;
import com.loschidos.chatgram.adapters.CommentAdapter;
import com.loschidos.chatgram.adapters.PostsAdapter;
import com.loschidos.chatgram.adapters.SliderAdapter;
import com.loschidos.chatgram.models.Comment;
import com.loschidos.chatgram.models.Post;
import com.loschidos.chatgram.models.SliderItem;
import com.loschidos.chatgram.providers.AuthProvider;
import com.loschidos.chatgram.providers.CommentsProvider;
import com.loschidos.chatgram.providers.LikesProvider;
import com.loschidos.chatgram.providers.PostProvider;
import com.loschidos.chatgram.providers.UserProvider;
import com.loschidos.chatgram.utils.RelativeTime;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
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
    CommentsProvider mcommentsProvider;
    AuthProvider mAuthProvider;
    RecyclerView mRecyclerView;
    CommentAdapter mAdapter;
FloatingActionButton mFabComment;
    TextView mTextViewRelativeTime;
    TextView mTextViewLikes;
   LikesProvider mLikesProvider;


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
        mFabComment =findViewById(R.id.fabComment);
        mRecyclerView=findViewById(R.id.recyclerViewComments);
        mTextViewRelativeTime =findViewById(R.id.textRelativeTime);
        mTextViewLikes = findViewById(R.id.textLikes);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PostDetailActivity.this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mPostProvider = new PostProvider();
        mUserProvider = new UserProvider();
        mcommentsProvider = new CommentsProvider();
        mAuthProvider = new AuthProvider();
        mLikesProvider = new LikesProvider();

        mExtraPostId = getIntent().getStringExtra("id");


        mFabComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDialogComment();
            }
        });

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

        getPost();
        getNumberLikes();
    }

    private void getNumberLikes() {
        mLikesProvider.getLikesByPost(mExtraPostId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                int numberLikes = queryDocumentSnapshots.size();
                if (numberLikes==1){
                    mTextViewLikes.setText(numberLikes + "Me gusta");
                }
                else {
                    mTextViewLikes.setText(numberLikes + "Me gustas");
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Query query = mcommentsProvider.getCommentsByPost(mExtraPostId);
        FirestoreRecyclerOptions<Comment> options =
                new FirestoreRecyclerOptions.Builder<Comment>()
                        .setQuery(query, Comment.class )
                        .build();

        mAdapter = new CommentAdapter(options, PostDetailActivity.this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    private void ShowDialogComment() {
        AlertDialog.Builder alert = new AlertDialog.Builder(PostDetailActivity.this);
        alert.setTitle("COMENTARIO!!");
        alert.setMessage("Ingresa tu comentario");

       final EditText editText = new EditText(PostDetailActivity.this);
       editText.setHint("Deja tu comentario");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(36,0,36,36);

        editText.setLayoutParams(params);

RelativeLayout container = new RelativeLayout(PostDetailActivity.this);
RelativeLayout.LayoutParams relativeparams = new RelativeLayout.LayoutParams(
        RelativeLayout.LayoutParams.MATCH_PARENT,
        RelativeLayout.LayoutParams.WRAP_CONTENT
);
container.setLayoutParams(relativeparams);
container.addView(editText);
alert.setView(container);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            String value = editText.getText().toString();
            if(!value.isEmpty()){
                createComment(value);
            }
            else{
                Toast.makeText(PostDetailActivity.this, "Debe Ingresar Algo", Toast.LENGTH_SHORT).show();
            }

            }
        });
        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alert.show();
    }

    private void createComment(String value) {
        Comment comment = new Comment();
        comment.setComment(value);
        comment.setIdPost(mExtraPostId);
        comment.setIdUser(mAuthProvider.getUid());
        comment.setTimestamp(new Date().getTime());
        mcommentsProvider.create(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                 Toast.makeText(PostDetailActivity.this, "El comentario se ha publicado exitosamente", Toast.LENGTH_SHORT).show();
                }
                else {
                Toast.makeText(PostDetailActivity.this, "No se pudo publicar el comentario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void goToShowProfile() {
        if(!midUser.equals("")){
            Intent intent = new Intent(PostDetailActivity.this, UserProfileActivity.class);
            intent.putExtra("IdUser",midUser);
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
                    if(documentSnapshot.contains("timestamp")){
                      long  timestamp = documentSnapshot.getLong("timestamp");
                      String relativeTime = RelativeTime.getTimeAgo(timestamp,PostDetailActivity.this);
                      mTextViewRelativeTime.setText(relativeTime);
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