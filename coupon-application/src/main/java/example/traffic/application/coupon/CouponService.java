package example.traffic.application.coupon;

import example.traffic.domain.coupon.Coupon;

public interface CouponService {

    Coupon createCoupon(String name, String code, int totalCoupons);

    void issueCoupon(String couponCode, String username);
}
