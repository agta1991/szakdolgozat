package hu.bme.agocs.videoeditor.videoeditor.data;

import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import hu.bme.agocs.videoeditor.videoeditor.presentation.VideoEditor;

/**
 * Created by Agócs Tamás on 2015. 11. 27..
 */
public class ImageManager {

    private static final int CACHE_SIZE = 20 * 1024 * 1024;  //20M

    private static ImageManager instance;
    private Picasso picasso;

    private ImageManager() {
        this.picasso = new Picasso.Builder(VideoEditor.getContext())
                .loggingEnabled(true)
                .memoryCache(new LruCache(CACHE_SIZE))
                .build();
    }

    public static ImageManager getInstance() {
        if (instance == null) {
            instance = new ImageManager();
        }
        return instance;
    }

    public Picasso getPicasso() {
        return picasso;
    }
}
