package com.example.biometricapp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static com.google.android.gms.common.internal.safeparcel.SafeParcelable.NULL;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity2 extends AppCompatActivity {
    EditText phone, otp;
    Button btngenOTP,btnverify;
    
    FirebaseAuth mAuth;
    String verificationID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        phone = findViewById(R.id.phone);
        otp = findViewById(R.id.otp);
        btngenOTP = findViewById(R.id.btngenerateOTP);
        btnverify = findViewById(R.id.btnVerifyOTP);

        mAuth = FirebaseAuth.getInstance();

        btngenOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TextUtils.isEmpty(phone.getText().toString())){
                    Toast.makeText(MainActivity2.this, "Enter valid phone number", Toast.LENGTH_SHORT).show();
                    System.out.println("Enter valid phone number");
                }else{
                    Toast.makeText(MainActivity2.this, "GOOD phone number", Toast.LENGTH_SHORT).show();
                    String number = phone.getText().toString();
                    System.out.println("GOOD phone number");
                    sendVerificationCode(number);
                }
                
            }
        });

        btnverify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(otp.getText().toString())){
                    Toast.makeText(MainActivity2.this, "WRONG OTP", Toast.LENGTH_SHORT).show();
                    System.out.println("OTP is WRONG");

                }
                else {
                    System.out.println("OTP is GOOD");
                    verifyCode(otp.getText().toString());
                }
            }
        });
    }

    private void verifyCode(String code){
        System.out.println("verifyCode()");
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID,code);
        signinbyCredentials(credential);

    }

    private void signinbyCredentials(PhoneAuthCredential credential) {
        System.out.println("signinbyCredentials()");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity2.this, "Login Succesfull", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity2.this,MainActivity.class));
                        }
                    }
                });
    }

    private void sendVerificationCode(String phoneNumber){
        System.out.println("sendVerificationCode()");
        System.out.println("Number Phone = +40"+phoneNumber);
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+40"+phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // (optional) Activity for callback binding
                        // If no activity is passed, reCAPTCHA verification can not be used.
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {


        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
            System.out.println("onVerificationCompleted()");

            final String code = credential.getSmsCode();
            if(code!=NULL){
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            System.out.println("onVerificationFailed()");
            Toast.makeText(MainActivity2.this, "Verification Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Cod nevalid solicitat
            } else if (e instanceof FirebaseTooManyRequestsException) {
                // A fost depășită limita de cereri de SMS
            }
            // Log pentru a vizualiza eroarea în detaliu
            Log.e(TAG, "Verification failed", e);
        }

        @Override
        public void onCodeSent(@NonNull String verificationId,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            System.out.println("onCodeSent()");
            super.onCodeSent(verificationId,token);
            verificationID = verificationId;
        }
    };

    @Override
    protected void onStart() {
        System.out.println("onStart()");
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser!=null){
            startActivity(new Intent(MainActivity2.this,MainActivity.class));
            finish();
        }
    }
}