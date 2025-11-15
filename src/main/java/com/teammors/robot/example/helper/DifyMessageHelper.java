package com.teammors.robot.example.helper;

import com.teammors.robot.example.agent.DifyChatClient;
import com.teammors.robot.example.utils.PositiveIntegerValidator;
import com.teammors.robot.ws.TRobotClient;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.IOException;
import java.util.UUID;

@Slf4j
public class DifyMessageHelper implements Runnable{

    String chatId;
    String message;
    DifyChatClient difyChatClient;

    public DifyMessageHelper(String chatId, String message, DifyChatClient difyChatClient) {
        this.chatId = chatId;
        this.message = message;
        this.difyChatClient = difyChatClient;
    }
    @Override
    public void run() {
        askDify();
    }

    private boolean askDify() {
        try {

            if(message == null) return false;
            if(message.isEmpty()) return false;

            // 为每个请求生成唯一标识
            String requestId = UUID.randomUUID().toString();
            log.info("Request [{}]: {}", requestId, message);
            String preMessage = "AI is processing...";
            processResponse(chatId, requestId, preMessage);
            String completeMessage = difyChatClient.waitAIResponse(message);
            // 处理响应，确保使用正确的userId
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
