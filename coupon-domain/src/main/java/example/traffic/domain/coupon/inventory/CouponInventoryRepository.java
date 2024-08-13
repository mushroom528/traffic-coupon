package example.traffic.domain.coupon.inventory;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponInventoryRepository extends JpaRepository<CouponInventory, Long>, CouponInventoryCustomRepository {

}