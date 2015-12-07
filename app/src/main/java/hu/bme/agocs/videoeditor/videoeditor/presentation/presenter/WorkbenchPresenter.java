package hu.bme.agocs.videoeditor.videoeditor.presentation.presenter;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import java.util.ArrayList;

import hu.bme.agocs.videoeditor.videoeditor.data.ContentManager;
import hu.bme.agocs.videoeditor.videoeditor.data.entity.MediaObject;
import hu.bme.agocs.videoeditor.videoeditor.presentation.view.editor.IWorkbenchFragment;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Agócs Tamás on 2015. 11. 29..
 */
public class WorkbenchPresenter extends MvpBasePresenter<IWorkbenchFragment> {

    Subscription subscription;

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    public void loadWorkbench() {
        if (isViewAttached()) {
            getView().showLoading(false);
        }
        subscription = ContentManager.getInstance().getMediaObjects()
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
}
