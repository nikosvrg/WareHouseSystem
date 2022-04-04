package com.example.karveliasae;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Accept_order extends AppCompatActivity {

    public boolean gived;
    public double cost;
    TextView tx;
    Button bt;

    SharedPreferences preferences;
    String uid="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_order);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        uid = preferences.getString("str1", "0");
        tx=findViewById(R.id.textView7);
        bt=findViewById(R.id.button2);
        tx.setText("No waiting order");
        bt.setVisibility(View.GONE);
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                gived = Boolean.parseBoolean(snapshot.child("Orders").child(uid).child("gived").getValue().toString());
                cost = Double.parseDouble(snapshot.child("Orders").child(uid).child("price").getValue().toString());
                if(gived){
                    tx.setText("No waiting order");
                    bt.setVisibility(View.GONE);

                }else{
                    tx.setText("Pending order, cost:"+cost);
                    bt.setVisibility(View.VISIBLE);
                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public void accept(View view){
        tx.setText("No waiting order");
        bt.setVisibility(View.GONE);
        DatabaseReference rootRef1 = FirebaseDatabase.getInstance().getReference();
        rootRef1.child("Orders").child(uid).child("gived").setValue("TRUE");
        rootRef1.child("Orders").child(uid).child("price").setValue("0");
    }
}
