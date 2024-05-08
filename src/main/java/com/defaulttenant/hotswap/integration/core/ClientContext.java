package com.defaulttenant.hotswap.integration.core;

import com.netease.cloud.RemoteCallApi;


import java.util.HashMap;
import java.util.Map;

public class ClientContext {
    private final static Map<String, RemoteCallApi> context = new HashMap<>();
    public static void register(final String code, final RemoteCallApi remoteCallApi) {
        context.put(code, remoteCallApi);
    }
    public static RemoteCallApi get(final String code) {
        if ("HTTPS".equalsIgnoreCase(code)) {
            return context.get("HTTP");
        }
        return context.get(code);
    }
}
