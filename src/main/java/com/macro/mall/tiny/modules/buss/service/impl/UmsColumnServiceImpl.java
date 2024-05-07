package com.macro.mall.tiny.modules.buss.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hazelcast.core.HazelcastInstance;
import com.macro.mall.tiny.common.constant.*;
import com.macro.mall.tiny.config.OrikaMapper;
import com.macro.mall.tiny.modules.buss.dto.*;
import com.macro.mall.tiny.modules.buss.mapper.*;
import com.macro.mall.tiny.modules.buss.model.*;
import com.macro.mall.tiny.modules.buss.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macro.mall.tiny.modules.ums.dto.UmsMenuNode;
import com.macro.mall.tiny.modules.ums.service.UmsMenuService;
import com.macro.mall.tiny.security.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单关联节点表 服务实现类
 * </p>
 *
 * @author zhouzz
 * @since 2024-03-28
 */
@Service
public class UmsColumnServiceImpl extends ServiceImpl<UmsColumnMapper, UmsColumn> implements UmsColumnService {


    private  final String cache ="column";
    @Autowired
    private UmsNodeChangeFileService changeFileService;

    @Autowired
    private UmsNodeFileService nodeFileService;

    @Autowired
    private UmsNodeChangeSubmitService changeSubmitService;
    @Autowired
    private UmsNodeChangeDetailService changeDetailService;

    @Autowired
    private UmsWorkChangeService workChangeService;

    @Autowired
    private UmsWorkService workService;

    @Autowired
    private BaseDictService dictService;

    @Autowired
    private UmsDepartmentService departmentService;
    @Autowired
    private UmsContractService contractService;
    @Autowired
    private UmsMenuService menuService;
    @Autowired
    private UmsNodeService nodeService;

    @Autowired
    private UmsNodeChangeReviewService reviewService;

    @Autowired
    private UmsColumnMapper columnMapper;

    @Autowired
    private HazelcastInstance hazelcastInstance;
    @Override
    public Long updateSource(UmsColumnChangeDto colum) {
        //一个变更多次提交，保存一次变更记录，一次提交记录。多次审核记录。
        if(Objects.isNull(colum.getId())){
            throw new RuntimeException("节点不能为空");
        }
        UmsNodeChangeSubmit tmp = colum.getSubmit();
        UmsNodeChangeSubmit submit = new UmsNodeChangeSubmit();
        if(Objects.nonNull(tmp)){
            submit.setId(submit.getId());
            List<UmsNodeChangeDetail>  exitChangeList = changeDetailService.selectListBySumitId(submit.getId());
            if(!CollectionUtils.isEmpty(exitChangeList)){
                List<Long> exitChangeIds = exitChangeList.stream().map(UmsNodeChangeDetail::getId).collect(Collectors.toList());
                changeDetailService.removeBatchByIds(exitChangeIds);
                changeFileService.deleteByNodeIds(exitChangeIds);
            }
        }
        submit.setChangeType(BusinessType.INSERT.ordinal());
        submit.setStatus(SubmitStatus.WAIT.ordinal());
        submit.setProcessStatus(ProcessStatus.WAIT.ordinal());
        changeSubmitService.saveOrUpdate(submit);

        //标价下必须有节点不然报错
        if(CollectionUtils.isEmpty(colum.getNodeDtoList())){
            throw new RuntimeException("节点不能为空");
        }
        List<UmsNodeChangeDetailDto> changeList = colum.getNodeDtoList();
        for (UmsNodeChangeDetailDto umsNodeChangeDetailDto : changeList) {
            UmsNodeChangeDetail changeDetail = OrikaMapper.map(umsNodeChangeDetailDto, UmsNodeChangeDetail.class);
            changeDetail.setSubmitId(submit.getId());
            changeDetail.setChangeType(BusinessType.UPDATE.ordinal());
            changeDetail.setCategoryType(CategoryType.COMMON.ordinal());
            changeDetail.setProcessStatus(ProcessStatus.WAIT.ordinal());
            changeDetailService.insert(changeDetail);
            List<UmsNodeChangeFileDto> changeFileDtos = umsNodeChangeDetailDto.getFileDtoList();
            if(!CollectionUtils.isEmpty(changeFileDtos)){
                List<UmsNodeChangeFile> fileList = OrikaMapper.mapList(changeFileDtos,UmsNodeChangeFileDto.class, UmsNodeChangeFile.class);
                fileList.stream().forEach(e->{
                    e.setNodeId(changeDetail.getId());
                });
                changeFileService.saveBatchs(fileList);
            }

        }
        return submit.getId();
    }

