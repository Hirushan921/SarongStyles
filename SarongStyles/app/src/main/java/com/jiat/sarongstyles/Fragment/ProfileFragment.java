package com.jiat.sarongstyles.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jiat.sarongstyles.Activity.DeliveryDetailsActivity;
import com.jiat.sarongstyles.Activity.OrderDetailsActivity;
import com.jiat.sarongstyles.Activity.PasswordActivity;
import com.jiat.sarongstyles.R;

public class ProfileFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private Context context;
    private RelativeLayout profileorderlist,profilenotification,profilepassword,profileUpdate;
    private FirebaseUser currentUser;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();



        currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            firestore.collection("users")
                    .document(userEmail)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String f_name = documentSnapshot.getString("firstName");
                            String l_name = documentSnapshot.getString("lastName");
                            TextView user_n = root.findViewById(R.id.txt_name);
                            user_n.setText(f_name+" "+l_name);
                            TextView user_m = root.findViewById(R.id.txt_mail);
                            user_m.setText(userEmail);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }else{

        }



        //OrderList Click

        profileorderlist=root.findViewById(R.id.profile_orderlist);
        profileorderlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), OrderDetailsActivity.class);
                startActivity(intent);

            }
        });

        //Notification Click

        profilenotification=root.findViewById(R.id.profile_notification);
        profilenotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

        //Password Click

        profilepassword=root.findViewById(R.id.profile_password);
        profilepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), PasswordActivity.class);
                startActivity(intent);

            }
        });

        //update Click

        profileUpdate=root.findViewById(R.id.profile_update);
        profileUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,DeliveryDetailsActivity.class);
                startActivity(intent);
            }
        });











        return  root;
    }

}