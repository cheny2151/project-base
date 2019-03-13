package com.cheney.system.protocol;

import com.cheney.system.page.Page;
import com.cheney.system.page.Pageable;
import com.cheney.utils.RequestParamHolder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;

/**
 * 功能描述：
 *
 * @param <T>
 * @作者 luozhh 创建时间： 2018年2月6日 下午8:21:51
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseRequest<T> implements Serializable {

    private static final long serialVersionUID = 8744687666253393218L;

    /**
     * 请求标识
     */
    private String requestId;

    /**
     * 请求时间戳
     */
    private String timestamp;

    /**
     * 请求参数
     */
    private T data;

    /**
     * 分页参数
     */
    private Pageable pageable = new Pageable();

    public static <T> BaseRequest<T> createNew(T data, Pageable page) {
        BaseRequest<T> request = new BaseRequest<>();
        request.setData(data);
        request.setTimestamp(String.valueOf(System.currentTimeMillis()));
        request.setRequestId(UUID.randomUUID().toString());
        request.setPageable(page);
        return request;
    }

    public static <T> BaseRequest<?> createByCurrentReq(T data, Pageable page) {
        final BaseRequest<?> param = RequestParamHolder.currentRequestParam();
        if (param == null) {
            return createNew(data, page);
        }
        Object requestData = data == null ? new HashMap<>() : data;
        return new BaseRequest<>(
                param.requestId, String.valueOf(System.currentTimeMillis())
                , requestData, page
        );
    }

}
