package com.jiat.sarongstylesadmin.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jiat.sarongstylesadmin.R;
import com.jiat.sarongstylesadmin.Util.EmailValidator;

import java.util.HashMap;
import java.util.Map;

public class AddUsersActivity extends AppCompatActivity {

    private TextInputEditText firstname_edit,lastname_edit,password_edit,email_edit;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private ImageView backArrow;
    private Button addButton,viewButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //REMOVE TOOLBAR
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        setContentView(R.layout.activity_add_users);

        //BACKARROW

        backArrow=(ImageView) findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        firstname_edit=(TextInputEditText)findViewById(R.id.edit_firstname);
        lastname_edit=(TextInputEditText)findViewById(R.id.edit_lastname);
        email_edit=(TextInputEditText)findViewById(R.id.edit_emailaddress);
        password_edit=(TextInputEditText)findViewById(R.id.edit_password);



        addButton=(Button) findViewById(R.id.button_add_user);
        addButton.setOnClickListener(new View.OnClickListener() {
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

                                        Toast.makeText(AddUsersActivity.this,"User Added Successfully..",Toast.LENGTH_LONG).show();
//                                        Intent intent = new Intent(AddUsersActivity.this,MainActivity.class);
//                                        startActivity(intent);
                                    }else{
                                        //Log.w(TAG,"createUserWithEmailAndPassword:failed");
                                        Toast.makeText(AddUsersActivity.this,"Registration failed",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                }

            }
        });


        viewButton=(Button) findViewById(R.id.button_view_user);
        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddUsersActivity.this,ViewUsersActivity.class);
                startActivity(intent);
            }
        });


    }




    private void storeUserData(String firstName, String lastName, String email) {

        Map<String, Object> user = new HashMap<>();
        user.put("firstName", firstName);
        user.put("lastName", lastName);
        user.put("email", email);
        user.put("type", "Operator");
        // Log.i(TAG,"firestore ekt awaa");

        firestore.collection("admin")
                .document(email)
                .set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                           firstname_edit.setText("");
                           lastname_edit.setText("");
                           email_edit.setText("");
                           password_edit.setText("");
                            // Signup process complete
                            // You can navigate to the next activity or display a success message
                        } else {

                            // Handle errors
                            // You can display a toast or handle errors in a more user-friendly way
                        }
                    }
                });
    }



}