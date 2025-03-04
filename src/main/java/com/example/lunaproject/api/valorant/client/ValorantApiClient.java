package com.example.lunaproject.api.valorant.client;

import com.example.lunaproject.api.lostark.client.ApiRateLimiter;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

@Service
public class ValorantApiClient {
    public InputStreamReader valorantGetApi(String link){
        try{
            HttpURLConnection httpURLConnection = getHttpURLConntection(link, "GET");
            return getInputStreamReader(httpURLConnection);
        } catch (Exception e) {
            throw new RuntimeException("API 호출 중 오류 발생: " + e.getMessage());
        }
    }
    public HttpURLConnection getHttpURLConntection(String link, String method){
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
    private InputStreamReader getInputStreamReader(HttpURLConnection httpURLConnection) {
        try {
            int result = httpURLConnection.getResponseCode();
            InputStream inputStream;
            if(result == 200) {
                inputStream = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                return inputStreamReader;
            }
            else {
                throw new RuntimeException("API 응답 오류: " + httpURLConnection.getResponseMessage());
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("API 응답 처리 중 오류 발생: " + e.getMessage());
        }
    }
}