    @Override
    public void reviewSource(UmsNodeChangeReviewDto reviewDto) {
        UmsNodeChangeSubmit submit = changeSubmitService.selectById(reviewDto.getSubmitId());
        if (Objects.isNull(submit)) {
            return;
        }

        if(submit.getProcessStatus() == ProcessStatus.finish.ordinal()){
            return;
        }

        if (reviewDto.getStatus() == SubmitStatus.REJECT.ordinal()) {
              submit.setStatus(reviewDto.getStatus());
              submit.setProcessStatus(ProcessStatus.ing.ordinal());
              submit.setStatus(reviewDto.getStatus());
              changeSubmitService.updateEntityById(submit);
              UmsNodeChangeReview review = OrikaMapper.map(reviewDto, UmsNodeChangeReview.class);
              review.setCreatePerson(SecurityUtils.getUserInfo().getUmsAdmin().getId());
              reviewService.insert(review);
              return;
        }

        if(reviewDto.getStatus().equals(SubmitStatus.Agree.ordinal())){
            List<UmsNodeChangeDetail> detailList = changeDetailService.selectListBySumitId(reviewDto.getSubmitId());
            List<Long> ids = detailList.stream().map(UmsNodeChangeDetail::getId).collect(Collectors.toList());
            List<Long> columnIds = detailList.stream().map(UmsNodeChangeDetail::getColumnId).collect(Collectors.toList());
            if(columnIds.size()>=2){
                throw new RuntimeException("只能选择一个栏目");
            }

            List<UmsNodeChangeFile>  changeFileList = changeFileService.selectListByNodeIds(ids);
            Map<Long,List<UmsNodeChangeFile>> fileMap = new HashMap<>();
            if(!CollectionUtils.isEmpty(changeFileList)){
                fileMap =  changeFileList.stream().collect(Collectors.groupingBy(UmsNodeChangeFile::getNodeId));
            }

            UmsColumn column = lambdaQuery().eq(UmsColumn::getId,columnIds.get(0)).one();
            //新闻审核
            if(column.getCategoryType() == CategoryType.NEWS.ordinal()){
                for (UmsNodeChangeDetail detail: detailList){
                    nodeFileService.deleteByNodeIds(Arrays.asList(detail.getNodeId()));
                    UmsNode node = OrikaMapper.map(detail, UmsNode.class);
                    if(detail.getNodeId()>=0){
                        node.setId(detail.getNodeId());
                        nodeService.updateEntity(node);
                    }else{
                        nodeService.insert(columnIds.get(0),node);
                    }
                    List<UmsNodeChangeFile> fileList = fileMap.get(detail.getId());
                    List<UmsNodeFile> files= new ArrayList<>();
                    if(!CollectionUtils.isEmpty(fileList)){
                        files = OrikaMapper.mapList(fileList,UmsNodeChangeFile.class,UmsNodeFile.class);
                        files.stream().forEach(e->{
                            e.setNodeId(node.getId());
                        });
                        nodeFileService.saveBatchs(files);
                    }
                }
                //工作审核
            }else if(column.getCategoryType() == CategoryType.JOBS.ordinal()){

                //一般节点审核
            }else{
                List<UmsNode> nodeList = nodeService.selectListByColumn(columnIds.get(0));
                List<Long> nodeIds = nodeList.stream().map(UmsNode::getId).collect(Collectors.toList());
                if(!CollectionUtils.isEmpty(nodeIds)){
                    nodeService.deleteBatchIds(columnIds.get(0),nodeIds);
                    nodeFileService.deleteByNodeIds(nodeIds);
                }
                for (UmsNodeChangeDetail umsNodeChangeDetail : detailList) {
                    List<UmsNodeChangeFile> fileList = fileMap.get(umsNodeChangeDetail.getId());
                    UmsNode node = OrikaMapper.map(umsNodeChangeDetail, UmsNode.class);
                    node.setId(null);
                    nodeService.insert(columnIds.get(0),node);
                    if(!CollectionUtils.isEmpty(fileList)){
                        List<UmsNodeFile> files = OrikaMapper.mapList(fileList,UmsNodeChangeFile.class,UmsNodeFile.class);
                        files.stream().forEach(e->e.setNodeId(node.getId()));
                        nodeFileService.saveBatchs(files);
                    }
                }
            }
            submit.setStatus(reviewDto.getStatus());
            submit.setProcessStatus(ProcessStatus.finish.ordinal());
            submit.setStatus(reviewDto.getStatus());
            changeSubmitService.updateEntityById(submit);
            UmsNodeChangeReview review = OrikaMapper.map(reviewDto, UmsNodeChangeReview.class);
            review.setCreatePerson(SecurityUtils.getUserInfo().getUmsAdmin().getId());
            reviewService.insert(review);

        }
    }

