package au.com.abn.controller;

import au.com.abn.utils.ResponseUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;

@RestController
@ApiIgnore
@Log4j2
public class HealthCheckController {


    @RequestMapping(value = "/health", method = RequestMethod.GET)
    public void checkHealth(final HttpServletResponse response) {
        // If API is to connect to a DB or other sources, we can add check to source here
        ResponseUtils.sendResponseToUser(response, HttpStatus.OK);
    }
}
