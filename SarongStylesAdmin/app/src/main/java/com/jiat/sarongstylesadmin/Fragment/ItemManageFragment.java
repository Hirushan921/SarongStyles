package com.jiat.sarongstylesadmin.Fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jiat.sarongstylesadmin.Adapter.CategoryAdapter;
import com.jiat.sarongstylesadmin.Adapter.ItemAdapter;
import com.jiat.sarongstylesadmin.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ItemManageFragment extends Fragment {

    private RecyclerView recyclerView;
    private Button updateButton,deleteButton;
    private FirebaseFirestore firestore;
    private ItemAdapter itemAdapter;
    private List<Map<String, Object>> itemList; // Change to Map

    private Context context;



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_item_manage, container, false);

        firestore = FirebaseFirestore.getInstance();

        recyclerView = root.findViewById(R.id.recyclerView);
        itemList = new ArrayList<>();
        itemAdapter = new ItemAdapter(itemList,context);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(itemAdapter);

        // get realtime data
        setUpFirestoreListener();





        return root;
    }






    private void setUpFirestoreListener() {
        firestore.collection("item")
                .whereEqualTo("status",true)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            // Handle error
                            return;
                        }

                        // Clear previous data
                        itemList.clear();

                        // Process the new data
                        for (QueryDocumentSnapshot document : value) {
                            Map<String, Object> itemData = document.getData();
                            itemData.put("documentId", document.getId());
                            itemList.add(itemData);
                        }

                        // Update the UI
                        itemAdapter.notifyDataSetChanged();
                    }
                });
    }



}