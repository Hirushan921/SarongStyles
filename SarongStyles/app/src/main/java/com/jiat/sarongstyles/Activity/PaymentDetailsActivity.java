package com.jiat.sarongstyles.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.jiat.sarongstyles.R;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PaymentDetailsActivity extends AppCompatActivity {

    private AppCompatEditText edit_card_name,edit_card_number,edit_exp,edit_cvv;
    private ImageView backarrow;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private RelativeLayout saveDataButton;
    private Fragment fragment;
    private CollectionReference userPayCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //REMOVE TOOLBAR
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        setContentView(R.layout.activity_payment_details);

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

        edit_card_name=findViewById(R.id.edit_cardname);
        edit_card_number=findViewById(R.id.edit_cardnumber);
        edit_exp=findViewById(R.id.edit_exp);
        edit_cvv=findViewById(R.id.edit_cvv);

        loadSavedDetails();

        saveDataButton=findViewById(R.id.save_data);
        saveDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {

                    String cardName = edit_card_name.getText().toString();
                    String cardNumber = edit_card_number.getText().toString();
                    String cvv = edit_cvv.getText().toString();
                    String exp = edit_exp.getText().toString();

                    if (cardName.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Please enter card name", Toast.LENGTH_SHORT).show();
                    }else if (cardNumber.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Please enter card number", Toast.LENGTH_SHORT).show();
                    } else if (exp.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Please enter exp", Toast.LENGTH_SHORT).show();
                    } else if(cvv.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Invalid enter cvv", Toast.LENGTH_SHORT).show();
                    }else{
                        //Toast.makeText(DeliveryDetailsActivity.this, fullName+mobileNumber+address, Toast.LENGTH_SHORT).show();

                        String userEmail = currentUser.getEmail();
                        userPayCollection = firestore.collection("users").document(userEmail).collection("paymentDetails");


                        Map<String, Object> payData = new HashMap<>();
                        payData.put("cardName", cardName);
                        payData.put("cardNumber", cardNumber);
                        payData.put("cvv", cvv);
                        payData.put("exp", exp);

                        checkPaymentDetailsExists(userEmail,payData);

                    }



                } else {
                    Toast.makeText(getApplicationContext(), "log wela ne yakooo", Toast.LENGTH_SHORT).show();
                }


            }
        });



    }




    private void checkPaymentDetailsExists(String userEmail,Map<String, Object>  payData) {

        firestore.collection("users")
                .document(userEmail)
                .collection("paymentDetails")
                .limit(1)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        if (!querySnapshot.isEmpty()) {
                            DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                            String documentId = documentSnapshot.getId();
                            //update saved data
                            updatePaymentDetails(userEmail,payData,documentId);
                        } else {
                            //save new data
                            savePaymentDetails(userEmail,payData);
                        }
                    }
                })
                .addOnFailureListener(e -> {

                });
    }


    private void updatePaymentDetails(String userEmail, Map<String, Object> payData,String documentId) {
        userPayCollection.document(documentId)
                .set(payData, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(PaymentDetailsActivity.this, "Payment Details Updated Successfully.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {

                });
    }

    private void savePaymentDetails(String userEmail, Map<String, Object> payData) {

        userPayCollection.add(payData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(PaymentDetailsActivity.this, "Payment Details Saved Successfully.", Toast.LENGTH_SHORT).show();
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
                    .collection("paymentDetails")
                    .limit(1)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot querySnapshot) {
                            if (!querySnapshot.isEmpty()) {
                                DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                                String documentId = documentSnapshot.getId();
                                String card_name = documentSnapshot.getString("cardName");
                                String card_number = documentSnapshot.getString("cardNumber");
                                String expP = documentSnapshot.getString("exp");
                                String cvvP = documentSnapshot.getString("cvv");

                                edit_card_name.setText(card_name);
                                edit_card_number.setText(card_number);
                                edit_exp.setText(expP);
                                edit_cvv.setText(cvvP);

                            } else {

                            }
                        }
                    })
                    .addOnFailureListener(e -> {

                    });
        }

    }





}