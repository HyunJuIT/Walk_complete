package com.example.walk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    TextView ment, today, txt_location, txt_weather, txt_temp;
    ImageView weatherImg;
    Button btn_goWalkStart;
    BottomNavigationView navigationView;
    static String name;
    static String email;
    static Uri photoUrl;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    GpsTracker gpsTracker;
    static Activity activity;
    static Handler handler;
    Thread thread;
    String result = null;
    String weather, temp,weatherCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = MainActivity.this;

        pref = getApplicationContext().getSharedPreferences("mine", MODE_PRIVATE);
        editor = pref.edit();

        btn_goWalkStart = (Button) findViewById(R.id.goWalkStart);
        ment = (TextView) findViewById(R.id.txt_mainMent);
        today = (TextView) findViewById(R.id.main_today);
        txt_location = (TextView) findViewById(R.id.txt_Location);

        gpsTracker = new GpsTracker(MainActivity.this);
        double latitude = gpsTracker.getLatitude();// 위도
        double longitude = gpsTracker.getLongitude(); //경도
        weather(latitude, longitude);
        // 필요시
        String address = getCurrentAddress(latitude, longitude); //대한민국 서울시 종로구 ~~

        txt_location.setText(address);
        txt_location.setGravity(Gravity.CENTER);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        txt_weather = (TextView) findViewById(R.id.txt_weather);
        txt_temp = (TextView) findViewById(R.id.txt_temp);
        weatherImg = (ImageView) findViewById(R.id.img_weather_img);

        if (user != null) {
            name = user.getDisplayName();
            email = user.getEmail();
            photoUrl = user.getPhotoUrl();

            Log.d("시험해보자", name + "/" + email + "/" + photoUrl);

            if (name == null || photoUrl == null || pref.getString("nickName", null) == null) {
                Intent intent = new Intent(MainActivity.this, Profile.class);
                startActivity(intent);
                finish();
            } else {

                if(name == null){
                    name = pref.getString("nickName", null);
                }

                ment.setText(name + "님 오늘 산책 어떠신가요?");
                if (thread == null) {
                    thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            firebaseInformationFeed();
                            firebaseInformationMemo();
                            firebaseInformationMypage();
                            firebaseInformationMyRoute();
                        }
                    });

                    thread.start();
                }
            }
            // User is signed in
        } else {
            // No user is signed in
        }

        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy년 MM월 dd일 a hh:mm");
        today.setText(simpleDate.format(new Date()));

        navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_journal:
                        startActivity(new Intent(getApplicationContext(), Journal.class));
                        return true;
                    case R.id.action_route:
                        startActivity(new Intent(getApplicationContext(), WalkRoute.class));
                        return true;
                    case R.id.action_home:
                        return true;
                    case R.id.action_feed:
                        startActivity(new Intent(getApplicationContext(), Feed.class));
                        return true;
                    case R.id.action_person:
                        startActivity(new Intent(getApplicationContext(), Mypage.class));
                        return true;
                }
                return false;
            }
        });

        navigationView.setSelectedItemId(R.id.action_home);

        btn_goWalkStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), WalkStart.class);
                startActivity(intent);
            }
        });

        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case 1:
                        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy년 MM월 dd일 a hh:mm");
                        today.setText(simpleDate.format(new Date()));

                        sendEmptyMessageDelayed(1, 30000);
                        break;
                    case 2:
                        firebaseInformationFeed();
                        break;
                    case 3:
                        firebaseInformationMemo();
                        break;
                    case 4:
                        firebaseInformationMypage();
                        break;
                    case 5:
                        txt_temp.setText(temp);
                        txt_weather.setText(weather);
                        sendImageRequest();
                        break;
                    case 6:
                        firebaseInformationMyRoute();
                        break;
                }
            }
        };

        handler.sendEmptyMessage(1);
    }

    @Override
    protected void onResume() {
        if (User.getNickName() != null) {
            ment.setText(User.getNickName() + "님 오늘 산책 어떠신가요?");
        }
        handler.sendEmptyMessage(1);
        navigationView.setSelectedItemId(R.id.action_home);
        super.onResume();
    }

    public void sendImageRequest() {
        String url = "http://openweathermap.org/img/wn/"+weatherCode+"@2x.png";

        ImageLoadTask task = new ImageLoadTask(url,weatherImg);
        task.execute();
    }

    ///////////////////////////////GPS
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
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GPS_ENABLE_REQUEST_CODE:
                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }
                break;
        }
    }

    @Override
    protected void onStart() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            name = user.getDisplayName();
            email = user.getEmail();
            photoUrl = user.getPhotoUrl();

            Log.d("시험해보자", name + "/" + email + "/" + photoUrl);

            if (name == null || photoUrl == null) {
                Intent intent = new Intent(MainActivity.this, Profile.class);
                startActivity(intent);
                finish();
            } else {
                ment.setText(name + "님 오늘 산책 어떠신가요?");
            }
            // User is signed in
        } else {
            // No user is signed in
        }
        super.onStart();
    }

    void checkRunTimePermission() {

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            // 3.  위치 값을 가져올 수 있음


        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(MainActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void firebaseInformationFeed() {

        try {
            //피드 데이터베이스 참조
            FirebaseDatabase.getInstance().getReference("feed").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Feed.list.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Log.d("MainActivity", "Single ValueEventListener : " + snapshot.getValue());
                        Feed.list.add(snapshot.getValue(FeedVO.class));
                    }
                    /*  Feed.handler.sendEmptyMessage(1);*/
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (NullPointerException e) {

        }


    }

    public void firebaseInformationMemo() {

        try {
            //레코드 데이터베이스 참조
            SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");
            today.setText(simpleDate.format(new Date()));

            FirebaseDatabase.getInstance().getReference("memo").child(MainActivity.name).child(simpleDate.format(new Date())).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    WalkRecord.list.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Log.d("MainActivity", "Single ValueEventListener : " + snapshot.getValue());
                        WalkRecord.list.add(snapshot.getValue(MemoVO.class));
                    }
                    /*  WalkRecord.handler.sendEmptyMessage(1);*/
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } catch (NullPointerException e) {

        }
    }

    public void firebaseInformationMyRoute(){
        try {

            //마이페이지 정보
            FirebaseDatabase.getInstance().getReference("route").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    MyRoute.list.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Log.d("MainActivity", "Single ValueEventListener : " + snapshot.getValue());
                        MyRouteVO myRouteVO = snapshot.getValue(MyRouteVO.class);
                        MyRoute.list.add(myRouteVO);

                    }
                    /*    Mypage.handler.sendEmptyMessage(1);*/
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (NullPointerException e) {

        }
    }

    public void firebaseInformationMypage() {

        try {

            //마이페이지 정보
            FirebaseDatabase.getInstance().getReference("feed").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    MyFeed.list.clear();
                    MyFavoriteFeed.list.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Log.d("MainActivity", "Single ValueEventListener : " + snapshot.getValue());
                        FeedVO feedVO = snapshot.getValue(FeedVO.class);

                        if (feedVO.getID().equals(MainActivity.email)) {
                            MyFeed.list.add(feedVO);
                        }

                        if (feedVO.getFavorite_main().contains(MainActivity.email)) {
                            MyFavoriteFeed.list.add(feedVO);
                        }
                    }
                    /*    Mypage.handler.sendEmptyMessage(1);*/
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (NullPointerException e) {

        }

    }

    public void weather(double lat, double lon) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url;
                String Url_path = "https://api.openweathermap.org/data/2.5/onecall?lat=" + lat + "&lon=" + lon + "&units=metric&lang=Kr&exclude=minutely,hourly,daily&appid=0162456c3e8383a0c8742abe7061db78";
/*
                String Url_path = "http://api.openweathermap.org/data/2.5/weather?lat="+lat+"&lon="+lon+"&APPID=0162456c3e8383a0c8742abe7061db78";
*/
                HttpURLConnection conn = null;

                try {
                    url = new URL(Url_path);

                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Cache-Control", "no-cache");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    int responseCode = conn.getResponseCode();
                    if (responseCode != 200)
                        throw new IOException("Post failed with error code" + responseCode);

                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuilder builder = new StringBuilder();

                    String str;
                    while ((str = reader.readLine()) != null) {
                        builder.append(str);

                    }

                    result = builder.toString();
                    Log.d("날씨정보", result);


                    JSONObject jsonObject = new JSONObject(result);
                    JSONObject object = new JSONObject(jsonObject.get("current").toString());
                    temp = object.get("temp").toString();
                    Log.d("날씨정보", object.get("temp").toString());

                    JSONArray object1 = (JSONArray) object.get("weather");
                    JSONObject object2 = (JSONObject) object1.get(0);
                    weather = object2.get("description").toString();

                    weatherCode = object2.get("icon").toString();

                    handler.sendEmptyMessage(5);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            }
        }).start();

    }

    public void dust() {

    }
}