package com.teammors.robot.example;

import com.teammors.robot.ws.TRobotClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

@SpringBootApplication
public class RobotExampleApplication implements CommandLineRunner, ApplicationListener<ContextClosedEvent> {

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

        String imServer = "ws://192.168.3.94:9922";
        String imUser = "3000_800000020";
        String imToken = "gskPFrZNUObyEdQqhwOBtwQ9pLTishRe";

        RobotExample robotExample = new RobotExample();
        robotExample.robotManagerSubject.addObserver(robotExample);
        TRobotClient.instance().init(imServer, imUser, imToken,robotExample.robotManagerSubject);

    }
}
