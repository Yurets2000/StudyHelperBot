package com.yube.bot;

import com.yube.db.dao.ActorChatDao;
import com.yube.db.dao.ActorDao;
import com.yube.db.dao.PostgresActorChatDaoImpl;
import com.yube.db.dao.PostgresActorDaoImpl;
import com.yube.db.dto.Actor;
import com.yube.db.dto.ActorChat;
import com.yube.exceptions.ConfigurationException;
import com.yube.exceptions.DatabaseException;
import com.yube.utils.ExceptionHandler;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;

public abstract class Bot extends TelegramLongPollingBot {

    protected final String token, botName;

    protected Bot(String token, String botName) {
        this.token = token;
        this.botName = botName;
    }

    public static void runBot(Bot newBot) {
        try {
            new TelegramBotsApi().registerBot(newBot);
        } catch (TelegramApiException e) {
            newBot.processException(e);
        }
    }

    public Message sendTextMessage(long chatId, String text) {
        try {
            SendMessage send = new SendMessage().setChatId(chatId);
            send.setText(text.trim());
            return execute(send);
        } catch (Exception e) {
            processException(e);
            return null;
        }
    }

    public Message sendPhotoMessage(long chatId, String caption, File file) {
        try {
            SendPhoto send = new SendPhoto().setChatId(chatId).setPhoto(file);
            if (caption != null) {
                send = send.setCaption(caption);
            }
            return execute(send);
        } catch (TelegramApiException e) {
            processException(e);
            return null;
        }
    }

    protected final void processException(Exception e) {
        ExceptionHandler.processException(e);
    }

    @Override
    public final String getBotUsername() {
        return botName;
    }

    @Override
    public final String getBotToken() {
        return token;
    }

    protected void register(Message message, String type) {
        long chatId = message.getChatId();
        int userId = message.getFrom().getId();
        try {
            ActorDao actorDao = new PostgresActorDaoImpl();
            Actor actor = actorDao.getActorByUserIdAndType(userId, type);
            if (actor != null) {
                ActorChatDao actorChatDao = new PostgresActorChatDaoImpl();
                ActorChat actorChat = actorChatDao.getActorChat(actor.getActorId(), chatId);
                if (actorChat == null) {
                    actorChatDao.addActorChat(new ActorChat(actor.getActorId(), chatId));
                }
            }
        } catch (DatabaseException | ConfigurationException e) {
            processException(e);
        }
    }
}