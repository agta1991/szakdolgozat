package hu.bme.agocs.videoeditor.videoeditor.presentation.view.init;

import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * Created by Agócs Tamás on 2015. 12. 04..
 */
public interface IMainActivity extends MvpView {
    void navigateToEditor();

    void navigateToGallery();

    void navigateToAbout();
}
