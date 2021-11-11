package com.ooad.oj_backend.rabbitmq.controller;

import com.google.gson.Gson;
import com.ooad.oj_backend.rabbitmq.MqUtil;
import com.ooad.oj_backend.rabbitmq.entity.RecvPacket;
import com.ooad.oj_backend.redis.RedisUtil;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Component
@RabbitListener(queues = MqUtil.RESPONSE_QUEUE)
public class PacketReceiver {
    @Autowired
    RedisUtil redisUtil;

    @RabbitHandler
    public void recievePacket(byte[] bytes) {
        RecvPacket recvPacket = RecvPacket.fromString(new String(bytes));
        if (recvPacket.getType() == 0) { //basic
            System.out.println(recvPacket.getResult().getMessage());
            redisUtil.hPut(recvPacket.getSubmitId(), ""+recvPacket.getResult().getId(), (new Gson()).toJson(recvPacket.getResult()));
            redisUtil.expire(recvPacket.getSubmitId(), 1, TimeUnit.HOURS);
        } else if (recvPacket.getType() == 1) { //end
            //
            //数据库操作
            //
            //
//            redisUtil.del(recvPacket.getSubmitId());
        }

    }

}
