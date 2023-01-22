package au.com.abn.service;

import au.com.abn.entity.ReportData;

import java.io.File;
import java.util.List;

public interface ReportService {
    /**
     * Generate daily report based on file inputzx
     */
    List<ReportData> getDailyReport(final File inputFile);
}
