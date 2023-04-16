package com.loschidos.chatgram.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.loschidos.chatgram.R;
import com.loschidos.chatgram.models.User;
import com.loschidos.chatgram.providers.AuthProvider;
import com.loschidos.chatgram.providers.UserProvider;

import java.util.HashMap;
import java.util.Map;

public class CompleteProfileActivity extends AppCompatActivity {

    TextInputEditText mTextInputUsername;
    Button mButtonRegister;
    AuthProvider mAuthProvider;
    UserProvider mUserProvider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);


        mTextInputUsername=findViewById(R.id.textInputUsername);
        mButtonRegister=findViewById(R.id.btnRegister);
     //INSTANCIANDO PROPIEDADES AUTH Y USER
        //instanciamos el objeto de autenticacion firebase
        mAuthProvider= new AuthProvider();
        mUserProvider= new UserProvider();

        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

    }

    private void register() {
        String username=mTextInputUsername.getText().toString();
        if (!username.isEmpty()) {
            updateUser(username);
        } else {
            Toast.makeText(this, "Para continuar ingresa todos los campos", Toast.LENGTH_LONG).show();
        }


    }

    //Metodo para crear un usuario con parametros email, password
    private void updateUser(final String username) {
        //id nos de vuelve UID del usuario, que se crea en Authentication(firebase)
        String id = mAuthProvider.getUid();
        User user = new User();
        user.setUsername(username);
        user.setId(id);
        /*si hubo un registro nuevo con google vamos a pedir que complete su inf
        * En este caso que ingrese su usuario(usamos update para que se sobre escriban los datos)*/
        /*Si la informacion se almaceno correctamente en la base de datos*/
        mUserProvider.update(user).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Intent intent = new Intent(CompleteProfileActivity.this,HomeActivity.class);
                startActivity(intent);
            }else {
                Toast.makeText(CompleteProfileActivity.this, "No se almaceno el usuario en la base de datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

}