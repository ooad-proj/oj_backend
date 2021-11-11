package com.ooad.oj_backend.rabbitmq.controller;

import com.ooad.oj_backend.rabbitmq.entity.SendPacket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @Autowired
    PacketSender packetSender;

    @GetMapping("/test/mq/send")
    public String mqTestSend() {
        SendPacket packet = new SendPacket("avbkdj", "src/main/resources/tc.zip", PacketSender.getJudgeDetailTest());
        packetSender.sendPacket(packet);
        return "yes";
    }
}
