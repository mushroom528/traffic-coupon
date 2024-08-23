package example.traffic.application.config;

import example.traffic.application.coupon.CouponService;
import example.traffic.application.coupon.CouponServiceV3;
import example.traffic.domain.coupon.CouponRepository;
import example.traffic.domain.coupon.history.CouponHistoryRepository;
import example.traffic.domain.coupon.inventory.CouponInventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

@TestConfiguration
@RequiredArgsConstructor
@Import(ApplicationConfig.class)
public class CouponConfig {

    private final CouponRepository couponRepository;
    private final CouponHistoryRepository couponHistoryRepository;
    private final CouponInventoryRepository couponInventoryRepository;

    @Bean
    @Primary
    public CouponService couponService() {
        return new CouponServiceV3(couponRepository, couponInventoryRepository, couponHistoryRepository);
    }

}
