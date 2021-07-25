package com.example.jaikisan;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {
    Button kisanButton,consumerButton;
    ImageView coverPhoto;

    CardView kisanCardView,consumerCardView,KisanCreateAccountCardView;

    EditText kisanPhoneNumber,kisanOTP;
    Button kisanLoginButton,kisanCreateButton;
    TextView kisanForgotPasswordText,consumerForgotPasswordText;

    EditText consumerPhoneNumber,consumerPassword;
    Button consumerLoginButton,consumerCreateButton;

    EditText farmerFullNameAC,farmer_stateAC,farmer_cityAC,farmer_AddressAC,farmer_PhoneNumberAC,farmer_verify_otp_AC;
    Button farmer_get_otp_AC,finalCreateAC;

    FirebaseAuth mAuth;
    String codeSent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();
        coverPhoto = findViewById(R.id.coverPhoto);
        kisanButton =findViewById(R.id.FarmerButton);
        consumerButton =findViewById(R.id.ConsumerButton);

        kisanCardView=findViewById(R.id.KisanCardView);
        consumerCardView=findViewById(R.id.ConsumerCardView);

        kisanLoginButton=findViewById(R.id.KisanLoginButton);
        kisanPhoneNumber=findViewById(R.id.KisanPhoneNumber);
        kisanOTP=findViewById(R.id.KisanOTPEdit);
        kisanCreateButton=findViewById(R.id.KisanCreateAccount);

        consumerLoginButton=findViewById(R.id.ConsumerLoginButton);
        consumerPhoneNumber=findViewById(R.id.ConsumerPhoneNumber);
        consumerPassword=findViewById(R.id.ConsumerPassword);
        consumerCreateButton=findViewById(R.id.ConsumerCreateAccount);
        consumerForgotPasswordText=findViewById(R.id.ConsumerResetPassword);

        KisanCreateAccountCardView =findViewById(R.id.KisanCreateAccountCardView);

        farmerFullNameAC =findViewById(R.id.farmerFullNameAC);
        farmer_stateAC=findViewById(R.id.farmer_stateAC);
        farmer_cityAC=findViewById(R.id.farmer_cityAC);
        farmer_AddressAC=findViewById(R.id.farmer_AddressAC);
        farmer_PhoneNumberAC=findViewById(R.id.farmer_PhoneNumberAC);
        farmer_verify_otp_AC=findViewById(R.id.farmer_verify_otp_AC);

        farmer_get_otp_AC=findViewById(R.id.farmer_get_otp_AC);
        finalCreateAC=findViewById(R.id.finalCreateAC);

        mAuth = FirebaseAuth.getInstance();

    }

    public void FarmerLogin(View view) {
        consumerCardView.setVisibility(View.GONE);
        kisanCardView.setVisibility(View.VISIBLE);
        KisanCreateAccountCardView.setVisibility(View.GONE);
    }

    public void ConsumerLogin(View view) {
        consumerCardView.setVisibility(View.VISIBLE);
        kisanCardView.setVisibility(View.GONE);
        KisanCreateAccountCardView.setVisibility(View.GONE);
    }
    public void KisanGenerateOTPButton(View view) {
        String phoneNumber = kisanPhoneNumber.getText().toString();
        String temp = "+91"+phoneNumber;
        generate_OTP(temp);
    }
    public void generate_OTP(String phoneNumber){
        if(phoneNumber.length() != 13){
            kisanPhoneNumber.setText("");
            kisanPhoneNumber.requestFocus();
            Toast.makeText(getApplicationContext(),"Enter Valid Phone Number",Toast.LENGTH_LONG).show();
            return;
        }

        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }
                // Show a message and update the UI
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                codeSent = verificationId;
                //mResendToken = token;
            }
        };
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public void KisanLoginButtonOnClickListener(View view) {
        verifySignIncodeKisan();
    }
    //For Kisan Login Auth only
    private void verifySignIncodeKisan(){
        String code = kisanOTP.getText().toString();
        if(code.isEmpty()){
            Toast.makeText(getApplicationContext(),"Enter OTP",Toast.LENGTH_LONG).show();
            return;
        }
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, code);
        signInWithPhoneAuthCredential_LOGIN(credential);
    }
    private void signInWithPhoneAuthCredential_LOGIN(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            Toast.makeText(getApplicationContext(),"Login Successful",Toast.LENGTH_LONG).show();
                            FirebaseUser user = task.getResult().getUser();
                            long creationTimestamp = user.getMetadata().getCreationTimestamp();
                            long lastSignInTimestamp = user.getMetadata().getLastSignInTimestamp();
                            if (creationTimestamp == lastSignInTimestamp) {
                                Toast.makeText(getApplicationContext(),"Please create Account first",Toast.LENGTH_LONG).show();
                            } else {
                                Intent intent = new Intent(getApplicationContext(),Famers_Dashboard.class);
                                startActivity(intent);
                            }
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(getApplicationContext(),"Incorrect Verification code",Toast.LENGTH_LONG).show();

                            }
                        }
                    }
                });
    }

    public void KisanCreateAccountButtonOnClickListener(View view) {
        consumerCardView.setVisibility(View.GONE);
        kisanCardView.setVisibility(View.GONE);
        KisanCreateAccountCardView.setVisibility(View.VISIBLE);
    }
    public void farmer_Create_AC_OTP_Generate(View view) {
        String phoneNumber = "+91"+farmer_PhoneNumberAC.getText().toString();
        generate_OTP(phoneNumber);
    }
    public void Create_Farmer_AC(View view) {
        verifySignIncodeKisan_createAC();
    }
    private void verifySignIncodeKisan_createAC(){
        String code = farmer_verify_otp_AC.getText().toString();
        if(code.isEmpty()){
            Toast.makeText(getApplicationContext(),"Enter OTP",Toast.LENGTH_LONG).show();
            return;
        }
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, code);
        signInWithPhoneAuthCredential_Create_AC(credential);
    }
    private void signInWithPhoneAuthCredential_Create_AC(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            Toast.makeText(getApplicationContext(),"Login Successful",Toast.LENGTH_LONG).show();
                            FirebaseUser user = task.getResult().getUser();
                            long creationTimestamp = user.getMetadata().getCreationTimestamp();
                            long lastSignInTimestamp = user.getMetadata().getLastSignInTimestamp();
                            if (creationTimestamp != lastSignInTimestamp) {
                                Toast.makeText(getApplicationContext(),"Account Already Exists",Toast.LENGTH_LONG).show();
                            } else {
                                //create a database and store all the inputs from create account cardview
                            }
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(getApplicationContext(),"Incorrect Verification code",Toast.LENGTH_LONG).show();

                            }
                        }
                    }
                });
    }

    // Consumer CardView Functions
    public void ConsumerLoginOnClickListener(View view) {
    }

    public void ConsumerCreateAccOnClickListener(View view) {
    }

    public void consumerForgotPasswordOnClickListener(View view) {
    }
    /*@Override
    public void onBackPressed()
    {
        kisanCardView.setVisibility(View.GONE);
        consumerCardView.setVisibility(View.GONE);
    }*/
    // Backbutton functions
    int doubleBackToExitPressed = 1;
    @RequiresApi
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressed == 1) {
            kisanCardView.setVisibility(View.GONE);
            consumerCardView.setVisibility(View.GONE);
            KisanCreateAccountCardView.setVisibility(View.GONE);
        }
        if (doubleBackToExitPressed == 2) {
            finishAffinity();
            System.exit(0);
        }
        else {
            doubleBackToExitPressed++;
            Toast.makeText(this, "Please press Back again to exit", Toast.LENGTH_SHORT).show();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressed=1;
            }
        }, 2000);
    }



}