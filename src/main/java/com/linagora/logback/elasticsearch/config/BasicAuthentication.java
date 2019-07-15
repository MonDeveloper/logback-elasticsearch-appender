package com.linagora.logback.elasticsearch.config;

import com.linagora.logback.elasticsearch.util.Base64;

import java.net.HttpURLConnection;

public class BasicAuthentication implements Authentication {
    public void addAuth(HttpURLConnection urlConnection, String body) {
        String userInfo = urlConnection.getURL().getUserInfo();
        if (userInfo != null) {
            String basicAuth = "Basic " + Base64.encode(userInfo.getBytes());
            urlConnection.setRequestProperty("Authorization", basicAuth);
        }
    }
}
