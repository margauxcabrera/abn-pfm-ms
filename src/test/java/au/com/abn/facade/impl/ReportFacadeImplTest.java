package au.com.abn.facade.impl;

import au.com.abn.entity.ReportData;
import au.com.abn.service.ReportService;
import au.com.abn.service.impl.ReportServiceImpl;
import au.com.abn.strategy.ExportStrategyRegistry;
import au.com.abn.strategy.export.CSVExportStrategy;
import au.com.abn.strategy.export.JSONExportStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportFacadeImplTest {

  @Mock
  private ExportStrategyRegistry exportStrategyRegistry;

  @Mock
  private ReportService reportService;

  @Mock
  private JSONExportStrategy jsonExportStrategy = new JSONExportStrategy();

  @Mock
  private CSVExportStrategy csvExportStrategy = new CSVExportStrategy();

  private ReportFacadeImpl reportFacade;

  MultipartFile multipartFile;
  File csvFile;
  final List<ReportData> reportDataList = new ArrayList<>();

  @BeforeEach
  void setUp() throws IOException {
    MockitoAnnotations.openMocks(this);
    this.reportFacade = new ReportFacadeImpl(this.reportService, this.exportStrategyRegistry);
    multipartFile = new MockMultipartFile("sourceFile.txt", "sourceFile.txt", "text/plain", "315CL  432100020001SGXDC FUSGX NK    20100910JPY01B 0000000001 0000000000000000000060DUSD000000000030DUSD000000000000DJPY201008200012380     688032000092500000000             O".getBytes());
    csvFile = File.createTempFile("output", ".csv");
    final ReportData reportData = new ReportData();
    reportData.setClientInformation("CL1");
    reportData.setProductInformation("PR1");
    reportData.setTotalTransactionAmount(new BigDecimal(100));
    reportDataList.add(reportData);
  }

  @Test
  void testInvalidOutputType() throws ClassNotFoundException {
    when(exportStrategyRegistry.getExportStrategy("txt")).thenThrow(ClassNotFoundException.class);
    assertEquals(reportFacade.getDailyReport(multipartFile, "txt"), null);
  }

  @Test
  void testJSONOutputType() throws ClassNotFoundException {
    when(exportStrategyRegistry.getExportStrategy("JSON")).thenReturn(jsonExportStrategy);
    when(jsonExportStrategy.exportData(reportDataList)).thenReturn(reportDataList);
    when(reportService.getDailyReport(any(File.class))).thenReturn(reportDataList);
    final Object returnReportData = reportFacade.getDailyReport(multipartFile, "JSON");
    assertInstanceOf(List.class, returnReportData);
  }

  @Test
  void testCSVOutputType() throws ClassNotFoundException {
    when(exportStrategyRegistry.getExportStrategy("CSV")).thenReturn(csvExportStrategy);
    when(csvExportStrategy.exportData(reportDataList)).thenReturn(csvFile);
    when(reportService.getDailyReport(any(File.class))).thenReturn(reportDataList);
    final Object returnReportData = reportFacade.getDailyReport(multipartFile, "CSV");
    assertInstanceOf(File.class, returnReportData);
  }

}