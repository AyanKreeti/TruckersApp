package com.ayan.truckersapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ayan.truckersapp.directionhelpers.models.Order;
import com.ayan.truckersapp.utils.DataTransferInterface;
import com.ayan.truckersapp.utils.ItemTouchHelperAdapter;

import java.util.ArrayList;

import static com.ayan.truckersapp.MapActivity.MY_PERMISSIONS_REQUEST_LOCATION;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> implements
        ItemTouchHelperAdapter {
    DataTransferInterface dtInterface;
    Context ctx;
    int layout;
    ArrayList<Order> orders;
    private OnOrderListener onOrderListener;
    private ItemTouchHelper itemTouchHelper;

    public OrdersAdapter(Context ctx, int layout, ArrayList<Order> orders, OnOrderListener onOrderListener, DataTransferInterface dtInterface) {
        this.ctx = ctx;
        this.layout = layout;
        this.orders = orders;
        this.onOrderListener = onOrderListener;
        this.dtInterface = dtInterface;
    }

    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_orders, parent, false);
        return new OrderViewHolder(view, onOrderListener);
    }

    @Override
    public void onBindViewHolder(OrderViewHolder holder, int position) {
        holder.oname.setText(orders.get(position).getName());
        holder.oaddr.setText(orders.get(position).getAddress());
        holder.olat.setText(orders.get(position).getLat() + "");
        holder.olng.setText(orders.get(position).getLng() + "");
    }


    @Override
    public int getItemCount() {
        return orders.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Order order = orders.get(fromPosition);
        orders.remove(order);
        orders.add(toPosition, order);
        notifyItemMoved(fromPosition, toPosition);
        notifyDataSetChanged();
//        dtInterface.setValues(orders);
    }

    public void setTouchHelper(ItemTouchHelper touchHelper){
        this.itemTouchHelper = touchHelper;
    }


    public class OrderViewHolder extends RecyclerView.ViewHolder implements
            View.OnTouchListener,
            GestureDetector.OnGestureListener {
        TextView oname, oaddr, olat, olng;
        OnOrderListener onOrderListener;
        GestureDetector gestureDetector;

        public OrderViewHolder(View itemView, OnOrderListener listener) {
            super(itemView);
            oname = itemView.findViewById(R.id.name);
            oaddr = itemView.findViewById(R.id.address);
            olat = itemView.findViewById(R.id.lat);
            olng = itemView.findViewById(R.id.lng);
            onOrderListener = listener;
            gestureDetector = new GestureDetector(itemView.getContext(), this);
            itemView.setOnTouchListener(this);
        }

        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
//            Toast.makeText(itemView.getContext(), "CLicked", Toast.LENGTH_SHORT).show();

            onOrderListener.onOrderClick(getAdapterPosition());
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return true;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {
            itemTouchHelper.startDrag(this);
        }

        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            gestureDetector.onTouchEvent(motionEvent);
            return true;
        }
    }

    public interface OnOrderListener{
        void onOrderClick(int position);
    }
}