    @Override
    public Long saveNewSource(UmsNodeChangeDetailDto detailDto) {
        Long submitId = detailDto.getSubmitId();
        if(Objects.nonNull(submitId) && submitId>0){
            List<UmsNodeChangeDetail>  exitChangeList = changeDetailService.selectListBySumitId(submitId);
            if(!CollectionUtils.isEmpty(exitChangeList)){
                List<Long> exitChangeIds = exitChangeList.stream().map(UmsNodeChangeDetail::getId).collect(Collectors.toList());
                changeDetailService.removeBatchByIds(exitChangeIds);
                changeFileService.deleteByNodeIds(exitChangeIds);
            }
        }

        UmsNodeChangeSubmit submit = new UmsNodeChangeSubmit();
        submit.setCreateTime(new Date());
        submit.setStatus(SubmitStatus.WAIT.ordinal());
        submit.setChangeType(BusinessType.INSERT.ordinal());
        submit.setProcessStatus(ProcessStatus.WAIT.ordinal());
        changeSubmitService.saveOrUpdate(submit);

        UmsNodeChangeDetail changeDetail = OrikaMapper.map(detailDto, UmsNodeChangeDetail.class);
        changeDetail.setSubmitId(submit.getId());
        changeDetail.setChangeType(BusinessType.INSERT.ordinal());
        changeDetail.setCategoryType(CategoryType.NEWS.ordinal());
        changeDetailService.insert(changeDetail);

        List<UmsNodeChangeFileDto> changeFileDtos = detailDto.getFileDtoList();
        if(!CollectionUtils.isEmpty(changeFileDtos)){
            List<UmsNodeChangeFile> fileList = OrikaMapper.mapList(changeFileDtos,UmsNodeChangeFileDto.class, UmsNodeChangeFile.class);
            fileList.stream().forEach(e->{
                e.setNodeId(changeDetail.getId());
            });
            changeFileService.saveBatchs(fileList);
        }
        return submit.getId();
    }

