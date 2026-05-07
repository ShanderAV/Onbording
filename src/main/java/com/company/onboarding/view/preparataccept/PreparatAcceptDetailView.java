package com.company.onboarding.view.preparataccept;

import com.company.onboarding.entity.PreparatAccept;
import com.company.onboarding.entity.User;
import com.company.onboarding.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.core.NoResultException;
import io.jmix.core.TimeSource;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;

@Route(value = "preparat-accepts/:id", layout = MainView.class)
@ViewController(id = "PreparatAccept.detail")
@ViewDescriptor(path = "preparat-accept-detail-view.xml")
@EditedEntityContainer("preparatAcceptDc")
public class PreparatAcceptDetailView extends StandardDetailView<PreparatAccept> {

    private final TimeSource timeSource;
    @Autowired
    private CurrentAuthentication currentAuthentication;
    @Autowired
    private Notifications notifications;
    @Autowired
    private DataManager dataManager;

    public PreparatAcceptDetailView(TimeSource timeSource) {
        this.timeSource = timeSource;
    }

    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {
        //                               notifications.show("event onBeforeShow");

    }

    @Subscribe
    public void onInitEntity(final InitEntityEvent<PreparatAccept> event) {

        final ZonedDateTime zonedDateTime = timeSource.now();
        LocalDateTime acceptedDate = LocalDateTime.now();

        event.getEntity().setAcceptedDate(acceptedDate);
        final User user = (User) currentAuthentication.getUser();
        event.getEntity().setUser(user);

        try {
            final PreparatAccept LastRecord = dataManager.load(PreparatAccept.class)
                    .query("select p from PreparatAccept p where p.user = :user1 order by p.acceptedDate desc")
                    .parameter("user1", user)
                    .one();
            event.getEntity().setPreparat(LastRecord.getPreparat());
        } catch (NoResultException e) {
            // если нет нашлось записей не страшно
            //throw new RuntimeException(e);
        }


    }


}
