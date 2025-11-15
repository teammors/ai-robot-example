package com.teammors.robot.example.helper;

import com.teammors.robot.example.agent.CozeChatClient;
import com.teammors.robot.example.utils.PositiveIntegerValidator;
import com.teammors.robot.ws.TRobotClient;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class CozeMessageHelper implements Runnable{

    String chatId;
    String message;
    CozeChatClient cozeChatClient;

    public CozeMessageHelper(String chatId, String message, CozeChatClient cozeChatClient) {
        this.chatId = chatId;
        this.message = message;
        this.cozeChatClient =  cozeChatClient;
    }

    @Override
    public void run() {
        askCoze();
    }

    private boolean askCoze() {
        try {

            if(message == null) return false;
            if(message.isEmpty()) return false;

            String requestId = UUID.randomUUID().toString();
            log.info("Request [{}]: {}", requestId, message);
            String preMessage = "AI is processing...";
            processResponse(chatId, requestId, preMessage);
            String completeMessage = cozeChatClient.waitAIResponse(message);
            if(!completeMessage.trim().isEmpty()) {
                processResponse(chatId, requestId, completeMessage);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean processResponse(String chatId, String requestId, String responseBody) {

        if(PositiveIntegerValidator.isPositiveInteger(chatId)) {
            String toUid = TRobotClient.instance().mId+"_"+chatId;
            return TRobotClient.instance().sendSingleUserTxtMessage(responseBody,toUid,1);
        }else {
            return TRobotClient.instance().sendToGroupTxtMessage(responseBody,chatId,1);
        }

    }
}
