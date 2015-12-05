package hu.bme.agocs.videoeditor.videoeditor.presentation.view.gallery.adapter;

import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import hu.bme.agocs.videoeditor.videoeditor.R;
import hu.bme.agocs.videoeditor.videoeditor.data.ImageManager;
import hu.bme.agocs.videoeditor.videoeditor.data.entity.MediaObject;
import hu.bme.agocs.videoeditor.videoeditor.presentation.view.gallery.GalleryActivity;

/**
 * Created by Agócs Tamás on 2015. 12. 05..
 */
public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private ArrayList<MediaObject> data = new ArrayList<>();
    private OnItemClickListener listener;

    public GalleryAdapter(OnItemClickListener listener) {
        super();
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_gallery, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MediaObject object = data.get(position);
        ImageManager.getInstance().getPicasso()
                .load(Uri.parse(ImageManager.VIDEO + "://" + object.getFilePath()))
                .centerCrop()
                .into(holder.galleryThumbnailIV);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(ArrayList<MediaObject> data) {
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.galleryItemCard)
        CardView galleryItemCard;
        @Bind(R.id.galleryThumbnailIV)
        ImageView galleryThumbnailIV;

        private OnItemClickListener listener;

        public ViewHolder(View itemView, OnItemClickListener listener) {
            super(itemView);
            this.listener = listener;
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onItemClick(data.get(getAdapterPosition()));
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(MediaObject selected);
    }
}
