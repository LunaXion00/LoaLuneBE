package com.example.lunaproject.character.service;

import org.apache.tomcat.util.json.JSONParser;
import org.json.JSONArray;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

@Service
public class CharacterService {
    public JSONArray Characters(String characterName){
        try{
            URL url = new URL("https://developer-lostark.game.onstove.com/characters/"+ URLEncoder.encode(characterName, "UTF-8") +"/siblings");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection   .setRequestMethod("GET");
            httpURLConnection.setRequestProperty("accept", "application/json");
            httpURLConnection.setRequestProperty("authorization",
                    "bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6IktYMk40TkRDSTJ5NTA5NWpjTWk5TllqY2lyZyIsImtpZCI6IktYMk40TkRDSTJ5NTA5NWpjTWk5TllqY2lyZyJ9.eyJpc3MiOiJodHRwczovL2x1ZHkuZ2FtZS5vbnN0b3ZlLmNvbSIsImF1ZCI6Imh0dHBzOi8vbHVkeS5nYW1lLm9uc3RvdmUuY29tL3Jlc291cmNlcyIsImNsaWVudF9pZCI6IjEwMDAwMDAwMDA1NzExOTQifQ.Qr8rpHlBX6TnjHo5hbEgWSWZrkKj3LT4D1Z36c1gSULWuA-8ZqCgGa4TsoWgFf8k3b6-8Sv90kCpWZpPyI13kVREYrd_oLdq6zMxfsqXxfs8vE7yqTSF50PCEye8ToeH1OVMTSNAgPhmtsUzH0SHo5haMlwry5McMJBSH_ozCxVpnEf1PGLnnUMUJgTXFX24FgHMpge0d1TG4btsTlTneobKuaMYPgQeLsl4Q-orRkXlYx35VL1aBnGjTuVSOrfyuIQijl3eLODB-qDaO-tgvSkg4g9PMfAi9pvp-16oA8eEY_w7zxF8DuLRfkeXKAUx1vPX_9cMo4Te48vUgtRKQg");
            int responseCode = httpURLConnection.getResponseCode();
            InputStream inputStream;
            if(responseCode == 200){
                inputStream = httpURLConnection.getInputStream();
            }else{
                inputStream = httpURLConnection.getErrorStream();
            }
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

            JSONParser parser = new JSONParser(inputStreamReader);
            JSONArray array = (JSONArray) parser.parse();
            return array;
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

}
