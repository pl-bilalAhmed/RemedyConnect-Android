package com.newpush.greenwoodpediatrics;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

public class Skin {
    public static String getSkinDirectoryPath() {
        /*
        @TODO Use a fool-proof location for storing and using skins
         Right now this is only for testing which works on my SGS, but it
         won't work on anything else - most likely. For accessing the files
         in the application's data storage, use:

         String skinDir = context.getFilesDir().getAbsolutePath() + "/skin/";
         */
        return "/storage/sdcard0/pediatricweb/skin/";
    }

    public static void prepareSkinDirectory(Context context) {
        File skinFolder = new File(getSkinDirectoryPath());
        skinFolder.mkdir();
    }

    public static void applyThemeSplash(Activity splashActivity) {
        File splashFile = new File(getSkinDirectoryPath() + "mountain.png");
        if (splashFile.exists()) {
            Bitmap splashBitmap = BitmapFactory.decodeFile(splashFile.getAbsolutePath());
            ImageView splashView = (ImageView) splashActivity.findViewById(R.id.splash_image);
            splashView.setImageBitmap(splashBitmap);
        }
    }
}
