package com.company.onboarding.view.api1c;


import com.company.onboarding.app.ReportGenerationService;
import com.company.onboarding.service.BasicAuthImprovedService;
import com.company.onboarding.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.component.checkbox.JmixCheckbox;
import io.jmix.flowui.component.textarea.JmixTextArea;
import io.jmix.flowui.component.textfield.TypedTextField;
import io.jmix.flowui.download.DownloadFormat;
import io.jmix.flowui.download.Downloader;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.StandardCharsets;

@Route(value = "Api1cView", layout = MainView.class)
@ViewController(id = "Api1cview")
@ViewDescriptor(path = "Api1cView.xml")
public class Api1cview extends StandardView {

    private final BasicAuthImprovedService basicAuthService;

    private final Notifications notifications;

    private final Downloader downloader;
    @Autowired
    private ReportGenerationService reportGenerationService;

    public Api1cview(BasicAuthImprovedService basicAuthService,
                     Notifications notifications,
                     Downloader downloader) {
        this.basicAuthService = basicAuthService;
        this.notifications = notifications;
        this.downloader = downloader;
    }

    @ViewComponent
    private JmixCheckbox cb_savemode;
    @ViewComponent
    private JmixTextArea resultTextArea;
    @ViewComponent
    private TypedTextField<Object> tf_inn;

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
}