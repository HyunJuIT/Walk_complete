package com.example.walk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BookMarkRoute extends Fragment {

    androidx.recyclerview.widget.RecyclerView recyclerView;
    static Handler handler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup)inflater.inflate(R.layout.activity_book_mark_route,container,false);

        recyclerView = viewGroup.findViewById(R.id.recycler_book_mark_route);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        reset();

        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case 1:
                        reset();
                }
            }
        };

        return viewGroup;
    }

    @Override
    public void onStart() {
        super.onStart();
        reset();
    }

    public void reset(){
        ShareRouteBookMarkAdapter adapter = new ShareRouteBookMarkAdapter(MyRoute.list);
        recyclerView.setAdapter(adapter);
    }
}