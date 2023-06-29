package socialnet.service.logging;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LogClean {

    public void deleteOldLogs(String yandexToken) throws IOException, ParseException {

        Integer afterDayDelete = 13;
        Map<String, Date> logs = getListLogsFiles(yandexToken);
        cleanLogs(logs, afterDayDelete, yandexToken);

    }

    public Map<String, Date> getListLogsFiles(String yandexToken) throws IOException, ParseException {

        HashMap<String, Date> logsList = new HashMap<>();

        URL url = new URL("https://cloud-api.yandex.net/v1/disk/resources/last-uploaded?");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", yandexToken);

        StringBuilder content = new StringBuilder();

        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
        }

        JSONObject obj = new JSONObject(content.toString());
        JSONArray arr = obj.getJSONArray("items");

        for (int i = 0; i < arr.length(); i++) {
            String dateFile = arr.getJSONObject(i).getString("modified");
            String pathFail = arr.getJSONObject(i).getString("path");
            Date dataLog = dateFormat(dateFile);

            logsList.put(pathFail, dataLog);
        }

        return logsList;
    }

    public Date dateFormat(String dateFile) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").
            parse(dateFile.
            replace("T", " ")
            .substring(0, 19));
    }

    public void cleanLogs(Map<String, Date> logs, Integer afterDayDelete,String yandexToken) throws IOException {
        Date today = new Date(System.currentTimeMillis());
        Calendar deleteData = Calendar.getInstance();
        deleteData.setTime(today);
        deleteData.add(Calendar.DATE, -afterDayDelete);

        for (Map.Entry<String, Date> entry : logs.entrySet()) {
            String path = entry.getKey();
            Date date = entry.getValue();

            Calendar logCal = Calendar.getInstance();
            logCal.setTime(date);

            if (logCal.getTime().before(deleteData.getTime())) {
                delete(path, yandexToken);
            }
        }
    }

    public void delete(String path,String yandexToken) throws IOException {
        URL url = new URL("https://cloud-api.yandex.net/v1/disk/resources?path=" + path + "&permanently=true");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("DELETE");
        con.setRequestProperty("Authorization", yandexToken);

        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            StringBuilder content = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
        }
    }
}
