package com.example.walk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FeedWriting extends AppCompatActivity {

    private EditText feed;
    private ImageView camera, album;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private androidx.recyclerview.widget.RecyclerView recyclerView;
    private ArrayList<byte[]> imgs = new ArrayList<>();
    private ArrayList<String> imgUri = new ArrayList<>();
    private final int PICTURE_REQUEST_CODE = 2;
    private int index = -1;
    StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("feed"); //파이어베이스 스토리지

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_writing);
        feed = (EditText) findViewById(R.id.edit_feed);
        camera = (ImageView) findViewById(R.id.camera_feed);
        album = (ImageView) findViewById(R.id.album_feed);


        index = getIntent().getIntExtra("feededit", -1);

        if (index != -1) {
            FeedVO feedVO = Feed.list.get(index);

            feed.setText(feedVO.getFeed());
           /* imgs = feedVO.getImgs();*/
        }

        recyclerView = (androidx.recyclerview.widget.RecyclerView) findViewById(R.id.recycler_feed_writing);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(manager);

        ImageAdapter adapter = new ImageAdapter(imgs,null, false);
        recyclerView.setAdapter(adapter);


        pref = getApplicationContext().getSharedPreferences("mine", MODE_PRIVATE);
        editor = pref.edit();

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

                    ImageAdapter adapter = new ImageAdapter(imgs,null, false);
                    recyclerView.setAdapter(adapter);

                } else if (uri != null) {
                    //이미지 뷰의 이미지 설정

                    try {
                        InputStream is = getContentResolver().openInputStream(data.getData());
                        imgs.add(getBytes(is));

                        ImageAdapter adapter = new ImageAdapter(imgs,null, false);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feedwritingmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_post:
                if (index != -1) {
                    Feed.list.get(index).setFeed(feed.getText().toString());

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("feed");
                    myRef.setValue(Feed.list);

                    Feed.reset();
                  /*  Feed.list.get(index).setImgs(imgs);*/
                    finish();
                } else {
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
                                    imgUri.add("feed/"+name);
                                    Log.d("테스트", String.valueOf(imgUri.size())+"개");

                                    if (imgUri.size() == imgs.size()) {

                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        DatabaseReference myRef = database.getReference("feed");

                                        Feed.list.add(0,new FeedVO(MainActivity.email,String.valueOf(MainActivity.photoUrl), MainActivity.name, feedDate, feed.getText().toString(), imgUri));
                                        myRef.setValue(Feed.list);
                                        finish();

                                    }
                                }
                            });
                        }
                    } else {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("feed");

                        FeedVO feedVO = new FeedVO(MainActivity.email,String.valueOf(MainActivity.photoUrl), MainActivity.name, feedDate, feed.getText().toString(), null);

                        Feed.list.add(0,feedVO);
                        myRef.setValue(Feed.list);

                        finish();
                    }

                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
       /* if (feed.getText().toString().length() > 0) {
            Toast.makeText(getApplicationContext(), "임시저장이 되었습니다.", Toast.LENGTH_LONG).show();
            editor.putString("feed", feed.getText().toString());
            editor.commit();
        }*/
        super.onPause();
    }

    @Override
    protected void onStop() {
     /*   if (feed.getText().toString().length() > 0) {
            Toast.makeText(getApplicationContext(), "임시저장이 되었습니다.", Toast.LENGTH_LONG).show();
            editor.putString("feed", feed.getText().toString());
            editor.commit();
        }*/
        super.onStop();
    }

  /*  @Override
    protected void onResume() {
        if (pref.getString("feed", null) != null) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("임시저장");
            alert.setMessage("전에 작업하던 글이 있습니다. 이어서 작업할까요?");
            alert.setPositiveButton("네", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    feed.setText(pref.getString("feed", null));
                    dialog.cancel();
                }
            });
            alert.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    editor.remove("feed");
                    dialog.cancel();
                }
            });
            alert.show();

        }
        super.onResume();
    }*/
}