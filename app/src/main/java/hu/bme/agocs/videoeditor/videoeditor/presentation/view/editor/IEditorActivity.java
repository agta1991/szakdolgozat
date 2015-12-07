package hu.bme.agocs.videoeditor.videoeditor.presentation.view.editor;

import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * Created by Agócs Tamás on 2015. 11. 27..
 */
public interface IEditorActivity extends MvpView {

    void showVideoPickerDialog();

    void showAudioPickerDialog();

    void showPicturePickerDialog();

    void informWorkbenchFragment();

    void clearTimeLineAdapter();

    void showProgressDialog(boolean isShown, int processCount);
}
