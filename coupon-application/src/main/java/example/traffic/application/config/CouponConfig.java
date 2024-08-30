package example.traffic.application.config;

import example.traffic.application.coupon.CouponService;
import example.traffic.application.coupon.CouponServiceV2;
import example.traffic.application.coupon.CouponServiceV5;
import example.traffic.domain.coupon.CouponRepository;
import example.traffic.domain.coupon.history.CouponHistoryRepository;
import example.traffic.domain.coupon.inventory.CouponInventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@RequiredArgsConstructor
public class CouponConfig {

    private final CouponRepository couponRepository;
    private final CouponInventoryRepository couponInventoryRepository;
    private final CouponHistoryRepository couponHistoryRepository;

    @Bean
    public CouponService couponProxyService() {
        return new CouponServiceV5(couponService());
    }

    @Bean
    @Primary
    public CouponService couponService() {
        return new CouponServiceV2(
                couponRepository,
                couponInventoryRepository,
                couponHistoryRepository
        );
    }
}
