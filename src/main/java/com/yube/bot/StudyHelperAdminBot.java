package com.yube.bot;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import com.yube.db.dao.ActorChatDao;
import com.yube.db.dao.PostgresActorChatDaoImpl;
import com.yube.db.dto.ActorChat;
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
import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class StudyHelperAdminBot extends Bot {

    private static Logger log = Logger.getLogger(StudyHelperAdminBot.class.getName());

    protected StudyHelperAdminBot(String token, String botName) {
        super(token, botName);
        try {
            ActorChatDao dao = new PostgresActorChatDaoImpl();
            RedissonClient client = RedissonClientFactory.getInstance().getRedissonClient();
            RSet<ActorChat> inMemoryAdminChats = client.getSet("adminChats");
            if(inMemoryAdminChats.isEmpty()){
                Set<ActorChat> admins =  dao.getActorChatsByType("admin");
                inMemoryAdminChats.addAll(admins);
            }

            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    readMessages();
                }
            };
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);


            /*Uncomment in prod*/
            /*
            long period = 24 * 3600 * 1000;
            Calendar desired = Calendar.getInstance();
            desired.set(Calendar.HOUR_OF_DAY, 8);
            desired.set(Calendar.MINUTE, 30);
            desired.set(Calendar.SECOND, 0);
            long delay = desired.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
            executor.scheduleAtFixedRate(task, delay, period,  TimeUnit.MILLISECOND);
            */
            long period = 1L;
            long delay  = 0;
            executor.scheduleAtFixedRate(task, delay, period,  TimeUnit.DAYS);
            long sleepTime = 24 * 3600 * 1000;
            while (true) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    processException(e);
                }
            }
        } catch (DatabaseException | ConfigurationException e) {
            processException(e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String text = message.getText().trim();
            if (text.equals("/register")) {
                super.register(message, "admin");
            }
        }
    }

    private void readMessages(){
        try(Connection conn = RabbitMqConnectionFactory.getInstance().getConnection();
            Channel channel = conn.createChannel()){
            channel.queueDeclare("bot", false, false, false, null);
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                ActorMessage actorMessage = SerializationUtils.deserialize(delivery.getBody());
                processMessage(actorMessage);
                try {
                    long sleepTime = 60 * 1000;
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    processException(e);
                }
            };
            channel.basicConsume("bot", true, deliverCallback, consumerTag -> {});
            long sleepTime = 60 * 60 * 1000;
            Thread.sleep(sleepTime);
        } catch (ServiceConnectionException | ConfigurationException | IOException | TimeoutException | InterruptedException e) {
            processException(e);
        }
    }

    private void processMessage(ActorMessage actorMessage){
        try{
            RedissonClient client = RedissonClientFactory.getInstance().getRedissonClient();
            RSet<ActorChat> inMemoryAdminChats = client.getSet("adminChats");
            Set<ActorChat> actorChats = inMemoryAdminChats.readAll();
            actorChats.forEach(actorChat -> sendTextMessage(actorChat.getChatId(), actorMessage.getMessage()));
        } catch (ConfigurationException e) {
            processException(e);
        }
    }
}
