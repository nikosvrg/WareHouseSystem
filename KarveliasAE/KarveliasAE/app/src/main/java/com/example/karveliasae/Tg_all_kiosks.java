package com.example.karveliasae;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

public class Tg_all_kiosks extends AppCompatActivity implements LocationListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

                                     // LEITOURGIA EMFANISH PERIPTERWN ston TRUCKGUY
    Menu optionsMenu;
    SQLiteDatabase db;
    GoogleMap googleM;

//////////////////////////////////////////////////////////////// onCreate /////////////////////////////////////////////////////////////////////////////////
                                                /* syndesh me sql kai anoigma xarth*/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tr_map);
        db = openOrCreateDatabase("markers", MODE_PRIVATE, null);
        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.google_map);
        supportMapFragment.getMapAsync(this);

    }

///////////////////////////////////////////////////// onCreateOptionsMenu ////////////////////////////////////////////////////////////////////////////////
                                              /* Dhmiourgia menu epilogwn */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.truckboy_opt, menu);
        optionsMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

/////////////////////////////////////////////////// onOptionsItemSelected ////////////////////////////////////////////////////////////////////////////////
                                                    /*  Epiloges menu  */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.neo_drom) {
            Tg_in();
        }
        if (id == R.id.mark) {
            Tg_all();
        }
        if (id == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            setContentView(R.layout.activity_main);
        }
        return super.onOptionsItemSelected(item);
    }

/////////////////////////////////////////////////////////////// onMapReady /////////////////////////////////////////////////////////////////////////////
                                                  /* Otan fortw8ei o xarths kanei zoom
                                                   * panw apo thn a8hna kai fortwnei
                                                   *        OLA ta periptera            */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleM = googleMap;
        googleM.setOnMarkerClickListener(this);
        LatLng athens= new LatLng(37.983810, 23.727539);
        float zoomLevel = 12.0f; //This goes up to 21
        googleM.moveCamera(CameraUpdateFactory.newLatLngZoom(athens, zoomLevel));
        get_kiosk();

    }

//////////////////////////////////////////////////////////// get_kiosk ///////////////////////////////////////////////////////////////////////////////
                                /*  Pernei ola ta periptera apo ton sql pinaka  */

    public void get_kiosk(){
        Cursor cursor = db.rawQuery("SELECT * FROM KIOSKS", null);
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                double la = cursor.getDouble(3);
                double lo = cursor.getDouble(4);
                String name = cursor.getString(1);
              try {
                    googleM.addMarker(new MarkerOptions().position(new LatLng(la, lo)).title(name).icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
              } catch (Exception okkk) {

              }
            }
        }

    }

//////////////////////////////////////////////////////// onMarkerClick ///////////////////////////////////////////////////////////////////////////////
                                /* Emfanish stoixeiwn peripterou otan patas sto antistoixo marker */
    @Override
    public boolean onMarkerClick(Marker marker) {
        Cursor cursor = db.rawQuery("SELECT * FROM KIOSKS", null);
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                double la = cursor.getDouble(3);
                double lo = cursor.getDouble(4);
                if (marker.getPosition().latitude==la && marker.getPosition().longitude==lo) {
                    String name=cursor.getString(1);
                    String phone=cursor.getString(2);
                    String email=cursor.getString(5);
                    TextView map_name=findViewById(R.id.mapname);
                    map_name.setText("Owner: "+name);
                    TextView map_phone=findViewById(R.id.mapphone);
                    map_phone.setText("Call: "+phone);
                    TextView map_email=findViewById(R.id.mapemail);
                    map_email.setText("Send: "+email);
                    break;
                }
            }
        }
        return false;
    }
//////////////////////////////////////////////////////////// Tg_all ////////////////////////////////////////////////////////////////////////////////////////////
                                             /*  kwdikas an epilekseis sto menou emfanish peripterwn
                                              *     (sthn ousia anoigeis pali authn thn klash         */
    public void Tg_all(){
        Intent intent =new Intent(this,Tg_all_kiosks.class);
        startActivity(intent);

    }

//////////////////////////////////////////////////////////// Tg_in ////////////////////////////////////////////////////////////////////////////////////////////
                                  /*  kwdikas an epilekseis sto menou emfanish dromologiou */
    public void Tg_in(){
        Intent intent =new Intent(this,Tg_init.class);
        startActivity(intent);

    }

                                                    //////////////////////////

    @Override
    public void onLocationChanged(Location location) {

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

}