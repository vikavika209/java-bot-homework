package org.homework.api;

import org.homework.model.User;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.IOException;
import java.net.URISyntaxException;

public interface CommandService {
    SendMessage getHeroes(String chatId);

    SendMessage getHelp(String chatId);

    SendMessage setPlan(String chatId);

    SendMessage handleSetAmount(String chatId, String userMessage, User user) throws IOException, URISyntaxException;

    SendMessage addTransaction (String chatId);

    SendMessage handleNewTransaction (String chatId, String userMessage, User user);

    SendMessage deleteTransaction(String chatId);

    SendMessage handleDeleteTransaction (String chatId, String transactionName, User user);

    SendMessage getAllTransactions(String chatId, User user) throws IOException, URISyntaxException;

    SendMessage getCurrentUser(String chatId);

    User handleFindUser(String chatId, String userMessage);
}
