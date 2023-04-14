package com.loschidos.chatgram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity2 extends AppCompatActivity {

    CircleImageView mCirculeImageViewBack;
    TextInputEditText mTextInputUsername, mTextInputEmail, mTextInputPassword, mTextInputConfirPassword;
    Button mButtonRegister;
    FirebaseAuth mAuth;
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
        mAuth=FirebaseAuth.getInstance();

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
                        createUser(email, password);
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
    private void createUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //si la tarea fue exitosa
                if(task.isSuccessful()){
                    Toast.makeText(RegisterActivity2.this, "El usario se registro correctamente", Toast.LENGTH_SHORT).show();
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