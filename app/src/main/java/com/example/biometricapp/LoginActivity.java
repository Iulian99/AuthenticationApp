package com.example.biometricapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.Executor;

public class LoginActivity extends AppCompatActivity {
    View circle1,circle2,circle3,circle4;
    EditText hiddenEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        dialogAuthentication();
        Button login = findViewById(R.id.login);
        ImageView fingerprintImageView = findViewById(R.id.imageViewBiometricFingerprint);
        TextView contact1 = findViewById(R.id.contact1);
        TextView contact2 = findViewById(R.id.contact2);

        circle1 = findViewById(R.id.circle1);
        circle2 = findViewById(R.id.circle2);
        circle3 = findViewById(R.id.circle3);
        circle4 = findViewById(R.id.circle4);

        hiddenEditText = findViewById(R.id.hiddenEditText);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                circle1.setBackground(AppCompatResources.getDrawable(LoginActivity.this, R.drawable.rounded_corners_gray_checked));
                System.out.println("Buttonul a fost apasat");
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
            }
        });
        fillCircle(hiddenEditText);

        getCallContact(contact1);
        getCallContact(contact2);

        ImageView imageView = findViewById(R.id.imageViewBiometricFingerprint);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAuthentication();
            }
        });

    }

    public void dialogAuthentication(){
//        search executor
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            // Suprascrie metodele necesare, cum ar fi onAuthenticationSucceeded, onAuthenticationFailed, etc.
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Verify identity")
                .setDescription("Scan your fingerprint")
                .setNegativeButtonText("Use PIN")
                .build();

        // Afișează dialogul de autentificare biometrică
        biometricPrompt.authenticate(promptInfo);
    }
    private void getCallContact(TextView contact) {
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Uniform Resource Identifier
                String phoneNumber = contact.getText().toString();
                Intent dial = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
                startActivity(dial);
            }
        });
    }

    private void fillCircle(EditText hiddenEditText) {
        hiddenEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                System.out.println("charSequence = " + charSequence);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                System.out.println("charSequence = " + charSequence);
            }


            @Override
            public void afterTextChanged(Editable editable) {
                int length = editable.length();
                System.out.println("length = " + length);
//                Change the color of circle
                if(length>=1){
                    circle1.setBackground(AppCompatResources.getDrawable(LoginActivity.this, R.drawable.rounded_corners_gray_checked));
                }else{
                    circle1.setBackground(AppCompatResources.getDrawable(LoginActivity.this, R.drawable.rounded_corners_gray_unchecked));
                }
                if(length>=2){
                    circle2.setBackground(AppCompatResources.getDrawable(LoginActivity.this, R.drawable.rounded_corners_gray_checked));
                }else{
                    circle2.setBackground(AppCompatResources.getDrawable(LoginActivity.this, R.drawable.rounded_corners_gray_unchecked));
                }
                if(length>=3){
                    circle3.setBackground(AppCompatResources.getDrawable(LoginActivity.this, R.drawable.rounded_corners_gray_checked));
                }else{
                    circle3.setBackground(AppCompatResources.getDrawable(LoginActivity.this, R.drawable.rounded_corners_gray_unchecked));
                }
                if(length>=4){
                    circle4.setBackground(AppCompatResources.getDrawable(LoginActivity.this, R.drawable.rounded_corners_gray_checked));
                }else{
                    circle4.setBackground(AppCompatResources.getDrawable(LoginActivity.this, R.drawable.rounded_corners_gray_unchecked));
                }
                Log.d("MainActivity", "length = " + length);
//                circle1.setBackground(length >= 1 ? AppCompatResources.getDrawable(LoginActivity.this, R.drawable.rounded_corners_gray_checked) : AppCompatResources.getDrawable(LoginActivity.this, R.drawable.rounded_corners_gray_unchecked));
//                circle2.setBackground(length >= 2 ? AppCompatResources.getDrawable(LoginActivity.this, R.drawable.rounded_corners_gray_checked) : AppCompatResources.getDrawable(LoginActivity.this, R.drawable.rounded_corners_gray_unchecked));
//                circle3.setBackground(length >= 3 ? AppCompatResources.getDrawable(LoginActivity.this, R.drawable.rounded_corners_gray_checked) : AppCompatResources.getDrawable(LoginActivity.this, R.drawable.rounded_corners_gray_unchecked));
//                circle4.setBackground(length >= 4 ? AppCompatResources.getDrawable(LoginActivity.this, R.drawable.rounded_corners_gray_checked) : AppCompatResources.getDrawable(LoginActivity.this, R.drawable.rounded_corners_gray_unchecked));

            }
        });
    }
}