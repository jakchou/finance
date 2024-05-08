package com.defaulttenant.hotswap.domain; 

import com.fasterxml.jackson.annotation.JsonAutoDetect; 
import java.io.Serializable; 
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility; 
import com.defaulttenant.hotswap.util.CommonFunctionUtil; 
import java.util.Objects; 

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,getterVisibility = JsonAutoDetect.Visibility.NONE)
public class SessionVariables implements Serializable{

    @javax.validation.constraints.NotNull
    public com.defaulttenant.hotswap.domain.http.HttpRequest<String> httpRequest = new com.defaulttenant.hotswap.domain.http.HttpRequest<>();
    @javax.validation.constraints.NotNull
    public com.defaulttenant.hotswap.domain.http.HttpResponse<String> httpResponse = CommonFunctionUtil.newWithInitiation(new com.defaulttenant.hotswap.domain.http.HttpResponse<String>(), _bean601 -> {});
    @javax.validation.constraints.NotNull
    public com.netease.lowcode.auth.domain.LCAPUser currentUser = new com.netease.lowcode.auth.domain.LCAPUser();
    private <Source, Target> Boolean equals(Source source, Target target) {
        return Objects.equals(source, target);
    } 


}
