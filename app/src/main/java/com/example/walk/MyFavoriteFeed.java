package com.example.walk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyFavoriteFeed extends AppCompatActivity {

    androidx.recyclerview.widget.RecyclerView recyclerView;
    static ArrayList<FeedVO> list = new ArrayList<>();
    static Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_favorite_feed);

        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case 1:
                        reset();
                        break;
                }
            }
        };

        recyclerView = (androidx.recyclerview.widget.RecyclerView) findViewById(R.id.recycler_my_favorite_feed);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        MyFavoriteFeedAdapter adapter = new MyFavoriteFeedAdapter(Feed.list);
        recyclerView.setAdapter(adapter);
    }

    private void reset() {
        MyFavoriteFeedAdapter adapter = new MyFavoriteFeedAdapter(Feed.list);
        recyclerView.setAdapter(adapter);
    }
}