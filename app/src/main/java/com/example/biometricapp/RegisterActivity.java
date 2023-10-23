package com.example.biometricapp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import static com.google.android.gms.common.internal.safeparcel.SafeParcelable.NULL;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
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
import android.window.OnBackInvokedDispatcher;

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


public class RegisterActivity extends AppCompatActivity{
    private EditText phoneEditText,codeEditText,codeTextInputPINcode,codeTextInputPINcodeRetype;
    private TextInputLayout textInputLayout;
    private int current_state=1;
    Button btn_next_step;
    private StepView stepView;
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

        codeTextInputPINcode = findViewById(R.id.codeTextInputPINcode);
        codeTextInputPINcodeRetype = findViewById(R.id.codeTextInputPINcodeRetype);
        mAuth = FirebaseAuth.getInstance();

// declarare stepview - bara de sus
        stepView = findViewById(R.id.step_view);
        setStepView(stepView);
// Butonul de back
        goBack();

        if(current_state==1){
            fillCircle(phoneEditText);
        }
        btn_next_step.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    System.out.println("Butonul1 a fost apasat");
                    if(current_state==1){
                        System.out.println("current state  = "+current_state);
                        String text = phoneEditText.getText().toString();
                        System.out.println("PhoneEditText ="+text);
                        verifyPhone(text); //good

                    }else if(current_state==2){
                        System.out.println("current state  = "+current_state);

                        verifySendCode();
                    }else if(current_state==3){
                        System.out.println("current state  = "+current_state);

                        comparePINCode(codeTextInputPINcode,codeTextInputPINcodeRetype);
                    }
                    System.out.println("first current_state = "+current_state);
                    current_state++;
                    System.out.println("last current_state = "+current_state);

