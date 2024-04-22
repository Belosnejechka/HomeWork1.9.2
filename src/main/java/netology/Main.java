package netology;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.imageio.ImageIO;
import java.io.*;

public class Main {
    public static final ObjectMapper objectMapper = new ObjectMapper();

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
        CloseableHttpResponse response = httpClient.execute(request);

        //json в java-объект
        Nasa nasa = objectMapper.readValue(response.getEntity().getContent(), Nasa.class);
        String url = nasa.getUrl();

        //новый http-запрос
        HttpGet requestNew = new HttpGet(url);
        CloseableHttpResponse responseNew = httpClient.execute(requestNew);
        String[] arrayUrlImage = url.split("/");

        //сохраняем ответ в файл с именем, извлеченным из url
        File file = new File(arrayUrlImage[arrayUrlImage.length - 1]);
        ImageIO.write(ImageIO.read(responseNew.getEntity().getContent()), "jpg", file);

    }
}