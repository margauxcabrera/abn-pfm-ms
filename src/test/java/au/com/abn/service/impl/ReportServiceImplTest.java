package au.com.abn.service.impl;

import au.com.abn.entity.InputData;
import au.com.abn.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

  private String clientInfoAttributes = "clientType,clientNumber";
  private String productInformationAttributes = "exchangeCode,productGroupCode";
  private String clientInformationDelimiter = "-";
  private String productInformationDelimiter = "-";

  private ReportService reportService;

  Map<String, BigDecimal> summary;
  File file;

  @BeforeEach
  void setUp() throws IOException {
    reportService = new ReportServiceImpl(clientInfoAttributes, productInformationAttributes, clientInformationDelimiter, productInformationDelimiter);
    summary = new HashMap<>();
    file = File.createTempFile("input",".txt");
  }

  @Test
  void testGetKey() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    final InputData inputData = new InputData();
    inputData.setClientType("CL1");
    inputData.setClientNumber("CLN");
    final List<String> clientAttributeList = new ArrayList<>();
    clientAttributeList.add("clientType");
    clientAttributeList.add("clientNumber");
    final String key = String.valueOf(getPrivateMethod("getKey", List.class, InputData.class, String.class)
            .invoke(reportService, clientAttributeList, inputData, "-"));
    assertEquals( "CL1-CLN", key);
  }

  @Test
  void testConvertInputData() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    final String inputString = "315CL  432100020001SGXDC FUSGX NK    20100910JPY01B 0000000001 0000000000000000000060DUSD000000000030DUSD000000000000DJPY201008200012380     688032000092500000000             O";
    final LinkedHashMap<String, Integer> inputMapping = new LinkedHashMap<>();
    inputMapping.put("recordCode", 3);
    inputMapping.put("clientType", 4);
    inputMapping.put("clientNumber", 4);
    final LinkedHashMap<String, Integer> inputDecimal = new LinkedHashMap<>();
    inputDecimal.put("clientNumber", 2);
    final InputData inputData = (InputData) getPrivateMethod("convertInputData", String.class, LinkedHashMap.class, LinkedHashMap.class)
            .invoke(reportService, inputString, inputMapping, inputDecimal);
    assertEquals("315", inputData.getRecordCode());
    assertEquals("CL", inputData.getClientType());
    assertEquals("43.21", inputData.getClientNumber());
  }

  @Test
  void testGetDecimalValue() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    final StringBuilder sb = new StringBuilder();
    sb.append("000000000060");
    final Integer decimalPlaces = 2;
    getPrivateMethod("getDecimalValue", StringBuilder.class, Integer.class).invoke(reportService, sb, decimalPlaces);
    assertEquals(sb.toString(), "0000000000.60");
  }

  private Method getPrivateMethod(final String methodName, final Class<?>... parameterTypes) throws NoSuchMethodException {
    ReportServiceImpl reportService1 = new ReportServiceImpl(clientInfoAttributes, productInformationAttributes, clientInformationDelimiter, productInformationDelimiter);
    Method method = reportService1.getClass().getDeclaredMethod(methodName, parameterTypes);
    method.setAccessible(true);
    return method;
  }

}