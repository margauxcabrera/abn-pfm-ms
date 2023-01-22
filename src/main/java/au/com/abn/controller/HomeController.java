package au.com.abn.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@ApiIgnore
public class HomeController {

    private static final Logger LOG = LogManager.getLogger(HomeController.class);

    @RequestMapping("/")
    public void swagger(final HttpServletResponse httpServletResponse) {
        try {
            httpServletResponse.sendRedirect("swagger-ui/");
        } catch (final IOException e) {
            LOG.error("Error returning swagger document.", e);
        }
    }

}
