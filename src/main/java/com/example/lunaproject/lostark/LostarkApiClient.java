package com.example.lunaproject.lostark;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

@Service
public class LostarkApiClient {
    public InputStreamReader lostarkGetApi(String link, String key){
        try{
            HttpURLConnection httpURLConnection = getHttpURLConntection(link, "GET", key);
            return getInputStreamReader(httpURLConnection);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("API 호출 중 오류 발생: " + e.getMessage());
        }
    }
    public HttpURLConnection getHttpURLConntection(String link, String method, String key){
        try{
            URL url = new URL(link);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod(method);
            httpURLConnection.setRequestProperty("authorization", "Bearer "+key);
            httpURLConnection.setRequestProperty("accept", "application/json");
            httpURLConnection.setRequestProperty("content-Type", "application/json");
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
            else if(result == 401) {
                throw new IllegalArgumentException("올바르지 않은 apiKey 입니다.");
            }
            else if(result == 429) {
                throw new IllegalArgumentException("사용한도 (1분에 100개)를 초과했습니다.");
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
