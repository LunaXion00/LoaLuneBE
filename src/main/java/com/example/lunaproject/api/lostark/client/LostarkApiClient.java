package com.example.lunaproject.api.lostark.client;

import org.springframework.beans.factory.annotation.Value;
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
    @Value("${Lostark-API-KEY}")
    String apiKey;
    public InputStreamReader lostarkGetApi(String link){
        while(true){
            try{
                ApiRateLimiter.checkAndWait();
                HttpURLConnection httpURLConnection = getHttpURLConntection(link, "GET");
                return getInputStreamReader(httpURLConnection);
            } catch (IllegalArgumentException e) {
                if(e.getMessage().contains("사용한도")){
                    try{
                        Thread.sleep(60_000);
                        ApiRateLimiter.resetCounter();
                    }catch(InterruptedException ie){
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("쓰레드 인터럽트 발생", ie);
                    }
                } else{
                    throw e;
                }
            } catch (Exception e) {
                throw new RuntimeException("API 호출 중 오류 발생: " + e.getMessage());
            }
        }
    }
    public HttpURLConnection getHttpURLConntection(String link, String method){
        try{
            URL url = new URL(link);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod(method);
            httpURLConnection.setRequestProperty("authorization", "Bearer "+apiKey);
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
