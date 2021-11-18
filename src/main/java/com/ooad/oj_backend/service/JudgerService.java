package com.ooad.oj_backend.service;

import com.google.gson.Gson;
import com.ooad.oj_backend.rabbitmq.controller.PacketSender;
import com.ooad.oj_backend.rabbitmq.entity.JudgeDetail;
import com.ooad.oj_backend.rabbitmq.entity.Result;
import com.ooad.oj_backend.rabbitmq.entity.SendPacket;
import com.ooad.oj_backend.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class JudgerService {
    @Autowired
    PacketSender packetSender;
    @Autowired
    RedisUtil redisUtil;

    public String judge(String testCaseFile, JudgeDetail judgeDetail) {
        String uuid = UUID.randomUUID().toString();
        SendPacket sendPacket = new SendPacket(uuid, 0, testCaseFile, null, judgeDetail);
        packetSender.sendPacket(sendPacket);
        return uuid;
    }

    public String testTestCase(String testCase, JudgeDetail userJudgeDetail, JudgeDetail answerJudgeDetail) {
        String uuid = UUID.randomUUID().toString();
        SendPacket userPacket = new SendPacket("u" + uuid, 1, null, testCase, userJudgeDetail);
        SendPacket answerPacket = new SendPacket("a" + uuid, 1, null, testCase, userJudgeDetail);
        packetSender.sendPacket(userPacket);
        packetSender.sendPacket(answerPacket);
        return uuid;
    }

    public List<Result> getResultFromRedis(String uuid) {
        Map<Object, Object> map = redisUtil.hGetAll(uuid);
        Collection<Object> coll = map.values();
        ArrayList<Result> results = new ArrayList<>();
        Gson gson = new Gson();
        for (Object o : coll) {
            results.add(gson.fromJson((String) o, Result.class));
        }
        results.sort(Comparator.comparingInt(Result::getId));
        return results;
    }

    public Result[] getTestResult(String uuid) {
        Gson gson = new Gson();
        Map<Object, Object> map = redisUtil.hGetAll(uuid);
        if (map.containsKey("a") && map.containsKey("u")) {
            Result[] ans = new Result[2];
            ans[0] = gson.fromJson((String) map.get("u"), Result.class);
            ans[1] = gson.fromJson((String) map.get("a"), Result.class);
            return ans;
        }
        return null;
    }

//    public List<Result> getJudgeResults(String uuid) {
//
//    }


}
