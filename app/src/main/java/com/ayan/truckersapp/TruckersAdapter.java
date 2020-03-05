package com.ayan.truckersapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ayan.truckersapp.directionhelpers.models.Trucker;

import java.util.ArrayList;

public class TruckersAdapter extends
        RecyclerView.Adapter<TruckersAdapter.TruckerViewHolder> {

    ArrayList<Trucker> truckers = new ArrayList<>();
//    private OnTruckerListener onTruckerListener;



    public TruckersAdapter(ArrayList<Trucker> truckers) {
//        this.onTruckerListener = onTruckerListener;
        this.truckers = truckers;
    }

    @NonNull
    @Override
    public TruckerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_truckers, parent, false);
        return new TruckersAdapter.TruckerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TruckerViewHolder holder, final int position) {
        try{
            holder.tname.setText(truckers.get(position).getName());
            holder.tid.setText(truckers.get(position).getId()+"");

        }catch (Exception e){
            Log.e("Failed binding", "onBindViewHolder: Null Pointer: " + e.getMessage() );
        }

    }

    @Override
    public int getItemCount() {
        return truckers.size();
    }

    public class TruckerViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener{
        TextView tid, tname;
//        OnTruckerListener mTruckerListener;

        public TruckerViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tid = itemView.findViewById(R.id.tid);
            tname = itemView.findViewById(R.id.tname);
        }

        @Override
        public void onClick(View view) {
            view.getContext().startActivity(new Intent(view.getContext(), Schedules.class).putExtra("id",tid.getText()));
//            Toast.makeText(view.getContext(),tid.getText()+"",Toast.LENGTH_LONG).show();
        }
    }

//    public interface OnTruckerListener{
//        void onTruckerCLick(int position);
//    }
}