    @Override
    public Long updateNewSource(UmsNodeChangeDetailDto detailDto) {
        UmsNodeChangeSubmit submit = new UmsNodeChangeSubmit();
        submit.setCreateTime(new Date());
        submit.setChangeType(BusinessType.UPDATE.ordinal());
        submit.setStatus(SubmitStatus.WAIT.ordinal());
        changeSubmitService.insert(submit);

        UmsNodeChangeDetail changeDetail = OrikaMapper.map(detailDto, UmsNodeChangeDetail.class);
        changeDetail.setId(null);
        changeDetail.setSubmitId(submit.getId());
        changeDetail.setChangeType(BusinessType.UPDATE.ordinal());
        changeDetailService.insert(changeDetail);

        List<UmsNodeChangeFileDto> changeFileDtos = detailDto.getFileDtoList();
        if(!CollectionUtils.isEmpty(changeFileDtos)){
            List<UmsNodeChangeFile> fileList = OrikaMapper.mapList(changeFileDtos,UmsNodeChangeFileDto.class, UmsNodeChangeFile.class);
            fileList.stream().forEach(e->{
                e.setNodeId(changeDetail.getId());
            });
            changeFileService.saveBatchs(fileList);
        }
        return submit.getId();
    }

    @Override
    public void saveJobSource(UmsWorkChangeDto workDto) {
        UmsNodeChangeSubmit submit = new UmsNodeChangeSubmit();
        Long submitId = workDto.getSubmitId();
        if(Objects.nonNull(submitId)){
            UmsNodeChangeSubmit exitSubmit = changeSubmitService.selectById(submitId);
            UmsWorkChange workChange =  workChangeService.selectBySubmit(exitSubmit.getId());
            workChangeService.deleteById(workChange.getId());
        }
        submit.setCreateTime(new Date());
        submit.setChangeType(BusinessType.INSERT.ordinal());
        submit.setStatus(SubmitStatus.WAIT.ordinal());
        submit.setProcessStatus(ProcessStatus.WAIT.ordinal());
        changeSubmitService.saveOrUpdate(submit);

        UmsWorkChange changeDetail = OrikaMapper.map(workDto, UmsWorkChange.class);
        changeDetail.setSubmitId(submit.getId());
        submit.setStatus(SubmitStatus.WAIT.ordinal());
        workChangeService.insert(changeDetail);
    }

    @Override
    public void updateJobSource(UmsWorkChangeDto workDto) {
        if(Objects.isNull(workDto.getWorkId())){
            throw new RuntimeException("岗位不能为空");
        }

        Long submitId = workDto.getSubmitId();
        if(Objects.nonNull(submitId)){
            UmsNodeChangeSubmit exitSubmit = changeSubmitService.selectById(submitId);
            UmsWorkChange workChange =  workChangeService.selectBySubmit(exitSubmit.getId());
            workChangeService.deleteById(workChange.getId());
        }
        UmsNodeChangeSubmit submit = new UmsNodeChangeSubmit();
        submit.setCreateTime(new Date());
        submit.setChangeType(BusinessType.UPDATE.ordinal());
        submit.setStatus(SubmitStatus.WAIT.ordinal());
        changeSubmitService.saveOrUpdate(submit);

        UmsWorkChange changeDetail = OrikaMapper.map(workDto, UmsWorkChange.class);
        changeDetail.setSubmitId(submit.getId());
        submit.setStatus(SubmitStatus.WAIT.ordinal());
        workChangeService.insert(changeDetail);
    }

    @Override
    public void deleteJobSource(UmsWorkDto workDto) {
        workChangeService.deleteById(workDto.getId());
    }

    @Override
    public UmsColumnDto querySource(Long menuId, Long columnId) {
        List<UmsColumnDto> columnList = queryColumnOnly(menuId);
        if(CollectionUtils.isEmpty(columnList)){
            return null;
        }
        UmsColumnDto umsColumn = columnList.stream().filter(e->e.getId().equals(columnId)).findFirst().orElse(null);
        if(Objects.isNull(umsColumn)){
            return null;
        }
        if(umsColumn.getCategoryType().equals(CategoryType.LEAF.ordinal())){
            Map<Long,List<UmsColumnDto>> map =columnList.stream().collect(Collectors.groupingBy(UmsColumnDto::getParentId));
            umsColumn.setColumnDtoList(map.get(umsColumn.getId()));
            return umsColumn;
        }

        List<UmsNode> nodeList = nodeService.selectListByColumn(umsColumn.getId());
        if(!CollectionUtils.isEmpty(nodeList)){
            List<UmsNodeDto> nodeDtoList = OrikaMapper.mapList(nodeList,UmsNode.class,UmsNodeDto.class);
            for (UmsNodeDto umsNodeDto : nodeDtoList) {
                List<UmsNodeFile> fileList = nodeFileService.selectListByNodeId(umsNodeDto.getId());
                if(!CollectionUtils.isEmpty(fileList)){
                    List<UmsNodeFileDto> fileDtoList = OrikaMapper.mapList(fileList,UmsNodeFile.class,UmsNodeFileDto.class);
                    umsNodeDto.setFileDtoList(fileDtoList);
                }
            }
            umsColumn.setNodeDtoList(nodeDtoList);
        }
        return umsColumn;
    }

