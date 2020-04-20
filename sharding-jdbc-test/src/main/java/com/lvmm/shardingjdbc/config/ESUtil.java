package com.lvmm.shardingjdbc.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.ml.PostDataRequest;

import java.io.IOException;

public class ESUtil {

       public static boolean cereateIndex(RestHighLevelClient client,Object obj) throws IOException {
           ObjectMapper mapper = new ObjectMapper();
           String jsonStr=mapper.writeValueAsString(obj);
           System.out.println(jsonStr);
           IndexRequest indexRequest = new IndexRequest("posts")
                   .id("1").source(jsonStr);
           IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
          String response= indexResponse.toString();
           System.out.println(response);
           return true;
       }
}
