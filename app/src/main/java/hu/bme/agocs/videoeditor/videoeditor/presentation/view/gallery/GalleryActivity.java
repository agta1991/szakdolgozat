package hu.bme.agocs.videoeditor.videoeditor.presentation.view.gallery;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;

import com.hannesdorfmann.mosby.mvp.lce.MvpLceActivity;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import hu.bme.agocs.videoeditor.videoeditor.R;
import hu.bme.agocs.videoeditor.videoeditor.data.entity.MediaObject;
import hu.bme.agocs.videoeditor.videoeditor.presentation.presenter.GalleryPresenter;
import hu.bme.agocs.videoeditor.videoeditor.presentation.view.gallery.adapter.GalleryAdapter;

/**
 * Created by Agócs Tamás on 2015. 12. 05..
 */
public class GalleryActivity extends MvpLceActivity<LinearLayout, ArrayList<MediaObject>, IGalleryActivity, GalleryPresenter> implements IGalleryActivity, GalleryAdapter.OnItemClickListener {


    @Bind(R.id.galleryToolbar)
    Toolbar galleryToolbar;
    @Bind(R.id.galleryGridRV)
    RecyclerView galleryGridRV;

    private GalleryAdapter adapter;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        ButterKnife.bind(this);

        setSupportActionBar(galleryToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Gallery of finished projects");

        adapter = new GalleryAdapter(this);
        galleryGridRV.setLayoutManager(new GridLayoutManager(this, 5, LinearLayoutManager.VERTICAL, false));
        galleryGridRV.setAdapter(adapter);

        loadData(false);
    }

    @Override
    public void setData(ArrayList<MediaObject> data) {
        if (adapter != null) {
            adapter.setData(data);
        }
    }

    @Override
    public void loadData(boolean pullToRefresh) {
        getPresenter().loadMediaInfo();
    }

    @Override
    public void onItemClick(MediaObject selected) {
        getPresenter().onItemSelected(selected);
    }

    @Override
    public void startMediaPlayer(Uri uri) {
        Intent viewIntent = new Intent(Intent.ACTION_VIEW);
        viewIntent.setDataAndType(uri, "video/*");
        startActivity(Intent.createChooser(viewIntent, null));
    }
}
