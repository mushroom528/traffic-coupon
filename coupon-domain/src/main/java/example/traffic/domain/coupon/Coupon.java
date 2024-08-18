package example.traffic.domain.coupon;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Entity
@Getter
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String code;
    private LocalDateTime expiredAt;

    public static Coupon of(String name, String code, LocalDateTime expiredAt) {
        Coupon coupon = new Coupon();
        coupon.name = name;
        coupon.code = code;
        coupon.expiredAt = expiredAt;
        return coupon;
    }

    public String generateCouponNumber() {
        String timeComponent = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuidComponent = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        return code + timeComponent + uuidComponent;
    }

    public boolean isExpired() {
        return expiredAt.isBefore(LocalDateTime.now());
    }
}
