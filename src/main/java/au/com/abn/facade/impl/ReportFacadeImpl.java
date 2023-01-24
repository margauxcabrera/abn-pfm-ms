package au.com.abn.facade.impl;

import au.com.abn.entity.ReportData;
import au.com.abn.facade.ReportFacade;
import au.com.abn.service.ReportService;
import au.com.abn.strategy.ExportStrategy;
import au.com.abn.strategy.ExportStrategyRegistry;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Date;
import java.util.List;


@Component
@Log4j2
public class ReportFacadeImpl implements ReportFacade {

    private ReportService reportService;

    private ExportStrategyRegistry exportStrategyRegistry;

    public ReportFacadeImpl(ReportService reportService, ExportStrategyRegistry exportStrategyRegistry) {
        this.reportService = reportService;
        this.exportStrategyRegistry = exportStrategyRegistry;
    }

    /**
     * Retrieve daily report based on output file and export based on output type specified
     * @param file
     * @param outputType
     * */
    @Override
    public Object getDailyReport(final MultipartFile file, final String outputType) {
        Assert.notNull(file, "Input file should not be null.");
        Assert.notNull(outputType, "Output type should not be null.");
        try {
            final ExportStrategy exportStrategy = exportStrategyRegistry.getExportStrategy(outputType);
            final File inputFile = convertToFile(file);
            if (null != inputFile && ".txt".equals(getExtensionWithPeriod(file.getOriginalFilename()))) {
                final List<ReportData> reportDataList = reportService.getDailyReport(inputFile);
                return exportStrategy.exportData(reportDataList);
            }
        } catch (final ClassNotFoundException e) {
            log.error("Error encountered while retrieving export strategy.", e);
        } catch (final IllegalArgumentException e) {
            log.error("Error encountered while processing input file to generate report.", e);
        }
        return null;
    }

    /**
     * Converts multipart file to file
     * */
    private File convertToFile(final MultipartFile file) {
        try {
            final String fileName = "inputFile_".concat(String.valueOf(new Date().getTime()));
            final File tempFile = File.createTempFile(fileName, getExtensionWithPeriod(file.getName()));
            FileUtils.copyInputStreamToFile(file.getInputStream(), tempFile);
            return tempFile;
        } catch (final IOException e) {
            log.error("Error encountered while creating temp file.", e);
        }
        return null;
    }

    /**
     * Retrieves the extension
     * */
    private static String getExtensionWithPeriod(final String fileName) {
        final String extension = FilenameUtils.getExtension(fileName);
        if (StringUtils.isNotEmpty(extension)) {
            return "." + extension;
        }
        return null;
    }
}
