package com.teammors.robot.example;

import com.teammors.robot.observer.TRobotManagerSubject;
import com.teammors.robot.observer.TRobotObserver;
import com.teammors.robot.ws.TRobotClient;


public class RobotExample implements TRobotObserver {

    TRobotManagerSubject robotManagerSubject = new TRobotManagerSubject();

    @Override
    public void onIMMessage(String message) {
        System.out.println("message:"+message);
    }

    @Override
    public void onIMError(String message) {
        System.out.println("error:"+message);
    }
}