package com.example.karveliasae;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class CustomerListAdapter extends AppCompatActivity implements  LocationListener, GoogleMap.OnMarkerClickListener,OnMapReadyCallback {

    private List<Customers> myCustomerList;
    private RecyclerView KioskListView;
    private RecyclerView.Adapter KioskListAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DatabaseReference mDatabase;
    SQLiteDatabase db;
    Tg_all_kiosks allKiosks;
    Customers customers;
    SharedPreferences preferences;
    GoogleMap map;

    Context context;


    MarkerOptions place1, place2;
    Marker marker;

    double latitude;
    double longitude;
    double cLT;
    double cLG;
    private final Handler handler = new Handler();

    String uid = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kiosk_list);


        db = openOrCreateDatabase("markers", MODE_PRIVATE, null);
        //customers column on firebase
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Customers");
        mDatabase.keepSynced(true);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        KioskListView = findViewById(R.id.KioskListView);

        mLayoutManager = new LinearLayoutManager(this);
        KioskListView.setLayoutManager(mLayoutManager);


        db = openOrCreateDatabase("markers", MODE_PRIVATE, null);
        try {
            db.execSQL("Drop table KIOSKS");
            db.execSQL("Drop table ITIN");
        } catch (Exception err) {

        }
        db.execSQL("CREATE TABLE IF NOT EXISTS KIOSKS(uid TEXT,name TEXT,phone TEXT,longi DOUBLE,lat DOUBLE,email TEXT,PRIMARY KEY (longi,lat));");
        db.execSQL("CREATE TABLE IF NOT EXISTS ITIN(name TEXT,longi DOUBLE,lat DOUBLE,price DOUBLE,gived BOOLEAN,PRIMARY KEY (longi,lat));");
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Customers, CustomerViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Customers, CustomerViewHolder>
                (Customers.class, R.layout.kiosk_item, CustomerViewHolder.class, mDatabase) {

            @Override
            protected void populateViewHolder(CustomerViewHolder customerViewHolder, final Customers model, int position) {
                customerViewHolder.ImageView.setImageResource(R.drawable.kioskimg);
               /* customerViewHolder.ImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(context,MapViewActivity.class);
                        intent.putExtra("Latitude", customers.getLatitude());
                        intent.putExtra("Longitude", customers.getLongitude());
                        context.startActivity(intent);


                    }

                });
*/
                customerViewHolder.setName(model.getName());
                customerViewHolder.setEmail(model.getEmail());
                // viewHolder.setPhone(model.getPhone());
                customerViewHolder.setLatitude(model.getLatitude());
                customerViewHolder.setLongitude(model.getLongitude());


            }
        };

        KioskListView.setAdapter(firebaseRecyclerAdapter);


    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }



    public static class CustomerViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ImageView;
        View mView;

        public CustomerViewHolder(View itemView) {
            super(itemView);
            ImageView = itemView.findViewById(R.id.imageview);


            mView = itemView;


        }

        public void setName(String name) {
            TextView customer_name = (TextView) mView.findViewById(R.id.customername);
            customer_name.setText("Name: " + String.valueOf(name));
        }

        public void setEmail(String email) {
            TextView customer_email = (TextView) mView.findViewById(R.id.customeremail);
            customer_email.setText("Email: " + String.valueOf(email));
        }

        public void setPhone(String phone) {
            TextView customer_phone = (TextView) mView.findViewById(R.id.customerphone);
            customer_phone.setText("Phone: " + String.valueOf(phone));

        }

        public void setLatitude(double latitude) {
            TextView customer_latitude = (TextView) mView.findViewById(R.id.customerlatitude);
            customer_latitude.setText("Latitude: " + String.valueOf(latitude));
        }

        public void setLongitude(double longitude) {
            TextView customer_longitude = (TextView) mView.findViewById(R.id.customerlongitude);
            customer_longitude.setText("Longitude: " + String.valueOf(longitude));
        }

    }

    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMarkerClickListener(this);
        LatLng athens = new LatLng(37.983810, 23.727539);
        float zoomLevel = 12.0f; //This goes up to 21
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(athens, zoomLevel));

        place1 = new MarkerOptions()
                .position(new LatLng(customers.getLatitude(), customers.getLongitude())).icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        marker = map.addMarker(place1);


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

