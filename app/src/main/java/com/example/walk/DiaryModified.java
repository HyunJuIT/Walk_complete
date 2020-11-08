package com.example.walk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class DiaryModified extends AppCompatActivity {

    Intent getIntent;
    EditText title;
    EditText diary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_modified);

        ActionBar actionBar = getSupportActionBar();  //제목줄 객체 얻어오기
        actionBar.setTitle("");  //액션바 제목설정
        actionBar.setDisplayHomeAsUpEnabled(true);   //뒤로가는 버튼 <- 만들기

        title = (EditText) findViewById(R.id.edt_diary_title);
        diary = (EditText) findViewById(R.id.edt_diary);

        getIntent = getIntent();

        title.setText(getIntent.getExtras().getString("title"));
        diary.setText(getIntent.getExtras().getString("diary"));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post_modified_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            //포스트 추가 아이콘을 누를 시
            case R.id.btn_modified_ok:
                Intent intent = new Intent(DiaryModified.this,DiaryDtail.class);
                intent.putExtra("title",title.getText().toString());
                intent.putExtra("diary",diary.getText().toString());
                startActivity(intent);
                finish();
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