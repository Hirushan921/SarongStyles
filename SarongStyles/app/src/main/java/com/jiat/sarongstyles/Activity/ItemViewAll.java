package com.jiat.sarongstyles.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jiat.sarongstyles.Adapter.ItemAdapter;
import com.jiat.sarongstyles.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ItemViewAll extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private ImageView backarrow;
    private FirebaseFirestore firestore;
    private List<Map<String, Object>> itemList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //REMOVE TOOLBAR
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        setContentView(R.layout.activity_item_view_all);

        firestore = FirebaseFirestore.getInstance();

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        itemList = new ArrayList<>();
        itemAdapter = new ItemAdapter(itemList,ItemViewAll.this);
        RecyclerView.LayoutManager mGridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(mGridLayoutManager);
        recyclerView.setAdapter(itemAdapter);

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
                .whereEqualTo("status",true)
//                .orderBy("registeredDate", Query.Direction.DESCENDING)
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