package com.example.walk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.List;
import java.util.Locale;

public class MemoDialog extends Activity {

    private TextView tvNegative, tvPositive;
    private EditText title, post;
    private TextView date, time, adress;
    private ImageView camera, album;
    static androidx.recyclerview.widget.RecyclerView recyclerView;
    private ArrayList<byte[]> imgs = new ArrayList<>();
    private ArrayList<String> imgUri = new ArrayList<String>();
    private final int PICTURE_REQUEST_CODE = 2;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("memo/" + MainActivity.name + "/"); //파이어베이스 스토리지
    private String adressText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_dialog);

        //layout의 구성요소들 연결하기
        tvPositive = (TextView) findViewById(R.id.btn_sava_postDialog);
        tvNegative = (TextView) findViewById(R.id.option_codetype_dialog_negative);
        title = (EditText) findViewById(R.id.edit_title_postDialog);
        post = (EditText) findViewById(R.id.edit_postDialog);
        date = (TextView) findViewById(R.id.dateTime_postDialog);
        time = (TextView) findViewById(R.id.time_postDialog);
        adress = (TextView) findViewById(R.id.adress_postDialog);
        camera = (ImageView) findViewById(R.id.camera_post);
        album = (ImageView) findViewById(R.id.album_post);

        //리사이클러뷰
        recyclerView = (androidx.recyclerview.widget.RecyclerView) findViewById(R.id.recycler_img_post);
        //리사이클러뷰 가로 설정하기
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(manager);

        ImageAdapter adapter = new ImageAdapter(imgs, null, false);
        recyclerView.setAdapter(adapter);

        //날짜 데이터 받아오는 코드
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");
        date.setText(simpleDate.format(new Date()));

        SimpleDateFormat simpleTime = new SimpleDateFormat("a hh:mm");
      /*  SimpleDateFormat simpleA = new SimpleDateFormat("a", Locale.US);*/
        time.setText(simpleTime.format(new Date()));


        adressText = getCurrentAddress(WalkStart.gpsTracker.getLatitude(), WalkStart.gpsTracker.getLongitude());

        String[] adressSetText = adressText.split(" ");
        adress.setText(adressSetText[1] + " " + adressSetText[2] + " " + adressSetText[3]);

        //앨범에서 사진 가져오는 버튼
        album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("image/*");
                /*       intent.setType(MediaStore.Images.Media.CONTENT_TYPE);*/
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICTURE_REQUEST_CODE);
            }
        });

        tvPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (imgs.size() > 0) {
                    for (int i = 0; i < imgs.size(); i++) {
                        SimpleDateFormat datess = new SimpleDateFormat("yyyyMMddHHmmss");
                        String namedate = datess.format(new Date());

                        SimpleDateFormat datefileName = new SimpleDateFormat("yyyy-MM-dd");
                        String namedateNAme = datefileName.format(new Date());

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
                                imgUri.add("memo/" + MainActivity.name + "/" + name);
                                Log.d("테스트", String.valueOf(imgUri.size()) + "개");

                                if (imgUri.size() == imgs.size()) {

                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference myRef = database.getReference("memo").child(MainActivity.name).child(namedateNAme);
                                    DatabaseReference myRefAlbum = database.getReference("album").child(MainActivity.name);

                                    WalkRecord.list.add(new MemoVO(false, title.getText().toString(), post.getText().toString(), date.getText().toString(), time.getText().toString(), adressText, WalkStart.gpsTracker.getLatitude(), WalkStart.gpsTracker.getLongitude(), imgUri));
                                    WalkAlbum.img.addAll(imgUri);

                                    myRef.setValue(WalkRecord.list);
                                    myRefAlbum.setValue(WalkAlbum.img);
                                    finish();

                                }
                            }
                        });
                    }
                } else {

                    SimpleDateFormat datefileName = new SimpleDateFormat("yyyy-MM-dd");
                    String namedateNAme = datefileName.format(new Date());

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("memo").child(MainActivity.name).child(namedateNAme);
                    DatabaseReference myRefAlbum = database.getReference("album").child(MainActivity.name);

                    WalkRecord.list.add(new MemoVO(false, title.getText().toString(), post.getText().toString(), date.getText().toString(), time.getText().toString(), adressText, WalkStart.gpsTracker.getLatitude(), WalkStart.gpsTracker.getLongitude(), imgUri));
                    WalkAlbum.img.addAll(imgUri);

                    myRef.setValue(WalkRecord.list);
                    myRefAlbum.setValue(WalkAlbum.img);
                    finish();

                }
            }
        });


        tvNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
                            byte[] img_byte = getBytes(is);
                            imgs.add(img_byte);
/*
                            String byteToString_img = Base64.encodeToString(img_byte,Base64.DEFAULT);*/

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
                        byte[] img_byte = getBytes(is);
                        imgs.add(img_byte);

                        /*    String byteToString_img = Base64.encodeToString(img_byte,Base64.DEFAULT);
                         */
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
        AlertDialog.Builder builder = new AlertDialog.Builder(MemoDialog.this);
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