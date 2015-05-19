package org.appeyroad.bob;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Outline;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageButton;

public class FloatingActionButton extends ImageButton {

    private final static int ELEVATION = 8;

    public FloatingActionButton(Context context) {
        this(context, null);
    }

    public FloatingActionButton(Context context, AttributeSet attrs) {
        this(context, attrs, Resources.getSystem().getIdentifier("imgButton", "id", "android"));
    }

    public FloatingActionButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setBackgroundResource(R.drawable.bg_floating_action_button);
        if (Build.VERSION.SDK_INT >= 21) {
            setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    if (Build.VERSION.SDK_INT >= 21) {
                        outline.setOval(0, 0, view.getWidth(), view.getHeight());
                    }
                }
            });
            setClipToOutline(true);
            setElevation(ELEVATION);
        }
    }

}
