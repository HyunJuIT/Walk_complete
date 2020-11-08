package com.example.walk;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private ArrayList<byte[]> mData = null;
    private ArrayList<String> mDatas = null;
    private StorageReference storageReference;
    boolean firebase;
    Context context;

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imageView_recycle);
        }
    }

    public ImageAdapter(ArrayList<byte[]> mData,ArrayList<String> mDatas, boolean firebase) {
        this.mData = mData;
        this.mDatas = mDatas;
        this.firebase = firebase;
    }

    public StorageReference getStorageReference() {
        return storageReference;
    }

    public void setStorageReference(StorageReference storageReference) {
        this.storageReference = storageReference;
    }

    @NonNull
    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.image, parent, false);
        ImageAdapter.ViewHolder vh = new ImageAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.ViewHolder holder, int position) {

        if (mData!=null) {
            byte[] img = mData.get(position);

            Bitmap bitmapProfile = BitmapFactory.decodeByteArray(img, 0, img.length);
            bitmapProfile = ThumbnailUtils.extractThumbnail(bitmapProfile, 300, 300);
            holder.img.setImageBitmap(bitmapProfile);
        } else {
            String img = mDatas.get(position);
            // Reference to an image file in Cloud Storage

            if(firebase){
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
            }else{
               //일단 보류
                byte[] encodeByte = Base64.decode(img, android.util.Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte,0,encodeByte.length);
                bitmap = ThumbnailUtils.extractThumbnail(bitmap, 300, 300);
                holder.img.setImageBitmap(bitmap);
            }
        }
    }

    @Override
    public int getItemCount() {
        int number;
        if(mData!=null){
            number = mData.size();
        }else{
            number = mDatas.size();
        }
        return number;
    }
}
