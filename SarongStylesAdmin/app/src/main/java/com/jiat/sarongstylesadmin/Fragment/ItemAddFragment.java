package com.jiat.sarongstylesadmin.Fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jiat.sarongstylesadmin.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class ItemAddFragment extends Fragment {


    private Context context;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private Spinner categorySpinner;
    private Map<String, String> categoryMap;
    private Button saveButton;
    private TextInputEditText itemName_edit,description_edit,qty_edit,price_edit,deliveryCharge_edit;
    private String selectedCategoryName,selectedCategoryId;
    private ImageButton imageButton;
    private Uri imagePath;





    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_item_add, container, false);


        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        categorySpinner = root.findViewById(R.id.categorySpinner);
        imageButton = root.findViewById(R.id.imageButton);

        itemName_edit = root.findViewById(R.id.edit_item_name);
        description_edit = root.findViewById(R.id.edit_description);
        qty_edit = root.findViewById(R.id.edit_quantity);
        price_edit = root.findViewById(R.id.edit_price);
        deliveryCharge_edit = root.findViewById(R.id.edit_delivery_charge);

        categoryMap = new HashMap<>();

        Picasso.get()
                .load(R.drawable.image_add)
                .fit()
                .centerCrop()
                .into(imageButton);

        loadCategories();

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                activityResultLauncher.launch(Intent.createChooser(intent,"Select Image"));
            }
        });

        saveButton=(Button) root.findViewById(R.id.button_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemName = itemName_edit.getText().toString();
                String description = description_edit.getText().toString();
                String qty = qty_edit.getText().toString();
                String price = price_edit.getText().toString();
                String deliveryCharge = deliveryCharge_edit.getText().toString();

                if (itemName.isEmpty()) {
                    Toast.makeText(context, "Please enter item name", Toast.LENGTH_SHORT).show();
                }else if (description.isEmpty()) {
                    Toast.makeText(context, "Please enter description", Toast.LENGTH_SHORT).show();
                }else if (qty.isEmpty()) {
                    Toast.makeText(context, "Please enter quantity", Toast.LENGTH_SHORT).show();
                }else if (price.isEmpty()) {
                    Toast.makeText(context, "Please enter price", Toast.LENGTH_SHORT).show();
                }else if (deliveryCharge.isEmpty()) {
                    Toast.makeText(context, "Please enter delivery charge", Toast.LENGTH_SHORT).show();
                }else if (imagePath==null) {
                    Toast.makeText(context, "Please add item image", Toast.LENGTH_SHORT).show();
                }else if (!qty.matches("^[1-9]\\d*$")) {
                    Toast.makeText(context, "Please enter valid quantity", Toast.LENGTH_SHORT).show();
                }else if (!price.matches("^\\d+(\\.\\d{1,2})?$")) {
                    Toast.makeText(context, "Please enter valid price", Toast.LENGTH_SHORT).show();
                }else if (!deliveryCharge.matches("^\\d+(\\.\\d{1,2})?$")) {
                    Toast.makeText(context, "Please enter valid delivery charge", Toast.LENGTH_SHORT).show();
                }else{

                   double priceValue = Double.parseDouble(price);
                   double deliveryChargeValue = Double.parseDouble(deliveryCharge);

                    selectedCategoryName = (String) categorySpinner.getSelectedItem();
                    if (selectedCategoryName != null) {
                        selectedCategoryId = categoryMap.get(selectedCategoryName);
                        //Log.i("awooo",selectedCategoryId+selectedCategoryName);
                    }
                    //Toast.makeText(context, "okkoma hari "+imagePath, Toast.LENGTH_SHORT).show();
                    checkItem(itemName,selectedCategoryId,description,qty,priceValue,deliveryChargeValue);

                }
            }
        });



        return  root;
    }



    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode()== Activity.RESULT_OK){
                        imagePath = result.getData().getData();
                        //Log.i(TAG,"image Path: "+imagePath.getPath());

                        Picasso.get()
                                .load(imagePath)
                                .fit()
                                .centerCrop()
                                .into(imageButton);

                    }
                }
            }
    );







    private void loadCategories(){
        firestore.collection("category")
                .whereEqualTo("status",true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> categoryNames = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String categoryId = document.getId();
                                String categoryName = document.getString("categoryName");

                                categoryMap.put(categoryName, categoryId);
                                categoryNames.add(categoryName);
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, categoryNames);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            categorySpinner.setAdapter(adapter);

                        } else {
                            System.err.println("Error querying categories: " + task.getException().getMessage());
                        }
                    }
                });
    }



    public void checkItem(String itemName, String categoryId, String description, String qty, Double price, Double deliveryCharge) {

        String lowerCaseItemName = itemName.toLowerCase();

        firestore.collection("item")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    boolean itemExists = false;
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String storedItemName = documentSnapshot.getString("itemName");
                        if (storedItemName != null) {
                            String lowerStoredItemName = storedItemName.toLowerCase();
                            if (lowerStoredItemName.equals(lowerCaseItemName)) {
                                itemExists = true;
                                break;
                            }
                        }
                    }

                    if (!itemExists) {
                        storeItemData(itemName,categoryId,description,qty,price,deliveryCharge);
                    } else {
                        Toast.makeText(context,"Item with the same name already exists",Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error querying items: " + e.getMessage());
                });
    }



    private void storeItemData(String itemName, String categoryId, String description, String qty, Double price, Double deliverCharge) {

        //Log.i("awoooo",itemName+" "+categoryId+" "+description+" "+qty+" "+price+" "+deliverCharge);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = dateFormat.format(new Date());

        String imageId = UUID.randomUUID().toString();

        Map<String, Object> item = new HashMap<>();
        item.put("itemName", itemName);
        item.put("categoryId", categoryId);
        item.put("description", description);
        item.put("qty", qty);
        item.put("price", price);
        item.put("deliveryCharge", deliverCharge);
        item.put("registeredDate", formattedDate);
        item.put("image", imageId);
        item.put("status",true);

        String currentTime = String.valueOf(System.currentTimeMillis());
        String itemId = "I"+currentTime;

        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Adding new item");
        dialog.setCancelable(false);
        dialog.show();

        firestore.collection("item")
                .document(itemId)
                .set(item)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dialog.setMessage("Uploading image...");

                        StorageReference reference = storage.getReference("item-images")
                                .child(imageId);
                        reference.putFile(imagePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                dialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.dismiss();
                                Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                                dialog.setMessage("Uploading "+(int)progress+"%");
                            }
                        });

                        Toast.makeText(context, "Item Saved Successfully", Toast.LENGTH_LONG).show();
                        itemName_edit.setText("");
                        description_edit.setText("");
                        qty_edit.setText("");
                        price_edit.setText("");
                        deliveryCharge_edit.setText("");
                        categorySpinner.setSelection(0);
                        Picasso.get()
                                .load(R.drawable.image_add)
                                .fit()
                                .centerCrop()
                                .into(imageButton);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        System.err.println("Error adding item: " + e.getMessage());
                    }
                });
    }








}