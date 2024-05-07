package com.macro.mall.tiny;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ThreadPoolExecutor;

@SpringBootTest
public class MallTinyApplicationTests {

    @Autowired
    private ThreadPoolExecutor orderThread;

    @Test
    public void contextLoads() {
  }
}
