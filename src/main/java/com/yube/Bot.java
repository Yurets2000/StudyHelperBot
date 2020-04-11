package com.yube;

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

    protected Bot(String token, String botName){
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

    public Message sendTextMessage(long chatId, String text){
        try {
            SendMessage send = new SendMessage().setChatId(chatId);
            send.setText(text.trim());
            return execute(send);
        } catch (Exception e) {
            processException(e);
            return null;
        }
    }

    public Message sendPhotoMessage(long chatId, String caption, File file){
        try {
            SendPhoto send = new SendPhoto().setChatId(chatId).setPhoto(file);
            if(caption != null){
                send = send.setCaption(caption);
            }
            return execute(send);
        } catch (TelegramApiException e) {
            processException(e);
            return null;
        }
    }

    protected final void processException(Exception e){
        ExceptionHandler.processException(e);
    }

    public final String getBotUsername() {
        return botName;
    }

    @Override
    public final String getBotToken() {
        return token;
    }
}