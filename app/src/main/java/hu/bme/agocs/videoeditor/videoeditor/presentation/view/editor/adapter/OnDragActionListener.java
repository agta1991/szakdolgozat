package hu.bme.agocs.videoeditor.videoeditor.presentation.view.editor.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;

/**
 * Created by Agócs Tamás on 2015. 11. 29..
 */
public interface OnDragActionListener {
    void onDragStarted(RecyclerView.ViewHolder holder);

    void onOuterDragEntered(DragEvent event, int position);

    void onOuterDragDropped(DragEvent event, int position);

    void onOuterDragExited(int position);
}