                    nextIncludedLayout();
                }catch (NullPointerException e) {
                    Toast.makeText(RegisterActivity.this, "An error occurred!", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void comparePINCode(EditText codeTextInputPINcode, EditText codeTextInputPINcodeRetype) {
        System.out.println("comparePINCode()");
        codeTextInputPINcode = findViewById(R.id.codeTextInputPINcode);
        codeTextInputPINcodeRetype = findViewById(R.id.codeTextInputPINcodeRetype);

        if(codeTextInputPINcode.getText().toString().equals(codeTextInputPINcodeRetype.getText().toString())){
//            Toast.makeText(RegisterActivity.this, "Login Succesfull", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(RegisterActivity.this,MainActivity.class));
        }
    }

    private void verifySendCode() {
        System.out.println("verifySendCode()");
        codeEditText = findViewById(R.id.codeBrandTextInputCode);
        String smsCode = codeEditText.getText().toString();
        System.out.println("smsCode = "+ smsCode);
        if(smsCode.length() == 6){
            verifyCode(smsCode);

        }else{
            Toast.makeText(RegisterActivity.this, "Please enter your code number", Toast.LENGTH_SHORT).show();
        }
    }

    private void verifyPhone(String numberPhone) {

        System.out.println("Number phont = " + numberPhone);
        sendVerificationCode(numberPhone);

    }

    private void sendVerificationCode(String numberPhone) {
        System.out.println("sendVerificationCode()");
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+40"+numberPhone)       // Phone number to verify
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
            final String code = credential.getSmsCode();

            if(code!=null){
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
//            Toast.makeText(RegisterActivity.this, "onVerificationFailed", Toast.LENGTH_SHORT).show();
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
//            Toast.makeText(RegisterActivity.this, "Code was sent", Toast.LENGTH_SHORT).show();
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
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
//                            isVerified = true;
                        }
                    }
                });
    }

    private TextWatcher textWatcher = new TextWatcher() {
        // ... alte metode ...

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            switch (current_state) {
                case 1:
                case 2:
                    btn_next_step.setEnabled(s.length() >= 2);
                    break;
                case 3:
                    btn_next_step.setEnabled(codeTextInputPINcode.getText().toString().length() >= 2 &&
                            codeTextInputPINcode.getText().toString().equals(codeTextInputPINcodeRetype.getText().toString()));
                    break;
                default:
                    btn_next_step.setEnabled(false);
                    break;
            }
        }

    };


    private void fillCircle(EditText... editTexts) {
        // Clear any existing TextWatcher
        // Clear any existing TextWatcher
        for (EditText editText : editTexts) {
            if (editText != null) {  // Null-check added here
                editText.removeTextChangedListener(textWatcher);
                editText.addTextChangedListener(textWatcher);
            }
        }
    }

    private void nextIncludedLayout() {
        ConstraintLayout constraintLayout = findViewById(R.id.constraintLayoutInclude);
        constraintLayout.removeAllViews();

        // Load the new layout you want to display in the container
        LayoutInflater inflater = LayoutInflater.from(this);
        View newLayout;

        switch (current_state) {
            case 1:
                newLayout = inflater.inflate(R.layout.validate_phone_number1, constraintLayout, false);
                constraintLayout.addView(newLayout);
                // Initialize the views after inflating
                btn_next_step = newLayout.findViewById(R.id.btn_next_step);
                btn_next_step = findViewById(R.id.btn_next_step);

                phoneEditText = newLayout.findViewById(R.id.phoneBrandTextInput);

                btn_next_step.setEnabled(false);
                fillCircle(phoneEditText);
                stepView.go(current_state - 1, true);
                break;

            case 2:
                newLayout = inflater.inflate(R.layout.provide_sms_code2, constraintLayout, false);
                constraintLayout.addView(newLayout);
                btn_next_step = newLayout.findViewById(R.id.btn_next_step);
                btn_next_step = findViewById(R.id.btn_next_step);

                codeEditText = newLayout.findViewById(R.id.codeBrandTextInputCode);

                btn_next_step.setEnabled(false);
                fillCircle(codeEditText);
                stepView.go(current_state - 1, true);
                break;

            case 3:
                newLayout = inflater.inflate(R.layout.provide_pin_code3, constraintLayout, false);
                constraintLayout.addView(newLayout);
                btn_next_step = newLayout.findViewById(R.id.btn_next_step);
                btn_next_step = findViewById(R.id.btn_next_step);
                codeTextInputPINcode = findViewById(R.id.codeTextInputPINcode);
                codeTextInputPINcodeRetype = findViewById(R.id.codeTextInputPINcodeRetype);
                btn_next_step.setEnabled(false);
                fillCircle(codeTextInputPINcode,codeTextInputPINcodeRetype);
                stepView.go(current_state - 1, true);
                break;
        }
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

//
    private void setStepView(StepView stepView) {
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
    }
    private void goBack() {
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Toast.makeText(RegisterActivity.this, "Back Button was pressed", Toast.LENGTH_SHORT).show();
                System.out.println("Back Button was pressed");
                previousIncludedLayout();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }
    private void previousIncludedLayout() {
//        go to previous Layout
        ConstraintLayout constraintLayout;
        if(current_state>1){
            constraintLayout = findViewById(R.id.constraintLayoutInclude);
            constraintLayout.removeAllViews(); // înlăturăm orice view existent în container
            // Încarcă noul layout pe care vrei să-l afișezi în container
            LayoutInflater inflater = LayoutInflater.from(this);
            View newLayout = null;
            System.out.println("CURRENT STATE = "+current_state);
            if(current_state==3){
                System.out.println("Current state a fost "+current_state);
                current_state--;
                System.out.println("Current state este "+current_state);
                newLayout = inflater.inflate(R.layout.provide_sms_code2, constraintLayout, false);
            }else if (current_state==2){
                System.out.println("Current state a fost "+current_state);
                current_state--;
                System.out.println("Current state este "+current_state);
                newLayout = inflater.inflate(R.layout.validate_phone_number1, constraintLayout, false);
            }

            // Adăugăm noul layout în container
            constraintLayout.addView(newLayout);
        }else{
//            se foloseste cand suntem in primul constraint layout - validate_phone_number1
            finish();
        }

    }
}