package cl.duoc.andesstay.catalog.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI catalogOpenApi() {
		return new OpenAPI().info(new Info()
				.title("AndesStay Catalog API")
				.version("1.0")
				.description("CRUD de unidades. Consumido vía BFF en producción."));
	}
}
