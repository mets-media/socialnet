package socialnet.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.util.ArrayList;
import java.util.List;

@Configuration
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("Zerone-API")
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    public OpenAPI springShopOpenAPI() {

        List<Server> serverList = new ArrayList<>();
        serverList.add(new Server().url("http://localhost:8086/").description("localhost"));
        serverList.add(new Server().url("https://81.177.6.228:8086/").description("server"));
        List<Tag> tagList = new ArrayList<>();
        tagList.add(new Tag().name("MessageWsController").description("message WebSocket"));
        return new OpenAPI()
                .info(new Info()
                        .title("Zerone API")
                        .description("API for social network")
                        .version("1.0")
                        .contact(new Contact().name("JAVA Pro 37 Group").url("http://81.177.6.228:8080").email("aaa@aaaa.aa"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .tags(tagList)
                .servers(serverList);
    }
}

