package com.example.karveliasae;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

public class Make_order extends AppCompatActivity {
    DatabaseReference reference;
    RecyclerView recyclerView;
    SharedPreferences preferences;
    ArrayList<Cigarettes> list;
    OrderAdapter adapter;
    String uid="";
    Random r = new Random();
    int i1;

    public boolean gived;
    public double cost;

    Button bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_order);
        recyclerView=findViewById(R.id.myRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        i1 = r.nextInt(1000-200)+200;
        list=new ArrayList<Cigarettes>();
        bt=findViewById(R.id.send);
        bt.setVisibility(View.GONE);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        uid = preferences.getString("str1", "0");
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                gived = Boolean.parseBoolean(snapshot.child("Orders").child(uid).child("gived").getValue().toString());
                cost = Double.parseDouble(snapshot.child("Orders").child(uid).child("price").getValue().toString());
                if(gived){
                    bt.setVisibility(View.VISIBLE);
                    bt.setText("Send order, cost:"+i1);
                    reference= FirebaseDatabase.getInstance().getReference().child("Cigarettes");
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                                Cigarettes c=dataSnapshot1.getValue(Cigarettes.class);
                                list.add(c);
                            }
                            adapter=new OrderAdapter(Make_order.this,list);
                            recyclerView.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }else{
                    Toast.makeText(Make_order.this,"Has placed an order",Toast.LENGTH_LONG).show();
                    bt.setVisibility(View.GONE);
                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void sendOrder(View view){

        DatabaseReference rootRef1 = FirebaseDatabase.getInstance().getReference();
        rootRef1.child("Orders").child(uid).child("gived").setValue("FALSE");
        rootRef1.child("Orders").child(uid).child("price").setValue(i1+"");
        Toast.makeText(Make_order.this,"Your order has been shipped",Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, Make_order.class);
        finish();
        startActivity(intent);



    }

}
