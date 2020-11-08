package com.example.walk;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aqoong.lib.expandabletextview.ExpandableTextView;
import com.github.vipulasri.timelineview.TimelineView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class RecordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<MemoVO> mData = null;
    Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout itemLayout;
        ExpandableTextView memo;
        TextView title, time, adress, imgCount; //메모 제목, 메모내용, 시간, 주소
        androidx.recyclerview.widget.RecyclerView recyclerView;  //이미지를 담는 리니어 레이아웃
        TimelineView timelineView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemLayout = itemView.findViewById(R.id.item_layout);
            title = itemView.findViewById(R.id.title_record);
            memo = itemView.findViewById(R.id.memo_record);
            time = itemView.findViewById(R.id.time_record);
            adress = itemView.findViewById(R.id.adress_record);
            imgCount = itemView.findViewById(R.id.memo_imgCount);
            timelineView = itemView.findViewById(R.id.timeline);
            recyclerView = itemView.findViewById(R.id.record_Recycler);

        }
    }

    public class ViewHolder2 extends RecyclerView.ViewHolder {

        LinearLayout itemLayout;
        ExpandableTextView memo;
        TextView title, time, adress; //메모 제목, 메모내용, 시간, 주소
        TimelineView timelineView;

        public ViewHolder2(@NonNull View itemView) {
            super(itemView);

            itemLayout = itemView.findViewById(R.id.item_layout);
            title = itemView.findViewById(R.id.title_record);
            memo = itemView.findViewById(R.id.memo_record);
            time = itemView.findViewById(R.id.time_record);
            adress = itemView.findViewById(R.id.adress_record);
            timelineView = itemView.findViewById(R.id.timeline);

        }
    }

    RecordAdapter(ArrayList<MemoVO> list) {
        mData = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = null;

        switch (viewType) {
            case 2:
                view = inflater.inflate(R.layout.record, parent, false);
                ViewHolder vh = new ViewHolder(view);

                return vh;
            case 1:
                view = inflater.inflate(R.layout.walk_record, parent, false);
                ViewHolder2 vh1 = new ViewHolder2(view);

                return vh1;
        }

        view = inflater.inflate(R.layout.record, parent, false);
        RecordAdapter.ViewHolder vh = new RecordAdapter.ViewHolder(view);

        return vh;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (mData.get(position).isMemoCheck()) {
            ViewHolder2 holder2 = (ViewHolder2) holder;

            String title = mData.get(position).getTitle();
            String memo = mData.get(position).getMemo();
            String date = mData.get(position).getDate();
            String time = mData.get(position).getTime();
            String adress = mData.get(position).getAdress();
            /*         ArrayList<String> imgs = mData.get(position).getImgs();*/

            holder2.title.setText(title);
            holder2.memo.setText(memo, "더보기");
            holder2.memo.setState(ExpandableTextView.STATE.COLLAPSE);
            holder2.time.setText(time);

            String[] adressSpilt = adress.split(" ");

            holder2.adress.setText(adressSpilt[1] + " " + adressSpilt[2] + " " + adressSpilt[3]);

            holder2.itemLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    WalkRecord.check = false;
                    PopupMenu popup = new PopupMenu(context, v);
                    popup.getMenuInflater().inflate(R.menu.memo_pop_menu, popup.getMenu());

                    PopupMenu.OnMenuItemClickListener listener = new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            // TODO Auto-generated method stub
                            switch (item.getItemId()) {//눌러진 MenuItem의 Item Id를 얻어와 식별
                                case R.id.feed_edit:
                                    WalkRecord.check = false;
                                    Intent intent = new Intent((Activity) context, RecordDtailModified.class);
                                    intent.putExtra("memo", position);
                                    ((Activity) context).startActivity(intent);
                                    break;
                                case R.id.feed_remove:
                                    androidx.appcompat.app.AlertDialog.Builder alert = new AlertDialog.Builder((Activity) context);
                                    alert.setTitle("메모 삭제");
                                    alert.setMessage("메모를 정말로 삭제하시겠습니까?");
                                    alert.setPositiveButton("네", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

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
                                    alert.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                                    alert.show();
                                    break;
                            }
                            return false;
                        }
                    };

                    popup.setOnMenuItemClickListener(listener);
                    popup.show();//Popup Menu 보이기
                    return false;
                }
            });

