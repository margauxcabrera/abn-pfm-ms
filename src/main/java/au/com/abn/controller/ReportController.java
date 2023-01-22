package au.com.abn.controller;

import au.com.abn.facade.ReportFacade;
import au.com.abn.utils.ResponseUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;

@RestController
@RequestMapping(value = "/api/report")
@Log4j2
@Api(tags={"ABN Reports"})
public class ReportController {

    @Value("${abn.api.key}")
    private String abnApiKey;

    @Autowired
    private ReportFacade reportFacade;

    @ApiOperation(value = "Retrieve daily report")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json")),
            @ApiResponse(description = "Error has occurred.")
    })
    @CrossOrigin
    @GetMapping(value= "/daily/{outputType}")
    public void getAllProducts(@RequestHeader(name = "apiKey") final String apiKey, @PathVariable("outputType") final String outputType,
                               @RequestParam("inputFile") MultipartFile inputFile, final HttpServletResponse response) {
        try {
            if (abnApiKey.equals(apiKey)) {
                final Object reportData = reportFacade.getDailyReport(inputFile, outputType.toUpperCase());
                if (null != reportData) {
                    if (reportData instanceof File) {
                        ResponseUtils.sendFileToUser((File) reportData, response, HttpStatus.OK);
                    } else {
                        ResponseUtils.sendResponseToUser(reportData, response, HttpStatus.OK);
                    }
                } else {
                    ResponseUtils.sendResponseToUser("Error encountered while generating daily report. Please check logs.", response, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                ResponseUtils.sendResponseToUser("User not authorised to access resource.", response, HttpStatus.FORBIDDEN);
            }
        } catch (final Exception e) {
            log.error("Error encountered while retrieving daily report", e);
            ResponseUtils.sendResponseToUser("Error encountered while retrieving products", response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
