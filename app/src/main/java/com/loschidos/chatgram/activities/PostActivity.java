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
import android.app.AlertDialog;
import android.content.DialogInterface;
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

import com.android.car.ui.AlertDialogBuilder;
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

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class PostActivity extends AppCompatActivity {

    ImageView imgpost1; //variable que referencia la imagen que se va a subir
    ImageView imgpost2;
    File imgFile;
    File imgFile2;
    private final int GALLERY_REQUEST_CODE = 1;// identificador dee la actividad
    private final int GALLERY_REQUEST_CODE_2 = 2;
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
    AlertDialog mDialog;

    AlertDialog.Builder mBuilderSelector;
    CircleImageView mCircleImageBack;
    CharSequence options[];

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        imgprov = new ImageProvider();
        mPostProvider = new PostProvider();
        Mauthprov = new AuthProvider();

        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento")
                .setCancelable(false).build();

        mBuilderSelector = new AlertDialog.Builder(this);
        mBuilderSelector.setTitle("Selecciona una opcion");
        options = new CharSequence[] {"Imagen de galeria", "Tomar foto"};


        imgpost1 = findViewById(R.id.imgpost1); //Instancia de la variable imgpost1
        imgpost2 = findViewById(R.id.imgpost2);
        btnpublicar = findViewById(R.id.btnPost); //Instancia del boton para publicar imagen


        EtTitulo = findViewById(R.id.textInputPost); //INSTANCIANDO METODOS DE POST
        EtDesc = findViewById(R.id.textInputDesc);
        imgdep = findViewById(R.id.imgviewDeporte);
        imgjuegos = findViewById(R.id.imgviewJuegos);
        imgMusica = findViewById(R.id.imgviewmusica);
        imgPeli = findViewById(R.id.imgviewPelis);
        cate = findViewById(R.id.cate);
        mCircleImageBack = findViewById(R.id.circleImageBack);

        mCircleImageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //GUARDAR IMAGEN
        btnpublicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickPost();
            }
        });


        //Con este metodo cuando se toque el icono de la imagen se abrira la galeria
        imgpost1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelecOptionImage(GALLERY_REQUEST_CODE);

            }
        });
        imgpost2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelecOptionImage(GALLERY_REQUEST_CODE_2);
            }
        });
        imgdep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoria = "DEPORTE";
                cate.setText(categoria);
            }
        });
        imgjuegos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoria = "JUEGOS";
                cate.setText(categoria);
            }
        });

        imgMusica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoria = "MUSICA";
                cate.setText(categoria);
            }
        });

        imgPeli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoria = "PELICULAS";
                cate.setText(categoria);
            }
        });
    }

    private void SelecOptionImage(final int requestCode) {
        mBuilderSelector.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    OpenGallery(requestCode);
                }
                else if (i == 1){
                    takePhoto();
                }
            }
        });

        mBuilderSelector.show();

    }

    private void takePhoto() {
        Toast.makeText(this, "Selecciono tomar foto", Toast.LENGTH_SHORT).show();
    }

    private void ClickPost() {
        mtitulo = EtTitulo.getText().toString();
        mdescripcion = EtDesc.getText().toString();
        if (!mtitulo.isEmpty() && !mdescripcion.isEmpty() && !categoria.isEmpty()) {
            if (imgFile != null) {
                SaveImg();
            } else {
                Toast.makeText(PostActivity.this, "Debes seleccionar una imagen", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(PostActivity.this, "Completa los campos para publicar", Toast.LENGTH_LONG).show();
        }
    }


    //METODO GUARDAR IMAGEN
    private void SaveImg() {
        mDialog.show();
        imgprov.save(PostActivity.this, imgFile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    imgprov.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String url = uri.toString();

                            imgprov.save(PostActivity.this, imgFile2).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> taskImage2) {
                                    if (taskImage2.isSuccessful()) {
                                        imgprov.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri2) {
                                                String url2 = uri2.toString();
                                                Post post = new Post();
                                                post.setImg1(url);
                                                post.setImg2(url2);
                                                post.setTitulo(mtitulo);
                                                post.setDescripcion(mdescripcion);
                                                post.setCategoria(categoria);
                                                post.setIdUsuario(Mauthprov.getUid());
                                                mPostProvider.save(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> taskSave) {
                                                        mDialog.dismiss();
                                                        if (taskSave.isSuccessful()) {
                                                            clearForm();
                                                            Toast.makeText(PostActivity.this, "La informacion se almaceno correctamente", Toast.LENGTH_SHORT).show();
                                                        }
                                                        else {
                                                            Toast.makeText(PostActivity.this, "No se pudo almacenar la informacion", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                    }
                                    else {
                                        mDialog.dismiss();
                                        Toast.makeText(PostActivity.this, "La imagen numero 2 no se pudo guardar", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
                else {
                    mDialog.dismiss();
                    Toast.makeText(PostActivity.this, "Hubo error al almacenar la imagen", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


//Limpiar controles despues de almacenar datos

    private void clearForm() {
        EtTitulo.setText("");
        EtDesc.setText("");
        cate.setText("CATEGORIAS");
        imgpost1.setImageResource(R.drawable.upload_image);
        imgpost2.setImageResource(R.drawable.upload_image);
        mtitulo = "";
        mdescripcion = "";
        categoria = "";
        imgFile = null;
        imgFile2 = null;
    }


    //Con este metodo se ejecuta cuando el usuario realiza la accion que lo lleva a la galeria
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            try {
                imgFile = FileUtil.from(this, data.getData());
                imgpost1.setImageBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
            } catch (Exception e) {
                Log.d("ERROR", "Se produjo un error " + e.getMessage());
                Toast.makeText(this, "Se produjo un error " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == GALLERY_REQUEST_CODE_2 && resultCode == RESULT_OK) {
            try {
                imgFile2 = FileUtil.from(this, data.getData());
                imgpost2.setImageBitmap(BitmapFactory.decodeFile(imgFile2.getAbsolutePath()));
            } catch (Exception e) {
                Log.d("ERROR", "Se produjo un error " + e.getMessage());
                Toast.makeText(this, "Se produjo un error " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
    private void OpenGallery(int requestCode) {
        Intent galleryIntent = new Intent (Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        // galleryLauncher.launch(galleryIntent);
        startActivityForResult(galleryIntent,requestCode);
        //  de esta forma se abre la galeria y se usa el numero como
        // Un identificador  de la actividad que realiza en base a la Accion del usuario
    }

}