package com.company.onboarding.view.api1c;


import com.company.onboarding.app.ReportGenHtml;
import com.company.onboarding.app.ReportGenerationService;
import com.company.onboarding.entity.Step;
import com.company.onboarding.entity.User;
import com.company.onboarding.service.BasicAuthImprovedService;
import com.company.onboarding.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.component.checkbox.JmixCheckbox;
import io.jmix.flowui.component.textarea.JmixTextArea;
import io.jmix.flowui.component.textfield.TypedTextField;
import io.jmix.flowui.download.DownloadFormat;
import io.jmix.flowui.download.Downloader;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.view.*;
import io.jmix.reportsflowui.runner.ParametersDialogShowMode;
import io.jmix.reportsflowui.runner.UiReportRunner;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Route(value = "Api1cView", layout = MainView.class)
@ViewController(id = "Api1cview")
@ViewDescriptor(path = "Api1cView.xml")
public class Api1cview extends StandardView {

    private final BasicAuthImprovedService basicAuthService;

    private final Notifications notifications;

    @Autowired
    private Downloader downloader;
    @Autowired
    private ReportGenerationService reportGenerationService;
    @Autowired
    private ReportGenHtml reportGenHtml;
    @Autowired
    private DataManager dataManager;
    @Autowired
    private UiReportRunner uiReportRunner;

    public Api1cview(BasicAuthImprovedService basicAuthService,
                     Notifications notifications,
                     Downloader downloader) {
        this.basicAuthService = basicAuthService;
        this.notifications = notifications;
    }

    @ViewComponent
    private JmixCheckbox cb_savemode;
    @ViewComponent
    private JmixTextArea resultTextArea;
    @ViewComponent
    private TypedTextField<Object> tf_inn;

    @Autowired
    private CurrentAuthentication currentAuthentication;

    @Subscribe(id = "getrequest", subject = "clickListener")
    public void onGetrequestClick(final ClickEvent<JmixButton> event) {

        String result = basicAuthService.getWithBasicAuth(
                "https://1s.rf11.ru/Rezo30/hs/Kontr/dolg/" + tf_inn.getValue() + "/",
                "БахтинИС",
                "ba1202"
        );
        if (result == null ) {
            notifications.show("Не удалось получить результат из 1С.");
            return;
        }
        //
        resultTextArea.setValue(result);
        if (cb_savemode.getValue()) {
            byte[] fileContent = result.getBytes(StandardCharsets.UTF_8);

            if (fileContent.length == 0) {
                notifications.show("Не удалось получить данные из запрооса 1С.");
                return;
            }
            // (4) Инициируем скачивание в браузере
            downloader.download(
                    fileContent,                // данные
                    "response_from_1s_1101006547.json",   // имя файла для сохранения
                    DownloadFormat.JSON
            );
            notifications.show("Ответ сохранен.");
        }
    }

    @Subscribe(id = "clear", subject = "clickListener")
    public void onClearClick(final ClickEvent<JmixButton> event) {
        resultTextArea.clear();
    }

    @Subscribe(id = "jasperTest", subject = "clickListener")
    public void onJasperTestClick(final ClickEvent<JmixButton> event) {

        reportGenerationService.generateAndShowCustomerReport();


    }

    @Subscribe(id = "pdfTest", subject = "clickListener")
    public void onPdfTestClick(final ClickEvent<JmixButton> event) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate Db = LocalDate.parse("01.01.2026", formatter);
        LocalDate De = LocalDate.parse("01.07.2026", formatter);
        final User user = (User) currentAuthentication.getUser();
        try {
            byte[] pdfBytes = reportGenHtml.generateReport(Db, De, user);
            // просто в файл
            /*Path path = Paths.get("report.pdf");
            Files.write(path, pdfBytes);
            System.out.println("PDF сохранён: " + path.toAbsolutePath());*/
            downloader.download(
                    pdfBytes,                           // массив байтов
                    "customer_report1.pdf",              // имя файла
                    // DownloadFormat.OCTET_STREAM
                    DownloadFormat.PDF
            );

        } catch (Exception e) {
            notifications.show(e.getMessage());
        }


    }

    @Subscribe(id = "add_Steps", subject = "clickListener")
    public void onAdd_StepsClick(final ClickEvent<JmixButton> event) {
        // добавим 200 записей
        for (int i = 1; i <= 200; i++) {
            Step step = dataManager.create(Step.class);
            step.setName("Номер " + i);
            step.setDuration(i * 10);   // пример: 10, 20, 30...
            step.setSortValue(i);
            dataManager.save(step);
        }
        notifications.show("Добавлено 200 записей.");
    }

    @Subscribe(id = "pdf_addone", subject = "clickListener")
    public void onPdf_addoneClick(final ClickEvent<JmixButton> event) {
        uiReportRunner.byReportCode("report-for-step")
                .withTemplateCode("pdf-report-for-step")
                .withParametersDialogShowMode(ParametersDialogShowMode.NO)
                .runAndShow(); // Запускаем и показываем результат в UI
        //.addParam("user", usersDataGrid.getSingleSelectedItem())

    }
}