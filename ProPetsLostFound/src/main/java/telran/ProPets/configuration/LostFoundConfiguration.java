package telran.ProPets.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@Getter
@Setter
@RefreshScope
public class LostFoundConfiguration {
	@Value("${templateLoginLost}")
	String templateLoginLost;
	@Value("${templateLoginFound}")
	String templateLoginFound;
	@Value("${templateId}")
	String templateId;
	@Value("${colorUrl}")
	String colorUrl;
	@Value("${tagUrl}")
	String tagUrl;
	@Value("${headerKey}")
	String headerKey;
	@Value("${checkJwtUri}")
	String checkJwtUri;
	@Value("${activityUri}")
	String activityUri;
	@Value("${spring.application.name}")
	String applicationName;
	@Value("${radius}")
	Integer radius;
}
