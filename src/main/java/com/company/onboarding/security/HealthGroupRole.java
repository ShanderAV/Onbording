package com.company.onboarding.security;

import com.company.onboarding.entity.Monitor;
import com.company.onboarding.entity.Preparat;
import com.company.onboarding.entity.PreparatAccept;
import io.jmix.security.model.EntityAttributePolicyAction;
import io.jmix.security.model.EntityPolicyAction;
import io.jmix.security.role.annotation.EntityAttributePolicy;
import io.jmix.security.role.annotation.EntityPolicy;
import io.jmix.security.role.annotation.ResourceRole;
import io.jmix.securityflowui.role.annotation.MenuPolicy;
import io.jmix.securityflowui.role.annotation.ViewPolicy;

@ResourceRole(name = "HealthGroup", code = HealthGroupRole.CODE)
public interface HealthGroupRole {
    String CODE = "health-group";

    @MenuPolicy(menuIds = {
            "Monitor.list",
            "Preparat.list",
            "PreparatAccept.list"
    })
    @ViewPolicy(viewIds = {
            "Monitor.list",
            "Monitor.detail",
            "Preparat.list",
            "PreparatAccept.list",
            "Preparat.detail",
            "PreparatAccept.detail"
    })
    void screens();

    @EntityAttributePolicy(entityClass = Monitor.class,
            attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Monitor.class, actions = EntityPolicyAction.ALL)
    void monitor();

    @EntityAttributePolicy(entityClass = PreparatAccept.class,
            attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = PreparatAccept.class, actions = EntityPolicyAction.ALL)
    void preparatAccept();

    @EntityAttributePolicy(entityClass = Preparat.class,
            attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Preparat.class, actions = EntityPolicyAction.ALL)
    void preparat();
}