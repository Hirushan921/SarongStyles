package com.jiat.sarongstyles.Fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jiat.sarongstyles.Activity.CategoryViewAll;
import com.jiat.sarongstyles.Activity.ItemViewAll;
import com.jiat.sarongstyles.Adapter.ItemAdapter;
import com.jiat.sarongstyles.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class HomeFragment extends Fragment {




    private TextView newarrivalviewall;

    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private ImageView backarrow;
    private FirebaseFirestore firestore;
    private List<Map<String, Object>> itemList;

    private Context context;
    private Button category_view,sarong_view;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);



        firestore = FirebaseFirestore.getInstance();

        recyclerView = root.findViewById(R.id.recyclerView);
        itemList = new ArrayList<>();
        itemAdapter = new ItemAdapter(itemList,context);
        RecyclerView.LayoutManager mGridLayoutManager = new GridLayoutManager(context, 2);
        recyclerView.setLayoutManager(mGridLayoutManager);
        recyclerView.setAdapter(itemAdapter);

        // get realtime data
        setUpFirestoreListener();


        //Category Select
        category_view =root.findViewById(R.id.button_category);
        category_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a scale animation
                ObjectAnimator scaleX = ObjectAnimator.ofFloat(category_view, "scaleX", 1f, 0.8f);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(category_view, "scaleY", 1f, 0.8f);

                // Combine the scale animations
                AnimatorSet scaleAnimatorSet = new AnimatorSet();
                scaleAnimatorSet.playTogether(scaleX, scaleY);
                scaleAnimatorSet.setDuration(200); // Adjust the duration as needed

                // Create a translation animation
                ObjectAnimator translationY = ObjectAnimator.ofFloat(category_view, "translationY", 0, 50f);

                // Combine the scale and translation animations
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.play(scaleAnimatorSet).before(translationY);

                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        Intent intent = new Intent(getActivity(), CategoryViewAll.class);
                        startActivity(intent);
                        reverseAnimation();
                    }
                });

                // Start the animation
                animatorSet.start();
            }

            private void reverseAnimation() {
                // Create a reverse scale animation
                ObjectAnimator scaleXReverse = ObjectAnimator.ofFloat(category_view, "scaleX", 0.8f, 1f);
                ObjectAnimator scaleYReverse = ObjectAnimator.ofFloat(category_view, "scaleY", 0.8f, 1f);

                // Combine the reverse scale animations
                AnimatorSet reverseScaleAnimatorSet = new AnimatorSet();
                reverseScaleAnimatorSet.playTogether(scaleXReverse, scaleYReverse);
                reverseScaleAnimatorSet.setDuration(200); // Adjust the duration as needed

                // Create a reverse translation animation
                ObjectAnimator translationYReverse = ObjectAnimator.ofFloat(category_view, "translationY", 50f, 0);

                // Combine the reverse scale and translation animations
                AnimatorSet reverseAnimatorSet = new AnimatorSet();
                reverseAnimatorSet.play(reverseScaleAnimatorSet).after(translationYReverse);

                // Start the reverse animation
                reverseAnimatorSet.start();
            }

        });



        //Sarong Select
        sarong_view =root.findViewById(R.id.button_sarong);
        sarong_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a scale animation
                ObjectAnimator scaleX = ObjectAnimator.ofFloat(sarong_view, "scaleX", 1f, 0.8f);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(sarong_view, "scaleY", 1f, 0.8f);

                // Combine the scale animations
                AnimatorSet scaleAnimatorSet = new AnimatorSet();
                scaleAnimatorSet.playTogether(scaleX, scaleY);
                scaleAnimatorSet.setDuration(200); // Adjust the duration as needed

                // Create a translation animation
                ObjectAnimator translationY = ObjectAnimator.ofFloat(sarong_view, "translationY", 0, 50f);

                // Combine the scale and translation animations
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.play(scaleAnimatorSet).before(translationY);

                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        Intent intent = new Intent(getActivity(), ItemViewAll.class);
                        startActivity(intent);
                        reverseAnimation();
                    }
                });

                // Start the animation
                animatorSet.start();
            }


            private void reverseAnimation() {
                // Create a reverse scale animation
                ObjectAnimator scaleXReverse = ObjectAnimator.ofFloat(sarong_view, "scaleX", 0.8f, 1f);
                ObjectAnimator scaleYReverse = ObjectAnimator.ofFloat(sarong_view, "scaleY", 0.8f, 1f);

                // Combine the reverse scale animations
                AnimatorSet reverseScaleAnimatorSet = new AnimatorSet();
                reverseScaleAnimatorSet.playTogether(scaleXReverse, scaleYReverse);
                reverseScaleAnimatorSet.setDuration(200); // Adjust the duration as needed

                // Create a reverse translation animation
                ObjectAnimator translationYReverse = ObjectAnimator.ofFloat(sarong_view, "translationY", 50f, 0);

                // Combine the reverse scale and translation animations
                AnimatorSet reverseAnimatorSet = new AnimatorSet();
                reverseAnimatorSet.play(reverseScaleAnimatorSet).after(translationYReverse);

                // Start the reverse animation
                reverseAnimatorSet.start();
            }

        });




        newarrivalviewall=root.findViewById(R.id.newarrival_viewall);
        newarrivalviewall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), ItemViewAll.class);
                startActivity(intent);

            }
        });


        return  root;
    }



    private void setUpFirestoreListener() {
        firestore.collection("item")
                .whereEqualTo("status",true)
//                .orderBy("registeredDate", Query.Direction.DESCENDING)  // Order by timestamp in ascending order
                .limit(4)
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