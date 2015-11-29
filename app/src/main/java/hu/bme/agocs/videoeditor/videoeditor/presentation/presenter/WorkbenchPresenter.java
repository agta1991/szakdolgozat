package hu.bme.agocs.videoeditor.videoeditor.presentation.presenter;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import java.util.ArrayList;

import hu.bme.agocs.videoeditor.videoeditor.data.entity.MediaObject;
import hu.bme.agocs.videoeditor.videoeditor.presentation.view.editor.IWorkbenchFragment;

/**
 * Created by Agócs Tamás on 2015. 11. 29..
 */
public class WorkbenchPresenter extends MvpBasePresenter<IWorkbenchFragment> {

    public void loadWorkbench() {
        if (isViewAttached()) {
            getView().showLoading(false);
        }
        ArrayList<MediaObject> data = new ArrayList<>();
        data.add(new MediaObject());
        data.add(new MediaObject());
        data.add(new MediaObject());
        if (isViewAttached()) {
            getView().setData(data);
            getView().showContent();
        }
    }
}
