package com.jiat.sarongstylesadmin.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jiat.sarongstylesadmin.R;

public class AccountActivity extends AppCompatActivity {

    private TextInputEditText firstname_edit,lastname_edit,password_edit,email_edit,password_new;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private ImageView backArrow;
    private Button updatePass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //REMOVE TOOLBAR
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        setContentView(R.layout.activity_account);



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
        password_new=(TextInputEditText)findViewById(R.id.new_password);


        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            firestore.collection("admin")
                    .document(userEmail)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String f_name = documentSnapshot.getString("firstName");
                            String l_name = documentSnapshot.getString("lastName");
                            String email = documentSnapshot.getString("email");

                            firstname_edit.setText(f_name);
                            lastname_edit.setText(l_name);
                            email_edit.setText(email);


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }


        updatePass = (Button) findViewById(R.id.button_pass_update);
        updatePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              String currentPassword = String.valueOf(password_edit.getText());
              String newPassword = String.valueOf(password_new.getText());

              if(currentPassword.isEmpty()){
                  Toast.makeText(AccountActivity.this, "Current password field is empty", Toast.LENGTH_SHORT).show();
              } else if (newPassword.isEmpty()) {
                  Toast.makeText(AccountActivity.this, "New password field is empty", Toast.LENGTH_SHORT).show();
              }else if(newPassword.toString().length()<6){
                  Toast.makeText(AccountActivity.this, "New password must be contain 6 characters", Toast.LENGTH_SHORT).show();
              }else{

                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseUser user = auth.getCurrentUser();

                // Reauthenticate the user
                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // User has been reauthenticated, update the password
                                    user.updatePassword(newPassword)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(AccountActivity.this, "Password Changed", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(AccountActivity.this,MainActivity.class);
                                                        startActivity(intent);
                                                    } else {
                                                        Toast.makeText(AccountActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    Toast.makeText(AccountActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
              }

            }
        });


    }
}