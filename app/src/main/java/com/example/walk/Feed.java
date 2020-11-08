package com.example.walk;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Feed extends AppCompatActivity {

    static androidx.recyclerview.widget.RecyclerView recyclerView;
    BottomNavigationView navigationView;
    static ArrayList<FeedVO> list = new ArrayList<>();
    static ArrayList<FeedVO> firebaseList = new ArrayList<>();
    static Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

/*        FeedVO data =  (FeedVO)getIntent().getSerializableExtra("feed");

        if(data != null){
            list.add(data);
        }*/
/*
        FirebaseDatabase.getInstance().getReference("feed").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d("MainActivity", "Single ValueEventListener : " + snapshot.getValue());
                    list.add(snapshot.getValue(FeedVO.class));
                }
                reset();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

        recyclerView = (androidx.recyclerview.widget.RecyclerView) findViewById(R.id.feed_RecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FeedAdapter adapter = new FeedAdapter(list);
        recyclerView.setAdapter(adapter);

        ActionBar actionBar = getSupportActionBar();  //제목줄 객체 얻어오기
        actionBar.setTitle("피드");  //액션바 제목설정
        actionBar.setDisplayHomeAsUpEnabled(true);   //뒤로가는 버튼 <- 만들기

        //네비게이션 바 설정
        navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_journal:
                        startActivity(new Intent(getApplicationContext(), Journal.class));
                        finish();
                        return true;
                    case R.id.action_route:
                        startActivity(new Intent(getApplicationContext(), WalkRoute.class));
                        return true;
                    case R.id.action_home:
                        finish();
                        return true;
                    case R.id.action_feed:
                        return true;
                    case R.id.action_person:
                        startActivity(new Intent(getApplicationContext(), Mypage.class));
                        finish();
                        return true;
                }
                return false;
            }
        });

        navigationView.setSelectedItemId(R.id.action_feed);

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

    }

    public static void reset() {
        FeedAdapter adapter = new FeedAdapter(list);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        navigationView.setSelectedItemId(R.id.action_feed);
        FeedAdapter adapter = new FeedAdapter(list);
        recyclerView.setAdapter(adapter);
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feedmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            //포스트 추가 아이콘을 누를 시
            case R.id.btn_writing:
                Intent intent = new Intent(getApplicationContext(), FeedWriting.class);
                startActivity(intent);
                return true;
            //뒤로가기 버큰을 누를 시
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}