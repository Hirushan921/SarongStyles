package com.jiat.sarongstyles.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jiat.sarongstyles.R;

public class ShopDataActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //REMOVE TOOLBAR
        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e) {
        }
        setContentView(R.layout.activity_shop_data);


      RelativeLayout callButton = findViewById(R.id.call_shop);
      callButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {

              String phoneNumber = "tel:" + "0741225494";

              Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(phoneNumber));

              startActivity(dialIntent);

          }

      });

    }


}