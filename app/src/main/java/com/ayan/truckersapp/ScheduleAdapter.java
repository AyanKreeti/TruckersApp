package com.ayan.truckersapp;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ayan.truckersapp.directionhelpers.models.Schedule;

import java.util.ArrayList;

public class ScheduleAdapter extends
        RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    ArrayList<Schedule> schedules = new ArrayList<>();

    public ScheduleAdapter(ArrayList<Schedule> schedules) {
        this.schedules = schedules;
    }

    @Override
    public ScheduleAdapter.ScheduleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_schedule, parent, false);
        return new ScheduleAdapter.ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ScheduleAdapter.ScheduleViewHolder holder, int position) {
        try{
            holder.sid.setText(schedules.get(position).getId()+"");
//            holder.sname.setText(schedules.get(position).getSchedule_name());
//            holder.tid.setText(schedules.get(position).getTrucker_id()+"");
            holder.date.setText(schedules.get(position).getDelivery_date());
            holder.status.setText(schedules.get(position).getStatus());

        }catch (Exception e){
            Log.e("Failed binding", "onBindViewHolder: Null Pointer: " + e.getMessage() );
        }
    }

    @Override
    public int getItemCount() {
        return schedules.size();
    }

    public class ScheduleViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener{

        TextView sid, sname, tid, date, status;
        public ScheduleViewHolder(View itemView) {
            super(itemView);
            sid = itemView.findViewById(R.id.sid);
//            sname = itemView.findViewById(R.id.sname);
//            tid = itemView.findViewById(R.id.tid);
            date = itemView.findViewById(R.id.date);
            status = itemView.findViewById(R.id.status);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Toast.makeText(view.getContext(),sid.getText()+"",Toast.LENGTH_LONG).show();
                    view.getContext().startActivity(new Intent(view.getContext(), MapActivity.class).putExtra("id",sid.getText()));
//                    Log.d("CLIIICCKKED","YESSS");
                }
            });
        }

        @Override
        public void onClick(View view) {
//            view.getContext().startActivity(new Intent(view.getContext(), MapActivity.class).putExtra("id",tid.getText()));
            Toast.makeText(view.getContext(),"HII"+"",Toast.LENGTH_LONG).show();

        }
    }
}
