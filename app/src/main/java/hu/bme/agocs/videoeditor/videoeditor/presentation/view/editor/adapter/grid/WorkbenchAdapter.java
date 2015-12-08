package hu.bme.agocs.videoeditor.videoeditor.presentation.view.editor.adapter.grid;

import android.content.ClipData;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import hu.bme.agocs.videoeditor.videoeditor.R;
import hu.bme.agocs.videoeditor.videoeditor.data.ImageManager;
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
        MediaObject mediaObject = workbenchData.get(position);
        switch (mediaObject.getType()) {
            case AUDIO:
                holder.workbenchThumbnailIV.setImageResource(R.drawable.ic_music_note);
                holder.workbenchItemTypeIconContainer.setVisibility(View.GONE);
                break;
            case VIDEO:
                ImageManager.getInstance().getPicasso()
                        .load(Uri.parse(ImageManager.VIDEO + "://" + mediaObject.getFilePath()))
                        .resizeDimen(R.dimen.workbench_item_size, R.dimen.workbench_item_size)
                        .centerCrop()
                        .into(holder.workbenchThumbnailIV);
                holder.workbenchItemTypeIcon.setImageResource(R.drawable.ic_movie);
                holder.workbenchItemTypeIconContainer.setVisibility(View.VISIBLE);
                break;
            case PICTURE:
                ImageManager.getInstance().getPicasso()
                        .load(mediaObject.getFilePath())
                        .resizeDimen(R.dimen.workbench_item_size, R.dimen.workbench_item_size)
                        .centerCrop()
                        .into(holder.workbenchThumbnailIV);
                holder.workbenchItemTypeIcon.setImageResource(R.drawable.ic_image_filter_hdr);
                holder.workbenchItemTypeIconContainer.setVisibility(View.VISIBLE);
                break;
        }
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
        @Bind(R.id.workbenchThumbnailIV)
        ImageView workbenchThumbnailIV;
        @Bind(R.id.workbenchItemTypeIcon)
        ImageView workbenchItemTypeIcon;
        @Bind(R.id.workbenchItemTypeIconContainer)
        CardView workbenchItemTypeIconContainer;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            workbenchItemCard.setOnLongClickListener(this);
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
