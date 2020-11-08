package com.example.walk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Profile extends AppCompatActivity {

    ImageView img;
    Button btn_prifile,btn_nickName;
    EditText  editText;
    byte[] imgs;
    final int CALL_GALLERY = 1;
    Uri profileUri;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        editText = (EditText) findViewById(R.id.edt_nickname);
        img = (ImageView) findViewById(R.id.ProfileImg);
        btn_prifile = (Button) findViewById(R.id.btn_profile) ;
        btn_nickName = (Button) findViewById(R.id.button4_profile);

        if(MainActivity.photoUrl!=null){
            try {
                InputStream in = getContentResolver().openInputStream(MainActivity.photoUrl);
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                bitmap = ThumbnailUtils.extractThumbnail(bitmap, 300, 300);
                img.setImageBitmap(bitmap);
                img.setBackground(new ShapeDrawable(new OvalShape()));
                img.setClipToOutline(true);

            }catch (IOException e){

            }

            editText.setText(MainActivity.name);
        }

        btn_nickName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Profile.this,"닉네임을 사용할 수 있습니다.",Toast.LENGTH_SHORT).show();
            }
        });

        btn_prifile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(editText.getText().toString())
                        .setPhotoUri(Uri.parse(String.valueOf(profileUri)))
                        .build();

                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("TAG", "User profile updated.");

                                    pref = getApplicationContext().getSharedPreferences("mine", MODE_PRIVATE);
                                    editor = pref.edit();

                                    editor.putString("nickName",editText.getText().toString());
                                    editor.commit();

                                    Intent intent = new Intent(Profile.this,MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });

   /*             if (user != null) {
                    MainActivity.name = user.getDisplayName();
                    MainActivity.email = user.getEmail();
                    MainActivity. photoUrl = user.getPhotoUrl();
                } else {

                }*/


            }
        });

       img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);   //CallLog와 달리 이미 지정된 상숫값을 불러온다.
                startActivityForResult(intent, CALL_GALLERY);

            }
        });

    }

    @Override
    protected void onResume() {

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Profile.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Profile.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
        }

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_SETTINGS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Profile.this, new String[]{Manifest.permission.WRITE_SETTINGS}, 2);
        }

        super.onResume();
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


    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CALL_GALLERY){
            if(resultCode==RESULT_OK){
                if(data != null){

                    img.setImageURI(data.getData());
                    profileUri = data.getData();
                    img.setBackground(new ShapeDrawable(new OvalShape()));
                    img.setClipToOutline(true);

                    try {

                        InputStream is = getContentResolver().openInputStream(data.getData());
                        imgs = getBytes(is);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
            }
        }
    }
}