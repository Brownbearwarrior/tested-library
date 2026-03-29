package com.test.library_loan.common.utils;

import com.test.library_loan.common.model.Response;

public class ResponseUtils {
    private ResponseUtils(){}

    public static <T>Response<T> toResponse(String message, T data){
        return Response.<T>builder()
                .responseMessage(message)
                .data(data)
                .build();
    }
}
