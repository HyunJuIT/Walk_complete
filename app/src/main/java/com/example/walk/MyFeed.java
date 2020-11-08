package com.example.walk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyFeed extends AppCompatActivity {

    androidx.recyclerview.widget.RecyclerView recyclerView;
    static ArrayList<FeedVO> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_feed);

        recyclerView = (androidx.recyclerview.widget.RecyclerView) findViewById(R.id.recycler_my_feed);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FeedAdapter adapter = new FeedAdapter(list);
        recyclerView.setAdapter(adapter);

    }

    private void reset() {
        FeedAdapter adapter = new FeedAdapter(list);
        recyclerView.setAdapter(adapter);
    }
}