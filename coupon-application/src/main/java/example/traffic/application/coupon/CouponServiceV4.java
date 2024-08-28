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
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
@Slf4j
public class CouponServiceV4 implements CouponService {

    private final CouponRepository couponRepository;
    private final CouponInventoryRepository couponInventoryRepository;
    private final CouponHistoryRepository couponHistoryRepository;

    private final AtomicBoolean lock = new AtomicBoolean(false);

    @Transactional
    public Coupon createCoupon(String name, String code, int totalCoupons) {
        Coupon coupon = Coupon.of(name, code, LocalDateTime.now().plusDays(7));
        CouponInventory couponInventory = CouponInventory.of(coupon, totalCoupons);

        couponRepository.save(coupon);
        couponInventoryRepository.save(couponInventory);

        return coupon;
    }

    /**
     * 동시성 이슈 해결방법 -> spin lock을 사용한다.(compare-and-set)
     * busy wait, lock을 획득할 때 까지 cpu 자원을 계속 사용한다. -> 아주 짧은 연산을 수행할 때만 사용해야한다.
     * 낙관적인 방법 -> 충돌이 나는 경우에만 해결, 따라서 충돌이 나지 않는 상황에서 유리함
     */
    @Transactional
    public void issueCoupon(String couponCode, String username) {
        while (!lock.compareAndSet(false, true)) {
            // 원자적인 연산(false일 때만 true로 변경한다.)를 통해 여러 스레드가 동시에 실행해도 안전함
        }
        CouponInventory couponInventory = couponInventoryRepository.findByCouponCode(couponCode)
                .orElseThrow(() -> new IllegalArgumentException("No inventory found for coupon code: " + couponCode));

        Coupon coupon = couponInventory.getCoupon();
        try {
            String couponNumber = couponInventory.issueCoupon();
            couponInventoryRepository.save(couponInventory);
            CouponHistory history = CouponHistory.successHistory(coupon.getId(), username, couponNumber);
            couponHistoryRepository.save(history);
        } catch (Exception e) {
            CouponHistory history = CouponHistory.failHistory(coupon.getId(), username, e.getMessage());
            couponHistoryRepository.save(history);
        } finally {
            lock.set(false);
        }
    }

}
