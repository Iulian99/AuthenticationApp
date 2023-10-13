package com.example.biometricapp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import static com.google.android.gms.common.internal.safeparcel.SafeParcelable.NULL;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.shuhart.stepview.StepView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class RegisterActivity extends AppCompatActivity implements RegisterUser {
    String[] items = {"+40","+93","+355"};
    private AutoCompleteTextView autoCompleteTextView;

    private ArrayAdapter<String> adapterItems;
    private EditText phoneEditText;
    private EditText codeEditText;
    private TextInputLayout textInputLayout;
    private StepView stepView;
    private int current_state = 0;
    private Button btn_next_step;
    private List<Integer> listCustomLayout;
    private ConstraintLayout constraintLayout;
    TextInputEditText textInputEditText;
    FirebaseAuth mAuth;
    String verificationID1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        btn_next_step = findViewById(R.id.btn_next_step);


        textInputLayout = findViewById(R.id.textInputPhoneBrand);

        phoneEditText = findViewById(R.id.phoneBrandTextInput);
        codeEditText = findViewById(R.id.codeBrandTextInputCode);

        stepView = findViewById(R.id.step_view);


        listOfPrefix();

        focusEditText(phoneEditText);


        itemsListPrefix();

        stepView.getState()
                .selectedTextColor(ContextCompat.getColor(this, R.color.darkBlue))
                .animationType(StepView.ANIMATION_CIRCLE)
                .steps(new ArrayList<String>() {{
                    add("Validate Phone No.");
                    add("Provide SMS code");
                    add("Set PIN code");
                }})
                .selectedStepNumberColor(ContextCompat.getColor(this, R.color.darkBlue))
                .stepsNumber(3)
                .animationDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                .stepLineWidth(5)
                .stepNumberTextSize(60)
                .commit();



        btn_next_step.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                try {
                    String text = phoneEditText.getText().toString();

                    if(!text.isEmpty() && text.length()==9){
                        if(current_state<2){
                            current_state++;
                            stepView.go(current_state, true);//activam butonul de NEXT STEP

                            if(TextUtils.isEmpty(text)){
                                Toast.makeText(RegisterActivity.this, "Enter valid phone number", Toast.LENGTH_SHORT).show();
                                System.out.println("Enter valid phone number");
                            }else{
                                Toast.makeText(RegisterActivity.this, "GOOD phone number", Toast.LENGTH_SHORT).show();
                                String number = text;
                                System.out.println("GOOD phone number");
                                sendVerificationCode(number);
                                nextIncludedLayout();
                                String smsCode = codeEditText.getText().toString();
                                if(!smsCode.isEmpty() && smsCode.length()==6){
                                    if(TextUtils.isEmpty(text)){
                                        Toast.makeText(RegisterActivity.this, "Enter valid code number", Toast.LENGTH_SHORT).show();
                                        System.out.println("Enter valid code number");
                                    }else{
                                        Toast.makeText(RegisterActivity.this, "GOOD code number", Toast.LENGTH_SHORT).show();
                                        System.out.println("GOOD code number");
                                        verifyCode(smsCode);
                                    }
//                                    nextIncludedLayout();
                                }else{
                                    Toast.makeText(RegisterActivity.this, "Please enter your code number", Toast.LENGTH_SHORT).show();
                                }
                            }
//                        verifycode();
                        }

                    }


                    // restul codului
                } catch (NullPointerException e) {
                    Toast.makeText(RegisterActivity.this, "An error occurred!", Toast.LENGTH_SHORT).show();
                }


            }
        });

//        textInputEditText = findViewById(R.id.phoneBrandTextInput); = phoneEditText
        String phoneNumber = phoneEditText.getText().toString();
        mAuth = FirebaseAuth.getInstance();


    }

    private void sendVerificationCode(String phoneNumber) {
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

    private void itemsListPrefix() {
        autoCompleteTextView = findViewById(R.id.auto_complete_txt);
        adapterItems = new ArrayAdapter<String>(getApplication(),R.layout.list_item_text_input_layout, items);
        autoCompleteTextView.setAdapter(adapterItems);
    }

    private void focusEditText(EditText phoneEditText) {
        phoneEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    textInputLayout.setHintEnabled(true); // Activează hint-ul când câmpul primește focus
                } else {
                    textInputLayout.setHintEnabled(false); // Dezactivează hint-ul când câmpul pierde focus
                }
            }
        });
    }

    public void listOfPrefix(){
        listCustomLayout = new ArrayList<>();
        listCustomLayout.add(R.layout.validate_phone_number1);
        listCustomLayout.add(R.layout.provide_sms_code2);
    }

    private void nextIncludedLayout() {
//        go to next Layout
        constraintLayout = findViewById(R.id.constraintLayoutInclude);
        constraintLayout.removeAllViews(); // înlăturăm orice view existent în container
        // Încarcă noul layout pe care vrei să-l afișezi în container
        LayoutInflater inflater = LayoutInflater.from(this);
        View newLayout = inflater.inflate(R.layout.provide_sms_code2, constraintLayout, false);
        // Adăugăm noul layout în container
        constraintLayout.addView(newLayout);
    }

    private void previousIncludedLayout() {
//        go to previous Layout
        constraintLayout = findViewById(R.id.constraintLayoutInclude);
        constraintLayout.removeAllViews(); // înlăturăm orice view existent în container
        // Încarcă noul layout pe care vrei să-l afișezi în container
        LayoutInflater inflater = LayoutInflater.from(this);
        View newLayout = inflater.inflate(R.layout.validate_phone_number1, constraintLayout, false);
        // Adăugăm noul layout în container
        constraintLayout.addView(newLayout);
    }

    @Override
    public void login(String phoneNumber) {
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



    @Override
    public void logout() {

    }

    @Override
    public boolean isAuthenticated() {
        return false;
    }


    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
    mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
            final String code = credential.getSmsCode();

            if(code!=null){
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(RegisterActivity.this, "Verification Failed", Toast.LENGTH_SHORT).show();
            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request
            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
            } else if (e instanceof FirebaseAuthMissingActivityForRecaptchaException) {
                // reCAPTCHA verification attempted with null Activity
            }

        }

        @Override
        public void onCodeSent(@NonNull String verificationId,
                @NonNull PhoneAuthProvider.ForceResendingToken token) {
            Toast.makeText(RegisterActivity.this, "Code was sent", Toast.LENGTH_SHORT).show();
            super.onCodeSent(verificationId,token);
            verificationID1 = verificationId;
        }
    };

    private void verifyCode(String code) {
        System.out.println("Verify code");
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID1,code);
        signinByCredentials(credential);
    }

    private void signinByCredentials(PhoneAuthCredential credential) {
        System.out.println("signinByCredentials()");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "Login Succesfull", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser!=null){
            startActivity(new Intent(RegisterActivity.this,MainActivity.class));
            finish();
        }
    }
}