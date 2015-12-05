package hu.bme.agocs.videoeditor.videoeditor.data;

import com.snappydb.DB;
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

        

    public Observable<ArrayList<MediaObject>> getMediaObjects() {
        return Observable.create(subscriber -> {

            try {
                DB db = new SnappyDB.Builder(VideoEditor.getContext())
                        .directory(Constants.DB_FILE_NAME)
                        .build();

                MediaObject[] mediaObjects = db.getObjectArray(Constants.MEDIA_OBJECTS, MediaObject.class);

                ArrayList<MediaObject> result = new ArrayList<>(Arrays.asList(mediaObjects));

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
                DB db = new SnappyDB.Builder(VideoEditor.getContext())
                        .directory(Constants.DB_FILE_NAME)
                        .build();

                MediaObject[] mediaObjects = db.getObjectArray(Constants.MEDIA_OBJECTS, MediaObject.class);

                ArrayList<MediaObject> currentMediaObjects = new ArrayList<>(Arrays.asList(mediaObjects));

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
