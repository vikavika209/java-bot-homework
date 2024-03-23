package org.homework.api;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface CommandService {
    SendMessage getHeroes(String chatId);

    SendMessage getHelp(String chatId);
}
