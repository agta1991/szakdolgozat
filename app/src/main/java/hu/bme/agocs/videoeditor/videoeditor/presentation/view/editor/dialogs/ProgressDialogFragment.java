package hu.bme.agocs.videoeditor.videoeditor.presentation.view.editor.dialogs;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import at.grabner.circleprogress.CircleProgressView;
import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import hu.bme.agocs.videoeditor.videoeditor.R;
import hu.bme.agocs.videoeditor.videoeditor.data.event.ProgressEvent;
import timber.log.Timber;

/**
 * Created by Agócs Tamás on 2015. 12. 06..
 */
public class ProgressDialogFragment extends ProgressDialog {


    @Bind(R.id.progressView)
    CircleProgressView progressView;
    @Bind(R.id.progressMessageTV)
    TextView progressMessageTV;

    private int actualTask = 1;
    private int taskCount = 0;

    public ProgressDialogFragment(Context context) {
        super(context);
    }

    public ProgressDialogFragment(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getLayoutInflater().inflate(R.layout.dialog_progress, null);
        setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        progressView.setMaxValue(100);
        progressView.spin();
    }

    public void setTaskCount(int taskCount) {
        this.taskCount = taskCount;
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void onEventMainThread(ProgressEvent event) {
        Timber.d(String.valueOf(event.getProgress()));
        if (event.isFinished()) {
            actualTask++;
            progressView.spin();
            progressMessageTV.setText("Processing " + actualTask + "/" + taskCount);
        } else {
            int value = (int) Math.min(event.getProgress() * 100, 100);
            progressView.setValueAnimated(value);
        }
    }
}
