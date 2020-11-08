package com.example.walk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;

public class Mypage extends AppCompatActivity {

    BottomNavigationView navigationView;
    ImageView profile;
    TextView nickName,txt_myFeedCount,txt_myFavoriteFeedCount;
    LinearLayout myFeed, myFavoriteFeed, myProfileEdit, myPassword, myLogout, withdrawal;
    static Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        profile = (ImageView) findViewById(R.id.profile_mypage);
        nickName = (TextView) findViewById(R.id.txt_nickName_mypage);
        txt_myFeedCount = (TextView) findViewById(R.id.txt_myFeed_count);
        txt_myFavoriteFeedCount = (TextView) findViewById(R.id.txt_myFavoriteFeed_count);
        myFeed = (LinearLayout) findViewById(R.id.myFeed_mypage);
        myFavoriteFeed = (LinearLayout) findViewById(R.id.myFavoriteFeed_mypage);
        myProfileEdit = (LinearLayout) findViewById(R.id.profile_edit_mypage);
        myPassword = (LinearLayout) findViewById(R.id.password_edit_mypage);
        myLogout = (LinearLayout) findViewById(R.id.logout_mypage);
        withdrawal = (LinearLayout) findViewById(R.id.withdrawal_mypage);

   /*     FirebaseDatabase.getInstance().getReference("feed").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MyFeed.list.clear();
                MyFavoriteFeed.list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d("MainActivity", "Single ValueEventListener : " + snapshot.getValue());
                    FeedVO feedVO = snapshot.getValue(FeedVO.class);

                    if(feedVO.getID().equals(MainActivity.email)){
                        MyFeed.list.add(feedVO);
                    }

                    if(feedVO.getFavorite_main().contains(MainActivity.email)){
                        MyFavoriteFeed.list.add(feedVO);
                    }
                }
                reset();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

        txt_myFeedCount.setText(MyFeed.list.size()+"개");
        txt_myFavoriteFeedCount.setText(MyFavoriteFeed.list.size()+"개");

        //프로필 이미지 설정
        try {
            InputStream in = getContentResolver().openInputStream(MainActivity.photoUrl);
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, 300, 300);
            profile.setImageBitmap(bitmap);
            profile.setBackground(new ShapeDrawable(new OvalShape()));
            profile.setClipToOutline(true);

        }catch (IOException e){

        }

        nickName.setText(MainActivity.name);

        myFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Mypage.this,MyFeed.class);
                startActivity(intent);
                finish();
            }
        });

        myFavoriteFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Mypage.this,MyFavoriteFeed.class);
                startActivity(intent);
                finish();
            }
        });

        myProfileEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Mypage.this,Profile.class);
                startActivity(intent);
                finish();
            }
        });

        myPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Mypage.this,PasswordEdit.class);
                startActivity(intent);
                finish();
            }
        });

        myLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alert = new AlertDialog.Builder(Mypage.this);
                alert.setTitle("로그아웃");
                alert.setMessage("정말로 로그아웃 할까요?");
                alert.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();

                        SharedPreferences pref = getApplicationContext().getSharedPreferences("mine", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.remove("id");
                        editor.remove("pw");
                        editor.commit();

                        Toast.makeText(getApplicationContext(),"성공적으로 로그아웃이 되었습니다.",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(Mypage.this,Login.class);
                        startActivity(intent);
                        MainActivity.activity.finish();
                    }
                });
                alert.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alert.show();


            }
        });

        withdrawal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(Mypage.this);
                alert.setTitle("회원탈퇴");
                alert.setMessage("정말로 회원탈퇴를 진행할까요?");
                alert.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        user.delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(),"성공적으로 회원탈퇴가 되었습니다.",Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(Mypage.this,Login.class);
                                            startActivity(intent);
                                            MainActivity.activity.finish();
                                        }
                                    }
                                });
                    }
                });
                alert.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alert.show();
            }
        });

        //네비게이션 바 설정
        navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_journal:
                        startActivity(new Intent(getApplicationContext(), Journal.class));
                        finish();
                        return true;
                    case R.id.action_route:
                        startActivity(new Intent(getApplicationContext(), WalkRoute.class));
                        return true;
                    case R.id.action_home:
                        finish();
                        return true;
                    case R.id.action_feed:
                        startActivity(new Intent(getApplicationContext(), Feed.class));
                        finish();
                        return true;
                    case R.id.action_person:
                        return true;
                }
                return false;
            }
        });

        navigationView.setSelectedItemId(R.id.action_person);

        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case 1:
                        reset();
                        break;
                }
            }
        };
    }

    @Override
    protected void onResume() {
        navigationView.setSelectedItemId(R.id.action_person);
        super.onResume();
    }

    public void reset(){
        txt_myFeedCount.setText(MyFeed.list.size()+"개");
        txt_myFavoriteFeedCount.setText(MyFavoriteFeed.list.size()+"개");
    }

}