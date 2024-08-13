package example.traffic.domain.coupon;

import example.traffic.domain.coupon.inventory.CouponInventory;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CouponTest {

    @Test
    void 쿠폰_발급() {

        // given
        Coupon coupon = Coupon.of("테스트 쿠폰", "CODE-1", LocalDateTime.now().plusDays(1));
        CouponInventory couponInventory = CouponInventory.of(coupon, 100);

        // when
        String couponNumber = couponInventory.issueCoupon();

        // then
        assertNotNull(couponNumber);

    }

    @Test
    void 발급가능한_쿠폰이_없으면_예외_발생() {

        // given
        Coupon coupon = Coupon.of("테스트 쿠폰", "CODE-1", LocalDateTime.now().plusDays(1));
        CouponInventory couponInventory = CouponInventory.of(coupon, 0);

        // then
        assertThrows(IllegalStateException.class, couponInventory::issueCoupon);
    }

    @Test
    void 쿠폰의_기간이_만료되면_예외_발생() {

        // given
        Coupon coupon = Coupon.of("테스트 쿠폰", "CODE-1", LocalDateTime.now().minusDays(1));
        CouponInventory couponInventory = CouponInventory.of(coupon, 100);

        // then
        assertThrows(IllegalStateException.class, couponInventory::issueCoupon);
    }

}