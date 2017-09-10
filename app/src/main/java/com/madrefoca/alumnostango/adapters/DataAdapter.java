package com.madrefoca.alumnostango.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.madrefoca.alumnostango.R;
import com.madrefoca.alumnostango.model.Attendee;

import java.util.ArrayList;


/**
 * Created by fernando on 09/09/17.
 */

public class DataAdapter extends RecyclerView.Adapter{

    private ArrayList<Attendee> attendeesList;

    public DataAdapter(ArrayList attendeesList) {
        this.attendeesList = attendeesList;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder)holder;
        viewHolder.attendeeName.setText(attendeesList.get(position).getName() +" "+ attendeesList.get(position).getLastName());
    }

    @Override
    public int getItemCount() {
        return attendeesList.size();
    }

    public void addItem(Attendee attendee) {
        attendeesList.add(attendee);
        notifyItemInserted(attendeesList.size());
    }

    public void removeItem(int position) {
        attendeesList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, attendeesList.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView attendeeName;
        public ViewHolder(View view) {
            super(view);

            attendeeName = (TextView)view.findViewById(R.id.attendee_name);
        }
    }
}
