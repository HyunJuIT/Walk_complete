package com.example.walk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

public class Journal extends AppCompatActivity {

    /*    ImageButton walkLog;
        ImageButton memo;
        ImageButton diary;
        ImageButton album;*/
    TabLayout tabLayout;
    WalkMemo walkMemo;  //프래그먼트 1
    WalkRecord walkRecord;  //프래그먼트 2
    WalkAlbum walkAlbum;    //프레그먼트 3
    BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);

        walkMemo = new WalkMemo();
        walkRecord = new WalkRecord();
        walkAlbum = new WalkAlbum();

        navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_journal:
                        return true;
                    case R.id.action_route:
                        startActivity(new Intent(getApplicationContext(), WalkRoute.class));
                        finish();
                        return true;
                    case R.id.action_home:
                        finish();
                        return true;
                    case R.id.action_feed:
                        startActivity(new Intent(getApplicationContext(), Feed.class));
                        finish();
                        return true;
                    case R.id.action_person:
                        startActivity(new Intent(getApplicationContext(), Mypage.class));
                        finish();
                        return true;
                }
                return false;
            }
        });

        navigationView.setSelectedItemId(R.id.action_journal);

        //프래그먼트 매니져로 먼저 Frame레이아웃에 표기할 프래그먼트 설정
        getSupportFragmentManager().beginTransaction().add(R.id.journalFrame, walkMemo).commit();

        //상단 탭
        tabLayout = (TabLayout) findViewById(R.id.journal_tab);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //선택된 탭 번호 반환
                int position = tab.getPosition();

                Fragment selected = null;

                if (position == 0) {

                    selected = walkMemo;

                } else if (position == 1) {

                    selected = walkRecord;

                } else if (position == 2) {

                    selected = walkAlbum;
                }

                //선택된 프레그먼트로 바꿔줌
                getSupportFragmentManager().beginTransaction().replace(R.id.journalFrame, selected).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

     /*   walkLog = (ImageButton) findViewById(R.id.goWalkLog);
        memo = (ImageButton) findViewById(R.id.goMemo);
        diary = (ImageButton) findViewById(R.id.goDiary);
        album = (ImageButton) findViewById(R.id.goAlbum);

        walkLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),WalkLog.class);
                startActivity(intent);
            }
        });

        memo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Memo.class);
                startActivity(intent);
            }
        });

        diary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Diary.class);
                startActivity(intent);
            }
        });

        album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Album.class);
                startActivity(intent);
            }
        });*/
    }

    @Override
    protected void onResume() {
        navigationView.setSelectedItemId(R.id.action_journal);
        super.onResume();
    }
}