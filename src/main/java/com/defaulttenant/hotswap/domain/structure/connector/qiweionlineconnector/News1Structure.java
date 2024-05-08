package com.defaulttenant.hotswap.domain.structure.connector.qiweionlineconnector; 

import com.fasterxml.jackson.annotation.JsonAutoDetect; 
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility; 
import java.util.ArrayList; 
import java.util.List; 

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,getterVisibility = JsonAutoDetect.Visibility.NONE)
public class News1Structure {

    public List<com.defaulttenant.hotswap.domain.structure.connector.qiweionlineconnector.Articles1Structure> articles = new ArrayList<>();
    public List<com.defaulttenant.hotswap.domain.structure.connector.qiweionlineconnector.Articles1Structure> getArticles() {
        return articles;
    } 

    public void setArticles(List<com.defaulttenant.hotswap.domain.structure.connector.qiweionlineconnector.Articles1Structure> articles) {
        this.articles = articles; 
    } 


}
