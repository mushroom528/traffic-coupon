package example.traffic.domain.coupon.history;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "coupon_history")
public class CouponHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long couponId;
    private String username;
    @Enumerated(value = EnumType.STRING)
    private HistoryType historyType;

    private String message;
    @CreatedDate
    private LocalDateTime createdAt;

    public static CouponHistory successHistory(Long couponId, String username, String couponNumber) {
        String message = """
                쿠폰 발급 성공 -> 번호: %s
                """.formatted(couponNumber);
        return of(couponId, username, message, HistoryType.SUCCESS);
    }

    public static CouponHistory failHistory(Long couponId, String username, String message) {
        return of(couponId, username, message, HistoryType.FAIL);
    }

    private static CouponHistory of(Long couponId, String username, String message, HistoryType type) {
        CouponHistory couponHistory = new CouponHistory();
        couponHistory.couponId = couponId;
        couponHistory.username = username;
        couponHistory.message = message;
        couponHistory.historyType = type;

        return couponHistory;
    }
}
