package hu.bme.agocs.videoeditor.videoeditor.presentation.view.editor.dialogs;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
public class ProgressDialogFragment extends DialogFragment {


    @Bind(R.id.progressView)
    CircleProgressView progressView;
    @Bind(R.id.progressMessageTV)
    TextView progressMessageTV;

    private int actualTask = 1;
    private int taskCount = 0;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.dialog_progress, container, false);
        ButterKnife.bind(this, view);


        progressView.setMaxValue(100);
        progressView.spin();

        if (getDialog() != null) {
            getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
            getDialog().setCancelable(false);
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
    }


    public void setTaskCount(int taskCount) {
        this.taskCount = taskCount;
        progressMessageTV.setText("Processing " + actualTask + "/" + taskCount);
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
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
