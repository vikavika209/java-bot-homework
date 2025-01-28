package org.homework.api;

import org.homework.exception.TransactionNotFoundException;
import org.homework.model.CharacterData;
import org.homework.model.Expenses;
import org.homework.model.Transaction;
import org.homework.model.User;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public interface DataService {
    List<CharacterData> getCharacterData() throws IOException, URISyntaxException;
    Expenses getUserExpenses(String chatId, User user) throws IOException, URISyntaxException;
    String addNewTransaction(String chatId, Transaction transaction, User user) throws IOException, URISyntaxException;
    void removeTransaction(String chatId, String transactionName, User user) throws IOException, URISyntaxException, TransactionNotFoundException;
    List<Transaction> findAllTransactions(String chatId, User user) throws IOException, URISyntaxException;
    User getUser(String username);
}
