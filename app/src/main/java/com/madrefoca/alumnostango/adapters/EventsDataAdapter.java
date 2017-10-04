package com.madrefoca.alumnostango.adapters;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.madrefoca.alumnostango.R;
import com.madrefoca.alumnostango.model.Event;
import com.madrefoca.alumnostango.utils.ManageFragmentsNavigation;

import java.util.ArrayList;


/**
 * Created by fernando on 09/09/17.
 */

public class EventsDataAdapter extends RecyclerView.Adapter<EventsDataAdapter.ViewHolder>{

    private ArrayList<Event> eventsList;
    private Context context;

    public EventsDataAdapter(ArrayList eventsList, Context context) {
        this.context = context;
        this.eventsList = eventsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event_row_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView eventName;
        public ViewHolder(View view) {
            super(view);
            eventName = (TextView)view.findViewById(R.id.event_name);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.d("EventDataAdapter", "onClick " + eventsList.get(getPosition()).getName()+ " " + eventsList.get(getPosition()).getIdEvent());
            ManageFragmentsNavigation.navItemIndex = 10;
            ManageFragmentsNavigation.CURRENT_TAG = ManageFragmentsNavigation.TAG_ATTENDEE_EVENT_PAYMENT;

            Bundle bundle = new Bundle();
            bundle.putInt("eventId", eventsList.get(getPosition()).getIdEvent());

            // update the main content by replacing fragments
            Fragment fragment = ManageFragmentsNavigation.getHomeFragment();
            fragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction = ((Activity) context).getFragmentManager().beginTransaction();

            fragmentTransaction.replace(R.id.frame, fragment, ManageFragmentsNavigation.CURRENT_TAG);
            fragmentTransaction.commitAllowingStateLoss();
        }
    }
}
