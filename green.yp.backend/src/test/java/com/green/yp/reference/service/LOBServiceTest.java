package com.green.yp.reference.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.green.yp.api.apitype.enumeration.AuditActionType;
import com.green.yp.api.apitype.enumeration.AuditObjectType;
import com.green.yp.api.contract.ProducerAuditContract;
import com.green.yp.exception.NotFoundException;
import com.green.yp.exception.PreconditionFailedException;
import com.green.yp.reference.data.enumeration.LineOfBusinessCreateType;
import com.green.yp.reference.data.model.LineOfBusiness;
import com.green.yp.reference.data.repository.LineOfBusinessRepository;
import com.green.yp.reference.dto.LineOfBusinessDto;
import com.green.yp.reference.mapper.LineOfBusinessMapperImpl;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
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
class LOBServiceTest {

    @Mock
    LineOfBusinessRepository lobRepository;

    @Mock
    ProducerAuditContract producerAuditContract;

    @InjectMocks
    private LineOfBusinessService lobService;

    @BeforeEach
    void initialize() {
        ReflectionTestUtils.setField(
                lobService, "lineOfBusinessMapper", new LineOfBusinessMapperImpl());
    }

    @Test
    void testGetAllLineOfBusiness_success() {

        List<LineOfBusiness> lobList = List.of(
                LineOfBusiness.builder()
                        .lineOfBusinessName("Lawn Care")
                        .build(),
                LineOfBusiness.builder()
                        .lineOfBusinessName("Pond supplies")
                        .build());

        when(lobRepository.findAllOrderByLineOfBusinessAsc()).thenReturn(lobList);

        List<LineOfBusinessDto> lineOfBusinessDtos = lobService.getAllLineOfBusiness();

        assertThat(lineOfBusinessDtos).isNotNull();
        assertThat(lineOfBusinessDtos.size()).isGreaterThan(0);
    }

    @Test
    void testCreateLineOfBusiness_Success() {

        LineOfBusinessDto lobDto = LineOfBusinessDto.builder()
                   .lineOfBusinessName("Lawn care")
                .description("Basic lawn care and maintaince service")
                .createType(LineOfBusinessCreateType.SYSTEM_DEFAULT)
                .build();

        LineOfBusiness lob = LineOfBusiness.builder()
                .lineOfBusinessName("Lawn care")
                .description("Basic lawn care and maintaince service")
                .shortDescription("")
                .build();

        when(lobRepository.findById(lobDto.lineOfBusinessId())).thenReturn(Optional.empty());

        when(lobRepository.saveAndFlush(any(LineOfBusiness.class)))
                .thenReturn(lob);

        LineOfBusinessDto createdDto = lobService.createLineOfBusiness(lobDto, null, "127.0.0.1");

        assertThat(createdDto.createDate()).isNotNull();
        assertThat(createdDto.lineOfBusinessName()).isEqualTo("Lawn care");
        assertThat(createdDto.description()).isEqualTo("Basic lawn care and maintaince service");
    }

    @Test
    void testCreateLineOfBusiness_AlreadyExists() {

        LineOfBusinessDto lobDto = LineOfBusinessDto.builder()
                .lineOfBusinessId(UUID.randomUUID())
                .lineOfBusinessName("Lawn care")
                .description("Basic lawn care and maintaince service")
                .createType(LineOfBusinessCreateType.SYSTEM_DEFAULT)
                .build();

        LineOfBusiness lob = new LineOfBusiness();
        lob.setId(lobDto.lineOfBusinessId());
        lob.setLineOfBusinessName(lobDto.lineOfBusinessName());
        lob.setCreateType(lobDto.createType());
        lob.setDescription(lobDto.description());
        lob.setVersion(1L);
        lob.setCreateDate(OffsetDateTime.now());
        lob.setLastUpdateDate(OffsetDateTime.now());

        when(lobRepository.findById(lobDto.lineOfBusinessId())).thenReturn(Optional.of(lob));

        when(lobRepository.saveAndFlush(any(LineOfBusiness.class)))
                .thenReturn(lob);

        assertThrows(
                PreconditionFailedException.class,
                () -> lobService.createLineOfBusiness(lobDto, null, "127.0.0.1"));

        verify(producerAuditContract, times(0))
                .createAuditRecord(eq(AuditObjectType.LINE_OF_BUSINESS), eq(AuditActionType.CREATE_LINE_OF_BUSINESS),
                        anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void testUpdateLineOfBusiness_NotFound() {
        LineOfBusinessDto lobDto = LineOfBusinessDto.builder()
                .lineOfBusinessName("Lawn care")
                .description("Basic lawn care and maintaince service")
                .createType(LineOfBusinessCreateType.SYSTEM_DEFAULT)
                .build();

        LineOfBusiness lob = new LineOfBusiness();
        lob.setLineOfBusinessName(lobDto.lineOfBusinessName());
        lob.setCreateType(lobDto.createType());
        lob.setDescription(lobDto.description());
        lob.setVersion(1L);
        lob.setCreateDate(OffsetDateTime.now());
        lob.setLastUpdateDate(OffsetDateTime.now());

        when(lobRepository.findById(lobDto.lineOfBusinessId())).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> lobService.updateLineOfBusinessDescription(lobDto, null, "127.0.0.1"));

        verify(producerAuditContract, times(0))
                .createAuditRecord(eq(AuditObjectType.LINE_OF_BUSINESS), eq(AuditActionType.UPDATE_LINE_OF_BUSINESS),
                        anyString(), anyString(), anyString(), anyString());

        verify(lobRepository, times(0)).saveAndFlush(any(LineOfBusiness.class));
    }

    @Test
    void testUpdateLineOfBusiness_Success() {

        LineOfBusinessDto lobDto = LineOfBusinessDto.builder()
                .lineOfBusinessName("Lawn care")
                .description("Basic lawn care and maintaince service")
                .createType(LineOfBusinessCreateType.SYSTEM_DEFAULT)
                .build();

        LineOfBusiness lob = new LineOfBusiness();
        lob.setLineOfBusinessName(lobDto.lineOfBusinessName());
        lob.setCreateType(lobDto.createType());
        lob.setDescription(lobDto.description());
        lob.setVersion(1L);
        lob.setCreateDate(OffsetDateTime.now());
        lob.setLastUpdateDate(OffsetDateTime.now());

        when(lobRepository.findById(lobDto.lineOfBusinessId())).thenReturn(Optional.of(lob));

        when(lobRepository.saveAndFlush(any(LineOfBusiness.class)))
                .thenReturn(lob);

        LineOfBusinessDto createdDto = lobService.updateLineOfBusinessDescription(lobDto, null, "127.0.0.1");

        assertThat(createdDto.createDate()).isNotNull();
        assertThat(createdDto.lineOfBusinessName()).isEqualTo("Lawn care");
        assertThat(createdDto.description()).isEqualTo("Basic lawn care and maintaince service");
    }
}