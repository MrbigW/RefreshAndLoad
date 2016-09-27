package com.mydemos.list_asybctask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by Mr.W on 2016/9/6.
 */
public class ImageLoader {

    private Context context;
    private File filedir;
    private LruCache<String, Bitmap> mCaches;


    public ImageLoader(Context context) {
        this.context = context;
        //  获取最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 4;

        mCaches = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
        //  获取文件存储目录
        filedir = context.getFilesDir();
    }

    //  将数据增加到缓存
    public void addBitmapToCache(Bitmap bitmap, String url) {
        if (getBitmapFromCache(url) == null) {
            mCaches.put(url, bitmap);
        }
    }

    //  从缓存中获取数据
    public Bitmap getBitmapFromCache(String url) {
        return mCaches.get(url);
    }

    //  将数据增加到内部存储
    public void addBitmapToFile(Bitmap bitmap, String url) {
        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,new FileOutputStream(new File(filedir, url.substring(url.length() - 30, url.length()))));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    // 从内部存储中获取数据
    public Bitmap getBitmapFormFile(String url) {
        try {
            return BitmapFactory.decodeStream(context.openFileInput(url.substring(url.length() - 30, url.length())));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

//    private Handler handler = new Handler() {
//        public void handleMessage(Message msg) {
//            if (mImageView.getTag().equals(mUrl)) {
//                mImageView.setImageBitmap((Bitmap) msg.obj);
//            }
//        }
//    };

//    public void showImageByThread(ImageView imageView, final String url) {
//        mImageView = imageView;
//        mUrl = url;
//        new Thread() {
//            public void run() {
//                Bitmap bitmap = BitmapFactory.decodeStream(getStremFromURL(url));
//                Message message = Message.obtain();
//                message.obj = bitmap;
//                handler.sendMessage(message);
//            }
//        }.start();
//    }

    //  通过一个url来获取一个流
//    public InputStream getStremFromURL(String urlPath) {
//        try {
//            URL url = new URL(urlPath);
//            InputStream is = url.openStream();
//            return is;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    //  得到图片
    public void showImageByAsyncTask(ImageView imageView, String url) {
        Bitmap bitmap = getBitmapFromCache(url);
        if (bitmap == null) {
         Bitmap   filebitmap = getBitmapFormFile(url);
            if (filebitmap == null) {
                new NewsAsyncTask(imageView, url).execute(url);
            } else {
                imageView.setImageBitmap(filebitmap);
                addBitmapToCache(filebitmap, url);
            }
        } else {
            imageView.setImageBitmap(bitmap);
        }

    }

    //  下载图片
    private class NewsAsyncTask extends AsyncTask<String, Void, Bitmap> {

        private ImageView mImageView;
        private String mUrl;


        public NewsAsyncTask(ImageView imageView, String url) {
            mImageView = imageView;
            mUrl = url;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap;
            String url = params[0];
            InputStream is = null;
            try {
                is = new URL(url).openStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bitmap = BitmapFactory.decodeStream(is);
            addBitmapToCache(bitmap, mUrl);
            addBitmapToFile(bitmap, mUrl);

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (mImageView.getTag().equals(mUrl)) {
                mImageView.setImageBitmap(bitmap);
            }
        }
    }

}
