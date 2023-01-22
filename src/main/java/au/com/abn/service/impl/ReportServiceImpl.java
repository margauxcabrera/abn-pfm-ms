package au.com.abn.service.impl;

import au.com.abn.entity.InputData;
import au.com.abn.entity.ReportData;
import au.com.abn.service.ReportService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;

import static au.com.abn.constant.CoreConstants.INPUT_MAPPING_FILE_PATH;
import static au.com.abn.constant.CoreConstants.SUMMARY_DELIMITER;

@Service
@Log4j2
public class ReportServiceImpl implements ReportService {

    private String clientInformationAttributes;
    private String productInformationAttributes;
    private String clientInformationDelimiter;
    private String productInformationDelimiter;

    public ReportServiceImpl(@Value("${client.information.attributes}") final String clientInformationAttributes,
                             @Value("${product.information.attributes}") final String productInformationAttributes,
                             @Value("${client.information.delimiter}") final String clientInformationDelimiter,
                             @Value("${product.information.delimiter}") final String productInformationDelimiter) {
        this.clientInformationAttributes = clientInformationAttributes;
        this.productInformationAttributes = productInformationAttributes;
        this.clientInformationDelimiter = clientInformationDelimiter;
        this.productInformationDelimiter = productInformationDelimiter;
    }

    /**
     * Generate daily report based on file input
     *
     * @param inputFile
     */
    @Override
    public List<ReportData> getDailyReport(final File inputFile) {
        final Map<String, BigDecimal> summary = getSummary(inputFile);
        if (MapUtils.isNotEmpty(summary)) {
            final List<ReportData> reportDataList = new ArrayList<>();
            for (Map.Entry<String, BigDecimal> entry : summary.entrySet()) {
                final ReportData reportData = new ReportData();
                final String[] keys = entry.getKey().split(SUMMARY_DELIMITER);
                reportData.setClientInformation(keys[0]);
                reportData.setProductInformation(keys[1]);
                reportData.setTotalTransactionAmount(entry.getValue());
                reportDataList.add(reportData);
            }
            return reportDataList;
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * Get summary based on input file
     * */
    private Map<String, BigDecimal> getSummary(final File inputFile) {
        final LinkedHashMap<String, Integer> inputMapping = getInputMapping();
        // Retrieve configurable list of client and product information keys
        final List<String> clientInformationAttributeList = Arrays.asList(clientInformationAttributes.split(","));
        final List<String> productInformationAttributeList = Arrays.asList(productInformationAttributes.split(","));
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            final Map<String, BigDecimal> summary = new HashMap<>();
            String input;
            while ((input = br.readLine()) != null) {
                final InputData inputData = convertInputData(input, inputMapping);
                if (null != inputData) {
                    final String clientInformationKey = getKey(clientInformationAttributeList, inputData, clientInformationDelimiter);
                    final String productInformationKey = getKey(productInformationAttributeList, inputData, productInformationDelimiter);
                    if (StringUtils.isNotBlank(clientInformationKey) && StringUtils.isNotBlank(productInformationKey)) {
                        final BigDecimal quantityLong = new BigDecimal(inputData.getQuantityLong().trim());
                        final BigDecimal quantityShort = new BigDecimal(inputData.getQuantityShort().trim());
                        final BigDecimal currentTransactionAmount = quantityLong.subtract(quantityShort);
                        BigDecimal transactionAmount = currentTransactionAmount;
                        final String summaryKey = clientInformationKey.concat(SUMMARY_DELIMITER).concat(productInformationKey);
                        // If client and product key combination is already existing in the map
                        // then current transaction amount will be added to the transaction amount in the map
                        if (summary.containsKey(summaryKey)) {
                            transactionAmount = transactionAmount.add(summary.get(summaryKey));
                        }
                        summary.put(summaryKey, transactionAmount);
                    }
                }
            }
            return summary;
        } catch (final IOException e) {
            log.error("Error encountered while getting summary.", e);
        }
        return Collections.EMPTY_MAP;
    }

    /**
     * Generates the key with a delimiter
     * */
    private String getKey(final List<String> attributeList, final InputData inputData, final String delimiter) {
        try {
            final StringBuilder attributeKey = new StringBuilder();
            for (final String attribute : attributeList) {
                final String methodName = "get".concat(attribute.substring(0, 1).toUpperCase()).concat(attribute.substring(1));
                final Method getMethod = InputData.class.getMethod(methodName);
                if (StringUtils.isNotBlank(attributeKey)) {
                    attributeKey.append(delimiter);
                }
                attributeKey.append(getMethod.invoke(inputData));
            }
            return attributeKey.toString();
        } catch (final NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            log.error("Error encountered while formatting client key.", e);
        }
        return Strings.EMPTY;
    }

    /**
     * Converts string read from the file to InputData for parsing
     * */
    private InputData convertInputData(final String input, final LinkedHashMap<String, Integer> inputMapping) {
        final InputData inputData = new InputData();
        int initialIndex = 0;
        for (Map.Entry<String, Integer> entry : inputMapping.entrySet()) {
            try {
                final String setMethodName = "set".concat(entry.getKey().substring(0, 1).toUpperCase())
                        .concat(entry.getKey().substring(1));
                final Class attributeType = InputData.class.getDeclaredField(entry.getKey()).getType();
                final Method setMethod = InputData.class.getMethod(setMethodName, attributeType);
                final String value = input.substring(initialIndex, initialIndex + entry.getValue()).trim();
                setMethod.invoke(inputData, value);
                initialIndex += entry.getValue();
            } catch (final NoSuchFieldException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                log.error("Error encountered while converting input data.", e);
            }
        }
        return inputData;
    }

    /**
     * Retrieves configuration that will be used to parse input data
     * */
    private LinkedHashMap<String, Integer> getInputMapping() {
        final ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream;
        LinkedHashMap<String, Integer> inputMapping = null;
        try {
            inputStream = classLoader.getResourceAsStream(INPUT_MAPPING_FILE_PATH);
            final Type mapData = new TypeToken<LinkedHashMap<String, Integer>>() {}.getType();
            final Gson gson = new Gson();
            try (JsonReader reader = new JsonReader(new InputStreamReader(inputStream))) {
                inputMapping = gson.fromJson(reader, mapData);
            }
        } catch (final IOException e) {
            log.error("IO Exception in fetching Input Mapping File: %s".formatted(e.getMessage()));
        }
        return inputMapping;
    }
}
