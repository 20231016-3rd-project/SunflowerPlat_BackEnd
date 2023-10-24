package com.hamtaro.sunflowerplate.service.member;

import com.hamtaro.sunflowerplate.dto.member.MemberLoginDto;
import com.hamtaro.sunflowerplate.dto.member.MemberSaveDto;
import com.hamtaro.sunflowerplate.entity.member.MemberEntity;
import com.hamtaro.sunflowerplate.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public ResponseEntity<?> memberSignUp(MemberSaveDto memberSaveDto) {
        Map<String, String> result = new HashMap<>();
        String passwordPatten = "^[A-Za-z0-9]{8,20}$";
        if (findByEmail(memberSaveDto.getEmail())) {
            if (findByPhoneNumber(memberSaveDto.getPhone())) {
                if (Pattern.matches(passwordPatten, memberSaveDto.getPassword())) {
                    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
                    String encPassword = bCryptPasswordEncoder.encode(memberSaveDto.getPassword());
                    MemberEntity memberEntity = MemberEntity.builder()
                            .memberEmail(memberSaveDto.getEmail())
                            .memberPassword(encPassword)
                            .memberNickName(memberSaveDto.getNickName())
                            .memberProfilePicture("https://plate-user-img.s3.ap-northeast-2.amazonaws.com/BasicImage.png")
                            .memberPhone(memberSaveDto.getPhone())
                            .memberRole("USER")
                            .build();
                    try {
                        memberRepository.save(memberEntity);
                        result.put("message", "회원가입이 완료되었습니다.");
                        return ResponseEntity.status(200).body(result);
                    } catch (Exception e) {
                        result.put("message", "회원가입에 실패하였습니다.");
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
                    }
                } else {
                    result.put("message", "형식이 잘못된 비밀번호 입니다.");
                    return ResponseEntity.badRequest().body(result);
                }
            } else {
                result.put("message", "중복된 전화번호 입니다.");
                return ResponseEntity.badRequest().body(result);
            }
        } else {
            result.put("message", "중복된 아이디 입니다.");
            return ResponseEntity.badRequest().body(result);
        }
    }

    public boolean findByEmail(String email) {
        MemberEntity memberEntity = memberRepository.findByMemberEmail(email);
        if (memberEntity != null) {
            return false; // 중복된 아이디
        } else {
            return true; // 새로운 아이디
        }
    }

    public boolean findByPhoneNumber(String telNumber) {
        MemberEntity memberEntity = memberRepository.findByMemberPhone(telNumber);
        if (memberEntity != null) {
            return false; // 중복된 전화번호
        } else {
            return true; // 새로운 전화번호
        }
    }

    public boolean findByNickName(String nickName) {
        MemberEntity memberEntity = memberRepository.findByMemberNickName(nickName);
        if (memberEntity != null) {
            return false; // 중복된 닉네임
        } else {
            return true; // 새로운 닉네임
        }
    }

    public ResponseEntity<Boolean> memberLogin(MemberLoginDto memberLoginDto) {
        return null;
    }
}
