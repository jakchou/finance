package com.lvmm.shardingjdbc.service.Imp;

import com.lvmm.shardingjdbc.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
//@PropertySource(value = {"classpath:dev.properties"})
public class FileServiceImp implements FileService {
    @Value("${file.downloaduil}")
    private String uploadURL;


    @Override
    public void saveFIle() {
        System.out.println("上传路径：" +uploadURL);
    }
}
