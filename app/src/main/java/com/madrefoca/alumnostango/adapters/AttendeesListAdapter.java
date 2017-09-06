package com.madrefoca.alumnostango.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.madrefoca.alumnostango.R;
import com.madrefoca.alumnostango.model.Attendee;
import com.madrefoca.alumnostango.utils.RecyclerItemClickListener;
import com.madrefoca.alumnostango.widgets.LetterTile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fernando on 05/09/17.
 */

public class AttendeesListAdapter extends
        RecyclerView.Adapter<AttendeesListAdapter.AttendeesHolder>{

    private List<Attendee> attendeeList;
    private Context context;
    private RecyclerItemClickListener recyclerItemClickListener;

    public AttendeesListAdapter(Context context) {
        this.context = context;
        this.attendeeList = new ArrayList<>();
    }

    private void add(Attendee item) {
        attendeeList.add(item);
        notifyItemInserted(attendeeList.size() - 1);
    }

    public void addAll(List<Attendee> attendeeList) {
        for (Attendee attendee : attendeeList) {
            add(attendee);
        }
    }

    public void remove(Attendee item) {
        int position = attendeeList.indexOf(item);
        if (position > -1) {
            attendeeList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public Attendee getItem(int position) {
        return attendeeList.get(position);
    }

    @Override
    public AttendeesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_attendee_item, parent, false);
        final AttendeesHolder attendeesHolder = new AttendeesHolder(view);
        attendeesHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPos = attendeesHolder.getAdapterPosition();
                if (adapterPos != RecyclerView.NO_POSITION) {
                    if (recyclerItemClickListener != null) {
                        recyclerItemClickListener.onItemClick(adapterPos, attendeesHolder.itemView);
                    }
                }
            }
        });
        return attendeesHolder;
    }

    @Override
    public void onBindViewHolder(AttendeesHolder holder, int position) {
        final Attendee attendee = attendeeList.get(position);
        final Resources res = context.getResources();
        final int tileSize = res.getDimensionPixelSize(R.dimen.letter_tile_size);
        LetterTile letterTile = new LetterTile(context);
        Bitmap letterBitmap = letterTile.getLetterTile(attendee.getName(),
                String.valueOf(attendee.getDni()), tileSize, tileSize);
        holder.thumb.setImageBitmap(letterBitmap);
        holder.name.setText(attendee.getName());
        holder.phone.setText(attendee.getCellphoneNumber());
    }

    @Override
    public int getItemCount() {
        return attendeeList.size();
    }

    public void setOnItemClickListener(RecyclerItemClickListener recyclerItemClickListener) {
        this.recyclerItemClickListener = recyclerItemClickListener;
    }

    static class AttendeesHolder extends RecyclerView.ViewHolder {
        ImageView thumb;
        TextView name;
        TextView phone;
        public AttendeesHolder(View itemView) {
            super(itemView);
            thumb = (ImageView) itemView.findViewById(R.id.thumb);
            name = (TextView) itemView.findViewById(R.id.attendee_name);
            phone = (TextView) itemView.findViewById(R.id.attendee_phone);
        }
    }
}
