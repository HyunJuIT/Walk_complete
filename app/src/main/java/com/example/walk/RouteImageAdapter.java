package com.example.walk;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class RouteImageAdapter extends RecyclerView.Adapter<RouteImageAdapter.ViewHolder>  {

    private ArrayList<String> mDatas = null;
    Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imageView_recycle_route);
        }
    }

    public RouteImageAdapter(ArrayList<String> data){
        this.mDatas = data;
    }

    @NonNull
    @Override
    public RouteImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.image_route, parent, false);
        RouteImageAdapter.ViewHolder vh = new RouteImageAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String img = mDatas.get(position);
        // Reference to an image file in Cloud Storage

            StorageReference storageReference = FirebaseStorage.getInstance().getReference(img);

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

    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }
}
