package com.company.onboarding.view.monitor;

import com.company.onboarding.entity.Monitor;
import com.company.onboarding.entity.User;
import com.company.onboarding.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.core.TimeSource;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalTime;
import java.time.ZonedDateTime;

@Route(value = "monitors/:id", layout = MainView.class)
@ViewController(id = "Monitor.detail")
@ViewDescriptor(path = "monitor-detail-view.xml")
@EditedEntityContainer("monitorDc")
public class MonitorDetailView extends StandardDetailView<Monitor> {
    @Autowired
    private Notifications notifications;
    @Autowired
    private TimeSource timeSource;
    @Autowired
    private CurrentAuthentication currentAuthentication;

    @Subscribe
    public void onInitEntity(final InitEntityEvent<Monitor> event) {
        final User user = (User) currentAuthentication.getUser();
        event.getEntity().setUser(user);

        final ZonedDateTime zonedDateTime = timeSource.now();
        LocalTime localtime = LocalTime.now();

        event.getEntity().setDateTest(zonedDateTime.toLocalDate());
        event.getEntity().setTimeTest(localtime);

       // notifications.show("Значение " + zonedDateTime.toLocalDate());
    }

}