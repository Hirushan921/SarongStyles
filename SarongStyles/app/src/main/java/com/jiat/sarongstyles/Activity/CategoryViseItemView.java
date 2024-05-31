package com.jiat.sarongstyles.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jiat.sarongstyles.Adapter.ItemAdapter;
import com.jiat.sarongstyles.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CategoryViseItemView extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private ImageView backarrow;
    private FirebaseFirestore firestore;
    private List<Map<String, Object>> itemList;
    private String categoryID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //REMOVE TOOLBAR
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        setContentView(R.layout.activity_category_vise_item_view);

        firestore = FirebaseFirestore.getInstance();

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        itemList = new ArrayList<>();
        itemAdapter = new ItemAdapter(itemList,CategoryViseItemView.this);
        RecyclerView.LayoutManager mGridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(mGridLayoutManager);
        recyclerView.setAdapter(itemAdapter);


        Intent intent = getIntent();
        if (intent.hasExtra("categoryData")) {
            Map<String, Object> categoryData = (Map<String, Object>) intent.getSerializableExtra("categoryData");
            categoryID = String.valueOf(categoryData.get("documentId"));
            TextView ftitle = findViewById(R.id.fragment_title);
            ftitle.setText(String.valueOf(categoryData.get("categoryName")));
        }


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
        firestore.collection("item")
                .whereEqualTo("categoryId",categoryID)
                .orderBy("registeredDate", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }

                        itemList.clear();

                        for (QueryDocumentSnapshot document : value) {
                            Map<String, Object> itemData = document.getData();
                            itemData.put("documentId", document.getId());
                            itemList.add(itemData);
                        }

                        itemAdapter.notifyDataSetChanged();
                    }
                });
    }



}

