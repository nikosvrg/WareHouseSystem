package com.example.karveliasae;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.model.Marker;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;


public class TruckGuy extends AppCompatActivity implements LocationListener, GoogleMap.OnMarkerClickListener {

                                                       // LEITOURGIA TRUCKGUY xrhsth
    SharedPreferences preferences;
    public String name=" ",email=" ",phone=" ";
    Menu optionsMenu;
    SQLiteDatabase db;
    double TgLG;
    double TgLT;
    public String ep=" ";
    String uid="";

///////////////////////////////////////////////////////////////// onCreate ////////////////////////////////////////////////////////////////////////////////////////
                                     /* Me to login tou energopoihte to location gia ton xrhsth TRUCKGUY
                                     *   syndesh me thn sql vash dhmiourgontas toys pinakes KIOSKS,ITIN
                                     *     syndesh me thn firebase kai emfanish twn stoixeiwn tou
                                     *       truckguy sthn arxikh selida tou xrhsth                      */

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_truck_guy);
        LoadLanguage();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.app_name));

                                                           //permission gia to location

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
        } else {

            doStuff();
        }

                               //diagarfh twn hdh yparxontwn pinakwn dioti ka8e fora pou ginetai login ta dedomena einai kainourgia

        db = openOrCreateDatabase("markers", MODE_PRIVATE, null);
        try {
            db.execSQL("Drop table KIOSKS");
            db.execSQL("Drop table ITIN");
        }catch(Exception err){

        }

                                                      //dhmiourgia pinakwn KIOSKS,ITIN

        db.execSQL("CREATE TABLE IF NOT EXISTS KIOSKS(uid TEXT,name TEXT,phone TEXT,longi DOUBLE,lat DOUBLE,email TEXT,PRIMARY KEY (longi,lat));");
        db.execSQL("CREATE TABLE IF NOT EXISTS ITIN(name TEXT,longi DOUBLE,lat DOUBLE,price DOUBLE,gived BOOLEAN,PRIMARY KEY (longi,lat));");
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

                                   //apo8hkeysh sthn metavlhth uid to id tou truckguy pou exei sto firebase

        uid = preferences.getString("str1", "0");

                                            //retrieve data from firebase tou truckguy

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                name=snapshot.child("Truckguy").child(uid).child("name").getValue().toString();
                email=snapshot.child("Truckguy").child(uid).child("email").getValue().toString();
                phone=snapshot.child("Truckguy").child(uid).child("phone").getValue().toString();
                 TextView txv=findViewById(R.id.textView3);
                TextView txv1=findViewById(R.id.textView4);
                TextView txv2=findViewById(R.id.textView5);
                txv.setText("Welcome "+name);
                txv1.setText("Email: "+email);
                txv2.setText("Phone number: "+phone);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

                                               //orismos twn katasthmatwn

        set_kiosk();

    }


//////////////////////////////////////////////////////////////// doStuff ///////////////////////////////////////////////////////////////////////////////////////////
                                                    /* entopismos tou location*/

    @SuppressLint("MissingPermission")
    private void doStuff(){
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (lm != null){
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
        }
        Toast.makeText(this,"Waiting for GPS connection!", Toast.LENGTH_SHORT).show();


    }


/////////////////////////////////////////////////////////////////////// Tg_all /////////////////////////////////////////////////////////////////////////////////////
                                   /*    epilogh na metafer8oume sthn provolh twn shmeiwn polishs        */

    public void Tg_all(){
        Intent intent =new Intent(this,Tg_all_kiosks.class);
        startActivity(intent);

    }

//////////////////////////////////////////////////////////////////// Tg_in ///////////////////////////////////////////////////////////////////////////////////////////
                                      /*   epilogh na metafer8oume sthn leitourgia provolhs tou dromologiou  */

    public void Tg_in(){
        Intent intent =new Intent(this,Tg_init.class);
        startActivity(intent);

    }

///////////////////////////////////////////////////////////////////// Set_kiosk ////////////////////////////////////////////////////////////////////////////////////////
                                           /* Pernei ta stoixeia twn pelatwn apo thn firebase kai ta
                                            *   apo8hkevei se sql. Ston pinaka KIOSKS vriskontai
                                            *                 ola ta katasthmata. */

    public void set_kiosk() {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference custom = rootRef.child("Customers");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String cuid = ds.getKey();
                    try {
                        double longi = Double.parseDouble(snapshot.child(cuid).child("longitude").getValue().toString());
                        double lat = Double.parseDouble(snapshot.child(cuid).child("latitude").getValue().toString());
                        String na1 = snapshot.child(cuid).child("name").getValue().toString();
                        String ph1 = snapshot.child(cuid).child("phone").getValue().toString();
                        String em1 = snapshot.child(cuid).child("email").getValue().toString();
                        db.execSQL("INSERT INTO KIOSKS values" + "('" + cuid + "','" + na1 + "','" + ph1 + "','" + longi + "','" + lat + "','" + em1 + "');");
                    } catch (Exception o) {

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        custom.addListenerForSingleValueEvent(valueEventListener);
    }

/////////////////////////////////////////////////////////////// onCreateOptionsMenu ///////////////////////////////////////////////////////////////////////////////
                                                 /*   Dhmiourgia menu sto programma       */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.truckboy_opt, menu);
        optionsMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

///////////////////////////////////////////////////////////// onOptionsItemSelected //////////////////////////////////////////////////////////////////////////////
                                                               /* Epiloges menu*/

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.EnTruckGuy) {

            setLanguage("en");
            recreate();
        }

        if (id == R.id.GrTruckGuy) {

            setLanguage("el");
            recreate();
        }


        if (id == R.id.neo_drom) {
            Tg_in();
            LoadLanguage();
        }
        if (id == R.id.mark) {
            Tg_all();
            LoadLanguage();
        }
        if (id == R.id.logout) {
           FirebaseAuth.getInstance().signOut();
            LoadLanguage();
           finish();
           startActivity(new Intent(this,TruckGuy.class));

        }
        return super.onOptionsItemSelected(item);
    }

//////////////////////////////////////////////////////////// onRequestPermissionsResult ////////////////////////////////////////////////////////////////////////
                                                                /* Adeia xrhsth */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                doStuff();
            } else {

                finish();
            }

        }

    }


///////////////////////////////////////////////////////// onLocationChanged /////////////////////////////////////////////////////////////////////////////////////
                                         /* Oso o truckguy metakeinite allazei to location
                                         *  kai to stelnei sthn firebase (longitude,latitude) */

    @Override
    public void onLocationChanged(Location location) {
        TgLG=location.getLongitude();
        TgLT=location.getLatitude();
        DatabaseReference rootRef1 = FirebaseDatabase.getInstance().getReference();
        rootRef1.child("Truckguy").child(uid).child("longitude").setValue(TgLG);
        rootRef1.child("Truckguy").child(uid).child("latitude").setValue(TgLT);

    }

                                                   ////////////////////////////////
    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }



    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

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
    @Override
    public void onBackPressed() {

    }


}
