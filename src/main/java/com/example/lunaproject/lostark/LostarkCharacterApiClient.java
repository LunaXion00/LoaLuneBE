package com.example.lunaproject.lostark;

import com.example.lunaproject.character.dto.CharacterDTO;
import com.example.lunaproject.character.entity.LoaCharacter;
import com.example.lunaproject.character.service.CharacterService;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
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
    private static final Logger logger = LoggerFactory.getLogger(CharacterService.class);

    public List<LoaCharacter> createCharacterList(String characterName, String apiKey){
        try{
            JSONArray jsonArray = findCharactersByApi(characterName, apiKey);
            List<LoaCharacter> characterList = new ArrayList<>();
            for(Object o: jsonArray){
                JSONObject jsonObject = (JSONObject) o;
                JSONObject details = findCharacterDetailsByApi(jsonObject.get("CharacterName").toString(), apiKey);
                if (details == null) {
                    logger.warn("Skipping character: " + characterName + " due to missing data.");
                    continue; // 다음 캐릭터로 건너뜀
                }
                LoaCharacter character = LoaCharacter.builder()
                        .characterName(details.get("CharacterName").toString())
                        .serverName(getStringOrNull(details.get("ServerName")))
                        .characterClassName(getStringOrNull(details.get("CharacterClassName")))
                        .characterLevel(Integer.parseInt(details.get("CharacterLevel").toString()))
                        .itemLevel(Double.parseDouble(details.get("ItemMaxLevel").toString().replace(",", "")))
                        .characterImage(getStringOrNull(details.get("CharacterImage")))
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

    public String getStringOrNull(Object value){
        return value != null ? value.toString() : null;
    }

    public JSONArray findCharactersByApi(String characterName, String apiKey){
        String encodedCharacterName = URLEncoder.encode(characterName, StandardCharsets.UTF_8);
        String link = "https://developer-lostark.game.onstove.com/characters/"+encodedCharacterName+"/siblings";
        InputStreamReader inputStreamReader = apiClient.lostarkGetApi(link, apiKey);
        JSONParser parser = new JSONParser();
        try{
            JSONArray array = (JSONArray) parser.parse(inputStreamReader);
            return filterLevel(array);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public JSONArray filterLevel(JSONArray jsonArray){
        JSONArray filtered = new JSONArray();
        for(Object o:jsonArray){
            JSONObject obj = (JSONObject) o;
            double itemLevel = Double.parseDouble(obj.get("ItemMaxLevel").toString().replace(",", ""));
            if(itemLevel >= 1640D) filtered.add(obj);
        }
        return filtered;
    }
    public JSONObject findCharacterDetailsByApi(String characterName, String apiKey){
        String encodedCharacterName = URLEncoder.encode(characterName, StandardCharsets.UTF_8);
        String link = "https://developer-lostark.game.onstove.com/armories/characters/" + encodedCharacterName + "/profiles";
        InputStreamReader inputStreamReader = apiClient.lostarkGetApi(link, apiKey);
        JSONParser parser = new JSONParser();
        try{
            JSONObject object = (JSONObject) parser.parse(inputStreamReader);
            if (object == null) {
                logger.warn("Character details not found for: " + encodedCharacterName);
                return null; // 데이터가 없는 경우 null 반환
            }
            logger.info(object.toString());
            return object;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public List<CharacterDTO> getSiblings(String characterName, String apiKey){
        String encodedCharacterName = URLEncoder.encode(characterName, StandardCharsets.UTF_8);
        String link = "https://developer-lostark.game.onstove.com/characters/"+encodedCharacterName+"/siblings";

        try(InputStreamReader reader = apiClient.lostarkGetApi(link, apiKey)){
            ObjectMapper objectMapper = new ObjectMapper();
            List<CharacterDTO> characterDTOS = objectMapper.readValue(
                    reader,
                    new TypeReference<List<CharacterDTO>>() {}
            );
            return characterDTOS.stream()
                    .filter(this::isItemLevelAboveThreshold)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Error in getSiblings: "+e.getMessage());
        }
    }
    private boolean isItemLevelAboveThreshold(CharacterDTO dto) {
        try {
            double itemLevel = dto.getItemLevel();
            return itemLevel >= 1640.00;
        } catch (NumberFormatException e) {
            throw new NumberFormatException(e.getMessage());
        }
    }

    public CharacterDTO getCharacter(String characterName, String apiKey){
        try{
            String encodeCharacterName = URLEncoder.encode(characterName, StandardCharsets.UTF_8);
            String link = "https://developer-lostark.game.onstove.com/armories/characters/" + encodeCharacterName + "/profiles";

            InputStreamReader inputStreamReader = apiClient.lostarkGetApi(link, apiKey);
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(inputStreamReader, CharacterDTO.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
