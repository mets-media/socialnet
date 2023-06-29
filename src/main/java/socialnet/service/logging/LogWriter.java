package socialnet.service.logging;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
@Slf4j
public class LogWriter {

    @Value("${auth.logging.yandexToken}")
    private String yandexToken;

    private final LogClean cleanLogsInCloud = new LogClean();

    @Bean
    @Async
    @Scheduled(fixedRate = 3_600_000)
    public void writer() throws IOException, ParseException {

        cleanLogsInCloud.deleteOldLogs(yandexToken);
        pushLogs(getHref());
    }

    public String getHref() throws IOException {

        String path = createLogFile();
        URL url = new URL("https://cloud-api.yandex.net/v1/disk/resources/upload?path=" + path + "&overwrite=true");
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

        String[] split = content.toString().split("\"");

        return split[7];
    }

    public void pushLogs(String href) throws IOException{
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            logArchiving();
            File file = new File("logs/logs.zip");
            HttpPut httpPut = new HttpPut(href);
            httpPut.setEntity(new FileEntity(file));
            HttpResponse response = httpclient.execute(httpPut);

            if (response.getStatusLine().getStatusCode() != 200) {
                log.error(response.getStatusLine().getReasonPhrase());
            }
        }
    }

    public String createLogFile() {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateToday = dateFormatter.format(new Date());
        return "log_" + dateToday + ".zip";
    }

    public void logArchiving() throws IOException {
        String sourceFile = "logs/logs.log";

        try (FileOutputStream fos = new FileOutputStream("logs/logs.zip")) {
            try (ZipOutputStream zipOut = new ZipOutputStream(fos)) {
                File fileToZip = new File(sourceFile);

                try (FileInputStream fis = new FileInputStream(fileToZip)) {
                    ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
                    zipOut.putNextEntry(zipEntry);

                    byte[] bytes = new byte[1024];
                    int length;

                    while ((length = fis.read(bytes)) >= 0) {
                        zipOut.write(bytes, 0, length);
                    }
                }
            }
        }
    }
}
