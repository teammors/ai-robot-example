package com.teammors.robot.example.utils;

import com.alibaba.fastjson2.JSONObject;
import okhttp3.*;

import java.io.File;
import java.io.IOException;

public class FileUtils {



    public static String uploadFiles(String filePath) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type","image");
        jsonObject.put("mId",3000);

        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                        "file",
                        "file.txt",
                        RequestBody.create(
                                new File("path/to/your/file.txt"),
                                MediaType.parse("application/octet-stream")
                        )
                )
                .addFormDataPart("data", "your_data_here")
                .build();

        Request request = new Request.Builder()
                .url("http://127.0.0.1:5000/uploadFiles")
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                System.out.println("OK: " + responseBody);
            } else {
                System.out.println("Failed: " + response.code() + " - " + response.message());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";

    }
}
