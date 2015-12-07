package hu.bme.agocs.videoeditor.videoeditor.presentation.presenter;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import java.util.ArrayList;

import hu.bme.agocs.videoeditor.videoeditor.data.ContentManager;
import hu.bme.agocs.videoeditor.videoeditor.data.VideoManager;
import hu.bme.agocs.videoeditor.videoeditor.data.entity.MediaObject;
import hu.bme.agocs.videoeditor.videoeditor.presentation.view.editor.IEditorActivity;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Agócs Tamás on 2015. 11. 27..
 */
public class EditorPresenter extends MvpBasePresenter<IEditorActivity> {

    private CompositeSubscription subscriptions;

    @Override
    public void attachView(IEditorActivity view) {
        super.attachView(view);
        if (subscriptions == null) {
            subscriptions = new CompositeSubscription();
        }
    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        if (subscriptions != null) {
            subscriptions.unsubscribe();
        }
    }

    public void insertNewPictureIntoWorkbench(String picPath) {
        //Subscription subscription =
    }

    public void insertNewAudioIntoWorkbench(String audioPath) {

    }

    public void insertNewVideoIntoWorkbench(String videoPath) {
        Subscription subscription = ContentManager.getInstance().processNewVideoImport(videoPath)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mediaObject -> {
                    if (isViewAttached()) {
                        getView().informWorkbenchFragment();
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                });
        subscriptions.add(subscription);
    }

    public void clearTimeLine() {
        Subscription subscription = ContentManager.getInstance()
                .clearTimeLine()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    if (isViewAttached()) {
                        getView().clearTimeLineAdapter();
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                });
        subscriptions.add(subscription);
    }

    public void startConcatTask(ArrayList<MediaObject> timeLine) {
        if (isViewAttached()) {
            getView().showProgressDialog(true, timeLine.size() + 1);
        }
        Subscription subscription = VideoManager.getInstance()
                .concatMediaObjects(timeLine)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mediaObject -> {
                    if (isViewAttached()) {
                        getView().showProgressDialog(false, 0);
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                });
        subscriptions.add(subscription);
    }
}
