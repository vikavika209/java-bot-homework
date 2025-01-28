package org.homework.bot;

import org.homework.api.CommandService;
import org.homework.logger.Logger;
import org.homework.di.annotations.Register;
import org.homework.di.annotations.Resolve;
import org.homework.model.User;
import org.homework.model.UserStates;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Register
public class Bot extends TelegramLongPollingBot {
    @Resolve
    private CommandService commandService;
    @Resolve
    private Logger logger;
    private final String botUsername;
    private final String botToken;
    private final Map<String, UserStates> userStates = new HashMap<>();
    private User user = null;

    public Bot() {
        // Чтение конфигурационного файла
        Properties properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("application.properties"));
        } catch (Exception e) {
            throw new RuntimeException("Не удалось загрузить конфигурацию бота", e);
        }

        this.botUsername = properties.getProperty("bot.username");
        this.botToken = properties.getProperty("bot.token");
    }

    @Override
    public String getBotUsername() {
        // Верните имя вашего бота
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
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
                if (command.equalsIgnoreCase("/start")){
                    execute(commandService.getCurrentUser(chatId));
                    userStates.put(chatId, UserStates.WAITING_FOR_USER_NAME);

                } else if (userStates.get(chatId) == UserStates.WAITING_FOR_USER_NAME) {
                    // Поиск или создание нового пользователя
                    String userMessage = update.getMessage().getText();
                    user = commandService.handleFindUser(chatId, userMessage);
                    execute(commandService.getHelp(chatId));

                    userStates.remove(chatId);

                }else if (command.equalsIgnoreCase("/help")) {
                    // Вызов метода getHelp из CommandService
                     execute(commandService.getHelp(chatId));
                }else if (command.equalsIgnoreCase("/set_amount")) {
                   // Вызов метода setPlan из CommandService
                   execute(commandService.setPlan(chatId));
                   userStates.put(chatId, UserStates.WAITING_FOR_AMOUNT);

               } else if (userStates.get(chatId) == UserStates.WAITING_FOR_AMOUNT) {
                   // Установка суммы плана
                   String userMessage = update.getMessage().getText();
                   execute(commandService.handleSetAmount(chatId, userMessage, user));

                   userStates.remove(chatId);

               } else if (command.equalsIgnoreCase("/add_transaction")) {
                   // Вызов метода addTransaction из CommandService
                   execute(commandService.addTransaction(chatId));

                   userStates.put(chatId, UserStates.WAITING_FOR_TRANSACTIONS_DATA);

               }else if (userStates.get(chatId) == UserStates.WAITING_FOR_TRANSACTIONS_DATA) {
                   // Добавление транзакции
                   String userMassage = update.getMessage().getText();
                   execute(commandService.handleNewTransaction(chatId, userMassage, user));

                   userStates.remove(chatId);

               } else if (command.equalsIgnoreCase("/delete_transaction")) {
                   // Вызов метода deleteTransaction из CommandService
                   execute(commandService.deleteTransaction(chatId));
                   userStates.put(chatId, UserStates.WAITING_FOR_TRANSACTION_NAME);
               } else if (userStates.get(chatId) == UserStates.WAITING_FOR_TRANSACTION_NAME) {
                   // Удаление транзакции
                   String transactionName = update.getMessage().getText();
                   execute(commandService.handleDeleteTransaction(chatId, transactionName, user));

                   userStates.remove(chatId);

            } else if (command.equalsIgnoreCase("/get_all_transactions")) {
                   // Вызов метода getAllTransactions из CommandService
                   execute(commandService.getAllTransactions(chatId, user));
               } else {
                   SendMessage sendMessage = new SendMessage();
                   sendMessage.setChatId(chatId);
                   sendMessage.setText("Неизвестная команда. Используйте /help для получения инструкции.");
               }

                // Отправляем сообщение
            } catch (TelegramApiException e) {
                logger.error("Ошибка при отправке сообщения в Telegram: " + e.getMessage());
            } catch (IOException e) {

            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
    }
}