package com.loschidos.chatgram.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.loschidos.chatgram.R;
import com.loschidos.chatgram.models.User;
import com.loschidos.chatgram.providers.AuthProvider;
import com.loschidos.chatgram.providers.UserProvider;
import android.app.AlertDialog;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {

    //Google Sign in
    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    TextView mTextViewRegister;
    TextInputEditText mTextInpuTEmail, mTextInputPassword;
    Button mButtonLogin;
    AuthProvider mAuthProvider;
    SignInButton mButtonGoogle;
    UserProvider mUserProvider;
    AlertDialog mDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDialog=new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento")
                .setCancelable(false).build(); mDialog=new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento")
                .setCancelable(false).build();



        mTextInpuTEmail=findViewById(R.id.textInputEmail);
        mTextInputPassword=findViewById(R.id.textInputPassword);
        mButtonLogin=findViewById(R.id.btnLogin);
        mButtonGoogle=findViewById(R.id.btnLoginGoogle);
        mTextViewRegister = findViewById(R.id.textViewRegister);


        //Inicializamos la autenticacion de firebase
        mAuthProvider = new AuthProvider();

        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mUserProvider = new UserProvider();

        //Button para registrase con correo y contrase;a
        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mTextInpuTEmail.getText().toString().isEmpty() && !mTextInputPassword.getText().toString().isEmpty()){
                    login();
                }else {
                    Toast.makeText(MainActivity.this, "Debe ingresar su correo y contraseña", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Button para registrase con google
        mButtonGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInGoogle();
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                //Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        mAuthProvider.googleLogin(acct).addOnCompleteListener(this,(task)-> {
                        if (task.isSuccessful()) {
                            String id = mAuthProvider.getUid();
                            checkUserExist(id);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "No se puedo iniciar con Google", Toast.LENGTH_SHORT).show();
                        }
                });
    }

    //Verificamos si el usuario existe
    private void checkUserExist(final String id) {
        mUserProvider.getUser(id).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
                else {
                    String email = mAuthProvider.getEmail();
                    User user = new User();
                    user.setEmail(email);
                    user.setId(id);
                    mUserProvider.create(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(MainActivity.this, CompleteProfileActivity.class);
                                startActivity(intent);
                            }
                            else {
                                Toast.makeText(MainActivity.this, "No se pudo almacenar la informacion del usuario", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    // [END auth_with_google]


    // ver si la sesion del usuario existe
    @Override
    protected void onStart() {
        super.onStart();
        if(mAuthProvider.getUserSesion() != null){
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    // [START signin]
    /*dispara todo el proceso de autenticacion con google*/
    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void login() {
        String email = mTextInpuTEmail.getText().toString();
        String password = mTextInputPassword.getText().toString();
        mAuthProvider.login(email, password).addOnCompleteListener((task) ->{
                if(task.isSuccessful()){
                    mDialog.show();
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }else {
                    mDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Email o contraseña que ingresaste es incorrecta", Toast.LENGTH_LONG).show();
                }
        });
        Log.d("CAMPO", "email: "+email);
        Log.d("CAMPO", "password: "+password);
    }
}