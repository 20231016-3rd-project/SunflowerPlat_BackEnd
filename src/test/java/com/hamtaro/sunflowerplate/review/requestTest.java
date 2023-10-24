package com.hamtaro.sunflowerplate.review;


import com.hamtaro.sunflowerplate.dto.RequestDto;
import com.hamtaro.sunflowerplate.entity.member.MemberEntity;
import com.hamtaro.sunflowerplate.entity.review.RequestEntity;
import com.hamtaro.sunflowerplate.repository.RequestRepository;
import com.hamtaro.sunflowerplate.service.ReviewService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;


@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class requestTest {

    @Autowired
    EntityManager em;
    @Autowired
    ReviewService reviewService;
    @Autowired
    RequestRepository requestRepository;


    private MemberEntity createMember(){
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setMemberId(3L);
        memberEntity.setMemberEmail("smry2022@naver.com");
        memberEntity.setMemberPassword("20022022");
        memberEntity.setMemberPhone("010-2889-2938");
        memberEntity.setMemberRole("20");

        em.persist(memberEntity);
        return memberEntity;
    }
    @Test
    public void request() throws Exception {
        //given
        MemberEntity memberEntity = createMember();
        RequestDto requestDto = new RequestDto();
        RequestEntity requestEntity = new RequestEntity();

        //when

        requestEntity.setRequestContent("으아으어어");

        reviewService.requestRestaurant(requestDto , requestEntity.getRequestId());

        //then
            }
}
