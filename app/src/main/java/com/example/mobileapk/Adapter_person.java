package com.example.mobileapk;

import static com.example.mobileapk.LoggedActivity.convert;
import static com.example.mobileapk.LoggedActivity.drawableToBitmap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

class Adapter_person extends RecyclerView.Adapter<Adapter_person.ViewHolder> {

    ArrayList<UserObject> osoby;
    Context context;
    Intent i_rozmowa;
    SessionManager sessionManager;

    public Adapter_person(ArrayList<UserObject> osoby, SessionManager sessionManager){
        this.osoby=osoby;
        this.sessionManager=sessionManager;
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

    public boolean isOnline() {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnectedOrConnecting()){
            return true;
        }
        else{
            return false;
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        context=holder.cardView.getContext();

        ImageView iv= holder.cardView.findViewById(R.id.avatar);

        if(isOnline()) {
            Glide.with(context).load(osoby.get(position).getAvatar())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false; // important to return false so the error placeholder can be placed
                        }

                        @SuppressLint("CheckResult")
                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            String imageBase64 = convert(drawableToBitmap(resource));

                            sessionManager.cacheAvatars(imageBase64, osoby.get(position).getUserName().getName());
                            return false;
                        }
                    })
                    .into(iv);
        }
        else if(sessionManager.preferences.getString(osoby.get(position).getUserName().getName(),"") != ""){
            iv.setImageBitmap(convert(sessionManager.preferences.getString(osoby.get(position).getUserName().getName(),"")));
        }



        if(position==osoby.size()-1){
            System.out.println("ostatni");
        }


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