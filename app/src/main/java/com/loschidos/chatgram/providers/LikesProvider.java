package com.loschidos.chatgram.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.loschidos.chatgram.models.Like;

public class LikesProvider {

    CollectionReference mCollection;

    public LikesProvider(){
        mCollection = FirebaseFirestore.getInstance().collection("Likes");
    }

    public Task<Void> Create(Like like){
        DocumentReference document = mCollection.document();
        String id = document.getId();
        like.setId(id);
        return document.set(like);
    }

    public Query getLikeByPostAndUser(String idPost, String idUsuario){
        return mCollection.whereEqualTo("idPost", idPost).whereEqualTo("idUser", idUsuario);
    }

    //metodo para eliliminar like
    public Task<Void> delete(String id){
        return mCollection.document(id).delete();
    }
    //para contarlikes

    public Query getLikesByPost(String idPost) {
        return mCollection.whereEqualTo("idPost", idPost);
    }
}
