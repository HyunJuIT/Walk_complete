package com.example.walk;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WalkEnd extends AppCompatActivity implements OnMapReadyCallback {

    TextView txt_km, txt_time, txt_speed, txt_cal, txt_today;
    EditText edit_title, edit_diary;
    Button btn_OK;
    GoogleMap mMap;
    MapView mapView;
    GpsTracker gpsTracker;
    Marker currentMarker;
    String date, time, timeVO;
    WalkInformation information;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    static boolean check = false;
    private PolylineOptions polylineOptions;
    private ArrayList<LatLng> arrayPoints = new ArrayList<>();
    static ArrayList<location> locations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_end);

        txt_km = (TextView) findViewById(R.id.txt_walk_end_km);
        txt_time = (TextView) findViewById(R.id.txt_walk_end_time);
        txt_speed = (TextView) findViewById(R.id.txt_walk_end_speed);
        txt_cal = (TextView) findViewById(R.id.txt_walk_end_cal);
        txt_today = (TextView) findViewById(R.id.txt_walk_end_today);

        edit_title = (EditText) findViewById(R.id.edit_walk_end_title);
        edit_diary = (EditText) findViewById(R.id.edit_walk_end_diary);

        btn_OK = (Button) findViewById(R.id.btn_walk_end_OK);

        mapView = (MapView) findViewById(R.id.walk_end_map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        Intent intent = getIntent();

        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");
        date = simpleDate.format(new Date());

        SimpleDateFormat simpleTimes = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat simpleTime = new SimpleDateFormat("a hh:mm");
        /*        SimpleDateFormat simpleA = new SimpleDateFormat("a", Locale.US);*/

        timeVO = simpleTime.format(new Date());
        time = simpleTimes.format(new Date());
        txt_today.setText(date + " " + time);

        if (intent != null) {
            String str = intent.getStringExtra("km");
            String result = str.substring(0, str.length() - 2);
            txt_km.setText(result);
            txt_time.setText(intent.getStringExtra("time"));
            txt_cal.setText(intent.getStringExtra("cal"));
            txt_speed.setText(intent.getStringExtra("speed"));
        }

        btn_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(check==false) {
                    SimpleDateFormat postText = new SimpleDateFormat("E요일 a 산책");

                    MemoVO memoVO = new MemoVO(true, "산책종료", postText.format(new Date()), date, timeVO, getCurrentAddress(WalkStart.gpsTracker.getLatitude(), WalkStart.gpsTracker.getLongitude()), WalkStart.gpsTracker.getLatitude(), WalkStart.gpsTracker.getLongitude(), null);
                    memoVO.setWalkTitle(edit_title.getText().toString());
                    memoVO.setDiary(edit_diary.getText().toString());
                    memoVO.setInformation(information);
                    WalkRecord.list.add(memoVO);

                    SimpleDateFormat datefileName = new SimpleDateFormat("yyyy-MM-dd");
                    String namedateNAme = datefileName.format(new Date());

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("memo").child(MainActivity.name).child(namedateNAme);

                    myRef.setValue(WalkRecord.list);
                }
                if (check == false) {
                    androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(WalkEnd.this);
                    alert.setTitle("루트 추가");
                    alert.setMessage("오늘의 산책 루트를 내 루트에 추가하겠습니까?");
                    alert.setPositiveButton("네", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            check = true;
                            Intent intent1 = new Intent(WalkEnd.this, MyRouteDialog.class);
                            intent1.putExtra("myrouteadress", WalkStart.adress);

                            SimpleDateFormat datefileName = new SimpleDateFormat("yyyy-MM-dd");
                            String namedateNAme = datefileName.format(new Date());

                            intent1.putExtra("myroutedate", namedateNAme);
                            startActivity(intent1);
                            dialog.cancel();
                        }
                    });

                    alert.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            check = false;
                            dialog.cancel();
                            finish();
                        }
                    });
                    alert.show();
                }else{
                    check = false;
                    finish();
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        double latitude = WalkStart.information.getLatLng().getLatitude();// 위도
        double longitude = WalkStart.information.getLatLng().getLongitude(); //경도

        LatLng latLng = new LatLng(latitude, longitude);

        /*//지도 마커
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("현재위치");
        mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));*/
        setCurrentLocation(latLng,"현재위치","");
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
        for (int i = 0; i < WalkStart.locations.size();i++){
            arrayPoints.add(new LatLng(WalkStart.locations.get(i).getLatitude(),WalkStart.locations.get(i).getLongitude()));
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

    public String getCurrentAddress(double latitude, double longitude) {
        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 100);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            showDialogForLocationServiceSetting();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            showDialogForLocationServiceSetting();
            return "잘못된 GPS 좌표";
        }
        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            showDialogForLocationServiceSetting();
            return "주소 미발견";
        }
        Address address = addresses.get(0);
        return address.getAddressLine(0).toString();
    }

    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(WalkEnd.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n" + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onStart() {
        mapView.onStart();
        super.onStart();
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