package com.mangkyu.template.core.infra.jpa;

import com.mangkyu.template.core.shared.TemplateCoreConfig;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@EntityScan("com.mangkyu.template")
@EnableJpaRepositories("com.mangkyu.template")
public class JpaConfig implements TemplateCoreConfig {
}