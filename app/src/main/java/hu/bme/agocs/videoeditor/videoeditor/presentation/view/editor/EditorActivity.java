package hu.bme.agocs.videoeditor.videoeditor.presentation.view.editor;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.DragEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SeekBar;

import com.hannesdorfmann.mosby.mvp.MvpActivity;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import hu.bme.agocs.videoeditor.videoeditor.R;
import hu.bme.agocs.videoeditor.videoeditor.data.Constants;
import hu.bme.agocs.videoeditor.videoeditor.presentation.presenter.EditorPresenter;
import hu.bme.agocs.videoeditor.videoeditor.presentation.view.editor.drawer.EditorDrawer;
import hu.bme.agocs.videoeditor.videoeditor.presentation.view.editor.adapter.EditorItemTouchHelperCallback;
import hu.bme.agocs.videoeditor.videoeditor.presentation.view.editor.adapter.OnDragActionListener;
import hu.bme.agocs.videoeditor.videoeditor.presentation.view.editor.adapter.video.VideoAdapter;
import timber.log.Timber;

/**
 * Created by Agócs Tamás on 2015. 11. 27..
 */
public class EditorActivity extends MvpActivity<IEditorActivity, EditorPresenter> implements IEditorActivity, OnDragActionListener, SeekBar.OnSeekBarChangeListener {

    private static final String WORKBENCH_FRAGMENT = "WorkbenchFragment";

    @Bind(R.id.videoChannelRV)
    RecyclerView videoChannelRV;
    @Bind(R.id.editorWorkbenchContainer)
    FrameLayout editorWorkbenchContainer;
    @Bind(R.id.editorVideoChannelZoomSB)
    SeekBar editorVideoChannelZoomSB;
    @Bind(R.id.editorToolbar)
    Toolbar editorToolbar;

    private EditorDrawer editorDrawer;
    private ItemTouchHelper mItemTouchHelper;
    private VideoAdapter videoChannelAdapter;

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
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.editorWorkbenchContainer, WorkbenchFragment.newInstance(), WORKBENCH_FRAGMENT);
        ft.commit();
    }

    private void initLayout() {
        ArrayList<Integer> videos = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            int test = (int) (Math.random() * 5 + 3);
            Timber.d("Data: " + test);
            videos.add(test);
        }

        videoChannelAdapter = new VideoAdapter(this);
        videoChannelAdapter.setData(videos);

        videoChannelRV.setHasFixedSize(true);
        videoChannelRV.setAdapter(videoChannelAdapter);
        videoChannelRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

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
        videoChannelAdapter.addOuterDragItem(1, position);
    }

    @Override
    public void onOuterDragExited(int position) {
        videoChannelAdapter.removeOuterDragItem(position);
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
}
