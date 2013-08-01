package com.newpush.mypractice;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.FileFilter;

public class Skin {
    public static String getSkinDirectoryPath(Context context) {
        FileFilter fileFilter = new FileFilter() {
            public boolean accept(File file) {
                return file.isDirectory();
            }
        };
        File skinRootDirectory = new File(context.getFilesDir() + "/skin/");
        File[] files = skinRootDirectory.listFiles(fileFilter);
        return files[0].getAbsolutePath();
    }

    public static void extractDesignPack(Context context) {
        try {
            ZipFile designPack = new ZipFile(getSkinDirectoryPath(context) + "DesignPack.zip");
            designPack.extractAll(getSkinDirectoryPath(context));
        } catch (ZipException e) {
            e.printStackTrace();
        }
   }

    public static void prepareSkinDirectory(Context context) {
        File skinFolder = new File(getSkinDirectoryPath(context));
        if (!skinFolder.exists()) {
            skinFolder.mkdir();
        }
    }

    public static void applyThemeSplash(Activity splashActivity) {
        File splashFile = new File(getSkinDirectoryPath(splashActivity) + "/splashscreen.png");
        if (splashFile.exists()) {
            Bitmap splashBitmap = BitmapFactory.decodeFile(splashFile.getAbsolutePath());
            ImageView splashView = (ImageView) splashActivity.findViewById(R.id.splash_image);
            splashView.setImageBitmap(splashBitmap);
        }
    }
    
    public static void applyThemeLogo(Activity activityWithLogo) {
        File logoFile = new File(getSkinDirectoryPath(activityWithLogo) + "/logo.png");
        if (logoFile.exists()) {
            Bitmap logoBitmap = BitmapFactory.decodeFile(logoFile.getAbsolutePath());
            ImageView logoView = (ImageView) activityWithLogo.findViewById(R.id.logo);
            logoView.setImageBitmap(logoBitmap);
        }
    }
}
