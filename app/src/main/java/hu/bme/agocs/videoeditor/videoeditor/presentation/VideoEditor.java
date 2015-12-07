package hu.bme.agocs.videoeditor.videoeditor.presentation;

import android.app.Application;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.widget.ImageView;

import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.squareup.picasso.Picasso;

import java.io.File;

import hu.bme.agocs.videoeditor.videoeditor.data.ImageManager;
import hu.bme.agocs.videoeditor.videoeditor.data.VideoManager;
import timber.log.Timber;

/**
 * Created by Agócs Tamás on 2015. 11. 27..
 */
public class VideoEditor extends Application {

    private static Context context;

    private static String baseDirPath;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this.getApplicationContext();

        Timber.plant(new Timber.DebugTree());

        initDrawerImageLoader();
        initAppStorageDirectory();

        VideoManager.getInstance().init();
    }

    public static Context getContext() {
        return context;
    }

    public static String getBaseDirPath() {
        return baseDirPath;
    }

    private void initDrawerImageLoader() {
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                ImageManager.getInstance().getPicasso()
                        .load(uri)
                        .placeholder(placeholder)
                        .into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                ImageManager.getInstance().getPicasso()
                        .cancelRequest(imageView);
            }
        });
    }

    private void initAppStorageDirectory() {
        File baseDir = new File(Environment.getExternalStorageDirectory() + "/" + getPackageName());
        boolean success = true;
        if (!baseDir.exists()) {
            success = baseDir.mkdir()
                    && baseDir.setWritable(true)
                    && baseDir.setReadable(true);
        }
        if (success) {
            baseDirPath = baseDir.getAbsolutePath() + "/";
        } else {
            throw new RuntimeException("Can not create base directory. Permission denied.");
        }
    }
}
