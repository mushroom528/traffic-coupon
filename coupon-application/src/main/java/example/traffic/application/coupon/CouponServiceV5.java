package example.traffic.application.coupon;

import akka.actor.typed.ActorSystem;
import example.traffic.application.akka.CouponIssueActor;
import example.traffic.domain.coupon.Coupon;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CouponServiceV5 implements CouponService {

    private final CouponService couponService;
    private final ActorSystem<CouponIssueActor.Command> couponIssueActor;

    public CouponServiceV5(CouponService couponService) {
        this.couponService = couponService;
        couponIssueActor = ActorSystem.create(CouponIssueActor.create(couponService), "coupon-issue-actor");
    }

    public Coupon createCoupon(String name, String code, int totalCoupons) {
        return couponService.createCoupon(name, code, totalCoupons);
    }

    /**
     * 메세지큐를 통해서 처리하는 방식
     * akka actor는 내부에 mailbox라는 메세지큐를 가지고 있어, 메세지를 순차적으로 처리한다.
     */
    public void issueCoupon(String couponCode, String username) {
        couponIssueActor.tell(new CouponIssueActor.IssueCommand(couponCode, username));
    }

}
