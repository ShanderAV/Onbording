package com.company.onboarding.report;

import com.company.onboarding.entity.User;
import com.company.onboarding.view.monitor.MonitorListView;
import com.company.onboarding.view.user.UserListView;
import io.jmix.core.DataManager;
import io.jmix.core.MetadataTools;
import io.jmix.core.Sort;
import io.jmix.core.querycondition.PropertyCondition;
import io.jmix.reports.annotation.*;
import io.jmix.reports.entity.DataSetType;
import io.jmix.reports.entity.ParameterType;
import io.jmix.reports.entity.ReportOutputType;
import io.jmix.reports.yarg.loaders.ReportDataLoader;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ReportDef(
        code = "user-preparat",
        group = Reports_group.class,
        name = "Карта пациента",
        description = "",
        uuid = "763a1a7c-5053-46d7-98fd-63d8b1921224"
)
@AvailableInViews(
        viewClasses = {MonitorListView.class, UserListView.class}
)

@TemplateDef(
        isDefault = true,
        code = "DOCX → DOCX",
        filePath = "com/company/onboarding/report/user-preparat-report.docx",
        outputType = ReportOutputType.DOCX,
        outputNamePattern = "user-preparat.docx"
)

@InputParameterDef(
        alias = "user", // <1>
        name = "Пользователь", // <2>
        type = ParameterType.ENTITY, // <3>
        required = true, // <4>
        entity = @EntityParameterDef(entityClass = User.class) // <5>
)

@BandDef(
        name = "Root",
        root = true,
        dataSets = @DataSetDef( // <2>
                name = "root", // <3>
                type = DataSetType.DELEGATE // <4>
        )

)
@BandDef(
        name = "Header",
        parent = "Root"
)
@BandDef(
        name = "User",
        parent = "Root",
        dataSets = @DataSetDef(
                name = "User",
                type = DataSetType.DELEGATE
        )
)
@BandDef(
        name = "Roles",
        parent = "User",
        dataSets = @DataSetDef(
                name = "roles",
                type = DataSetType.SQL,
                query = """
                        select ra.role_code as "role", ra.role_type as "type"
                         from sec_role_assignment ra
                         where ra.username = ${User.username}"""
        )
)
// <<< end example code
public class UserPreparat {
    private final DataManager dataManager;

    private final MetadataTools metadataTools;

    public UserPreparat(DataManager dataManager,
                        MetadataTools metadataTools) {
        this.dataManager = dataManager;
        this.metadataTools = metadataTools;
    }

    @DataSetDelegate(name = "root") // <1>
    public ReportDataLoader rootDataLoader() { // <2>
        return (reportQuery, parentBand, params) -> {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy' 'HH:mm:ss");
            String formattedDate = now.format(formatter);
            return List.of(Map.of("generatedAt", formattedDate));
        };
    }
    // >>> begin example code
    @DataSetDelegate(name = "User")
            public ReportDataLoader usersDataLoader() {
        return (reportQuery, parentBand, params) -> {
            User user = (User) params.get("user");

            Map<String, Object> fields = new HashMap<>();
            fields.put("firstname", user.getFirstName());
            fields.put("lastname", user.getLastName());
            //fields.put("accountManager", client.getAccountManager() == null ?
             //       "" : metadataTools.getInstanceName(client.getAccountManager()));
            return List.of(fields);
        };

    }
    // <<< end example code
}