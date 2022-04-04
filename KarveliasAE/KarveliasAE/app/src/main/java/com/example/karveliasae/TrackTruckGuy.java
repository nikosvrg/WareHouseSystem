package com.example.karveliasae;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TrackTruckGuy extends AppCompatActivity  implements LocationListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener, TaskLoadedCallback {

    SQLiteDatabase db;
    GoogleMap googleM;
    SharedPreferences preferences;
    int M=0;
    MarkerOptions a;
    MarkerOptions place1,place2;
    Marker m ;
    Polyline[] currentPolyline=new Polyline[100];
    String uid="";

    double TgLT;
    double TgLG;
    double clg;
    double clt;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tr_map);
        db = openOrCreateDatabase("markers", MODE_PRIVATE, null);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.google_map);
        supportMapFragment.getMapAsync(this);

        uid = preferences.getString("str1", "0");


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
        } else {

            doStuff();
        }

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

    }


    @SuppressLint("MissingPermission")
    private void doStuff(){
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (lm != null){
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
        }
        Toast.makeText(this,"Waiting for GPS connection!", Toast.LENGTH_SHORT).show();



    doTheAutoRefresh();
    }


    private void doTheAutoRefresh() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    for(int i=0;i<=M;i++) {
                        currentPolyline[i].remove();              //diagrafontai h prohgoumenes grammes ston xarth (pio katw ksana mapinoun kainourgies)
                    }
                }catch(Exception cp){

                }
                set_itinerary();
                get_itinerary();
                doTheAutoRefresh();
            }
        }, 10000);
    }

    public void set_itinerary(){

        DatabaseReference rootRef1 = FirebaseDatabase.getInstance().getReference();
        DatabaseReference custom1 = rootRef1.child("Orders");
        ValueEventListener valueEventListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String cuid = ds.getKey();
                    boolean gived= Boolean.parseBoolean(snapshot.child(cuid).child("gived").getValue().toString());
                    double price = Double.parseDouble(snapshot.child(cuid).child("price").getValue().toString());
                    Cursor cursor = db.rawQuery("SELECT * FROM KIOSKS WHERE '" + cuid + "'=uid", null);
                    if (cursor.getCount() != 0) {
                        while (cursor.moveToNext()) {
                            double longi = cursor.getDouble(3);
                            double lat = cursor.getDouble(4);
                            String na1 = cursor.getString(1);
                            try {
                                db.execSQL("INSERT INTO ITIN values" + "('" + na1 + "','" + longi + "','" + lat + "','" + price + "','" + gived + "');");
                            }catch(Exception putnull){
                                db.execSQL("UPDATE ITIN SET gived= '"+gived+"'WHERE longi='"+ longi +"' AND lat='"+ lat +"'");
                            }

                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        custom1.addListenerForSingleValueEvent(valueEventListener1);
    }

    /////////////////////////////////////////////////////////////// onMapReady /////////////////////////////////////////////////////////////////////////////
    /* Otan fortw8ei o xarths kanei zoom
     * panw apo thn a8hna kai fortwnei
     * thn topo8esia tou truckguy kai
     * ta periptera p exoun kanei paraggelia */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleM = googleMap;
        googleM.setOnMarkerClickListener(this);
        LatLng athens= new LatLng(37.983810, 23.727539);
        float zoomLevel = 12.0f; //This goes up to 21
        googleM.moveCamera(CameraUpdateFactory.newLatLngZoom(athens, zoomLevel));

        a = new MarkerOptions()
                .position(new LatLng(TgLT,TgLG)).icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        m = googleM.addMarker(a);
        set_itinerary();
        get_itinerary();

    }

/////////////////////////////////////////////////////////////////// onMarkerClick ///////////////////////////////////////////////////////////////////////////////
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

/////////////////////////////////////////////////////////////////// get_itinerary ///////////////////////////////////////////////////////////////////////////////
    /* vriskei to dromologio.
     *  1 marker h etairia, 1 marker o truckguy, marker gia ola ta periptera me paraggelia
     *   xwrizontai me diaforetiko xrwma. Ta periptera p exei dw8ei h paraggelia emfanizontai
     *   me prasino kai se auta p prepei na paei me kokkino.*/

    /* parallhla ftiaxnontai kai oi grammes p enonoun ton truckguy me ta periptera
     * kai to teleutaio periptero me thn etairia*/


    public void get_itinerary(){

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference custom = rootRef.child("Ceo");
        ValueEventListener valueEventListener = new ValueEventListener() {       // pernei tis sintetagmenes tis etairias
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String cuid = ds.getKey();
                    clg= Double.parseDouble(snapshot.child(cuid).child("longitude").getValue().toString());
                    clt = Double.parseDouble(snapshot.child(cuid).child("latitude").getValue().toString());
                    googleM.addMarker(new MarkerOptions().position(new LatLng(clt, clg)).title("CEO").icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        custom.addListenerForSingleValueEvent(valueEventListener);
        DatabaseReference rootRef1 = FirebaseDatabase.getInstance().getReference();
        DatabaseReference custom1 = rootRef1.child("Truckguy");                                    //  pernei tis sintetagmenes tou truckguy
        ValueEventListener valueEventListener1 = new ValueEventListener() {
            @Override
            public void onDataChange (DataSnapshot snapshot){
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String cuid = ds.getKey();
                    TgLG = Double.parseDouble(snapshot.child(uid).child("longitude").getValue().toString());
                    TgLT= Double.parseDouble(snapshot.child(uid).child("latitude").getValue().toString());
                    a = new MarkerOptions()
                            .position(new LatLng(TgLT,TgLG)).icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                    place1=a;                                                                        //(POLYLINE) H grammh ksekinaei panta apo ton truckguy

                }
            }

            @Override
            public void onCancelled (@NonNull DatabaseError databaseError){

            }

        };
        custom1.addListenerForSingleValueEvent(valueEventListener1);
        Cursor cursor = db.rawQuery("SELECT * FROM ITIN", null);               // orizei ta periptera p exoun paraggelia ston xarth
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                double la = cursor.getDouble(1);
                double lo = cursor.getDouble(2);
                boolean gived=Boolean.parseBoolean(cursor.getString(4));
                String name = cursor.getString(0);
                try {
                    if (!gived) {
                        googleM.addMarker(new MarkerOptions().position(new LatLng(la, lo)).title(name));
                        place2=new MarkerOptions().position(new LatLng(la, lo)).title(name);                       //(POLYLINE) ennonetai sto periptero pou vriskei
                        String url=getUrl(place1.getPosition(),place2.getPosition(),"driving");
                        new FetchURL(TrackTruckGuy.this).execute(url,"driving");                             //(POLYLINE) emfanizetai h grammh
                        place1=place2;                                                                            //(POLYLINE) twra ksekinaei apo auto to periptero
                        // (POLYLINE) epanalalipsi
                    } else {
                        googleM.addMarker(new MarkerOptions().position(new LatLng(la, lo)).title(name).icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    }
                    final String uid = preferences.getString("str1", "0");
                    m.setPosition(new LatLng(TgLT,TgLG));

                }catch(Exception nupo){

                }
            }
            place2=new MarkerOptions().position(new LatLng(clt, clg)).title("CEO");                             //(POLYLINE) h grammh teliwnei panta sthn CEO
            String url=getUrl(place1.getPosition(),place2.getPosition(),"driving");             //(POLYLINE) ennonetai to teleutaio periptero me thn CEO
            new FetchURL(TrackTruckGuy.this).execute(url,"driving");                                      //(POLYLINE) emfanish grammhs(teleutaias)
        }
    }

    /////////////////////////////////////////////////////////////////// getURL ////////////////////////////////////////////////////////////////////////////
    /* eisagwgh stoixeiwn topo8esias twn marker. ginetai anazhthsh sto diadiktyo
     *                     (xrhsh api)                                           */
    private String getUrl(LatLng origin,LatLng dest,String directionMode)
    {
        String str_origin="origin=" + origin.latitude + "," +  origin.longitude;
        String str_dest="destination=" + dest.latitude + "," +  dest.longitude;
        String mode="mode=" + directionMode;
        String parameters=str_origin + "&" + str_dest + "&" + mode;
        String output="json";
        String url="https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" +getString(R.string.map_key);
        Log.d("myTag", url);

        return url;

    }

    /////////////////////////////////////////////////////////////////////// onTaskDone ////////////////////////////////////////////////////////////////////////////
    @Override
    public void onTaskDone(Object... values) {
        //if(currentPolyline != null) {
        //Toast.makeText(Tg_init.this, "2", Toast.LENGTH_SHORT).show();
        //  currentPolyline.remove();
        currentPolyline[M] = googleM.addPolyline((PolylineOptions) values[0]); //emfanish grammhs
        M++;                                                                   // +1 ston pointer gia ka8e grammh p vgainei
        //  }
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
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
    @Override
    public void onBackPressed() {
        setContentView(R.layout.activity_company);
        super.onBackPressed();
    }
}
