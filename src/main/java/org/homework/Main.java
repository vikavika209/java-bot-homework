package org.homework;

import org.homework.bot.Bot;
import org.homework.di.DIContainer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {
        DIContainer container = new DIContainer();
        try {

            // Создаем TelegramBotsApi и регистрируем бота
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(container.resolve(Bot.class));

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}