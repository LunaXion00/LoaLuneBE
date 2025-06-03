package com.example.lunaproject.api.valorant.client;

import com.example.lunaproject.game.character.service.LoaCharacterService;
import com.example.lunaproject.streamer.controller.StreamerController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

@Service
public class ValorantApiClient {
    private static final Logger logger = LoggerFactory.getLogger(ValorantApiClient.class);

    public InputStreamReader valorantGetApi(String link){
        final int MAX_RETRIES = 7;
        final int RETRY_DELAY = 5000;
        for (int attempt = 0; attempt <= MAX_RETRIES; attempt++) {
            try {
                HttpURLConnection httpURLConnection = getHttpURLConnection(link, "GET");
                return getInputStreamReader(httpURLConnection);
            } catch (Exception e) {
                if (attempt >= MAX_RETRIES) {
                    throw new RuntimeException(MAX_RETRIES + "회 재시도 실패: " + e.getMessage(), e);
                }
                try {
                    Thread.sleep(RETRY_DELAY);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("재시도 대기 중단", ie);
                }
            }
        }
        throw new RuntimeException("최대 재시도 횟수 초과");
    }

    public HttpURLConnection getHttpURLConnection(String link, String method){
        try{
            URL url = new URL(link);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod(method);
            httpURLConnection.setDoOutput(true);
            return httpURLConnection;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private InputStreamReader getInputStreamReader(HttpURLConnection conn) throws IOException {
        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            return new InputStreamReader(conn.getInputStream());
        }
        throw new RuntimeException("API 응답 오류 [" + responseCode + "]: " + conn.getResponseMessage());
    }
}
