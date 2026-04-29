package com.company.onboarding.view.monitor;

import com.company.onboarding.entity.Monitor;
import com.company.onboarding.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;


@Route(value = "monitors", layout = MainView.class)
@ViewController(id = "Monitor.list")
@ViewDescriptor(path = "monitor-list-view.xml")
@LookupComponent("monitorsDataGrid")
@DialogMode(width = "64em")
public class MonitorListView extends StandardListView<Monitor> {
}