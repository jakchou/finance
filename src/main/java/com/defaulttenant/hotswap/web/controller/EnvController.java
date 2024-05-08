package com.defaulttenant.hotswap.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.GetMapping;
import com.defaulttenant.hotswap.config.RemoteUserCenterProperties;
import com.defaulttenant.hotswap.service.system.configuration.NaslConfigurationComponent;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * Get app properties
 *
 * @author ifcc
 */
@RestController
public class EnvController {
   @Autowired
   private RemoteUserCenterProperties remoteUserCenterConfig;

    @Autowired
    private NaslConfigurationComponent naslConfigurationComponent;

   private static final ObjectMapper MAPPER = new ObjectMapper();

   @GetMapping("/api/system/config")
   public Map<String, String> getEvnConfig() throws JsonProcessingException {
       Map<String, String> result = new HashMap<>();
       result.put("userCenter", MAPPER.writeValueAsString(remoteUserCenterConfig));
       return result;
   }

   @GetMapping("/api/system/getCustomConfig/{configKey}")
   public String getCustomConfig(@PathVariable("configKey") String configKey, @RequestParam(value = "group", required = false) String group) {
        return naslConfigurationComponent.getUnPrivateCustomConfig(group, configKey);
   }
}
