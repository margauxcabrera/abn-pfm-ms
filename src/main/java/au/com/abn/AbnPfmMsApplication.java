package au.com.abn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;

@SpringBootApplication
@EnableConfigurationProperties
@EnableOpenApi
@Configuration
public class AbnPfmMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AbnPfmMsApplication.class, args);
    }

    @Bean
    public Docket apiDocket() {
        return new Docket(DocumentationType.OAS_30)
                .select()
                .apis(RequestHandlerSelectors.basePackage("au.com.abn"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(getApiInfo());
    }

    private ApiInfo getApiInfo() {
        return new ApiInfo(
                "ABN AMRO Clearing Sydney API",
                "ABN AMRO Clearing Sydney API",
                "1.0.0",
                "",
                new Contact("ABN AMRO Clearing Sydney", "https://www.abnamro.com/", ""),
                "",
                "",
                Collections.emptyList()
        );
    }

}
