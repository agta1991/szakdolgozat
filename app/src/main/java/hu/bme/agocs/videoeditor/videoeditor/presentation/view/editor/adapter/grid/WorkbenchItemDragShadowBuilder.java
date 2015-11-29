package hu.bme.agocs.videoeditor.videoeditor.presentation.view.editor.adapter.grid;

import android.graphics.Canvas;
import android.graphics.Point;
import android.view.View;

import hu.bme.agocs.videoeditor.videoeditor.data.Constants;

/**
 * Created by Agócs Tamás on 2015. 11. 29..
 */
public class WorkbenchItemDragShadowBuilder extends View.DragShadowBuilder {


    private Point mScaleFactor;

    public WorkbenchItemDragShadowBuilder(View v) {
        super(v);
    }

    @Override
    public void onProvideShadowMetrics(Point shadowSize, Point touchPoint) {

        int width;
        int height;

        width = (int) (getView().getWidth() * Constants.WORKBENCH_DRAG_SHADOW_SCALE);
        height = (int) (getView().getHeight() * Constants.WORKBENCH_DRAG_SHADOW_SCALE);

        shadowSize.set(width, height);
        mScaleFactor = shadowSize;

        touchPoint.set(width / 2, height / 2);
    }

    @Override
    public void onDrawShadow(Canvas canvas) {
        canvas.scale(mScaleFactor.x / (float) getView().getWidth(), mScaleFactor.y / (float) getView().getHeight());
        getView().draw(canvas);
    }
}
