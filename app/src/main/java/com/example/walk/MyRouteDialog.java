package com.example.walk;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MyRouteDialog extends Activity implements OnMapReadyCallback {

    androidx.recyclerview.widget.RecyclerView recyclerView;
    TextView btn_cancle, btn_save, btn_share, btn_img_plus, km;
    EditText title, introduce;
    RatingBar ratingBar;
    WalkInformation information;
    Marker currentMarker;
    String adress;
    String dates;
    boolean share;
    GoogleMap mMap;
    MapView mapView;
    private ArrayList<byte[]> imgs = new ArrayList<>();
    private ArrayList<String> imgUri = new ArrayList<>();
    private final int PICTURE_REQUEST_CODE = 2;
    StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("route"); //파이어베이스 스토리지
    private PolylineOptions polylineOptions;
    private ArrayList<LatLng> arrayPoints = new ArrayList<>();
    static ArrayList<location> locations = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_route_dialog);

        btn_cancle = (TextView) findViewById(R.id.btn_cancle_routeDialog);
        btn_save = (TextView) findViewById(R.id.btn_sava_routeDialog);
        btn_img_plus = (TextView) findViewById(R.id.btn_img_plus_route_dialog);
        btn_share = (TextView) findViewById(R.id.btn_share_my_route);
        title = (EditText) findViewById(R.id.edit_route_name);
        introduce = (EditText) findViewById(R.id.edit_route_introduce);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar_route_dialog);
        km = (TextView) findViewById(R.id.txt_km_my_route);

        mapView = (MapView) findViewById(R.id.mapView_my_route_dialog);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        recyclerView = (androidx.recyclerview.widget.RecyclerView) findViewById(R.id.recycle_my_route_dialog);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(manager);

        Intent intent = getIntent();

        if (intent != null) {
            information = WalkStart.information;

            km.setText(String.format("%.2f", (information.getKm())));
            adress = intent.getStringExtra("myrouteadress");
            dates = intent.getStringExtra("myroutedate");
        }

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
            }
        });

        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
                String feedDate = simpleDate.format(new Date());

                if (imgs.size() > 0) {
                    for (int i = 0; i < imgs.size(); i++) {
                        SimpleDateFormat date = new SimpleDateFormat("yyyyMMddHHmmss");
                        String namedate = date.format(new Date());

                        String name = "img" + namedate + i;
                        StorageReference mProfileRef = mStorageRef.child(name); //프로필 스토리지 저장이름은 사용자 고유토큰과 스트링섞어서 만들었다.
                        UploadTask uploadTask = mProfileRef.putBytes(imgs.get(i));
                        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                System.out.println("Upload is " + progress + "% done");
                            }
                        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                                System.out.println("Upload is paused");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                                // ...
                                //다운로드 Uri 반환
                                imgUri.add("route/" + name);
                                Log.d("테스트", String.valueOf(imgUri.size()) + "개");

                                if (imgUri.size() == imgs.size()) {

                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference myRef = database.getReference("route");

                                    MyRouteVO myRouteVO = new MyRouteVO(MainActivity.email, MainActivity.name, dates, title.getText().toString(), introduce.getText().toString(), adress, ratingBar.getRating(), WalkStart.locations, imgUri, information, share);
                                    WalkStart.locations = new ArrayList<>();
                                    MyRoute.list.add(myRouteVO);
                                    myRef.setValue(MyRoute.list);

                                    finish();
                                }
                            }
                        });
                    }
                } else {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("route");

                    MyRouteVO myRouteVO = new MyRouteVO(MainActivity.email, MainActivity.name, dates, title.getText().toString(), introduce.getText().toString(), adress, ratingBar.getRating(), WalkStart.locations, imgUri, information, share);
                    WalkStart.locations = new ArrayList<>();
                    MyRoute.list.add(myRouteVO);
                    myRef.setValue(MyRoute.list);

                    finish();
                }
            }
        });

        btn_img_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("image/*");
                /*       intent.setType(MediaStore.Images.Media.CONTENT_TYPE);*/
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICTURE_REQUEST_CODE);
            }
        });

        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //PopupMenu객체 생성.

                PopupMenu popup = new PopupMenu(MyRouteDialog.this, v);//view는 오래 눌러진 뷰를 의미

                getMenuInflater().inflate(R.menu.sharemenu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.share:
                                ((TextView) v).setText("공개");
                                share = true;
                                break;
                            case R.id.noshare:
                                ((TextView) v).setText("비공개");
                                share = false;
                                break;
                        }

                        return false;
                    }
                });
                popup.show();//Popup Menu 보이기
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        double latitude = WalkStart.information.getLatLng().getLatitude();// 위도
        double longitude = WalkStart.information.getLatLng().getLongitude();  //경도

        LatLng latLng = new LatLng(latitude, longitude);

        setCurrentLocation(latLng,"","");
        setArrayPoint();
        Line();

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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICTURE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //이미지가 1개일때
                Uri uri = data.getData();
                //여러장일때 저장
                ClipData clipData = data.getClipData();

                //이미지를 여러장 가져왔을때 URI들을 하나씩 꺼내서 적용시키는 코드
                if (clipData != null) {
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        Uri urione = clipData.getItemAt(i).getUri();
                        try {
                            InputStream is = getContentResolver().openInputStream(urione);
                            imgs.add(getBytes(is));

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    ImageAdapter adapter = new ImageAdapter(imgs, null, false);
                    recyclerView.setAdapter(adapter);

                } else if (uri != null) {
                    //이미지 뷰의 이미지 설정

                    try {
                        InputStream is = getContentResolver().openInputStream(data.getData());
                        imgs.add(getBytes(is));

                        ImageAdapter adapter = new ImageAdapter(imgs, null, false);
                        recyclerView.setAdapter(adapter);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    // 이미지를 바이트 배열로 변환
    public byte[] getBytes(InputStream is) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024; // 버퍼 크기
        byte[] buffer = new byte[bufferSize]; // 버퍼 배열

        int len = 0;

        // InputStream에서 읽어올 게 없을 때까지 바이트 배열에 쓴다.
        while ((len = is.read(buffer)) != -1)
            byteBuffer.write(buffer, 0, len);

        return byteBuffer.toByteArray();
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