    @Override
    public List<UmsColumnDto> queryColumn(Long menuId) {
        List<UmsColumnDto> columnList = queryColumnTree(menuId);
        return columnList;
    }

    @Override
    public List<UmsNodeDto> newSourceList(Long menuId,Long columnId) {
        List<UmsNodeDto> nodeDtoList =new ArrayList<>();
        List<UmsColumnDto> columnDtoList = queryColumnOnly(menuId);
        if(!CollectionUtils.isEmpty(columnDtoList)){
            UmsColumnDto columnDto = columnDtoList.stream().filter(e -> e.getId() .equals(columnId) ).findAny().orElse(null);
            if(Objects.isNull(columnDto)){
              List<UmsNode> nodeList = nodeService.selectListByColumn(columnDto.getId());
              if(!CollectionUtils.isEmpty(nodeList)){
                  nodeDtoList = OrikaMapper.mapList(nodeList,UmsNode.class,UmsNodeDto.class);
                  nodeDtoList = nodeDtoList.stream().sorted(Comparator.comparing(UmsNodeDto::getName).reversed()).collect(Collectors.toList());
                    for (UmsNodeDto umsNodeDto : nodeDtoList) {
                        List<UmsNodeFile> fileList = nodeFileService.selectListByNodeId(umsNodeDto.getId());
                        if(!CollectionUtils.isEmpty(fileList)){
                            List<UmsNodeFileDto> fileDtoList = OrikaMapper.mapList(fileList,UmsNodeFile.class,UmsNodeFileDto.class);
                            umsNodeDto.setFileDtoList(fileDtoList);
                        }
                    }
              }

            }
        }
        return nodeDtoList;
    }

    @Override
    public void deleteNewsSource(UmsNodeDto job) {
        List<Long> nodes = new ArrayList<>();
        nodes.add(job.getId());
        nodeService.deleteBatchIds(job.getColumnId(),nodes);
        nodeFileService.deleteByNodeIds(nodes);
    }

    @Override
    public List<UmsColumnChangeDto> reviewSourceList() {
        //查询所有审核



        List<UmsMenuNode> treeList = menuService.treeList();
        for (UmsMenuNode umsMenuNode : treeList) {
            List<UmsColumnDto> columnDtoList = queryColumnOnly(umsMenuNode.getId());
           /* for (){

            }*/




        }





        return null;
    }

