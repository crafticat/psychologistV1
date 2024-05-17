package com.apptoeat.psychologist;

import com.apptoeat.psychologist.history.ChatData;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.ChatFutures;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.BlockThreshold;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.GenerationConfig;
import com.google.ai.client.generativeai.type.HarmCategory;
import com.google.ai.client.generativeai.type.SafetySetting;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.Arrays;

import lombok.Getter;

@Getter
public class Psychologist implements FutureCallback<GenerateContentResponse> {


    private final ChatFutures chat;
    private final ChatFutures qChat, sumChat;
    private boolean init = false, init2 = false;
    private String lastQuestion;
    private String lastPrompt;
    private String diagnoses, solutions;
    private int counter = 0;
    private final ChatData data;
    private boolean working = false;

    public Psychologist(ChatData data) {
        this.data = data;
        String apiKey = "AIzaSyDji_5JIkRkmLScPCDp_KAUc4RyKXU330I";

        GenerationConfig.Builder configBuilder = new GenerationConfig.Builder();
        configBuilder.temperature = 0.3f;
        configBuilder.topK = 1;
        configBuilder.topP = 1f;
        configBuilder.maxOutputTokens = 512;

        SafetySetting harassmentSafety = new SafetySetting(HarmCategory.HARASSMENT,
                BlockThreshold.MEDIUM_AND_ABOVE);

        SafetySetting hateSpeechSafety = new SafetySetting(HarmCategory.HATE_SPEECH,
                BlockThreshold.MEDIUM_AND_ABOVE);
        SafetySetting h2 = new SafetySetting(HarmCategory.SEXUALLY_EXPLICIT,
                BlockThreshold.MEDIUM_AND_ABOVE);
        SafetySetting h3 = new SafetySetting(HarmCategory.DANGEROUS_CONTENT,
                BlockThreshold.MEDIUM_AND_ABOVE);

        GenerativeModel gm = new GenerativeModel("gemini-1.0-pro", apiKey,configBuilder.build(),Arrays.asList(harassmentSafety, hateSpeechSafety,h2,h3));
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        chat = model.startChat(data.getC1());
        qChat = model.startChat(data.getC2());
        sumChat = model.startChat();
    }

    public boolean ask(String prompt) {
        if (working) return true;
        working = true;
        String t1 = "question: " + lastQuestion + "\n" +
                "input: " + prompt + "\n" +
                "diagnose + questions:";
        Content content = new Content.Builder().addText(t1).build();
        if (!init) {
            t1 = "You are a professional psychologist and also mental health professional who's job is to diagnose. Given a the user input your job is to \n" +
                    "    give a list of diagnose and a list of questions which can provide you more information. Also give a list of possible solutions.  it is possible for there to be no diagnoses and no solutions yet if the conversion just started.\n" +
                    "    Do this on every user input\n" +
                    "    if a question was asked you may remove it from the list.\n" +
                    "    do not tell the patient to seek for help, you are the help.\n" +
                    "input: " + prompt + "\n" +
                    "diagnose + questions:";
            content = new Content.Builder().addText(t1).build();
        }

        var response = chat.sendMessage(content);
        lastPrompt = prompt;
        Futures.addCallback(response,this,MoreExecutors.directExecutor());
        return false;
    }

    @Override
    public void onSuccess(GenerateContentResponse result) {
        counter++;
        String t1 = "";

        Content content;
        System.out.println(result.getText());
        if (!init) {
            t1 = "You are a professional psychologist and also mental health professional who's job is to ask questions to the patient. \n" +
                    "        You are given a list of diagnoses and a list of questions which you may choose from to ask the patient to get more infromation. \n" +
                    "        you are given an input (user response)\n" +
                    "        and a data which is the diagnose\n" +
                    "        you may not ask more then one question\n" +
                    "        do this on every user input\n" +
                    "        do not ask the same questions twice\n" +
                    "        if the conversation is over (no more questions) you may start a new one\n" +
                    "        or instead of a question tell the user to try one of the solutions\n" +
                    "        When writing your output as it is will be given to the user so dont write question: before\n" +
                    "input: " + lastPrompt  + "\n" +
                    "data: " + result.getText() + "\n" +
                    "question (only one):";
            content = new Content.Builder().addText(t1).build();
        } else {
            t1 = "input: "+ lastPrompt  + "\n" +
                    "data:" + result.getText() + "\n" +
                                        "question (only one):";
            content = new Content.Builder().addText(t1).build();
        }

        if (counter >= 10) {
            content = new Content.Builder().addText("input: " + lastPrompt  + "\n" +
                    "data: " + result.getText() + "\n" +
                    "solution + short explanation (only one):").build();
            counter = 0;
        }

        var response = qChat.sendMessage(content);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                MainActivity.instance.addResponse(result.getText());
                lastQuestion = result.getText();
                init = true;

                data.loadC1((chat.getChat().getHistory()));
                data.loadC2((qChat.getChat().getHistory()));
                WelcomeActivity.getHistory().applyChanges();
                working = false;
            }

            @Override
            public void onFailure(Throwable t) {

            }
        }, MoreExecutors.directExecutor());
        data.setLastAns(result.getText());
    }

    public void calculateSum(Runnable done) {
        String prom = "";
        if (!init2) {
            init2 = true;
            prom = "You are a summarise bot, you may be given diagnoses and solutions and questions." +
                    "\n Your job is to summarise only the following in the following format: \n" +
                    "summarise:" +
                    "diagnose:\n" +
                    "  - (insert here a diagnose)\n" +
                    "  - (insert here another diagnose)\n" +
                    "||| (used to split the diagnoses with solutions)\n" +
                    "solutions:\n" +
                    "  - (insert here a solution)\n" +
                    "  - (insert here another solution)\n";
        }
        prom += "data: " + data.getLastAns();
        prom += "\n summarise: ";
        var content2 = new Content.Builder().addText(prom).build();
        var response2 = sumChat.sendMessage(content2);

        if (!working) {
            working = true;
            Futures.addCallback(response2, new FutureCallback<GenerateContentResponse>() {
                @Override
                public void onSuccess(GenerateContentResponse result) {
                    System.out.println(result.getText());
                    var s = result.getText().split("\\|");
                    diagnoses = s[0].replace("diagnose:\n", "");
                    solutions = s[3].replace("\nsolutions:\n", "");
                    done.run();
                    working = false;
                }

                @Override
                public void onFailure(Throwable t) {

                }
            }, MoreExecutors.directExecutor());
        } else {
            diagnoses = "Before jumping to diagnoses please let me finish my sentence!!!";
            solutions = "Before jumping to diagnoses please let me finish my sentence!!!";
            done.run();
        }
    }

    @Override
    public void onFailure(Throwable t) {

    }
}
