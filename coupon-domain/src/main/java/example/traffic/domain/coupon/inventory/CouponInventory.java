package example.traffic.domain.coupon.inventory;

import example.traffic.domain.coupon.Coupon;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class CouponInventory {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;
    private int totalCoupons;
    private int issuedCoupons;

    public static CouponInventory of(Coupon coupon, int totalCoupons) {
        CouponInventory couponInventory = new CouponInventory();
        couponInventory.coupon = coupon;
        couponInventory.totalCoupons = totalCoupons;
        return couponInventory;
    }

    public String issueCoupon() {
        if (issuedCoupons >= totalCoupons) throw new IllegalStateException("No more coupons available to issue.");
        if (coupon.isExpired()) throw new IllegalStateException("Coupon is expired.");
        
        issuedCoupons++;
        return coupon.generateCouponNumber();
    }
}
