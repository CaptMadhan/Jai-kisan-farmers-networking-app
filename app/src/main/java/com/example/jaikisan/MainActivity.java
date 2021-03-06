package com.example.jaikisan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

    EditText consumerPhoneNumber,consumerOTP;
    Button consumerLoginButton,consumerCreateButton;

    EditText farmerFullNameAC,farmer_stateAC,farmer_cityAC,farmer_AddressAC,farmer_PhoneNumberAC,farmer_verify_otp_AC;
    Button farmer_get_otp_AC,finalCreateAC;
    //Consumer items
    CardView ConsumerCreateAccountCardView;
    EditText consumerFullNameAC,consumer_stateAC,consumer_cityAC,consumer_AddressAC,consumer_PhoneNumberAC,consumer_verify_otp_AC;
    Button consumer_get_otp_AC,consumerFinalCreateAC;

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
        consumerOTP=findViewById(R.id.ConsumerOTP);
        consumerCreateButton=findViewById(R.id.ConsumerCreateAccount);

        KisanCreateAccountCardView =findViewById(R.id.KisanCreateAccountCardView);

        farmerFullNameAC =findViewById(R.id.farmerFullNameAC);
        farmer_stateAC=findViewById(R.id.farmer_stateAC);
        farmer_cityAC=findViewById(R.id.farmer_cityAC);
        farmer_AddressAC=findViewById(R.id.farmer_AddressAC);
        farmer_PhoneNumberAC=findViewById(R.id.farmer_PhoneNumberAC);
        farmer_verify_otp_AC=findViewById(R.id.farmer_verify_otp_AC);
        //Consumer items
        ConsumerCreateAccountCardView=findViewById(R.id.ConsumerCreateAccountCardView);
        consumerFullNameAC=findViewById(R.id.consumerFullNameAC);
        consumer_stateAC=findViewById(R.id.consumer_stateAC);
        consumer_cityAC=findViewById(R.id.consumer_cityAC);
        consumer_AddressAC=findViewById(R.id.consumer_AddressAC);
        consumer_PhoneNumberAC=findViewById(R.id.consumer_PhoneNumberAC);
        consumer_verify_otp_AC=findViewById(R.id.consumer_verify_otp_AC);
        consumer_get_otp_AC=findViewById(R.id.consumer_get_otp_AC);
        consumerFinalCreateAC=findViewById(R.id.consumerFinalCreateAC);

        farmer_get_otp_AC=findViewById(R.id.farmer_get_otp_AC);
        finalCreateAC=findViewById(R.id.finalCreateAC);

        mAuth = FirebaseAuth.getInstance();

    }

    public void FarmerLogin(View view) {
        consumerCardView.setVisibility(View.GONE);
        kisanCardView.setVisibility(View.VISIBLE);
        KisanCreateAccountCardView.setVisibility(View.GONE);
        ConsumerCreateAccountCardView.setVisibility(View.GONE);
    }

    public void ConsumerLogin(View view) {
        consumerCardView.setVisibility(View.VISIBLE);
        kisanCardView.setVisibility(View.GONE);
        KisanCreateAccountCardView.setVisibility(View.GONE);
        ConsumerCreateAccountCardView.setVisibility(View.GONE);
    }
    public void KisanGenerateOTPButton(View view) {
        String phoneNumber = kisanPhoneNumber.getText().toString();
        String temp = "+91"+phoneNumber;
        generate_OTP(temp);
    }
    public void ConsumerGenerateOTPButton(View view) {
        String phoneNumber = consumerPhoneNumber.getText().toString();
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
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(getApplicationContext(), "Invalid Request", Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(getApplicationContext(), "SMS quota for the project has been exceeded", Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(getApplicationContext(),Famers_Dashboard.class);
        startActivity(intent);
        //uncomment the below line after testing
        //verifySignIncodeKisan();
    }

    public void ConsumerLoginButtonOnClickListener(View view) {
        Intent intent = new Intent(getApplicationContext(),Consumer_Dashboard.class);
        startActivity(intent);
        //uncomment the below line after testing
        //verifySignIncodeConsumer();
    }
    //For Kisan Login Auth only
    private void verifySignIncodeKisan(){
        String code = kisanOTP.getText().toString();
        if(code.isEmpty()){
            Toast.makeText(getApplicationContext(),"Enter OTP",Toast.LENGTH_LONG).show();
            return;
        }
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, code);
        signInWithPhoneAuthCredential_LOGIN(credential,0);
        //userType = 0 -> Farmer
        //userType = 1 -> Consumer
     }
    //For Consumer Login Auth
    private void verifySignIncodeConsumer(){
        String code = consumerOTP.getText().toString();
        if(code.isEmpty()){
            Toast.makeText(getApplicationContext(),"Enter OTP",Toast.LENGTH_LONG).show();
            return;
        }
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, code);
        signInWithPhoneAuthCredential_LOGIN(credential,1);
    }
    private void signInWithPhoneAuthCredential_LOGIN(PhoneAuthCredential credential, int userType) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            Toast.makeText(getApplicationContext(),"Login Successful",Toast.LENGTH_LONG).show();
                            FirebaseUser user = Objects.requireNonNull(task.getResult()).getUser();
                            long creationTimestamp = Objects.requireNonNull(Objects.requireNonNull(user).getMetadata()).getCreationTimestamp();
                            long lastSignInTimestamp = Objects.requireNonNull(user.getMetadata()).getLastSignInTimestamp();
                            if (creationTimestamp == lastSignInTimestamp ) {
                                Toast.makeText(getApplicationContext(),"Please create Account first",Toast.LENGTH_LONG).show();
                                user.delete();
                            } else {
                                if(userType == 0) {
                                    Intent intent = new Intent(getApplicationContext(), Famers_Dashboard.class);
                                    startActivity(intent);
                                }
                                else{
                                    Intent intent = new Intent(getApplicationContext(), Consumer_Dashboard.class);
                                    startActivity(intent);
                                }
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
        ConsumerCreateAccountCardView.setVisibility(View.GONE);
    }
    public void ConsumerCreateAccountButtonOnClickListener(View view) {
        consumerCardView.setVisibility(View.GONE);
        kisanCardView.setVisibility(View.GONE);
        KisanCreateAccountCardView.setVisibility(View.GONE);
        ConsumerCreateAccountCardView.setVisibility(View.VISIBLE);
    }
    public void farmer_Create_AC_OTP_Generate(View view) {
        String phoneNumber = "+91"+farmer_PhoneNumberAC.getText().toString();
        generate_OTP(phoneNumber);
    }
    public void Consumer_Create_AC_OTP_Generate(View view) {
        String phoneNumber = "+91"+consumer_PhoneNumberAC.getText().toString();
        generate_OTP(phoneNumber);
    }
    public void Create_Farmer_AC(View view) {

        verifySignIncodeKisan_createAC();
    }
    public void Create_Consumer_AC(View view) {
        //verifySignIncodeConsumer_createAC();
        //Delete the below line to apply AUTH
        createNewDBforConsumer();
    }
    //Only for Kisan to avoid alternative signIn
    private void verifySignIncodeKisan_createAC(){
        String code = farmer_verify_otp_AC.getText().toString();
        if(code.isEmpty()){
            Toast.makeText(getApplicationContext(),"Enter OTP",Toast.LENGTH_LONG).show();
            return;
        }
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, code);
        signInWithPhoneAuthCredential_Create_AC_Kisan(credential);
    }
    private void signInWithPhoneAuthCredential_Create_AC_Kisan(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = Objects.requireNonNull(task.getResult()).getUser();
                            assert user != null;
                            long creationTimestamp = Objects.requireNonNull(user.getMetadata()).getCreationTimestamp();
                            long lastSignInTimestamp = user.getMetadata().getLastSignInTimestamp();
                            if (creationTimestamp != lastSignInTimestamp) {
                                Toast.makeText(getApplicationContext(),"Account Already Exists",Toast.LENGTH_LONG).show();
                            } else {
                                createNewDBforKisan();
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
    //Only for Consumer to avoid alternative signIn
    private void verifySignIncodeConsumer_createAC(){
        String code = consumer_verify_otp_AC.getText().toString();
        if(code.isEmpty()){
            Toast.makeText(getApplicationContext(),"Enter OTP",Toast.LENGTH_LONG).show();
            return;
        }
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, code);
        signInWithPhoneAuthCredential_Create_AC_Consumer(credential);
    }
    private void signInWithPhoneAuthCredential_Create_AC_Consumer(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = Objects.requireNonNull(task.getResult()).getUser();
                            assert user != null;
                            long creationTimestamp = Objects.requireNonNull(user.getMetadata()).getCreationTimestamp();
                            long lastSignInTimestamp = user.getMetadata().getLastSignInTimestamp();
                            if (creationTimestamp != lastSignInTimestamp) {
                                Toast.makeText(getApplicationContext(),"Account Already Exists",Toast.LENGTH_LONG).show();
                            } else {
                                createNewDBforConsumer();
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

    private void createNewDBforKisan() {

        String name,city,state,address,phone;
        name = farmerFullNameAC.getText().toString();
        city = farmer_cityAC.getText().toString();
        state=farmer_stateAC.getText().toString();
        phone=farmer_PhoneNumberAC.getText().toString();
        address=farmer_AddressAC.getText().toString();
        if(name.isEmpty()||city.isEmpty()||state.isEmpty()||phone.isEmpty()||address.isEmpty()){
            Toast.makeText(getApplicationContext(), "Please fill the required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        DAOKisan dao = new DAOKisan();
        dao.add(name,city,state,address,phone).addOnSuccessListener(suc->{
            Toast.makeText(getApplicationContext(),"Account Created, Please Login Now",Toast.LENGTH_LONG).show();
            consumerCardView.setVisibility(View.GONE);
            kisanCardView.setVisibility(View.VISIBLE);
            KisanCreateAccountCardView.setVisibility(View.GONE);
            ConsumerCreateAccountCardView.setVisibility(View.GONE);
        }).addOnFailureListener(er-> Toast.makeText(getApplicationContext(),"Error Occurred, Please try again",Toast.LENGTH_LONG).show());
        farmerFullNameAC.setText("");
        farmer_cityAC.setText("");
        farmer_stateAC.setText("");
        farmer_PhoneNumberAC.setText("");
        farmer_AddressAC.setText("");

    }
    private void createNewDBforConsumer() {

        String name,city,state,address,phone;
        name = consumerFullNameAC.getText().toString();
        city = consumer_cityAC.getText().toString();
        state=consumer_stateAC.getText().toString();
        phone=consumer_PhoneNumberAC.getText().toString();
        address=consumer_AddressAC.getText().toString();
        if(name.isEmpty()||city.isEmpty()||state.isEmpty()||phone.isEmpty()||address.isEmpty()){
            Toast.makeText(getApplicationContext(), "Please fill the required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        DAOConsumer dao = new DAOConsumer();
        dao.add(name,city,state,address,phone).addOnSuccessListener(suc->{
            Toast.makeText(getApplicationContext(),"Account Created, Please Login Now",Toast.LENGTH_LONG).show();
            consumerCardView.setVisibility(View.VISIBLE);
            kisanCardView.setVisibility(View.GONE);
            KisanCreateAccountCardView.setVisibility(View.GONE);
            ConsumerCreateAccountCardView.setVisibility(View.GONE);
        }).addOnFailureListener(er-> Toast.makeText(getApplicationContext(),"Error Occurred, Please try again",Toast.LENGTH_LONG).show());
        /*FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        myRef.setValue("Hello, World!");
         */
        consumerFullNameAC.setText("");
        consumer_cityAC.setText("");
        consumer_stateAC.setText("");
        consumer_PhoneNumberAC.setText("");
        consumer_AddressAC.setText("");

    }

    //Back Button functions
    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {

            super.onBackPressed();
            return;
        }
        kisanCardView.setVisibility(View.GONE);
        consumerCardView.setVisibility(View.GONE);
        KisanCreateAccountCardView.setVisibility(View.GONE);
        ConsumerCreateAccountCardView.setVisibility(View.GONE);
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }


}