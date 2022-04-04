package com.example.karveliasae;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    EditText fullnameTxt;
    EditText emailTxt;
    EditText passwordTxt;
    EditText phoneTxt;
    EditText latitudeTxt;
    EditText longitudeTxt;
    Button registerBtn;
    String userID;
    private ProgressBar progressBar;


    String email,password,name,phone;
    double latitude,longitude;

    private FirebaseAuth myAuth;
    private FirebaseDatabase myFb;
    private DatabaseReference myRef;
    FirebaseAuth.AuthStateListener mAuthListener;
    Context context;
    private static final String TAG = "SignUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        fullnameTxt=findViewById(R.id.fullnameTxt);
        emailTxt=findViewById(R.id.emailTxt);
        passwordTxt=findViewById(R.id.passwordTxt);
        phoneTxt=findViewById(R.id.phoneTxt);
        latitudeTxt=findViewById(R.id.latitudeTxt);
        longitudeTxt=findViewById(R.id.longitudeTxt);

        progressBar=findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.GONE);

        myAuth=FirebaseAuth.getInstance();
        myFb=FirebaseDatabase.getInstance();
        myRef= myFb.getReference();


        registerBtn= findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerCustomer();
            }
        });


        setupFirebaseAuthentication();

    }

    @Override
    protected void onStart() {
        super.onStart();

        if(myAuth.getCurrentUser()!=null){

        }

    }







    private void registerCustomer() {
      final String email = emailTxt.getText().toString().trim();
        final String password = passwordTxt.getText().toString().trim();
        final String name = fullnameTxt.getText().toString().trim();
        final String phone = phoneTxt.getText().toString().trim();
        final double  latitude = Double.parseDouble(latitudeTxt.getText().toString().trim());
        final double  longitude = Double.parseDouble(longitudeTxt.getText().toString().trim());



        if (name.isEmpty()) {
            fullnameTxt.setError("full name required");
            fullnameTxt.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            emailTxt.setError("email required");
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            emailTxt.setError("enter a valid email");
            emailTxt.requestFocus();
            return;
        }

        if (password.length() < 6) {
            passwordTxt.setError("password should be atleast 6 characters long");
            passwordTxt.requestFocus();
            return;
        }

        if (phone.length() != 10) {
            phoneTxt.setError("enter a valid phone number");
            phoneTxt.requestFocus();
            return;
        }
        if (phone.isEmpty()) {
            phoneTxt.setError("enter a valid phone number");
            phoneTxt.requestFocus();
            return;
        }

        if (String.valueOf(latitude).isEmpty()) {
            latitudeTxt.setError("enter a valid latitude");
            latitudeTxt.requestFocus();
            return;
        }

        if (String.valueOf(longitude).isEmpty()) {
            longitudeTxt.setError("enter a valid latitude");
            longitudeTxt.requestFocus();
            return;
        }


        progressBar.setVisibility(View.VISIBLE);
        myAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {

                            Customers customer = new Customers(

                                    name,
                                    email,
                                    password,
                                    phone,
                                    latitude,
                                    longitude

                            );




                            FirebaseDatabase.getInstance().getReference("Customers")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .push().setValue(customer).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        Toast.makeText(com.example.karveliasae.SignUpActivity.this, "Success registration", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(com.example.karveliasae.SignUpActivity.this,"Registration failed",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                            //we will store the additional fields in firebase database
                        } else {
                            Toast.makeText(com.example.karveliasae.SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }




    public void sendCustomerData(String name, String email,String password, String phone, double latitude, double longitude)
    {
        Customers customers = new Customers(name, email, password, phone, latitude, longitude);
        myRef.push().child("Customers").setValue(customers);

    }


    private void setupFirebaseAuthentication() {

        myAuth = FirebaseAuth.getInstance();
        Log.d(TAG, "Read to send data");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = myAuth.getCurrentUser();

                if (user!=null) {


                    String userID = myAuth.getCurrentUser().getUid();

                    Log.d(TAG, "userID:"+userID);
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            sendCustomerData(name, email, password,  phone, latitude, longitude);

                            Toast.makeText(context,"Registration Success",Toast.LENGTH_LONG).show();
                            myAuth.signOut();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    finish();
                }
            }
        };
    }




    @Override
    public void onClick(View view) {


    }
}
