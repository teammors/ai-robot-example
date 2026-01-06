package com.teammors.robot.example;

import com.alibaba.fastjson.JSONObject;

import com.teammors.robot.example.agent.CozeChatClient;
import com.teammors.robot.example.agent.DifyChatClient;
import com.teammors.robot.example.agent.SpringAIClient;
import com.teammors.robot.example.configs.BotConfig;

import com.teammors.robot.example.helper.CozeMessageHelper;
import com.teammors.robot.example.helper.DifyMessageHelper;
import com.teammors.robot.example.helper.SpringAIMessageHelper;
import com.teammors.robot.example.property.RedisProperty;
import com.teammors.robot.example.utils.JsonUtils;
import com.teammors.robot.example.utils.RedisUtils;
import com.teammors.robot.example.utils.ThreadUtils;
import com.teammors.robot.observer.TRobotManagerSubject;
import com.teammors.robot.observer.TRobotObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class RobotExample implements TRobotObserver {

    @Autowired
    RedisProperty  redisProperty;

    String robotToken = "";

    int agentType = 2;//1=Dify 2-Coze 3=FastGPT

    String difyApiUrl = "";
    String difyApiKey = "";

    String cozeApiUrl = "";
    String cozeAccessToken = "";
    String cozeBotId = "";
    String cozeUserId = "";    // 用于标识用户的唯一ID

    String fastGPTApiUrl = "";
    String fastGPTApiKey = "Y";

    String springApiUrl = "";
    String springApiKey = "";


    DifyChatClient difyChatClient = null;
    CozeChatClient cozeChatClient = null;
    SpringAIClient springAIClient = null;



    TRobotManagerSubject robotManagerSubject = new TRobotManagerSubject();


    public void setBotConfig(BotConfig botConfig){

        this.robotToken = botConfig.getRobotToken();
        this.agentType = botConfig.getAgentType();

        this.difyApiUrl = botConfig.getDify().getApiUrl();
        this.difyApiKey = botConfig.getDify().getApiKey();

        this.cozeApiUrl = botConfig.getCoze().getApiUrl();
        this.cozeAccessToken = botConfig.getCoze().getAccessToken();
        this.cozeBotId = botConfig.getCoze().getBotId();
        this.cozeUserId = botConfig.getCoze().getUserId();

        this.fastGPTApiUrl = botConfig.getFastgpt().getApiUrl();
        this.fastGPTApiKey = botConfig.getFastgpt().getApiKey();

        this.springApiUrl = botConfig.getSpringAI().getApiUrl();
        this.springApiKey = botConfig.getSpringAI().getApiKey();

        initRedis();
    }

    @Override
    public void onIMMessage(String message) {

        try {

            log.info("message:"+message);
            if(JsonUtils.isJsonObject(message)) {

                JSONObject jsonObject = JSONObject.parseObject(message);
                if(jsonObject.containsKey("text")) {

                    String chatId = jsonObject.getString("chatId");
                    String text = jsonObject.getString("text");

                    log.info("chatId:{} text:{}", chatId, text);

                    if (agentType == 1) {
                        DifyMessageHelper difyMessageHelper = new DifyMessageHelper(chatId,text,difyChatClient);
                        ThreadUtils.instance().getExecutor().execute(difyMessageHelper);
                    } else if (agentType == 2) {
                        CozeMessageHelper cozeMessageHelper = new CozeMessageHelper(chatId,text,cozeChatClient);
                        ThreadUtils.instance().getExecutor().execute(cozeMessageHelper);
                    }else if (agentType == 4) {
                        SpringAIMessageHelper springAIMessageHelper = new SpringAIMessageHelper(chatId,text,springAIClient);
                        ThreadUtils.instance().getExecutor().execute(springAIMessageHelper);
                    }

                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public void onIMError(String message) {
        System.out.println("error:"+message);
    }



    public void initDify(String difyApiUrl,String difyApiKey) {
        difyChatClient =  new DifyChatClient(difyApiUrl, difyApiKey);
    }


    public void initCoze(String cozeApiUrl,String cozeAccessToken, String cozeBotId, String cozeUserId) {
        cozeChatClient =  new CozeChatClient(cozeApiUrl,cozeAccessToken, cozeBotId, cozeUserId);
    }


    public void initSpringAI(String springUrl,String springApiKey) {
        springAIClient = new SpringAIClient(springUrl, springApiKey);
    }

    public void initRedis(){
        RedisUtils.instance().init(redisProperty.getIp(),redisProperty.getUser(),
                redisProperty.getPassword(),redisProperty.getPort(),
                redisProperty.getDb(), redisProperty.getIscluster());

    }


}