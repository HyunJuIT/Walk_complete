package com.example.walk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.transition.TransitionManager;
import android.util.EventLog;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WalkStart extends AppCompatActivity implements OnMapReadyCallback {

    MapView walkStartMapView;
    GoogleMap mMap;
    ImageButton post, camera, btn_Start, btn_stop, btn_interrupt;
    TextView time, km, cal, speed;
    static GpsTracker gpsTracker;
    static Handler handler;
    static boolean check = true;
    Marker currentMarker;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private static ArrayList<LatLng> latLngs;
    static String adress;
    static WalkInformation information;
    private PolylineOptions polylineOptions;
    private ArrayList<LatLng> arrayPoints = new ArrayList<>();
    static ArrayList<location> locations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_start);

        walkStartMapView = (MapView) findViewById(R.id.walkStartMap);
        walkStartMapView.onCreate(savedInstanceState);
        walkStartMapView.getMapAsync(this);

        gpsTracker = new GpsTracker(WalkStart.this);

        time = (TextView) findViewById(R.id.txt_walk_start_time);
        km = (TextView) findViewById(R.id.txt_walk_start_km);
        cal = (TextView) findViewById(R.id.txt_walk_start_cal);
        speed = (TextView) findViewById(R.id.txt_walk_start_speed);

        camera = (ImageButton) findViewById(R.id.goCamera);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 1);
            }
        });

        post = (ImageButton) findViewById(R.id.btn_post);
        post.setEnabled(false);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WalkStart.this, MemoDialog.class);
                startActivity(intent);
            }
        });

        btn_Start = (ImageButton) findViewById(R.id.btn_walk_Start);
        btn_Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                latLngs = new ArrayList<>();

                v.setVisibility(View.GONE);
                post.setEnabled(true);
                btn_stop.setVisibility(View.VISIBLE);
                btn_interrupt.setVisibility(View.VISIBLE);

                //날짜 데이터 받아오는 코드
                SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");

                SimpleDateFormat simpleTime = new SimpleDateFormat("a hh:mm");
                /* SimpleDateFormat simpleA = new SimpleDateFormat("a", Locale.US);*/

                SimpleDateFormat postText = new SimpleDateFormat("E요일 a 산책");

                adress = getCurrentAddress(gpsTracker.getLatitude(), gpsTracker.getLongitude());

                WalkRecord.list.add(new MemoVO(true, "산책 시작", postText.format(new Date()), simpleDate.format(new Date()), simpleTime.format(new Date()), adress, gpsTracker.getLatitude(), gpsTracker.getLongitude(), null));

                boolean permissionAccessCoarseLocationApproved =
                        ActivityCompat.checkSelfPermission(WalkStart.this,
                                Manifest.permission.ACCESS_COARSE_LOCATION) ==
                                PackageManager.PERMISSION_GRANTED;

                if (permissionAccessCoarseLocationApproved) {
                    Intent intent = new Intent(WalkStart.this, MyWalkService.class);
                    intent.setAction("startForeground");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(intent);
                    }
                } else {
                    // Make a request for foreground-only location access.
                    ActivityCompat.requestPermissions(WalkStart.this, new String[]{
                                    Manifest.permission.ACCESS_COARSE_LOCATION},
                            2);
                }

            }
        });

        btn_stop = (ImageButton) findViewById(R.id.btn_walk_stop);
        btn_stop.setVisibility(View.GONE);
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentService = new Intent(WalkStart.this, MyWalkService.class);
                stopService(intentService);

                Intent intent = new Intent(WalkStart.this, WalkEnd.class);

                intent.putExtra("time", time.getText().toString());
                intent.putExtra("km", km.getText().toString());
                intent.putExtra("cal", cal.getText().toString());
                intent.putExtra("speed", speed.getText().toString());

                startActivity(intent);
                finish();
            }
        });

        btn_interrupt = (ImageButton) findViewById(R.id.btn_walk_interrupt);
        btn_interrupt.setVisibility(View.GONE);
        btn_interrupt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (check) {
                    v.setSelected(true);
                    MyWalkService.onStop();
                    check = false;
                } else {
                    v.setSelected(false);
                    MyWalkService.onReStart();
                    check = true;
                }
            }
        });

        if (MyWalkService.mThread != null) {
            btn_Start.setVisibility(View.GONE);
            post.setEnabled(true);
            btn_stop.setVisibility(View.VISIBLE);
            btn_interrupt.setVisibility(View.VISIBLE);
        }


        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case 1:
                        information = (WalkInformation) msg.obj;

                        int timeLong = information.getTime();
                        String date = "";

                        int hour = timeLong / 3600;
                        int minuteNumber = (timeLong % 3600) / 60;
                        String minute = minuteNumber + "";
                        if (minuteNumber < 10) {
                            minute = "0" + minute;
                        }
                        int secondNumber = timeLong % 60;
                        String second = secondNumber + "";
                        if (secondNumber < 10) {
                            second = "0" + second;
                        }

                        if (timeLong >= 3600) {
                            date += hour + ":" + minute + ":" + second;
                        } else if (timeLong >= 60) {
                            date += minute + ":" + second;
                        } else {
                            date += "00:" + second;
                        }

                        time.setText(date);
                        km.setText(String.format("%.2f", (information.getKm())) + "km");

                        String sppedText = String.format("%.2f", information.getSpeed()) + "km/s";
                        if (Double.isNaN(information.getSpeed())) {
                            sppedText = "0m/s";
                        }

                        speed.setText(sppedText);
                        cal.setText(((int) information.getCul()) + "cal");
                        setCurrentLocation(new LatLng(information.getLatLng().getLatitude(), information.getLatLng().getLongitude()), "현재위치", "");
                        setLine(new LatLng(information.getLatLng().getLatitude(), information.getLatLng().getLongitude()));
                }
            }
        };

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

 /*       polyline1 = mMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .add(
                        new LatLng(-35.016, 143.321),
                        new LatLng(-34.747, 145.592),
                        new LatLng(-34.364, 147.891),
                        new LatLng(-33.501, 150.217),
                        new LatLng(-32.306, 149.248),
                        new LatLng(-32.491, 147.309)));

        List<LatLng> list = polyline1.getPoints();
        Log.d("포인트", String.valueOf(list));*/

    /*    double latitude = gpsTracker.getLatitude();// 위도
        double longitude = gpsTracker.getLongitude(); //경도

        LatLng latLng = new LatLng(latitude, longitude);*/
        Location location = gpsTracker.getLocation();

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

   /*     //지도 마커
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("현재위치");
        mMap.addMarker(markerOptions);*/
        setLine(latLng);
        setCurrentLocation(latLng, "현재위치", "");

        /*        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));*/
    }

    @Override
    protected void onStart() {
        walkStartMapView.onStart();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.FOREGROUND_SERVICE) == PackageManager.PERMISSION_GRANTED) {
                Log.d("test", "권한 설정 완료");
            } else {
                Log.d("test", "권한 설정 요청");
                ActivityCompat.requestPermissions(WalkStart.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.FOREGROUND_SERVICE}, 1);
            }
        }

        boolean permissionAccessCoarseLocationApproved =
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;

        if (permissionAccessCoarseLocationApproved) {
            boolean backgroundLocationPermissionApproved =
                    ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                            == PackageManager.PERMISSION_GRANTED;

            if (backgroundLocationPermissionApproved) {
                // App can access location both in the foreground and in the background.
                // Start your service that doesn't have a foreground service type
                // defined.
            } else {
                // App can only access location in the foreground. Display a dialog
                // warning the user that your app must have all-the-time access to
                // location in order to function properly. Then, request background
                // location.
                ActivityCompat.requestPermissions(this, new String[]{
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                        2);
            }
        } else {
            // App doesn't have access to the device's location at all. Make full request
            // for permission.
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    },
                    2);
        }

        super.onStart();
    }

    @Override
    protected void onPostResume() {
        walkStartMapView.onStart();
        super.onPostResume();
    }

    @Override
    protected void onPause() {
        walkStartMapView.onStart();
        super.onPause();
    }

    @Override
    protected void onStop() {
        walkStartMapView.onStart();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        walkStartMapView.onStart();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        walkStartMapView.onStart();
        super.onLowMemory();
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

    public void setLine(LatLng latLng) {

        locations.add(new location(latLng.latitude, latLng.longitude));

        polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.BLUE);
        polylineOptions.width(5);
        arrayPoints.add(latLng);
        polylineOptions.addAll(arrayPoints);
        mMap.addPolyline(polylineOptions);
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
        return address.getAddressLine(0).toString() + "\n";
    }

    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(WalkStart.this);
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

}