    @Override
    public UmsColumnChangeDto reviewSourceDetail(Long id) {
        UmsColumnChangeDto result = new UmsColumnChangeDto();
        UmsNodeChangeSubmit  submit = changeSubmitService.selectById(id);
        if(Objects.isNull(submit)){
            throw new RuntimeException("提交记录不存在");
        }
        List<UmsNodeChangeDetail> detailList = changeDetailService.selectListBySumitId(id);
        List<Long> nodeIds = detailList.stream().map(UmsNodeChangeDetail::getId).collect(Collectors.toList());
        List<UmsNodeChangeFile>  changeFileList = changeFileService.selectListByNodeIds(nodeIds);
        Map<Long,List<UmsNodeChangeFile>> map= new HashMap<>();
        if(!CollectionUtils.isEmpty(changeFileList)){
            map = changeFileList.stream().collect(Collectors.groupingBy(UmsNodeChangeFile::getNodeId));
        }
        for (UmsNodeChangeDetail detail : detailList) {
            List<UmsNodeChangeFile> fileList = map.get(detail.getId());
            UmsNodeChangeDetailDto detailDto = OrikaMapper.map(detail,UmsNodeChangeDetailDto.class);
            if(!CollectionUtils.isEmpty(fileList)){
                List<UmsNodeChangeFileDto> fileDtoList = OrikaMapper.mapList(fileList,UmsNodeChangeFile.class,UmsNodeChangeFileDto.class);
                detailDto.setFileDtoList(fileDtoList);
            }
        }
        result.setNodeDtoList(OrikaMapper.mapList(detailList,UmsNodeChangeDetail.class,UmsNodeChangeDetailDto.class));
        return result;
    }

    @Override
    public UmsNodeDto newSourceDetail(Long columnId,Long nodeId) {
        List<UmsNode> list = nodeService.selectListByColumn(columnId);
        if(CollectionUtils.isEmpty(list)){
            return null;
        }
        UmsNode node= list.stream().filter(e->e.getId().equals(nodeId)).findFirst().orElse(null);
        UmsNodeDto nodeDto =OrikaMapper.map(node,UmsNodeDto.class);
        if(Objects.isNull(node)){
            return null;
        }

        List<UmsNodeFile> fileList = nodeFileService.selectListByNodeId(nodeId);
        if(!CollectionUtils.isEmpty(fileList)){
            List<UmsNodeFileDto> nodeFileDtos = OrikaMapper.mapList(fileList,UmsNodeFile.class,UmsNodeFileDto.class);
            nodeDto.setFileDtoList(nodeFileDtos);
        }
        return nodeDto;
    }

    @Override
    public List<UmsWorkDto> jobList() {
        List<UmsWorkDto> workDtos = workService.listAll();
        if(CollectionUtils.isEmpty(workDtos)){
            return workDtos;
        }
        Map<Long,UmsDepartmentDto> departmentDtoMap = new HashMap<>();
        Map<Long,UmsContractDto> contractDtoMap = new HashMap<>();
        Map<String,BaseDictDto> reMap = new HashMap<>();
        Map<String,BaseDictDto> elMap = new HashMap<>();
        List<UmsDepartmentDto> departmentList = departmentService.selectList();
        List<UmsContractDto> contractList = contractService.selectList();
        List<BaseDictDto> reList = dictService.listByDictType(DictType.Recruitment);
        List<BaseDictDto> elList = dictService.listByDictType(DictType.Educational);
        departmentDtoMap = departmentList.stream().collect(Collectors.toMap(UmsDepartmentDto::getId, e->e));
        contractDtoMap = contractList.stream().collect(Collectors.toMap(UmsContractDto::getId, e->e));
        reMap = reList.stream().collect(Collectors.toMap(BaseDictDto::getDictCode, e->e));
        elMap = elList.stream().collect(Collectors.toMap(BaseDictDto::getDictCode, e->e));
        for (UmsWorkDto workDto : workDtos){
            if(Objects.nonNull(departmentDtoMap.get(workDto.getDepartmentId()))){
                workDto.setDepartmentName(departmentDtoMap.get(workDto.getDepartmentId()).getName());
            }
            if(Objects.nonNull(contractDtoMap.get(workDto.getContactId()))){
                workDto.setContactName(contractDtoMap.get(workDto.getContactId()).getName());
            }
            if(Objects.nonNull(reMap.get(workDto.getWorkType().toString()))){
                workDto.setWorkTypeName(reMap.get(workDto.getWorkType().toString()).getDictValue());
            }
            if(Objects.nonNull(elMap.get(workDto.getEducation()))){
                workDto.setEducationName(elMap.get(workDto.getEducation()).getDictValue());
            }
        }
        return workDtos;
    }

