package com.green.yp.reference.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.green.yp.common.dto.ResponseApi;
import com.green.yp.reference.dto.LineOfBusinessDto;
import com.green.yp.reference.service.LineOfBusinessService;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
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
public class LineOfBusinessControllerTest {

  @Mock private LineOfBusinessService lineOfBusinessService;
  @InjectMocks private LineOfBusinessController referenceController;

  @BeforeEach
  public void setup() {}

  //    @Test
  void testGetLineOfBusiness_Success() {
    List<LineOfBusinessDto> lobList = new ArrayList<>();
    lobList.add(new LineOfBusinessDto());
    when(lineOfBusinessService.getAllLineOfBusiness()).thenReturn(lobList);

    ResponseApi<List<LineOfBusinessDto>> lobResponseDto = referenceController.getLineOfBusiness();

    assertThat(lobResponseDto).isNotNull();
    assertThat(lobResponseDto.response()).isNotNull();
    assertThat(lobResponseDto.response().size()).isGreaterThan(0);
  }

  @Test
  void testCreateLineOfBusiness_Success() {
    LineOfBusinessDto lobDto =
        LineOfBusinessDto.builder()
            .lineOfBusiness("Lawn-care Service")
            .description("Lawn-care service provider")
            .createDate(OffsetDateTime.now())
            .createByReference(null)
            .build();

    when(lineOfBusinessService.createLineOfBusiness(any(LineOfBusinessDto.class), any(), any()))
        .thenReturn(lobDto);

    ResponseApi<LineOfBusinessDto> responseDto =
        referenceController.createLineOfBusiness(
            LineOfBusinessDto.builder()
                .lineOfBusiness("Lawn-care Service")
                .description("Lawn-care service provider")
                .build());

    assertThat(responseDto).isNotNull();
    assertThat(responseDto.response()).isNotNull();
    assertThat(responseDto.response().getLineOfBusiness()).isEqualTo("Lawn-care Service");
    assertThat(responseDto.response().getCreateDate()).isNotNull();
  }

  @Test
  void testUpdateDescription_Success() {
    LineOfBusinessDto lobDto =
        LineOfBusinessDto.builder()
            .lineOfBusiness("Lawn-care Service")
            .description("Lawn-care service provider")
            .createDate(OffsetDateTime.now())
            .createByReference(null)
            .build();

    when(lineOfBusinessService.updateLineOfBusiness(any(LineOfBusinessDto.class), any(), any()))
        .thenReturn(lobDto);

    ResponseApi<LineOfBusinessDto> responseDto =
        referenceController.updateDescription(
            LineOfBusinessDto.builder()
                .lineOfBusiness("Lawn-care Service")
                .description("Lawn-care service provider")
                .build());

    assertThat(responseDto).isNotNull();
    assertThat(responseDto.response()).isNotNull();
    assertThat(responseDto.response().getLineOfBusiness()).isEqualTo("Lawn-care Service");
    assertThat(responseDto.response().getCreateDate()).isNotNull();
  }
}
