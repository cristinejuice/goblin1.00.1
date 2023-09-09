package com.example.goblin1001;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executor;

public class Login extends AppCompatActivity {

    private static final int REQUEST_CODE = 101010;
    ImageButton ibBiometrics;

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    EditText inputEmail, inputPassword;
    ImageButton ibPin;
    View tvCreate;
    View tvForgot;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ibBiometrics=findViewById(R.id.ibBiometrics);

        inputEmail=findViewById(R.id.inputEmail);
        inputPassword=findViewById(R.id.inputPassword);
        ibPin=findViewById(R.id.ibPin);
        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
        progressDialog=new ProgressDialog(this);
        tvCreate=findViewById(R.id.tvCreate);
        tvForgot=findViewById(R.id.tvForgot);

        ibPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=inputEmail.getText().toString();
                String password=inputPassword.getText().toString();
                PerformAuth(email,password);
            }
        });

        tvCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, gettingstarted.class);
                startActivity(intent);
            }
        });

        sharedPreferences=getSharedPreferences("data",MODE_PRIVATE);
        boolean isLogin=sharedPreferences.getBoolean("isLogin",false);
        if (isLogin)
        {

            ibBiometrics.setVisibility(View.VISIBLE);
        }

        //This checks if the user's device have a fingerprint sensor.

        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(BIOMETRIC_STRONG | DEVICE_CREDENTIAL)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                Log.d("MY_APP_TAG", "App can authenticate using biometrics.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Log.e("MY_APP_TAG", "The device has no fingerprint sensor.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                // Prompts the user to create credentials that your app accepts.
                final Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
                startActivityForResult(enrollIntent, REQUEST_CODE);
                break;
        }

        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(Login.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                                "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);

                String email=sharedPreferences.getString("email","");
                String password=sharedPreferences.getString("password","");

                PerformAuth(email,password);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed",
                                Toast.LENGTH_SHORT)
                        .show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for my app")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Use account password")
                .build();

        // Prompt appears when user clicks "Log in".
        // Consider integrating with the keystore to unlock cryptographic operations,
        // if needed by your app.

        ibBiometrics.setOnClickListener(view -> {
            biometricPrompt.authenticate(promptInfo);
        });
    }

    private void PerformAuth(String email, String password) {
        progressDialog.setMessage("Login");
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

            if (task.isSuccessful())
            {
                SharedPreferences.Editor editor=getSharedPreferences("data",MODE_PRIVATE).edit();
                editor.putString("email",email);
                editor.putString("password",password);
                editor.putBoolean("isLogin",true);
                editor.apply();
                startActivity(new Intent(Login.this,Home.class));

                progressDialog.dismiss();
                Toast.makeText(Login.this,"Login Successful", Toast.LENGTH_SHORT).show();
            }

            else
            {
                progressDialog.dismiss();
                Toast.makeText(Login.this,""+task.getException(), Toast.LENGTH_SHORT).show();
            }

            }
        });
    }
}
