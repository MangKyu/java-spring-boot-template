package com.mangkyu.template.core.shared;

import com.mangkyu.template.core.infra.jpa.JpaConfig;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TemplateConfigGroup {

	JPA(JpaConfig.class),
	;

	private final Class<? extends TemplateCoreConfig> configClass;

}