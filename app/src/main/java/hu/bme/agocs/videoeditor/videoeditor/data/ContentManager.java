package hu.bme.agocs.videoeditor.videoeditor.data;

import com.esotericsoftware.kryo.Kryo;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappyDB;
import com.snappydb.SnappydbException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Timer;

import hu.bme.agocs.videoeditor.videoeditor.data.entity.MediaObject;
import hu.bme.agocs.videoeditor.videoeditor.presentation.VideoEditor;
import rx.Observable;
import rx.SingleSubscriber;
import rx.Subscription;
import timber.log.Timber;

/**
 * Created by Agócs Tamás on 2015. 12. 05..
 */
public class ContentManager {

    public static ContentManager instance;

    public static ContentManager getInstance() {
        if (instance == null) {
            instance = new ContentManager();
        }
        return instance;
    }

    private ContentManager() {
    }

    public Observable<ArrayList<MediaObject>> getMediaObjects() {
        return Observable.create(subscriber -> {

            try {
                DB db = DBFactory.open(VideoEditor.getContext(), Constants.DB_FILE_NAME, new Kryo());

                ArrayList<MediaObject> result = new ArrayList<>();

                if (db.exists(Constants.MEDIA_OBJECTS)) {
                    MediaObject[] mediaObjects = db.getObjectArray(Constants.MEDIA_OBJECTS, MediaObject.class);
                    result.addAll(Arrays.asList(mediaObjects));
                }

                subscriber.onNext(result);

                db.close();

            } catch (SnappydbException e) {
                Timber.e(e, "getMediaObjects");
                subscriber.onError(e);
            }
            subscriber.onCompleted();
        });
    }

    public Observable<Boolean> storeMediaObject(MediaObject mediaObject) {
        return Observable.create(subscriber -> {

            try {
                DB db = DBFactory.open(VideoEditor.getContext(), Constants.DB_FILE_NAME, new Kryo());

                ArrayList<MediaObject> currentMediaObjects = new ArrayList<>();

                if (db.exists(Constants.MEDIA_OBJECTS)) {
                    MediaObject[] mediaObjects = db.getObjectArray(Constants.MEDIA_OBJECTS, MediaObject.class);
                    currentMediaObjects.addAll(Arrays.asList(mediaObjects));
                }

                if (!currentMediaObjects.contains(mediaObject)) {
                    currentMediaObjects.add(mediaObject);
                    subscriber.onNext(true);
                } else {
                    subscriber.onNext(false);
                }

                db.close();

            } catch (SnappydbException e) {
                Timber.e(e, "getMediaObjects");
                subscriber.onError(e);
            }
            subscriber.onCompleted();
        });
    }
}
