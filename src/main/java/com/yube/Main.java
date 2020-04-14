package com.yube;

import com.yube.bot.Bot;
import com.yube.bot.BotFactory;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.ApiContextInitializer;

public class Main {

    private static Logger log = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        if(args == null || args.length != 3){
            log.error("You must run bot with 3 args - BotType, BotToken and BotName");
        } else {
            ApiContextInitializer.init();
            Bot bot = BotFactory.getBot(args[0], args[1], args[2]);
            Bot.runBot(bot);
            log.info("Bot started successfully");
        }
    }
}
