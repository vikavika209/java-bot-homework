package org.homework.bot;

import org.homework.api.CommandService;
import org.homework.logger.Logger;
import org.homework.di.annotations.Register;
import org.homework.di.annotations.Resolve;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Register
public class Bot extends TelegramLongPollingBot {
    @Resolve
    private CommandService commandService;
    @Resolve
    private Logger logger;
    @Override
    public String getBotUsername() {
        // Верните имя вашего бота
        return "YOUR_BOT_USERNAME";
    }

    @Override
    public String getBotToken() {
        return "YOUR_BOT_TOKEN";
    }


    @Override
    public void onUpdateReceived(Update update) {
        logger.debug("Получено новое обновление: " + update.toString());

        // Проверяем, есть ли в обновлении сообщение и текст сообщения
        if (update.hasMessage() && update.getMessage().hasText()) {
            String command = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();

            logger.info("Обработка команды: " + command);

            try {
                if (command.equalsIgnoreCase("/getHeroes")) {
                    // Вызов метода getHeroes из CommandService
                    execute(commandService.getHeroes(chatId));

                } else if (command.equalsIgnoreCase("/help")) {
                    // Вызов метода getHelp из CommandService
                     execute(commandService.getHelp(chatId));
                }
                // Отправляем сообщение
            } catch (TelegramApiException e) {
                logger.error("Ошибка при отправке сообщения в Telegram: " + e.getMessage());
            }
        }
    }
}