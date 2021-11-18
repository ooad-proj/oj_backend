package com.ooad.oj_backend.rabbitmq.controller;

import com.ooad.oj_backend.rabbitmq.entity.Result;
import com.ooad.oj_backend.rabbitmq.entity.SendPacket;
import com.ooad.oj_backend.service.JudgerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TestController {
    @Autowired
    PacketSender packetSender;
    @Autowired
    JudgerService judgerService;

    @GetMapping("/test/mq/send")
    public String mqTestSend() {
        SendPacket packet = new SendPacket("avbkdj", "src/main/resources/tc.zip", PacketSender.getJudgeDetailTest());
        packetSender.sendPacket(packet);
        return "yes";
    }

    @GetMapping("/test/mq/test")
    public String mqTestTestSend() {
        judgerService.testTestCase("1 2 3", PacketSender.getJudgeDetailTest(), PacketSender.getJudgeDetailTest());
        return "yes";
    }

    @GetMapping("/test/mq/redis")
    public String redisTest() {
        List<Result> results = judgerService.getResultFromRedis("avbkdj");
        return "yes";
    }
}
