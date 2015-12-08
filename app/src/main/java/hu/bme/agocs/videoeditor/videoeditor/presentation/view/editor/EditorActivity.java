package hu.bme.agocs.videoeditor.videoeditor.presentation.view.editor;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.FractionRes;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SeekBar;

import com.devpaul.filepickerlibrary.FilePickerBuilder;
import com.devpaul.filepickerlibrary.enums.FileScopeType;
import com.hannesdorfmann.mosby.mvp.MvpActivity;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import hu.bme.agocs.videoeditor.videoeditor.R;
import hu.bme.agocs.videoeditor.videoeditor.data.Constants;
import hu.bme.agocs.videoeditor.videoeditor.data.entity.MediaObject;
import hu.bme.agocs.videoeditor.videoeditor.data.enums.MediaType;
import hu.bme.agocs.videoeditor.videoeditor.presentation.presenter.EditorPresenter;
import hu.bme.agocs.videoeditor.videoeditor.presentation.view.editor.adapter.video.VideoChannelItemViewHolder;
import hu.bme.agocs.videoeditor.videoeditor.presentation.view.editor.dialogs.ProgressDialogFragment;
import hu.bme.agocs.videoeditor.videoeditor.presentation.view.editor.drawer.EditorDrawer;
import hu.bme.agocs.videoeditor.videoeditor.presentation.view.editor.adapter.EditorItemTouchHelperCallback;
import hu.bme.agocs.videoeditor.videoeditor.presentation.view.editor.adapter.OnDragActionListener;
import hu.bme.agocs.videoeditor.videoeditor.presentation.view.editor.adapter.video.VideoAdapter;
import ru.bartwell.exfilepicker.ExFilePicker;
import ru.bartwell.exfilepicker.ExFilePickerActivity;
import ru.bartwell.exfilepicker.ExFilePickerParcelObject;
import timber.log.Timber;

/**
 * Created by Agócs Tamás on 2015. 11. 27..
 */
public class EditorActivity extends MvpActivity<IEditorActivity, EditorPresenter> implements IEditorActivity, OnDragActionListener, SeekBar.OnSeekBarChangeListener {

    private static final String WORKBENCH_FRAGMENT = "WorkbenchFragment";

    private static final int VIDEO_REQUEST = 1001;
    private static final int AUDIO_REQUEST = 1002;
    private static final int PICTURE_REQUEST = 1003;

    @Bind(R.id.videoChannelRV)
    RecyclerView videoChannelRV;
    @Bind(R.id.editorWorkbenchContainer)
    FrameLayout editorWorkbenchContainer;
    @Bind(R.id.editorVideoChannelZoomSB)
    SeekBar editorVideoChannelZoomSB;
    @Bind(R.id.editorToolbar)
    Toolbar editorToolbar;
    @Bind(R.id.mainContainer)
    FrameLayout mainContainer;

    private EditorDrawer editorDrawer;
    private ItemTouchHelper mItemTouchHelper;
    private VideoAdapter videoChannelAdapter;

    private WorkbenchFragment workbenchFragment;
    private ProgressDialogFragment progressDialog;

    @NonNull
    @Override
    public EditorPresenter createPresenter() {
        return new EditorPresenter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        ButterKnife.bind(this);

        setSupportActionBar(editorToolbar);
        setTitle("Working Bench");
        editorDrawer = new EditorDrawer(this, editorToolbar);

        initWorkbenchFragment();
        initLayout();
    }

