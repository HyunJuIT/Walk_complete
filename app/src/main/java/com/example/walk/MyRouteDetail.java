package com.example.walk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MyRouteDetail extends AppCompatActivity  implements OnMapReadyCallback {

    TextView adress, date, title, routeExplanation, km;
    MapView mapView;
    Marker currentMarker;
    androidx.recyclerview.widget.RecyclerView recyclerView;
    RatingBar ratingBar;
    MyRouteVO myRoute;
    GoogleMap mMap;
    private PolylineOptions polylineOptions;
    private ArrayList<LatLng> arrayPoints = new ArrayList<>();
    static ArrayList<location> locations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_route_detail);


        ActionBar actionBar = getSupportActionBar();  //제목줄 객체 얻어오기
        actionBar.setTitle("");  //액션바 제목설정
        actionBar.setDisplayHomeAsUpEnabled(true);   //뒤로가는 버튼 <- 만들기

        adress = (TextView) findViewById(R.id.txt_my_route_detail_adress);
        title = (TextView) findViewById(R.id.txt_my_route_detail_title);
        km = (TextView) findViewById(R.id.txt_my_route_detail_km);
        date = (TextView) findViewById(R.id.txt_my_route_detail_date);
        routeExplanation = (TextView) findViewById(R.id.txt_my_route_detail_RouteExplanation);
        ratingBar = (RatingBar) findViewById(R.id.my_route_detail_rating);

        recyclerView = (androidx.recyclerview.widget.RecyclerView) findViewById(R.id.recycler_my_route_detail);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();

        if (intent != null) {
            int index = intent.getIntExtra("myrouteindex", -1);
            if (index != -1) {
                myRoute = MyRoute.list.get(index);
                title.setText(myRoute.getRouteTitle());

                String[] adressSetText = myRoute.getAdress().split(" ");
                adress.setText(adressSetText[1] + " " + adressSetText[2] + " " + adressSetText[3]);
                date.setText(myRoute.getDate());
                routeExplanation.setText(myRoute.getRouteExplanation());

                km.setText(String.format("%.2f", myRoute.getInformation().getKm()) + "km");
                ratingBar.setRating(myRoute.getSatisfaction());
            }
        }

        mapView = (MapView) findViewById(R.id.mapView_my_route_detail);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        reset();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        double latitude = myRoute.getLatLngs().get(0).getLatitude();// 위도
        double longitude = myRoute.getLatLngs().get(0).getLongitude(); //경도

        LatLng latLng = new LatLng(latitude, longitude);

        //지도 마커
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