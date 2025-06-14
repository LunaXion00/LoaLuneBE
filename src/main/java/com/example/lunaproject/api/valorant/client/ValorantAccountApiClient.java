package com.example.lunaproject.api.valorant.client;

import com.example.lunaproject.api.client.GameApiClient;
import com.example.lunaproject.game.character.dto.LoaCharacterDTO;
import com.example.lunaproject.game.character.dto.VlrtAccountDTO;
import com.example.lunaproject.game.character.service.LoaCharacterService;
import com.example.lunaproject.game.character.utils.VlrtTier;
import com.example.lunaproject.global.utils.GameServer;
import com.example.lunaproject.global.utils.GameType;
import com.example.lunaproject.streamer.dto.StreamerRequestDTO;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ValorantAccountApiClient implements GameApiClient<VlrtAccountDTO> {
    private final ValorantApiClient apiClient;
    private static final Logger logger = LoggerFactory.getLogger(ValorantAccountApiClient.class);

    @Override
    public List<VlrtAccountDTO> createCharacterList(StreamerRequestDTO requestDTO) {
        try {
            String[] parts = requestDTO.getMainCharacter().split("#");
            String apiResponse = getRankByApi(parts[0], parts[1], requestDTO.getGameServer());

            String[] responseParts = apiResponse.split(" - ");
            String tierString = responseParts[0].replace(" ", "_");
            int rr = Integer.parseInt(responseParts[1].replace("RR.", ""));

            VlrtAccountDTO dto = VlrtAccountDTO.builder()
                    .tier(VlrtTier.fromApiString(tierString))
                    .rr(rr)
                    .characterName(requestDTO.getMainCharacter())
                    .server(requestDTO.getGameServer())
                    .build();
            return List.of(dto);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getRankByApi(String accountName, String tag, GameServer region) throws IOException, ParseException {
        String link = "https://api.kyroskoh.xyz/valorant/v1/mmr/" + region + "/" + URLEncoder.encode(accountName, StandardCharsets.UTF_8) + "/" + URLEncoder.encode(tag, StandardCharsets.UTF_8);;
        try {
            InputStreamReader inputStreamReader = apiClient.valorantGetApi(link);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            return line;
        } catch(Exception e){
            return null;
        }
    }

    @Override
    public GameType getGameType() {
        return GameType.vlrt;
    }

}