    @Override
    public UmsWorkDto jobDetail(Long id) {
        UmsWorkDto workDto = workService.selectById(id);
        Map<Long,UmsDepartmentDto> departmentDtoMap = new HashMap<>();
        Map<Long,UmsContractDto> contractDtoMap = new HashMap<>();
        Map<String,BaseDictDto> reMap = new HashMap<>();
        Map<String,BaseDictDto> elMap = new HashMap<>();
        List<UmsDepartmentDto> departmentList = departmentService.selectList();
        List<UmsContractDto> contractList = contractService.selectList();
        List<BaseDictDto> reList = dictService.listByDictType(DictType.Recruitment);
        List<BaseDictDto> elList = dictService.listByDictType(DictType.Educational);
        departmentDtoMap = departmentList.stream().collect(Collectors.toMap(UmsDepartmentDto::getId, e->e));
        contractDtoMap = contractList.stream().collect(Collectors.toMap(UmsContractDto::getId, e->e));
        reMap = reList.stream().collect(Collectors.toMap(BaseDictDto::getDictCode, e->e));
        elMap = elList.stream().collect(Collectors.toMap(BaseDictDto::getDictCode, e->e));
        if(Objects.nonNull(departmentDtoMap.get(workDto.getDepartmentId()))){
            workDto.setDepartmentName(departmentDtoMap.get(workDto.getDepartmentId()).getName());
        }
        if(Objects.nonNull(contractDtoMap.get(workDto.getContactId()))){
            workDto.setContactName(contractDtoMap.get(workDto.getContactId()).getName());
        }
        if(Objects.nonNull(reMap.get(workDto.getWorkType().toString()))){
            workDto.setWorkTypeName(reMap.get(workDto.getWorkType().toString()).getDictValue());
        }
        if(Objects.nonNull(elMap.get(workDto.getEducation()))){
            workDto.setEducationName(elMap.get(workDto.getEducation()).getDictValue());
        }
        return workDto;
    }


    private List<UmsColumnDto> queryColumnTree(Long menuId) {
        List<UmsColumnDto> allDtos = queryColumnOnly(menuId);
        List<UmsColumnDto> result = allDtos.stream().filter(e->e.getParentId() == 0L).collect(Collectors.toList());
        Map<Long,List<UmsColumnDto> > childMap = allDtos.stream().collect(Collectors.groupingBy(UmsColumnDto::getParentId));
        result.stream().forEach(e->{
            List<UmsColumnDto> childList = allDtos.stream().filter(c->c.getParentId().equals(e.getId())).collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(childList)){
                for (UmsColumnDto sec : childList){
                    List<UmsColumnDto> three = childMap.get(sec.getId());
                    sec.setColumnDtoList(three);
                }
            }
            e.setColumnDtoList(childList);
        });
        return result;
    }

    private List<UmsColumnDto> queryColumnOnly(Long menuId) {
        List<UmsColumnDto> result = (List<UmsColumnDto>) hazelcastInstance.getMap(Constant.CACHE_IMAP_NAME+cache).get(menuId);
        if(!CollectionUtils.isEmpty(result)){
            return result;
        }
        List<UmsColumn> columnList = lambdaQuery().eq(UmsColumn::getMenuId,menuId).orderByAsc(UmsColumn::getSort).list();
        if(CollectionUtils.isEmpty(result)){
            return result;
        }
        result = OrikaMapper.mapList(columnList,UmsColumn.class,UmsColumnDto.class);
        hazelcastInstance.getMap(Constant.CACHE_IMAP_NAME+cache).put(menuId,result);
        return result;
    }

    @Override
    public void deleteSource(UmsNodeDto nodeDto) {
          Long columnId = nodeDto.getColumnId();
          List<Long> nodeIds = new ArrayList<>();
          nodeIds.add(nodeDto.getId());
          nodeService.deleteBatchIds(columnId,nodeIds);
          nodeFileService.deleteByNodeIds(nodeIds);
    }




}
