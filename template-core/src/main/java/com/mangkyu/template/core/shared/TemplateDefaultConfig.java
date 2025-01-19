package com.mangkyu.template.core.shared;

import com.mangkyu.template.core.infra.database.DataSourceConfig;
import com.mangkyu.template.core.infra.jpa.JpaConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@EnableConfigurationProperties
@Import({DataSourceConfig.class, JpaConfig.class})
class TemplateDefaultConfig {
}
