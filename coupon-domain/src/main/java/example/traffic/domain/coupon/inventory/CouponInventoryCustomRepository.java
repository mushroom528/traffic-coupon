package example.traffic.domain.coupon.inventory;

import java.util.Optional;

public interface CouponInventoryCustomRepository {

    Optional<CouponInventory> findByCouponCode(String couponCode);
}
