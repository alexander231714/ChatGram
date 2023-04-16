package com.loschidos.chatgram.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.loschidos.chatgram.R;
import com.loschidos.chatgram.models.User;
import com.loschidos.chatgram.providers.AuthProvider;
import com.loschidos.chatgram.providers.UserProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity2 extends AppCompatActivity {

    CircleImageView mCirculeImageViewBack;
    TextInputEditText mTextInputUsername, mTextInputEmail, mTextInputPassword, mTextInputConfirPassword;
    Button mButtonRegister;
  AuthProvider mAuthProvider ;
    UserProvider mUserProviders;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);

        mTextInputEmail=findViewById(R.id.textInputEmail);
        mTextInputUsername=findViewById(R.id.textInputUsername);
        mTextInputPassword=findViewById(R.id.textInputPassword);
        mTextInputConfirPassword=findViewById(R.id.textInputConfirPassword);
        mButtonRegister=findViewById(R.id.btnRegister);

        mCirculeImageViewBack=findViewById(R.id.circleImageBack);

        //instanciamos el objeto de autenticacion firebase
        mAuthProvider = new AuthProvider();
        //instanciamos la BD
        mUserProviders= new UserProvider();

        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });


        mCirculeImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void register() {
        String username=mTextInputUsername.getText().toString();
        String email=mTextInputEmail.getText().toString();
        String password=mTextInputPassword.getText().toString();
        String confirmPassword=mTextInputConfirPassword.getText().toString();

        if (!username.isEmpty() && !email.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty()) {
            if (isEmailValid(email)) {
                if (password.equals(confirmPassword)) {
                    if (password.length() >= 6) {
                        // Ejecutar método createUser()
                        createUser(username, email, password);
                    } else {
                        Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "El correo electrónico no es válido, por favor ingresa uno válido", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Para continuar ingresa todos los campos", Toast.LENGTH_LONG).show();
        }


    }

    //Metodo para crear un usuario con parametros email, password
    //En caso de no querer almacenar sus datos se usa el esquema anterior
    private void createUser(final String username,final String email, String password) {

        mAuthProvider.register(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //si la tarea fue exitosa
                //crear el usuario en firebaseAuthentication
                if(task.isSuccessful()){
                    //id nos de vuelve UID del usuario, que se crea en Authentication(firebase)
                    String id = mAuthProvider.getUid();
                    //Mejorando codigo Uso de Usersprovider para el manejo de insercion de datos en firebase
                    // Creando propiedades de usuario
                    User user = new User();
                    user.setId(id);
                    user.setEmail(email);
                    user.setUsername(username);
                    /*Si la informacion se almaceno correctamente en la base de datos*/
                    mUserProviders.create(user).addOnCompleteListener(task1 -> {
                        if(task1.isSuccessful()){
                            Toast.makeText(RegisterActivity2.this, "El usuario se almaceno correctamente en la base de datos", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(RegisterActivity2.this, "No se almaceno el usuario en la base de datos", Toast.LENGTH_SHORT).show();
                        }
                    });
                    //Toast.makeText(RegisterActivity2.this, "El usario se registro correctamente", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(RegisterActivity2.this, "No se pudo registrar el usuario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Verifica que el correo ingresado sea valido
    public boolean isEmailValid(String email) {
        String expression = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}