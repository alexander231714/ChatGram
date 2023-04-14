package com.loschidos.chatgram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.loschidos.chatgram.R;

public class MainActivity extends AppCompatActivity {

    TextView mTextViewRegister;
    TextInputEditText mTextInpuTEmail, mTextInputPassword;
    Button mButtonLogin;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextInpuTEmail=findViewById(R.id.textInputEmail);
        mTextInputPassword=findViewById(R.id.textInputPassword);
        mButtonLogin=findViewById(R.id.btnLogin);


        mTextViewRegister = findViewById(R.id.textViewRegister);

        //Inicializamos la autenticacion de firebase
        mAuth = FirebaseAuth.getInstance();
        //Button
        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });


        mTextViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity2.class);
                startActivity(intent);
            }
        });

    }

    private void login() {
        String email = mTextInpuTEmail.getText().toString();
        String password = mTextInputPassword.getText().toString();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(MainActivity.this, "Email o contraseña que ingresaste es incorrecta", Toast.LENGTH_LONG).show();
                }
            }
        });
        Log.d("CAMPO", "email: "+email);
        Log.d("CAMPO", "password: "+password);
    }
}