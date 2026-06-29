package com.company.onboarding.report;

import com.company.onboarding.entity.User;
import com.company.onboarding.view.monitor.MonitorListView;
import io.jmix.core.DataManager;
import io.jmix.core.Sort;
import io.jmix.core.TimeSource;
import io.jmix.core.entity.KeyValueEntity;
import io.jmix.core.querycondition.PropertyCondition;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.reports.annotation.*;
import io.jmix.reports.entity.DataSetType;
import io.jmix.reports.entity.ParameterType;
import io.jmix.reports.entity.ReportOutputType;
import io.jmix.reports.yarg.loaders.ReportDataLoader;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ReportDef(
        code = "list-of-monitor",
        name = "Журнал контроля измерения давления",
        uuid = "761b689b-4e51-4a7e-b445-f480c99f6c68",
        group = Reports_group.class
)

@AvailableInViews(viewClasses = MonitorListView.class)
@TemplateDef(
        isDefault = true,                   // Этот шаблон будет использоваться по умолчанию
        code = "pdf-print-version",        // Уникальный код шаблона
        filePath = "com/company/onboarding/report/list-of-monitor.xlsx",
        outputType = ReportOutputType.PDF // Результат будет в PDF

)

@TemplateDef(
        isDefault = false,
        code = "excel-print-version",
        filePath = "com/company/onboarding/report/list-of-monitor.xlsx",
        outputType = ReportOutputType.XLSX,
        outputNamePattern = "list-of-monitor.xlsx"
)

@InputParameterDef(
        alias = "dateFrom",
        name = "Начало",
        type = ParameterType.DATE
)

@InputParameterDef(
        alias = "dateTo",
        name = "Окончание",
        type = ParameterType.DATE,
        defaultDateIsCurrent = true
)
// >>> begin example code
/*@InputParameterDef(
        alias = "username",
        name = "Username contains (leave empty to select all)",
        type = ParameterType.TEXT
)*/
@BandDef(
        name = "Root",
        root = true
)
@BandDef(
        name = "Header",
        parent = "Root",
        dataSets = @DataSetDef(name = "header", type = DataSetType.DELEGATE)
)
@BandDef(
        name = "Mon",
        parent = "Root",
        dataSets = @DataSetDef(name = "mon", type = DataSetType.DELEGATE)
)
@BandDef(
        name = "MonTrailer",
        parent = "Root",
        dataSets = @DataSetDef(name = "MonTrailer", type = DataSetType.DELEGATE)
)

/*@BandDef(
        name = "Monitors",
        parent = "Root",
        dataSets = @DataSetDef(
                name = "monitors",
                type = DataSetType.SQL,
                //where ra.username = ${Users.username}
                //order by dateTest desc, timeTest desc
                query = """
                        select u.DATE_TEST  as "dateTest", u.TIME_TEST  as "timeTest", u.UPPERPRES  as "upperpres", u.LOWPRES as "lowpres", u.PULSE  as "pulse"
                        from MONITOR u  
                        where u.DELETED_DATE  is NULL
                        """
        )
)*/
@BandDef(
        name = "HeaderUsers",
        parent = "Root"
)
@BandDef(
        name = "Users",
        parent = "Root",
        dataSets = @DataSetDef(
                name = "Users",
                type = DataSetType.DELEGATE
        )
)


// <<< end example code
public class ListOfMonitor {
    // Компьютеды
    private final ThreadLocal<BigDecimal> countRecords =
            ThreadLocal.withInitial(() -> BigDecimal.ZERO);
    private final ThreadLocal<BigDecimal> avgUpperPres =
            ThreadLocal.withInitial(() -> BigDecimal.ZERO);
    private final ThreadLocal<BigDecimal> avgLowPres =
            ThreadLocal.withInitial(() -> BigDecimal.ZERO);
    private final ThreadLocal<BigDecimal> avgPulse =
            ThreadLocal.withInitial(() -> BigDecimal.ZERO);



    private final DataManager dataManager;
    private final CurrentAuthentication currentAuthentication;
    private final TimeSource timeSource;

    public ListOfMonitor(DataManager dataManager, CurrentAuthentication currentAuthentication, TimeSource timeSource) {
        this.dataManager = dataManager;
        this.currentAuthentication = currentAuthentication;
        this.timeSource = timeSource;
    }
    @DataSetDelegate(name = "header")
    public ReportDataLoader headerDataLoader() {
        return (reportQuery, parentBand, params) ->
            List.of(
                    Map.of(
                            "dateFrom", ReportUtils.formatDateTime(params.get("dateFrom"), "dd.MM.yyyy"),
                            "dateTo", ReportUtils.formatDateTime(params.get("dateTo"), "dd.MM.yyyy"),
                            "user", ((User) currentAuthentication.getUser()).getDisplayName(),
                            "generatedAt", ReportUtils.formatDateTime(LocalDateTime.now(), "dd.MM.yyyy HH:mm:ss")
                    )
            );

    }

