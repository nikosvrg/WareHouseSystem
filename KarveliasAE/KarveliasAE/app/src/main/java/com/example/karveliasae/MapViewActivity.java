package com.example.karveliasae;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapViewActivity extends Activity implements OnMapReadyCallback {
    String Latitude = getIntent().getStringExtra("Latitude");
    String Longitude = getIntent().getStringExtra("Longitude");
    double lat=Double.parseDouble(Latitude);
    double lng=Double.parseDouble(Longitude);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tr_map);
        Bundle bundle = getIntent().getExtras();
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);


    }

    public void onMapReady(GoogleMap map) {
        map.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lng))
                .title("Marker"));
    }
}
