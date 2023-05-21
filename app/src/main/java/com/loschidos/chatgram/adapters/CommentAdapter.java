package com.loschidos.chatgram.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.loschidos.chatgram.R;
import com.loschidos.chatgram.models.Comment;
import com.loschidos.chatgram.providers.UserProvider;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class CommentAdapter extends FirestoreRecyclerAdapter<Comment, CommentAdapter.ViewHolder> {

    Context context;
    UserProvider mUserProvider;

    public CommentAdapter(FirestoreRecyclerOptions<Comment> options, Context context) {
        super(options);
        this.context = context;
        mUserProvider = new UserProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Comment comment) {

        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String CommentId = document.getId();
        String idUser = document.getString("idUser");

        holder.textViewComment.setText(comment.getComment());
        getUserInfo(idUser, holder);
    }

    private  void getUserInfo(String idUser, final ViewHolder holder){
        mUserProvider.getUser(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("username")){
                        String username = documentSnapshot.getString("username");
                        holder.textViewUsername.setText(username);
                    }
                    if(documentSnapshot.contains("image_profile")){
                        String imageProfile = documentSnapshot.getString("image_profile");
                        if(imageProfile != null){
                            if(!imageProfile.isEmpty()){
                                Picasso.with(context).load(imageProfile).into(holder.circleimageComment);
                            }
                        }
                    }
                }
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_comment, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUsername, textViewComment;
        CircleImageView circleimageComment;
        //Esta variable va optener la info de la tarjeta (post)
        View viewHolder;

        public ViewHolder(View view) {
            super(view);
            textViewUsername = view.findViewById(R.id.textViewUsername);
            textViewComment = view.findViewById(R.id.textViewComment);
            circleimageComment = view.findViewById(R.id.circleImageComment);
            viewHolder = view;
        }
    }

}