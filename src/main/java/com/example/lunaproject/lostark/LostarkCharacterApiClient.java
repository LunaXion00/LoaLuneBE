package com.example.lunaproject.lostark;

import com.example.lunaproject.character.entity.LoaCharacter;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LostarkCharacterApiClient {
    private final LostarkApiClient apiClient;
    public List<LoaCharacter> createCharacterList(String characterName, String apiKey){
        try{
            JSONArray jsonArray = findCharactersByApi(characterName, apiKey);
            List<LoaCharacter> characterList = new ArrayList<>();
            for(Object o: jsonArray){
                JSONObject jsonObject = (JSONObject) o;
                LoaCharacter character = LoaCharacter.builder()
                        .characterName(jsonObject.get("CharacterName").toString())
                        .serverName(jsonObject.get("ServerName").toString())
                        .characterClassName(jsonObject.get("CharacterClassName").toString())
                        .characterLevel(Integer.parseInt(jsonObject.get("CharacterLevel").toString()))
                        .itemLevel(Double.parseDouble(jsonObject.get("ItemMaxLevel").toString().replace(",", "")))
                        .build();
                characterList.add(character);
            }
            List<LoaCharacter> sortedList = characterList.stream()
                    .sorted(Comparator.comparing(LoaCharacter::getItemLevel).reversed()).collect(Collectors.toList());
            return sortedList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public JSONArray findCharactersByApi(String characterName, String apiKey){
        String encodedCharacterName = URLEncoder.encode(characterName, StandardCharsets.UTF_8);
        String link = "https://developer-lostark.game.onstove.com/characters/"+encodedCharacterName+"/siblings";
        InputStreamReader inputStreamReader = apiClient.lostarkGetApi(link, apiKey);
        JSONParser parser = new JSONParser();
        try{
            JSONArray array = (JSONArray) parser.parse(inputStreamReader);
            return array;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    public JSONObject findCharacterDetailsByApi(String characterName, String apiKey){
        String encodedCharacterName = URLEncoder.encode(characterName, StandardCharsets.UTF_8);
        String link = "https://developer-lostark.game.onstove.com/armories/characters/" + encodedCharacterName + "/profiles";
        InputStreamReader inputStreamReader = apiClient.lostarkGetApi(link, apiKey);
        JSONParser parser = new JSONParser();
        try{
            JSONObject object = (JSONObject) parser.parse(inputStreamReader);
            return object;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
