package example.traffic.application.akka;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import example.traffic.application.coupon.CouponService;

public class CouponIssueActor extends AbstractBehavior<CouponIssueActor.Command> {

    private final CouponService couponService;

    public interface Command {
    }

    public record IssueCommand(String couponCode, String username) implements Command {
    }

    public CouponIssueActor(ActorContext<Command> context, CouponService couponService) {
        super(context);
        this.couponService = couponService;
    }

    public static Behavior<Command> create(CouponService couponService) {
        return Behaviors.setup(ctx -> new CouponIssueActor(ctx, couponService));
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(IssueCommand.class, this::onIssue)
                .build();
    }

    private Behavior<Command> onIssue(IssueCommand command) {
        couponService.issueCoupon(command.couponCode, command.username);
        return this;
    }
}
