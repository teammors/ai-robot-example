package com.teammors.robot.example;

import com.teammors.robot.example.configs.BotConfig;
import com.teammors.robot.ws.TRobotClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

@SpringBootApplication
@EnableConfigurationProperties(BotConfig.class)
public class RobotExampleApplication implements CommandLineRunner, ApplicationListener<ContextClosedEvent> {


    @Autowired
    private BotConfig botConfig;

    @Autowired
    RobotExample robotExample;

    public static void main(String[] args) {
        try {
            SpringApplication.run(RobotExampleApplication.class, args);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {

    }

    @Override
    public boolean supportsAsyncExecution() {
        return ApplicationListener.super.supportsAsyncExecution();
    }

    @Override
    public void run(String... args) throws Exception {

        robotExample.setBotConfig(botConfig);
        robotExample.robotManagerSubject.addObserver(robotExample);
        TRobotClient.instance().init(robotExample.robotToken, robotExample.robotManagerSubject);

        if(robotExample.agentType == 1){
            robotExample.initDify(robotExample.difyApiUrl,robotExample.difyApiKey);
        }else if(robotExample.agentType == 2){
            robotExample.initCoze(robotExample.cozeApiUrl, robotExample.cozeAccessToken, robotExample.cozeBotId,  robotExample.cozeUserId);
        }
    }

}
