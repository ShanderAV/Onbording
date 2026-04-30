package com.company.onboarding.view.monitor;

import com.company.onboarding.entity.Monitor;
import com.company.onboarding.entity.User;
import com.company.onboarding.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;


@Route(value = "monitors", layout = MainView.class)
@ViewController(id = "Monitor.list")
@ViewDescriptor(path = "monitor-list-view.xml")
@LookupComponent("monitorsDataGrid")
@DialogMode(width = "64em")
public class MonitorListView extends StandardListView<Monitor> {
    @Autowired
    private CurrentAuthentication currentAuthentication;
    @ViewComponent
    private CollectionLoader<Monitor> monitorsDl;

    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {

        final User user = (User) currentAuthentication.getUser();
        monitorsDl.setParameter("user1", user);
        monitorsDl.load();

    }

}