package com.yube.bot;

public final class BotFactory {

    public static Bot getBot(String type, String token, String botName){
        Bot bot;
        switch (type){
            case "admin":
                bot = new StudyHelperAdminBot(token, botName);
                break;
            case "client":
                bot = new StudyHelperClientBot(token, botName);
                break;
            default:
                throw new IllegalArgumentException("Can't create bot with such type");
        }
        return bot;
    }
}
