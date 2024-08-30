# 📚 traffic-coupon

- 인프런 강의 `김영한의 실전 자바 - 고급 1편, 멀티스레드와 동시성` 을 들으면서 배운것을 응용하기 위해 만든 프로젝트
- 동시에 쿠폰을 발행하면 어떻게 될까?
- **스레드 동기화**, **동시성 처리**

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

## 테스트

### 서비스 로직 테스트

- **junit**을 사용한다.

```java

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CouponConfig.class})
class CouponServiceTest {

    @Autowired
    CouponService sut;
    @Autowired
    CouponInventoryRepository couponInventoryRepository;
    @Autowired
    CouponHistoryRepository couponHistoryRepository;
    @Autowired
    CouponRepository couponRepository;

    Coupon coupon;

    int couponCount = 30;
    int requestCount = 50;

    @BeforeEach
    void setUp() {
        coupon = sut.createCoupon("테스트쿠폰1", "COUPON-1", couponCount);
    }

    @AfterEach
    void tearDown() {
        couponInventoryRepository.deleteAll();
        couponHistoryRepository.deleteAll();
        couponRepository.deleteAll();
    }

    @Test
    @DisplayName("50명이 신청하는 경우, 30개의 성공이력 20개의 실패이력이 생성된다.")
    void shouldCreate30SuccessAnd20FailureRecordsWhen50ApplicantsApply() {

        // when
        for (int i = 0; i < requestCount; i++) {
            sut.issueCoupon(coupon.getCode(), "USER");
        }
        CouponInventory couponInventory = couponInventoryRepository.findByCouponCode(coupon.getCode()).get();
        List<CouponHistory> histories = couponHistoryRepository.findAll();

        // then
        assertEquals(50, histories.size());
        assertEquals(30, couponInventory.getIssuedCoupons());
        assertHistoryCount(HistoryType.SUCCESS, 30, histories);
        assertHistoryCount(HistoryType.FAIL, 20, histories);
    }

    @Test
    @DisplayName("동시에 50명이 신청하는 경우, 30개의 성공이력 20개의 실패이력이 생성된다.")
    void shouldCreate30SuccessAnd20FailureRecordsWhen50ApplicantsApplyConcurrently() throws InterruptedException {

        // when
        try (ExecutorService executorService = Executors.newFixedThreadPool(32)) {
            CountDownLatch countDownLatch = new CountDownLatch(requestCount);
            for (int i = 0; i < requestCount; i++) {
                executorService.submit(() -> {
                    try {
                        sut.issueCoupon(coupon.getCode(), "USER");
                    } finally {
                        countDownLatch.countDown();
                    }
                });
            }
            countDownLatch.await();
        }

        CouponInventory couponInventory = couponInventoryRepository.findByCouponCode(coupon.getCode()).get();
        List<CouponHistory> histories = couponHistoryRepository.findAll();

        assertEquals(50, histories.size());
        assertEquals(30, couponInventory.getIssuedCoupons());
        assertHistoryCount(HistoryType.SUCCESS, 30, histories);
        assertHistoryCount(HistoryType.FAIL, 20, histories);
    }

    void assertHistoryCount(HistoryType type, int expected, List<CouponHistory> histories) {
        long count = histories.stream().filter(history -> history.getHistoryType() == type).count();
        assertEquals(expected, count);
    }

}
```

- `CouponService`의 구현체를 변경하면서 테스트를 진행한다.
- 구현체 변경은 `CouponConfing`에서 변경한다.
- 동시성을 고려하지 않은 `CouponServiceV1`에서는 아래와 같이 테스트 결과가 나올 것이다.

```shell
Expected :30
Actual   :12
<Click to see difference>

org.opentest4j.AssertionFailedError: expected: <30> but was: <12>
	at org.junit.jupiter.api.AssertionFailureBuilder.build(AssertionFailureBuilder.java:151)
	at org.junit.jupiter.api.AssertionFailureBuilder.buildAndThrow(AssertionFailureBuilder.java:132)
	at org.junit.jupiter.api.AssertEquals.failNotEqual(AssertEquals.java:197)
	at org.junit.jupiter.api.AssertEquals.assertEquals(AssertEquals.java:150)
	at org.junit.jupiter.api.AssertEquals.assertEquals(AssertEquals.java:145)
	at org.junit.jupiter.api.Assertions.assertEquals(Assertions.java:531)
	at example.traffic.application.coupon.CouponServiceTest.shouldCreate30SuccessAnd20FailureRecordsWhen50ApplicantsApplyConcurrently(CouponServiceTest.java:100)
```

- 동시성 이슈가 발생하여 테스트가 실패한다.
- `CouponServiceV1`이 아닌 다른 구현체로 테스트를 진행하면 모든 테스트가 성공한다.
    - 구현체마다 테스트 소요 시간이 다를 것이다.

### API 부하 테스트

- **gatling**을 사용한다.
- 자세한 사용방법은 아래의 링크를 참고하였다.
    - [블로그](https://code-run.tistory.com/42)
    - [gatling 공식문서](https://docs.gatling.io/)
- 처음 사용해보기 때문에 간단하게 샘플을 만들었다.

**샘플 코드**

```java

@RestController
@RequestMapping("/api/gatling")
public class GatlingSampleRestController {

    @GetMapping()
    public String sample() throws InterruptedException {
        TimeUnit.SECONDS.sleep(1);
        return "Hello World";
    }
}
```

- 해당 API는 요청을 받으면 1초동안 대기한다.

```java
public class SampleApiSimulator extends Simulation {
    private static final String BASE_URL = "http://localhost:8080";

    ScenarioBuilder scn = scenario("Gatling Sample Test")
            .exec(http("GET /api/gatling")
                    .get("/api/gatling")
                    .check(status().is(200))
                    .check(bodyString().is("Hello World"))
            );

    {
        setUp(
                scn.injectClosed(constantConcurrentUsers(10).during(10))
        ).protocols(
                http.baseUrl(BASE_URL)
        );
    }
}
```

- 샘플 API에 요청을 보내는 테스트
- `setUp`블록에서 가상의 유저를 설정할 수 있음.
- 10명의 유저가 10초 동안 동시에 요청하는 시나리오
- 테스트 실행: `mvn gatling:test -Dgatling.simulationClass=example.gatling.SampleApiSimulator`
- 실행이 완료 되면 결과를 확인할 수 있도록 리포트가 생성된다.
    - 리포트 위치: target/gatling 하위 디렉토리의 `index.html`
      ![img.png](images/img1.png)
    - 요청 횟수 100
    - 모든 요청이 800ms ~ 1200ms 이내에 처리되었음
    - 99%의 요청이 1014ms 이내에 처리되었음
- 