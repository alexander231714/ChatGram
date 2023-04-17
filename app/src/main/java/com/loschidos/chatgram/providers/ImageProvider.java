package com.loschidos.chatgram.providers;

import android.content.Context;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.loschidos.chatgram.utils.CompressorBitmapImage;

import java.io.File;
import java.util.Date;

public class ImageProvider {

    StorageReference mStorage;

    public ImageProvider() {
        mStorage = FirebaseStorage.getInstance().getReference(); //Se hace referencia al almacenamiento de imagenes dentro de la BD
    }
//LOGICA PARA GUARDAR IMAGENES
    public UploadTask save(Context context, File File) {
    byte[] imgbyte = CompressorBitmapImage.getImage(context, File.getPath(),500,500);
    StorageReference storage = mStorage.child(new Date()+ ".jpg");
    UploadTask task = storage.putBytes(imgbyte);
    return task;
    }

}
