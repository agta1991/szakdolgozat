package hu.bme.agocs.videoeditor.videoeditor.presentation.view.editor.adapter.video;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import hu.bme.agocs.videoeditor.videoeditor.R;
import hu.bme.agocs.videoeditor.videoeditor.presentation.VideoEditor;
import hu.bme.agocs.videoeditor.videoeditor.presentation.view.editor.adapter.ItemTouchHelperViewHolder;
import hu.bme.agocs.videoeditor.videoeditor.presentation.view.editor.adapter.OnDragActionListener;

/**
 * Created by Agócs Tamás on 2015. 11. 29..
 */
public class VideoChannelItemViewHolder extends RecyclerView.ViewHolder
        implements ItemTouchHelperViewHolder, View.OnLongClickListener, View.OnDragListener {

    private OnDragActionListener dragActionListener;

    @Bind(R.id.videoChannelCard)
    CardView videoChannelCard;
    @Bind(R.id.videoChannelItemTitle)
    TextView videoChannelItemTitle;
    @Bind(R.id.videoChannelItemTime)
    TextView videoChannelItemTime;
    @Bind(R.id.videoChannelThumbnailIV)
    ImageView videoChannelThumbnailIV;
    @Bind(R.id.videoChannelHighlightLayer)
    FrameLayout videoChannelHighlightLayer;

    public VideoChannelItemViewHolder(View itemView, OnDragActionListener dragActionListener) {
        super(itemView);
        this.dragActionListener = dragActionListener;
        ButterKnife.bind(this, itemView);
        itemView.setOnLongClickListener(this);
        itemView.setOnDragListener(this);
    }

    @Override
    public void onItemSelected() {
        videoChannelHighlightLayer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClear() {
        videoChannelHighlightLayer.setVisibility(View.GONE);
    }

    @Override
    public boolean onLongClick(View v) {
        if (dragActionListener != null) {
            dragActionListener.onDragStarted(this);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_ENTERED:
                dragActionListener.onOuterDragEntered(event, getAdapterPosition());
                return true;
            case DragEvent.ACTION_DRAG_STARTED:
                return true;
            default:
                return false;
        }
    }
}