package com.example.walk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ShareRouteBookMarkAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<MyRouteVO> mData = null;
    Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView adress, title, km;
        ImageView img;
        RatingBar ratingBar;
        ImageButton imageButton;
        androidx.constraintlayout.widget.ConstraintLayout item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            adress = itemView.findViewById(R.id.txt_adress_share_route);
            title = itemView.findViewById(R.id.txt_title_share_route);
            km = itemView.findViewById(R.id.txt_km_share_route);
            img = itemView.findViewById(R.id.img_share_route);
            ratingBar = itemView.findViewById(R.id.txt_rating_share_route);
            imageButton = itemView.findViewById(R.id.bookmark_share_route);
            item = itemView.findViewById(R.id.share_route);
        }
    }

    public class ViewHolderMyroute extends RecyclerView.ViewHolder {

        TextView adress, title, km;
        ImageView img;
        RatingBar ratingBar;
        androidx.constraintlayout.widget.ConstraintLayout item;

        public ViewHolderMyroute(@NonNull View itemView) {
            super(itemView);

            adress = itemView.findViewById(R.id.txt_adress_my_route);
            title = itemView.findViewById(R.id.txt_title_share_route);
            km = itemView.findViewById(R.id.txt_km_my_route);
            img = itemView.findViewById(R.id.img_my_route);
            ratingBar = itemView.findViewById(R.id.txt_rating_my_route);
            item = itemView.findViewById(R.id.my_route);
        }
    }

    public ShareRouteBookMarkAdapter(ArrayList<MyRouteVO> mData) {
        this.mData = mData;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = null;

        switch (viewType) {
            case 2:
                view = inflater.inflate(R.layout.share_route, parent, false);
                ShareRouteBookMarkAdapter.ViewHolder vh = new ShareRouteBookMarkAdapter.ViewHolder(view);

                return vh;
            case 1:
                view = inflater.inflate(R.layout.my_route, parent, false);
                ShareRouteBookMarkAdapter.ViewHolderMyroute vh1 = new ShareRouteBookMarkAdapter.ViewHolderMyroute(view);

                return vh1;
        }

        view = inflater.inflate(R.layout.share_route, parent, false);
        ShareRouteBookMarkAdapter.ViewHolder vh = new ShareRouteBookMarkAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (mData.get(position).getId().equals(MainActivity.email)) {

            ShareRouteBookMarkAdapter.ViewHolderMyroute viewHolderMyroute = (ShareRouteBookMarkAdapter.ViewHolderMyroute) holder;

           /* if (mData.get(position).isShare()) {

                String title = mData.get(position).getRouteTitle();
                double km = mData.get(position).getInformation().getKm();
                String adress = mData.get(position).getAdress();
                ArrayList<String> imgs = mData.get(position).getImgs();
                float rating = mData.get(position).getSatisfaction();


                String[] adressSetText = adress.split(" ");

                viewHolderMyroute.title.setText(title);
                viewHolderMyroute.km.setText("산책로 길이 " + String.format("%.2f", km) + "km");
                viewHolderMyroute.adress.setText(adressSetText[1] + " " + adressSetText[2] + " " + adressSetText[3]);
                viewHolderMyroute.ratingBar.setRating(rating);

                StorageReference storageReference = FirebaseStorage.getInstance().getReference(imgs.get(0));

                storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            // Glide 이용하여 이미지뷰에 로딩
                            Glide.with((Activity) context)
                                    .load(task.getResult())
                                    .into(viewHolderMyroute.img);
                        } else {

                        }
                    }
                });

                viewHolderMyroute.item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, MyRouteDetail.class);
                        intent.putExtra("myrouteindex", position);
                        ((Activity) context).startActivity(intent);
                    }
                });
            } else {*/
                viewHolderMyroute.item.setVisibility(View.GONE);
         /*   }*/
        } else {
            ShareRouteBookMarkAdapter.ViewHolder viewHolder = (ShareRouteBookMarkAdapter.ViewHolder) holder;
            boolean bookMark = mData.get(position).getBookMark().contains(MainActivity.email);
            if (mData.get(position).isShare()&&bookMark) {
                String title = mData.get(position).getRouteTitle();
                double km = mData.get(position).getInformation().getKm();
                String adress = mData.get(position).getAdress();
                ArrayList<String> imgs = mData.get(position).getImgs();
                float rating = mData.get(position).getSatisfaction();
                ArrayList<String> bookMarks = mData.get(position).getBookMark();

                String[] adressSetText = adress.split(" ");

                viewHolder.title.setText(title);
                viewHolder.km.setText("산책로 길이 " + String.format("%.2f", km) + "km");
                viewHolder.adress.setText(adressSetText[1] + " " + adressSetText[2] + " " + adressSetText[3]);
                viewHolder.ratingBar.setRating(rating);

                StorageReference storageReference = FirebaseStorage.getInstance().getReference(imgs.get(0));

                storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            // Glide 이용하여 이미지뷰에 로딩
                            Glide.with((Activity) context)
                                    .load(task.getResult())
                                    .into(viewHolder.img);
                        } else {

                        }
                    }
                });

                viewHolder.item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ShareRouteDetail.class);
                        intent.putExtra("myrouteindex", position);
                        ((Activity) context).startActivity(intent);
                    }
                });


                if (bookMark) {
                    viewHolder.imageButton.setBackgroundResource(R.drawable.ic_bookmark_24px);
                } else {
                    viewHolder.imageButton.setBackgroundResource(R.drawable.ic_bookmark_border_24px);
                }

                viewHolder.imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!bookMark) {
                            v.setBackgroundResource(R.drawable.ic_bookmark_24px);
                            bookMarks.add(MainActivity.email);
                        } else {
                            v.setBackgroundResource(R.drawable.ic_bookmark_border_24px);
                            bookMarks.remove(MainActivity.email);
                        }

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("route");
                        myRef.setValue(MyRoute.list);
                        MainActivity.handler.sendEmptyMessage(6);
                        BookMarkRoute.handler.sendEmptyMessage(1);
                    }
                });
            } else {
                viewHolder.item.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mData.get(position).getId().equals(MainActivity.email)) {
            return 1;
        } else {
            return 2;
        }
        /* return super.getItemViewType(position);*/
    }
}
