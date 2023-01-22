package au.com.abn.strategy;

import au.com.abn.entity.ReportData;

import java.util.List;

public interface ExportStrategy {

    /**
     * Specifies which format the strategy generates
     * */
    String exportTo();

    /**
     * Converts data to format
     * */
    Object exportData(final List<ReportData> reportDataList);
}
