package com.example.walk;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WalkRecord extends Fragment {

    androidx.recyclerview.widget.RecyclerView recyclerView;
    public static ArrayList<MemoVO> list = new ArrayList<>();
    TextView today,memoCount;
    CalendarView calendarView;
    static boolean check = true;
    static Handler handler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.activity_walk_record, container, false);

        today = viewGroup.findViewById(R.id.today_walk_record);
        memoCount = viewGroup.findViewById(R.id.today_walk_memo_count);
        calendarView = viewGroup.findViewById(R.id.calendarView);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                String date = year+"";

                if (month + 1 < 10) {
                    date += "-0" + (month + 1);
                } else {
                    date += "-" + (month + 1);
                }

                if (dayOfMonth < 10) {
                    date += "-0" + (dayOfMonth);
                } else {
                    date += "-" + dayOfMonth;
                }

                today.setText(date);

                FirebaseDatabase.getInstance().getReference("memo").child(MainActivity.name).child(date).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        list.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Log.d("MainActivity", "Single ValueEventListener : " + snapshot.getKey());
                            WalkRecord.list.add(snapshot.getValue(MemoVO.class));
                        }
                        reset(list);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        recyclerView = (androidx.recyclerview.widget.RecyclerView) viewGroup.findViewById(R.id.recycler_memo_record);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        System.out.println(list.size());
        RecordAdapter adapter = new RecordAdapter(list);
        recyclerView.setAdapter(adapter);

        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case 1:
                        reset(list);
                        break;
                }
            }
        };

        return viewGroup;
    }

    public void reset(ArrayList<MemoVO> list){
        RecordAdapter adapter = new RecordAdapter(list);
        recyclerView.setAdapter(adapter);
        memoCount.setText("메모 "+list.size()+"개");
    }

    @Override
    public void onStart() {
        reset(list);
       /* SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");
        today.setText(simpleDate.format(new Date()));
        check = true;

        FirebaseDatabase.getInstance().getReference("memo").child(MainActivity.name).child(simpleDate.format(new Date())).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d("MainActivity", "Single ValueEventListener : " + snapshot.getValue());
                    list.add(snapshot.getValue(MemoVO.class));
                }
                reset(list);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
        super.onStart();
    }

    @Override
    public void onPause() {
       /* SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");
        today.setText(simpleDate.format(new Date()));

        if(check){
            FirebaseDatabase.getInstance().getReference("memo").child(MainActivity.name).child(simpleDate.format(new Date())).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    list.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Log.d("MainActivity", "Single ValueEventListener : " + snapshot.getValue());
                        list.add(snapshot.getValue(MemoVO.class));
                    }
                    reset(list);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }*/

        super.onPause();
    }
}
