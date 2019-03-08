package com.yusriyusron.practice.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yusriyusron.practice.R;
import com.yusriyusron.practice.adapter.TrackAdapter;
import com.yusriyusron.practice.data.Track;

import java.util.ArrayList;

public class TrackActivity extends AppCompatActivity {

    private TextView textViewArtistName;
    private EditText editTextTrackName;
    private SeekBar seekBarRating;
    private RecyclerView recyclerViewTrack;
    private TextView textViewEmptyData;

    private Button buttonAddTrack;

    private ArrayList<Track> dataListTrack;

    private DatabaseReference databaseTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        textViewArtistName = findViewById(R.id.textViewArtistName);
        editTextTrackName = findViewById(R.id.editTextTrackName);
        seekBarRating = findViewById(R.id.seekBarRating);
        recyclerViewTrack = findViewById(R.id.recyclerViewTrack);
        buttonAddTrack = findViewById(R.id.buttonAddTrack);

        dataListTrack = new ArrayList<>();

        textViewEmptyData = findViewById(R.id.emptyData);

        // Get data from Artist
        String id = getIntent().getStringExtra(MainActivity.ARTIST_ID);
        String name = getIntent().getStringExtra(MainActivity.ARTIST_NAME);

        textViewArtistName.setText(name);

        setTitle("Tracks");

        // Tree "Tracks" in FIREBASE be a Foreign Key from tree "Artist" (id Artist)
        databaseTrack = FirebaseDatabase.getInstance().getReference("tracks").child(id);

        buttonAddTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTrack();
            }
        });

        databaseTrack.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataListTrack.clear();
                if (dataSnapshot.exists()){
                    for (DataSnapshot trackSnapshot : dataSnapshot.getChildren()){
                        Track track = trackSnapshot.getValue(Track.class);
                        dataListTrack.add(track);
                    }
                    LinearLayoutManager layoutManager = new LinearLayoutManager(TrackActivity.this);
                    TrackAdapter trackAdapter = new TrackAdapter(TrackActivity.this,dataListTrack);
                    recyclerViewTrack.setAdapter(trackAdapter);
                    recyclerViewTrack.setLayoutManager(layoutManager);
                    textViewEmptyData.setVisibility(View.GONE);
                }else {
                   textViewEmptyData.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveTrack(){
        String trackName = editTextTrackName.getText().toString().trim();
        int trackRating = seekBarRating.getProgress();

        if (!TextUtils.isEmpty(trackName)){
            String id = databaseTrack.push().getKey();

            Track track = new Track(id,trackName,trackRating);

            // Add data track (id,trackName,trackRating) to Firebase
            databaseTrack.child(id).setValue(track);

            Toast.makeText(this,"Track Added",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this,"Enter a name",Toast.LENGTH_SHORT).show();
        }
    }
}
