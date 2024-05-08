package com.defaulttenant.hotswap.task.permission.model;


/**
* 应用发布时逻辑鉴权需要使用到的元数据
*
* @author sys
* @since 2.22.0
*/

public class DeployLogicAuthMetaData {
    private String uiPath;
    private String type;

    public String getUiPath() {
        return uiPath;
    }

    public void setUiPath(String uiPath) {
        this.uiPath = uiPath;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "DeployLogicAuthMetaData{" +
                "uiPath='" + uiPath + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
