package com.AAZl3l4.UserServe.service;


import org.json.JSONObject;

public interface FaceService {
    public boolean registerFace(String base64, String groupId, String userId);
    public boolean updateFace(String base64, String groupId, String userId);
    public boolean compareWithUser(String base64, String groupId, String userId);
}
