package com.loschidos.chatgram.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
//import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.UploadTask;
import com.loschidos.chatgram.R;
import com.loschidos.chatgram.models.Post;
import com.loschidos.chatgram.models.User;
import com.loschidos.chatgram.providers.AuthProvider;
import com.loschidos.chatgram.providers.ImageProvider;
import com.loschidos.chatgram.providers.UserProvider;
import com.loschidos.chatgram.utils.FileUtil;
import com.squareup.picasso.Picasso;
import android.app.AlertDialog;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class EditProfileActivity extends AppCompatActivity {

    CircleImageView mCircleImageViewBack, mcircleImageViewProfile;
    ImageView mImageViewCovert;
    TextInputEditText mTextInputUsername, mTextInputPhone;
    Button mButtonEditProfile;
    AlertDialog mDialog;
    AlertDialog.Builder mBuilderSelector;
    ImageProvider imgprov;
    UserProvider mUsersProvider;
    AuthProvider mAuthProvider;
    CharSequence options[];
    private final int GALLERY_REQUEST_CODE_PROFILE = 1, GALLERY_REQUEST_CODE_COVER = 2;// identificador dee la actividad
    private final int PHOTO_REQUEST_CODE_PROFILE = 3, PHOTO_REQUEST_CODE_COVER = 4; //identificador de fotos
    String mAdsolutePhotoPath, mPhotoPath;
    File mPhotoFile;
    //para tomar foto camara 2
    String mAdsolutePhotoPath2, mPhotoPath2;
    File mPhotoFile2;
    File mImgFile1, mImgFile2;
    String mUsername="";
    String mPhone="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mcircleImageViewProfile=findViewById(R.id.circleImageProfile);
        mImageViewCovert=findViewById(R.id.imageViewCover);
        mTextInputUsername=findViewById(R.id.textInputUsername);
        mTextInputPhone=findViewById(R.id.textInputTelefono);
        mCircleImageViewBack =findViewById(R.id.circleImageBack);
        mButtonEditProfile=findViewById(R.id.btnEditProfile);


        mUsersProvider=new UserProvider();
        imgprov = new ImageProvider();
        mAuthProvider = new AuthProvider();

        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento")
                .setCancelable(false).build();

        mBuilderSelector = new AlertDialog.Builder(this);
        mBuilderSelector.setTitle("Selecciona una opcion");
        options = new CharSequence[] {"Imagen de galeria", "Tomar foto"};

        mButtonEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickEditProfile();
            }
        });
        mcircleImageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // uno identifica la imagen del perfil
                SelecOptionImage(1);
            }
        });

        mImageViewCovert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dos identifica la imagen del cover
                SelecOptionImage(2);
            }
        });

        mCircleImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void clickEditProfile() {
        mUsername=mTextInputUsername.getText().toString();
        mPhone=mTextInputPhone.getText().toString();
        if(!mUsername.isEmpty() && !mPhone.isEmpty()){
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
                Toast.makeText(EditProfileActivity.this, "Debes seleccionar una imagen", Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(this, "Ingrese el nombre de usuario y telefono", Toast.LENGTH_SHORT).show();
        }
    }

    private void SaveImg(File imageFile1, File imageFile2) {
        mDialog.show();
        imgprov.save(EditProfileActivity.this, imageFile1).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    imgprov.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String urlProfile = uri.toString();

                            imgprov.save(EditProfileActivity.this, imageFile2).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> taskImage2) {
                                    if (taskImage2.isSuccessful()) {
                                        imgprov.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri2) {
                                                String urlCover = uri2.toString();
                                                User user=new User();
                                                user.setUsername(mUsername);
                                                user.setTelefono(mPhone);
                                                user.setImageProfile(urlProfile);
                                                user.setImageCover(urlCover);
                                                user.setId(mAuthProvider.getUid());
                                                mUsersProvider.update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        mDialog.dismiss();
                                                        if(task.isSuccessful()){
                                                            Toast.makeText(EditProfileActivity.this, "Informacion actualizada correctamente", Toast.LENGTH_SHORT).show();
                                                        }else{
                                                            Toast.makeText(EditProfileActivity.this, "La informacion no se pudo actualizar", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                    }
                                    else {
                                        mDialog.dismiss();
                                        Toast.makeText(EditProfileActivity.this, "La imagen numero 2 no se pudo guardar", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
                else {
                    mDialog.dismiss();
                    Toast.makeText(EditProfileActivity.this, "Hubo error al almacenar la imagen", Toast.LENGTH_LONG).show();
                }
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
                        OpenGallery(GALLERY_REQUEST_CODE_PROFILE);
                    }
                    else if(numberImage == 2){
                        OpenGallery(GALLERY_REQUEST_CODE_COVER);
                    }
                }
                //para difereciar imagenes de camara
                else if (i == 1){
                    if(numberImage == 1){
                        takePhoto(PHOTO_REQUEST_CODE_PROFILE);
                    }
                    else if(numberImage == 2){
                        takePhoto(PHOTO_REQUEST_CODE_COVER);
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
                Uri photoUri = FileProvider.getUriForFile(EditProfileActivity.this, "com.loschidos.chatgram", photoFile);
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
        if(requestCode == PHOTO_REQUEST_CODE_PROFILE){
            mPhotoPath = "file:" + photoFile.getAbsolutePath();
            mAdsolutePhotoPath=photoFile.getAbsolutePath();
        } else if (requestCode == PHOTO_REQUEST_CODE_COVER) {
            mPhotoPath2 = "file:" + photoFile.getAbsolutePath();
            mAdsolutePhotoPath2=photoFile.getAbsolutePath();
        }
        return photoFile;
    }

    //Con este metodo se ejecuta cuando el usuario realiza la accion que lo lleva a la galeria
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //SELECCION DE IMAGEN DESDE LA GALERIA
        //para perfil  de usuario
        if (requestCode == GALLERY_REQUEST_CODE_PROFILE && resultCode == RESULT_OK) {
            try {
                mPhotoFile = null;
                mImgFile1 = FileUtil.from(this, data.getData());
                mcircleImageViewProfile.setImageBitmap(BitmapFactory.decodeFile(mImgFile1.getAbsolutePath()));
            } catch (Exception e) {
                Log.d("ERROR", "Se produjo un error " + e.getMessage());
                Toast.makeText(this, "Se produjo un error " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        //para imagen de covert
        if (requestCode == GALLERY_REQUEST_CODE_COVER && resultCode == RESULT_OK) {
            try {
                mImgFile2 = null;
                mImgFile2 = FileUtil.from(this, data.getData());
                mImageViewCovert.setImageBitmap(BitmapFactory.decodeFile(mImgFile2.getAbsolutePath()));
            } catch (Exception e) {
                Log.d("ERROR", "Se produjo un error " + e.getMessage());
                Toast.makeText(this, "Se produjo un error " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        //SELECCION DE FOTOGRAFIA
        if(requestCode == PHOTO_REQUEST_CODE_PROFILE && resultCode == RESULT_OK){
            mImgFile1=null;
            mPhotoFile = new File(mAdsolutePhotoPath);
            //liberia que sirve para mostrar una imagen atraves de una URL
            Picasso.with(EditProfileActivity.this).load(mPhotoPath).into(mcircleImageViewProfile);
        }
        if(requestCode == PHOTO_REQUEST_CODE_COVER && resultCode == RESULT_OK){
            mImgFile2=null;
            mPhotoFile2 = new File(mAdsolutePhotoPath2);
            Picasso.with(EditProfileActivity.this).load(mPhotoPath2).into(mImageViewCovert);
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