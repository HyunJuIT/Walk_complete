package com.example.walk;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

public class Memo extends AppCompatActivity implements OnMapReadyCallback {

    MapView memoMap;
    GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);

        memoMap = (MapView) findViewById(R.id.memoMap);
        memoMap.onCreate(savedInstanceState);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng seoul = new LatLng(37.566, 126.978);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(seoul));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(10f));
    }

    @Override
    protected void onStart() {
        memoMap.onStart();
        super.onStart();
    }

    @Override
    protected void onPostResume() {
        memoMap.onStart();
        super.onPostResume();
    }

    @Override
    protected void onPause() {
        memoMap.onStart();
        super.onPause();
    }

    @Override
    protected void onStop() {
        memoMap.onStart();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        memoMap.onStart();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        memoMap.onStart();
        super.onLowMemory();
    }
}