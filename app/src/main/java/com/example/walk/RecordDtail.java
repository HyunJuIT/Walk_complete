package com.example.walk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RecordDtail extends AppCompatActivity {

    androidx.recyclerview.widget.RecyclerView recyclerView;
    TextView date, time, adress, title, memo;
    int index;
    MemoVO memoVO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_dtail);

        ActionBar actionBar = getSupportActionBar();  //제목줄 객체 얻어오기
        actionBar.setTitle("");  //액션바 제목설정
        actionBar.setDisplayHomeAsUpEnabled(true);   //뒤로가는 버튼 <- 만들기

        Intent intent = getIntent();

        if (intent != null) {
            index = intent.getIntExtra("memo", 0);
            memoVO = WalkRecord.list.get(index);
        }

        date = (TextView) findViewById(R.id.dateTime_recordDtail);
        time = (TextView) findViewById(R.id.time_recordDtail);
        adress = (TextView) findViewById(R.id.adress_recordDtail);
        title = (TextView) findViewById(R.id.title_recordDtail);
        memo = (TextView) findViewById(R.id.memo_recordDtail);

        recyclerView = (androidx.recyclerview.widget.RecyclerView) findViewById(R.id.recycler_record_dtail);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(manager);

        if (memoVO.getImgs() != null) {
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
        getMenuInflater().inflate(R.menu.postmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            //수정 아이콘을 누를 시
            case R.id.btn_modified:
                Intent intent = new Intent(RecordDtail.this, RecordDtailModified.class);
                intent.putExtra("memo", index);
                startActivity(intent);
                finish();
                return true;
            case R.id.btn_delete:

                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("메모 삭제");
                alert.setMessage("메모를 정말로 삭제하시겠습니까?");
                alert.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String namedateNAme = WalkRecord.list.get(index).getDate();
                        WalkRecord.list.remove(index);

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("memo").child(MainActivity.name).child(namedateNAme);
                        myRef.setValue(WalkRecord.list);

                        finish();
                        dialog.cancel();
                    }
                });
                alert.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alert.show();

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