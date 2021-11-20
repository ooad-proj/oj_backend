package com.ooad.oj_backend.rabbitmq.controller;

import com.ooad.oj_backend.rabbitmq.MqUtil;
import com.ooad.oj_backend.rabbitmq.entity.JudgeDetail;
import com.ooad.oj_backend.rabbitmq.entity.SendPacket;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

@Component
public class PacketSender {
    @Autowired
    RabbitTemplate rabbitTemplate;

    public void sendPacket(SendPacket sendPacket) {
        rabbitTemplate.convertAndSend(MqUtil.REQUEST_QUEUE, sendPacket.toString());
    }

    protected static JudgeDetail getJudgeDetailTest() {
        String codeJava = "import java.util.Scanner;\n" +
                "\n" +
                "public class Main {\n" +
                "    \n" +
                "    public static void main(String[] args) {\n" +
                "        Scanner s = new Scanner(System.in);\n" +
                "        int len = s.nextInt();\n" +
                "        for (int i = 1; i <= len; i++) {\n" +
                "            System.out.println(s.nextInt() + s.nextInt());\n" +
                "        }\n" +
                "    }\n" +
                "}";
        JudgeDetail judgeDetail = new JudgeDetail("java", codeJava, 2000, 128000, null);
        return judgeDetail;
    }

}
