package au.com.abn.strategy.export;

import au.com.abn.entity.ReportData;
import au.com.abn.strategy.ExportStrategy;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static au.com.abn.constant.CoreConstants.*;

@Component
@Log4j2
public class CSVExportStrategy implements ExportStrategy {
    /**
     * Specifies which format the strategy generates
     */
    @Override
    public String exportTo() {
        return CSV_OUTPUT_TYPE;
    }

    /**
     * Converts data to CSV format
     *
     * @param reportDataList
     */
    @Override
    public Object exportData(List<ReportData> reportDataList) {
        try {
            final String outputFileName = "output".concat("_").concat(String.valueOf(new Date().getTime()));
            final File outputFile = File.createTempFile(outputFileName, ".csv");
            FileWriter fw = new FileWriter(outputFile);
            BufferedWriter bw = new BufferedWriter(fw);
            addHeader(bw);
            for (final ReportData reportData : reportDataList) {
                final StringBuffer oneLine = new StringBuffer();
                oneLine.append(StringUtils.isNotBlank(reportData.getClientInformation()) ? reportData.getClientInformation() : "");
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(StringUtils.isNotBlank(reportData.getProductInformation()) ? reportData.getProductInformation() : "");
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(null != reportData.getTotalTransactionAmount() ? reportData.getTotalTransactionAmount() : 0);
                bw.write(oneLine.toString());
                bw.newLine();
            }
            bw.flush();
            bw.close();
            return outputFile;
        } catch (final IOException e) {
            log.error("Error encountered while creating CSV output.", e);
        }
        return null;
    }

    private void addHeader(final BufferedWriter bw) {
        try {
            final StringBuffer oneLine = new StringBuffer();
            oneLine.append(CLIENT_INFORMATION_HEADER);
            oneLine.append(CSV_SEPARATOR);
            oneLine.append(PRODUCT_INFORMATION_HEADER);
            oneLine.append(CSV_SEPARATOR);
            oneLine.append(TOTAL_TRANSACTION_AMT_HEADER);
            bw.write(oneLine.toString());
            bw.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
