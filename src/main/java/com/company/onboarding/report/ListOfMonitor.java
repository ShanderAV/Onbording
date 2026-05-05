package com.company.onboarding.report;

import com.company.onboarding.entity.Monitor;
import com.company.onboarding.entity.User;
import com.company.onboarding.view.monitor.MonitorListView;
import io.jmix.core.DataManager;
import io.jmix.core.Sort;
import io.jmix.core.entity.KeyValueEntity;
import io.jmix.core.querycondition.PropertyCondition;
import io.jmix.reports.annotation.*;
import io.jmix.reports.entity.DataSetType;
import io.jmix.reports.entity.ParameterType;
import io.jmix.reports.entity.ReportOutputType;
import io.jmix.reports.yarg.loaders.ReportDataLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ReportDef(
        code = "list-of-monitor",
        name = "List Of Monitor",
        uuid = "761b689b-4e51-4a7e-b445-f480c99f6c68"
)
@AvailableInViews(viewClasses = MonitorListView.class)
@TemplateDef(
        isDefault = true,
        code = "DEFAULT",
        filePath = "com/company/onboarding/report/list-of-monitor.xlsx",
        outputType = ReportOutputType.XLSX,
        outputNamePattern = "list-of-monitor.xlsx"
)

@InputParameterDef(
        alias = "dateFrom",
        name = "From",
        type = ParameterType.DATE
)

@InputParameterDef(
        alias = "dateTo",
        name = "To",
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
        parent = "Root"
)
@BandDef(
        name = "Mon",
        parent = "Root",
        dataSets = @DataSetDef(name = "mon", type = DataSetType.DELEGATE)
)

@BandDef(
        name = "Monitors",
        parent = "Root",
        dataSets = @DataSetDef(
                name = "monitors",
                type = DataSetType.SQL,
                //where ra.username = ${Users.username}
                //order by dateTest desc, timeTest desc
                query = """
                        select u.DATE_TEST  as "dateTest", u.TIME_TEST  as "timeTest", u.UPPERPRES  as "upperpres", u.LOWPRES as "lowres", u.PULSE  as "pulse"
                        from MONITOR u
                        where u.DELETED_DATE  is NULL
                        """
        )
)
@BandDef(
        name = "Users",
        parent = "Root",
        dataSets = @DataSetDef(
                name = "users",
                type = DataSetType.DELEGATE
        )
)


// <<< end example code
public class ListOfMonitor {
    private final DataManager dataManager;

    public ListOfMonitor(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    // >>> begin example code
    @DataSetDelegate(name = "users")
            public ReportDataLoader usersDataLoader() {
        return (reportQuery, parentBand, params) -> {
            List<User> users = dataManager.load(User.class)
                    .condition(PropertyCondition.contains("username", params.get("username")).skipNullOrEmpty())
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
        return (reportQuery, parentBand, params) -> {
            //select u.dateTest, u.timeTest, u.upperpres, u.lowpres, u.pulse
            //"dateTest", "timeTest",
            List<KeyValueEntity> keyValueEntities = dataManager.loadValues("""
                        select u.dateTest, u.timeTest, u.upperpres, u.lowpres, u.pulse
                        from Monitor u
                        where (:dateTo is null or u.dateTest <= :dateTo) and 
                              (:dateFrom is null or u.dateTest >= :dateFrom) 
                        order by u.dateTest desc""")
                    .properties("dateTest", "timeTest", "upperpres", "lowres", "pulse")
                    .parameter("dateFrom", params.get("dateFrom"))
                    .parameter("dateTo", params.get("dateTo"))
                    .list();
            return keyValueEntities.stream()
                    .map(kve -> {
                        Map<String, Object> map = new HashMap<>();
                        //map.put("dateTest", kve.getValue("dateTest"));
                        //map.put("timeTest", kve.getValue("timeTest"));
                        map.put("upperpres", kve.getValue("upperpres"));
                        map.put("lowres", kve.getValue("lowres"));
                        map.put("pulse", kve.getValue("pulse"));
                        return map;
                    })
                    .toList();
        };
    }
}