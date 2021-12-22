package com.example.mobileapk;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.bson.types.ObjectId;

import java.util.ArrayList;

class Adapter_message extends RecyclerView.Adapter<Adapter_message.ViewHolder> {

    ArrayList<String> messages;
    ArrayList<String> id_from;
    String id_user;
    Context context;
    int maxWidth;

    public Adapter_message(ArrayList<String> messages,ArrayList<String> id_from,String id_user, int maxWidth){
        this.messages=messages;
        this.id_from=id_from;
        this.id_user=id_user;
        this.maxWidth=maxWidth;
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
        CardView cv= (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_message,parent,false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        context=holder.cardView.getContext();
        TextView tv= holder.cardView.findViewById(R.id.textViewMessage);
        LinearLayout ll= holder.cardView.findViewById(R.id.linearLayoutMessage);

        tv.setText(messages.get(position));

        if( id_user.equals(id_from.get(position))){
            tv.setBackground(context.getResources().getDrawable(R.drawable.send_message));
            tv.setMaxWidth(maxWidth);
            ll.setGravity(Gravity.RIGHT);
        }
        else {
            tv.setBackground(context.getResources().getDrawable(R.drawable.received_message));
            tv.setMaxWidth(maxWidth);
            ll.setGravity(Gravity.LEFT);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
