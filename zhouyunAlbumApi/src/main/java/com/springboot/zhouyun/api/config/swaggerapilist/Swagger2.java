package com.springboot.zhouyun.api.config.swaggerapilist;


import com.zhouyun.common.enums.ExceptionCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.*;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * Swagger2的配置类
 */

@Configuration
@EnableSwagger2
public class Swagger2 implements WebMvcConfigurer {


    @Value("${swagger2.package}")
    private String basePackage;

    @Value("${swagger2.service.name:spring.application.name}")
    private String title;
    @Value("${swagger2.service.description:null}")
    private String description;
    @Value("${swagger2.service.contact.name:null}")
    private String contactName;
    @Value("${swagger2.service.contact.url:null}")
    private String contactUrl;
    @Value("${swagger2.service.contact.email:null}")
    private String contactEmail;

    @Value("${auth.client.token-header:token}")
    private String tokenHeader;

    private static final String ERROR = "Error";

    /**
     * 这个地方要重新注入一下资源文件，不然不会注入资源的，也没有注入requestHandlerMappping,相当于xml配置的
     * <!--swagger资源配置-->
     * <mvc:resources location="classpath:/META-INF/resources/" mapping="swagger-ui.html"/>
     * <mvc:resources location="classpath:/META-INF/resources/webjars/" mapping="/webjars/**"/>
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Bean
    public Docket createRestApi() {
        ParameterBuilder tokenPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<>();
        tokenPar.name(tokenHeader).description("token令牌")
                .modelRef(new ModelRef("string")).parameterType("header").required(true).build();
        pars.add(tokenPar.build());

        //自定义异常响应头信息
        ArrayList<ResponseMessage> responseMessages = new ArrayList<>();
        responseMessages.add(new ResponseMessageBuilder().code(0).message("成功").build());
        responseMessages.add(new ResponseMessageBuilder().code(ExceptionCode.JWT_TOKEN_EXPIRED.getCode()).message(ExceptionCode.JWT_TOKEN_EXPIRED.getMsg())
                .responseModel(new ModelRef(ERROR)).build());
        responseMessages.add(new ResponseMessageBuilder().code(ExceptionCode.JWT_SIGNATURE.getCode()).message(ExceptionCode.JWT_SIGNATURE.getMsg())
                .responseModel(new ModelRef(ERROR)).build());
        responseMessages.add(new ResponseMessageBuilder().code(ExceptionCode.JWT_ILLEGAL_ARGUMENT.getCode()).message(ExceptionCode.JWT_ILLEGAL_ARGUMENT.getMsg())
                .responseModel(new ModelRef(ERROR)).build());
        responseMessages.add(new ResponseMessageBuilder().code(ExceptionCode.JWT_PARSER_TOKEN_FAIL.getCode()).message(ExceptionCode.JWT_PARSER_TOKEN_FAIL.getMsg())
                .responseModel(new ModelRef(ERROR)).build());
        responseMessages.add(new ResponseMessageBuilder().code(ExceptionCode.SYSTEM_TIMEOUT.getCode()).message(ExceptionCode.SYSTEM_TIMEOUT.getMsg())
                .responseModel(new ModelRef(ERROR)).build());
        responseMessages.add(new ResponseMessageBuilder().code(ExceptionCode.SYSTEM_BUSY.getCode()).message(ExceptionCode.SYSTEM_BUSY.getMsg())
                .responseModel(new ModelRef(ERROR)).build());

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage(basePackage))
                .paths(PathSelectors.any())
                .build().globalOperationParameters(pars)
                .globalResponseMessage(RequestMethod.GET, responseMessages)
                .globalResponseMessage(RequestMethod.POST, responseMessages)
                .globalResponseMessage(RequestMethod.PUT, responseMessages)
                .globalResponseMessage(RequestMethod.DELETE, responseMessages);
    }

    private ApiInfo apiInfo() {
        Contact contact = new Contact(contactName, contactUrl, contactEmail);
        return new ApiInfoBuilder()
                .title(title + " RESTful APIs")
                .description(description)
                .contact(contact)
                .version("1.0")
                .build();
    }


}
