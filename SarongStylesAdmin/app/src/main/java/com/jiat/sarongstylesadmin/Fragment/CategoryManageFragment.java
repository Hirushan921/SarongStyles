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
import com.jiat.sarongstylesadmin.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CategoryManageFragment extends Fragment {

    private RecyclerView recyclerView;
    private Button saveButton;
    private TextInputEditText categoryEdit;
    private FirebaseFirestore firestore;
    private CategoryAdapter categoryAdapter;
    private List<Map<String, Object>> categoryList; // Change to Map

    private Context context;



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_category_manage, container, false);

        firestore = FirebaseFirestore.getInstance();

        recyclerView = root.findViewById(R.id.recyclerView);
        categoryList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(categoryList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(categoryAdapter);

        // get realtime data
        setUpFirestoreListener();

        saveButton=root.findViewById(R.id.button_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryEdit = root.findViewById(R.id.edit_category_name);
                String categoryName = categoryEdit.getText().toString();
                if (categoryName.isEmpty()) {
                    Toast.makeText(context, "Please enter category name", Toast.LENGTH_SHORT).show();
                }else{
                    checkCategory(categoryName);
                }
            }
        });



        return root;
    }



    public void checkCategory(String categoryName) {

        String lowerCaseCategoryName = categoryName.toLowerCase();

        firestore.collection("category")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    boolean categoryExists = false;
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String storedCategoryName = documentSnapshot.getString("categoryName");
                        if (storedCategoryName != null) {
                          String lowerStoredCategoryName = storedCategoryName.toLowerCase();
                            if (lowerStoredCategoryName.equals(lowerCaseCategoryName)) {
                                categoryExists = true;
                                break;
                            }
                        }
                    }

                    if (!categoryExists) {
                        addCategory(categoryName);
                    } else {
                        Toast.makeText(context,"Category with the same name already exists",Toast.LENGTH_LONG).show();
                        //System.out.println("Category with the same name already exists");
                    }
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error querying categories: " + e.getMessage());
                });
    }


    public void addCategory(String cName){

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = dateFormat.format(new Date());

        Map<String, Object> category = new HashMap<>();
        category.put("categoryName", cName);
        category.put("registeredDate", formattedDate);
        category.put("status", true);

        String currentTime = String.valueOf(System.currentTimeMillis());
        String categoryId = "C"+currentTime;

        firestore.collection("category")
                .document(categoryId)
                .set(category)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context,"Category Saved Successfully",Toast.LENGTH_LONG).show();
                    categoryEdit.setText("");
                    //System.out.println("Category added with ID: " + categoryId);
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error adding category: " + e.getMessage());
                });

    }



    private void setUpFirestoreListener() {
        firestore.collection("category")
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