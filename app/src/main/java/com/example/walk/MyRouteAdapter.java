package com.example.walk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MyRouteAdapter extends RecyclerView.Adapter<MyRouteAdapter.ViewHolder> {

    private ArrayList<MyRouteVO> mData = null;
    Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView adress, title, km;
        ImageView img;
        RatingBar ratingBar;
        androidx.constraintlayout.widget.ConstraintLayout item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            adress = itemView.findViewById(R.id.txt_adress_my_route);
            title = itemView.findViewById(R.id.txt_title_share_route);
            km = itemView.findViewById(R.id.txt_km_my_route);
            img = itemView.findViewById(R.id.img_my_route);
            ratingBar = itemView.findViewById(R.id.txt_rating_my_route);
            item = itemView.findViewById(R.id.my_route);
        }
    }

    public MyRouteAdapter(ArrayList<MyRouteVO> mData) {
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyRouteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.my_route, parent, false);
        MyRouteAdapter.ViewHolder vh = new MyRouteAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyRouteAdapter.ViewHolder holder, int position) {

        if(mData.get(position).getId().equals(MainActivity.email)){
            String title = mData.get(position).getRouteTitle();
            double km = mData.get(position).getInformation().getKm();
            String adress = mData.get(position).getAdress();
            ArrayList<String> imgs = mData.get(position).getImgs();
            float rating = mData.get(position).getSatisfaction();


            String[] adressSetText = adress.split(" ");

            holder.title.setText(title);
            holder.km.setText("산책로 길이 " + String.format("%.2f", km) +"km");
            holder.adress.setText(adressSetText[1] + " " + adressSetText[2] + " " + adressSetText[3]);
            holder.ratingBar.setRating(rating);

            holder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,MyRouteDetail.class);
                    intent.putExtra("myrouteindex",position);
                    ((Activity) context).startActivity(intent);
                }
            });

            StorageReference storageReference = FirebaseStorage.getInstance().getReference(imgs.get(0));

            storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        // Glide 이용하여 이미지뷰에 로딩
                        Glide.with((Activity) context)
                                .load(task.getResult())
                                .into(holder.img);
                    } else {

                    }
                }
            });
        }else{
            holder.item.setVisibility(View.GONE);
            holder.item.setLayoutParams(new androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(0,0));
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
