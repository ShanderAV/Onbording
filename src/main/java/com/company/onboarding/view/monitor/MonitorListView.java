package com.company.onboarding.view.monitor;

import com.company.onboarding.entity.Monitor;
import com.company.onboarding.entity.User;
import com.company.onboarding.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.router.Route;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.view.*;
import io.jmix.reports.entity.ReportOutputType;
import io.jmix.reports.runner.ReportRunner;
import io.jmix.reports.yarg.reporting.ReportOutputDocument;
import io.jmix.reportsflowui.runner.ParametersDialogShowMode;
import io.jmix.reportsflowui.runner.UiReportRunner;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;


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
    @Autowired
    private ReportRunner reportRunner;
    @Autowired
    private UiReportRunner uiReportRunner;

    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {

        final User user = (User) currentAuthentication.getUser();
        monitorsDl.setParameter("user1", user);
        monitorsDl.load();

    }

    @Subscribe(id = "print", subject = "clickListener")
    public void onPrintClick(final ClickEvent<JmixButton> event) {
        uiReportRunner.byReportCode("list-of-monitor")
                .withOutputType(ReportOutputType.XLSX)
                .withOutputNamePattern("11122.xlsx")
                .withParametersDialogShowMode(ParametersDialogShowMode.IF_REQUIRED)
                .inBackground(this)
                .runAndShow();
        //.withParams(Map.of("param1", "value1"))

    }

}