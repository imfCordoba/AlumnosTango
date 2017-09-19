package com.madrefoca.alumnostango.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.madrefoca.alumnostango.R;
import com.madrefoca.alumnostango.model.Event;

import java.util.ArrayList;


/**
 * Created by fernando on 09/09/17.
 */

public class EventsDataAdapter extends RecyclerView.Adapter{

    private ArrayList<Event> eventsList;

    public EventsDataAdapter(ArrayList eventsList) {
        this.eventsList = eventsList;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event_row_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder)holder;
        viewHolder.eventName.setText(eventsList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }

    public void addItem(Event event) {
        eventsList.add(event);
        notifyItemInserted(eventsList.size());
    }

    public void removeItem(int position) {
        eventsList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, eventsList.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView eventName;
        public ViewHolder(View view) {
            super(view);

            eventName = (TextView)view.findViewById(R.id.event_name);
        }
    }
}
