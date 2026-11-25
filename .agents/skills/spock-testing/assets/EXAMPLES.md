# Spock Testing — Extended Examples

## Core unit test template
```groovy
import spock.lang.Specification
import spock.lang.Subject

class OrderServiceSpec extends Specification {

    def orderRepository = Mock(OrderRepository)
    def pricingClient = Stub(PricingClient)

    @Subject
    def orderService = new OrderService(orderRepository, pricingClient)

    def "returns full order details for existing transaction id"() {
        given:
        def transactionId = "TXN-123"
        orderRepository.findByTransactionId(transactionId) >> Optional.of(new OrderInfo(transactionId: transactionId))

        when:
        def result = orderService.getFullOrderDetails(transactionId)

        then:
        result != null
        1 * orderRepository.findByTransactionId(transactionId)
    }

    def "throws ResourceNotFoundException when order does not exist"() {
        given:
        orderRepository.findByTransactionId("MISSING") >> Optional.empty()

        when:
        orderService.getFullOrderDetails("MISSING")

        then:
        thrown(ResourceNotFoundException)
    }
}
```

## Data table with @Unroll
```groovy
@Unroll("validates '#input' => valid=#expectedValid")
def "validates transaction id inputs"() {
    expect:
    validator.isValid(input) == expectedValid

    where:
    input      | expectedValid
    "TXN-123" | true
    ""         | false
    null       | false
}
```

## Spring Boot integration spec
```groovy
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerSpec extends Specification {

    @Autowired MockMvc mockMvc

    def "returns 200 with expected payload"() {
        expect:
        mockMvc.perform(get("/orders/{transactionId}", "TXN-123"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
    }
}
```

## JSON fixture helper
```groovy
private String jsonFixture(String fileName) {
    def path = "/${this.class.simpleName.toLowerCase()}/${fileName}"
    new String(this.class.getResourceAsStream(path).readAllBytes(), StandardCharsets.UTF_8)
}
```

## Multi-variable assignment with data tables
```groovy
@Unroll("tier=#tier, quantity=#quantity -> discount=#expectedDiscount")
def "calculates discount by tier and quantity"() {
    expect:
    calculator.discountFor(tier, quantity) == expectedDiscount

    where:
    [tier, quantity, expectedDiscount] << [
        ["STANDARD", 1, 0G],
        ["STANDARD", 10, 5G],
        ["PREMIUM", 10, 10G],
        ["PREMIUM", 50, 20G]
    ]
}
```

## Unified success/error table
```groovy
@Unroll("amount=#amount, card=#cardType => code=#expectedCode, exception=#expectedException")
def "authorizes payment with unified success and error table"() {
    when:
    def response = gateway.authorize(amount, cardType)

    then:
    if (expectedException == null) {
        noExceptionThrown()
        response.code == expectedCode
    } else {
        def ex = thrown(expectedException)
        ex != null
    }

    where:
    [amount, cardType, expectedCode, expectedException] << [
        [100G, "VISA",      "APPROVED", null],
        [0G,   "VISA",      null,         IllegalArgumentException],
        [50G,  "BLOCKED",   null,         PaymentDeclinedException],
        [25G,  "MASTERCARD", "APPROVED", null]
    ]
}
```

## WireMock with inline JSON
```groovy
@SpringBootTest
class PricingClientSpec extends Specification {

    @Shared
    static WireMockServer wireMockServer = new WireMockServer(options().dynamicPort()).tap { start() }

    @Autowired PricingClient pricingClient

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("clients.pricing.base-url", wireMockServer::baseUrl)
    }

    def "returns pricing details from downstream service"() {
        given:
        wireMockServer.resetAll()
        wireMockServer.stubFor(get(urlPathEqualTo("/pricing/TXN-123"))
            .willReturn(okJson('{"transactionId":"TXN-123","price":199.99,"currency":"USD"}')))

        when:
        def result = pricingClient.getPrice("TXN-123")

        then:
        result.transactionId == "TXN-123"
        result.price == 199.99G
    }
}
```

## WireMock with fixture files
```groovy
def "maps downstream response from fixture file"() {
    given:
    wireMockServer.resetAll()
    def responseBody = serviceResponseFixture("pricing-service", "pricing-txn-123_200")
    wireMockServer.stubFor(get(urlEqualTo("/pricing/TXN-123"))
        .willReturn(okJson(responseBody)))

    when:
    def result = pricingClient.getPrice("TXN-123")

    then:
    result.transactionId == "TXN-123"
}
```

