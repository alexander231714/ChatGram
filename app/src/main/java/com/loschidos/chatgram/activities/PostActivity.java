package com.loschidos.chatgram.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class PostActivity extends AppCompatActivity {

    ImageView imgpost1, imgpost2; //variable que referencia la imagen que se va a subir
    File mImgFile1, mImgFile2;
    private final int GALLERY_REQUEST_CODE = 1, GALLERY_REQUEST_CODE_2 = 2;// identificador dee la actividad
    private final int PHOTO_REQUEST_CODE = 3, PHOTO_REQUEST_CODE_2 = 4; //identificador de fotos

    ImageProvider imgprov; // INSTANCIA clase
    Button btnpublicar;
    AuthProvider Mauthprov;
    PostProvider mPostProvider;
    TextInputEditText EtTitulo, EtDesc;
    ImageView imgdep, imgjuegos, imgMusica, imgPeli;
    TextView cate;
    String categoria = "", mtitulo = "", mdescripcion;
    AlertDialog mDialog;
    AlertDialog.Builder mBuilderSelector;
    CircleImageView mCircleImageBack;
    CharSequence options[];

    //para tomar foto camara 1
    String mAdsolutePhotoPath, mPhotoPath;
    File mPhotoFile;
    //para tomar foto camara 2
    String mAdsolutePhotoPath2, mPhotoPath2;
    File mPhotoFile2;

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
                SelecOptionImage(1);

            }
        });
        imgpost2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelecOptionImage(2);
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

    private void SelecOptionImage(final int numberImage) {
        mBuilderSelector.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //para difereciar imagenes de galeria
                if (i == 0) {
                    if(numberImage == 1){
                        OpenGallery(GALLERY_REQUEST_CODE);
                    }
                    else if(numberImage == 2){
                        OpenGallery(GALLERY_REQUEST_CODE_2);
                    }
                }
                //para difereciar imagenes de camara
                else if (i == 1){
                    if(numberImage == 1){
                        takePhoto(PHOTO_REQUEST_CODE);
                    }
                    else if(numberImage == 2){
                        takePhoto(PHOTO_REQUEST_CODE_2);
                    }
                }
            }
        });

        mBuilderSelector.show();

    }

    private void takePhoto(int requestCode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) !=null){
            File photoFile = null;
            try {
                photoFile = createPhotoFile(requestCode);
            }catch (Exception e){
                Toast.makeText(this, "Hubo un error con el archivo"+e.getMessage(), Toast.LENGTH_LONG).show();
            }

            if(photoFile != null){
                Uri photoUri = FileProvider.getUriForFile(PostActivity.this, "com.loschidos.chatgram", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, requestCode);
            }
        }
    }

    private File createPhotoFile(int requestCode) throws IOException {
        String timestamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File photoFile = File.createTempFile(
                new Date()+"_photo",
                ".jpg",
                storageDir
        );
        if(requestCode == PHOTO_REQUEST_CODE){
            mPhotoPath = "file:" + photoFile.getAbsolutePath();
            mAdsolutePhotoPath=photoFile.getAbsolutePath();
        } else if (requestCode == PHOTO_REQUEST_CODE_2) {
            mPhotoPath2 = "file:" + photoFile.getAbsolutePath();
            mAdsolutePhotoPath2=photoFile.getAbsolutePath();
        }
        return photoFile;
    }


    private void ClickPost() {
        mtitulo = EtTitulo.getText().toString();
        mdescripcion = EtDesc.getText().toString();
        if (!mtitulo.isEmpty() && !mdescripcion.isEmpty() && !categoria.isEmpty()) {
            //seleccionaste ambas imagenes de galeria
            if (mImgFile1 != null && mImgFile2 !=null) {
                SaveImg(mImgFile1, mImgFile2);
            }
            //Selecciono ambas fotos de camara
            else if (mPhotoFile != null && mPhotoFile2 != null){
                SaveImg(mPhotoFile, mPhotoFile2);
            }
            //Selecciono una foto de galeria y la otra de la camara
            else if (mImgFile1 != null && mPhotoFile2 != null){
                SaveImg(mImgFile1, mPhotoFile2);
            }
            //Selecciono foto de la camara y la otra de galeria
            else if (mPhotoFile != null && mImgFile2 != null){
                SaveImg(mPhotoFile, mImgFile2);
            }
            else {
                Toast.makeText(PostActivity.this, "Debes seleccionar una imagen", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(PostActivity.this, "Completa los campos para publicar", Toast.LENGTH_LONG).show();
        }
    }


    //METODO GUARDAR IMAGEN
    private void SaveImg(File imageFile1, File imageFile2) {
        mDialog.show();
        imgprov.save(PostActivity.this, imageFile1).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    imgprov.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String url = uri.toString();

                            imgprov.save(PostActivity.this, imageFile2).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
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
                                                post.setTimestamp(new Date().getTime());
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
        mImgFile1 = null;
        mImgFile2 = null;
    }


    //Con este metodo se ejecuta cuando el usuario realiza la accion que lo lleva a la galeria
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //SELECCION DE IMAGEN DESDE LA GALERIA
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            try {
                mPhotoFile = null;
                mImgFile1 = FileUtil.from(this, data.getData());
                imgpost1.setImageBitmap(BitmapFactory.decodeFile(mImgFile1.getAbsolutePath()));
            } catch (Exception e) {
                Log.d("ERROR", "Se produjo un error " + e.getMessage());
                Toast.makeText(this, "Se produjo un error " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == GALLERY_REQUEST_CODE_2 && resultCode == RESULT_OK) {
            try {
                mImgFile2 = null;
                mImgFile2 = FileUtil.from(this, data.getData());
                imgpost2.setImageBitmap(BitmapFactory.decodeFile(mImgFile2.getAbsolutePath()));
            } catch (Exception e) {
                Log.d("ERROR", "Se produjo un error " + e.getMessage());
                Toast.makeText(this, "Se produjo un error " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        //SELECCION DE FOTOGRAFIA
        if(requestCode == PHOTO_REQUEST_CODE && resultCode == RESULT_OK){
            mImgFile1=null;
            mPhotoFile = new File(mAdsolutePhotoPath);
            //liberia que sirve para mostrar una imagen atraves de una URL
            Picasso.with(PostActivity.this).load(mPhotoPath).into(imgpost1);
        }
        if(requestCode == PHOTO_REQUEST_CODE_2 && resultCode == RESULT_OK){
            mImgFile2=null;
            mPhotoFile2 = new File(mAdsolutePhotoPath2);
            Picasso.with(PostActivity.this).load(mPhotoPath2).into(imgpost2);
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