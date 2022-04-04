package com.example.karveliasae;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;

public class Company extends AppCompatActivity {
    Menu optionsMenu;
    public String name = "", email = " ", latitude = "", longitude = "";
    public long phone;
    private ImageView ImageView;
    SharedPreferences preferences;
    SQLiteDatabase db;
    String uid = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        LoadLanguage();


        uid = preferences.getString("str1", "0");
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                name = snapshot.child("Ceo").child(uid).child("name").getValue().toString();
                email = snapshot.child("Ceo").child(uid).child("email").getValue().toString();
                phone = Long.parseLong(snapshot.child("Ceo").child(uid).child("phone").getValue().toString());
                TextView txv = findViewById(R.id.textView2);
                txv.setText("Welcome " + name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.company_opt, menu);
        optionsMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.EnCompany) {

            setLanguage("en");
            recreate();
        }

        if (id == R.id.GrCompany) {

            setLanguage("el");
            recreate();
        }

        if (id == R.id.warehouse) {
            LoadLanguage();
        }
        if (id == R.id.customer_list) {
            LoadLanguage();
            Intent intent = new Intent(this, CustomerListAdapter.class);
            startActivity(intent);

        }
        //provolh truck guy ston xarth
        if (id == R.id.track_truck) {
            LoadLanguage();
            Intent intent = new Intent(this, TrackTruckGuy.class);
            startActivity(intent);

        }
        if (id == R.id.logout) {
            LoadLanguage();
            FirebaseAuth.getInstance().signOut();
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
    public void setLanguage(String lang) {

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("Settings",MODE_PRIVATE).edit();
        editor.putString("My_Lang",lang);
        editor.apply();

    }

    //Load language saved in shared preferences

    public void LoadLanguage(){
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang","");
        setLanguage(language);
    }


    public void onBackPressed() {

    }
}








