package hu.bme.agocs.videoeditor.videoeditor.presentation.view.gallery;

import android.support.annotation.NonNull;
import android.widget.LinearLayout;

import com.hannesdorfmann.mosby.mvp.lce.MvpLceActivity;

import java.util.ArrayList;

import hu.bme.agocs.videoeditor.videoeditor.data.entity.MediaObject;
import hu.bme.agocs.videoeditor.videoeditor.presentation.presenter.GalleryPresenter;

/**
 * Created by Agócs Tamás on 2015. 12. 05..
 */
public class GalleryActivity extends MvpLceActivity<LinearLayout, ArrayList<MediaObject>, IGalleryActivity, GalleryPresenter> implements IGalleryActivity {


    @Override
    protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
        return e.getMessage();
    }

    @NonNull
    @Override
    public GalleryPresenter createPresenter() {
        return new GalleryPresenter();
    }

    @Override
    public void setData(ArrayList<MediaObject> data) {

    }

    @Override
    public void loadData(boolean pullToRefresh) {
        getPresenter().loadMediaInfo();
    }
}
