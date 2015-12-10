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

import hu.bme.agocs.videoeditor.videoeditor.data.entity.FFmpegInfo;
import hu.bme.agocs.videoeditor.videoeditor.data.entity.MediaObject;
import hu.bme.agocs.videoeditor.videoeditor.data.entity.ProcessUpdate;
import hu.bme.agocs.videoeditor.videoeditor.data.enums.MediaType;
import hu.bme.agocs.videoeditor.videoeditor.data.enums.ProcessUpdateType;
import hu.bme.agocs.videoeditor.videoeditor.presentation.VideoEditor;
import rx.Observable;
import rx.SingleSubscriber;
import rx.Subscription;
import rx.functions.Func1;
import timber.log.Timber;

/**
 * Created by Agócs Tamás on 2015. 12. 05..
 */
public class ContentManager {

    public static ContentManager instance;

    private DB db;

    public static ContentManager getInstance() {
        if (instance == null) {
            instance = new ContentManager();
        }
        return instance;
    }

    private ContentManager() {
        try {
            db = DBFactory.open(VideoEditor.getContext(), Constants.DB_FILE_NAME);
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }

    public synchronized Observable<ArrayList<MediaObject>> getMediaObjects() {
        return Observable.create(subscriber -> {

            try {
                synchronized (db) {
                    ArrayList<MediaObject> result = new ArrayList<>();

                    if (db.exists(Constants.MEDIA_OBJECTS)) {
                        MediaObject[] mediaObjects = db.getObjectArray(Constants.MEDIA_OBJECTS, MediaObject.class);
                        result.addAll(Arrays.asList(mediaObjects));
                    }

                    subscriber.onNext(result);
                }

            } catch (SnappydbException e) {
                Timber.e(e, "getMediaObjects");
                subscriber.onError(e);
            }
            subscriber.onCompleted();
        });
    }

    public synchronized Observable<Boolean> storeMediaObject(MediaObject mediaObject) {
        return Observable.create(subscriber -> {

            try {
                synchronized (db) {
                    ArrayList<MediaObject> currentMediaObjects = new ArrayList<>();

                    if (db.exists(Constants.MEDIA_OBJECTS)) {
                        MediaObject[] mediaObjects = db.getObjectArray(Constants.MEDIA_OBJECTS, MediaObject.class);
                        currentMediaObjects.addAll(Arrays.asList(mediaObjects));
                    }

                    if (!currentMediaObjects.contains(mediaObject)) {
                        currentMediaObjects.add(mediaObject);
                        db.put(Constants.MEDIA_OBJECTS, currentMediaObjects.toArray(new MediaObject[currentMediaObjects.size()]));
                        subscriber.onNext(true);
                    } else {
                        subscriber.onNext(false);
                    }
                }

            } catch (SnappydbException e) {
                Timber.e(e, "storeMediaObject");
                subscriber.onError(e);
            }
            subscriber.onCompleted();
        });
    }

    public synchronized Observable<ArrayList<MediaObject>> getTimeLine() {
        return Observable.create(subscriber -> {

            try {
                synchronized (db) {
                    ArrayList<MediaObject> result = new ArrayList<>();

                    if (db.exists(Constants.TIME_LINE)) {
                        MediaObject[] mediaObjects = db.getObjectArray(Constants.TIME_LINE, MediaObject.class);
                        result.addAll(Arrays.asList(mediaObjects));
                    }

                    subscriber.onNext(result);
                }

            } catch (SnappydbException e) {
                Timber.e(e, "getTimeLine");
                subscriber.onError(e);
            }
            subscriber.onCompleted();
        });
    }

    public synchronized Observable<Boolean> storeTimeLine(ArrayList<MediaObject> timeLine) {
        return Observable.create(subscriber -> {

            try {
                synchronized (db) {
                    db.put(Constants.TIME_LINE, timeLine.toArray(new MediaObject[timeLine.size()]));

                    subscriber.onNext(true);
                }

            } catch (SnappydbException e) {
                Timber.e(e, "storeTimeLine");
                subscriber.onError(e);
            }
            subscriber.onCompleted();
        });
    }

    public synchronized Observable<Boolean> clearTimeLine() {
        return Observable.create(subscriber -> {

            try {
                synchronized (db) {
                    if (db.exists(Constants.TIME_LINE)) {
                        db.del(Constants.TIME_LINE);
                        subscriber.onNext(true);
                    } else {
                        subscriber.onNext(false);
                    }
                }

            } catch (SnappydbException e) {
                Timber.e(e, "clearTimeLine");
                subscriber.onError(e);
            }
            subscriber.onCompleted();
        });
    }


    public Observable<Boolean> processNewVideoImport(String videoPath) {
        return VideoManager.getInstance()
                .analyzeMedia(MediaType.VIDEO, videoPath)
                .flatMap(this::storeMediaObject);
    }

    public Observable<Boolean> processNewAudioImport(String audioPath) {
        return VideoManager.getInstance()
                .analyzeMedia(MediaType.AUDIO, audioPath)
                .flatMap(this::storeMediaObject);
    }

    public Observable<Boolean> processNewPictureImport(String picPath) {
        return VideoManager.getInstance()
                .analyzeMedia(MediaType.PICTURE, picPath)
                .flatMap(this::storeMediaObject);
    }
}
