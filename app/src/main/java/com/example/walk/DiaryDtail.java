package com.example.walk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DiaryDtail extends AppCompatActivity {

    Intent getIntent;
    TextView title;
    TextView diary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_dtail);

        ActionBar actionBar = getSupportActionBar();  //제목줄 객체 얻어오기
        actionBar.setTitle("");  //액션바 제목설정
        actionBar.setDisplayHomeAsUpEnabled(true);   //뒤로가는 버튼 <- 만들기

        title = (TextView) findViewById(R.id.txt_diary_detail_title);
        diary = (TextView) findViewById(R.id.txt_diary_detail);

        getIntent = getIntent();

        title.setText(getIntent.getExtras().getString("title"));
        diary.setText(getIntent.getExtras().getString("diary"));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.postmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            //수정 아이콘을 누를 시
            case R.id.btn_modified:
                Intent intent = new Intent(getApplicationContext(), DiaryModified.class);
                intent.putExtra("title",title.getText().toString());
                intent.putExtra("diary",diary.getText().toString());
                startActivity(intent);
                finish();
                return true;
            case R.id.btn_delete:
                return true;
            //뒤로가기 버큰을 누를 시
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}