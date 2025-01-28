package org.homework;

import org.homework.bot.Bot;
import org.homework.di.DIContainer;
import org.homework.di.annotations.Resolve;
import org.homework.logger.Logger;
import org.homework.logger.LoggerImpl;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {

        DIContainer container = new DIContainer();
        Logger logger = container.resolve(org.homework.logger.Logger.class);

            try {
                // Создаем TelegramBotsApi и регистрируем бота
                TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
                botsApi.registerBot(container.resolve(Bot.class));
                logger.info("Бот запущен");

            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
    }
}