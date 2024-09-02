package example.traffic.application.coupon;

import example.traffic.domain.coupon.Coupon;
import example.traffic.domain.coupon.CouponRepository;
import example.traffic.domain.coupon.history.CouponHistory;
import example.traffic.domain.coupon.history.CouponHistoryRepository;
import example.traffic.domain.coupon.inventory.CouponInventory;
import example.traffic.domain.coupon.inventory.CouponInventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Slf4j
public class CouponServiceV2 implements CouponService {

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

    /**
     * 동시성 이슈 해결방법 -> synchronized 키워드를 사용한다.
     * synchronized를 메서드 부분에 선언 해서 메서드 전체에 락을 설정할 수 있지만,
     * 임계영역에만 synchronized 키워드를 사용해서 부분적으로 락을 설정하는 방법이 있다. -> 이 부분이 더 효율적
     */
    @Transactional
    public void issueCoupon(String couponCode, String username) {
        CouponInventory couponInventory = couponInventoryRepository.findByCouponCode(couponCode)
                .orElseThrow(() -> new IllegalArgumentException("No inventory found for coupon code: " + couponCode));

        Coupon coupon = couponInventory.getCoupon();
        CouponHistory history;
        try {
            String couponNumber = couponInventory.issueCoupon();
            couponInventoryRepository.save(couponInventory);
            history = CouponHistory.successHistory(coupon.getId(), username, couponNumber);
        } catch (Exception e) {
            history = CouponHistory.failHistory(coupon.getId(), username, e.getMessage());
        }
        couponHistoryRepository.save(history);
    }

}
