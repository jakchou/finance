package com.macro.mall.tiny.modules.buss.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hazelcast.core.HazelcastInstance;
import com.macro.mall.tiny.common.constant.Constant;
import com.macro.mall.tiny.config.OrikaMapper;
import com.macro.mall.tiny.modules.buss.dto.UmsColumnDto;
import com.macro.mall.tiny.modules.buss.model.UmsColumn;
import com.macro.mall.tiny.modules.buss.model.UmsNode;
import com.macro.mall.tiny.modules.buss.mapper.UmsNodeMapper;
import com.macro.mall.tiny.modules.buss.model.UmsNodeChangeFile;
import com.macro.mall.tiny.modules.buss.service.UmsNodeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * <p>
 * 节点对象 服务实现类
 * </p>
 *
 * @author macro
 * @since 2024-03-28
 */
@Service
public class UmsNodeServiceImpl extends ServiceImpl<UmsNodeMapper, UmsNode> implements UmsNodeService {

    private static final String cache = "NODE";
    @Autowired
    private UmsNodeMapper nodeMapper;

    @Autowired
    private HazelcastInstance hazelcastInstance;

    @Override
    public List<UmsNode> selectListByColumn(Long columnId) {
        List<UmsNode> nodeList = (List<UmsNode>) hazelcastInstance.getMap(Constant.CACHE_IMAP_NAME+cache).get(columnId);
        if(!CollectionUtils.isEmpty(nodeList)){
            return nodeList;
        }
        nodeList= lambdaQuery().eq(UmsNode::getColumnId,columnId).orderByAsc(UmsNode::getSort).list();
        if(CollectionUtils.isEmpty(nodeList)){
            return nodeList;
        }
        hazelcastInstance.getMap(Constant.CACHE_IMAP_NAME+cache).put(columnId,nodeList);
        return nodeList;
    }

    @Override
    public void deleteBatchIds(Long columnId, List<Long> nodeIds) {
        QueryWrapper<UmsNode> wrapper= new QueryWrapper<>();
        wrapper.in("id",nodeIds);
        nodeMapper.delete(wrapper);
        hazelcastInstance.getMap(Constant.CACHE_IMAP_NAME+cache).remove(columnId);
    }

    @Override
    public void insert(Long columnId, UmsNode node) {
        nodeMapper.insert(node);
        hazelcastInstance.getMap(Constant.CACHE_IMAP_NAME+cache).remove(columnId);
    }

    @Override
    public void updateEntity(UmsNode node) {
        nodeMapper.updateById(node);
        hazelcastInstance.getMap(Constant.CACHE_IMAP_NAME+cache).remove(node.getColumnId());
    }
}
