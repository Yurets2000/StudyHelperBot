package com.yube;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class StudyHelperBot extends Bot {

    public static void main(String[] args) {
        if(args == null || args.length != 2){
            System.out.println("You must run bot with 2 args - BotToken and BotName");
        } else {
            ApiContextInitializer.init();
            Bot.runBot(new StudyHelperBot(args[0], args[1]));
        }
    }

    protected StudyHelperBot(String token, String botName) {
        super(token, botName);
    }

    @Override
    protected void processTheException(Exception e) {
        e.printStackTrace();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            long chatId = message.getChatId();
            sendTextMessage(chatId, "Bot developing in progress, new features will be added soon...");
        }
    }
}
