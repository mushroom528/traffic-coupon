package example.traffic.application.coupon;

import example.traffic.domain.coupon.Coupon;
import example.traffic.domain.coupon.CouponRepository;
import example.traffic.domain.coupon.history.CouponHistory;
import example.traffic.domain.coupon.history.CouponHistoryRepository;
import example.traffic.domain.coupon.inventory.CouponInventory;
import example.traffic.domain.coupon.inventory.CouponInventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RequiredArgsConstructor
@Service
@Slf4j
@Primary
public class CouponServiceV3 implements CouponService {

    private final CouponRepository couponRepository;
    private final CouponInventoryRepository couponInventoryRepository;
    private final CouponHistoryRepository couponHistoryRepository;

    private final Lock lock = new ReentrantLock();

    @Transactional
    public Coupon createCoupon(String name, String code, int totalCoupons) {
        Coupon coupon = Coupon.of(name, code, LocalDateTime.now().plusDays(7));
        CouponInventory couponInventory = CouponInventory.of(coupon, totalCoupons);

        couponRepository.save(coupon);
        couponInventoryRepository.save(couponInventory);

        return coupon;
    }

    /**
     * 동시성 이슈 해결방법 -> ReentrantLock 을 사용한다.
     * ReentrantLock은 synchroinzed의 문제점인 공정성, 무한 대기를 해결해준다.
     * lock을 획득하고 나서 작업을 종료되면 반드시 unlock()을 호출해줘야한다.
     */
    @Transactional
    public synchronized void issueCoupon(String couponCode, String username) {
        CouponInventory couponInventory = couponInventoryRepository.findByCouponCode(couponCode)
                .orElseThrow(() -> new IllegalArgumentException("No inventory found for coupon code: " + couponCode));

        Coupon coupon = couponInventory.getCoupon();
        CouponHistory history;
        lock.lock();
        try {
            String couponNumber = couponInventory.issueCoupon();
            couponInventoryRepository.save(couponInventory);
            history = CouponHistory.successHistory(coupon.getId(), username, couponNumber);
        } catch (Exception e) {
            history = CouponHistory.failHistory(coupon.getId(), username, e.getMessage());
        } finally {
            lock.unlock();
        }
        couponHistoryRepository.save(history);
    }

}
