package hu.bme.agocs.videoeditor.videoeditor.presentation.view.editor.adapter.grid;

import android.content.ClipData;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import hu.bme.agocs.videoeditor.videoeditor.R;
import hu.bme.agocs.videoeditor.videoeditor.data.entity.MediaObject;

/**
 * Created by Agócs Tamás on 2015. 11. 29..
 */
public class WorkbenchAdapter extends RecyclerView.Adapter<WorkbenchAdapter.ViewHolder> {

    private ArrayList<MediaObject> workbenchData = new ArrayList<>();

    public WorkbenchAdapter(ArrayList<MediaObject> workbenchData) {
        super();
        this.workbenchData = workbenchData;
    }

    public WorkbenchAdapter() {
        super();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_workbench_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return workbenchData.size();
    }

    public void setData(@NonNull ArrayList<MediaObject> data) {
        this.workbenchData = data;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        @Bind(R.id.workbenchItemCard)
        CardView workbenchItemCard;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View draggableView) {
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder dragShadowBuilder = new WorkbenchItemDragShadowBuilder(draggableView);
            draggableView.startDrag(data, dragShadowBuilder, workbenchData.get(getAdapterPosition()), 0);
            return true;
        }
    }
}
