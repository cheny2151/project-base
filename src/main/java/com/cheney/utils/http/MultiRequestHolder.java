package com.cheney.utils.http;

import com.alibaba.fastjson.JSON;
import com.cheney.exception.MultiRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 聚合http请求工具类
 */
@Component("multiRequestHolder")
@Slf4j
public class MultiRequestHolder {

    public final static MultiRequestHandler<Map<String, Object>> defaultRequestHandler = new KeyValueMultiRequestHandler();

    /**
     * 异步线程池
     */
    private ExecutorService executorService = new ThreadPoolExecutor(0, 1000,
            60L, TimeUnit.SECONDS,
            new SynchronousQueue<>());

    /**
     * 同步聚合请求
     *
     * @param requestInfoList     请求集合
     * @param multiRequestHandler 结果处理函数式接口
     * @param <R>                 预计返回类型
     * @return 包装实体
     */
    @SuppressWarnings("unchecked")
    public <R> R multiRequestSync(Collection<RequestInfo> requestInfoList, MultiRequestHandler<R> multiRequestHandler) {

        log.info("聚合请求开始-->");

        final List<ResponseInfo> responseEntities = new ArrayList<>();
        requestInfoList.forEach((info) -> {
            ResponseEntity<?> responseEntity;
            RequestInfo.Method method = info.getMethod();
            String url = info.getUrl();
            Object requestBody = null;
            if (RequestInfo.Method.GET.equals(method)) {
                responseEntity = HttpUtils.getForEntity(url, info.getResultType());
            } else {
                requestBody = info.getRequestBody();
                responseEntity = HttpUtils.postForEntity(url, requestBody, info.getResultType());
            }
            if (responseEntity.getStatusCodeValue() != 200) {
                //请求失败，抛出异常
                throw new MultiRequestException(info, responseEntity);
            }
            ResponseInfo responseInfo = new ResponseInfo(info.getResultType(), responseEntity, info.getLabel());
            responseEntities.add(responseInfo);
            log.info("聚合请求地址->{},请求参数->{},响应数据->{}", url, JSON.toJSONString(requestBody), JSON.toJSONString(responseEntity.getBody()));
        });

        //函数式接口处理响应包装
        return multiRequestHandler.handler(responseEntities);
    }

    /**
     * 异步聚合请求
     *
     * @param requestInfoList     请求集合
     * @param multiRequestHandler 结果处理函数式接口
     * @param <R>                 预计返回类型
     * @return 包装实体
     */
    @SuppressWarnings("unchecked")
    public <R> R multiRequestAsync(Collection<RequestInfo> requestInfoList, MultiRequestHandler<R> multiRequestHandler) {

        log.info("异步聚合请求开始-->");

        final Map<RequestInfo, Future<ResponseEntity<?>>> futures = new HashMap<>();
        requestInfoList.forEach((info) -> {
            RequestInfo.Method method = info.getMethod();
            String url = info.getUrl();
            Future<ResponseEntity<?>> responseEntityFuture;
            try {
                if (RequestInfo.Method.GET.equals(method)) {
                    responseEntityFuture = executorService.submit(
                            () -> HttpUtils.getForEntity(url, info.getResultType()));
                } else {
                    Object requestBody = info.getRequestBody();
                    responseEntityFuture = executorService.submit(
                            () -> HttpUtils.postForEntity(url, requestBody, info.getResultType()));
                }
            } catch (Exception e) {
                throw new MultiRequestException(info, e);
            }

            futures.put(info, responseEntityFuture);
        });

        List<ResponseInfo> responseEntities = getResultFromFuture(futures);

        //函数式接口处理响应包装
        return multiRequestHandler.handler(responseEntities);
    }

    /**
     * 从异步线程任务中获取响应结果
     *
     * @param responseFutures 线程任务集合
     * @return 响应结果
     */
    @SuppressWarnings("unchecked")
    private List<ResponseInfo> getResultFromFuture(Map<RequestInfo, Future<ResponseEntity<?>>> responseFutures) {
        final ArrayList<ResponseInfo> result = new ArrayList<>();
        responseFutures.forEach((requestInfo, future) -> {
            ResponseEntity<?> responseEntity = null;
            try {
                responseEntity = future.get();
                ResponseInfo<?> responseInfo = new ResponseInfo(requestInfo.getResultType(), responseEntity, requestInfo.getLabel());
                if (responseEntity.getStatusCodeValue() != 200) {
                    //请求失败，抛出异常
                    throw new MultiRequestException(requestInfo, responseEntity);
                }
                result.add(responseInfo);
                log.info("异步聚合请求>>>请求地址->{},请求参数->{},响应数据->{}", requestInfo.getUrl(), JSON.toJSONString(requestInfo.getRequestBody()), JSON.toJSONString(responseEntity.getBody()));
            } catch (Exception e) {
                if (responseEntity != null)
                    throw new MultiRequestException(requestInfo, responseEntity, e);
                else
                    throw new MultiRequestException(requestInfo, e);
            }
        });
        return result;
    }

    public static List<RequestInfo> createRequestInfoList(RequestInfo... requestInfos) {
        return new ArrayList<>(Arrays.asList(requestInfos));
    }

    /**
     * 函数式接口处理响应包装
     *
     * @param <R> 包装类型
     */
    @FunctionalInterface
    public interface MultiRequestHandler<R> {

        /**
         * 包装处理函数式接口
         *
         * @param entities 响应信息集合
         * @return 包装类型
         */
        R handler(List<ResponseInfo> entities);

    }

    /**
     * 默认实现处理响应包装
     */
    public static class KeyValueMultiRequestHandler implements MultiRequestHandler<Map<String, Object>> {

        @Override
        public Map<String, Object> handler(List<ResponseInfo> entities) {
            return entities.stream().collect(Collectors.toMap(ResponseInfo<Object>::getLabel,
                    responseInfo -> responseInfo.getResponseEntity().getBody()));
        }
    }

}
