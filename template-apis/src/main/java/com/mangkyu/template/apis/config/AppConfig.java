package com.mangkyu.template.apis.config;

import com.mangkyu.template.core.shared.EnableTemplateConfig;
import com.mangkyu.template.core.shared.TemplateConfigGroup;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableTemplateConfig({TemplateConfigGroup.JPA, })
class AppConfig {
}
