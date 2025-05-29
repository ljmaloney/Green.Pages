package com.green.yp.reference.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.green.yp.exception.NotFoundException;
import com.green.yp.reference.data.enumeration.SubscriptionType;
import com.green.yp.reference.data.model.Subscription;
import com.green.yp.reference.data.repository.SubscriptionRepository;
import com.green.yp.reference.dto.LineOfBusinessDto;
import com.green.yp.reference.dto.SubscriptionDto;
import com.green.yp.reference.mapper.SubscriptionMapperImpl;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

@Slf4j
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class SubscriptionServiceTest {

  @Mock private SubscriptionRepository subscriptionRepository;

  @Mock LineOfBusinessService lobService;

  @InjectMocks SubscriptionService subscriptionService;

  @BeforeEach
  public void setup() {
    ReflectionTestUtils.setField(
        subscriptionService, "subscriptionMapper", new SubscriptionMapperImpl());
  }

  @Test
  void getActiveSubscriptions_NotFound() {
    OffsetDateTime testDate = OffsetDateTime.now();
    String lob = null;

    when(subscriptionRepository.findAllActive(any(Date.class), any()))
        .thenReturn(new ArrayList<>());

    assertThrows(NotFoundException.class, () -> subscriptionService.findActiveSubscription());
  }

  @Test
  void testFindActiveSubscription_Success() throws ParseException {
    OffsetDateTime testDate = OffsetDateTime.now();
    String lob = null;

    List<Subscription> subs =
        List.of(
            Subscription.builder()
                .startDate(new SimpleDateFormat("yyyy-MM-dd").parse("2022-01-01"))
                .endDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-12-31"))
                .annualBillAmount(BigDecimal.valueOf(150.0d))
                .monthlyAutopayAmount(BigDecimal.valueOf(20d))
                .displayName("Basic Subscription")
                .build());

    when(subscriptionRepository.findAllActive(
            any(Date.class), eq(SubscriptionType.TOP_LEVEL), eq(SubscriptionType.ADD_ON)))
        .thenReturn(subs);

    List<SubscriptionDto> subscriptions = subscriptionService.findActiveSubscription();

    assertThat(subscriptions).isNotEmpty();
    SubscriptionDto dto = subscriptions.get(0);
    assertThat(dto.shortDescription()).isEqualTo(subs.get(0).getShortDescription());
    assertThat(dto.startDate()).isEqualTo(subs.get(0).getStartDate());
  }

  @Test
  void testGetActiveSubscriptions_ByLineOfBusiness_Success() throws ParseException {
    OffsetDateTime testDate = OffsetDateTime.now();
    UUID lobId = UUID.randomUUID();

    List<Subscription> subs =
        List.of(
            Subscription.builder()
                .startDate(new SimpleDateFormat("yyyy-MM-dd").parse("2022-01-01"))
                .endDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-12-31"))
                .subscriptionType(SubscriptionType.LINE_OF_BUSINESS)
                .lineOfBusinessId(UUID.randomUUID())
                .annualBillAmount(BigDecimal.valueOf(150.0d))
                .monthlyAutopayAmount(BigDecimal.valueOf(20d))
                .displayName("Basic Subscription")
                .build());

    when(subscriptionRepository.findAllActive(
            eq(lobId),
            any(Date.class),
            eq(SubscriptionType.LINE_OF_BUSINESS),
            eq(SubscriptionType.LINE_OF_BUSINESS_ADD_ON)))
        .thenReturn(subs);

    when(lobService.findLineOfBusiness(eq(lobId)))
        .thenReturn(
            LineOfBusinessDto.builder()
                .lineOfBusinessId(lobId)
                .lineOfBusinessName("Lawn maintenance")
                .build());

    List<SubscriptionDto> subscriptions = subscriptionService.findActiveLobSubscription(lobId);

    assertThat(subscriptions).isNotEmpty();
    SubscriptionDto dto = subscriptions.get(0);
    assertThat(dto.shortDescription()).isEqualTo(subs.get(0).getShortDescription());
    assertThat(dto.startDate()).isEqualTo(subs.get(0).getStartDate());
    assertThat(dto.lineOfBusinessId()).isEqualTo(subs.get(0).getLineOfBusinessId());
  }
}
