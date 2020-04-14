package com.yube.bot;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.yube.db.dao.ActorDao;
import com.yube.db.dao.PostgresActorDaoImpl;
import com.yube.db.dao.PostgresTriggerDaoImpl;
import com.yube.db.dao.TriggerDao;
import com.yube.db.dto.Actor;
import com.yube.db.dto.Trigger;
import com.yube.exceptions.ConfigurationException;
import com.yube.exceptions.DatabaseException;
import com.yube.exceptions.ServiceConnectionException;
import com.yube.rabbitmq.RabbitMqConnectionFactory;
import com.yube.rabbitmq.dto.ActorMessage;
import com.yube.redis.RedissonClientFactory;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.log4j.Logger;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeoutException;

public class StudyHelperClientBot extends Bot {

    private static Logger log = Logger.getLogger(StudyHelperClientBot.class.getName());

    protected StudyHelperClientBot(String token, String botName) {
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
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            long chatId = message.getChatId();
            String text = message.getText().trim();
            if(text.equals("/register")) {
                super.register(message, "lecturer");
            }else{
                Set<Trigger> triggers = getTriggers();
                Optional<Trigger> optionalTrigger = triggers.stream()
                        .filter(t -> t.getTriggerValue().contains(text)).findFirst();
                if (optionalTrigger.isPresent()) {
                    Trigger trigger = optionalTrigger.get();
                    if (trigger.getTriggerSubType() != null && trigger.getTriggerSubType().equals("lecturer")) {
                        tryProcessLecturerMessage(message);
                    } else {
                        sendTextMessage(chatId, String.format("Have you just said \"%s\"? Bruh...", text.toUpperCase()));
                    }
                }
            }
        }
    }

    private void tryProcessLecturerMessage(Message message){
        int userId = message.getFrom().getId();
        String text = message.getText().trim();
        try {
            ActorDao actorDao = new PostgresActorDaoImpl();
            Actor actor = actorDao.getActorByUserIdAndType(userId, "lecturer");
            if(actor != null){
                ActorMessage actorMessage = new ActorMessage(
                        actor.getActorId(),
                        String.format("Lecturer %s says: \"%s\" in %s chat",
                                actor.getActorName(),
                                text,
                                message.getChat().getTitle() == null ? "private" : message.getChat().getTitle())
                );
                publishMessage(actorMessage);
            }
        } catch (DatabaseException | ConfigurationException e) {
            processException(e);
        }
    }

    private void publishMessage(ActorMessage message){
        try(Connection conn = RabbitMqConnectionFactory.getInstance().getConnection();
            Channel channel = conn.createChannel()){
            channel.queueDeclare("bot", false, false, false, null);
            channel.basicPublish("", "bot", null, SerializationUtils.serialize(message));
        } catch (ServiceConnectionException | ConfigurationException | IOException | TimeoutException e) {
            processException(e);
        }
    }

    private Set<Trigger> getTriggers(){
        Set<Trigger> triggers = null;
        try {
            triggers = new HashSet<>();
            RedissonClient client = RedissonClientFactory.getInstance().getRedissonClient();
            RSet<Trigger> inMemoryCommonTriggers = client.getSet("common");
            RSet<Trigger> inMemoryBotTriggers = client.getSet(token);
            triggers.addAll(inMemoryCommonTriggers.readAll());
            triggers.addAll(inMemoryBotTriggers.readAll());
        } catch (ConfigurationException e) {
            processException(e);
        }
        return triggers;
    }
}
