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
    private InputStreamReader getInputStreamReader(HttpURLConnection httpURLConnection) throws InterruptedException {
        final int MAX_RETRIES = 3;
        final int RETRY_DELAY = 10000;
        for(int attempt=0; attempt<=MAX_RETRIES+1; attempt++) {
            try {
                int responseCode = httpURLConnection.getResponseCode();
                if(responseCode == 200) {
                    return new InputStreamReader(httpURLConnection.getInputStream());
                } else {
                    throw new RuntimeException("API 응답 오류 [" + responseCode + "]: "
                            + httpURLConnection.getResponseMessage());
                }
            }
            catch (IllegalArgumentException e) {
                throw e; // 파라미터 오류시 즉시 실패
            }
            catch (Exception e) {
                if(attempt > MAX_RETRIES) {
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
}
