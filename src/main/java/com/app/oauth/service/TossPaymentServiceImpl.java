package com.app.oauth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class TossPaymentServiceImpl implements TossPaymentService {

    @Value("${toss.payments.api.key}")
    private String apiKey;

    @Value("${toss.payments.api.url}")
    private String apiUrl;

    @Override
    public String processPayment(Map<String, Object> paymentData) {

        RestTemplate restTemplate = new RestTemplate();

//        API키를 Base64로 인코딩
        String encodedApiKey = Base64.getEncoder().encodeToString((apiKey + ":").getBytes());

        HttpHeaders headers = new HttpHeaders();

        headers.set("Authorization", "Basic " + encodedApiKey);
        headers.set("Content-Type", "application/json; charset=utf-8");
        headers.set("Accept", "application/json; charset=utf-8");

//        HTTP 바디에 결제 데이터 추가
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(paymentData, headers);

        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);

        log.info("{}", response);

        return response.toString();
    }
}
