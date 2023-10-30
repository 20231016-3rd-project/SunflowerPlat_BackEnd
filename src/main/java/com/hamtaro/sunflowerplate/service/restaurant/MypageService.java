package com.hamtaro.sunflowerplate.service.restaurant;

import com.hamtaro.sunflowerplate.entity.review.LikeCountEntity;
import com.hamtaro.sunflowerplate.repository.restaurant.LikeCountRepository;
import com.hamtaro.sunflowerplate.repository.restaurant.RestaurantRepository;
import com.hamtaro.sunflowerplate.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MypageService {
    private final LikeCountRepository likeCountRepository;
    private final MemberRepository memberRepository;
    private final RestaurantRepository restaurantRepository;

    public ResponseEntity<?> clickLikeButton(Long restaurantId, Long memberId) {
        Map<String, Object> likeCountMap = new HashMap<>();
        // 좋아요가 있는지 체크
        Optional<LikeCountEntity> likeCountEntityOptional = likeCountRepository.findByMemberEntityAndRestaurantEntity(memberId,restaurantId);

        // 장소 저장이 되었는지 체크 후 장소 저장 카운트 리턴
        int likeCount;
        boolean likeButton;
        if (likeCountEntityOptional.isPresent()) { // 찜이 이미 존재하는 경우
            boolean likeStatus = likeCountEntityOptional.get().isLikeStatus();
            likeCountEntityOptional.get().setLikeStatus(!likeStatus);
            likeCountRepository.save(likeCountEntityOptional.get());
            likeButton = !likeStatus;
        } else { // 값이 없다면 생성하기
            LikeCountEntity likeCountEntity = LikeCountEntity
                    .builder()
                    .memberEntity(memberRepository.findById(memberId).get()) // 로그인한 아이디 값 가져오도록 수정 필요
                    .restaurantEntity(restaurantRepository.findByRestaurantId(restaurantId).get())
                    .likeStatus(true)
                    .build();
            likeCountRepository.save(likeCountEntity);
            likeButton = true;
        }
        likeCount = likeCountRepository.countByRestaurantEntity_RestaurantId(restaurantId);

        likeCountMap.put("likeButtonClicked",likeButton);
        likeCountMap.put("likeCount", likeCount);

        return ResponseEntity.status(200).body(likeCountMap);
    }

}
