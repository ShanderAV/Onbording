package com.company.onboarding.security;

import com.company.onboarding.entity.Department;
import com.company.onboarding.entity.Step;
import com.company.onboarding.entity.User;
import com.company.onboarding.entity.UserStep;
import io.jmix.security.model.EntityAttributePolicyAction;
import io.jmix.security.model.EntityPolicyAction;
import io.jmix.security.role.annotation.EntityAttributePolicy;
import io.jmix.security.role.annotation.EntityPolicy;
import io.jmix.security.role.annotation.ResourceRole;
import io.jmix.securityflowui.role.annotation.MenuPolicy;
import io.jmix.securityflowui.role.annotation.ViewPolicy;

@ResourceRole(name = "Employee", code = EmployeeRole.CODE, scope = "UI")
public interface EmployeeRole {
    String CODE = "employee";

    @EntityAttributePolicy(entityClass = User.class,
            attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = User.class, actions = {EntityPolicyAction.READ, EntityPolicyAction.UPDATE})
    void user();

    @EntityAttributePolicy(entityClass = UserStep.class,
            attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = UserStep.class, actions = {EntityPolicyAction.READ, EntityPolicyAction.UPDATE})
    void userStep();

    @MenuPolicy(menuIds = {
            "MyOnboardingView",
            "DataGridLargeDataset"
    })
    @ViewPolicy(viewIds = {
            "MyOnboardingView",
            "DataGridLargeDataset"
    })
    void screens();

    @EntityPolicy(entityClass = Step.class, actions = EntityPolicyAction.READ)
    @EntityAttributePolicy(entityClass = Step.class,
            attributes = "*", action = EntityAttributePolicyAction.VIEW)
    void step();

    @EntityPolicy(entityClass = Department.class, actions = EntityPolicyAction.READ)
    void department();
}