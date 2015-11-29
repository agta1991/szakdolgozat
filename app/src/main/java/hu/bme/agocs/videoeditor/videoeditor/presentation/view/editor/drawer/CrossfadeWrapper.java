package hu.bme.agocs.videoeditor.videoeditor.presentation.view.editor.drawer;

import com.mikepenz.crossfader.Crossfader;
import com.mikepenz.materialdrawer.interfaces.ICrossfader;

/**
 * Created by Agócs Tamás on 2015. 11. 27..
 */
public class CrossfadeWrapper implements ICrossfader {
    private Crossfader mCrossfader;

    public CrossfadeWrapper(Crossfader crossfader) {
        this.mCrossfader = crossfader;
    }

    @Override
    public void crossfade() {
        mCrossfader.crossFade();
    }

    @Override
    public boolean isCrossfaded() {
        return mCrossfader.isCrossFaded();
    }
}