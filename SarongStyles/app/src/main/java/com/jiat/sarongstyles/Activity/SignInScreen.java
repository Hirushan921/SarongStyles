package com.jiat.sarongstyles.Activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.GetSignInIntentRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.rpc.context.AttributeContext;
import com.jiat.sarongstyles.R;

import java.util.HashMap;
import java.util.Map;

public class SignInScreen extends AppCompatActivity {

    private TextInputEditText email_edit,password_edit;
    private ImageView emailCheck,backArrow;
    private TextView passCheck,signupClick,forgotpassClick;
    private Button loginButton;
    private RelativeLayout googleClick;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private SignInClient signInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //REMOVE STATUSBAR
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //REMOVE TOOLBAR
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}


        setContentView(R.layout.activity_sign_in_screen);


        firebaseAuth = FirebaseAuth.getInstance();

        firestore = FirebaseFirestore.getInstance();

        signInClient = Identity.getSignInClient(getApplicationContext());

        emailCheck=(ImageView) findViewById(R.id.email_check);
        passCheck=(TextView) findViewById(R.id.pass_strong);

        //Edit Function

        email_edit=(TextInputEditText)findViewById(R.id.edit_emailaddress);
        email_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (email_edit.getText().toString().length()<6) {
                    emailCheck.setVisibility(View.GONE);
                } else if (email_edit.getText().toString().length()>=6) {
                    emailCheck.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        password_edit=(TextInputEditText)findViewById(R.id.edit_password);
        password_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (password_edit.getText().toString().length()<6) {
                    passCheck.setVisibility(View.GONE);
                } else if (password_edit.getText().toString().length()>=6) {
                    passCheck.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        //BACKARROW
        backArrow=(ImageView) findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(),IntroScreen.class);
                startActivity(intent);
            }
        });

        //Signup Click Function

        signupClick=(TextView) findViewById(R.id.signup_click);
        signupClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(), SignUpScreen.class);
                startActivity(intent);
            }
        });

        //LoginButton

        loginButton=(Button) findViewById(R.id.button_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

             String email = email_edit.getText().toString();
             String password = password_edit.getText().toString();

                if (email.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter username", Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter password", Toast.LENGTH_SHORT).show();
                } else {

                    firebaseAuth.signInWithEmailAndPassword(email,password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        updateUI(firebaseAuth.getCurrentUser());
                                    }else{
                                        Toast.makeText(getApplicationContext(), "Please Check Your Input Details..", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }

            }
        });




        //google authentication
        googleClick=(RelativeLayout)findViewById(R.id.google_click);
        googleClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Google Clicked", Toast.LENGTH_SHORT).show();
                GetSignInIntentRequest signInIntentRequest = GetSignInIntentRequest.builder()
                        .setServerClientId(getString(R.string.web_client_id)).build();

                Task<PendingIntent> signInIntent = signInClient.getSignInIntent(signInIntentRequest);
                signInIntent.addOnSuccessListener(new OnSuccessListener<PendingIntent>() {
                    @Override
                    public void onSuccess(PendingIntent pendingIntent) {
                        IntentSenderRequest intentSenderRequest = new IntentSenderRequest
                                .Builder(pendingIntent).build();
                        signInLauncher.launch(intentSenderRequest);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        });



        //ForgotPass

        forgotpassClick= findViewById(R.id.forgotpass_click);
        forgotpassClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "fp Clicked", Toast.LENGTH_SHORT).show();
                //Intent intent= new Intent(getApplicationContext(), ForgotPassword.class);
                //startActivity(intent);
            }
        });


    }



    private void updateUI(FirebaseUser user){
        if(user!=null){

            if(!user.isEmailVerified()){
                Toast.makeText(SignInScreen.this,"Please Verify Your Email..",Toast.LENGTH_LONG).show();
                return;
            }

            Intent intent = new Intent(SignInScreen.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }



    private void firebaseAuthWithGoogle(String idToken){
        AuthCredential authCredential = GoogleAuthProvider.getCredential(idToken, null);
        Task<AuthResult> authResultTask = firebaseAuth.signInWithCredential(authCredential);
        authResultTask.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    updateUIG(user);
                    //checkIfEmailExistsInFirestore(user);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }


    private void handleSignInResult(Intent intent){
        try {
            SignInCredential signInCredential = signInClient.getSignInCredentialFromIntent(intent);
            String idToken = signInCredential.getGoogleIdToken();
            firebaseAuthWithGoogle(idToken);

        } catch (ApiException e) {

        }
    }


    private final ActivityResultLauncher<IntentSenderRequest> signInLauncher =
            registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult o) {
                            handleSignInResult(o.getData());
                        }
                    });


    private void updateUIG(FirebaseUser user){
        if(user!=null){
            Intent intent = new Intent(SignInScreen.this,MainActivity.class);
            startActivity(intent);
            finish();
            storeDataInFirestore(user);
        }
    }


//    private void checkIfEmailExistsInFirestore(FirebaseUser user) {
//
//        String email = user.getEmail();
//        // Check if the email exists in Firestore "users" collection
//        firestore.collection("users")
//                .whereEqualTo("email", email)
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        if (task.getResult() != null && !task.getResult().isEmpty()) {
//                            // Email already exists in Firestore, handle accordingly
//                            Log.d("Firestore", "Email already exists in Firestore");
//                        } else {
//                            // Email does not exist in Firestore, proceed to store it
//                            storeDataInFirestore(user);
//                        }
//                    } else {
//                        // Handle Firestore query failure
//                        Log.e("Firestore", "Error checking email in Firestore", task.getException());
//                    }
//                });
//    }


    private void storeDataInFirestore(FirebaseUser user) {

        String email = user.getEmail();
        String firstName = null;
        String lastName = null;
        String fullname = user.getDisplayName();
        String[] nameParts = fullname.split(" ");
        if (nameParts.length >= 2) {
            firstName = nameParts[0];
            lastName = nameParts[1];
        } else {
            // Display name doesn't contain both first and last names
        }
        // Store the email in Firestore "users" collection
        Map<String, Object> userN = new HashMap<>();
        userN.put("email", email);
        userN.put("firstName", firstName);
        userN.put("lastName", lastName);

        firestore.collection("users")
                .document(email)
                .set(userN)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Email stored successfully in Firestore
                        Log.d("Firestore", "Email stored in Firestore");
                    } else {
                        // Handle Firestore write failure
                        Log.e("Firestore", "Error storing email in Firestore", task.getException());
                    }
                });
    }


}