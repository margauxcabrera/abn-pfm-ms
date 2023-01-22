package au.com.abn.facade;

import org.springframework.web.multipart.MultipartFile;

public interface ReportFacade {

    /**
     * Retrieve daily report based on output file and export based on output type specified
     * */
    Object getDailyReport(final MultipartFile file, final String outputType);
}
