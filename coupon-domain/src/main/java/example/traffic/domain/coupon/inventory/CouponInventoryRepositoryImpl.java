package example.traffic.domain.coupon.inventory;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static example.traffic.domain.coupon.QCoupon.coupon;
import static example.traffic.domain.coupon.inventory.QCouponInventory.couponInventory;

@Repository
@RequiredArgsConstructor
public class CouponInventoryRepositoryImpl implements CouponInventoryCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<CouponInventory> findByCouponCode(String couponCode) {
        CouponInventory result = queryFactory.selectFrom(couponInventory)
                .innerJoin(couponInventory.coupon, coupon)
                .fetchJoin()
                .where(coupon.code.eq(couponCode))
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
