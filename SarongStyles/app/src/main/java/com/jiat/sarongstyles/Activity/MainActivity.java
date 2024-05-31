package com.jiat.sarongstyles.Activity;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jiat.sarongstyles.Fragment.CartFragment;
import com.jiat.sarongstyles.Fragment.HomeFragment;
import com.jiat.sarongstyles.Fragment.ProfileFragment;
import com.jiat.sarongstyles.Fragment.WishlistFragment;
import com.jiat.sarongstyles.R;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, SensorEventListener {

    public static final String TAG = MainActivity.class.getName();
    private BottomNavigationView bottomNavigationView;

    private ImageView menuClick,menuClose,backArrow,logoIcon,shopclick;
    private TextView fragmentTitle;
    private RelativeLayout shopLayout;
    private long long_current_time;
    private int int_select_menu_id =0;
    private LinearLayout accountInformation,myCart,wishlist,helpCenter,privacyPolicy,logOut_layout,logIn_layout;
    private Fragment fragment;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private SensorManager sensorManager;
    private FirebaseUser currentUser;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //REMOVE TOOLBAR
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}


        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        firestore = FirebaseFirestore.getInstance();

        loadFragment(new HomeFragment());
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (accelerometerSensor != null) {
                sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }


        //NavigationDrawer Open
        menuClick=findViewById(R.id.nav_click);
        NavigationView nav_view = (NavigationView) findViewById(R.id.nav_view);
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        menuClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });


        //NavigationDrawer Close

        menuClose=findViewById(R.id.menu_close);
        menuClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                drawer.closeDrawers();
            }
        });


        //Back Arrow

        backArrow=findViewById(R.id.back_arrow);

        //Fragment Title

        fragmentTitle=findViewById(R.id.fragment_title);

        //Fragment Icon

        logoIcon=findViewById(R.id.logo_icon);



        shopLayout=findViewById(R.id.shop_layout);
        shopclick=findViewById(R.id.shop_click);

        logIn_layout = findViewById(R.id.logIn_layout);
        logOut_layout = findViewById(R.id.logOut_layout);



        currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {

           logIn_layout.setVisibility(View.GONE);
           logOut_layout.setVisibility(View.VISIBLE);

            String userEmail = currentUser.getEmail();
            firestore.collection("users")
                    .document(userEmail)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String f_name = documentSnapshot.getString("firstName");
                            String l_name = documentSnapshot.getString("lastName");
                            TextView user_n = findViewById(R.id.userName);
                            user_n.setText(f_name+" "+l_name);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }else{
            logIn_layout.setVisibility(View.VISIBLE);
            logOut_layout.setVisibility(View.GONE);
        }


        logOut_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Confirm Logout")
                        .setCancelable(false)
                        .setMessage("Are you sure you want to logout")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                firebaseAuth.signOut();
                                Intent intent = new Intent(getApplicationContext(), IntroScreen.class);
                                startActivity(intent);
                                drawer.closeDrawers();
                                finish();

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        logIn_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SignInScreen.class);
                startActivity(intent);
            }
        });





        //Account Select

        accountInformation=findViewById(R.id.account_information);
        accountInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                bottomNavigationView.setSelectedItemId(R.id.navigation_profile);
//                getSupportFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.fragment_container, new ProfileFragment()).commit();
//
//                drawer.closeDrawers();

            }
        });


        //myCart Select

        myCart=findViewById(R.id.my_cart);
        myCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
//                startActivity(intent);
//
//                drawer.closeDrawers();

            }
        });



        wishlist=findViewById(R.id.wish_list);
        wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bottomNavigationView.setSelectedItemId(R.id.navigation_whishlist);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new WishlistFragment()).commit();

                drawer.closeDrawers();

            }
        });



        helpCenter=findViewById(R.id.help_center);
        helpCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //PrivacyPolicy Select

        privacyPolicy=findViewById(R.id.privacy_policy);
        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });


        shopLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ShopDataActivity.class);
                startActivity(intent);
            }
        });




    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        int_select_menu_id=item.getItemId();

        if (int_select_menu_id==R.id.navigation_home){
            fragment = new HomeFragment();
            menuClick.setVisibility(View.VISIBLE);
            backArrow.setVisibility(View.GONE);
            shopLayout.setVisibility(View.VISIBLE);
            fragmentTitle.setText("");
            logoIcon.setVisibility(View.VISIBLE);
        } else if (int_select_menu_id==R.id.navigation_whishlist) {
            if (currentUser!=null){
            fragment = new WishlistFragment();
            menuClick.setVisibility(View.GONE);
            backArrow.setVisibility(View.VISIBLE);
            shopLayout.setVisibility(View.VISIBLE);
            fragmentTitle.setText("Wishlist");
            logoIcon.setVisibility(View.GONE);
            }else{
                 Toast.makeText(this, "Please login for view wishlist", Toast.LENGTH_SHORT).show();
            }
        } else if (int_select_menu_id==R.id.navigation_cart) {
            if (currentUser!=null){
            fragment = new CartFragment();
            menuClick.setVisibility(View.GONE);
            backArrow.setVisibility(View.VISIBLE);
            fragmentTitle.setText("Cart");
            shopLayout.setVisibility(View.VISIBLE);
            logoIcon.setVisibility(View.GONE);
            }else{
                Toast.makeText(this, "Please login for view cart", Toast.LENGTH_SHORT).show();
            }
        } else if (int_select_menu_id==R.id.navigation_profile) {
            if (currentUser!=null){
                fragment = new ProfileFragment();
                menuClick.setVisibility(View.GONE);
                backArrow.setVisibility(View.VISIBLE);
                shopLayout.setVisibility(View.VISIBLE);
                fragmentTitle.setText("Profile");
                logoIcon.setVisibility(View.GONE);
            }else{
                Toast.makeText(this, "Please login for view profile", Toast.LENGTH_SHORT).show();
            }

        }


        return loadFragment(fragment);
    }




    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }


    @Override
    public void onBackPressed() {



        if (int_select_menu_id != R.id.navigation_home){ // load home menu first
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        }else{

            if (long_current_time + 2000 > System.currentTimeMillis()) {

                finishAffinity();

            }else {

                Toast.makeText(MainActivity.this, "Please Press once again to Exit", Toast.LENGTH_SHORT).show();
            }

            long_current_time = System.currentTimeMillis();

        }


    }


//    public void switchToFragment(Fragment fragment) {
//        getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.fragment_container, fragment)
//                .addToBackStack(null)
//                .commit();
//
//        updateBottomNavigationView(fragment);
//    }
//
//
//    private void updateBottomNavigationView(Fragment fragment) {
//
//        if (fragment instanceof HomeFragment) {
//            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
//        } else if (fragment instanceof WishlistFragment) {
//            bottomNavigationView.setSelectedItemId(R.id.navigation_whishlist);
//        } else if (fragment instanceof CartFragment) {
//            bottomNavigationView.setSelectedItemId(R.id.navigation_cart);
//        } else if (fragment instanceof ProfileFragment) {
//            bottomNavigationView.setSelectedItemId(R.id.navigation_profile);
//        }
//    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float shakeThreshold = 20.0f;

        if (x > shakeThreshold || y > shakeThreshold || z > shakeThreshold) {
            finishAffinity();

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }



}