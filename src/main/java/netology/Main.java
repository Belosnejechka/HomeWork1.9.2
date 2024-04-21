package netology;

import com.github.axet.vget.VGet;
import org.apache.http.HttpHeaders;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.parser.JSONParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Main {
    public static final String REMOTE_SERVICE_URI = "https://api.nasa.gov/planetary/apod?api_key=xyHFK4MxE4enxO5wrwqxyXOdFuSI2ViZtSdTuOee";

    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)    // максимальное время ожидание подключения к серверу
                        .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                        .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                        .build())
                .build();

        HttpGet request = new HttpGet(REMOTE_SERVICE_URI);
        request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
        CloseableHttpResponse response = httpClient.execute(request);
        String JSON = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
        JSONParser parser = new JSONParser();
        Nasa nasa1 = null;
        try {
            Object nasa = parser.parse(JSON);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            nasa1 = gson.fromJson(String.valueOf(nasa), Nasa.class);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (org.json.simple.parser.ParseException e) {
            throw new RuntimeException(e);
        }

        HttpGet request2 = new HttpGet(nasa1.getUrl());
        request2.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
        CloseableHttpResponse response2 = httpClient.execute(request2);
        String JSON2 = new String(response2.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
        String filename = nasa1.getUrl().substring(nasa1.getUrl().lastIndexOf('/') + 1) + ".html";
        File myFile = new File("/Users/belosnejka/IdeaProjects/Projects/netology/Work/HomeWork4.9.2/src/main/resources/" + filename);
        try {
            if (myFile.createNewFile())
                System.out.println("Файл был создан");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(myFile))) {
            bw.write(JSON2);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}