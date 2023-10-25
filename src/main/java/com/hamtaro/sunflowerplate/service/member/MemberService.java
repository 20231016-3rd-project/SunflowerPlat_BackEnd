package com.hamtaro.sunflowerplate.service.member;

import com.hamtaro.sunflowerplate.dto.member.MemberLoginDto;
import com.hamtaro.sunflowerplate.dto.member.MemberSaveDto;
import com.hamtaro.sunflowerplate.entity.member.MemberEntity;
import com.hamtaro.sunflowerplate.jwt.config.TokenProvider;
import com.hamtaro.sunflowerplate.jwt.dto.LoginTokenSaveDto;
import com.hamtaro.sunflowerplate.jwt.dto.TokenDto;
import com.hamtaro.sunflowerplate.jwt.dto.TokenRequestDto;
import com.hamtaro.sunflowerplate.jwt.entity.RefreshTokenEntity;
import com.hamtaro.sunflowerplate.jwt.exception.CRefreshTokenException;
import com.hamtaro.sunflowerplate.jwt.exception.CUserNotFoundException;
import com.hamtaro.sunflowerplate.jwt.repository.RefreshTokenRepository;
import com.hamtaro.sunflowerplate.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

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
                            .memberNickname(memberSaveDto.getNickName())
                            .memberProfilePicture("https://plate-user-img.s3.ap-northeast-2.amazonaws.com/BasicImage.png")
                            .memberPhone(memberSaveDto.getPhone())
                            .memberJoinDate(LocalDate.now())
                            .memberRole("ROLE_USER")
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
        Optional<MemberEntity> memberEntity = memberRepository.findByMemberEmail(email);
        if (memberEntity.isPresent()) {
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
        MemberEntity memberEntity = memberRepository.findByMemberNickname(nickName);
        if (memberEntity != null) {
            return false; // 중복된 닉네임
        } else {
            return true; // 새로운 닉네임
        }
    }

    public ResponseEntity<?> memberLogin(MemberLoginDto memberLoginDto) {
        Map<String, Object> result = new HashMap<>();
        if (!findByEmail(memberLoginDto.getEmail())) {
            if (findByPasswordCheck(memberLoginDto.getEmail(), memberLoginDto.getPassword())) {
                MemberEntity memberEntity = memberRepository.findByMemberEmail(memberLoginDto.getEmail()).get();
                LoginTokenSaveDto loginTokenSaveDto = LoginTokenSaveDto.builder()
                        .id(memberEntity.getMemberId())
                        .email(memberEntity.getMemberEmail())
                        .memberNickName(memberEntity.getMemberNickname())
                        .memberRole(memberEntity.getMemberRole())
                        .build();
                TokenDto tokenDto = tokenProvider.createToken(loginTokenSaveDto.getId(), loginTokenSaveDto);
                Optional<RefreshTokenEntity> optionalRefreshToken = refreshTokenRepository.findByMemberEntityMemberId(memberEntity.getMemberId());
                if (optionalRefreshToken.isEmpty()) {
                    RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.builder()
                            .refreshToken(tokenDto.getRefreshToken())
                            .memberEntity(memberEntity)
                            .build();
                    refreshTokenRepository.save(refreshTokenEntity);
                } else {
                    RefreshTokenEntity refreshTokenEntity = optionalRefreshToken.get();
                    refreshTokenEntity.updateToken(tokenDto.getRefreshToken());
                    refreshTokenRepository.save(refreshTokenEntity);
                }
                TokenRequestDto tokenRequestDto = TokenRequestDto.builder()
                        .accessToken(tokenDto.getAccessToken())
                        .accessTokenExpireDate(tokenDto.getAccessTokenExpireDate())
                        .issuedAt(tokenDto.getIssuedAt())
                        .build();

                ResponseCookie cookie = ResponseCookie.from("refreshToken", tokenDto.getRefreshToken())
                        .maxAge(7 * 24 * 60 * 60)
                        .path("/")
                        .secure(true)
                        .sameSite("None")
                        .httpOnly(true)
                        .build();
                return ResponseEntity.status(200).header(HttpHeaders.SET_COOKIE, cookie.toString()).body(tokenRequestDto);
            } else {
                result.put("access", false);
                result.put("message", "아이디 또는 비밀번호가 틀립니다.");
                return ResponseEntity.status(401).body(result);
            }
        } else {
            result.put("access", false);
            result.put("message", "아이디 또는 비밀번호가 틀립니다.");
            return ResponseEntity.status(401).body(result);
        }
    }

    public boolean findByPasswordCheck(String email, String password) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String entityPassword = memberRepository.findByMemberEmail(email).get().getMemberPassword();
        return bCryptPasswordEncoder.matches(password, entityPassword);
    }

    @Transactional
    public ResponseEntity<?> reissue(String refreshToken) {

        if (!tokenProvider.validationToken(refreshToken)) {
            throw new CRefreshTokenException("리플레쉬 토큰이 유효하지 않음");
        }

        MemberEntity memberEntity = memberRepository.findById(refreshTokenRepository.findByRefreshToken(refreshToken).getMemberEntity().getMemberId())
                .orElseThrow(CUserNotFoundException::new);

        RefreshTokenEntity refreshTokenEntity = refreshTokenRepository.findByMemberEntityMemberId(memberEntity.getMemberId())
                .orElseThrow(CRefreshTokenException::new);

        if (!refreshTokenEntity.getRefreshToken().equals(refreshToken)) {
            throw new CRefreshTokenException();
        }

        LoginTokenSaveDto tokenSaveDto = LoginTokenSaveDto.builder()
                .id(memberEntity.getMemberId())
                .email(memberEntity.getMemberEmail())
                .memberNickName(memberEntity.getMemberNickname())
                .memberRole(memberEntity.getMemberRole())
                .build();

        TokenDto newCreatedToken = tokenProvider.createToken(memberEntity.getMemberId(), tokenSaveDto);
        RefreshTokenEntity updateRefreshToken = refreshTokenEntity.updateToken(newCreatedToken.getRefreshToken());
        refreshTokenRepository.save(updateRefreshToken);

        TokenRequestDto tokenRequestDto = TokenRequestDto.builder()
                .accessToken(newCreatedToken.getAccessToken())
                .accessTokenExpireDate(newCreatedToken.getAccessTokenExpireDate())
                .issuedAt(newCreatedToken.getIssuedAt())
                .build();

        Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
        if (currentAuthentication != null) {
            Authentication expiredAuthentication = new UsernamePasswordAuthenticationToken(null, null, null);
            SecurityContextHolder.getContext().setAuthentication(expiredAuthentication);
        }

        ResponseCookie cookie = ResponseCookie.from("refreshToken", newCreatedToken.getRefreshToken())
                .maxAge(7 * 24 * 60 * 60)
                .path("/")
                .secure(true)
                .sameSite("None")
                .httpOnly(true)
                .build();
        return ResponseEntity.status(200).header(HttpHeaders.SET_COOKIE, cookie.toString()).body(tokenRequestDto);

    }

    public ResponseEntity<?> logout(String userId) {
        Optional<RefreshTokenEntity> tokenOptional = refreshTokenRepository.findByMemberEntityMemberId(Long.valueOf(userId));
        if (tokenOptional.isPresent()){
            refreshTokenRepository.deleteById(tokenOptional.get().getId());
        }
        return ResponseEntity.status(200).build();
    }
}
