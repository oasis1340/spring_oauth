package com.app.oauth.controller;

import com.app.oauth.domain.OauthMemberVO;
import com.app.oauth.service.MemberService;
import com.app.oauth.service.SmsService;
import com.app.oauth.util.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/auth/*")
public class AuthController {

    private final SmsService smsService;

    @PostMapping("sendSms")
    public ResponseEntity<Map<String, Object>> sendSms(@RequestBody String memberPhone) {
        return smsService.sendEmailVerification(memberPhone);
    }

    @PostMapping("sendEmail")
    public ResponseEntity<Map<String, Object>> sendEmail(@RequestBody String memberEmail) {
        log.info(memberEmail);
        return smsService.sendEmailVerification(memberEmail);
    }

    @PostMapping("verifyCode")
    public void verifyCode(@RequestBody String code) {
//        log.info("verifyCode {}", smsService.verifyAuthCode(code));
    }
}
