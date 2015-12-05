package hu.bme.agocs.videoeditor.videoeditor.presentation.view.gallery;

import android.net.Uri;

import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;

import java.util.ArrayList;

import hu.bme.agocs.videoeditor.videoeditor.data.entity.MediaObject;

/**
 * Created by Agócs Tamás on 2015. 12. 05..
 */
public interface IGalleryActivity extends MvpLceView<ArrayList<MediaObject>> {
    void startMediaPlayer(Uri uri);
}
