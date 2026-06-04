package com.company.onboarding.app;

import com.company.onboarding.entity.Monitor;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.download.DownloadFormat;
import io.jmix.flowui.download.Downloader;
import net.sf.jasperreports.engine.*;
import org.springframework.stereotype.Component;

import io.jmix.core.DataManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class ReportGenerationService {


    private final Notifications notifications;
    @Autowired
    private DataManager dataManager;

    @Autowired
    private Downloader downloader;

    public ReportGenerationService(Notifications notifications) {
        this.notifications = notifications;
    }

    public void generateAndShowCustomerReport() {
        try {
            // 1. Загружаем данные через DataManager
            List<Monitor> monitors = dataManager.load(Monitor.class)
                    .query("select e from Monitor e")
                    .list();

            // 2. Создаем источник данных для JasperReports из списка загруженных сущностей
            //JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(customers);
            // 4. Главный dataSource может быть пустым (одна запись)
            JRDataSource mainDataSource = new JREmptyDataSource(1);

            // 3. Загружаем и компилируем JRXML шаблон
            InputStream reportStream = getClass().getResourceAsStream("/com/company/onboarding/report/onboarding_01.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            // 4. Параметры отчета (в нашем простом примере они не обязательны)
            Map<String, Object> parameters = new HashMap<>();
            //parameters.put(JRParameter.REPORT_LOCALE, new Locale("ru", "RU"));
            parameters.put("username", "Admin");
            parameters.put("reportDate", LocalDate.now());
            parameters.put("monitors", monitors);

            // 5. Заполняем отчет данными
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, mainDataSource);

            // 6. Экспортируем в PDF и показываем пользователю
            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

            // ⬇️ Выгружаем через Downloader
            downloader.download(
                    pdfBytes,                           // массив байтов
                    "customer_report.pdf",              // имя файла
                    DownloadFormat.PDF                  // формат
            );

        } catch (JRException e) {
            e.printStackTrace();
            // Обработка ошибки: показать уведомление пользователю
            notifications.create("Ошибка при создании отчета", e.toString())
                    .withType(Notifications.Type.ERROR)
                    .show();
        }
    }
}