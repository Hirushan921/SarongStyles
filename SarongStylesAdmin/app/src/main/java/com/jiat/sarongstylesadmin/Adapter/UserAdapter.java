package com.jiat.sarongstylesadmin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.jiat.sarongstylesadmin.R;

import java.util.List;
import java.util.Map;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<Map<String, Object>> userList;
    private Context context;

    public UserAdapter(List<Map<String, Object>> userList, Context context) {
        this.context=context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_view_list, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        Map<String, Object> userData = userList.get(position);

        holder.nametextView.setText(String.valueOf(userData.get("firstName"))+" "+String.valueOf(userData.get("lastName")));
        holder.emailtextView.setText(String.valueOf(userData.get("email")));
        holder.typeTextView.setText(String.valueOf(userData.get("type")));






    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nametextView,emailtextView,typeTextView;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nametextView = itemView.findViewById(R.id.fullname);
            emailtextView = itemView.findViewById(R.id.email);
            typeTextView = itemView.findViewById(R.id.type);



        }
    }



}