package com.yusriyusron.practice.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yusriyusron.practice.data.Artist;
import com.yusriyusron.practice.activity.MainActivity;
import com.yusriyusron.practice.R;
import com.yusriyusron.practice.activity.TrackActivity;

import java.util.ArrayList;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistsViewHolder> {

    private Context context;
    private ArrayList<Artist> dataArtist;

    public ArtistAdapter(Context context, ArrayList<Artist> dataArtist) {
        this.context = context;
        this.dataArtist = dataArtist;
    }

    @NonNull
    @Override
    public ArtistsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_artist_item,viewGroup,false);
        return new ArtistsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistsViewHolder artistsViewHolder, final int i) {
        final Artist artist = dataArtist.get(i);
        artistsViewHolder.textViewArtist.setText(artist.getArtistName());
        artistsViewHolder.textViewGenre.setText(artist.getArtistGenre());
        artistsViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,TrackActivity.class);
                intent.putExtra(MainActivity.ARTIST_ID, artist.getArtistId());
                intent.putExtra(MainActivity.ARTIST_NAME,artist.getArtistName());
                context.startActivity(intent);
            }
        });
        artistsViewHolder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Artist artist = dataArtist.get(i);
                // Show Update AlertDialog from method showUpdateDialog
                showUpdateDialog(artist.getArtistId(),artist.getArtistName());
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataArtist.size();
    }

    public class ArtistsViewHolder extends RecyclerView.ViewHolder {
        final TextView textViewArtist,textViewGenre;
        final CardView cardView;
        public ArtistsViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewArtist = itemView.findViewById(R.id.textViewArtist);
            textViewGenre = itemView.findViewById(R.id.textViewGenre);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }

    public void showUpdateDialog(final String artistId, String artistName){
        // Set AlertDialog.Builder in context (MainActivity.java)
        // But not always MainActivity.java, can be other activity
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

        //Set layout for display in Android
        LayoutInflater inflater = LayoutInflater.from(context);
        final View dialogView = inflater.inflate(R.layout.activity_main,null);
        dialogBuilder.setView(dialogView);

        final EditText editTextName = dialogView.findViewById(R.id.editTextName);
        final Spinner spinnerGenre = dialogView.findViewById(R.id.spinnerGenre);
        final Button buttonUpdate = dialogView.findViewById(R.id.buttonAdd);
        final Button buttonDelete = dialogView.findViewById(R.id.buttonDelete);

        dialogBuilder.setTitle("Updating Artist "+artistName);

        //Make a AlertDialog in Activity
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        editTextName.setHint("Enter new name");
        buttonUpdate.setText("Update");
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextName.getText().toString().trim();
                String genre = spinnerGenre.getSelectedItem().toString();

                if (TextUtils.isEmpty(name)){
                    editTextName.setError("Name Required");
                }else {
                    updateArtist(artistId,name,genre);
                    alertDialog.dismiss();
                }
            }
        });

        buttonDelete.setVisibility(View.VISIBLE);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteArtist(artistId);
                alertDialog.dismiss();
            }
        });
    }

    private boolean updateArtist(String artistId, String artistName, String artistGenre){
        // Get data artist from artistId
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("artists").child(artistId);

        // Set Artist Data
        Artist artist = new Artist(artistId,artistName,artistGenre);
        databaseReference.setValue(artist);

        Toast.makeText(context,"Artist Updated",Toast.LENGTH_SHORT).show();

        return true;
    }

    private void deleteArtist(String artistId){
        DatabaseReference databaseReferenceArtists = FirebaseDatabase.getInstance().getReference("artists").child(artistId);
        DatabaseReference databaseReferenceTracks = FirebaseDatabase.getInstance().getReference("tracks").child(artistId);

        databaseReferenceArtists.removeValue();
        databaseReferenceTracks.removeValue();

        Toast.makeText(context,"Artist Deleted",Toast.LENGTH_SHORT).show();
    }
}
