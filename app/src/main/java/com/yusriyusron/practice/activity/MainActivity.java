package com.yusriyusron.practice.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yusriyusron.practice.R;
import com.yusriyusron.practice.adapter.ArtistAdapter;
import com.yusriyusron.practice.data.Artist;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String ARTIST_ID = "artistid";
    public static final String ARTIST_NAME = "artistname";

    private EditText editTextName;
    private Spinner spinnerGenre;
    private Button buttonAdd, buttonDelete;
    private RecyclerView recyclerView;
    private TextView textViewEmptyData;

    private ArrayList<Artist> dataArtist;

    private DatabaseReference databaseArtists;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Add tree "artists" to firebase
        databaseArtists = FirebaseDatabase.getInstance().getReference("artists");

        editTextName = findViewById(R.id.editTextName);
        spinnerGenre = findViewById(R.id.spinnerGenre);
        buttonAdd = findViewById(R.id.buttonAdd);
        recyclerView = findViewById(R.id.recyclerView);

        dataArtist = new ArrayList<>();
        setTitle("Artists");

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addArtist();
            }
        });

        buttonDelete = findViewById(R.id.buttonDelete);
        buttonDelete.setVisibility(View.GONE);

        textViewEmptyData = findViewById(R.id.emptyData);

        // Add value to recycler view or relating data from firebase
        databaseArtists.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataArtist.clear();
                if (dataSnapshot.exists()){
                    for (DataSnapshot artistSnapshot : dataSnapshot.getChildren()){
                        Artist artist = artistSnapshot.getValue(Artist.class);

                        // Add data to ArrayList from firebase
                        dataArtist.add(artist);
                    }

                    // Set data from ArrayList to ArtistAdapter
                    ArtistAdapter adapter = new ArtistAdapter(MainActivity.this,dataArtist);
                    // Set Scroll data in recycler view to vertical
                    LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
                    // Set Scroll data in recycler view to horizontal
                    // LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapter);

                    textViewEmptyData.setVisibility(View.GONE);
                }else {
                    textViewEmptyData.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addArtist(){
        String name = editTextName.getText().toString().trim();
        String genre = spinnerGenre.getSelectedItem().toString();

        if (!TextUtils.isEmpty(name)){
            String id = databaseArtists.push().getKey();
            Artist artist = new Artist(id,name,genre);
            databaseArtists.child(id).setValue(artist);
            Toast.makeText(this,"Artist Added",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this,"Enter a name",Toast.LENGTH_SHORT).show();
        }
    }
}
