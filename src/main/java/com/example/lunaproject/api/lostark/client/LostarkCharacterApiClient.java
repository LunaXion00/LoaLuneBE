package com.example.lunaproject.api.lostark.client;

import com.example.lunaproject.api.client.GameApiClient;
import com.example.lunaproject.game.character.dto.LoaCharacterDTO;
import com.example.lunaproject.game.character.service.LoaCharacterService;
import com.example.lunaproject.global.utils.GameType;
import com.example.lunaproject.streamer.dto.StreamerRequestDTO;
import com.fasterxml.jackson.core.type.TypeReference;
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
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LostarkCharacterApiClient implements GameApiClient<LoaCharacterDTO> {
    private final LostarkApiClient apiClient;
    private static final Logger logger = LoggerFactory.getLogger(LoaCharacterService.class);

    public List<LoaCharacterDTO> createCharacterList(StreamerRequestDTO requestDTO){
        try{
            JSONArray jsonArray = findCharactersByApi(requestDTO.getMainCharacter());
            List<LoaCharacterDTO> dtos = new ArrayList<>();
            for(Object o: jsonArray){
                JSONObject jsonObject = (JSONObject) o;
                JSONObject details = findCharacterDetailsByApi(jsonObject.get("CharacterName").toString());
                if (details == null) {
                    logger.warn("Skipping character: " + requestDTO.getMainCharacter() + " due to missing data.");
                    continue; // 다음 캐릭터로 건너뜀
                }
                dtos.add(LoaCharacterDTO.builder()
                                .characterName(details.get("CharacterName").toString())
                                .serverName(getStringOrNull(details.get("ServerName")))
                                .characterClassName(getStringOrNull(details.get("CharacterClassName")))
                                .characterLevel(Integer.parseInt(details.get("CharacterLevel").toString()))
                                .itemLevel(Double.parseDouble(details.get("ItemMaxLevel").toString().replace(",", "")))
                                .characterImage(getStringOrNull(details.get("CharacterImage")))
                        .build());
            }
            List<LoaCharacterDTO> sortedDtos = dtos.stream()
                    .sorted(Comparator.comparing(LoaCharacterDTO::getItemLevel).reversed()).collect(Collectors.toList());
            return sortedDtos;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public GameType getGameType() {
        return GameType.lostark;
    }

    public String getStringOrNull(Object value){
        return value != null ? value.toString() : null;
    }

    public JSONArray findCharactersByApi(String characterName){
        String encodedCharacterName = URLEncoder.encode(characterName, StandardCharsets.UTF_8);
        String link = "https://developer-lostark.game.onstove.com/characters/"+encodedCharacterName+"/siblings";
        InputStreamReader inputStreamReader = apiClient.lostarkGetApi(link);
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
    public JSONObject findCharacterDetailsByApi(String characterName){
        String encodedCharacterName = URLEncoder.encode(characterName, StandardCharsets.UTF_8);
        String link = "https://developer-lostark.game.onstove.com/armories/characters/" + encodedCharacterName + "/profiles";
        InputStreamReader inputStreamReader = apiClient.lostarkGetApi(link);
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

    public List<LoaCharacterDTO> getSiblings(String characterName){
        String encodedCharacterName = URLEncoder.encode(characterName, StandardCharsets.UTF_8);
        String link = "https://developer-lostark.game.onstove.com/characters/"+encodedCharacterName+"/siblings";

        try(InputStreamReader reader = apiClient.lostarkGetApi(link)){
            ObjectMapper objectMapper = new ObjectMapper();
            List<LoaCharacterDTO> loaCharacterDTOS = objectMapper.readValue(
                    reader,
                    new TypeReference<List<LoaCharacterDTO>>() {}
            );
            return loaCharacterDTOS.stream()
                    .filter(this::isItemLevelAboveThreshold)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Error in getSiblings: "+e.getMessage());
        }
    }
    private boolean isItemLevelAboveThreshold(LoaCharacterDTO dto) {
        try {
            double itemLevel = dto.getItemLevel();
            return itemLevel >= 1640.00;
        } catch (NumberFormatException e) {
            throw new NumberFormatException(e.getMessage());
        }
    }

    public LoaCharacterDTO getCharacter(String characterName){
        try{
            String encodeCharacterName = URLEncoder.encode(characterName, StandardCharsets.UTF_8);
            String link = "https://developer-lostark.game.onstove.com/armories/characters/" + encodeCharacterName + "/profiles";

            InputStreamReader inputStreamReader = apiClient.lostarkGetApi(link);
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(inputStreamReader, LoaCharacterDTO.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
