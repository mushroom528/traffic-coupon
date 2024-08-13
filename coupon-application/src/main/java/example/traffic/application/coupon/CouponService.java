package example.traffic.application.coupon;

import example.traffic.domain.coupon.Coupon;
import example.traffic.domain.coupon.CouponRepository;
import example.traffic.domain.coupon.history.CouponHistory;
import example.traffic.domain.coupon.history.CouponHistoryRepository;
import example.traffic.domain.coupon.inventory.CouponInventory;
import example.traffic.domain.coupon.inventory.CouponInventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponInventoryRepository couponInventoryRepository;
    private final CouponHistoryRepository couponHistoryRepository;

    @Transactional
    public Coupon createCoupon(String name, String code, int totalCoupons) {
        Coupon coupon = Coupon.of(name, code, LocalDateTime.now().plusDays(7));
        CouponInventory couponInventory = CouponInventory.of(coupon, totalCoupons);

        couponRepository.save(coupon);
        couponInventoryRepository.save(couponInventory);

        return coupon;
    }

    @Transactional
    public void issueCoupon(String couponCode, String username) {
        CouponInventory couponInventory = couponInventoryRepository.findByCouponCode(couponCode)
                .orElseThrow(() -> new IllegalArgumentException("No inventory found for coupon code: " + couponCode));

        Coupon coupon = couponInventory.getCoupon();
        String couponNumber = couponInventory.issueCoupon();
        saveHistory(username, coupon, couponNumber);
    }

    private void saveHistory(String username, Coupon coupon, String couponNumber) {
        CouponHistory history = CouponHistory.of(coupon.getId(), username, couponNumber);
        couponHistoryRepository.save(history);
    }

}
