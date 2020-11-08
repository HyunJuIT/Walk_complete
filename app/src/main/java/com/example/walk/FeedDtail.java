package com.example.walk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FeedDtail extends AppCompatActivity {

    private ImageView profile;
    private TextView nickName, time, feed, total, comment;
    private EditText editComment;
    private ImageButton send;
    private FeedVO feedVO;
    private static androidx.recyclerview.widget.RecyclerView recyclerView_comment;
    private static androidx.recyclerview.widget.RecyclerView recyclerView_img;
    public static int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_dtail);

        Intent intent = getIntent();

        if (intent != null) {
            index = intent.getIntExtra("comment", 0);
            feedVO = Feed.list.get(index);
        }

        profile = (ImageView) findViewById(R.id.profile_img_feed);
        nickName = (TextView) findViewById(R.id.nickName_feed);
        time = (TextView) findViewById(R.id.time_feed);
        feed = (TextView) findViewById(R.id.text_feed_item);
        total = (TextView) findViewById(R.id.feed_total);
        comment = (TextView) findViewById(R.id.txt_commet_feedDtail);
        editComment = (EditText) findViewById(R.id.edit_commet);
        send = (ImageButton) findViewById(R.id.btn_send_comment);


        if (feedVO != null) {
            try {
                InputStream in = getContentResolver().openInputStream(MainActivity.photoUrl);
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                bitmap = ThumbnailUtils.extractThumbnail(bitmap, 300, 300);
                profile.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

            nickName.setText(feedVO.getNickName());
            time.setText(feedVO.getTime());
            feed.setText(feedVO.getFeed());
            total.setText("좋아요 " + feedVO.getFavorite() + "개");
            comment.setText("댓글 " + feedVO.getComments().size() + "개");
        }

        recyclerView_comment = (androidx.recyclerview.widget.RecyclerView) findViewById(R.id.feed_dtail_recycle);
        recyclerView_comment.setLayoutManager(new LinearLayoutManager(this));

        CommentAdapter adapter = new CommentAdapter(feedVO.getComments());
        recyclerView_comment.setAdapter(adapter);

        if (feedVO.getImgs().size() > 0 && feedVO.getImgs() != null) {
            recyclerView_img = (androidx.recyclerview.widget.RecyclerView) findViewById(R.id.recycle_feed_dtail);
            LinearLayoutManager manager = new LinearLayoutManager(FeedDtail.this);
            manager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView_img.setLayoutManager(manager);

            ImageAdapter adapterimg = new ImageAdapter(null, feedVO.getImgs(), true);
            recyclerView_img.setAdapter(adapterimg);
        }

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editComment.getText().toString().length() <= 0 || editComment.getText() == null) {
                    Toast.makeText(FeedDtail.this, "댓글을 입력해주세요.", Toast.LENGTH_LONG);
                } else {
                    Date today = new Date();
                    SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");
                    String commentDate = simpleDate.format(today);

                    SimpleDateFormat simpleTime = new SimpleDateFormat("HH:mm");
                    String commentTime = simpleTime.format(today);

                    feedVO.getComments().add(new CommentVO(MainActivity.email,String.valueOf(MainActivity.photoUrl), MainActivity.name, commentDate, commentTime, editComment.getText().toString()));
                    editComment.setText("");
                    comment.setText("댓글 " + feedVO.getComments().size() + "개");

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("feed");
                    myRef.setValue(Feed.list);

                    recyclerView_comment.setAdapter(new CommentAdapter(feedVO.getComments()));
                }
            }
        });

    }

    public static void reset() {
        CommentAdapter adapter = new CommentAdapter(Feed.list.get(index).getComments());
        recyclerView_comment.setAdapter(adapter);
        if (Feed.list.get(index).getImgs().size() > 0 && Feed.list.get(index).getImgs() != null) {
            ImageAdapter adapterimg = new ImageAdapter(null, Feed.list.get(index).getImgs(), true);
            recyclerView_img.setAdapter(adapterimg);
        }
    }
}