package com.example.walk;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CommentEditDialog extends Dialog {

    private int index;
    private CommentVO commentVO;
    private Context context;
    private TextView nickName;
    private EditText comment;
    private Button edit, cancle;
    private CommentEditDialogClickListener commentEditDialogClickListener;

    public CommentEditDialog(@NonNull Context context, CommentEditDialogClickListener commentEditDialogClickListener,int index) {
        super(context);
        this.context = context;
        this.commentEditDialogClickListener = commentEditDialogClickListener;
        this.index = index;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_edit);

        nickName = findViewById(R.id.nickName_commet_edit);
        comment = findViewById(R.id.comment_edit);
        edit = findViewById(R.id.OK_edit_comment);
        cancle = findViewById(R.id.btn_cancle_commentedit);

        commentVO =  Feed.list.get(FeedDtail.index).getComments().get(index);

        nickName.setText(commentVO.getNickName());
        comment.setText(commentVO.getComment());

        edit.setOnClickListener(v -> {
            // 수정버튼 클릭
            Feed.list.get(FeedDtail.index).getComments().get(index).setComment(comment.getText().toString());
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("feed");
            myRef.setValue(Feed.list);

            Feed.reset();
            FeedDtail.reset();
            this.commentEditDialogClickListener.onEditClick();
            dismiss();
        });

        cancle.setOnClickListener(v -> {
            // 취소버튼 클릭
            this.commentEditDialogClickListener.onCancleClick();
            dismiss();
        });
    }
}
