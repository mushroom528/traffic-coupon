package example.traffic.web.restapi.coupon;

public record CreateCouponRequest(String name, String code, int total) {
}
