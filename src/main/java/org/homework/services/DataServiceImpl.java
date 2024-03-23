package org.homework.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.homework.logger.Logger;
import org.homework.model.CharacterData;
import org.homework.model.CharacterResponse;
import org.homework.api.DataService;
import org.homework.api.HttpService;
import org.homework.di.annotations.Register;
import org.homework.di.annotations.Resolve;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

// Класс, представляющий полную структуру ответа API
// Использование сервиса для получения данных
@Register
public class DataServiceImpl implements DataService {
    @Resolve
    private Logger logger;
    @Resolve
    private HttpService httpService;


    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<CharacterData> getCharacterData() throws IOException, URISyntaxException {
        logger.debug("Запрос данных персонажей с API");

        String url = "https://rickandmortyapi.com/api/character?page=1";
        String jsonResponse = httpService.sendGetRequest(url, Map.of());

        CharacterResponse response = objectMapper.readValue(jsonResponse, CharacterResponse.class);
        return response.results;
    }
}
