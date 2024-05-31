package com.jiat.sarongstyles.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.jiat.sarongstyles.Fragment.CartFragment;
import com.jiat.sarongstyles.Fragment.ProfileFragment;
import com.jiat.sarongstyles.R;
import com.jiat.sarongstyles.Util.EmailValidator;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeliveryDetailsActivity extends AppCompatActivity {

    private AppCompatEditText edit_full_name,edit_mobile_number,edit_address;
    private ImageView backarrow;
    private TextView locationText,locationadded;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private RelativeLayout saveDataButton;
    private Fragment fragment;
    private CollectionReference userDelCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //REMOVE TOOLBAR
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        setContentView(R.layout.activity_delivery_details);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        //Back Arrow
        backarrow=findViewById(R.id.back_arrow);
        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



        edit_full_name=findViewById(R.id.edit_full_name);
        edit_mobile_number=findViewById(R.id.edit_mobile_number);
        edit_address=findViewById(R.id.edit_address);

        loadSavedDetails();

        saveDataButton=findViewById(R.id.save_data);
        saveDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {

                    String fullName = edit_full_name.getText().toString();
                    String mobileNumber = edit_mobile_number.getText().toString();
                    String address = edit_address.getText().toString();

                    if (fullName.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Please enter full name", Toast.LENGTH_SHORT).show();
                    }else if (mobileNumber.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Please enter mobile number", Toast.LENGTH_SHORT).show();
                    } else if (address.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Please enter address", Toast.LENGTH_SHORT).show();
                    } else if(!isValidMobileNumber(mobileNumber)) {
                        Toast.makeText(getApplicationContext(), "Invalid mobile number", Toast.LENGTH_SHORT).show();
                    }else{
                        //Toast.makeText(DeliveryDetailsActivity.this, fullName+mobileNumber+address, Toast.LENGTH_SHORT).show();

                        String userEmail = currentUser.getEmail();
                        userDelCollection = firestore.collection("users").document(userEmail).collection("deliveryDetails");


                        Map<String, Object> delData = new HashMap<>();
                        delData.put("fullName", fullName);
                        delData.put("mobileNumber", mobileNumber);
                        delData.put("address", address);

                        checkDeliveryDetailsExists(userEmail,delData);

                    }



                } else {
                    Toast.makeText(getApplicationContext(), "log wela ne yakooo", Toast.LENGTH_SHORT).show();
                }


            }
        });



    }




    private void checkDeliveryDetailsExists(String userEmail,Map<String, Object>  delData) {

        firestore.collection("users")
                .document(userEmail)
                .collection("deliveryDetails")
                .limit(1)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        if (!querySnapshot.isEmpty()) {
                            DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                            String documentId = documentSnapshot.getId();
                            //update saved data
                            updateDeliveryDetails(userEmail,delData,documentId);
                        } else {
                           //save new data
                            saveDeliveryDetails(userEmail,delData);
                        }
                    }
                })
                .addOnFailureListener(e -> {

                });
    }


    private void updateDeliveryDetails(String userEmail, Map<String, Object> delData,String documentId) {
      userDelCollection.document(documentId)
                .set(delData, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(DeliveryDetailsActivity.this, "Delivery Details Updated Successfully.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {

                });
    }

    private void saveDeliveryDetails(String userEmail, Map<String, Object> delData) {

        userDelCollection.add(delData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(DeliveryDetailsActivity.this, "Delivery Details Saved Successfully.", Toast.LENGTH_SHORT).show();
//                        MainActivity mainActivity = (MainActivity) getParent();
//                        // Switch to the CartFragment
//                        mainActivity.switchToFragment(new CartFragment());
//                        finish();
                    }
                })
                .addOnFailureListener(e -> {

                });
    }


    private void loadSavedDetails() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            firestore.collection("users")
                    .document(userEmail)
                    .collection("deliveryDetails")
                    .limit(1)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot querySnapshot) {
                            if (!querySnapshot.isEmpty()) {
                                DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                                String documentId = documentSnapshot.getId();
                                String full_name = documentSnapshot.getString("fullName");
                                String mobile_number = documentSnapshot.getString("mobileNumber");
                                String addressD = documentSnapshot.getString("address");

                                edit_full_name.setText(full_name);
                                edit_mobile_number.setText(mobile_number);
                                edit_address.setText(addressD);

                            } else {

                            }
                        }
                    })
                    .addOnFailureListener(e -> {

                    });
        }

    }



    public static boolean isValidMobileNumber(String mobileNumber) {
        String regex = "^\\d{10}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(mobileNumber);
        return matcher.matches();
    }

}