package com.jiat.sarongstyles.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jiat.sarongstyles.R;

public class PasswordActivity extends AppCompatActivity {

    private TextInputEditText edit_password_old,edit_password_new;
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

        setContentView(R.layout.activity_password);

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

        edit_password_old=(TextInputEditText)findViewById(R.id.edit_password_old);
        edit_password_new=(TextInputEditText)findViewById(R.id.edit_password_new);



        updatePass = (Button) findViewById(R.id.button_password_change);
        updatePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentPassword = String.valueOf(edit_password_old.getText());
                String newPassword = String.valueOf(edit_password_new.getText());

                if(currentPassword.isEmpty()){
                    Toast.makeText(PasswordActivity.this, "Current password field is empty", Toast.LENGTH_SHORT).show();
                } else if (newPassword.isEmpty()) {
                    Toast.makeText(PasswordActivity.this, "New password field is empty", Toast.LENGTH_SHORT).show();
                }else if(newPassword.toString().length()<6){
                    Toast.makeText(PasswordActivity.this, "New password must be contain 6 characters", Toast.LENGTH_SHORT).show();
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

                                        user.updatePassword(newPassword)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(PasswordActivity.this, "Password Changed", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(PasswordActivity.this,MainActivity.class);
                                                            startActivity(intent);
                                                        } else {
                                                            Toast.makeText(PasswordActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    } else {
                                        Toast.makeText(PasswordActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

            }
        });



    }
}