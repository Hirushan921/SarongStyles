package com.jiat.sarongstyles.Fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jiat.sarongstyles.Adapter.ItemAdapter;
import com.jiat.sarongstyles.Adapter.WishlistAdapter;
import com.jiat.sarongstyles.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class WishlistFragment extends Fragment {

    private RecyclerView recyclerView;
    private WishlistAdapter wishlistAdapter;
    private List<Map<String, Object>> wishList;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;




    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = getActivity();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_wishlist, container, false);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        recyclerView = root.findViewById(R.id.recyclerView);
        wishList = new ArrayList<>();
        wishlistAdapter = new WishlistAdapter(wishList,context);
        RecyclerView.LayoutManager mGridLayoutManager = new GridLayoutManager(context, 2);
        recyclerView.setLayoutManager(mGridLayoutManager);
        recyclerView.setAdapter(wishlistAdapter);

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            setUpFirestoreListener(userEmail);
        }else{

        }


        return  root;
    }



    private void setUpFirestoreListener(String userEmail) {
        CollectionReference userWishCollection = firestore.collection("users").document(userEmail).collection("wishlist");

        userWishCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }

                wishList.clear();

                for (QueryDocumentSnapshot document : value) {
                    Map<String, Object> wishData = document.getData();
                    wishData.put("wishDocumentId", document.getId());
                    //wishList.add(wishData);

                    String itemId = String.valueOf(wishData.get("itemID"));
                    fetchItemData(itemId, wishData);

                }

               // wishlistAdapter.notifyDataSetChanged();

            }
        });

    }

    private void fetchItemData(String itemId, Map<String, Object> wishData) {
        firestore.collection("item").document(itemId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Merge the additional item data into the wishData map
                        Map<String, Object> itemData = documentSnapshot.getData();
                        itemData.put("documentId",documentSnapshot.getId());
                        wishData.putAll(itemData);

                        // Add the merged data to the wishList
                        wishList.add(wishData);

                        // Notify the adapter that the data has changed
                        wishlistAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle the failure to fetch additional item data
                });
    }


}