package org.homework.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.homework.exception.TransactionNotFoundException;
import org.homework.logger.Logger;
import org.homework.model.*;
import org.homework.api.DataService;
import org.homework.api.HttpService;
import org.homework.di.annotations.Register;
import org.homework.di.annotations.Resolve;
import org.homework.storage.InMemoryStorage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// Класс, представляющий полную структуру ответа API
// Использование сервиса для получения данных
@Register
public class DataServiceImpl implements DataService {

    @Resolve
    private Logger logger;
    @Resolve
    private HttpService httpService;
    @Resolve
    private InMemoryStorage storage;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<CharacterData> getCharacterData() throws IOException, URISyntaxException {
        logger.debug("Запрос данных персонажей с API");

        String url = "https://rickandmortyapi.com/api/character?page=1";
        String jsonResponse = httpService.sendGetRequest(url, Map.of());

        CharacterResponse response = objectMapper.readValue(jsonResponse, CharacterResponse.class);
        return response.results;
    }

    @Override
    public Expenses getUserExpenses(String chatId, User user) {
        Expenses expenses = user.getExpenses();

        if (expenses == null) {
            expenses = new Expenses();
        }
        return user.getExpenses();
    }

    @Override
    public String addNewTransaction(String chatId, Transaction transaction, User user) throws IOException, URISyntaxException {
        Expenses expenses = getUserExpenses(chatId, user);
        expenses.getTransactions().add(transaction);

        //Проверяем не превышен ли лимит
        long transactionsAmount = expenses.getTransactions().stream()
                .mapToLong(Transaction::getAmount)
                .sum();
        if (expenses.getPlan() < transactionsAmount){
            return String.format("Транзакция: %s успешно добавлена.\nОбратите внимание, что лимит расходов превышен.", transaction.getCategory());
        }else {
            return String.format("Транзакция: %s успешно добавлена.", transaction.getCategory());
        }
    }

    @Override
    public void removeTransaction(String chatId, String transactionName, User user) throws IOException, URISyntaxException, TransactionNotFoundException {
        Expenses expenses = getUserExpenses(chatId, user);
        // Ищем транзакцию с указанным названием
        Optional<Transaction> transactionToRemove = expenses.getTransactions().stream()
                .filter(t -> t.getCategory().equals(transactionName))
                .findFirst();

        // Если транзакция найдена, удаляем её
        if (transactionToRemove.isPresent()) {
            expenses.getTransactions().remove(transactionToRemove.get());
            logger.info("Транзакция: " + transactionName + " успешно удалена");
        }else {
            throw new TransactionNotFoundException("Ошибка при удалении транзакции: " + transactionName);
        }
    }

    @Override
    public List<Transaction> findAllTransactions(String chatId, User user) {
        Expenses expenses = getUserExpenses(chatId, user);
        return expenses.getTransactions();
    }

    @Override
    public User getUser(String username){
        var optionalUser = storage.getUsers().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst();
        User user = optionalUser.orElse(null);

        if (user == null) {
            return new User(username, new Expenses());
        }else return user;
    }
}
