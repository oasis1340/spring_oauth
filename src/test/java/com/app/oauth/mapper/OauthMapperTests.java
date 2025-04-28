package com.app.oauth.mapper;

import com.app.oauth.domain.OauthMemberVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class OauthMapperTests {

    @Autowired
    private MemberMapper memberMapper;

//    회원 조회 테스트
    @Test
    public void selectTest() {
        log.info("{}", memberMapper.select(1L));
    }
//    회원 전체 조회 테스트
    @Test
    public void selectAllTest() {
        log.info("{}", memberMapper.selectAll());
    }
//    이메일로 아이디 찾기 테스트
    @Test
    public void selectByIdTest() {
        log.info("{}", memberMapper.selectByEmail("test1234@gmail.com"));
    }
//    회원 가입 테스트
    @Test
    public void insertTest() {
        OauthMemberVO oauthMemberVO = new OauthMemberVO();
        oauthMemberVO.setMemberEmail("test1234@gmail.com");
        oauthMemberVO.setMemberPassword("1234");
        oauthMemberVO.setMemberName("홍길동");
        memberMapper.insert(oauthMemberVO);
    }
//    회원 수정 테스트
    @Test
    public void updateTest() {
//        세션
        Long memberId = memberMapper.selectByEmail("test1234@gmail.com");
        memberMapper.select(memberId).ifPresent(member -> {
            OauthMemberVO oauthMemberVO = new OauthMemberVO();
            oauthMemberVO.setId(member.getId());
            oauthMemberVO.setMemberEmail("jjy1234@gmail.com");
            oauthMemberVO.setMemberPassword("123123");
            oauthMemberVO.setMemberName(member.getMemberName());
            oauthMemberVO.setMemberNickName("바보");
            oauthMemberVO.setMemberPicture(member.getMemberPicture());
            oauthMemberVO.setMemberProvider(member.getMemberProvider());
            memberMapper.update(oauthMemberVO);
        });

    }
//    회원 탈퇴 테스트
    @Test
    public void deleteTest() {
//        세션
        Long memberId = memberMapper.selectByEmail("jjy1234@gmail.com");
        memberMapper.delete(memberId);
    }
}
