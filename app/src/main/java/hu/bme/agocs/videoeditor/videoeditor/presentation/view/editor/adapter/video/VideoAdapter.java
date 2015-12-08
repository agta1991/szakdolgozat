package hu.bme.agocs.videoeditor.videoeditor.presentation.view.editor.adapter.video;

import android.app.Activity;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Parcel;
import android.os.SystemClock;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import hu.bme.agocs.videoeditor.videoeditor.R;
import hu.bme.agocs.videoeditor.videoeditor.data.Constants;
import hu.bme.agocs.videoeditor.videoeditor.data.ImageManager;
import hu.bme.agocs.videoeditor.videoeditor.data.entity.MediaObject;
import hu.bme.agocs.videoeditor.videoeditor.presentation.view.editor.adapter.ItemTouchHelperAdapter;
import hu.bme.agocs.videoeditor.videoeditor.presentation.view.editor.adapter.OnDragActionListener;

/**
 * Created by Agócs Tamás on 2015. 11. 28..
 */
public class VideoAdapter extends RecyclerView.Adapter<VideoChannelItemViewHolder> implements ItemTouchHelperAdapter {


    private ArrayList<MediaObject> data = new ArrayList<>();
    private OnDragActionListener dragActionListener;
    private float scale;

    int highlightedPosition = -1;

    public VideoAdapter(OnDragActionListener dragActionListener) {
        super();
        this.dragActionListener = dragActionListener;
        scale = Constants.VIDEO_CHANNEL_SCALE;
    }

    public VideoAdapter(ArrayList<MediaObject> data, OnDragActionListener dragActionListener) {
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
        MediaObject mediaObject = data.get(position);
        switch (mediaObject.getType()) {
            case AUDIO:
                break;
            case VIDEO:
                ImageManager.getInstance().getPicasso()
                        .load(Uri.parse(ImageManager.VIDEO + "://" + mediaObject.getFilePath()))
                        .placeholder(android.R.drawable.progress_indeterminate_horizontal)
                        .into(holder.videoChannelThumbnailIV);
                break;
            case PICTURE:
                ImageManager.getInstance().getPicasso()
                        .load(mediaObject.getFilePath())
                        .placeholder(android.R.drawable.progress_indeterminate_horizontal)
                        .into(holder.videoChannelThumbnailIV);
                break;
        }
        String[] pathParts = mediaObject.getFilePath().split("/");
        holder.videoChannelItemTitle.setText(pathParts[pathParts.length - 1]);

        if (mediaObject.getMediaInfo() != null && mediaObject.getMediaInfo().getFormat() != null) {
            String duration = mediaObject.getMediaInfo().getFormat().getDuration();
            if (duration != null) {
                double durationSec = Double.parseDouble(duration) * 1000f;
                Date durationDate = new Date();
                durationDate.setTime((long) durationSec);
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SS");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                holder.videoChannelItemTime.setText(sdf.format(durationDate));
            } else {
                holder.videoChannelItemTime.setText("N/A");
            }
        }

        if (position == highlightedPosition) {
            holder.videoChannelInterceptHighlightLayer.setVisibility(View.VISIBLE);
        } else {
            holder.videoChannelInterceptHighlightLayer.setVisibility(View.GONE);
        }

        ViewGroup.LayoutParams params = holder.videoChannelCard.getLayoutParams();
        int newWidth = (int) (scale * 2);
        if (mediaObject.getMediaInfo() != null && mediaObject.getMediaInfo().getFormat() != null) {
            newWidth = (int) (scale * Math.sqrt(Double.parseDouble(mediaObject.getMediaInfo().getFormat().getDuration())));
        }
        params.width = Math.max(newWidth, params.height);
        holder.videoChannelCard.setLayoutParams(params);
        holder.videoChannelCard.requestLayout();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public MediaObject getItem(int position) {
        if (data.size() > position) {
            return data.get(position);
        }
        return null;
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

    public void setData(ArrayList<MediaObject> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void clearData() {
        this.data.clear();
        notifyDataSetChanged();
    }

    public void setScale(float scale) {
        this.scale = scale;
        notifyDataSetChanged();
    }

    public ArrayList<MediaObject> getData() {
        return data;
    }

    public void addOuterDragItem(MediaObject item, int position) {
        if (!data.contains(item)) {
            data.add(position, item);
            notifyItemInserted(position);
        }
    }

    public void removeOuterDragItem(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    public void highlightItem(int position) {
        this.highlightedPosition = position;
        notifyDataSetChanged();
    }

    public void removeHighlight() {
        this.highlightedPosition = -1;
        notifyDataSetChanged();
    }

    public void replaceMedia(MediaObject target, MediaObject with) {
        if (data.contains(target)) {
            int targetPosition = data.indexOf(target);
            data.add(targetPosition, with);
            data.remove(target);
            notifyDataSetChanged();
        }
    }
}
