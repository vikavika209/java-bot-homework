package org.homework.api;

import org.homework.model.CharacterData;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface DataService {
    List<CharacterData> getCharacterData() throws IOException, URISyntaxException;
}
