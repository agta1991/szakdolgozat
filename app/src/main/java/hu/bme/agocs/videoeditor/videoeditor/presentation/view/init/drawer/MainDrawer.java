package hu.bme.agocs.videoeditor.videoeditor.presentation.view.init.drawer;

import android.app.Activity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import hu.bme.agocs.videoeditor.videoeditor.R;
import hu.bme.agocs.videoeditor.videoeditor.presentation.view.init.MainActivity;

/**
 * Created by Agócs Tamás on 2015. 12. 05..
 */
public class MainDrawer {

    public static final int EDITOR = 0;
    public static final int GALLERY = 1;
    public static final int ABOUT = 2;

    private Drawer drawer;

    public MainDrawer(MainActivity activity, Toolbar toolbar) {
        initDrawer(activity, toolbar);
        configureEvents(activity);
    }

    public void initDrawer(Activity activity, Toolbar toolbar) {
        drawer = new DrawerBuilder(activity)
                .withRootView(R.id.drawer_container)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .withToolbar(toolbar)
                .withFullscreen(false)
                .addDrawerItems(
                        new PrimaryDrawerItem().withIdentifier(EDITOR).withName("Editor").withIcon(R.drawable.ic_mode_edit_white_24dp).withSetSelected(false),
                        new PrimaryDrawerItem().withIdentifier(GALLERY).withName("Gallery").withIcon(R.drawable.ic_grid_on_white_24dp).withSetSelected(false),
                        new PrimaryDrawerItem().withIdentifier(ABOUT).withName("About").withIcon(R.drawable.ic_info_outline_white_24dp).withSetSelected(false)
                ).build();
    }

    public void configureEvents(MainActivity activity) {
        if (drawer == null) {
            return;
        }
        drawer.setOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                drawer.closeDrawer();
                switch (drawerItem.getIdentifier()) {
                    case EDITOR:
                        activity.getPresenter().editorClicked();
                        return true;
                    case GALLERY:
                        activity.getPresenter().galleryClicked();
                        return true;
                    case ABOUT:
                        activity.getPresenter().aboutClicked();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }
}
