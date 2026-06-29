package com.company.onboarding.report;

import com.company.onboarding.entity.Step;
import com.company.onboarding.entity.User;
import io.jmix.core.DataManager;
import io.jmix.core.Sort;
import io.jmix.core.querycondition.PropertyCondition;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.reports.annotation.*;
import io.jmix.reports.entity.DataSetType;
import io.jmix.reports.entity.ParameterType;
import io.jmix.reports.entity.ReportOutputType;
import io.jmix.reports.yarg.loaders.ReportDataLoader;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ReportDef(
        code = "report-for-step",
        group = Reports_group.class,
        name = "Список шагов",
        uuid = "15278a0b-dd18-4213-b1d7-cee5e816638f"
)
@TemplateDef(
        isDefault = true,
        code = "xlsx-report-for-step",
        filePath = "com/company/onboarding/report/report-for-step.xlsx",
        outputType = ReportOutputType.XLSX,
        outputNamePattern = "report-for-step.xlsx"
)
@TemplateDef(
        isDefault = false,
        code = "pdf-report-for-step",
        filePath = "com/company/onboarding/report/report-for-step.xlsx",
        outputType = ReportOutputType.PDF,
        outputNamePattern = "report-for-step.pdf"
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
        dataSets = @DataSetDef(
        name = "Header",
        type = DataSetType.DELEGATE
)
)
@BandDef(
        name = "Step",
        parent = "Root",
        dataSets = @DataSetDef(
                name = "Step",
                type = DataSetType.DELEGATE
        )
)
// <<< end example code
public class Report_for_step {
    private final DataManager dataManager;

    private final CurrentAuthentication currentAuthentication;

    public Report_for_step(DataManager dataManager,
                           CurrentAuthentication currentAuthentication) {
        this.dataManager = dataManager;
        this.currentAuthentication = currentAuthentication;
    }

    // >>> begin example code
    @DataSetDelegate(name = "Header")
    public ReportDataLoader headerDataLoader() {
        return (reportQuery, parentBand, params) ->
                List.of(
                        Map.of(
                                //"dateFrom", ListOfMonitor.ReportUtils.formatDateTime(params.get("dateFrom"), "dd.MM.yyyy"),
                                //"dateTo", ListOfMonitor.ReportUtils.formatDateTime(params.get("dateTo"), "dd.MM.yyyy"),
                                "username", ((User) currentAuthentication.getUser()).getDisplayName(),
                                "generatedAt", ListOfMonitor.ReportUtils.formatDateTime(LocalDateTime.now(), "dd.MM.yyyy HH:mm:ss")
                        )
                );
    }

    @DataSetDelegate(name = "Step")
            public ReportDataLoader usersDataLoader() {
        return (reportQuery, parentBand, params) -> {
            List<Step> steps = dataManager.load(Step.class)
                    .condition(PropertyCondition.notEqual("name", params.get("username")).skipNullOrEmpty())
                    .sort(Sort.by("id"))
                    .list();

            return steps.stream()
                    .map(step -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("name", step.getName());
                        map.put("sortValue", step.getSortValue());
                        map.put("duration", step.getDuration());
                        return map;
                    })
                    .toList();
        };
    }
    // <<< end example code
}