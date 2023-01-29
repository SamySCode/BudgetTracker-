package com.example.budgettrackerv2.View;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgettrackerv2.R;
import com.google.gson.Gson;
import android.os.Environment;
import java.io.File;
import java.io.FileOutputStream;

import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AddImagesFromGalleryActivity extends AppCompatActivity {

    private RecyclerView imagesRecyclerView;
    private ImageAdapter imageAdapter;
    private ArrayList<Uri> imagesList = new ArrayList<Uri>();
    private static final int GALLERY_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_images_from_gallery);

        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Uri.class, new UriInstanceCreator());
        Gson gson = builder.create();
        String json = sharedPreferences.getString("imagesList", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        ArrayList<String> imagesPath = gson.fromJson(json, type);
        if(imagesPath == null) imagesPath = new ArrayList<>();
        imagesList = new ArrayList<Uri>();
        for (String imagePath : imagesPath) {
            imagesList.add(Uri.parse(imagePath));
        }

        imagesRecyclerView = findViewById(R.id.images_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        imagesRecyclerView.setLayoutManager(layoutManager);
        imageAdapter = new ImageAdapter(imagesList);
        imagesRecyclerView.setAdapter(imageAdapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Uri.class, new UriInstanceCreator());
        Gson gson = builder.create();
        ArrayList<String> imagesPath = new ArrayList<>();
        for (Uri uri : imagesList) {
            imagesPath.add(uri.toString());
        }
        String json = gson.toJson(imagesPath);
        editor.putString("imagesList", json);
        editor.apply();
    }

    public void addImageFromGallery(View view) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                FileOutputStream outputStream = openFileOutput("image.jpg", MODE_PRIVATE);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.close();
                Uri savedImageUri = Uri.fromFile(getFileStreamPath("image.jpg"));
                imagesList.add(savedImageUri);
                imageAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class UriInstanceCreator implements InstanceCreator<Uri> {
        @Override
        public Uri createInstance(Type type) {
            return Uri.parse("");
        }
    }

    public static class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
        private List<Uri> images;

        public ImageAdapter(ArrayList<Uri> images) {
            this.images = images;
        }

        @Override
        public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);
            return new ImageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ImageViewHolder holder, int position) {
            Uri imageUri = images.get(position);
            holder.imageView.setImageURI(imageUri);
        }

        @Override
        public int getItemCount() {
            return images.size();
        }

        public static class ImageViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            public ImageViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.image_view);
            }
        }
    }
}