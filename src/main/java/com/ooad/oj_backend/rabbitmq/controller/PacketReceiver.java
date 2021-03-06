package com.ooad.oj_backend.rabbitmq.controller;

import com.google.gson.Gson;
import com.ooad.oj_backend.rabbitmq.MqUtil;
import com.ooad.oj_backend.rabbitmq.entity.Color;
import com.ooad.oj_backend.rabbitmq.entity.RecvPacket;
import com.ooad.oj_backend.rabbitmq.entity.Result;
import com.ooad.oj_backend.redis.RedisUtil;
import com.ooad.oj_backend.service.JudgerService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@RabbitListener(queues = MqUtil.RESPONSE_QUEUE)
public class PacketReceiver {
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    JudgerService judgerService;

    @RabbitHandler
    public void receivePacket(byte[] bytes) {
        System.out.println(new String(bytes));
        RecvPacket recvPacket = RecvPacket.fromString(new String(bytes));
        if (recvPacket.getType() == 0) { //basic
            redisUtil.hPut(recvPacket.getSubmitId(), ""+recvPacket.getResult().getId(), (new Gson()).toJson(recvPacket.getResult()));
            redisUtil.expire(recvPacket.getSubmitId(), 1, TimeUnit.HOURS);


        } else if (recvPacket.getType() == 1) { //end
            if (redisUtil.hExists("judge", recvPacket.getSubmitId())) {
                List<Result> checkPoints = judgerService.getResultFromRedis(recvPacket.getSubmitId());
                com.ooad.oj_backend.mybatis.entity.Result result = judgerService.getSubmitDetail(recvPacket.getSubmitId());
                judgerService.setResultToSql(result, checkPoints);
                redisUtil.hDelete("judge", recvPacket.getSubmitId());
                redisUtil.delete(recvPacket.getSubmitId());
            }

        } else if (recvPacket.getType() == 2) {
            String totalSubmitId = recvPacket.getSubmitId();
            if (totalSubmitId.charAt(0) == 'a') {
                redisUtil.hPut(totalSubmitId.substring(1), "a", (new Gson()).toJson(recvPacket.getResult()));
                redisUtil.expire(recvPacket.getSubmitId(), 1, TimeUnit.HOURS);
            } else if (totalSubmitId.charAt(0) == 'u') {
                redisUtil.hPut(totalSubmitId.substring(1), "u", (new Gson()).toJson(recvPacket.getResult()));
                redisUtil.expire(recvPacket.getSubmitId(), 1, TimeUnit.HOURS);
            }
        } else if (recvPacket.getType() == -1) {
            String realSubmitId = recvPacket.getSubmitId();
            realSubmitId = realSubmitId.length() == 36 ? realSubmitId : realSubmitId.substring(1);
            Map<Object, Object> map = redisUtil.hGetAll(realSubmitId);
            if (map.containsKey("r")) {
                redisUtil.hPut(realSubmitId, "r", "ERROR");
                redisUtil.expire(realSubmitId, 1, TimeUnit.HOURS);
            } else {
                redisUtil.hPut(realSubmitId, "-1", (new Gson()).toJson(new Result(0,0,false,0,0,"ERR","Judge Server Error", "Judge Server Error", Color.GRAY)));
                redisUtil.hDelete("judge", realSubmitId);
                redisUtil.expire(realSubmitId, 1, TimeUnit.HOURS);
            }
        }

    }

}
