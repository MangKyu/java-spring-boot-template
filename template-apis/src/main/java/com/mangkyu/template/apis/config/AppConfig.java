package com.mangkyu.template.apis.config;

import com.mangkyu.template.TemplateCoreConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@Import(TemplateCoreConfig.class)
class AppConfig {
}
