package example.traffic.domain.coupon.history;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class CouponHistory {

    @Id
    @GeneratedValue
    private Long id;
    private Long couponId;
    private String username;
    private LocalDateTime createdAt;
}
