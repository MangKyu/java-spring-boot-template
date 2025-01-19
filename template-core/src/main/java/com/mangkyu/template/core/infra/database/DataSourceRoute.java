package com.mangkyu.template.core.infra.database;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DataSourceRoute {

    RW("Master(Read-Write) DB"),
    RO("Slave(Read-Only) DB"),
    ;

    private final String desc;

}