## Parameterized WireMock fixtures
```groovy
@Unroll("fixture=#fixtureToken, status=#expectedHttpStatus, exception=#expectedException")
def "maps downstream responses with one table for success and error"() {
    given:
    wireMockServer.resetAll()
    def responseBody = serviceResponseFixture("pricing-service", fixtureToken)
    wireMockServer.stubFor(get(urlEqualTo("/pricing/${transactionId}"))
        .willReturn(aResponse()
            .withStatus(expectedHttpStatus)
            .withHeader("Content-Type", "application/json")
            .withBody(responseBody)))

    when:
    def result = pricingClient.getPrice(transactionId)

    then:
    if (expectedException == null) {
        noExceptionThrown()
        result.statusCode == expectedHttpStatus
    } else {
        def ex = thrown(expectedException)
        ex != null
    }

    where:
    transactionId | fixtureToken             | expectedHttpStatus | expectedException
    "TXN-123"    | "pricing-txn-123_200"   | 200                | null
    "TXN-123"    | "pricing-txn-123_404"   | 404                | ResourceNotFoundException
    "TXN-123"    | "pricing-txn-123_503"   | 503                | DownstreamServiceException
}
```

## WireMock fixture naming convention
- Normalize path segments with dashes
- Replace path variables with concrete ids or stable placeholders
- Append HTTP status as the final suffix
- Examples:
  - `/pricing/TXN-123` with `200` -> `pricing-txn-123_200.json`
  - `/customers/{customerId}` with `404` -> `customers-customerid_404.json`

## WireMock fixture helper
```groovy
private String serviceResponseFixture(String serviceName, String endpointAndStatus) {
    jsonFixture("${serviceName}/${endpointAndStatus}.json".toString())
}
```

## MongoDB integration spec with seed data
```groovy
@DataMongoTest
@Testcontainers
class OrderInfoRepositorySpec extends Specification {

    @Shared
    static MongoDBContainer mongo = new MongoDBContainer("mongo:8")

    @Autowired OrderInfoRepository orderInfoRepository
    @Autowired JsonMapper jsonMapper

    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        mongo.start()
        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl)
    }

    def setup() {
        orderInfoRepository.deleteAll()
        def stream = this.class.getResourceAsStream("/data/order-repository-seed.json")
        def entities = jsonMapper.readValue(stream, new TypeReference<List<OrderInfo>>() {})
        orderInfoRepository.saveAll(entities)
    }

    def "finds seeded order by transaction id"() {
        when:
        def result = orderInfoRepository.findByTransactionId("TXN-123")

        then:
        result.present
    }
}
```

## Fixture file layout
```text
src/test/resources/
  data/
    order-repository-seed.json
  ordercontrollerspec/
    get-order-response.json
    create-order-request.json
    pricing-service/
      pricing-txn-123_200.json
      pricing-txn-123_404.json
```

## MySQL integration spec with seed data

Use a Spring-managed container so its lifecycle and connection details are available to the application context. The example assumes an existing `users` table with `id` and `name` columns; use the application's migrations or test schema to create it. The SQL scripts run before and after each feature to keep the seed data isolated.

```groovy
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.Sql.ExecutionPhase
import org.testcontainers.containers.MySQLContainer
import spock.lang.Specification

@SpringBootTest
@Import(MySqlTestContainers)
@Sql(scripts = '/data/users-seed.sql', executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = '/data/users-cleanup.sql', executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
class UserRepositorySpec extends Specification {

    @Autowired JdbcTemplate jdbcTemplate

    def "finds a seeded user in MySQL"() {
        expect:
        jdbcTemplate.queryForObject('select name from users where id = ?', String, 1001L) == 'Ada'
    }
}

@TestConfiguration(proxyBeanMethods = false)
class MySqlTestContainers {
    @Bean
    @ServiceConnection
    MySQLContainer<?> mysql() {
        new MySQLContainer<>('mysql:9.7.4')
    }
}
```

`src/test/resources/data/users-seed.sql`:
```sql
INSERT INTO users (id, name) VALUES (1001, 'Ada');
```

`src/test/resources/data/users-cleanup.sql`:
```sql
DELETE FROM users WHERE id = 1001;
```

Choose an available MySQL image tag compatible with the application, and use an isolated test database. Add Spring Boot's Testcontainers integration, the Testcontainers MySQL module, a MySQL JDBC driver, and Spring JDBC to the Gradle test runtime, using versions compatible with the project's Spring Boot and Testcontainers dependency management. Docker must be running. Unlike JUnit Jupiter's `@Testcontainers` and `@Container` annotations, the Spring-managed bean also starts and stops the container for a Spock specification.

## Interaction testing patterns
```groovy
then:
1 * repository.save({ it.status == "PRICED" })
0 * notifier._
```

```groovy
then:
1 * client.call(_ as Request) >> new Response(code: 200)
```

## Suggested dependencies (Gradle)
```groovy
// build.gradle
plugins {
    id 'groovy'
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation platform('org.spockframework:spock-bom:2.4-groovy-4.0')
    testImplementation 'org.spockframework:spock-core'
    testImplementation 'org.spockframework:spock-spring'
    testImplementation 'org.wiremock:wiremock:3.13.1'
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher:1.13.4'
}

tasks.named('test') {
    useJUnitPlatform()
}
```

## Version check checklist
- [ ] Java 25
- [ ] Spring Boot 4
- [ ] Spock from `spock-bom` at `2.4-M6+` with Groovy `4.0.x`
- [ ] No duplicate version pins outside Gradle dependency management
