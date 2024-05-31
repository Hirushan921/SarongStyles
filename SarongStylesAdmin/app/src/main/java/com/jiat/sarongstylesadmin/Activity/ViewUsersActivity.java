package com.jiat.sarongstylesadmin.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jiat.sarongstylesadmin.Adapter.OrderHistoryAdapter;
import com.jiat.sarongstylesadmin.Adapter.UserAdapter;
import com.jiat.sarongstylesadmin.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ViewUsersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseFirestore firestore;
    private ImageView backarrow;
    private UserAdapter userAdapter;
    private List<Map<String, Object>> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //REMOVE TOOLBAR
        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e) {
        }

        setContentView(R.layout.activity_view_users);

        firestore = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recyclerViewU);
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList,ViewUsersActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(userAdapter);

        // get realtime data
        setUpFirestoreListener();

        //Back Arrow
        backarrow=findViewById(R.id.back_arrow);
        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();

            }
        });
    }

    private void setUpFirestoreListener() {
        firestore.collection("admin")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            // Handle error
                            return;
                        }

                        // Clear previous data
                        userList.clear();

                        // Process the new data
                        for (QueryDocumentSnapshot document : value) {
                            Map<String, Object> userData = document.getData();
                            userData.put("documentId", document.getId());

                            userList.add(userData);
                        }

                        // Update the UI
                        userAdapter.notifyDataSetChanged();
                    }
                });
    }


}