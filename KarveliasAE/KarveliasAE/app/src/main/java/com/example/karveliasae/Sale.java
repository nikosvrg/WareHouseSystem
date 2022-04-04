package com.example.karveliasae;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Sale extends AppCompatActivity {
    SharedPreferences preferences;
    String uid="";
    Menu optionsMenu;
    SQLiteDatabase db;
    ListView simpleList;
    List<String> allrecs;
    double SUM;
    Button sell;
    //private List<SaleHistory> mySaleHistory;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        uid = preferences.getString("str1", "0");
        db = openOrCreateDatabase("sales", MODE_PRIVATE, null);
        SUM=0;
        sell=findViewById(R.id.button);
        take_sale();
       /* SaleHistoryAdapter = new SaleHistoryAdapter(SaleHistoryAdapter);
        SaleHistoryView.setAdapter(SaleHistoryAdapter);*/
    }

    public void take_sale(){
        try {
            allrecs = new ArrayList<>();
            Toast.makeText(Sale.this, "4", Toast.LENGTH_SHORT).show();
            Cursor cursor = db.rawQuery("SELECT * FROM sale", null);
            Toast.makeText(Sale.this, "5", Toast.LENGTH_SHORT).show();
            cursor.moveToFirst();
            Toast.makeText(Sale.this, "5.5", Toast.LENGTH_SHORT).show();
            if (cursor.getCount() !=0) {
                Toast.makeText(Sale.this, "6", Toast.LENGTH_SHORT).show();
                while (cursor.moveToNext()) {
                    Toast.makeText(Sale.this, "7", Toast.LENGTH_SHORT).show();
                    String barcode = cursor.getString(0);
                    String name = cursor.getString(1);
                    double price = cursor.getDouble(2);
                    SUM+=price;
                    int pos = cursor.getInt(3);
                    allrecs.add("Code: "+barcode+" cigarettes: "+name+" price: "+price);

                }
                simpleList = (ListView) findViewById(R.id.simpleListView);
                ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(this, R.layout.activity_sale_items, R.id.textView, allrecs);
                simpleList.setAdapter(arrayAdapter1);
                Toast.makeText(Sale.this, "Mphke takesale", Toast.LENGTH_SHORT).show();
            }
            sell.setText("Checkout, total cost:"+SUM);
        }catch (Exception n){
            Toast.makeText(Sale.this, "error", Toast.LENGTH_SHORT).show();

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.customer_opt, menu);
        optionsMenu = menu;


        return super.onCreateOptionsMenu(menu);
    }

///////////////////////////////////////////////////////////// onOptionsItemSelected //////////////////////////////////////////////////////////////////////////////
    /* Epiloges menu*/

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.Sale) {
            Intent intent =new Intent(this,Sale.class);
            startActivity(intent);
        }
        if (id == R.id.Order) {
            Intent intent =new Intent(this,Make_order.class);
            startActivity(intent);

        }
        if (id == R.id.Accept_and_Confirm) {
            Intent intent =new Intent(this,Accept_order.class);
            startActivity(intent);
        }
        if (id == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            finish();
            startActivity(new Intent(this,MainActivity.class));

        }
        return super.onOptionsItemSelected(item);
    }
    public void open_qr(View view){
        finish();
        Intent intent1 =new Intent(this,Qr_scanner.class);
        startActivity(intent1);


    }
    public void Sell(View v){
        try {
            final int[] neo_stock = new int[1];
            int pos;
            Cursor cursor = db.rawQuery("SELECT * FROM sale", null);
            if (cursor.getCount() != 0) {
                while (cursor.moveToNext()) {
                    String barcode = cursor.getString(0);
                    String name = cursor.getString(1);
                    double price = cursor.getDouble(2);
                    pos = cursor.getInt(3);
                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                    final String finalBarcode = barcode;
                    final int finalPos = pos;
                    final String bar = barcode;
                    rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            try {
                                int twrino_stock = Integer.parseInt(snapshot.child("Customers_stock").child(uid).child(finalBarcode).child("stock").getValue().toString());
                                neo_stock[0] = twrino_stock - finalPos;
                                DatabaseReference rootRef1 = FirebaseDatabase.getInstance().getReference();
                                rootRef1.child("Customers_stock").child(uid).child(bar).child("stock").setValue(neo_stock[0]);
                                try{
                                    db.execSQL("Drop table sale");
                                }catch(Exception ncig){

                                };
                                allrecs = new ArrayList<>();
                                simpleList = (ListView) findViewById(R.id.simpleListView);
                                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Sale.this, R.layout.activity_sale_items, R.id.textView, allrecs);
                                simpleList.setAdapter(arrayAdapter);
                                sell.setText("Checkout");

                            } catch (Exception oo) {

                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }
        }catch(Exception sel){
            Toast.makeText(Sale.this, "No cigarette found for sale", Toast.LENGTH_SHORT).show();

        }

    }
}
