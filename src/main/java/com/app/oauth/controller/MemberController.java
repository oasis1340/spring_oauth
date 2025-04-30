package com.app.oauth.controller;

import com.app.oauth.domain.OauthMemberVO;
import com.app.oauth.service.MemberService;
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
@RequestMapping("/member/*")
public class MemberController {

    private final MemberService memberService;
    private final JwtTokenUtil jwtTokenUtil;

    //    회원 가입
    @PostMapping("register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody OauthMemberVO oauthMemberVO) {
        Map<String, Object> response = new HashMap<>();

//        회원가입 시키고, 다시 로그인을 하게 만든다
//        early return
        Long memberId = memberService.getMemberIdByMemberEmail(oauthMemberVO.getMemberEmail());
        if(memberId != null) {
            OauthMemberVO foundUser = memberService.getMemberById(memberId).orElse(null);

            if(foundUser != null && foundUser.getMemberEmail().equals(oauthMemberVO.getMemberEmail())) {
                response.put("message", "이미 사용중인 아이디입니다.");
                response.put("provider", foundUser.getMemberProvider());
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
        }

//        회원 가입
        memberService.register(oauthMemberVO);
        response.put("message", "회원가입이 완료되었습니다. 반갑습니다😀");
        return ResponseEntity.ok(response);
    }

    //    로그인
    @PostMapping("login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody OauthMemberVO oauthMemberVO) {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> claims = new HashMap<>();

        Long memberId = memberService.getMemberIdByMemberEmail(oauthMemberVO.getMemberEmail());

//        방어코드1 : 이메일 검사
        if(memberId == null) {
            response.put("message", "등록되지 않은 이메일입니다.😥");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

//        유저를 찾는다
        OauthMemberVO foundUser = memberService.getMemberById(memberId).orElse(null);

//        방어코드2 : 비밀번호 검사
        if(!foundUser.getMemberPassword().equals(oauthMemberVO.getMemberPassword())) {
            response.put("message", "비밀번호가 틀렸습니다. 😅");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

//        아이디와 비밀번호가 일치하는 유저
//        토큰 발급 후 응답
        claims.put("email", foundUser.getMemberEmail());
        claims.put("name", foundUser.getMemberName());
        String jwtToken = jwtTokenUtil.generateToken(claims);
        response.put("jwtToken", jwtToken);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    //    토큰 정보로 유저 정보를 가져 제공하는 API
//    **토큰은 헤더로 주고 받는다
    @PostMapping("profile")
    public ResponseEntity<Map<String, Object>> profile(
            @RequestHeader(value = "Authorization", required = false) String jwtToken
    ){
        Map<String, Object> response = new HashMap<>();
        String token = jwtToken != null ? jwtToken.replace("Bearer ", "") : null;
//        log.info("token: {}", token);
        if (jwtToken != null && jwtTokenUtil.isTokenValid(token)) {
//            유저 정보로 바꾸기
//            ALT + SHIFT + T
            Claims claims = jwtTokenUtil.parseToken(token);
            String memberEmail = claims.get("email").toString();

            Long memberId = memberService.getMemberIdByMemberEmail(memberEmail);
            OauthMemberVO foundUser = memberService.getMemberById(memberId).orElseThrow(() -> {
                throw  new RuntimeException("member profile, Not found User");
            });
            response.put("currentUser", foundUser);
            return ResponseEntity.ok(response);
        }
        response.put("message", "토큰이 만료되었습니다.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

//    로그인 이후 이용해야하는 페이지
}
