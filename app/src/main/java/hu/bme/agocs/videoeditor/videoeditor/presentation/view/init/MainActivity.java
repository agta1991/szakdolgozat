package hu.bme.agocs.videoeditor.videoeditor.presentation.view.init;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;

import com.hannesdorfmann.mosby.mvp.MvpActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hu.bme.agocs.videoeditor.videoeditor.R;
import hu.bme.agocs.videoeditor.videoeditor.presentation.presenter.MainPresenter;

/**
 * Created by Agócs Tamás on 2015. 12. 04..
 */
public class MainActivity extends MvpActivity<IMainActivity, MainPresenter> implements IMainActivity {


    @Bind(R.id.mainToolbar)
    Toolbar mainToolbar;


    @OnClick(R.id.mainEditorBtn)
    protected void onEditorBtnClicked() {
        getPresenter().editorClicked();
    }

    @OnClick(R.id.mainGalleryBtn)
    protected void onGalleryBtnClicked() {
        getPresenter().galleryClicked();
    }

    @OnClick(R.id.mainAboutBtn)
    protected void onAboutBtnClicked() {
        getPresenter().aboutClicked();
    }

    @NonNull
    @Override
    public MainPresenter createPresenter() {
        return new MainPresenter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

    }
}
