package com.example.walk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aqoong.lib.expandabletextview.ExpandableTextView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {


    private ArrayList<FeedVO> mData = null;
    Context context;

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profile;    //프로필
        ImageButton btn_plusMenu;    //플러스메뉴
        ExpandableTextView feed;
        TextView nickName, time, total, imgCount;  //닉네임 //시간 //피드 내용 //좋아요, 댓글 갯수 띄우는 텍스트뷰
        androidx.recyclerview.widget.RecyclerView recyclerView;  //사진 저장 레이아웃
        Button favorite, comment;    //좋아요버튼 //댓글 버튼

        ViewHolder(View itemView) {
            super(itemView);

            // 뷰 객체에 대한 참조. (hold strong reference)
            profile = itemView.findViewById(R.id.profile_img_feed);
            nickName = itemView.findViewById(R.id.nickName_feed);
            time = itemView.findViewById(R.id.time_feed);
            btn_plusMenu = itemView.findViewById(R.id.btn_plusManu);
            feed = itemView.findViewById(R.id.text_feed_item);
            recyclerView = itemView.findViewById(R.id.feedItem_img_recycler);
            total = itemView.findViewById(R.id.feed_total);
            imgCount = itemView.findViewById(R.id.text_feed_imgCount);
            favorite = itemView.findViewById(R.id.btn_favorite);
            comment = itemView.findViewById(R.id.btn_comment);
        }
    }

    // 생성자에서 데이터 리스트 객체를 전달받음.
    FeedAdapter(ArrayList<FeedVO> list) {
        mData = list;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public FeedAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.feed_img, parent, false);
        FeedAdapter.ViewHolder vh = new FeedAdapter.ViewHolder(view);

        return vh;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(FeedAdapter.ViewHolder holder, int position) {
   /*     try {*/
            String profile = mData.get(position).getProfile();    //프로필
            String nickName = mData.get(position).getNickName();    //닉네임
            String time = mData.get(position).getTime();  //시간
            String feed = mData.get(position).getFeed();  //피드 내용
            ArrayList<String> imgs = mData.get(position).getImgs();
            int favorite = mData.get(position).getFavorite(); //좋아요 갯수 텍스트뷰
            ArrayList<String> favorite_main = mData.get(position).getFavorite_main();  //내 좋아요
            ArrayList<CommentVO> comments = mData.get(position).getComments();
            boolean favoritCheck = mData.get(position).getFavorite_main().contains(MainActivity.email);

            //프로필 이미지 설정
            try {

                InputStream in = ((Activity)context).getContentResolver().openInputStream(Uri.parse(profile));
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                bitmap = ThumbnailUtils.extractThumbnail(bitmap, 300, 300);
                holder.profile.setImageBitmap(bitmap);

            }catch (IOException e){

            }

            holder.nickName.setText(nickName);
            holder.time.setText(time);
            holder.feed.setText(feed,"더보기");
            holder.feed.setState(ExpandableTextView.STATE.COLLAPSE);

            if (imgs != null) {
                if (imgs.size() > 0){
                    holder.imgCount.setText("사진 "+imgs.size()+"장");
                }else{
                    holder.imgCount.setVisibility(View.GONE);
                }

                //글의 사진 설정
                LinearLayoutManager manager = new LinearLayoutManager(context);
                manager.setOrientation(LinearLayoutManager.HORIZONTAL);
                holder.recyclerView.setLayoutManager(manager);

                ImageAdapter adapter = new ImageAdapter(null,imgs,true);
                holder.recyclerView.setAdapter(adapter);
            }else{
                holder.imgCount.setVisibility(View.GONE);
            }

            holder.total.setText("좋아요 " + favorite + "개 댓글 " + comments.size() + "개");
/*
        } catch (NullPointerException e) {

        }*/

        if (favoritCheck) {
            /*mData.get(position).setFavorite_main(true);*/
            holder.favorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_24px, 0, 0, 0);
        } else {
          /*  mData.get(position).setFavorite_main(false);*/
            holder.favorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_border_24px, 0, 0, 0);
        }

        holder.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!favoritCheck) {
                    /*mData.get(position).setFavorite_main(true);*/
                    favorite_main.add(MainActivity.email);
                    mData.get(position).setFavorite(mData.get(position).getFavorite() + 1);
                    holder.total.setText("좋아요 " + mData.get(position).getFavorite() + "개 댓글 " + mData.get(position).getComments().size() + "개");
                    holder.favorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_24px, 0, 0, 0);
                } else {
                  /*  mData.get(position).setFavorite_main(false);*/
                    favorite_main.remove(MainActivity.email);
                    mData.get(position).setFavorite(mData.get(position).getFavorite() - 1);
                    holder.total.setText("좋아요 " + mData.get(position).getFavorite() + "개 댓글 " + mData.get(position).getComments().size() + "개");
                    holder.favorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_border_24px, 0, 0, 0);
                }

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("feed");
                myRef.setValue(Feed.list);
                MainActivity.handler.sendEmptyMessage(4);
            }
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FeedDtail.class);
                intent.putExtra("comment", position);
                ((Activity) context).startActivity(intent);
            }
        });

        holder.btn_plusMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, v);
                popup.getMenuInflater().inflate(R.menu.feed_pop_menu, popup.getMenu());

                PopupMenu.OnMenuItemClickListener listener = new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // TODO Auto-generated method stub
                        switch (item.getItemId()) {//눌러진 MenuItem의 Item Id를 얻어와 식별
                            case R.id.feed_edit:
                                Intent intent = new Intent(context, FeedWriting.class);
                                intent.putExtra("feededit", position);
                                ((Activity) context).startActivity(intent);
                                break;
                            case R.id.feed_remove:
                                AlertDialog.Builder alert = new AlertDialog.Builder((Activity)context);
                                alert.setTitle("글 삭제");
                                alert.setMessage("글을 정말로 삭제할까요?");
                                alert.setPositiveButton("네", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Feed.list.remove(position);

                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        DatabaseReference myRef = database.getReference("feed");
                                        myRef.setValue(Feed.list);

                                        Feed.reset();
                                    }
                                });
                                alert.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                alert.show();
                                break;
                            case R.id.feed_share:
                                break;
                        }
                        return false;
                    }
                };

                popup.setOnMenuItemClickListener(listener);
                popup.show();//Popup Menu 보이기
            }
        });
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return mData.size();
    }

}
