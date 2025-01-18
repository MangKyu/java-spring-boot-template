package com.mangkyu.template.core.shared;

import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Arrays;
import java.util.Map;

class TemplateConfigImportSelector implements DeferredImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata metadata) {
        return Arrays.stream(getValues(metadata))
                .map(v -> v.getConfigClass().getName())
                .toArray(String[]::new);
    }

    private TemplateConfigGroup[] getValues(AnnotationMetadata metadata) {
        Map<String, Object> attributes = metadata.getAnnotationAttributes(EnableTemplateConfig.class.getName());
        return attributes == null
                ? new TemplateConfigGroup[]{}
                : (TemplateConfigGroup[]) attributes.getOrDefault("value", new TemplateConfigGroup[]{});
    }
}