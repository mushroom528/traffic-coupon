package example.traffic.domain.coupon.history;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
public class CouponHistory {

    @Id
    @GeneratedValue
    private Long id;
    private Long couponId;
    private String username;
    private String couponNumber;

    @CreatedDate
    private LocalDateTime createdAt;

    public static CouponHistory of(Long couponId, String username, String couponNumber) {
        CouponHistory couponHistory = new CouponHistory();
        couponHistory.couponId = couponId;
        couponHistory.username = username;
        couponHistory.couponNumber = couponNumber;

        return couponHistory;
    }
}
