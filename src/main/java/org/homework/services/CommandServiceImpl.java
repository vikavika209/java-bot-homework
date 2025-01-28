package org.homework.services;

import org.homework.api.CommandService;
import org.homework.api.DataService;
import org.homework.di.annotations.Register;
import org.homework.di.annotations.Resolve;
import org.homework.exception.TransactionNotFoundException;
import org.homework.logger.Logger;
import org.homework.model.Expenses;
import org.homework.model.Transaction;
import org.homework.model.User;
import org.homework.storage.InMemoryStorage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.IOException;
import java.net.URISyntaxException;


@Register
public class CommandServiceImpl implements CommandService {
    @Resolve
    private DataService dataService;

    @Resolve
    private Logger logger;

    @Resolve
    public InMemoryStorage storage;

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
        var sendMessage = getNewSendMessage(chatId);
        sendMessage.setText(" Доступные команды:\n" +
                "            /help - Для получения инструкции\n" +
                "            /set_amount - Для установки бюджета\n" +
                "            /add_transaction - Для добавления новой транзакции\n" +
                "            /delete_transaction <статья расхода> - Для удаления транзакции\n" +
                "            /get_all_transactions - Для получения списка транзакций, отсортированных по убыванию\n" +
                "            \"\"\";");
        return sendMessage;
    }

    @Override
    public SendMessage setPlan(String chatId) {
        var sendMessage = getNewSendMessage(chatId);
        sendMessage.setText("Введите сумму, которую планируете истратить за месяц");
        return sendMessage;
    }

    @Override
    public SendMessage handleSetAmount(String chatId, String userMessage, User user) throws IOException, URISyntaxException {
        var sendMessage = getNewSendMessage(chatId);
        Expenses expenses = dataService.getUserExpenses(chatId, user);
            try {
                long planAmount = Long.parseLong(userMessage);
                expenses.setPlan(planAmount);
                sendMessage.setText("План " + planAmount + " успешно установлен.");
            } catch (NumberFormatException error) {
                sendMessage.setText("Сумма введена неверно, повторите попытку.");
            }
            return sendMessage;
    }

    @Override
    public SendMessage addTransaction(String chatId) {
        var sendMessage = getNewSendMessage(chatId);
        sendMessage.setText("Введите наименование статьи расхода и потраченную сумму через ;");
        return sendMessage;
    }

    @Override
    public SendMessage handleNewTransaction(String chatId, String userMessage, User user) {
        var sendMessage = getNewSendMessage(chatId);
        String transactionName = userMessage.split("; ")[0];
        String transactionAmount = userMessage.split("; ")[1];
        try{
            long longTransactionAmount = Long.parseLong(transactionAmount);
            Transaction transaction = new Transaction(transactionName, longTransactionAmount);
            var messageForUser = dataService.addNewTransaction(chatId, transaction, user);

            sendMessage.setText(messageForUser);
        }catch (Exception e) {
            sendMessage.setText("Ошибка при добавлении транзакции: " + e.getMessage());
        }
        return sendMessage;
    }

    @Override
    public SendMessage deleteTransaction(String chatId) {
        var sendMessage = getNewSendMessage(chatId);
        sendMessage.setText("Введите наименование транзакции, которую хотите удалить");
        return sendMessage;
    }

    @Override
    public SendMessage handleDeleteTransaction(String chatId, String transactionName, User user) {
        var sendMessage = getNewSendMessage(chatId);
        try{
            dataService.removeTransaction(chatId, transactionName, user);
            sendMessage.setText("Транзакция: " + transactionName + " успешно удалена");
        }catch (TransactionNotFoundException e){
            sendMessage.setText("Ошибка при удалении транзакции: " + transactionName);
        } catch (IOException | URISyntaxException e) {
            sendMessage.setText("Ошибка при удалении транзакции: " + e.getMessage());
        }
        return sendMessage;
    }

    @Override
    public SendMessage getAllTransactions(String chatId, User user) throws IOException, URISyntaxException {
        var sendMessage = getNewSendMessage(chatId);
        var transactions = dataService.findAllTransactions(chatId, user);
        sendMessage.setText(transactions.toString());
        return sendMessage;
    }

    @Override
    public SendMessage getCurrentUser(String chatId) {
        var sendMessage = getNewSendMessage(chatId);
        sendMessage.setText("Введите име пользователя");
        return sendMessage;
    }

    @Override
    public User handleFindUser(String chatId, String userMessage) {
        return dataService.getUser(userMessage);
    }

    private SendMessage getNewSendMessage(String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        return sendMessage;
    }
}
