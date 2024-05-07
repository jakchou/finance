package com.macro.mall.tiny.modules.buss.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hazelcast.core.HazelcastInstance;
import com.macro.mall.tiny.common.constant.Constant;
import com.macro.mall.tiny.modules.buss.model.UmsNode;
import com.macro.mall.tiny.modules.buss.model.UmsNodeFile;
import com.macro.mall.tiny.modules.buss.mapper.UmsNodeFileMapper;
import com.macro.mall.tiny.modules.buss.service.UmsNodeFileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 节点文件 服务实现类
 * </p>
 *
 * @author macro
 * @since 2024-03-28
 */
@Service
public class UmsNodeFileServiceImpl extends ServiceImpl<UmsNodeFileMapper, UmsNodeFile> implements UmsNodeFileService {
    @Autowired
    private UmsNodeFileMapper nodeFileMapper;
    private static final String cache = "NFILE";
    @Autowired
    private HazelcastInstance hazelcastInstance;


    @Override
    public void deleteByNodeIds(List<Long> nodeIds) {
        nodeFileMapper.delete(new QueryWrapper<UmsNodeFile>().in("node_id",nodeIds));
        nodeIds.stream().forEach(e->hazelcastInstance.getMap(Constant.CACHE_IMAP_NAME+cache).remove(e));
    }

    @Override
    public void saveBatchs(List<UmsNodeFile> files) {
        saveBatch(files);
        List<Long> nodeIds = files.stream().map(UmsNodeFile::getNodeId).distinct().collect(Collectors.toList());
        nodeIds.stream().forEach(e->hazelcastInstance.getMap(Constant.CACHE_IMAP_NAME+cache).remove(e));
    }

    @Override
    public List<UmsNodeFile> selectListByNodeId(Long nodeId) {
        List<UmsNodeFile> files = (List<UmsNodeFile>) hazelcastInstance.getMap(Constant.CACHE_IMAP_NAME+cache).get(nodeId);
        if(!CollectionUtils.isEmpty(files)){
            return files;
        }
        files = lambdaQuery().eq(UmsNodeFile::getNodeId,nodeId).orderByAsc(UmsNodeFile::getSort).list();
        if(CollectionUtils.isEmpty(files)){
            return files;
        }
        hazelcastInstance.getMap(Constant.CACHE_IMAP_NAME+cache).put(nodeId,files);
        return files;
    }
}
