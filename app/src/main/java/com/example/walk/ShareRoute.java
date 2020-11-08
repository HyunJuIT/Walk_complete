package com.example.walk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ShareRoute extends Fragment {

    androidx.recyclerview.widget.RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.activity_share_route, container, false);

        recyclerView = viewGroup.findViewById(R.id.recycler_share_route_detail);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        return viewGroup;
    }

    @Override
    public void onStart() {
        super.onStart();
        reset();
    }

    public void reset(){
        ShareRouteAdapter adapter = new ShareRouteAdapter(MyRoute.list);
        recyclerView.setAdapter(adapter);
    }
}