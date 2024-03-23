package org.homework.services;

import org.homework.api.CommandService;
import org.homework.api.DataService;
import org.homework.di.annotations.Register;
import org.homework.di.annotations.Resolve;
import org.homework.logger.Logger;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Register
public class CommandServiceImpl implements CommandService {
    @Resolve
    private DataService dataService;

    @Resolve
    private Logger logger;

    @Override
    public SendMessage getHeroes(String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        try {
            logger.info("Запрос данных персонажей для чата: " + chatId);
            sendMessage.setText(dataService.getCharacterData().toString());
        } catch (Exception error) {
            logger.error("Ошибка при получении данных персонажей: " + error.getMessage());

            sendMessage.setText("Произошла ошибка при получении данных. Попробуйте позже.");
        }
        return sendMessage;
    }

    @Override
    public SendMessage getHelp(String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Commands: /getHeroes, /help");
        return sendMessage;
    }
}
