package com.AAZl3l4.UserServe.service.impl;

import com.AAZl3l4.UserServe.service.FaceService;
import com.baidu.aip.face.AipFace;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@Slf4j
@Tag(name = "人脸服务")
public class FaceServiceImpl implements FaceService {

    @Autowired
    private AipFace aipFace;

    // 注册人脸
    @Operation(summary = "注册人脸")
    public boolean registerFace(String base64, String groupId, String userId) {
        // 前端传过来的 image是 data:image/xxx;base64,xxx 形式 先去掉头部
        String substring = base64.substring(base64.indexOf(",") + 1);

        HashMap<String, String> options = new HashMap<>();

        // 质量检测(正常)
        options.put("quality_control", "NORMAL");
        // 活体检测(低)
        options.put("liveness_control", "LOW");
        // 覆盖旧图
        options.put("action_type", "REPLACE");

        JSONObject value = aipFace.addUser(substring, "BASE64", groupId, userId, options);

        if (value.getInt("error_code")==0){
            return true;
        }else {
            log.error("百度人脸注册失败：{}",value.getString("error_msg"));
            return false;
        }
    }

    // 更新人脸
    @Operation(summary = "更新人脸")
    public boolean updateFace(String base64, String groupId, String userId) {
        // 前端传过来的 image是 data:image/xxx;base64,xxx 形式 先去掉头部
        String substring = base64.substring(base64.indexOf(",") + 1);

        HashMap<String, String> options = new HashMap<>();
        options.put("quality_control", "NORMAL");
        options.put("liveness_control", "LOW");
        // UPDATE 会一次替换所有旧图
        options.put("action_type", "UPDATE");

        JSONObject value = aipFace.updateUser(substring, "BASE64", groupId, userId, options);

        if (value.getInt("error_code")==0){
            return true;
        }else {
            log.error("百度人脸更新失败：{}",value.getString("error_msg"));
            return false;
        }
    }

    // 对比人脸
    @Operation(summary = "对比人脸")
    public boolean compareWithUser(String base64, String groupId, String userId) {
        // 前端传过来的 image是 data:image/xxx;base64,xxx 形式 先去掉头部
        String substring = base64.substring(base64.indexOf(",") + 1);

        HashMap<String, Object> options = new HashMap<>();
        options.put("user_id", userId);
        options.put("quality_control", "NORMAL");
        options.put("liveness_control", "LOW");

        JSONObject res = aipFace.search(substring, "BASE64", groupId, options);
        int errCode = res.getInt("error_code");
        if (errCode != 0) {
            return false;                      // 未找到或异常
        }
        JSONArray list = res.getJSONObject("result")
                .getJSONArray("user_list");
        if (list.length() == 0) {
            System.out.println(list);
            return false;
        }

        double score = list.getJSONObject(0).getDouble("score");
        return score >= 80;

    }
}