package com.company.onboarding.app;

import com.company.onboarding.entity.Monitor;
import com.company.onboarding.entity.User;
import com.lowagie.text.pdf.BaseFont;
import io.jmix.core.DataManager;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.reports.entity.Report;
import io.jmix.reports.entity.ReportOutputType;
import io.jmix.reports.entity.ReportTemplate;
import io.jmix.reports.runner.ReportRunner;
import io.jmix.reports.yarg.reporting.ReportOutputDocument;
import io.jmix.reportsflowui.runner.FluentUiReportRunner;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Service
public class ReportGenHtml {

    @Autowired
    private DataManager dataManager;

    @Autowired
    private ReportRunner reportRunner;

    @Autowired
    private CurrentAuthentication currentAuthentication;

    public byte[] generateReport(LocalDate startDate, LocalDate endDate, User user) throws Exception
    {
        try {
            // 1. Загружаем из БД
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            final DateTimeFormatter RUSSIAN_FORMATTER = DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("ru"));


            List<Monitor> monitors = dataManager.load(Monitor.class)
                    .query("select o from Monitor o where o.dateTest between :startDate and :endDate")
                    .parameter("startDate", startDate)
                    .parameter("endDate", endDate)
                    .list();
            String managerName = user.getUsername();


            // 2. Формируем строки таблицы напрямую в HTML
            StringBuilder tableRows = new StringBuilder();
            BigDecimal totalAmount = BigDecimal.ZERO;
            int counter = 1;
            for (Monitor mon_val : monitors) {
                BigDecimal itemTotal = BigDecimal.valueOf(mon_val.getPulse());
                totalAmount = totalAmount.add(itemTotal);
                // Сразу формируем HTML строку
                tableRows.append("""
                        <tr>
                            <td>%d</td>
                            <td class="text-right">%s</td>
                            <td class="text-right">%d</td>
                            <td class="text-right">%d</td>
                            <td class="text-right">%d</td>
                        </tr>
                        """.formatted(
                        counter++,
                        mon_val.getDateTest().format(formatter),
                        mon_val.getUpperpres(),
                        mon_val.getLowpres(),
                        mon_val.getPulse()
                ));

            }
            // 3. Загружаем HTML шаблон
            String htmlTemplate = loadHtmlTemplate();
            // 4. Заменяем все плейсхолдеры на реальные данные
            String finalHtml = htmlTemplate
                    .replace("${reportTitle}", "Отчёт  за " + formatter.format(startDate) + " - " + formatter.format(endDate))
                    .replace("${startDate}", formatter.format(startDate))
                    .replace("${endDate}", formatter.format(endDate))
                    .replace("${managerName}", managerName != null ? managerName : "Не указан")
                    .replace("${totalRecords}", String.valueOf(monitors.size()))
                    .replace("${tableRows}", tableRows.toString())
                    .replace("${totalAmount}", formatNumber(totalAmount))
                    .replace("${currentDate}", LocalDate.now().format(formatter));
            // 5. Конвертируем HTML в PDF
            return convertHtmlToPdf(finalHtml);

        } catch (Exception e) {
            throw new RuntimeException(e);

        }

    }
    private byte[] convertHtmlToPdf(String html) throws Exception {
        ITextRenderer renderer = new ITextRenderer();

        // Подключаем шрифты для кириллицы
        String fontPath = System.getProperty("os.name").toLowerCase().contains("win")
                ? "C:/Windows/Fonts/arial.ttf"
                : "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf";

        renderer.getFontResolver().addFont(fontPath,
                BaseFont.IDENTITY_H,
                BaseFont.EMBEDDED);

        renderer.setDocumentFromString(html);
        renderer.layout();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        renderer.createPDF(baos);

        return baos.toByteArray();
    }
    private String loadHtmlTemplate() throws IOException {
        ClassPathResource resource = new ClassPathResource(
                "io/jmix/reports/templates/template.html"
        );
        return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }
    private String formatNumber(BigDecimal number) {
        if (number == null) return "0";
        return String.format("%,.2f", number).replace(",", " ");
    }
}