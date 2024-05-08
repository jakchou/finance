package com.defaulttenant.hotswap.web.interceptor;

import com.defaulttenant.hotswap.context.UserContext;
import com.defaulttenant.hotswap.web.ApiReturn;
import com.defaulttenant.hotswap.service.logics.*;
import com.defaulttenant.hotswap.task.permission.model.DeployLogicAuthMetaData;
import org.apache.commons.lang3.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.core.annotation.Order;
import org.springframework.beans.factory.annotation.Value;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.collections4.MapUtils;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.stream.Stream;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 逻辑鉴权过滤器
 *
 * @author sys
 * @since 2.19
 */
@Order(5)
@WebFilter(filterName = "logicAuthFilter", urlPatterns = {"/api/*","/upload/*"})
public class LogicAuthFilter implements Filter {
    private final Logger log = LoggerFactory.getLogger(LogicAuthFilter.class);

    @Value("${logicAuthFlag:false}")
    private Boolean logicAuthFlag;

    public static final String LOGIC_IDENTIFIER_SEPARATOR = ":";


    private Map<String, List<List<DeployLogicAuthMetaData>>> logicAuthMetaDataMap;



    private ObjectMapper objectMapper = new ObjectMapper();

    public static final String VIEW_COMPONENT = "component";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        loadViewLogicData();
    }

    private void loadViewLogicData(){
        logicAuthMetaDataMap = readFileToCollect("permission/logicAuthMetaData.json", new TypeReference<Map<String, List<List<DeployLogicAuthMetaData>>>>() {});
        log.info("LogicAuthFilter init viewLogicData from permission/logicAuthMetaData.json");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if(!logicAuthFlag) {
            log.debug("逻辑鉴权开关关闭");
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        // 1. 识别到当前的请求路径
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        String requestURI = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();
        String logicIdentifier = requestURI + LOGIC_IDENTIFIER_SEPARATOR + method;
        log.info("当前请求的逻辑标识: {}", logicIdentifier);

        if(isUploadRequest(requestURI, method)) {
            log.info("upload 非POST逻辑{} 无需鉴权", logicIdentifier);
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        // 2. 判断是否为白名单接口（认证、登录、流程相关系统逻辑接口）
        for (String allAuthApi : apiWhiteList()) {
            if (logicIdentifier.startsWith(allAuthApi)) {
                log.warn("白名单逻辑{} 无需鉴权", logicIdentifier);
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }
        }

        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        //如果initConfig阶段失败，就重新加载一次
        if(Objects.isNull(logicAuthMetaDataMap)){
            loadViewLogicData();
        }

        // 3. 查询应用的逻辑和资源绑定关系（优化可放入缓存）
        if (isContainsLogicIdentifier(logicIdentifier)) {
            // 表示这个逻辑创建出来 未与页面资源进行关联
            log.warn("未查询到逻辑页面资源关联关系 逻辑{}鉴权不通过", logicIdentifier);
            handleReturn(httpResponse, "逻辑鉴权不通过");
            return;
        }
        // 3.1 如果当前逻辑关联的页面资源是 空数组则表示该逻辑关联了无需登录即可访问的页面，所以直接放行
        if(isShouldNotAuth(logicIdentifier)){
            log.debug("逻辑{} 无需鉴权", logicIdentifier);
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        // 4. 判断当前用户是否登录，如果3.1没有放行 则说明逻辑没有关联无需鉴权的页面，所以用户需要登录才能访问这个逻辑
        UserContext.UserInfo currentUser = UserContext.getCurrentUser();
        if (Objects.isNull(currentUser)) {
            log.warn("当前用户未登录 逻辑{} 鉴权不通过", logicIdentifier);
            handleReturn(httpResponse, "逻辑鉴权不通过");
            return;
        }

        // 5. 调用系统默认逻辑鉴权策略检查是否有权限访问
        List<String> currentUserResNames = getUserResourceList(currentUser.userId);
        if (CollectionUtils.isEmpty(currentUserResNames)) {
            log.warn("当前用户关联资源为空 逻辑{} 鉴权不通过", logicIdentifier);
            handleReturn(httpResponse, "用户关联资源为空，逻辑鉴权不通过");
            return;
        }
        List<List<DeployLogicAuthMetaData>> viewMappingsInGroupMap =  logicAuthMetaDataMap.get(logicIdentifier);
        for (int i = 0; i < viewMappingsInGroupMap.size(); i++) {
            // 一个组里的要全部满足才算通过，一个entry.getValue()表示一个组里的内容
            // 不同组之间的有一个组满足就可以通过
            // 父子页面会同步绑定角色，子页面有的角色 父页面一定有，父页面有的子页面不一定有
            List<DeployLogicAuthMetaData> viewMappingsInGroup = viewMappingsInGroupMap.get(i);
            // 组内的每一个元素，如果类型是page，用户关联的资源以此值开头就可以访问
            // 如果类型是component，用户关联的资源要完全相等于此值才可以访问
            boolean allMatch = viewMappingsInGroup.stream().allMatch(viewMapping -> isViewMappingMatch(currentUserResNames, viewMapping));
            if (allMatch) {
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }
        }

        handleReturn(httpResponse, "逻辑鉴权不通过");
    }

    private boolean isUploadRequest(String requestURI, String method) {
        return StringUtils.isNotBlank(requestURI) && requestURI.startsWith("/upload") &&!"POST".equals(method);
    }

    private boolean isContainsLogicIdentifier(String logicIdentifier) {
        return MapUtils.isEmpty(logicAuthMetaDataMap) || !logicAuthMetaDataMap.containsKey(logicIdentifier);
    }

    private boolean isShouldNotAuth(String logicIdentifier) {
        return logicAuthMetaDataMap.containsKey(logicIdentifier) &&
                !CollectionUtils.isEmpty(logicAuthMetaDataMap.get(logicIdentifier)) &&
                CollectionUtils.isEmpty(logicAuthMetaDataMap.get(logicIdentifier).get(0));
    }

    private boolean isViewMappingMatch(List<String> currentUserResNames, DeployLogicAuthMetaData viewMapping) {
        if (VIEW_COMPONENT.equals(viewMapping.getType())) {
            return currentUserResNames.stream().anyMatch(currentUserRes -> StringUtils.equals(currentUserRes, viewMapping.getUiPath()));
        } else {
            return currentUserResNames.stream().anyMatch(currentUserRes -> StringUtils.startsWith(viewMapping.getUiPath(), currentUserRes));
        }
    }


    private List<String> getUserResourceList(String userId){
        List<String> list = new ArrayList<>();
        List<Map> resourceMetaData = readFileToCollect("permission/resourceMetaData.json", new TypeReference<List<Map>>() {});
        if(!CollectionUtils.isEmpty(resourceMetaData)){
            resourceMetaData.stream().forEach(result->{
                Object resourceTypeObject = result.get("name");
                if(Objects.nonNull(resourceTypeObject)){
                    list.add(resourceTypeObject.toString());
                }
            });
        }
        return list;

    }

    private static List<String> apiWhiteList() {
        List<String> verifyApis = Arrays.asList("/api/user");
        List<String> systemApis = Arrays.asList("/api/system");
        List<String> processV2Apis = Arrays.asList("/api/processV2");
        return Stream.concat(Stream.concat(verifyApis.stream(), systemApis.stream()), processV2Apis.stream()).collect(Collectors.toList());
    }

    private <T> T readFileToCollect(String filePath, TypeReference<T> typeReference) {
        ClassPathResource classPathResource = new ClassPathResource(filePath);
        InputStream inputStream = null;
        try {
            inputStream = classPathResource.getInputStream();
        } catch (IOException e) {
            log.error("应用启动时权限数据 {} 读取失败 {}", filePath, e);
            return null;
        }
        T readValue = null;
        try {
            readValue = objectMapper.readValue(inputStream, typeReference);
        } catch (IOException e) {
            log.error("应用启动时权限数据 {} 转换失败 {}", filePath, e);
            return null;
        }
        return readValue;
    }

    private void handleReturn(HttpServletResponse response, String returnMessage) throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        ApiReturn<String> unAuthReturn = ApiReturn.of("", HttpStatus.UNAUTHORIZED.value(), returnMessage);
        response.getWriter().write(objectMapper.writeValueAsString(unAuthReturn));
    }
}
