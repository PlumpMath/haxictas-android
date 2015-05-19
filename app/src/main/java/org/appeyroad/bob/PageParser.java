package org.appeyroad.bob;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PageParser {

    public static final int DAYS_RANGE = 3;

    public static final int FINISHED_PARSING = 0;
    public static final int TODAY_DATA_EXISTS = 1;
    public static final int EXCEPTION_OCCURRED = 2;

    private Activity activity;

    public PageParser(Activity activity) {
        this.activity = activity;
    }

    public void getTodayDataForHandler(Handler handler) {
        new ParseTask(handler).execute(Date.today());
    }

    public void getAllData() {
        ArrayList<Date> targetDates = new ArrayList<>();
        for (int i = -DAYS_RANGE; i <= DAYS_RANGE; i++) {
            Date date = Date.today();
            date.add(Date.DAY_OF_MONTH, i);
            targetDates.add(date);
        }
        Date[] datesArray = new Date[targetDates.size()];
        new ParseTask().execute(targetDates.toArray(datesArray));
    }

    private class ParseTask extends AsyncTask<Date, Void, Void> {

        private Handler handler = null;

        public ParseTask() {}
        public ParseTask(Handler handler) {
            this.handler = handler;
        }

        @Override
        protected Void doInBackground(Date[] dates) {
            DatabaseHelper helper = DatabaseHelper.getInstance(activity);
            Elements elements;
            String url;
            Document document;

            try {
                // 오늘의 정보가 이미 있다면
                List<DailyMenu> todayInfo = helper.getMenus(Date.today());
                if (todayInfo != null && todayInfo.size() > 0) {
                    if (handler != null) handler.sendEmptyMessage(TODAY_DATA_EXISTS);
                }

                List<Cafeteria> storedCafeterias;
                List<Cafeteria> newCafeterias = new ArrayList<>();
                List<Cafeteria> aliveCafeterias = new ArrayList<>();

                // 인자로 받은 각각의 날짜에 대하여
                for (Date date : dates) {
                // 그 날의 정보가 완전히 있다면
                    List<DailyMenu> dailyInfo = helper.getMenus(date);
                    if (dailyInfo.size() > 0 && dailyInfo.size() >= helper.getAllCafeterias().size()) {
                        continue;
                    }

                    storedCafeterias = helper.getAllCafeterias();

                    // 아워홈
                    String contents[] = new String[] {"", "", ""};
                    // 각각의 끼니에 대하여
                    for (int i = 0; i < 3; i++) {
                        url = "http://emenu.ourhome.co.kr/meal/list.action" +
                                "?tempcd=31abebdcf2bb7fa767c0f9fb4d95d0a1" +
                                "&offerdt=" + date.toString().replaceAll("[^0-9]", "") +
                                "&up_yn=" +
                                "&up_busiplcd=31abebdcf2bb7fa767c0f9fb4d95d0a1&busiord=" +
                                "&mealclass=" + (i + 1) +
                                "&conner=A";
                        document = Jsoup.connect(url).post();
                        Elements tables = document.select(".menuT");

                        // 각각의 섹션(가마, 인터쉐프, 해피존)에 대하여
                        for (int j = 0; j < tables.size(); j++) {
                            String section = document.select(".conTitle span").get(j).text();
                            String menu = tables.get(j).text()
                                    .replaceAll("^[^가-힣0-9]", "")
                                    .replaceAll("[(].*?[)]", " ")
                                    .replaceAll("([가-힣])[^가-힣 ]([가-힣])", "$1 $2")
                                    .replaceAll("[^가-힣 ]", "")
                                    .replace("메뉴안내", "");
                            if (menu.contains(" ") && menu.split(" ").length > 2) menu = menu.split(" ")[0] + " " + menu.split(" ")[1];
                            contents[i] += String.format("<%s> %s ", section, menu);
                        }
                    }
                    Cafeteria ourHome = new Cafeteria();
                    ourHome.parse("아워홈", activity.getApplicationContext());
                    helper.insert(ourHome);
                    aliveCafeterias.add(ourHome);

                    DailyMenu dailyMenu = new DailyMenu();
                    dailyMenu.setCafeteriaName(ourHome.getName());
                    dailyMenu.setDate(date);
                    dailyMenu.setContents(contents, activity.getString(R.string.unknown));
                    helper.insert(dailyMenu);

                    // 직영 식당의 오늘 메뉴
                    url = "http://snuco.com/html/restaurant/restaurant_menu1.asp?date=";
                    url += String.format(
                            "%4d-%02d-%02d",
                            date.get(Date.YEAR), date.get(Date.MONTH) + 1, date.get(Date.DAY_OF_MONTH)
                    );
                    document = Jsoup.connect(url).get();
                    elements = document.select("tr");
                    // 준직영 식당의 오늘 메뉴
                    url = "http://snuco.com/html/restaurant/restaurant_menu2.asp?date=";
                    url += String.format(
                            "%4d-%02d-%02d",
                            date.get(Date.YEAR), date.get(Date.MONTH) + 1, date.get(Date.DAY_OF_MONTH)
                    );
                    document = Jsoup.connect(url).get();
                    elements.addAll(document.select("tr"));

                    // 각각의 식당에 대하여
                    for (Element cafeteriaInfo : elements) {
                        Elements rows = cafeteriaInfo.getElementsByTag("td");

                        // 올바른 형태가 아니라면
                        if (rows.get(0).getElementsByClass("left_text14") == null ||
                                rows.get(0).getElementsByClass("left_text14").size() == 0) {
                            continue;
                        }

                        // 연건 캠퍼스라면
                        if (rows.get(0).text().contains("연건")) {
                            continue;
                        }

                        Cafeteria cafeteria = new Cafeteria();
                        cafeteria.parse(rows.get(0).text(), activity.getApplicationContext());
                        helper.insert(cafeteria);
                        aliveCafeterias.add(cafeteria);

                        String todayMenu = "";
                        for (int i = 1; i < rows.size(); i++) {
                            String text = rows.get(i).text();
                            if (text.equals("")) {
                                continue;
                            }
                            todayMenu += text + DailyMenu.SEPARATOR;
                        }
                        dailyMenu = new DailyMenu();
                        dailyMenu.setCafeteriaName(cafeteria.getName());
                        dailyMenu.setDate(date);
                        dailyMenu.setContents(todayMenu.substring(0, todayMenu.length() - 1), activity.getString(R.string.unknown));
                        helper.insert(dailyMenu);
                    }

                    for (Cafeteria aliveCafeteria : aliveCafeterias) {
                        if (!storedCafeterias.contains(aliveCafeteria)) {
                            newCafeterias.add(aliveCafeteria);
                        }
                    }
                    int i = 0;
                    storedCafeterias = helper.getAllCafeterias();
                    for (Cafeteria storedCafeteria : storedCafeterias) {
                        if (!aliveCafeterias.contains(storedCafeteria)) {
                            String unknown = activity.getResources().getStringArray(R.array.불명)[0];
                            if (storedCafeteria.getName().contains(unknown)) {
                                if (newCafeterias.size() > 0) {
                                    helper.delete(storedCafeteria);
                                }
                            } else {
                                helper.delete(storedCafeteria);
                            }
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    CafeteriasFragment.reloadAllData();
                                }
                            });
                        }
                    }
                }
                if (handler != null) handler.sendEmptyMessage(FINISHED_PARSING);
            } catch (Exception e) {
                if (handler != null) handler.sendEmptyMessage(EXCEPTION_OCCURRED);
                e.printStackTrace();
            }
            return null;
        }

    }

}
