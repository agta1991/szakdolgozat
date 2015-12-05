package hu.bme.agocs.videoeditor.videoeditor.presentation.presenter;

import android.net.Uri;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import java.io.File;
import java.util.concurrent.ScheduledExecutorService;

import hu.bme.agocs.videoeditor.videoeditor.data.ContentManager;
import hu.bme.agocs.videoeditor.videoeditor.data.entity.MediaObject;
import hu.bme.agocs.videoeditor.videoeditor.presentation.view.gallery.IGalleryActivity;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Agócs Tamás on 2015. 12. 05..
 */
public class GalleryPresenter extends MvpBasePresenter<IGalleryActivity> {

    private Subscription subscription;

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    public void loadMediaInfo() {
        subscription = ContentManager.getInstance()
                .getMediaObjects()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mediaObjects -> {
                    if (isViewAttached()) {
                        getView().setData(mediaObjects);
                        getView().showContent();
                    }
                }, throwable -> {
                    if (isViewAttached()) {
                        getView().showError(throwable, false);
                    }
                });
    }

    public void onItemSelected(MediaObject selected) {
        if (isViewAttached()) {
            getView().startMediaPlayer(Uri.fromFile(new File(selected.getFilePath())));
        }
    }
}
