package com.teammors.robot.example.utils;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;

/**
 * Description: Java Random String Generator
 */
public class CreateRandomStr {

    /**
     * 1. Each position in the generated string could be any letter or number from str, requires import java.util.Random;
     * @param length
     * @return
     */
    public static String createRandomStr1(int length){
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            stringBuffer.append(str.charAt(number));
        }
        return stringBuffer.toString();
    }

    /**
     * 2. Can specify whether a position should be a-z, A-Z or 0-9, requires import java.util.Random;
     * @param length
     * @return
     */
    public static String createRandomStr2(int length){
        Random random = new Random();
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(3);
            long result = 0;
            switch (number) {
                case 0:
                    result = Math.round(Math.random()*25+65);
                    stringBuffer.append(String.valueOf((char)result));
                    break;
                case 1:
                    result = Math.round(Math.random()*25+97);
                    stringBuffer.append(String.valueOf((char)result));
                    break;
                case 2:
                    stringBuffer.append(String.valueOf(new Random().nextInt(10)));
                    break;
            }
        }
        return stringBuffer.toString();
    }

    /**
     * 3. The org.apache.commons.lang package has a RandomStringUtils class,
     * which contains a randomAlphanumeric(int length) function that can randomly generate a string of specified length.
     * @param length
     * @return
     */
    public static String createRandomStr3(int length){
        return RandomStringUtils.randomAlphanumeric(length);
    }
}