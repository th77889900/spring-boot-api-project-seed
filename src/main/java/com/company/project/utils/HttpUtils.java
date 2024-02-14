package com.company.project.utils;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.ObjectOutputStream;

/**
 * Http request tool
 *
 * @author : Cody.Teng
 * @date : 2024-02-13 4:57 p.m.
 */
@Component
public class HttpUtils<T> {

    @Resource
    private RestTemplate restTemplate;

//    public T getRequest() {
//
//        return ;
//
//    }

}
