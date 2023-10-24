package com.hamtaro.sunflowerplate.controller.member;

import com.hamtaro.sunflowerplate.dto.member.MemberLoginDto;
import com.hamtaro.sunflowerplate.dto.member.MemberSaveDto;
import com.hamtaro.sunflowerplate.service.member.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Tag(name = "회원가입", description = "회원가입관련 API")
public class MemberController {
    private final MemberService memberService;
    @Tag(name = "회원가입", description = "회원가입관련 API")
    @Operation(summary = "회원가입", description = "회원가입을 시킨다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입이 완료되었습니다."),
            @ApiResponse(responseCode = "400", description = "중복된 이메일입니다. / 형식이 잘못된 비밀번호입니다. / 사용중인 전화번호입니다.")
    })
    @PostMapping("/signup")
    public ResponseEntity<?> memberSignUp(@RequestBody MemberSaveDto memberSaveDto) {
        return memberService.memberSignUp(memberSaveDto);
    }
    @Tag(name = "회원가입", description = "회원가입관련 API")
    @Operation(summary = "이메일중복확인", description = "이메일 중복체크를 한다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 중복되면 false / 중복되지않으면 true")
    })
    @PostMapping("/emailcheck")
    public ResponseEntity<Boolean> memberIdCheck(@RequestBody Map<String, String> email) {
        boolean emailCheck = memberService.findByEmail(email.get("email"));
        return ResponseEntity.status(200).body(emailCheck);
    }
    @Tag(name = "회원가입", description = "회원가입관련 API")
        @Operation(summary = "닉네임중복확인", description = "닉네임 중복체크를 한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "닉네임이 중복되면 false / 중복되지않으면 true")
    })
    @PostMapping("/nickcheck")
    public ResponseEntity<Boolean> nickNameCheck(@RequestBody Map<String, String> nickName) {
        boolean nicknameCheck = memberService.findByNickName(nickName.get("nickName"));
        return ResponseEntity.status(200).body(nicknameCheck);
    }
    @Tag(name = "로그인", description = "로그인관련 API")
    @Operation(summary = "로그인", description = "로그인처리.")
    @PostMapping("/login")
    public ResponseEntity<Boolean> memberLogin(@RequestBody MemberLoginDto memberLoginDto) {
        return memberService.memberLogin(memberLoginDto);
    }
}
