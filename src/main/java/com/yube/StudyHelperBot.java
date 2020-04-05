package com.yube;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class StudyHelperBot extends Bot {

    private static Logger log = LogManager.getLogger(StudyHelperBot.class.getName());

    public static void main(String[] args) {
        if(args == null || args.length != 2){
            log.error("You must run bot with 2 args - BotToken and BotName");
        } else {
            ApiContextInitializer.init();
            Bot.runBot(new StudyHelperBot(args[0], args[1]));
            log.error("Bot started successfully");
        }
    }

    protected StudyHelperBot(String token, String botName) {
        super(token, botName);
    }

    @Override
    protected void processTheException(Exception e) {
        log.error(e.getMessage(), e);
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.debug("Update received");
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            long chatId = message.getChatId();
            sendTextMessage(chatId, "Bot developing in progress, new features will be added soon...");
        }
    }
}
