package yp970814.swagger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.Parameter;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author yuanping970814@163.com
 * @Date 2023-10-22 12:18
 */
@Configuration
public class SwaggerConfig {

    @Autowired
    private Docket docket;

    private void updateSwaggerDocket(){
        //加上验证参数
        ParameterBuilder ticketPar1 = new ParameterBuilder();
        ParameterBuilder ticketPar2 = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<Parameter>();
        ticketPar1.name("x-iac-token").description("IAC认证")
                .modelRef(new ModelRef("string")).parameterType("header")
                .required(false).build();
        ticketPar2.name("x-auth-token").description("JWT认证")
                .modelRef(new ModelRef("string")).parameterType("header")
                .required(false).build();
        pars.add(ticketPar1.build());
        pars.add(ticketPar2.build());
        docket.globalOperationParameters(pars);
    }

}
