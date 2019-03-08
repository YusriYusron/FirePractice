package com.yusriyusron.practice.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yusriyusron.practice.R;
import com.yusriyusron.practice.data.Track;

import java.util.ArrayList;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.TrackViewHolder> {

    private Context context;
    private ArrayList<Track> dataListTrack;

    public TrackAdapter(Context context, ArrayList<Track> dataListTrack) {
        this.context = context;
        this.dataListTrack = dataListTrack;
    }

    @NonNull
    @Override
    public TrackViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_track_item,viewGroup,false);
        return new TrackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackViewHolder trackViewHolder, int i) {
        final Track track = dataListTrack.get(i);
        trackViewHolder.textViewTrackName.setText(track.getTrackName());
        trackViewHolder.textViewTrackRating.setText("Rating "+String.valueOf(track.getTrackRating()));
    }

    @Override
    public int getItemCount() {
        return dataListTrack.size();
    }

    public class TrackViewHolder extends RecyclerView.ViewHolder {
        final TextView textViewTrackName,textViewTrackRating;
        public TrackViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTrackName = itemView.findViewById(R.id.textViewTrack);
            textViewTrackRating = itemView.findViewById(R.id.textViewRating);
        }
    }
}
