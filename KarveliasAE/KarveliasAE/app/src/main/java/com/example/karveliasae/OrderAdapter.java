package com.example.karveliasae;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Random;

import static android.preference.PreferenceManager.*;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {
    Context context;
    ArrayList<Cigarettes> Cigarettes;
    SharedPreferences preferences;

    String uid="";
    public OrderAdapter(Context c,ArrayList<Cigarettes> p){

        context = c;
        Cigarettes = p;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.cigaretteview,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.name.setText(Cigarettes.get(position).getName());
        holder.ws_price.setText(Cigarettes.get(position).getWs_price());
        holder.re_price.setText(Cigarettes.get(position).getRe_price());
        Random r = new Random();
        int i1 = r.nextInt(20-5)+0;
        Cigarettes.get(position).setQu(i1);
        holder.qu.setText(Cigarettes.get(position).getQu()+"");


    }

    @Override
    public int getItemCount() {
        return Cigarettes.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView name,ws_price,re_price,qu;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);
            ws_price=itemView.findViewById(R.id.ws_price);
            re_price=itemView.findViewById(R.id.re_price);
            qu =itemView.findViewById(R.id.qua);
        }
    }
}