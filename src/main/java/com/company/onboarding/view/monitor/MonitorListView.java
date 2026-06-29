package com.company.onboarding.view.monitor;

import com.company.onboarding.dto.JobMessage;
import com.company.onboarding.entity.Monitor;
import com.company.onboarding.entity.User;
import com.company.onboarding.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.router.Route;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.download.DownloadFormat;
import io.jmix.flowui.download.Downloader;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.view.*;
import io.jmix.quartz.service.QuartzService;
import io.jmix.quartz.util.QuartzJobClassFinder;
import io.jmix.reports.entity.ReportOutputType;
import io.jmix.reports.runner.ReportRunner;
import io.jmix.reports.yarg.reporting.ReportOutputDocument;
import io.jmix.reportsflowui.runner.ParametersDialogShowMode;
import io.jmix.reportsflowui.runner.UiReportRunner;
import org.quartz.SchedulerException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileOutputStream;

import static com.company.onboarding.view.login.LoginView.log;


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
    private UiReportRunner uiReportRunner;
    @Autowired
    private ReportRunner reportRunner;
    @Autowired
    private QuartzService quartzService;
    @Autowired
    private Notifications notifications;

    @Autowired
    private Downloader downloader;

    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {

        final User user = (User) currentAuthentication.getUser();
        monitorsDl.setParameter("user1", user);
        monitorsDl.load();

    }

    @Subscribe(id = "print", subject = "clickListener")
    public void onPrintClick(final ClickEvent<JmixButton> event) {
        final User user = (User) currentAuthentication.getUser();
        uiReportRunner.byReportCode("list-of-monitor")
                .withParametersDialogShowMode(ParametersDialogShowMode.YES)
                .withOutputType(ReportOutputType.XLSX)
                .withOutputNamePattern("list-of-monitor_"+ user.getUsername() + ".xlsx")
                .runAndShow();


        //".xlsx"
        //.inBackground(this)
        //.withParams(Map.of("param1", "value1"))*/
         /*
         ReportOutputDocument reportDocument = reportRunner.byReportCode("list-of-monitor")
                .addParam("user", user) // Передаем параметр, если он нужен
                .withOutputType(ReportOutputType.XLSX) // Указываем тип
                .withOutputNamePattern("list-of-monitor_" + user.getUsername() + ".xlsx")
                .run();

        //byte[] excelContent = reportDocument.getContent();
        try {

            byte[] pdfContent = pdfConverter.convertToPdf(
                    reportDocument.getContent(),
                    "list-of-monitor_" + user.getUsername() + ".pdf"
            );
            downloader.download(
                    pdfContent,                           // массив байтов
                    "list-of-monitor_" + user.getUsername() + ".pdf",              // имя файла
                    // DownloadFormat.OCTET_STREAM
                    DownloadFormat.PDF
            );


        } catch (Exception e) {
            log.error("Ошибка конвертации в PDF", e);
            // показать сообщение пользователю
        }*/

    }


}