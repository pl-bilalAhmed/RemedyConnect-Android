package com.newpush.mypractice;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.FileFilter;

public class Skin {
    @SuppressWarnings("deprecation")
    public static void setBackgroundOf(View view, Drawable bg) {
        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            //noinspection deprecation
            view.setBackgroundDrawable(bg);
        } else {
            view.setBackground(bg);
        }
    }

    public static String getSkinDirectoryPath(Context context) {
        FileFilter fileFilter = new FileFilter() {
            public boolean accept(File file) {
                return file.isDirectory();
            }
        };
        // If the skin directory doesn't exists yet, pass it back
        // (we're called from prepareSkinDirectory most likely)
        File skinRootDirectory = new File(context.getFilesDir() + "/skin/");
        if (!skinRootDirectory.exists()) {
            return skinRootDirectory.getAbsolutePath();
        }
        else {
            File[] files = skinRootDirectory.listFiles(fileFilter);
            if (files.length > 0) {
                return files[0].getAbsolutePath();
            }
            else {
                return skinRootDirectory.getAbsolutePath();
            }
        }
    }

    public static void extractDesignPack(Context context) throws ZipException{
        ZipFile designPack = new ZipFile(getSkinDirectoryPath(context) + "/DesignPack.zip");
        designPack.extractAll(getSkinDirectoryPath(context));
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

    public static void applyMainMenuBackground(Activity mainMenuActivity) {
        String filePath = getSkinDirectoryPath(mainMenuActivity) + "/background.png";
        File bgFile = new File(filePath);
        if (bgFile.exists()) {
            Bitmap bgBitmap = BitmapFactory.decodeFile(bgFile.getAbsolutePath());
            ImageView mainMenuBackgroundView = (ImageView) mainMenuActivity.findViewById(R.id.mainMenuBackground);
            mainMenuBackgroundView.setImageBitmap(bgBitmap);
        }
    }

    public static void applyMainMenuButtons(Activity mainMenuActivity) {
        String filePath = getSkinDirectoryPath(mainMenuActivity) + "/button.9.png";
        File buttonFile = new File(filePath);
        Typeface typeface = Typeface.createFromAsset(mainMenuActivity.getAssets(), "fonts/OpenSans-Regular.ttf");
        if (buttonFile.exists()) {
            Bitmap bitmapBg = BitmapFactory.decodeFile(filePath);
            Drawable drawableBg = new BitmapDrawable(mainMenuActivity.getResources(), bitmapBg);
            Button button = (Button) mainMenuActivity.findViewById(R.id.menuButton1);
            button.setTypeface(typeface);
            Skin.setBackgroundOf(button, drawableBg);
            button = (Button) mainMenuActivity.findViewById(R.id.menuButton2);
            button.setTypeface(typeface);
            Skin.setBackgroundOf(button, drawableBg);
            button = (Button) mainMenuActivity.findViewById(R.id.menuButton3);
            button.setTypeface(typeface);
            Skin.setBackgroundOf(button, drawableBg);
            button = (Button) mainMenuActivity.findViewById(R.id.menuButton4);
            button.setTypeface(typeface);
            Skin.setBackgroundOf(button, drawableBg);
            button = (Button) mainMenuActivity.findViewById(R.id.menuButton5);
            button.setTypeface(typeface);
            Skin.setBackgroundOf(button, drawableBg);
            button = (Button) mainMenuActivity.findViewById(R.id.menuButton6);
            button.setTypeface(typeface);
            Skin.setBackgroundOf(button, drawableBg);
        }
    }
}
