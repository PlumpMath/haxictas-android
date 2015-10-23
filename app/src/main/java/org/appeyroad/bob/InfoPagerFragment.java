package org.appeyroad.bob;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Guanadah on 2015-01-29.
 */
public class InfoPagerFragment extends Fragment {

    private Activity activity;
    private Cafeteria cafeteria;
    private DailyMenu dailyMenu;
    private Date date;

    public static InfoPagerFragment create(Cafeteria cafeteria, DailyMenu dailyMenu, int backgroundColor) {
        InfoPagerFragment fragment = new InfoPagerFragment();
        Bundle args = new Bundle();
        args.putParcelable("CAFETERIA", cafeteria);
        args.putParcelable("MENU", dailyMenu);
        args.putInt("COLOR", backgroundColor);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        Bundle args = getArguments();

        cafeteria = args.getParcelable("CAFETERIA");
        dailyMenu = args.getParcelable("MENU");
        date = dailyMenu.getDate();
        TextView dateTitle = (TextView)view.findViewById(R.id.date_title);
        RelativeLayout breakfastLabel = (RelativeLayout)view.findViewById(R.id.breakfast_label);
        RelativeLayout lunchLabel = (RelativeLayout)view.findViewById(R.id.lunch_label);
        RelativeLayout dinnerLabel = (RelativeLayout)view.findViewById(R.id.dinner_label);
        ListView breakfastList = (ListView)view.findViewById(R.id.breakfast_list);
        ListView lunchList = (ListView)view.findViewById(R.id.lunch_list);
        ListView dinnerList = (ListView)view.findViewById(R.id.dinner_list);

        view.findViewById(R.id.menu_card).setBackgroundColor(args.getInt("COLOR"));

        dateTitle.setText(getPageTitle());
        setLabel(breakfastLabel, getString(R.string.breakfast), cafeteria.getBreakfastTime());
        setLabel(lunchLabel, getString(R.string.lunch), cafeteria.getLunchTime());
        setLabel(dinnerLabel, getString(R.string.dinner), cafeteria.getDinnerTime());
        breakfastList.setAdapter(new MenuListAdapter(dailyMenu.getContent(DailyMenu.BREAKFAST)));
        lunchList.setAdapter(new MenuListAdapter(dailyMenu.getContent(DailyMenu.LUNCH)));
        dinnerList.setAdapter(new MenuListAdapter(dailyMenu.getContent(DailyMenu.DINNER)));

        return view;
    }

    public String getPageTitle() {
        Date today = Date.today();
        Date yesterday = Date.today();
        yesterday.add(Date.DAY_OF_MONTH, -1);
        Date tomorrow = Date.today();
        tomorrow.add(Date.DAY_OF_MONTH, 1);

        if (date.equals(today)) {
            return activity.getString(R.string.today);
        } else if (date.equals(yesterday)) {
            return activity.getString(R.string.yesterday);
        } else if (date.equals(tomorrow)) {
            return activity.getString(R.string.tomorrow);
        }

        return date.toString()
                .replaceAll("[-　]", " ")
                .replaceAll("[0-9]{4} ", "")
                .replaceAll("요일", "");
    }

    public void setLabel(RelativeLayout container, String codeName, String openTime) {
        TextView code = (TextView)container.findViewById(R.id.code);
        TextView time = (TextView)container.findViewById(R.id.time);
        code.setText(codeName);
        time.setText(formatTimeString(openTime));
    }

    public String formatTimeString(String string) {
        if (string.equals("")) {
            return getString(R.string.unknown);
        }

        final String weekendOpenTime = getString(R.string.weekend) + " [^ ]+";

        int weekDay = date.get(Calendar.DAY_OF_WEEK);
        boolean isWeekend = weekDay == Calendar.SATURDAY || weekDay == Calendar.SUNDAY;
        if (isWeekend) {
            string = string.replaceAll(".*(" + weekendOpenTime + ".*)", "$1");
        } else {
            string = string.replaceAll(weekendOpenTime, "");
        }
        return string;
    }

    public Date getDate() {
        return date;
    }

    private class MenuListAdapter extends BaseAdapter {

        private static final int PRICE = 0;
        private static final int ITEM = 1;
        private static final String ITEM_SEPARATOR = "<";
        private static final String FIELD_SEPARATOR = ">";

        private List<String> data;

        MenuListAdapter(String source) {
            data = new ArrayList<>();
            if (source.contains(ITEM_SEPARATOR)) {
                for (String e : source.split(ITEM_SEPARATOR)) {
                    if (!e.equals("")) data.add(e);
                }
            } else {
                data.add("");
            }
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public String getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(activity);
                convertView = inflater.inflate(R.layout.item_list_menu, parent, false);
            }
            TextView item = (TextView)convertView.findViewById(R.id.item);
            TextView price = (TextView)convertView.findViewById(R.id.price);

            String[] fields = getItem(position).split(FIELD_SEPARATOR);

            if (fields.length > ITEM) {
                price.setText(fields[PRICE]);
                item.setText(fields[ITEM]);
            } else {
                item.setText(fields[PRICE]);
                price.setVisibility(View.GONE);
            }

            if (item.getText().toString().equals("")) {
                item.setText(getString(R.string.unknown));
                item.setTextColor(getResources().getColor(R.color.black26));
                item.setGravity(Gravity.CENTER);
            } else {
                item.setTextColor(getResources().getColor(R.color.black87));
            }

            return convertView;
        }
    }
}