package au.com.abn.strategy.export;

import au.com.abn.entity.ReportData;
import au.com.abn.strategy.ExportStrategy;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

import static au.com.abn.constant.CoreConstants.JSON_OUTPUT_TYPE;

@Component
@Log4j2
public class JSONExportStrategy implements ExportStrategy {
    /**
     * Specifies which format the strategy generates
     */
    @Override
    public String exportTo() {
        return JSON_OUTPUT_TYPE;
    }

    /**
     * Converts data to JSON format
     *
     * @param reportDataList
     */
    @Override
    public Object exportData(final List<ReportData> reportDataList) {
        return reportDataList;
    }
}
