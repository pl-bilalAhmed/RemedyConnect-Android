package com.newpush.mypractice;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;

public class Skin {
    public static String getSkinDirectoryPath(Context context) {
        return context.getFilesDir().getAbsolutePath() + "/skin/";
    }

    public static void ExtractDesignPack(Context context) {
        try {
            ZipFile designPack = new ZipFile(getSkinDirectoryPath(context) + "DesignPack.zip");
            designPack.extractAll(getSkinDirectoryPath(context));
        } catch (ZipException e) {
            e.printStackTrace();
        }
   }

    public static void prepareSkinDirectory(Context context) {
        File skinFolder = new File(getSkinDirectoryPath(context));
        skinFolder.mkdir();
    }

    public static void applyThemeSplash(Activity splashActivity) {
        File splashFile = new File(getSkinDirectoryPath(splashActivity) + "splashscreen.png");
        if (splashFile.exists()) {
            Bitmap splashBitmap = BitmapFactory.decodeFile(splashFile.getAbsolutePath());
            ImageView splashView = (ImageView) splashActivity.findViewById(R.id.splash_image);
            splashView.setImageBitmap(splashBitmap);
        }
    }
    
    public static void applyThemeLogo(Activity activityWithLogo) {
        File logoFile = new File(getSkinDirectoryPath(activityWithLogo) + "logo.png");
        if (logoFile.exists()) {
            Bitmap logoBitmap = BitmapFactory.decodeFile(logoFile.getAbsolutePath());
            ImageView logoView = (ImageView) activityWithLogo.findViewById(R.id.logo);
            logoView.setImageBitmap(logoBitmap);
        }
    }
}
