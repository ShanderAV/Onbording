package com.company.onboarding.view.myonboarding;


import com.company.onboarding.entity.User;
import com.company.onboarding.entity.UserStep;
import com.company.onboarding.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.Route;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.flowui.UiComponents;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.model.DataContext;
import io.jmix.flowui.model.InstanceContainer;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

@Route(value = "my-onboarding-view", layout = MainView.class)
@ViewController(id = "MyOnboardingView")
@ViewDescriptor(path = "my-onboarding-view.xml")
public class MyOnboardingView extends StandardView {
    @Autowired
    private CurrentAuthentication currentAuthentication;
    @ViewComponent
    private CollectionLoader<UserStep> userStepsDl;
    @Autowired
    private UiComponents uiComponents;
    @ViewComponent
    private DataContext dataContext;

    // event open
    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {
        final User user = (User) currentAuthentication.getUser();
        userStepsDl.setParameter("user2", user);
        userStepsDl.load();
        //
        updateLabels();
    }

    @ViewComponent
    private Span totalStepsLabel;
    @ViewComponent
    private Span overdueLabel;
    @ViewComponent
    private Span completedStepsLabel;
    @ViewComponent
    private CollectionContainer<UserStep> userStepsDc;

    @Supply(to = "userStepsDataGrid.completed", subject = "renderer")
    private Renderer<UserStep> userStepsDataGridCompletedRenderer() {
        return new ComponentRenderer<>(userStep -> {
            Checkbox checkbox = uiComponents.create(Checkbox.class);

            checkbox.setValue(userStep.getComletedDate() != null);
            checkbox.addValueChangeListener(e -> {
                if (userStep.getComletedDate() == null) {
                    userStep.setComletedDate(LocalDate.now());
                } else {
                    userStep.setComletedDate(null);
                }
            });
            return checkbox;
        });
    }
    // расчет значений переменных
    private void updateLabels() {
        totalStepsLabel.setText("Total steps: " + userStepsDc.getItems().size());
        long completedCount = userStepsDc.getItems().stream()
                .filter(us -> us.getComletedDate() != null)
                .count();
        completedStepsLabel.setText("Completed steps: " + completedCount);

        long overdueCount = userStepsDc.getItems().stream()
                .filter(this::isOverdue)
                .count();

        overdueLabel.setText("Overdue steps: " + overdueCount);
    }

    private boolean isOverdue(UserStep us) {
        return us.getComletedDate() == null
                && us.getDueDate() != null
                && us.getDueDate().isBefore(LocalDate.now());
    }
    // itemchanged
    @Subscribe(id = "userStepsDc", target = Target.DATA_CONTAINER)
    public void onUserStepsDcItemPropertyChange(final InstanceContainer.ItemPropertyChangeEvent<UserStep> event) {
        updateLabels();
    }

    // event clicked Save
    @Subscribe(id = "saveButton", subject = "clickListener")
    public void onSaveButtonClick(final ClickEvent<JmixButton> event) {
        //сохраним сущность
        dataContext.save();
        // закрыть окно
        close(StandardOutcome.SAVE);
    }

    // event clicked Cancel
    @Subscribe(id = "discardButton", subject = "clickListener")
    public void onDiscardButtonClick(final ClickEvent<JmixButton> event) {
        close(StandardOutcome.DISCARD);
    }

    @Install(to = "userStepsDataGrid.dueDate", subject = "partNameGenerator")
    private String userStepsDataGridDueDatePartNameGenerator(final UserStep userStep) {

        return isOverdue(userStep) ? "overdue-step" : null;
    }


}