package hu.bme.agocs.videoeditor.videoeditor.presentation.view.editor.drawer;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import com.mikepenz.crossfadedrawerlayout.view.CrossfadeDrawerLayout;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.MiniDrawer;
import com.mikepenz.materialdrawer.adapter.BaseDrawerAdapter;
import com.mikepenz.materialdrawer.adapter.DrawerAdapter;
import com.mikepenz.materialdrawer.interfaces.ICrossfader;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;

import java.util.ArrayList;

import hu.bme.agocs.videoeditor.videoeditor.R;
import hu.bme.agocs.videoeditor.videoeditor.presentation.view.editor.EditorActivity;

/**
 * Created by Agócs Tamás on 2015. 11. 27..
 */
public class EditorDrawer {

    private static final int IMPORT = 0;

    private static final int AUDIO_IMPORT = 1;
    private static final int VIDEO_IMPORT = 2;
    private static final int PICTURE_IMPORT = 3;


    private final EditorActivity activity;
    private final Toolbar toolbar;
    private CrossfadeDrawerLayout crossfadeDrawerLayout;
    private BaseDrawerAdapter adapter;

    private PrimaryDrawerItem importHeader;
    private IDrawerItem importVideoHeader;
    private IDrawerItem importVideoRecord;
    private IDrawerItem importVideoBrowse;
    private IDrawerItem importPictureHeader;
    private IDrawerItem importPictureRecord;
    private IDrawerItem importPictureBrowse;
    private IDrawerItem importSoundHeader;
    private IDrawerItem importSoundRecord;
    private IDrawerItem importSoundBrowse;


    public EditorDrawer(@NonNull EditorActivity activity, @NonNull Toolbar toolbar) {
        this.activity = activity;
        this.toolbar = toolbar;
        crossfadeDrawerLayout = new CrossfadeDrawerLayout(activity);
        adapter = new DrawerAdapter();
        initSubMenuItems();
        showImportSubItems();
        configureDrawer();
    }

    private void initSubMenuItems() {
        //Import submenu
        importHeader = new PrimaryDrawerItem().withLevel(0).withIcon(R.drawable.ic_import_export_white_24dp).withName("Import");

        importVideoHeader = new SecondaryDrawerItem().withLevel(1).withIcon(R.drawable.ic_video_library_white_24dp).withName("Video").withIdentifier(VIDEO_IMPORT);
        importVideoRecord = new SecondaryDrawerItem().withLevel(2).withIcon(R.drawable.ic_photo_camera_white_24dp).withName("Record new video");
        importVideoBrowse = new SecondaryDrawerItem().withLevel(2).withIcon(R.drawable.ic_folder_open_white_24dp).withName("Select video file");

        importPictureHeader = new SecondaryDrawerItem().withLevel(1).withIcon(R.drawable.ic_photo_library_white_24dp).withName("Picture").withIdentifier(PICTURE_IMPORT);
        importPictureRecord = new SecondaryDrawerItem().withLevel(2).withIcon(R.drawable.ic_photo_camera_white_24dp).withName("Take a picture");
        importPictureBrowse = new SecondaryDrawerItem().withLevel(2).withIcon(R.drawable.ic_folder_open_white_24dp).withName("Select picture file");

        importSoundHeader = new SecondaryDrawerItem().withLevel(1).withIcon(R.drawable.ic_library_music_white_24dp).withName("Audio").withIdentifier(AUDIO_IMPORT);
        importSoundRecord = new SecondaryDrawerItem().withLevel(2).withIcon(R.drawable.ic_mic_white_24dp).withName("Record audio");
        importSoundBrowse = new SecondaryDrawerItem().withLevel(2).withIcon(R.drawable.ic_folder_open_white_24dp).withName("Select audio file");
    }

    private void configureDrawer() {
        Drawer editorMenu = new DrawerBuilder()
                .withActivity(activity)
                .withToolbar(toolbar)
                .withDrawerLayout(crossfadeDrawerLayout)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .withDrawerWidthDp(64)
                .addDrawerItems(
                        new PrimaryDrawerItem().withLevel(1).withIcon(R.drawable.ic_import_export_white_24dp).withName("Import"),
                        new DividerDrawerItem(),

                        new SecondaryDrawerItem().withLevel(1).withIcon(R.drawable.ic_video_library_white_24dp).withName("Video").withSelectable(false),
                        new SecondaryDrawerItem().withLevel(2).withIcon(R.drawable.ic_photo_camera_white_24dp).withName("Record new video"),
                        new SecondaryDrawerItem().withLevel(2).withIcon(R.drawable.ic_folder_open_white_24dp).withName("Select video file").withIdentifier(VIDEO_IMPORT),
                        new DividerDrawerItem(),

                        new SecondaryDrawerItem().withLevel(1).withIcon(R.drawable.ic_photo_library_white_24dp).withName("Picture").withSelectable(false),
                        new SecondaryDrawerItem().withLevel(2).withIcon(R.drawable.ic_photo_camera_white_24dp).withName("Take a picture"),
                        new SecondaryDrawerItem().withLevel(2).withIcon(R.drawable.ic_folder_open_white_24dp).withName("Select picture file").withIdentifier(PICTURE_IMPORT),
                        new DividerDrawerItem(),

                        new SecondaryDrawerItem().withLevel(1).withIcon(R.drawable.ic_library_music_white_24dp).withName("Audio").withSelectable(false),
                        new SecondaryDrawerItem().withLevel(2).withIcon(R.drawable.ic_mic_white_24dp).withName("Record audio"),
                        new SecondaryDrawerItem().withLevel(2).withIcon(R.drawable.ic_folder_open_white_24dp).withName("Select audio file").withIdentifier(AUDIO_IMPORT),
                        new DividerDrawerItem()
                )
                .build();

        crossfadeDrawerLayout.setMaxWidthPx(DrawerUIUtils.getOptimalDrawerWidth(activity));

        MiniDrawer miniDrawer = new MiniDrawer().withDrawer(editorMenu);
        View mini = miniDrawer.build(activity);
        crossfadeDrawerLayout.getSmallView().addView(mini, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        miniDrawer.withCrossFader(new ICrossfader() {
            @Override
            public void crossfade() {
                boolean isFaded = isCrossfaded();
                crossfadeDrawerLayout.crossfade(400);

                //only close the drawer if we were already faded and want to close it now
                if (isFaded) {
                    editorMenu.getDrawerLayout().closeDrawer(GravityCompat.START);
                }
            }

            @Override
            public boolean isCrossfaded() {
                return crossfadeDrawerLayout.isCrossfaded();
            }
        });

        editorMenu.setOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                editorMenu.closeDrawer();
                switch (drawerItem.getIdentifier()) {
                    case VIDEO_IMPORT:
                        activity.showVideoPickerDialog();
                        return true;
                    case AUDIO_IMPORT:
                        activity.showAudioPickerDialog();
                        return true;
                    case PICTURE_IMPORT:
                        activity.showPicturePickerDialog();
                        return true;
                    default:
                }
                return false;
            }
        });
    }

    private void showImportSubItems() {
        adapter.clearDrawerItems();
        adapter.addDrawerItems(
                importHeader,
                importVideoHeader,
                importVideoRecord,
                importVideoBrowse,
                importPictureHeader,
                importPictureRecord,
                importPictureBrowse,
                importSoundHeader,
                importSoundRecord,
                importSoundBrowse
        );
    }


}
