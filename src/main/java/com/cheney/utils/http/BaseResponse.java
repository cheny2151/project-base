package com.cheney.utils.http;

import lombok.Data;

@Data
public class BaseResponse<T> {

    private String message;

    private int status;

    private T data;

}
