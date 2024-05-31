package com.jiat.sarongstyles.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jiat.sarongstyles.Adapter.CategoryAdapter;
import com.jiat.sarongstyles.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CategoryViewAll extends AppCompatActivity {

    private ImageView backarrow;
    private FirebaseFirestore firestore;
    private CategoryAdapter categoryAdapter;
    private List<Map<String, Object>> categoryList;
    private RecyclerView recyclerView;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //REMOVE TOOLBAR
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        setContentView(R.layout.activity_category_view_all);

        //Back Arrow

        backarrow=findViewById(R.id.back_arrow);
        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        firestore = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recyclerView);
        categoryList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(categoryList,CategoryViewAll.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(categoryAdapter);

        setUpFirestoreListener();




    }


    private void setUpFirestoreListener() {
        firestore.collection("category")
                .whereEqualTo("status",true)
//                .orderBy("registeredDate", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            // Handle error
                            return;
                        }

                        // Clear previous data
                        categoryList.clear();

                        // Process the new data
                        for (QueryDocumentSnapshot document : value) {
                            Map<String, Object> categoryData = document.getData();
                            categoryData.put("documentId", document.getId());
                            categoryList.add(categoryData);
                        }

                        // Update the UI
                        categoryAdapter.notifyDataSetChanged();
                    }
                });
    }

}