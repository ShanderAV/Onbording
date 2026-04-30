package com.company.onboarding.view.preparataccept;

import com.company.onboarding.entity.PreparatAccept;
import com.company.onboarding.entity.User;
import com.company.onboarding.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.core.TimeSource;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;

@Route(value = "preparat-accepts/:id", layout = MainView.class)
@ViewController(id = "PreparatAccept.detail")
@ViewDescriptor(path = "preparat-accept-detail-view.xml")
@EditedEntityContainer("preparatAcceptDc")
public class PreparatAcceptDetailView extends StandardDetailView<PreparatAccept> {

    private final TimeSource timeSource;
    @Autowired
    private CurrentAuthentication currentAuthentication;

    public PreparatAcceptDetailView(TimeSource timeSource) {
        this.timeSource = timeSource;
    }

    @Subscribe
    public void onInitEntity(final InitEntityEvent<PreparatAccept> event) {

        final ZonedDateTime zonedDateTime = timeSource.now();
        LocalDateTime acceptedDate = LocalDateTime.now();

        event.getEntity().setAcceptedDate(acceptedDate);
        final User user = (User) currentAuthentication.getUser();
        event.getEntity().setUser(user);

    }


}
