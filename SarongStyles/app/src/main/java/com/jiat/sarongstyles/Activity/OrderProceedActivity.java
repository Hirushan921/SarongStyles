package com.jiat.sarongstyles.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jiat.sarongstyles.R;

import java.io.Serializable;
import java.util.Map;

public class OrderProceedActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private ImageView backarrow;

    String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //REMOVE TOOLBAR
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        setContentView(R.layout.activity_order_proceed);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        //Back Arrow
        backarrow=findViewById(R.id.back_Home);
        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderProceedActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        if (intent.hasExtra("YourOrderId")) {
            orderId = (String) intent.getSerializableExtra("YourOrderId");
            TextView orderText = findViewById(R.id.order_text);
            orderText.setText("Congratulations! and Thank You! for your Order. " +
                    "For check your order details please click below button. Your Order No is "+orderId);
        }

        Button mapSetButton =findViewById(R.id.mapSetButton);
        mapSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderProceedActivity.this,MapActivity.class);
                intent.putExtra("OrderId",(Serializable) orderId);
                startActivity(intent);
            }
        });


        AppCompatButton goToOrder =findViewById(R.id.button_View_order);
        goToOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderProceedActivity.this,OrderDetailsActivity.class);
                startActivity(intent);
            }
        });

    }
}