    private void initWorkbenchFragment() {
        workbenchFragment = WorkbenchFragment.newInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.editorWorkbenchContainer, workbenchFragment, WORKBENCH_FRAGMENT);
        ft.commit();
    }

    private void initLayout() {
        ArrayList<MediaObject> videos = new ArrayList<>();

        videoChannelAdapter = new VideoAdapter(this);
        videoChannelAdapter.setData(videos);

        videoChannelRV.setHasFixedSize(true);
        videoChannelRV.setAdapter(videoChannelAdapter);
        videoChannelRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        videoChannelRV.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                MediaObject draggedMedia = (MediaObject) event.getLocalState();
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_ENTERED:
                        Timber.d("Drag event entered.");
                        if (!MediaType.AUDIO.equals(draggedMedia.getType())) {
                            onOuterDragEntered(event, videoChannelAdapter.getItemCount());
                        }
                        return true;
                    case DragEvent.ACTION_DRAG_STARTED:
                        return true;
                    case DragEvent.ACTION_DRAG_EXITED:
                        videoChannelAdapter.removeHighlight();
                        return true;
                }
                return false;
            }
        });

        ItemTouchHelper.Callback callback = new EditorItemTouchHelperCallback(videoChannelAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(videoChannelRV);

        editorVideoChannelZoomSB.setOnSeekBarChangeListener(this);

        /*videoChannelRV.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                if (event.getAction() == DragEvent.ACTION_DRAG_ENTERED) {

                }
                return false;
            }
        });*/
    }

    @Override
    public void onDragStarted(RecyclerView.ViewHolder holder) {
        mItemTouchHelper.startDrag(holder);
    }

    @Override
    public void onOuterDragEntered(DragEvent event, int position) {
        MediaObject draggedMedia = (MediaObject) event.getLocalState();
        if (!MediaType.AUDIO.equals(draggedMedia.getType())) {
            videoChannelAdapter.addOuterDragItem((MediaObject) event.getLocalState(), position);
        } else {
            videoChannelAdapter.highlightItem(position);
        }
    }

    @Override
    public void onOuterDragDropped(DragEvent event, int position) {
        MediaObject draggedMedia = (MediaObject) event.getLocalState();
        if (MediaType.AUDIO.equals(draggedMedia.getType())) {
            showAudioChannelSwapDialog(videoChannelAdapter.getItem(position), draggedMedia);
        }
    }

    @Override
    public void onOuterDragExited(int position) {
        videoChannelAdapter.removeOuterDragItem(position);
        videoChannelAdapter.removeHighlight();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (videoChannelAdapter != null) {
            videoChannelAdapter.setScale(Constants.VIDEO_CHANNEL_SCALE_MIN +
                    (Constants.VIDEO_CHANNEL_SCALE * (progress / 100f)));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void showVideoPickerDialog() {
        Intent intent = new Intent(this, ExFilePickerActivity.class);
        intent.putExtra(ExFilePicker.DISABLE_NEW_FOLDER_BUTTON, true);
        intent.putExtra(ExFilePicker.ENABLE_QUIT_BUTTON, true);
        intent.putExtra(ExFilePicker.SET_FILTER_LISTED, new String[]{"avi", "mp4", "3gp", "mov"});
        startActivityForResult(intent, VIDEO_REQUEST);
    }

    @Override
    public void showAudioPickerDialog() {
        Intent intent = new Intent(this, ExFilePickerActivity.class);
        intent.putExtra(ExFilePicker.DISABLE_NEW_FOLDER_BUTTON, true);
        intent.putExtra(ExFilePicker.ENABLE_QUIT_BUTTON, true);
        intent.putExtra(ExFilePicker.SET_FILTER_LISTED, new String[]{"mp3", "wav"});
        startActivityForResult(intent, AUDIO_REQUEST);
    }

    @Override
    public void showPicturePickerDialog() {
        Intent intent = new Intent(this, ExFilePickerActivity.class);
        intent.putExtra(ExFilePicker.DISABLE_NEW_FOLDER_BUTTON, true);
        intent.putExtra(ExFilePicker.ENABLE_QUIT_BUTTON, true);
        intent.putExtra(ExFilePicker.SET_FILTER_LISTED, new String[]{"jpeg", "jpg", "png", "gif", "bmp", "wbmp"});
        startActivityForResult(intent, PICTURE_REQUEST);
    }

    @Override
    public void informWorkbenchFragment() {
        if (workbenchFragment != null) {
            workbenchFragment.loadData(false);
        }
    }

    @Override
    public void clearTimeLineAdapter() {
        if (videoChannelAdapter != null) {
            videoChannelAdapter.clearData();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clearTimeLine:
                getPresenter().clearTimeLine();
                return true;
            case R.id.renderTimeLine:
                if (videoChannelAdapter != null && videoChannelAdapter.getData().size() > 1) {
                    getPresenter().startConcatTask(videoChannelAdapter.getData());
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ExFilePickerActivity.RESULT_OK) {
            switch (requestCode) {
                case VIDEO_REQUEST:
                    handleVideoPickResult(data.getParcelableExtra(ExFilePickerParcelObject.class.getCanonicalName()));
                    break;
                case AUDIO_REQUEST:
                    handleAudioPickResult(data.getParcelableExtra(ExFilePickerParcelObject.class.getCanonicalName()));
                    break;
                case PICTURE_REQUEST:
                    handlePicturePickResult(data.getParcelableExtra(ExFilePickerParcelObject.class.getCanonicalName()));
                    break;
            }
        }
    }

    private void handlePicturePickResult(ExFilePickerParcelObject result) {
        if (result.count > 0) {
            getPresenter().insertNewPictureIntoWorkbench(result.path.concat(result.names.get(0)));
        }
    }

    private void handleAudioPickResult(ExFilePickerParcelObject result) {
        if (result.count > 0) {
            getPresenter().insertNewAudioIntoWorkbench(result.path.concat(result.names.get(0)));
        }
    }

    private void handleVideoPickResult(ExFilePickerParcelObject result) {
        if (result.count > 0) {
            getPresenter().insertNewVideoIntoWorkbench(result.path.concat(result.names.get(0)));
        }
    }

    @Override
    public void showProgressDialog(boolean isShown, int processCount) {
        if (isShown) {
            if (progressDialog == null || !progressDialog.isVisible()) {
                progressDialog = new ProgressDialogFragment();
                progressDialog.setCancelable(false);
                progressDialog.setTaskCount(processCount);
                progressDialog.show(getSupportFragmentManager(), "ProgressDialog");
            }
        } else {
            if (progressDialog != null && progressDialog.isVisible()) {
                progressDialog.dismiss();
            }
        }
    }

    public void showAudioChannelSwapDialog(MediaObject timelineMedia, MediaObject audio) {
        String timelineMediaName = new File(timelineMedia.getFilePath()).getName();
        String audioName = new File(audio.getFilePath()).getName();
        AlertDialog audioDialog = new AlertDialog.Builder(this)
                .setMessage(getString(R.string.editor_do_you_audio) + timelineMediaName + getString(R.string.editor_with_audio) + audioName + "?")
                .setPositiveButton(R.string.editor_yes, (dialog, which) -> {
                    getPresenter().replaceAudioChannelOnMedia(timelineMedia, audio);
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.editor_no, (dialog, which) -> {
                    videoChannelAdapter.removeHighlight();
                    dialog.dismiss();
                })
                .setCancelable(false)
                .setTitle(R.string.editor_audio_dialog_title)
                .create();
        audioDialog.show();
    }
}
