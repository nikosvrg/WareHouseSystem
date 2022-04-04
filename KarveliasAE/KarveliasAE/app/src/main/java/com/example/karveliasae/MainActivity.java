package com.example.karveliasae;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements LocationListener {
    SharedPreferences preferences;
    private FirebaseAuth myfb;
    EditText login_email,login_pwd;
    private Button signUpBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        myfb = FirebaseAuth.getInstance();
        login_email = findViewById(R.id.logInUn);
        login_pwd = findViewById(R.id.logInPwd);
        signUpBtn= (Button) findViewById(R.id.signUpBtn);
        signUpBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                openSignUp();
            }
        });


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
        } else {

            doStuff();
        }

    }

    @SuppressLint("MissingPermission")
    private void doStuff() {
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (lm != null) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
        Toast.makeText(this, "Waiting for GPS connection!", Toast.LENGTH_SHORT).show();

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                doStuff();
            } else {

                finish();
            }

        }

    }

    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void onLocationChanged(Location location) {

    }



    public void openSignUp(){
        Intent intent =new Intent(this,SignUpActivity.class);
        startActivity(intent);
    }
    public void openTruckGuy(){
        Intent intent =new Intent(this,TruckGuy.class);
        startActivity(intent);

    }

    public void openCompany(){
        Intent intent =new Intent(this,Company.class);
        startActivity(intent);

    }
    public void openCustomer(){
        Intent intent =new Intent(this,CustomerActivity.class);
        startActivity(intent);

    }





    public void signin(View view){
        if (!login_email.getText().toString().matches("") && !login_pwd.getText().toString().matches("")) {
            myfb.signInWithEmailAndPassword(
                    login_email.getText().toString(), login_pwd.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Successfull login!", Toast.LENGTH_SHORT).show();
                                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                                rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        String uid;
                                        uid = myfb.getUid();
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putString("str1",uid);
                                        editor.commit();
                                        if (snapshot.child("Ceo").getValue().toString().contains(uid)) {
                                           openCompany();
                                        }else if (snapshot.child("Customers").getValue().toString().contains(uid)) {
                                            openCustomer();
                                        }else {
                                            openTruckGuy();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                } else {
                                    Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                    });
        } else {
            Toast.makeText(MainActivity.this, "Some of the fields are empty. Please enter your details.", Toast.LENGTH_SHORT).show();
        }
    }

}


