package com.officesmart.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

@Configuration
@EnableSwagger2
class SwaggerConfiguration {

    @Bean
    Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .ignoredParameterTypes(MetaClass.class)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage('com.officesmart'))
                .paths(PathSelectors.any())
                .build()
    }

    private static ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title('API Document')
                .version("1.0")
                .termsOfServiceUrl("https://www.cargosmart.com/en/company/tou.htm")
                .contact(new Contact('Double A Team', '', 'double_a@cargosmart.com'))
                .build()
    }

}
