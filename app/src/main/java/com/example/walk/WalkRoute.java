package com.example.walk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

public class WalkRoute extends AppCompatActivity {

    TabLayout tabLayout;
    MyRoute myRoute; //프래그먼트 1
    BookMarkRoute bookMarkRoute; //프래그먼트 2
    ShareRoute shareRoute; //프레그먼트 3
    BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_route);

        myRoute = new MyRoute();
        bookMarkRoute = new BookMarkRoute();
        shareRoute = new ShareRoute();

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

        navigationView.setSelectedItemId(R.id.action_route);

        //프래그먼트 매니져로 먼저 Frame레이아웃에 표기할 프래그먼트 설정
        getSupportFragmentManager().beginTransaction().add(R.id.routeFrame, myRoute).commit();

        //상단 탭
        tabLayout = (TabLayout) findViewById(R.id.route_tab);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //선택된 탭 번호 반환
                int position = tab.getPosition();

                Fragment selected = null;

                if (position == 0) {

                    selected = myRoute;

                } else if (position == 1) {

                    selected = bookMarkRoute;

                } else if (position == 2) {

                    selected = shareRoute;
                }

                //선택된 프레그먼트로 바꿔줌
                getSupportFragmentManager().beginTransaction().replace(R.id.routeFrame, selected).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    protected void onResume() {
        navigationView.setSelectedItemId(R.id.action_route);
        super.onResume();
    }
}