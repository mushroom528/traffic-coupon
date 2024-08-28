package example.traffic.application.coupon;

import example.traffic.application.config.CouponConfig;
import example.traffic.domain.coupon.Coupon;
import example.traffic.domain.coupon.CouponRepository;
import example.traffic.domain.coupon.history.CouponHistory;
import example.traffic.domain.coupon.history.CouponHistoryRepository;
import example.traffic.domain.coupon.history.HistoryType;
import example.traffic.domain.coupon.inventory.CouponInventory;
import example.traffic.domain.coupon.inventory.CouponInventoryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CouponConfig.class})
class CouponServiceTestWithMessage {

    CouponService couponService;
    CouponInventoryRepository couponInventoryRepository;
    CouponHistoryRepository couponHistoryRepository;
    CouponRepository couponRepository;
    CouponService sut;

    @Autowired
    public CouponServiceTestWithMessage(CouponService couponService, CouponInventoryRepository couponInventoryRepository, CouponHistoryRepository couponHistoryRepository, CouponRepository couponRepository) {
        this.couponService = couponService;
        this.couponInventoryRepository = couponInventoryRepository;
        this.couponHistoryRepository = couponHistoryRepository;
        this.couponRepository = couponRepository;
        this.sut = new CouponServiceV5(couponService);
    }

    Coupon coupon;

    int couponCount = 30;
    int requestCount = 50;

    @BeforeEach
    void setUp() {
        coupon = sut.createCoupon("테스트쿠폰1", "COUPON-1", couponCount);
    }

    @AfterEach
    void tearDown() {
        couponInventoryRepository.deleteAll();
        couponHistoryRepository.deleteAll();
        couponRepository.deleteAll();
    }

    @Test
    @DisplayName("동시에 50명이 신청하는 경우, 30개의 성공이력 20개의 실패이력이 생성된다.")
    void shouldCreate30SuccessAnd20FailureRecordsWhen50ApplicantsApplyConcurrently() throws InterruptedException {

        // when
        try (ExecutorService executorService = Executors.newFixedThreadPool(32)) {
            for (int i = 0; i < requestCount; i++) {
                executorService.submit(() -> {
                    sut.issueCoupon(coupon.getCode(), "USER");
                });
            }
        }

        TimeUnit.MILLISECONDS.sleep(500);   // 메세지를 통해 별도의 스레드에서 작업을 처리하기 때문에 일정 시간 대기

        CouponInventory couponInventory = couponInventoryRepository.findByCouponCode(coupon.getCode()).get();
        List<CouponHistory> histories = couponHistoryRepository.findAll();

        assertEquals(50, histories.size());
        assertEquals(30, couponInventory.getIssuedCoupons());
        assertHistoryCount(HistoryType.SUCCESS, 30, histories);
        assertHistoryCount(HistoryType.FAIL, 20, histories);
    }

    void assertHistoryCount(HistoryType type, int expected, List<CouponHistory> histories) {
        long count = histories.stream().filter(history -> history.getHistoryType() == type).count();
        assertEquals(expected, count);
    }

}