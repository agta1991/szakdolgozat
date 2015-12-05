package hu.bme.agocs.videoeditor.videoeditor.presentation.presenter;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import hu.bme.agocs.videoeditor.videoeditor.presentation.view.init.IMainActivity;

/**
 * Created by Agócs Tamás on 2015. 12. 04..
 */
public class MainPresenter extends MvpBasePresenter<IMainActivity>{

    public void editorClicked() {
        if(isViewAttached()){
            getView().navigateToEditor();
        }
    }

    public void galleryClicked() {
        if(isViewAttached()){
            getView().navigateToGallery();
        }
    }

    public void aboutClicked() {
        if(isViewAttached()){
            getView().navigateToAbout();
        }
    }
}
