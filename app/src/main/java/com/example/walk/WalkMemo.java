package com.example.walk;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class WalkMemo extends Fragment implements OnMapReadyCallback {

    MapView memoMap;
    GoogleMap mMap;
    static ArrayList<Marker> markers = new ArrayList<>();
    static Handler handler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.activity_memo, container, false);

        //프래그먼트에서 findViewById()하는 방법은 View를 받아와야만 가능하다.
        memoMap = (MapView) viewGroup.findViewById(R.id.memoMap);
        memoMap.onCreate(savedInstanceState);
        memoMap.getMapAsync(this);


/*        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case 1:
                        Log.d("mMap", String.valueOf(mMap));
                        if (mMap != null) {
                            addMarkers();
                        }
                }
            }
        };*/

        return viewGroup;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        for (int i = 0; i < WalkRecord.list.size(); i++) {
            MemoVO memoVO = WalkRecord.list.get(i);

            if (!memoVO.isMemoCheck()) {
                LatLng latLng = new LatLng(memoVO.getLatitude(), memoVO.getLongitude());

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(memoVO.getTitle());

                String text = memoVO.getMemo();

                if (text.length() > 10) {
                    text = memoVO.getMemo().substring(0, 10);
                };

                markerOptions.snippet(text);
                markers.add(mMap.addMarker(markerOptions));
            }
        }

        LatLng seoul = new LatLng(37.566, 126.978);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 10));


        /*        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(seoul, 10));*/

/*        mMap.moveCamera(CameraUpdateFactory.newLatLng(seoul));

        mMap.moveCamera(CameraUpdateFactory.zoomTo(10f));*/
    }

    public void addMarkers() {
        for (int i = 0; i < WalkRecord.list.size(); i++) {
            MemoVO memoVO = WalkRecord.list.get(i);

            if (!memoVO.isMemoCheck()) {
                markers.add(mMap.addMarker(getMarkerOption(memoVO)));
            }
        }
    }

    public MarkerOptions getMarkerOption(MemoVO memoVO) {

        LatLng latLng = new LatLng(memoVO.getLatitude(), memoVO.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(memoVO.getTitle());

        String text = memoVO.getMemo();

        if (text.length() > 10) {
            text = memoVO.getMemo().substring(0, 10);
        };

        markerOptions.snippet(text);

        return markerOptions;
    }

    @Override
    public void onStart() {
    /*    Log.d("mMap", String.valueOf(mMap));
        if (mMap != null) {
            addMarkers();
        }*/
        super.onStart();
    }


}
