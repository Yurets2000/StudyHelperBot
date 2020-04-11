package com.yube;

import com.yube.dao.PostgresTriggerDaoImpl;
import com.yube.dao.TriggerDao;
import com.yube.dto.Trigger;
import com.yube.exceptions.ConfigurationException;
import com.yube.exceptions.DatabaseException;
import com.yube.redis.RedissonClientFactory;
import org.apache.log4j.Logger;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class StudyHelperBot extends Bot {

    private static Logger log = Logger.getLogger(StudyHelperBot.class.getName());

    public static void main(String[] args) {
        if(args == null || args.length != 2){
            log.error("You must run bot with 2 args - BotToken and BotName");
        } else {
            ApiContextInitializer.init();
            Bot.runBot(new StudyHelperBot(args[0], args[1]));
            log.info("Bot started successfully");
        }
    }

    protected StudyHelperBot(String token, String botName) {
        super(token, botName);
        try {
            TriggerDao dao = new PostgresTriggerDaoImpl();
            Set<Trigger> botTriggers =  dao.getBotTriggers(token);
            RedissonClient client = RedissonClientFactory.getInstance().getRedissonClient();
            RSet<Trigger> inMemoryCommonTriggers = client.getSet("common");
            if(inMemoryCommonTriggers.isEmpty()){
                Set<Trigger> commonTriggers =  dao.getCommonTriggers();
                inMemoryCommonTriggers.addAll(commonTriggers);
            }
            RSet<Trigger> inMemoryBotTriggers = client.getSet(token);
            inMemoryBotTriggers.addAll(botTriggers);
        } catch (DatabaseException | ConfigurationException e) {
            processException(e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.debug("Update received");
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            long chatId = message.getChatId();
            String text = message.getText().trim();
            List<String> triggers = getTriggers();
            if(triggers.contains(text)) {
                sendTextMessage(chatId, "Have you just said \"" + text.toUpperCase() + "\"? Bruh...");
            }
        }
    }

    private List<String> getTriggers(){
        List<String> triggerValues = null;
        try {
            Set<Trigger> triggers = new HashSet<>();
            RedissonClient client = RedissonClientFactory.getInstance().getRedissonClient();
            RSet<Trigger> inMemoryCommonTriggers = client.getSet("common");
            RSet<Trigger> inMemoryBotTriggers = client.getSet(token);
            triggers.addAll(inMemoryCommonTriggers.readAll());
            triggers.addAll(inMemoryBotTriggers.readAll());
            triggerValues = triggers.stream().map(Trigger::getTriggerValue).collect(Collectors.toList());
        } catch (ConfigurationException e) {
            processException(e);
        }
        return triggerValues;
    }
}
