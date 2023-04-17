package com.loschidos.chatgram.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.UploadTask;
import com.loschidos.chatgram.R;
import com.loschidos.chatgram.models.Post;
import com.loschidos.chatgram.providers.AuthProvider;
import com.loschidos.chatgram.providers.ImageProvider;
import com.loschidos.chatgram.providers.PostProvider;
import com.loschidos.chatgram.utils.FileUtil;

import java.io.File;

public class PostActivity extends AppCompatActivity {

    ImageView imgpost1; //variable que referencia la imagen que se va a subir
    File imgFile;
    private final int GALLERY_REQUEST_CODE = 1; // identificador dee la actividad
    ImageProvider imgprov; // INSTANCIA clase
    Button btnpublicar;
    AuthProvider Mauthprov;
    PostProvider mPostProvider;
    TextInputEditText EtTitulo;
    TextInputEditText EtDesc;
    ImageView imgdep;
    ImageView imgjuegos;
    ImageView imgMusica;
    ImageView imgPeli;
    TextView cate;

    String categoria = "";
String mtitulo = "";
String mdescripcion;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_post);
        imgpost1 = findViewById(R.id.imgpost1); //Instancia de la variable imgpost1
        btnpublicar = findViewById(R.id.btnPost); //Instancia del boton para publicar imagen

        imgprov= new ImageProvider();
        mPostProvider = new PostProvider();
        Mauthprov = new AuthProvider();

        EtTitulo = findViewById(R.id.textInputPost); //INSTANCIANDO METODOS DE POST
        EtDesc = findViewById(R.id.textInputDesc);
        imgdep = findViewById(R.id.imgviewDeporte);
        imgjuegos = findViewById(R.id.imgviewJuegos);
        imgMusica = findViewById(R.id.imgviewmusica);
        imgPeli = findViewById(R.id.imgviewPelis);
        cate = findViewById(R.id.cate);

        imgdep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoria="DEPORTE";
                cate.setText(categoria);
            }
        });
        imgjuegos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoria="JUEGOS";
                cate.setText(categoria);
            }
        });

        imgMusica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoria="MUSICA";
                cate.setText(categoria);
            }
        });

        imgPeli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoria="PELICULAS";
                cate.setText(categoria);
            }
        });
        //GUARDAR IMAGEN
        btnpublicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickPost();
            }

            private void ClickPost() {
                String mtitulo = EtTitulo.getText().toString();
                String mdescripcion = EtDesc.getText().toString();
          if(!mtitulo.isEmpty() && !mdescripcion.isEmpty() && !categoria.isEmpty()){
             if(imgFile != null){
                 SaveImg();
             }
             else{
                 Toast.makeText(PostActivity.this, "Debes seleccionar una imagen", Toast.LENGTH_LONG).show();
             }
          }
          else{
              Toast.makeText(PostActivity.this, "Completa los campos para publicar", Toast.LENGTH_LONG).show();
          }
            }


            //METODO GUARDAR IMAGEN
            private void SaveImg() {
                imgprov.save(PostActivity.this, imgFile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            imgprov.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String url = uri.toString();
                                   Post post = new Post();
                                   post.setImg1(url);
                                    post.setTitulo(mtitulo);
                                   post.setDescripcion(mdescripcion);
                                   post.setCategoria(categoria);
                                   post.setIdUsuario(Mauthprov.getUid());
                                    mPostProvider.save(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> taskSave) {
                                            if(taskSave.isSuccessful()){
                  Toast.makeText(PostActivity.this, "La Informacion se almaceno Correctamente", Toast.LENGTH_LONG).show();
                                            }
                                            else {
                                                Toast.makeText(PostActivity.this, "Hubo error al almacenar la Informacion", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                    });
                                }
                            });

                        } else {
                            Toast.makeText(PostActivity.this, "Hubo error al almacenar la imagen", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

       //Con este metodo cuando se toque el icono de la imagen se abrira la galeria
        imgpost1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            OpenGallery();
            }
        });
    }

    //Con este metodo se ejecuta cuando el usuario realiza la accion que lo lleva a la galeria
ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
     // Si la accion que realiza el usuario es la de seleccionar desde galeria caso contrario se mostrara un error
             if(result.getResultCode() == Activity.RESULT_OK){
            try {
        imgFile = FileUtil.from(PostActivity.this,result.getData().getData());
        imgpost1.setImageBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
    }catch (Exception e){
        Log.d("ERROR", "Se produjo un error al seleccionar la imagen" +e.getMessage());
        Toast.makeText(PostActivity.this,"Se produjo un error"+e.getMessage(),Toast.LENGTH_LONG).show();
    }
             }
            }
        }
);
    private void OpenGallery() {
        Intent galleryIntent = new Intent (Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        galleryLauncher.launch(galleryIntent);
      //  startActivityForResult(galleryIntent,GALLERY_REQUEST_CODE); (LINEA ALTERNA DE CODIGO)
        //  de esta forma se abre la galeria y se usa el numero como
        // Un identificador  de la actividad que realiza en base a la Accion del usuario
    }
}