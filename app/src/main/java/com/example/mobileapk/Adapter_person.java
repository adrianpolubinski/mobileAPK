package com.example.mobileapk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

class Adapter_person extends RecyclerView.Adapter<Adapter_person.ViewHolder> {

    ArrayList<UserObject> osoby;
    Context context;
    Intent i_rozmowa;

    public Adapter_person(ArrayList<UserObject> osoby){
        this.osoby=osoby;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final CardView cardView;
        public ViewHolder(CardView v){
            super(v);
            cardView=v;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv= (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_person,parent,false);
        return new ViewHolder(cv);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        context=holder.cardView.getContext();


        ImageView iv= holder.cardView.findViewById(R.id.avatar);
        Glide.with(context).load(osoby.get(position).getAvatar())
                .diskCacheStrategy(DiskCacheStrategy.NONE )
                .skipMemoryCache(true)
                .into(iv);

        TextView tv= holder.cardView.findViewById(R.id.textViewPerson);
        tv.setText(osoby.get(position).getUserName().getName() + " " + osoby.get(position).getUserName().getSurname());

        i_rozmowa=new Intent(context, ChatActivity.class);
        Button btn = holder.cardView.findViewById(R.id.buttonOpenChat);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i_rozmowa.putExtra("name",String.valueOf(osoby.get(position).getUserName().getName() + " " + osoby.get(position).getUserName().getSurname()));
                i_rozmowa.putExtra("id", String.valueOf(osoby.get(position).getId()));
                context.startActivity(i_rozmowa);
            }
        });
    }

    @Override
    public int getItemCount(){return osoby.size();}
}