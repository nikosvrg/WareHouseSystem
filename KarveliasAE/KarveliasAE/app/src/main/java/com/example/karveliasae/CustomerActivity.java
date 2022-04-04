package com.example.karveliasae;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

import java.util.Locale;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class CustomerActivity extends AppCompatActivity {

    public static TextView resultTExtView;
    public String name=" ",email=" ",phone=" ";
    SharedPreferences preferences;
    String uid="";
    String barcode="";
    TextView txv;
    Menu optionsMenu;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);
        txv=findViewById(R.id.textView);
        txv.setText(barcode);
        db = openOrCreateDatabase("sales", MODE_PRIVATE, null);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        uid = preferences.getString("str1", "0");

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    name = snapshot.child("Customers").child(uid).child("name").getValue().toString();
                    email = snapshot.child("Customers").child(uid).child("email").getValue().toString();
                    phone = snapshot.child("Customers").child(uid).child("phone").getValue().toString();
                }catch (Exception oo){

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

///////////////////////////////////////////////////////////// onOptionsItemSelected //////////////////////////////////////////////////////////////////////////////
                                                             /* Epiloges menu*/

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.EnCustomer) {

            setLanguage("en");
            recreate();
        }

        if (id == R.id.GrCustomer) {

            setLanguage("el");
            recreate();
        }


        if (id == R.id.Sale) {
            Intent intent =new Intent(this,Sale.class);
            startActivity(intent);
            LoadLanguage();
        }
        if (id == R.id.Order) {
            Intent intent =new Intent(this,Make_order.class);
            startActivity(intent);
            LoadLanguage();

        }
        if (id == R.id.Accept_and_Confirm) {
            Intent intent =new Intent(this,Accept_order.class);
            startActivity(intent);
            LoadLanguage();
        }
        if (id == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            LoadLanguage();
            finish();
            startActivity(new Intent(this,MainActivity.class));

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