/*            if (imgs != null) {
                //글의 사진 설정
                holder2.imgCount.setText("사진 " + imgs.size() + "장");
                LinearLayoutManager manager = new LinearLayoutManager(context);
                manager.setOrientation(LinearLayoutManager.HORIZONTAL);
                holder2.recyclerView.setLayoutManager(manager);

                ImageAdapter adapter = new ImageAdapter(null, imgs, true);
                holder2.recyclerView.setAdapter(adapter);
            } else {
                holder2.imgCount.setVisibility(View.GONE);
            }*/

            if (mData.size() == 1) {
                holder2.timelineView.initLine(3);
            } else {
                if (position == 0) {
                    holder2.timelineView.initLine(1);
                } else if (position == mData.size() - 1) {
                    holder2.timelineView.initLine(2);
                } else {
                    holder2.timelineView.initLine(4);
                }
            }

            holder2.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WalkRecord.check = false;
                    Intent intent = new Intent(context, RecordDtail.class);
                    intent.putExtra("memo", position);
                    ((Activity) context).startActivity(intent);
                }
            });

        } else {
            ViewHolder holder1 = (ViewHolder) holder;

            String title = mData.get(position).getTitle();
            String memo = mData.get(position).getMemo();
            String date = mData.get(position).getDate();
            String time = mData.get(position).getTime();
            String adress = mData.get(position).getAdress();
            ArrayList<String> imgs = mData.get(position).getImgs();

            holder1.title.setText(title);
            holder1.memo.setText(memo, "더보기");
            holder1.memo.setState(ExpandableTextView.STATE.COLLAPSE);
            holder1.time.setText(time);

            String[] adressSpilt = adress.split(" ");

            holder1.adress.setText(adressSpilt[1] + " " + adressSpilt[2] + " " + adressSpilt[3]);

            holder1.itemLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    WalkRecord.check = false;
                    PopupMenu popup = new PopupMenu(context, v);
                    popup.getMenuInflater().inflate(R.menu.memo_pop_menu, popup.getMenu());

                    PopupMenu.OnMenuItemClickListener listener = new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            // TODO Auto-generated method stub
                            switch (item.getItemId()) {//눌러진 MenuItem의 Item Id를 얻어와 식별
                                case R.id.feed_edit:
                                    WalkRecord.check = false;
                                    Intent intent = new Intent((Activity) context, RecordDtailModified.class);
                                    intent.putExtra("memo", position);
                                    ((Activity) context).startActivity(intent);
                                    break;
                                case R.id.feed_remove:
                                    androidx.appcompat.app.AlertDialog.Builder alert = new AlertDialog.Builder((Activity) context);
                                    alert.setTitle("메모 삭제");
                                    alert.setMessage("메모를 정말로 삭제하시겠습니까?");
                                    alert.setPositiveButton("네", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

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
                                    break;
                            }
                            return false;
                        }
                    };

                    popup.setOnMenuItemClickListener(listener);
                    popup.show();//Popup Menu 보이기
                    return false;
                }
            });

            if (imgs != null) {
                //글의 사진 설정

                if (imgs.size() == 0) {
                    holder1.imgCount.setVisibility(View.GONE);
                } else {
                    holder1.imgCount.setText("사진 " + imgs.size() + "장");
                }
                LinearLayoutManager manager = new LinearLayoutManager(context);
                manager.setOrientation(LinearLayoutManager.HORIZONTAL);
                holder1.recyclerView.setLayoutManager(manager);

                ImageAdapter adapter = new ImageAdapter(null, imgs, true);
                holder1.recyclerView.setAdapter(adapter);
            } else {
                holder1.imgCount.setVisibility(View.GONE);
            }

            if (mData.size() == 1) {
                holder1.timelineView.initLine(3);
            } else {
                if (position == 0) {
                    holder1.timelineView.initLine(1);
                } else if (position == mData.size() - 1) {
                    holder1.timelineView.initLine(2);
                } else {
                    holder1.timelineView.initLine(4);
                }
            }

            holder1.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WalkRecord.check = false;
                    Intent intent = new Intent(context, RecordDtail.class);
                    intent.putExtra("memo", position);
                    ((Activity) context).startActivity(intent);
                }
            });

        }
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mData.get(position).isMemoCheck()) {
            return 1;
        } else {
            return 2;
        }
        /* return super.getItemViewType(position);*/
    }


}
