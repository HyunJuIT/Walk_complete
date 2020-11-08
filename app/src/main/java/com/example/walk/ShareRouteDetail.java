package com.example.walk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class ShareRouteDetail extends AppCompatActivity  implements OnMapReadyCallback {

    TextView adress, title, routeExplanation, km, nickname;
    ImageView profile;
    androidx.recyclerview.widget.RecyclerView recyclerView;
    RatingBar ratingBar;
    MyRouteVO myRoute;
    Marker currentMarker;
    GoogleMap mMap;
    MapView mapView;
    private PolylineOptions polylineOptions;
    private ArrayList<LatLng> arrayPoints = new ArrayList<>();
    static ArrayList<location> locations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_route_detail);

        ActionBar actionBar = getSupportActionBar();  //제목줄 객체 얻어오기
        actionBar.setTitle("");  //액션바 제목설정
        actionBar.setDisplayHomeAsUpEnabled(true);   //뒤로가는 버튼 <- 만들기

        adress = (TextView) findViewById(R.id.txt_share_route_detail_adress);
        title = (TextView) findViewById(R.id.txt_share_route_detail_title);
        km = (TextView) findViewById(R.id.txt_share_route_detail_km);
        nickname = (TextView) findViewById(R.id.txt_share_route_detail_nickname);
        profile = (ImageView) findViewById(R.id.txt_share_route_detail_profile);
        routeExplanation = (TextView) findViewById(R.id.txt_share_route_detail_RouteExplanation);
        ratingBar = (RatingBar) findViewById(R.id.share_route_detail_rating);


        recyclerView = (androidx.recyclerview.widget.RecyclerView) findViewById(R.id.recycler_share_route_detail);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();

        if (intent != null) {
            int index = intent.getIntExtra("myrouteindex", -1);
            if (index != -1) {
                myRoute = MyRoute.list.get(index);
                title.setText(myRoute.getRouteTitle());

                String[] adressSetText = myRoute.getAdress().split(" ");
                adress.setText(adressSetText[1] + " " + adressSetText[2] + " " + adressSetText[3]);
                routeExplanation.setText(myRoute.getRouteExplanation());
                nickname.setText(myRoute.getNickname());

                km.setText(String.format("%.2f", myRoute.getInformation().getKm()) + "km");
                ratingBar.setRating(myRoute.getSatisfaction());
            }
        }
        reset();

        mapView = (MapView) findViewById(R.id.mapView_share_route_detail);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        double latitude = myRoute.getLatLngs().get(0).getLatitude();// 위도
        double longitude = myRoute.getLatLngs().get(0).getLongitude(); //경도

        LatLng latLng = new LatLng(latitude, longitude);

        setCurrentLocation(latLng,"","");
        setArrayPoint();
        Line();
    }

    public void setLine(LatLng latLng) {

        locations.add(new location(latLng.latitude, latLng.longitude));

        polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.BLUE);
        polylineOptions.width(5);
        arrayPoints.add(latLng);
        polylineOptions.addAll(arrayPoints);
        mMap.addPolyline(polylineOptions);
    }

    public void setArrayPoint(){
        for (int i = 0; i < myRoute.getLatLngs().size();i++){
            Log.d("테스트를 하고있어요",i+"");
            arrayPoints.add(new LatLng(myRoute.getLatLngs().get(i).getLatitude(),myRoute.getLatLngs().get(i).getLongitude()));
        }
    }

    public void Line(){
        polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.BLUE);
        polylineOptions.width(5);
        polylineOptions.addAll(arrayPoints);
        mMap.addPolyline(polylineOptions);
    }

    public void setCurrentLocation(LatLng location, String markerTitle, String markerSnippet) {

        if (currentMarker != null) currentMarker.remove();


        LatLng currentLatLng = location;

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);


        currentMarker = mMap.addMarker(markerOptions);

/*        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
        mMap.moveCamera(cameraUpdate);*/
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 17));

    }


    public void reset() {
        RouteImageAdapter adapter = new RouteImageAdapter(myRoute.getImgs());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPostResume() {
        mapView.onStart();
        super.onPostResume();
    }

    @Override
    protected void onPause() {
        mapView.onStart();
        super.onPause();
    }

    @Override
    protected void onStop() {
        mapView.onStart();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mapView.onStart();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        mapView.onStart();
        super.onLowMemory();
    }
}