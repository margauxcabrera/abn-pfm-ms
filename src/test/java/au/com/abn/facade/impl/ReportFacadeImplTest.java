package au.com.abn.facade.impl;

import au.com.abn.service.ReportService;
import au.com.abn.strategy.ExportStrategyRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportFacadeImplTest {

  @Mock
  private ExportStrategyRegistry exportStrategyRegistry;

  @Mock
  private ReportService reportService;

  private ReportFacadeImpl reportFacade;

  MultipartFile multipartFile;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    this.reportFacade = new ReportFacadeImpl(this.reportService, this.exportStrategyRegistry);
  }

  @Test
  void testInvalidOutputType() throws ClassNotFoundException {
    when(exportStrategyRegistry.getExportStrategy("txt")).thenThrow(ClassNotFoundException.class);
    assertEquals(reportFacade.getDailyReport(multipartFile, "txt"), null);
  }

}