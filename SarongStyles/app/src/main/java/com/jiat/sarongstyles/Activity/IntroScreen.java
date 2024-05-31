package com.jiat.sarongstyles.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.jiat.sarongstyles.Adapter.ImageSliderAdapter;
import com.jiat.sarongstyles.R;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class IntroScreen extends AppCompatActivity {

    //Image Slider
    private static ViewPager mPager;
    private static final Integer[] img = {R.drawable.slider01,R.drawable.slider02,R.drawable.slider03,R.drawable.slider04};
    private ArrayList<Integer> ImgArray = new ArrayList<Integer>();
    private static int currentPage = 0;
    private RelativeLayout guest_click,login_click;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();

        //REMOVE STATUSBAR
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //REMOVE TOOLBAR
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}


        setContentView(R.layout.activity_intro_screen);

        init();


        guest_click =(RelativeLayout)findViewById(R.id.guest_click);
        guest_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                Intent intent = new Intent(IntroScreen.this,MainActivity.class);
                startActivity(intent);

            }
        });



        login_click =(RelativeLayout)findViewById(R.id.log_in_click);
        login_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IntroScreen.this,SignInScreen.class);
                startActivity(intent);
            }
        });


    }

    private void init (){

        //IMAGE SLIDE

        for (int i=0; i<img.length; i++)
            ImgArray.add(img[i]);

        mPager = (ViewPager) findViewById(R.id.imagepager);
        mPager.setAdapter(new ImageSliderAdapter(IntroScreen.this, ImgArray));

        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            @Override
            public void run() {
                if (currentPage == img.length){
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        //Auto start
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 3000);



    }










}