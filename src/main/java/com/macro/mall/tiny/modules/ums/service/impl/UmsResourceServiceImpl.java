package com.macro.mall.tiny.modules.ums.service.impl;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hazelcast.core.HazelcastInstance;
import com.macro.mall.tiny.common.constant.Constant;
import com.macro.mall.tiny.config.OrikaMapper;
import com.macro.mall.tiny.modules.ums.dto.UmsResourceDto;
import com.macro.mall.tiny.modules.ums.mapper.UmsResourceMapper;
import com.macro.mall.tiny.modules.ums.model.UmsResource;
import com.macro.mall.tiny.modules.ums.service.UmsResourceService;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * 后台资源管理Service实现类
 * Created by macro on 2020/2/2.
 */
@Service
public class UmsResourceServiceImpl extends ServiceImpl<UmsResourceMapper, UmsResource> implements UmsResourceService {
    @Autowired
    private UmsResourceMapper resourceMapper;
    @Autowired
    private HazelcastInstance hazelcastInstance;

    @Override
    public boolean create(UmsResource umsResource) {
        umsResource.setCreateTime(new Date());
        return save(umsResource);
    }

    @Override
    public boolean update(Long id, UmsResource umsResource) {
        umsResource.setId(id);
        boolean success = updateById(umsResource);
        //adminCacheService.delResourceListByResource(id);
        return success;
    }

    @Override
    public boolean delete(Long id) {
        boolean success = removeById(id);
       // adminCacheService.delResourceListByResource(id);
        return success;
    }

    @Override
    public Page<UmsResource> list(Long categoryId, String nameKeyword, String urlKeyword, Integer pageSize, Integer pageNum) {
        Page<UmsResource> page = new Page<>(pageNum,pageSize);
        QueryWrapper<UmsResource> wrapper = new QueryWrapper<>();
        LambdaQueryWrapper<UmsResource> lambda = wrapper.lambda();
        if(categoryId!=null){
            //lambda.eq(UmsResource::getCategoryId,categoryId);
        }
        if(StrUtil.isNotEmpty(nameKeyword)){
            lambda.like(UmsResource::getName,nameKeyword);
        }
        if(StrUtil.isNotEmpty(urlKeyword)){
            lambda.like(UmsResource::getUrl,urlKeyword);
        }
        return page(page,wrapper);
    }


    @Override
    public List<UmsResourceDto> listAll() {
        List<UmsResource> temp =getSourceWithCache();
        List<UmsResourceDto> list = OrikaMapper.mapList(temp,UmsResource.class,UmsResourceDto.class);
        Map<Long,List<UmsResourceDto>> map = list.stream().collect(Collectors.groupingBy(UmsResourceDto::getParentId));
        List<UmsResourceDto> rootList= list.stream().filter(e->e.getParentId()==0 ).collect(Collectors.toList());
        for (UmsResourceDto root : rootList){
            List<UmsResourceDto> leafList =  map.get(root.getId());
            root.setChildList(leafList);
            if(!CollectionUtils.isEmpty(leafList)){
                for (UmsResourceDto leaf : leafList){
                    List<UmsResourceDto> child =  map.get(leaf.getId());
                    if(!CollectionUtils.isEmpty(child)){
                        child = child.stream().filter(e -> e.getHide() ==0).collect(Collectors.toList());
                        leaf.setChildList(CollectionUtils.isEmpty(child)?null:child);
                        for (UmsResourceDto three : child){
                            List<UmsResourceDto> last =  map.get(three.getId());
                            if(!CollectionUtils.isEmpty(last)){
                                three.setChildList(last.stream().filter(e -> e.getHide() ==0).collect(Collectors.toList()));
                            }
                        }

                    }

                }
            }

        }
        return rootList;
    }

    @Override
    public List<UmsResource> getSourceWithCache(){
        List<UmsResource> list = hazelcastInstance.getList(Constant.CACHE_LIST_NAME+"SOURCE");
        if(CollectionUtils.isEmpty(list)){
            QueryWrapper wrapper =new QueryWrapper();
            wrapper.orderByAsc(Arrays.asList("level","id"));
            list =resourceMapper.selectList(wrapper);

            hazelcastInstance.getList(Constant.CACHE_LIST_NAME+"SOURCE").addAll(list);
        }
        return list;
    }



}
