package com.jiat.sarongstylesadmin.Activity;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.jiat.sarongstylesadmin.Fragment.CategoryManageFragment;
import com.jiat.sarongstylesadmin.Fragment.HomeFragment;
import com.jiat.sarongstylesadmin.Fragment.ItemAddFragment;
import com.jiat.sarongstylesadmin.Fragment.ItemManageFragment;
import com.jiat.sarongstylesadmin.Fragment.OrderViewFragment;
import com.jiat.sarongstylesadmin.R;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigationView;

    private ImageView menuClick,menuClose,backArrow,logoIcon,cartclick;
    private TextView fragmentTitle;
    private RelativeLayout cartLayout;
    private long long_current_time;
    private int int_select_menu_id =0;
    private LinearLayout accountInformation,itemHistory,add_User,helpCenter,orderHistory,logout_btn;
    private Fragment fragment;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private String userType;

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


        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            firestore.collection("admin")
                    .document(userEmail)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                          userType = documentSnapshot.getString("type");
                            String f_name = documentSnapshot.getString("firstName");
                            String l_name = documentSnapshot.getString("lastName");
                            TextView user_n = findViewById(R.id.user_name_n);
                            TextView user_t = findViewById(R.id.user_type_n);
                            user_n.setText(f_name+" "+l_name);
                            user_t.setText(userType);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
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



        //Account Select

        accountInformation=findViewById(R.id.account_information);
        accountInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), AccountActivity.class);
                startActivity(intent);

                drawer.closeDrawers();

            }
        });


        //itemHistory Select

        itemHistory=findViewById(R.id.item_history);
        itemHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), ItemHistoryActivity.class);
                startActivity(intent);

                drawer.closeDrawers();

            }
        });



        add_User=findViewById(R.id.add_User);
        add_User.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userType.equals("Manager")){
                    Intent intent = new Intent(getApplicationContext(), AddUsersActivity.class);
                    startActivity(intent);

                    drawer.closeDrawers();
                }else{
                    Toast.makeText(MainActivity.this, "You can't access for add users", Toast.LENGTH_SHORT).show();
                }



            }
        });

        //Helpcenter Select

        helpCenter=findViewById(R.id.help_center);
        helpCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });



        orderHistory=findViewById(R.id.order_history);
        orderHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), OrderHistoryActivity.class);
                startActivity(intent);

                drawer.closeDrawers();

            }
        });


        logout_btn=findViewById(R.id.logout_btn);
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Confirm Logout")
                        .setCancelable(false)
                        .setMessage("Are you sure you want to logout")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                firebaseAuth.signOut();
                                Intent intent = new Intent(getApplicationContext(), SignInScreen.class);
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

    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        int_select_menu_id=item.getItemId();

        if (int_select_menu_id==R.id.navigation_home){
            fragment = new HomeFragment();
            menuClick.setVisibility(View.VISIBLE);
            backArrow.setVisibility(View.GONE);
            fragmentTitle.setText("");
            logoIcon.setVisibility(View.VISIBLE);
        } else if (int_select_menu_id==R.id.navigation_itemManage) {
            fragment = new ItemManageFragment();
            menuClick.setVisibility(View.GONE);
            backArrow.setVisibility(View.VISIBLE);
            fragmentTitle.setText("Manage Items");
            logoIcon.setVisibility(View.GONE);
        } else if (int_select_menu_id==R.id.navigation_itemAdd) {
            fragment = new ItemAddFragment();
            menuClick.setVisibility(View.GONE);
            backArrow.setVisibility(View.VISIBLE);
            fragmentTitle.setText("Add Items");
            logoIcon.setVisibility(View.GONE);
        } else if (int_select_menu_id==R.id.navigation_category) {
            fragment = new CategoryManageFragment();
            menuClick.setVisibility(View.GONE);
            backArrow.setVisibility(View.VISIBLE);
            fragmentTitle.setText("Categories");
            logoIcon.setVisibility(View.GONE);
        } else if (int_select_menu_id==R.id.navigation_orderManage) {
            fragment = new OrderViewFragment();
            menuClick.setVisibility(View.GONE);
            backArrow.setVisibility(View.VISIBLE);
            fragmentTitle.setText("Orders");
            logoIcon.setVisibility(View.GONE);
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


}