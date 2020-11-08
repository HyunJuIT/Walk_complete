package com.example.walk;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class WalkAlbum extends Fragment {

    static ArrayList<String> img = new ArrayList();
    androidx.recyclerview.widget.RecyclerView recyclerView;
    GridLayoutManager layoutManager;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup)inflater.inflate(R.layout.activity_walk_album,container,false);

        pref = getContext().getSharedPreferences("mine", getContext().MODE_PRIVATE);
        editor = pref.edit();

        FirebaseDatabase.getInstance().getReference("album").child(MainActivity.name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                img.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d("MainActivity", "Single ValueEventListener : " + snapshot.getValue());
                    img.add(snapshot.getValue(String.class));
                }
                reset();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        recyclerView = viewGroup.findViewById(R.id.recycler_album);
        ImageAdapter imageAdapter = new ImageAdapter(null,img,true);

        layoutManager = new GridLayoutManager(getContext(), 3);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(imageAdapter);

        return viewGroup;
    }

    public void reset(){
        ImageAdapter imageAdapter = new ImageAdapter(null,img,true);
        recyclerView.setAdapter(imageAdapter);
    }

    @Override
    public void onPause() {

        super.onPause();
    }
}
