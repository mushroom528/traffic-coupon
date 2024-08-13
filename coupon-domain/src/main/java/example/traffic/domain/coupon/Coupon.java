package example.traffic.domain.coupon;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Coupon {

    @Id @GeneratedValue
    private Long id;
    private String name;
    private String code;

    public static Coupon of(String name, String code) {
        Coupon coupon = new Coupon();
        coupon.name = name;
        coupon.code = code;
        return coupon;
    }
}
