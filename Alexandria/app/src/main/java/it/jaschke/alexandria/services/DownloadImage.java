package it.jaschke.alexandria.services;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.InputStream;

import it.jaschke.alexandria.MainActivity;
import it.jaschke.alexandria.R;

/**
 * Created by saj on 11/01/15.
 */
@Deprecated
public class DownloadImage extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;

    public DownloadImage(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String urlDisplay = urls[0];
        Bitmap bookCover = null;

        /**
         * BRUNO:
         * BUG:
         *  Appends when there is no internet connection and the UI trains to load an image
         *  if not connected, the execution of this method will return null and the book entry it will not display a cover and messing with the UI;
         *  Also every time the cover image is displayed it needs to be downloaded of the internet, thus consuming bandwidth and making a bad app experience.
         *
         *  The better solution is to cache the images and only fetch the ones not cashed.
         *
         *  This could be easily done with an auxiliary library like Glide or Picasso
         *
         * FIX:
         * I replaced all occurrences of this class with Picasso library
         * and ignoring this class.
         *
         *
         */
        if(urlDisplay!=null)

            try {
                InputStream in = new java.net.URL(urlDisplay).openStream();
                bookCover = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }

        return bookCover;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }
}

