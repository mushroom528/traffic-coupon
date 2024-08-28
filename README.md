# 📚 traffic-coupon

- 동시에 쿠폰을 발행하면 어떻게 될까?
- 개인 공부용 프로젝트
    - `Concurrent`, `Multi Thread`

## 🛠️ Tech Stack

### Backend

- JDK 21
- Spring 6.1
- Spring boot 3.3.2
- JPA
- QueryDsl
- Akka

### Database

- h2(test)
- Maria DB(local)

### Test

- Junit
- Gatling

## 📝 도메인

- 쿠폰 생성
    - 쿠폰 생성 시 만료 기한, 발급 개수를 설정할 수 있다.
- 쿠폰 발급
    - 만료 기한 이후에 쿠폰 발급 시 예외가 발생한다.
    - 발급 개수 넘은 다음 부터는 쿠폰 발급 시 예외가 발생한다.
    - 쿠폰을 발급하면 이력이 저장된다.

## ✨ 주요 로직

```java
public interface CouponService {

    Coupon createCoupon(String name, String code, int totalCoupons);

    void issueCoupon(String couponCode, String username);
}
```

- `createCoupon()`: 쿠폰 생성
- `issueCoupon()`: 쿠폰 발급

### 구현체

- `CouponServiceV1`: 멀티 스레드를 고려하지
  않음 [코드](https://github.com/mushroom528/traffic-coupon/blob/main/coupon-application/src/main/java/example/traffic/application/coupon/CouponServiceV1.java)
- `CouponServiceV2`: `synchronized` 키워드를 사용해서
  동기화 [코드](https://github.com/mushroom528/traffic-coupon/blob/main/coupon-application/src/main/java/example/traffic/application/coupon/CouponServiceV2.java)
- `CouponServiceV3`: `ReentrantLock`을 사용해서
  동기화 [코드](https://github.com/mushroom528/traffic-coupon/blob/main/coupon-application/src/main/java/example/traffic/application/coupon/CouponServiceV3.java)
- `CouponServiceV4`: CAS 연산을 통한 `spin lock`을 사용해서
  동기화 [코드](https://github.com/mushroom528/traffic-coupon/blob/main/coupon-application/src/main/java/example/traffic/application/coupon/CouponServiceV4.java)
- `CouponServiceV5`: **Akka**를 통해 메세지 큐 기능을
  사용 [코드](https://github.com/mushroom528/traffic-coupon/blob/main/coupon-application/src/main/java/example/traffic/application/coupon/CouponServiceV5.java)
