package com.company.onboarding.entity;

import io.jmix.core.metamodel.datatype.EnumClass;

import org.springframework.lang.Nullable;


public enum DirHealth implements EnumClass<Integer> {

    ХОРОШЕЕ(10),
    НОРМАЛЬНОЕ(20),
    УДОВЛЕТВОРИТЕЛЬНОЕ(30),
    ПЛОХОЕ(40);

    private final Integer id;

    DirHealth(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static DirHealth fromId(Integer id) {
        for (DirHealth at : DirHealth.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}