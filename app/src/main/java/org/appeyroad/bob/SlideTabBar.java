package org.appeyroad.bob;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class SlideTabBar extends HorizontalScrollView {

    private static final int INDICATOR_THICKNESS = 2;

    private ArrayList<TextView> children;
    private Context context;
    private LinearLayout container;
    private RelativeLayout root;
    private ImageView indicator;
    private ViewPager viewPager;
    private ViewPager.OnPageChangeListener onPageChangeListener;
    private LayoutInflater inflater;
    private RelativeLayout.LayoutParams params;

    public SlideTabBar(Context context) {
        this(context, null);
    }

    public SlideTabBar(Context context, AttributeSet attrs) {
        this(context, attrs, Resources.getSystem().getIdentifier("horizontalScrollView", "id", "android"));
    }

    public SlideTabBar(final Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        inflater = LayoutInflater.from(context);

        setFillViewport(true);

        root = new RelativeLayout(context);
        addView(root,
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )
        );

        container = new LinearLayout(context);
        container.setOrientation(LinearLayout.HORIZONTAL);
        root.addView(container,
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )
        );

        indicator = new ImageView(context);
        indicator.setImageDrawable(new ColorDrawable(getResources().getColor(R.color.white100)));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics())
        );
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        indicator.setLayoutParams(params);
        root.addView(indicator);

        if (Build.VERSION.SDK_INT >= 21) {
            setBackgroundColor(getResources().getColor(R.color.primary));
            setElevation(TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, INDICATOR_THICKNESS, getResources().getDisplayMetrics()
            ));
        }
    }

    public void setViewPager(final ViewPager viewPager) {
        this.viewPager = viewPager;
        PagerAdapter pagerAdapter = viewPager.getAdapter();

        children = new ArrayList<>();
        for (int i = 0; i < pagerAdapter.getCount(); i++) {
            TextView child = (TextView)inflater.inflate(R.layout.item_tab_title, null, false);
            CharSequence title = pagerAdapter.getPageTitle(i);
            child.setText(title);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.MATCH_PARENT, title.length() + 2
            );
            child.setLayoutParams(params);
            container.addView(child);
            children.add(child);
        }

        for (int i = 0; i < children.size(); i++) {
            final int page = i;
            View child = children.get(page);
            child.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewPager.setCurrentItem(page, true);
                }
            });
        }

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                for (TextView child : children) {
                    child.setTextColor(getResources().getColor(R.color.white70));
                }
                TextView selectedPage = children.get(position);
                selectedPage.setTextColor(getResources().getColor(R.color.white100));

                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageSelected(position);
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                scrollToItem(position, positionOffset);

                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageScrollStateChanged(state);
                }
            }
        });

        viewPager.post(new Runnable() {
            @Override
            public void run() {
                TextView item = children.get(viewPager.getCurrentItem());
                int destMarginLeft = item.getLeft();
                int destMarginRight = container.getWidth() - item.getRight();
                params = (RelativeLayout.LayoutParams) indicator.getLayoutParams();
                params.leftMargin = destMarginLeft;
                params.rightMargin = destMarginRight;
                item.setTextColor(getResources().getColor(R.color.white100));
            }
        });
    }

    public void scrollToItem(int position, float positionOffset) {
        final View item = children.get(position);
        View rightEndItem = children.get((int)Math.ceil(position + positionOffset));
        int destMarginLeft = (int)(
                item.getLeft() + (rightEndItem.getLeft() - item.getLeft()) * positionOffset
        );
        int destMarginRight = container.getWidth() - (int)(
                item.getRight() + (rightEndItem.getRight() - item.getRight()) * positionOffset
        );
        params = (RelativeLayout.LayoutParams)indicator.getLayoutParams();
        params.leftMargin = destMarginLeft;
        params.rightMargin = destMarginRight;
        indicator.requestLayout();
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        this.onPageChangeListener = onPageChangeListener;
    }
}
