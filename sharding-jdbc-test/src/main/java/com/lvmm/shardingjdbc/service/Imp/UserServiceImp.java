package com.lvmm.shardingjdbc.service.Imp;

import com.lvmm.shardingjdbc.entitys.UserEntity;
import com.lvmm.shardingjdbc.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImp implements UserService {
    @Override
    public UserEntity findAll() {
        log.info("hello world");
        return null;
    }
}
