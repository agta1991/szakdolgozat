package hu.bme.agocs.videoeditor.videoeditor.presentation.view.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.ImageView;

import butterknife.Bind;

/**
 * Created by Agócs Tamás on 2015. 12. 06..
 */
public class RepeatingImageView extends ImageView {

    private Drawable viewDrawable;

    public RepeatingImageView(Context context) {
        super(context);
    }

    public RepeatingImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RepeatingImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RepeatingImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        viewDrawable = drawable;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (viewDrawable != null) {
            Paint paint = new Paint();
            int runningX = 0;
            while (runningX <= canvas.getWidth()) {
                canvas.save();
                canvas.translate(runningX, 0);
                viewDrawable.draw(canvas);
                runningX += viewDrawable.getIntrinsicWidth();
                canvas.restore();
            }
        } else {
            super.onDraw(canvas);
        }
    }


}
