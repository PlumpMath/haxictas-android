package org.appeyroad.bob;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.LayoutInflater;

public class ColorPicker {

    public static final int COLOR_COUNT = 8;

    private static int[] colors;

    private Context context;

    public ColorPicker(Context context) {
        this.context = context;
        if (colors == null) {
            colors = new int[] {
                    context.getResources().getColor(R.color.tile_00),
                    context.getResources().getColor(R.color.tile_01),
                    context.getResources().getColor(R.color.tile_02),
                    context.getResources().getColor(R.color.tile_03),
                    context.getResources().getColor(R.color.tile_04),
                    context.getResources().getColor(R.color.tile_05),
                    context.getResources().getColor(R.color.tile_06),
                    context.getResources().getColor(R.color.tile_07)
            };
            if (colors.length != COLOR_COUNT) {
                throw new AssertionError();
            }
        }
    }

    public int getCode(int position) {
        return position;
    }

    public int getColor(int code) {
        return colors[Math.abs(code) % COLOR_COUNT];
    }

    public Drawable getTile(int code) {
        Drawable tile = context.getResources().getDrawable(R.drawable.bg_tile);
        if (tile != null) {
            ((GradientDrawable)((LayerDrawable)tile).getDrawable(0)).setStroke(
                    (int) (
                            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                    2,
                                    context.getResources().getDisplayMetrics())
                    ),
                    getColor(code)
            );
            if (Build.VERSION.SDK_INT >= 21) {
                return new RippleDrawable(
                        new ColorStateList(new int[][] {}, new int[] {
                                context.getResources().getColor(R.color.white12)
                        }),
                        tile,
                        null
                );
            }
        }
        return tile;
    }

}
