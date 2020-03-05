package com.ayan.truckersapp.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ayan.truckersapp.R;
import com.ayan.truckersapp.directionhelpers.models.Order;

import java.util.ArrayList;

public class CompletedOrdersAdapter extends RecyclerView.Adapter<CompletedOrdersAdapter.OrderViewHolder>{
    Context ctx;
    int layout;
    ArrayList<Order> orders;

    public CompletedOrdersAdapter(Context ctx, int layout, ArrayList<Order> orders) {
        this.ctx = ctx;
        this.layout = layout;
        this.orders = orders;
    }

    @Override
    public CompletedOrdersAdapter.OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_orders, parent, false);
        return new CompletedOrdersAdapter.OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CompletedOrdersAdapter.OrderViewHolder holder, int position) {
        holder.oname.setText(orders.get(position).getName());
        holder.oaddr.setText(orders.get(position).getAddress());
        holder.olat.setText(orders.get(position).getLat() + "");
        holder.olng.setText(orders.get(position).getLng() + "");
        holder.status.setVisibility(View.VISIBLE);
        holder.status.setImageResource(R.drawable.tick);
        holder.itemView.setBackgroundColor(
                ContextCompat.getColor(holder.itemView.getContext(), R.color.lightYellow)
        );
    }


    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView oname, oaddr, olat, olng;
        ImageView status;
        public OrderViewHolder(View itemView) {
            super(itemView);
            oname = itemView.findViewById(R.id.name);
            oaddr = itemView.findViewById(R.id.address);
            olat = itemView.findViewById(R.id.lat);
            olng = itemView.findViewById(R.id.lng);
            status = itemView.findViewById(R.id.status_image);
        }
    }
}
