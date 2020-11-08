package com.example.walk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class MyRoute extends Fragment {

    static ArrayList<MyRouteVO> list = new ArrayList<>();
    androidx.recyclerview.widget.RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.activity_my_route, container, false);

        recyclerView = viewGroup.findViewById(R.id.recyclerView_my_route);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        MyRouteAdapter adapter = new MyRouteAdapter(list);
        recyclerView.setAdapter(adapter);

        return viewGroup;
    }

    @Override
    public void onStart() {
        super.onStart();
        reset();
    }

    public void reset(){
        MyRouteAdapter adapter = new MyRouteAdapter(list);
        recyclerView.setAdapter(adapter);
    }
}