    // >>> begin example code
    @DataSetDelegate(name = "Users")
            public ReportDataLoader usersDataLoader() {
        return (reportQuery, parentBand, params) -> {
            List<User> users = dataManager.load(User.class)
                    .condition(PropertyCondition.isSet("firstName", true))
                    .sort(Sort.by("username"))
                    .list();
            return users.stream()
                    .map(user -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("username", user.getUsername());
                        map.put("firstName", user.getFirstName());
                        map.put("lastName", user.getLastName());
                        return map;
                    })
                    .toList();
        };
    }
    // <<< end example code
    @DataSetDelegate(name = "mon")
    public ReportDataLoader orderStatusDataLoader() {
        countRecords.set(BigDecimal.ZERO);
        avgUpperPres.set(BigDecimal.ZERO);
        avgLowPres.set(BigDecimal.ZERO);
        avgPulse.set(BigDecimal.ZERO);

        return (reportQuery, parentBand, params) -> {
            final User user = (User) currentAuthentication.getUser();

            List<KeyValueEntity> keyValueEntities = dataManager.loadValues("""
                        select u.dateTest, u.timeTest, u.upperpres, u.lowpres, u.pulse
                        from Monitor u
                        where u.user = :a_user and
                         (:dateTo is null or u.dateTest <= :dateTo) and
                         (:dateFrom is null or u.dateTest >= :dateFrom)
                         order by u.dateTest desc""")
                    .properties("dateTest", "timeTest", "upperpres", "lowpres", "pulse")
                    .parameter("dateFrom", params.get("dateFrom"))
                    .parameter("dateTo", params.get("dateTo"))
                    .parameter("a_user", user)
                    .list();
            return keyValueEntities.stream()
                    .map(kve -> {
                        countRecords.set(countRecords.get().add(BigDecimal.ONE));
                        avgUpperPres.set(avgUpperPres.get().add( new BigDecimal("" + kve.getValue("upperpres"))));
                        avgLowPres.set(avgLowPres.get().add(new BigDecimal("" + kve.getValue("lowpres"))));
                        avgPulse.set(avgPulse.get().add(new BigDecimal("" + kve.getValue("pulse"))));
                        //
                        Map<String, Object> map = new HashMap<>();
                        map.put("dateTest", kve.getValue("dateTest"));
                        map.put("timeTest", kve.getValue("timeTest"));
                        map.put("upperpres", kve.getValue("upperpres"));
                        map.put("lowpres", kve.getValue("lowpres"));
                        map.put("pulse", kve.getValue("pulse"));
                        map.put("rowNum", countRecords.get());
                        return map;
                    })
                    .toList();
        };
    }

    @DataSetDelegate(name = "MonTrailer")
    public ReportDataLoader clientTotalDataLoader() {
        return (reportQuery, parentBand, params) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("rowCount", countRecords.get()); // <5>
            /*runningGrandTotal.set(
                    runningGrandTotal.get().add(runningClientTotal.get())
            ); // <6>*/
            map.put("avgUpperPres", avgUpperPres.get());
            if (countRecords.get().compareTo(BigDecimal.ZERO) != 0){
                avgUpperPres.set(avgUpperPres.get().divide(new BigDecimal(String.valueOf(countRecords.get())), 0,BigDecimal.ROUND_UP));
                map.put("avgUpperPres", avgUpperPres.get());

                avgLowPres.set(avgLowPres.get().divide(new BigDecimal(String.valueOf(countRecords.get())), 0,BigDecimal.ROUND_UP));
                map.put("avgLowPres", avgLowPres.get());

                avgPulse.set(avgPulse.get().divide(new BigDecimal(String.valueOf(countRecords.get())), 0,BigDecimal.ROUND_UP));
                map.put("avgPulse", avgPulse.get());
            }


            countRecords.set(BigDecimal.ZERO);

            return List.of(map);
        };
    }

    public static class ReportUtils {
        public static Object formatDateTime(Object value, String pattern) {
            if (value == null) {
                return "";
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

            if (value instanceof LocalDateTime ldt) {
                //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                String str = ldt.format(formatter);
                return str;
            }else if (value instanceof LocalDate ld) {
                String str = ld.format(formatter);
                return str;
            }else if (value instanceof Date d) {
                return d.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
                        .format(formatter);
            }else {
                // на ваше усмотрение – либо выбросить ошибку, либо вернуть toString()
                return value.toString();
            }
        }
    }
}