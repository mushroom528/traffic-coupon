package example.traffic.application.coupon;

import example.traffic.application.config.CouponConfig;
import example.traffic.domain.coupon.Coupon;
import example.traffic.domain.coupon.history.CouponHistory;
import example.traffic.domain.coupon.history.CouponHistoryRepository;
import example.traffic.domain.coupon.history.HistoryType;
import example.traffic.domain.coupon.inventory.CouponInventory;
import example.traffic.domain.coupon.inventory.CouponInventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CouponConfig.class})
class CouponServiceTest {

    @Autowired
    CouponService sut;
    @Autowired
    CouponInventoryRepository couponInventoryRepository;
    @Autowired
    CouponHistoryRepository couponHistoryRepository;

    Coupon coupon;

    int couponCount = 30;
    int requestCount = 50;

    @BeforeEach
    void setUp() {
        coupon = sut.createCoupon("테스트쿠폰1", "COUPON-1", couponCount);
    }

    @Test
    @DisplayName("50명이 신청하는 경우, 30개의 성공이력 20개의 실패이력이 생성된다.")
    @Transactional
    void shouldCreate30SuccessAnd20FailureRecordsWhen50ApplicantsApply() {

        // when
        for (int i = 0; i < requestCount; i++) {
            sut.issueCoupon(coupon.getCode(), "USER");
        }
        CouponInventory couponInventory = couponInventoryRepository.findByCouponCode(coupon.getCode()).get();
        List<CouponHistory> histories = couponHistoryRepository.findAll();

        // then
        assertEquals(50, histories.size());
        assertEquals(30, couponInventory.getIssuedCoupons());
        assertHistoryCount(HistoryType.SUCCESS, 30, histories);
        assertHistoryCount(HistoryType.FAIL, 20, histories);
    }

    @Test
    @DisplayName("동시에 50명이 신청하는 경우, 30개의 성공이력 20개의 실패이력이 생성된다.")
    void shouldCreate30SuccessAnd20FailureRecordsWhen50ApplicantsApplyConcurrently() throws InterruptedException {

        // when
        try (ExecutorService executorService = Executors.newFixedThreadPool(32)) {
            CountDownLatch countDownLatch = new CountDownLatch(requestCount);
            for (int i = 0; i < requestCount; i++) {
                executorService.submit(() -> {
                    try {
                        sut.issueCoupon(coupon.getCode(), "USER");
                    } finally {
                        countDownLatch.countDown();
                    }
                });
            }
            countDownLatch.await();
        }

        List<CouponHistory> histories = couponHistoryRepository.findAll();

        assertEquals(50, histories.size());
        assertHistoryCount(HistoryType.SUCCESS, 30, histories);
        assertHistoryCount(HistoryType.FAIL, 20, histories);
    }

    void assertHistoryCount(HistoryType type, int expected, List<CouponHistory> histories) {
        long count = histories.stream().filter(history -> history.getHistoryType() == type).count();
        assertEquals(expected, count);
    }

}