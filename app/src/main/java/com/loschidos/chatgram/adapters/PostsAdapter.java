package com.loschidos.chatgram.adapters;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.loschidos.chatgram.R;
import com.loschidos.chatgram.activities.PostDetailActivity;
import com.loschidos.chatgram.models.Post;
import com.loschidos.chatgram.providers.PostProvider;
import com.loschidos.chatgram.providers.UserProvider;
import com.squareup.picasso.Picasso;

import java.lang.annotation.Documented;


public class PostsAdapter extends FirestoreRecyclerAdapter<Post, PostsAdapter.ViewHolder> {

    Context context;
    UserProvider mUserProvider;

    public PostsAdapter(FirestoreRecyclerOptions<Post> options, Context context) {
        super(options);
        this.context = context;
        mUserProvider = new UserProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Post post) {

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

        getUserInfo(post.getIdUsuario(), holder);
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
        TextView textViewTitle, textViewDescription, textViewUsername;
        ImageView imageViewPost;
        //Esta variable va optener la info de la tarjeta (post)
        View viewHolder;

        public ViewHolder(View view) {
            super(view);
            textViewTitle = view.findViewById(R.id.textViewTitlePostCard);
            textViewDescription = view.findViewById(R.id.textViewDescriptionPostcard);
            textViewUsername = view.findViewById(R.id.textViewUsernamePostCard);
            imageViewPost = view.findViewById(R.id.imageViewPosCard);
            viewHolder = view;
        }
    }

}