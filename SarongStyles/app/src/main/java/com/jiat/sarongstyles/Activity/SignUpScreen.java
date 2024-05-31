package com.jiat.sarongstyles.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jiat.sarongstyles.R;
import com.jiat.sarongstyles.Util.EmailValidator;

import java.util.HashMap;
import java.util.Map;

public class SignUpScreen extends AppCompatActivity {

    private static final String TAG = SignUpScreen.class.getName();

    private TextInputEditText firstname_edit,lastname_edit,password_edit,email_edit;
    private ImageView firstnameCheck,lastnameCheck,backArrow,emailCheck;
    private TextView passCheck,signinClick;
    private Button signupButton;
    private RelativeLayout googleClick;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;


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

        setContentView(R.layout.activity_sign_up_screen);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        firstnameCheck=(ImageView) findViewById(R.id.firstname_check);
        lastnameCheck=(ImageView) findViewById(R.id.lastname_check);
        passCheck=(TextView) findViewById(R.id.pass_strong);
        emailCheck=(ImageView) findViewById(R.id.email_check);

        //Edit Function

        firstname_edit=(TextInputEditText)findViewById(R.id.edit_firstname);
        firstname_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (firstname_edit.getText().toString().length()<3) {
                    firstnameCheck.setVisibility(View.GONE);
                } else if (firstname_edit.getText().toString().length()>=3) {
                    firstnameCheck.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        lastname_edit=(TextInputEditText)findViewById(R.id.edit_lastname);
        lastname_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (lastname_edit.getText().toString().length()<3) {
                    lastnameCheck.setVisibility(View.GONE);
                } else if (lastname_edit.getText().toString().length()>=3) {
                    lastnameCheck.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


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
                Intent intent= new Intent(getApplicationContext(), SignInScreen.class);
                startActivity(intent);
            }
        });



        signinClick=(TextView) findViewById(R.id.signin_click);
        signinClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(), SignInScreen.class);
                startActivity(intent);

            }
        });

        //SignupButton

        signupButton=(Button) findViewById(R.id.button_signUp);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String firstName = firstname_edit.getText().toString();
                String lastName = lastname_edit.getText().toString();
                String email = email_edit.getText().toString();
                String password = password_edit.getText().toString();

                if (firstName.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter first name", Toast.LENGTH_SHORT).show();
                }else if (lastName.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter last name", Toast.LENGTH_SHORT).show();
                } else if (email.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter email address", Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please create password", Toast.LENGTH_SHORT).show();
                } else if (password.length()<6) {
                    Toast.makeText(getApplicationContext(), "Password must be contain 6 characters", Toast.LENGTH_SHORT).show();
                } else if (!EmailValidator.validateEmail(email)) {
                    Toast.makeText(getApplicationContext(), "Invalid Email Address", Toast.LENGTH_SHORT).show();
                    return;
                }else{

                    firebaseAuth.createUserWithEmailAndPassword(email,password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        //Log.i(TAG,"createUserWithEmailAndPassword:Success");
                                        storeUserData(firstName, lastName, email);
                                        FirebaseUser authUser = firebaseAuth.getCurrentUser();
                                        authUser.sendEmailVerification();

                                        Toast.makeText(SignUpScreen.this,"Sign up success & Please verify your email..",Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(SignUpScreen.this,SignInScreen.class);
                                        startActivity(intent);
                                    }else{
                                        //Log.w(TAG,"createUserWithEmailAndPassword:failed");
                                        Toast.makeText(SignUpScreen.this,"Registration failed",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                }

            }
        });






    }




    private void storeUserData(String firstName, String lastName, String email) {

        Map<String, Object> user = new HashMap<>();
        user.put("firstName", firstName);
        user.put("lastName", lastName);
        user.put("email", email);
       // Log.i(TAG,"firestore ekt awaa");

        firestore.collection("users")
                .document(email)
                .set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.i(TAG,"firestore done");
                            // Signup process complete
                            // You can navigate to the next activity or display a success message
                        } else {
                            Log.i(TAG,"firestore failed");
                            // Handle errors
                            // You can display a toast or handle errors in a more user-friendly way
                        }
                    }
                });
    }




}