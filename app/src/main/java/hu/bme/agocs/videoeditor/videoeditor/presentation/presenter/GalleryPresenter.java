package hu.bme.agocs.videoeditor.videoeditor.presentation.presenter;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import hu.bme.agocs.videoeditor.videoeditor.data.ContentManager;
import hu.bme.agocs.videoeditor.videoeditor.presentation.view.gallery.IGalleryActivity;
import rx.Subscription;

/**
 * Created by Agócs Tamás on 2015. 12. 05..
 */
public class GalleryPresenter extends MvpBasePresenter<IGalleryActivity> {

    private Subscription subscription;

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        if(subscription != null){
            subscription.unsubscribe();
        }
    }

    public void loadMediaInfo() {
        subscription = ContentManager
    }

}
