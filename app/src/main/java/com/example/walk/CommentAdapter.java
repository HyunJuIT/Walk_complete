package com.example.walk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private ArrayList<CommentVO> mData = null;
    Context context;

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profile;
        TextView nickName, comment, date, edit, remove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.profile_comment);
            nickName = itemView.findViewById(R.id.nickName_commet);
            comment = itemView.findViewById(R.id.txt_comment);
            date = itemView.findViewById(R.id.txt_today_comment);
            edit = itemView.findViewById(R.id.txt_edit_comment);
            remove = itemView.findViewById(R.id.txt_remove_comment);
        }
    }

    // 생성자에서 데이터 리스트 객체를 전달받음.
    public CommentAdapter(ArrayList<CommentVO> mData) {
        this.mData = mData;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.comment, parent, false);
        CommentAdapter.ViewHolder vh = new CommentAdapter.ViewHolder(view);

        return vh;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder holder, int position) {
        String profile = mData.get(position).getProfile();    //프로필
        String nickName = mData.get(position).getNickName();    //닉네임
        String date = mData.get(position).getDate();
        String time = mData.get(position).getTime();  //시간
        String comment = mData.get(position).getComment();  //댓글 내용

        //프로필 이미지 설정
        try {

            InputStream in = ((Activity)context).getContentResolver().openInputStream(Uri.parse(profile));
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, 300, 300);
            holder.profile.setImageBitmap(bitmap);

        }catch (IOException e){

        }

        holder.nickName.setText(nickName);
        holder.date.setText(date+"  "+time);
        holder.comment.setText(comment);

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommentEditDialog commentEditDialog = new CommentEditDialog(context, new CommentEditDialogClickListener() {
                    @Override
                    public void onEditClick() {

                    }

                    @Override
                    public void onCancleClick() {

                    }
                },position);

                commentEditDialog.setCanceledOnTouchOutside(true);
                commentEditDialog.setCancelable(true);
                commentEditDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                commentEditDialog.show();
            }
        });

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alert = new AlertDialog.Builder((Activity)context);
                alert.setTitle("댓글 삭제");
                alert.setMessage("댓글을 정말로 삭제할까요?");
                alert.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Feed.list.get(FeedDtail.index).getComments().remove(position);

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("feed");
                        myRef.setValue(Feed.list);

                        Feed.reset();
                        FeedDtail.reset();
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
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return mData.size();
    }
}
