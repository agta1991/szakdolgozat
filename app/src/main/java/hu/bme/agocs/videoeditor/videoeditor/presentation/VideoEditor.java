package hu.bme.agocs.videoeditor.videoeditor.presentation;

import android.app.Application;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.squareup.picasso.Picasso;

import hu.bme.agocs.videoeditor.videoeditor.data.ImageManager;
import timber.log.Timber;

/**
 * Created by Agócs Tamás on 2015. 11. 27..
 */
public class VideoEditor extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this.getApplicationContext();

        Timber.plant(new Timber.DebugTree());

        initDrawerImageLoader();
    }

    public static Context getContext() {
        return context;
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
}
