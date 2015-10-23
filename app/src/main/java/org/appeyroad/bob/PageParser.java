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
import java.util.Calendar;
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
                List<Cafeteria> aliveCafeterias = new ArrayList<>();

                // 아워홈
                Cafeteria ourHome = new Cafeteria();
                ourHome.identifyBy("아워홈", activity.getApplicationContext());
                helper.insert(ourHome);
                aliveCafeterias.add(ourHome);

                // 이전 주와 이번 주, 다음 주에 대하여
                for (int week : new int[] {0, -1, 1}) {
                    Date dateInWeek = Date.today();
                    dateInWeek.add(Calendar.WEEK_OF_YEAR, week);

                    DailyMenu existingData = helper.getMenu(ourHome, dateInWeek);
                    String empty = DailyMenu.SEPARATOR + DailyMenu.SEPARATOR;
                    if (existingData != null && !existingData.getContents().equals(empty)){
                        continue;
                    }

                    url = "http://dorm.snu.ac.kr/dk_board/facility/food.php?start_date2="
                            + (Date.today().getTimeInMillis() / 1000 + week * 604800);
                    document = Jsoup.connect(url).get();
                    Elements tds = document.select(".t_col td");

                    List<DailyMenu> weeklyMenus = new ArrayList<>();

                    Date date;
                    for (int dayOfWeek = 0; dayOfWeek < 7; dayOfWeek++) {
                        DailyMenu dailyMenu = new DailyMenu();
                        dailyMenu.setCafeteriaCode(ourHome.getCode());

                        date = new Date(dateInWeek);
                        while (date.get(Calendar.DAY_OF_WEEK) != dayOfWeek + 1) {
                            date.add(Calendar.DAY_OF_WEEK, date.get(Calendar.DAY_OF_WEEK) < dayOfWeek ? 1 : -1);
                        }
                        dailyMenu.setDate(date);

                        weeklyMenus.add(dailyMenu);
                    }
                    int meal = -1;
                    int dayOfWeek = -1;
                    // 그 주의 모든 날과 시간대의 메뉴에 대하여
                    for (int i = 0; i < tds.size(); i++) {
                        Element td = tds.get(i);
                        if (td.text().equals("비고")) break;
                        else if (td.text().equals("아침")) meal = DailyMenu.BREAKFAST;
                        else if (td.text().equals("점심")) meal = DailyMenu.LUNCH;
                        else if (td.text().equals("저녁")) meal = DailyMenu.DINNER;
                        else if (td.text().equals("가마")
                                | td.text().equals("인터쉐프")
                                | td.text().equals("해피존")
                                | td.text().equals("아워홈")
                                | td.text().equals("919동"))
                            continue;
                        else {
                            String content;
                            if (td.getElementsByTag("li") != null && td.getElementsByTag("li").size() > 0) {
                                String className = td.getElementsByTag("li").get(0).className();
                                Element board = document.getElementsByClass("board").get(0);
                                String price;
                                if (className != null && className.length() > 0
                                        && board.getElementsByClass(className).size() > 0) {
                                    price = board.getElementsByClass(className).get(0).text().replaceAll("원", "");
                                } else {
                                    price = activity.getString(R.string.unknown);
                                }

                                String menu = td.text()
                                        .replaceAll("^[^가-힣0-9]", "")
                                        .replaceAll("[(].*?[)]", " ")
                                        .replaceAll("([가-힣])[^가-힣 ]([가-힣])", "$1 $2")
                                        .replaceAll("[^가-힣 ]", "")
                                        .replaceAll("<.+>", "");

                                content = String.format("<%s> %s ", price, menu);
                            } else {
                                content = null;
                            }

                            dayOfWeek = ++dayOfWeek % 7;
                            weeklyMenus.get(dayOfWeek).setContent(meal, content);
                        }
                    }

                    for (DailyMenu dailyMenu : weeklyMenus) {
                        if (helper.getMenu(ourHome, dailyMenu.getDate()) == null) helper.insert(dailyMenu);
                    }
                }


                // 인자로 받은 각각의 날짜에 대하여
                for (Date date : dates) {
                    // 그 날의 정보가 완전히 있다면
                    List<DailyMenu> dailyInfo = helper.getMenus(date);
                    if (dailyInfo.size() > 1 && dailyInfo.size() >= helper.getAllCafeterias().size()) {
                        continue;
                    }

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
                        cafeteria.identifyBy(rows.get(0).text(), activity.getApplicationContext());
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
                        DailyMenu dailyMenu = new DailyMenu();
                        dailyMenu.setCafeteriaCode(cafeteria.getCode());
                        dailyMenu.setDate(date);
                        dailyMenu.setContents(todayMenu.substring(0, todayMenu.length() - 1));
                        helper.insert(dailyMenu);
                    }

                    storedCafeterias = helper.getAllCafeterias();
                    for (Cafeteria storedCafeteria : storedCafeterias) {
                        if (!aliveCafeterias.contains(storedCafeteria)) {
                            helper.delete(storedCafeteria);
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
