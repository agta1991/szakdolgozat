package hu.bme.agocs.videoeditor.videoeditor.presentation.view.editor;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.hannesdorfmann.mosby.mvp.lce.MvpLceFragment;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import hu.bme.agocs.videoeditor.videoeditor.R;
import hu.bme.agocs.videoeditor.videoeditor.data.entity.MediaObject;
import hu.bme.agocs.videoeditor.videoeditor.presentation.presenter.WorkbenchPresenter;
import hu.bme.agocs.videoeditor.videoeditor.presentation.view.editor.adapter.grid.WorkbenchAdapter;

/**
 * Created by Agócs Tamás on 2015. 11. 29..
 */
public class WorkbenchFragment extends MvpLceFragment<FrameLayout, ArrayList<MediaObject>, IWorkbenchFragment, WorkbenchPresenter> implements IWorkbenchFragment {

    @Bind(R.id.workbenchRV)
    RecyclerView workbenchRV;

    private WorkbenchAdapter adapter;

    public static WorkbenchFragment newInstance() {
        WorkbenchFragment fragment = new WorkbenchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public WorkbenchPresenter createPresenter() {
        return new WorkbenchPresenter();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new WorkbenchAdapter();

        DisplayMetrics dm = getResources().getDisplayMetrics();
        int columnCount = (int) (dm.widthPixels / (getResources().getDimensionPixelSize(R.dimen.workbench_item_size) * 1.1f));

        workbenchRV.setLayoutManager(new GridLayoutManager(getContext(), columnCount, LinearLayoutManager.VERTICAL, false));
        workbenchRV.setAdapter(adapter);

        loadData(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        if (container == null) {
            return null;
        }

        View rootLayout = inflater.inflate(R.layout.fragment_workbench, container, false);
        ButterKnife.bind(this, rootLayout);

        return rootLayout;
    }

    @Override
    protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
        return e.getMessage();
    }

    @Override
    public void setData(ArrayList<MediaObject> data) {
        adapter.setData(data);
    }

    @Override
    public void loadData(boolean pullToRefresh) {
        getPresenter().loadWorkbench();
    }
}
