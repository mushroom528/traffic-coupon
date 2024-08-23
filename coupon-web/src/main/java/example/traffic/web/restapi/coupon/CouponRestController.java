package example.traffic.web.restapi.coupon;

import example.traffic.application.coupon.CouponServiceV1;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/coupon")
@RequiredArgsConstructor
public class CouponRestController {

    private final CouponServiceV1 couponService;

    @PostMapping
    public void create(@RequestBody CreateCouponRequest request) {
        couponService.createCoupon(request.name(), request.code(), request.total());
    }

    @PostMapping("/issue")
    public void issue(@RequestBody IssueCouponRequest request) {
        couponService.issueCoupon(request.code(), request.username());
    }
}
