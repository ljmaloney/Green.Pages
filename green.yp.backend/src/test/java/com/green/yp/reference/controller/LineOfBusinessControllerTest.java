package com.green.yp.reference.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.green.yp.common.dto.ResponseApi;
import com.green.yp.reference.data.enumeration.LineOfBusinessCreateType;
import com.green.yp.reference.dto.LineOfBusinessDto;
import com.green.yp.reference.service.LineOfBusinessService;
import java.time.OffsetDateTime;
import java.util.ArrayList;
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

@Slf4j
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LineOfBusinessControllerTest {

  @Mock private LineOfBusinessService lineOfBusinessService;
  @InjectMocks private LineOfBusinessController referenceController;

  @BeforeEach
  void setup() {}

  //    @Test
  void testGetLineOfBusiness_Success() {
    List<LineOfBusinessDto> lobList = new ArrayList<>();
    lobList.add(new LineOfBusinessDto(UUID.randomUUID(),
            OffsetDateTime.now(),
            OffsetDateTime.now(),
            "lineofbusiness",
            LineOfBusinessCreateType.SYSTEM_DEFAULT,
            "",
            "short description",
            "description",
false, "flower", null
            ));
    when(lineOfBusinessService.getAllLineOfBusiness()).thenReturn(lobList);

    ResponseApi<List<LineOfBusinessDto>> lobResponseDto = referenceController.getLineOfBusiness();

    assertThat(lobResponseDto).isNotNull();
    assertThat(lobResponseDto.response()).isNotNull();
    assertThat(lobResponseDto.response()).hasSizeGreaterThan(0);
  }

  @Test
  void testCreateLineOfBusiness_Success() {
    LineOfBusinessDto lobDto =
        LineOfBusinessDto.builder()
            .lineOfBusinessName("Lawn-care Service")
            .description("Lawn-care service provider")
            .createDate(OffsetDateTime.now())
            .createByReference(null)
            .build();

    when(lineOfBusinessService.createLineOfBusiness(any(LineOfBusinessDto.class), any(), any()))
        .thenReturn(lobDto);

    ResponseApi<LineOfBusinessDto> responseDto =
        referenceController.createLineOfBusiness(
            LineOfBusinessDto.builder()
                .lineOfBusinessName("Lawn-care Service")
                .description("Lawn-care service provider")
                .build());

    assertThat(responseDto).isNotNull();
    assertThat(responseDto.response()).isNotNull();
    assertThat(responseDto.response().lineOfBusinessName()).isEqualTo("Lawn-care Service");
    assertThat(responseDto.response().createDate()).isNotNull();
  }

  @Test
  void testUpdateDescription_Success() {
    LineOfBusinessDto lobDto =
        LineOfBusinessDto.builder()
            .lineOfBusinessName("Lawn-care Service")
            .description("Lawn-care service provider")
            .createDate(OffsetDateTime.now())
            .createByReference(null)
            .build();

    when(lineOfBusinessService.updateLineOfBusinessDescription(any(LineOfBusinessDto.class), any(), any()))
        .thenReturn(lobDto);

    ResponseApi<LineOfBusinessDto> responseDto =
        referenceController.updateDescription(
            LineOfBusinessDto.builder()
                .lineOfBusinessName("Lawn-care Service")
                .description("Lawn-care service provider")
                .build());

    assertThat(responseDto).isNotNull();
    assertThat(responseDto.response()).isNotNull();
    assertThat(responseDto.response().lineOfBusinessName()).isEqualTo("Lawn-care Service");
    assertThat(responseDto.response().createDate()).isNotNull();
  }
}
