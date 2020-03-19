package com.lvmm.shardingjdbc.service.Imp;

import com.lvmm.shardingjdbc.entitys.UserEntity;
import com.lvmm.shardingjdbc.respository.UserRespository;
import com.lvmm.shardingjdbc.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserServiceImp implements UserService {

    @Autowired
    private UserRespository userRespository;

    @Override
    public UserEntity findAll() {
      List<UserEntity> userList=  userRespository.findAll();
        log.info("hello world");
        System.out.println(userList);
        return null;
    }
}
