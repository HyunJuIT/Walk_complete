package com.example.walk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RecordDtailModified extends AppCompatActivity {

    androidx.recyclerview.widget.RecyclerView recyclerView;
    TextView date, time, adress;
    EditText title, memo;
    int index;
    MemoVO memoVO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_dtail_modified);

        ActionBar actionBar = getSupportActionBar();  //제목줄 객체 얻어오기
        actionBar.setTitle("");  //액션바 제목설정
        actionBar.setDisplayHomeAsUpEnabled(true);   //뒤로가는 버튼 <- 만들기

        Intent intent = getIntent();

        if (intent != null) {
            index = intent.getIntExtra("memo", 0);
            memoVO = WalkRecord.list.get(index);
        }

        date = (TextView) findViewById(R.id.dateTime_recordDtail2);
        time = (TextView) findViewById(R.id.time_recordDtail2);
        adress = (TextView) findViewById(R.id.adress_recordDtail2);
        title = (EditText) findViewById(R.id.edit_title_dtailModified);
        memo = (EditText) findViewById(R.id.edit_post_dtailModified);

        recyclerView = (androidx.recyclerview.widget.RecyclerView) findViewById(R.id.recycler_record_dtail_modified);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(manager);

        if(memoVO.getImgs()!=null) {
            ImageAdapter adapter = new ImageAdapter(null, memoVO.getImgs(), true);
            recyclerView.setAdapter(adapter);
        }

        if (memoVO != null) {
            date.setText(WalkRecord.list.get(index).getDate());
            time.setText(WalkRecord.list.get(index).getTime());
            adress.setText(WalkRecord.list.get(index).getAdress());
            title.setText(WalkRecord.list.get(index).getTitle());
            memo.setText(WalkRecord.list.get(index).getMemo());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post_modified_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_modified_ok:

                WalkRecord.list.get(index).setTitle(title.getText().toString());
                WalkRecord.list.get(index).setMemo(memo.getText().toString());

                String namedateNAme = WalkRecord.list.get(index).getDate();

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("memo").child(MainActivity.name).child(namedateNAme);
                myRef.setValue(WalkRecord.list);

                Intent intent = new Intent(RecordDtailModified.this, RecordDtail.class);
                intent.putExtra("memo", index);
                startActivity(intent);
                finish();
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}