package com.anbdevelopers.piechartgenerator;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.jsibbold.zoomage.ZoomageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.Objects;


public class PhotoFragment extends Fragment {

    private photoFragmentListener listener;

    public interface photoFragmentListener
    {
        void onInputASent(CharSequence input);
    }
    ImageButton shareBtn;
    String url;
    Button copyUrlBtn;
    Button saveBtn;
    ImageButton cancelBtn;
    static ZoomageView zoomageView;
    Context context;
    //BitmapDrawable drawable;
   // Bitmap bitmap;
    public static final int PERMISSION_WRITE = 0;
    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {

        context=getContext();
        View v = inflater.inflate(R.layout.fragment_photo, container, false);
        assert getArguments() != null;
        url=getArguments().getString("urlkey");
        cancelBtn=v.findViewById(R.id.cancelBtn);
        zoomageView=v.findViewById(R.id.myZoomageView);
        copyUrlBtn=v.findViewById(R.id.copyBtn);
        saveBtn=v.findViewById(R.id.saveBtn);
        shareBtn=v.findViewById(R.id.shareBtn);
//        Picasso.with(context).load(url).get();

        Picasso.get()
                .load(url)
                .placeholder(R.drawable.progress_animation)
                .error(R.drawable.failed)
                .into(zoomageView);


        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().getSupportFragmentManager().beginTransaction().remove(PhotoFragment.this).commit();
            }
        });

        copyUrlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (url != null) {
                    setClipboard(requireContext(),url);
                    Toast.makeText(getContext(), "Image Url copied to Clipboard", Toast.LENGTH_LONG).show();
                }
                else Toast.makeText(getContext(), "Failed to copy Url", Toast.LENGTH_SHORT).show();

            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (url!=null&&isNetworkAvailable())
                {
                DownloadImage(url);
                }
                else
                    {
                    Toast.makeText(getContext(), "Not valid", Toast.LENGTH_SHORT).show();
                    }
            }
        });
        shareBtn.setOnClickListener(view -> shareImage(url));
        return v;

    }

    private void shareImage(String url)
    {
        Picasso.get().load(url).into(new Target() {
            @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("image/*");
//                BitmapDrawable drawable = (BitmapDrawable) zoomageView.getDrawable();
//                Bitmap bitmap1 = drawable.getBitmap();
                Bitmap bitmap1=((BitmapDrawable)zoomageView.getDrawable()).getBitmap();
                i.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap1));
                startActivity(Intent.createChooser(i, "Share Image"));
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
            }


            @Override public void onPrepareLoad(Drawable placeHolderDrawable) { }
        });

    }
    public Uri getLocalBitmapUri(Bitmap bmp)
    {
        Uri bmpUri = null;

        try {

            File file =  new File(context.getExternalFilesDir(null), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri= FileProvider.getUriForFile(Objects.requireNonNull(context.getApplicationContext()),
                    BuildConfig.APPLICATION_ID + ".provider", file);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    private void setClipboard(Context context, String text)
    {

            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
        assert clipboard != null;
        clipboard.setPrimaryClip(clip);


    }


    void DownloadImage(String ImageUrl)
    {

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
            //showToast("Need Permission to access storage for Downloading Image");
            Toast.makeText(getContext(), "Need Permission to access storage for Downloading Image", Toast.LENGTH_SHORT).show();
        } else
            {

            Toast.makeText(getContext(), "Checking For connection", Toast.LENGTH_SHORT).show();
            Toast.makeText(getContext(), "Downloading Image...", Toast.LENGTH_LONG).show();
            //Asynctask to create a thread to download image in the background
             new DownloadsImageUsingBitmap(getContext()).execute(ImageUrl);
            //new DownloadsImage().execute(ImageUrl);
            }
    }
    private boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    static class DownloadsImageUsingBitmap extends AsyncTask<String, Void,Void>
    {
        //private Context mContext;
        private WeakReference<Context> mContext;
        public DownloadsImageUsingBitmap(Context context)
        {
            mContext = new WeakReference<>(context);
        }

        @Override
        protected Void doInBackground(String... strings)
        {
            Bitmap bitmapz=getbitmap();
//            bitmap1=BitmapFactory.decodeResource(context.getResources(),
//                    zoomageView.getDrawable());
            if (bitmapz==null)
            {
                //Log.d("bitmap", "doInBackground: Bitmap still null");
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveBitmap(bitmapz, mContext.get());
            }else
            {
                File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES+ "/charts"); //Creates app specific folder

                if(!path.exists()) {
                    path.mkdirs();
                }

                File imageFile = new File(path, "Chart "+String.valueOf(System.currentTimeMillis())+".jpg"); // Imagename.png
                FileOutputStream out = null;
                try {
                    if (!path.exists()){
                        path.mkdirs();
                    }
                    out = new FileOutputStream(imageFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try{
                    path.mkdirs();
                    assert bitmapz != null;
                    bitmapz.compress(Bitmap.CompressFormat.JPEG, 99, out); // Compress Image
                    assert out != null;
                    out.flush();
                    out.close();


                    MediaScannerConnection.scanFile(mContext.get(),new String[] { imageFile.getAbsolutePath() }, null, (path1, uri) -> {
                        Log.i("ExternalStorage", "Scanned " + path1 + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    });
                } catch(Exception e) {
                    e.printStackTrace();
                    Log.d("exception", "doInBackground: "+e);
                }}
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            // showToast("Image Saved!");
            Toast.makeText(mContext.get(), "Image Saved!\nTo Internal Storage /Pictures/charts", Toast.LENGTH_LONG).show();
        }
    }
    private static void saveBitmap(Bitmap finalBitmap, Context mContext)
    {
        try
        {
            OutputStream fos = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            {
                ContentResolver resolver = mContext.getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "Chart "+String.valueOf(System.currentTimeMillis())+".jpg");
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES+ "/charts");
                Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));
            }
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 99, fos);
            Objects.requireNonNull(fos).close();
        }catch (IOException e)
        {
            // Log Message
            Log.d("saveError", "saveBitmap: "+e);
        }

    }
    static Bitmap getbitmap()
    {
    return ((BitmapDrawable)zoomageView.getDrawable()).getBitmap();
    }

    @Override
    public void onDestroy() {
        //DownloadsImageUsingBitmap.cancel(true);
        super.onDestroy();

    }
}