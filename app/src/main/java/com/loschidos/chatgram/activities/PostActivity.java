package com.loschidos.chatgram.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.loschidos.chatgram.R;
import com.loschidos.chatgram.utils.FileUtil;

import java.io.File;

public class PostActivity extends AppCompatActivity {

    ImageView imgpost1; //variable que referencia la imagen que se va a subir
    File imgFile;
    private final int GALLERY_REQUEST_CODE = 1; // identificador dee la actividad
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
       //Instancia de la variable imgpost1
        imgpost1 = findViewById(R.id.imgpost1);
       //Con este metodo cuando se toque el icono de la imagen se abrira la galeria
        imgpost1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            OpenGallery();
            }
        });
    }

    private void OpenGallery() {
        Intent galleryIntent = new Intent (Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,GALLERY_REQUEST_CODE); //de esta forma se abre la galeria y se usa el numero como
        // Un identificador  de la actividad que realiza en base a la Accion del usuario
    }

    //Con este metodo se ejecuta cuando el usuario realiza la accion que lo lleva a la galeria
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Si la accion que realiza el usuario es la de seleccionar desde galeria caso contrario se mostrara un error
        if(requestCode==GALLERY_REQUEST_CODE && resultCode == RESULT_OK){
          try {
              imgFile = FileUtil.from(this,data.getData());
              imgpost1.setImageBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
          }catch (Exception e){
              Log.d("ERROR", "Se produjo un error al seleccionar la imagen" +e.getMessage());
              Toast.makeText(this,"Se produjo un error"+e.getMessage(),Toast.LENGTH_LONG).show();
          }
        }
    }
}