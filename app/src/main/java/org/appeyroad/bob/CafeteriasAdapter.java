package org.appeyroad.bob;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CafeteriasAdapter extends BaseAdapter {

    public static final int ALL = 0;
    public static final int BOOKMARK = 1;

    public static final int GRID = 0;
    public static final int LIST = 1;

    public static final int FULL = 0;
    public static final int SIMPLE = 1;

    private int dataType;
    private int itemType;
    private int viewMode;
    private Context context;
    private List<Cafeteria> cafeterias;
    private ColorPicker colorPicker;

    public CafeteriasAdapter(Context context, int dataType) {
        this.context = context;
        this.dataType = dataType;
        itemType = Preferences.getItemType(context, dataType);
        viewMode = Preferences.getViewMode(context, dataType);
        cafeterias = new ArrayList<>();
        colorPicker = new ColorPicker(context);
        reloadData();
    }

    @Override
    public int getCount() {
        return cafeterias.size();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup root) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            int viewId;
            if (viewMode == FULL) {
                viewId = (itemType == GRID) ? R.layout.item_grid_cafeteria : R.layout.item_list_cafeteria;
            } else {
                viewId = R.layout.item_simple_cafeteria;
            }
            convertView = inflater.inflate(viewId, root, false);
        }

        Cafeteria cafeteria = getItem(position);
        DatabaseHelper helper = DatabaseHelper.getInstance(context);
        DailyMenu dailyMenu = helper.getMenu(cafeteria, Date.today());
        TextView name = (TextView)convertView.findViewById(R.id.name);
        TextView breakfast;
        TextView lunch;
        TextView dinner;
        if (viewMode == FULL) {
            breakfast = (TextView)convertView.findViewById(R.id.breakfast);
            lunch = (TextView)convertView.findViewById(R.id.lunch);
            dinner = (TextView)convertView.findViewById(R.id.dinner);
            if (dailyMenu == null) {
                dailyMenu = new DailyMenu();
                dailyMenu.setContents("NULL|NULL|NULL");
            }
            formatTextView(breakfast, context.getString(R.string.breakfast), dailyMenu.getContent(DailyMenu.BREAKFAST));
            formatTextView(lunch, context.getString(R.string.lunch), dailyMenu.getContent(DailyMenu.LUNCH));
            formatTextView(dinner, context.getString(R.string.dinner), dailyMenu.getContent(DailyMenu.DINNER));
        }

        name.setText(cafeteria.getName());

        int code = colorPicker.getCode(position);
        convertView.setTag(colorPicker.getColor(code));
        convertView.setBackgroundDrawable(colorPicker.getTile(code));

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Cafeteria getItem(int position) {
        return cafeterias.get(position);
    }

    public void reloadData() {
        cafeterias.clear();
        notifyDataSetChanged();

        DatabaseHelper helper = DatabaseHelper.getInstance(context);
        cafeterias.addAll((dataType == ALL) ?
                helper.getAllCafeterias() : helper.getBookmarkedCafeterias());
        Collections.sort(cafeterias, new Comparator<Cafeteria>() {
            @Override
            public int compare(Cafeteria lhs, Cafeteria rhs) {
                return lhs.getCode().compareTo(rhs.getCode());
            }
        });
        notifyDataSetChanged();
    }

    public void formatTextView(TextView textView, String time, String menu) {
        textView.setText(time + " " + menu.replaceAll("<.+?>", ""));
        if (menu.equals("")) {
            textView.setText(context.getString(R.string.unknown));
            textView.setTextColor(context.getResources().getColor(R.color.black26));
        } else {
            textView.setTextColor(context.getResources().getColor(R.color.black87));
        }
    }

    public List<Cafeteria> getCafeterias() {
        return cafeterias;
    }

}