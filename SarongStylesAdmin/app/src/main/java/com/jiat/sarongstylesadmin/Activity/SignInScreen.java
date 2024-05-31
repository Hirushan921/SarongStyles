package com.jiat.sarongstylesadmin.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jiat.sarongstylesadmin.R;

public class SignInScreen extends AppCompatActivity {

    private TextInputEditText email_edit,password_edit;
    private ImageView emailCheck;
    private TextView passCheck,forgotpassClick;
    private Button loginButton;
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


        setContentView(R.layout.activity_sign_in_screen);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        emailCheck=(ImageView) findViewById(R.id.email_check);
        passCheck=(TextView) findViewById(R.id.pass_strong);

        //Edit Function

        email_edit=(TextInputEditText)findViewById(R.id.edit_email);
        email_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (email_edit.getText().toString().length()<3) {
                    emailCheck.setVisibility(View.GONE);
                } else if (email_edit.getText().toString().length()>=3) {
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

                    checkIfAdminExists(email, new AdminCheckCallback() {
                        @Override
                        public void onResult(boolean isAdmin) {
                            if (isAdmin) {
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
                            } else {
                                Toast.makeText(SignInScreen.this, "Please Check Your Input Details..", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }


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
            Intent intent = new Intent(SignInScreen.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }


    private void checkIfAdminExists(String email, final AdminCheckCallback callback) {
        firestore.collection("admin")
                .document(email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Admin exists
                                callback.onResult(true);
                            } else {
                                // Admin does not exist
                                callback.onResult(false);
                            }
                        } else {
                            // Handle errors
                            callback.onResult(false);
                        }
                    }
                });
    }

    interface AdminCheckCallback {
        void onResult(boolean isAdmin);
    }

}