package com.ooad.oj_backend.service;

import cn.dev33.satoken.stp.StpUtil;
import com.google.gson.Gson;
import com.ooad.oj_backend.rabbitmq.controller.PacketSender;
import com.ooad.oj_backend.rabbitmq.entity.JudgeDetail;
import com.ooad.oj_backend.rabbitmq.entity.Result;
import com.ooad.oj_backend.rabbitmq.entity.SendPacket;
import com.ooad.oj_backend.redis.RedisUtil;
import com.ooad.oj_backend.service.record.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class JudgerService {
    @Autowired
    PacketSender packetSender;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    RecordService recordService;

    public String judge(String testCaseFile, JudgeDetail judgeDetail, int problemId) {
        String uuid = UUID.randomUUID().toString();
        String userId = (String) StpUtil.getLoginId();
        redisUtil.hPut("judge", uuid, new Gson().toJson(new com.ooad.oj_backend.mybatis.entity.Result(uuid, System.currentTimeMillis()*1000, userId, problemId)));
        SendPacket sendPacket = new SendPacket(uuid, 0, testCaseFile, null, judgeDetail);
        packetSender.sendPacket(sendPacket);
        return uuid;
    }

    public boolean judgeRunning(String submitId) {
        return redisUtil.hExists("judge", submitId);
    }

    public String testTestCase(String testCase, JudgeDetail userJudgeDetail, JudgeDetail answerJudgeDetail) {
        String uuid = UUID.randomUUID().toString();
        redisUtil.hPut(uuid, "r", "RUNNING");
        SendPacket userPacket = new SendPacket("u" + uuid, 1, null, testCase, userJudgeDetail);
        SendPacket answerPacket = new SendPacket("a" + uuid, 1, null, testCase, answerJudgeDetail);
        packetSender.sendPacket(userPacket);
        packetSender.sendPacket(answerPacket);
        return uuid;
    }

    public boolean testTestCaseRunning(String testId) {
        return redisUtil.hExists(testId, "r");
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

    public void setResultToSql(com.ooad.oj_backend.mybatis.entity.Result result, List<Result> checkPoints) {
        recordService.addResult(result, checkPoints);
    }

    public com.ooad.oj_backend.mybatis.entity.Result getSubmitDetail(String submitId) {
        String resultStr = (String) redisUtil.hGet("judge", submitId);
        com.ooad.oj_backend.mybatis.entity.Result output = new Gson().fromJson(resultStr, com.ooad.oj_backend.mybatis.entity.Result.class);
        return output;
    }


    public Result[] getTestResult(String uuid) {
        Gson gson = new Gson();
        Map<Object, Object> map = redisUtil.hGetAll(uuid);
        if (map.containsKey("a") && map.containsKey("u")) {
            Result[] ans = new Result[2];
            ans[0] = gson.fromJson((String) map.get("u"), Result.class);
            ans[1] = gson.fromJson((String) map.get("a"), Result.class);
            redisUtil.delete(uuid);
            return ans;
        }
        return null;
    }

//    public List<Result> getJudgeResults(String uuid) {
//
//    }


}
