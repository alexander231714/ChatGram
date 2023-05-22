package com.loschidos.chatgram.adapters;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.loschidos.chatgram.R;
import com.loschidos.chatgram.activities.PostDetailActivity;
import com.loschidos.chatgram.models.Like;
import com.loschidos.chatgram.models.Post;
import com.loschidos.chatgram.providers.AuthProvider;
import com.loschidos.chatgram.providers.LikesProvider;
import com.loschidos.chatgram.providers.PostProvider;
import com.loschidos.chatgram.providers.UserProvider;
import com.squareup.picasso.Picasso;

import java.lang.annotation.Documented;
import java.util.Date;


public class PostsAdapter extends FirestoreRecyclerAdapter<Post, PostsAdapter.ViewHolder> {

    Context context;
    UserProvider mUserProvider;
    LikesProvider mLikeProvider;
    AuthProvider mAuthProvider;

    public PostsAdapter(FirestoreRecyclerOptions<Post> options, Context context) {
        super(options);
        this.context = context;
        mUserProvider = new UserProvider();
        mLikeProvider = new LikesProvider();
        mAuthProvider = new AuthProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull final Post post) {

        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String postId = document.getId();

        holder.textViewTitle.setText(post.getTitulo().toUpperCase());
        holder.textViewDescription.setText(post.getDescripcion());
        if (post.getImg1() != null) {
            if (!post.getImg1().isEmpty()) {
                Picasso.with(context).load(post.getImg1()).into(holder.imageViewPost);
            }
        }
        //añadimos un evente a la tarjeta de la publicacion
        holder.viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("id", postId);
                context.startActivity(intent);
            }
        });
        holder.imageViewLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Like like = new Like();
                like.setIdUser(mAuthProvider.getUid());
                like.setIdPost(postId);
                like.setTimestamp(new Date().getTime());
                like(like, holder);
            }
        });

        getUserInfo(post.getIdUsuario(), holder);
        getNumberLikesByPost(postId, holder);
        checkIfExistLike(postId, mAuthProvider.getUid(), holder);
    }
    /*private void getNumberLikesByPost(String idPost, final ViewHolder holder) {
        mLikeProvider.getLikesByPost(idPost).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                int numberLikes = queryDocumentSnapshots.size();
                holder.textViewLikes.setText(String.valueOf(numberLikes) + " Me gustas");
            }
        });
    }*/
    private void getNumberLikesByPost(String idPost, final ViewHolder holder) {
        mLikeProvider.getLikesByPost(idPost).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) { // Verificar si queryDocumentSnapshots no es null
                    int numberLikes = queryDocumentSnapshots.size();
                    holder.textViewLikes.setText(String.valueOf(numberLikes) + " Me gustas");
                }
            }
        });
    }



    private void like(final Like like, final ViewHolder holder) {

        mLikeProvider.getLikeByPostAndUser(like.getIdPost(), mAuthProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numberDocument = queryDocumentSnapshots.size();
                if(numberDocument > 0){
                    String idLike = queryDocumentSnapshots.getDocuments().get(0).getId();
                    holder.imageViewLike.setImageResource(R.drawable.icon_like_grey);
                    mLikeProvider.delete(idLike);
                }else {
                    holder.imageViewLike.setImageResource(R.drawable.icon_like_blue);
                    mLikeProvider.Create(like);
                }
            }
        });

    }

    private void checkIfExistLike(String idPost, String idUser, final ViewHolder holder) {
        mLikeProvider.getLikeByPostAndUser(idPost, idUser).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numberDocuments = queryDocumentSnapshots.size();
                if (numberDocuments > 0) {
                    holder.imageViewLike.setImageResource(R.drawable.icon_like_blue);
                }
                else {
                    holder.imageViewLike.setImageResource(R.drawable.icon_like_grey);
                }
            }
        });
    }

    private void getUserInfo(String idUsuario, final ViewHolder holder) {
        mUserProvider.getUser(idUsuario).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("username")){
                        String username = documentSnapshot.getString("username");
                        holder.textViewUsername.setText("By: " + username.toUpperCase());
                    }
                }
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_post, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewDescription, textViewUsername, textViewLikes;
        ImageView imageViewPost, imageViewLike;
        //Esta variable va optener la info de la tarjeta (post)
        View viewHolder;

        public ViewHolder(View view) {
            super(view);
            textViewTitle = view.findViewById(R.id.textViewTitlePostCard);
            textViewDescription = view.findViewById(R.id.textViewDescriptionPostcard);
            textViewUsername = view.findViewById(R.id.textViewUsernamePostCard);
            textViewLikes = view.findViewById(R.id.textViewLikes);
            imageViewPost = view.findViewById(R.id.imageViewPosCard);
            imageViewLike = view.findViewById(R.id.imageViewLike);
            viewHolder = view;
        }
    }

}