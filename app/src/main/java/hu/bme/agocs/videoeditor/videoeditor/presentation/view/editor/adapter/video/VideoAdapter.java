package hu.bme.agocs.videoeditor.videoeditor.presentation.view.editor.adapter.video;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;

import hu.bme.agocs.videoeditor.videoeditor.R;
import hu.bme.agocs.videoeditor.videoeditor.data.Constants;
import hu.bme.agocs.videoeditor.videoeditor.presentation.view.editor.adapter.ItemTouchHelperAdapter;
import hu.bme.agocs.videoeditor.videoeditor.presentation.view.editor.adapter.OnDragActionListener;

/**
 * Created by Agócs Tamás on 2015. 11. 28..
 */
public class VideoAdapter extends RecyclerView.Adapter<VideoChannelItemViewHolder> implements ItemTouchHelperAdapter {


    private ArrayList<Integer> data = new ArrayList<>();
    private OnDragActionListener dragActionListener;
    private float scale;

    public VideoAdapter(OnDragActionListener dragActionListener) {
        super();
        this.dragActionListener = dragActionListener;
        scale = Constants.VIDEO_CHANNEL_SCALE;
    }

    public VideoAdapter(ArrayList<Integer> data, OnDragActionListener dragActionListener) {
        super();
        this.data = data;
        this.dragActionListener = dragActionListener;
        scale = Constants.VIDEO_CHANNEL_SCALE;
    }

    @Override
    public VideoChannelItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_video_channel, parent, false);
        return new VideoChannelItemViewHolder(view, dragActionListener);
    }

    @Override
    public void onBindViewHolder(VideoChannelItemViewHolder holder, int position) {
        ViewGroup.LayoutParams params = holder.videoChannelCard.getLayoutParams();
        params.width = (int) (scale * data.get(position));
        holder.videoChannelCard.setLayoutParams(params);
        holder.videoChannelCard.requestLayout();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(data, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    public void setData(ArrayList<Integer> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void setScale(float scale) {
        this.scale = scale;
        notifyDataSetChanged();
    }

    public void addOuterDragItem(Integer item, int position) {
        data.add(position, item);
        notifyItemInserted(position);
    }

    public void removeOuterDragItem(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }
}
