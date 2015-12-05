package hu.bme.agocs.videoeditor.videoeditor.data;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;
import com.squareup.picasso.RequestHandler;

import java.io.IOException;

import hu.bme.agocs.videoeditor.videoeditor.presentation.VideoEditor;

/**
 * Created by Agócs Tamás on 2015. 11. 27..
 */
public class ImageManager {

    private static final int CACHE_SIZE = 20 * 1024 * 1024;  //20M

    public static final String VIDEO = "video";

    private static ImageManager instance;
    private Picasso picasso;

    private ImageManager() {
        this.picasso = new Picasso.Builder(VideoEditor.getContext())
                .loggingEnabled(true)
                .addRequestHandler(new VideoRequestHandler())
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

    public class VideoRequestHandler extends RequestHandler {

        @Override
        public boolean canHandleRequest(Request data) {
            return VIDEO.equals(data.uri.getScheme());
        }

        @Override
        public Result load(Request request, int networkPolicy) throws IOException {
            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(
                    request.uri.getHost(), MediaStore.Video.Thumbnails.MINI_KIND);
            return new Result(bitmap, Picasso.LoadedFrom.DISK);
        }
    }
}
