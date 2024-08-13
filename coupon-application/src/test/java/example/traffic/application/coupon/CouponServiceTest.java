package example.traffic.application.coupon;

import example.traffic.application.config.ApplicationConfig;
import example.traffic.domain.coupon.Coupon;
import example.traffic.domain.coupon.inventory.CouponInventory;
import example.traffic.domain.coupon.inventory.CouponInventoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ApplicationConfig.class})
class CouponServiceTest {

    @Autowired
    CouponService sut;
    @Autowired
    CouponInventoryRepository couponInventoryRepository;
    ExecutorService executorService = Executors.newFixedThreadPool(4);

    @Test
    void 동시에_여러건_발급받기() {

        // given
        int couponCount = 100;
        Coupon coupon = sut.createCoupon("쿠폰1", "COUPON-1", couponCount);

        // when
        for (int i = 0; i < couponCount; i++) {
            executorService.submit(() -> {
                sut.issueCoupon(coupon.getCode(), "USER");
            });
        }

        CouponInventory couponInventory = couponInventoryRepository.findByCouponCode(coupon.getCode()).get();
        // then
        assertEquals(couponInventory.getIssuedCoupons(), 100);

    }

}