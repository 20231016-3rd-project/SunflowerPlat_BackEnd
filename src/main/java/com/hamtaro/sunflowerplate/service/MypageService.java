package com.hamtaro.sunflowerplate.service;

import com.hamtaro.sunflowerplate.entity.member.MemberEntity;
import com.hamtaro.sunflowerplate.entity.review.LikeCountEntity;
import com.hamtaro.sunflowerplate.repository.LikeCountRepository;
import com.hamtaro.sunflowerplate.repository.RestaurantRepository;
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
        boolean checkLiked = likeCountEntityOptional.isPresent();

        // 장소 저장이 되었는지 체크 후 장소 저장 카운트 리턴
        int likeCount;
        boolean likeButton;
        if (checkLiked) {
            likeCountRepository.delete(likeCountEntityOptional.get());
            likeButton = false;
        } else {
            LikeCountEntity likeCountEntity = LikeCountEntity
                    .builder()
                    .memberEntity(memberRepository.findById(memberId).get())
                    .restaurantEntity(restaurantRepository.findByRestaurantId(restaurantId).get())
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
