package hu.bme.agocs.videoeditor.videoeditor.presentation.view.editor.adapter;

/**
 * Created by Agócs Tamás on 2015. 11. 28..
 */
public interface ItemTouchHelperViewHolder {

    /**
     * Called when the {@link ItemTouchHelper} first registers an item as being moved or swiped.
     * Implementations should update the item view to indicate it's active state.
     */
    void onItemSelected();


    /**
     * Called when the {@link ItemTouchHelper} has completed the move or swipe, and the active item
     * state should be cleared.
     */
    void onItemClear();

}
