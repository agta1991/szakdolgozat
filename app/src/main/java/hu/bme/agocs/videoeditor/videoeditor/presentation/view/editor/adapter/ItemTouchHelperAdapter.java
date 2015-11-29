package hu.bme.agocs.videoeditor.videoeditor.presentation.view.editor.adapter;

/**
 * Created by Agócs Tamás on 2015. 11. 28..
 */
public interface ItemTouchHelperAdapter {
    
    boolean onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);
}
