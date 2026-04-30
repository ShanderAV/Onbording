package com.company.onboarding.view.preparataccept;

import com.company.onboarding.entity.PreparatAccept;
import com.company.onboarding.entity.User;
import com.company.onboarding.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;


@Route(value = "preparat-accepts", layout = MainView.class)
@ViewController(id = "PreparatAccept.list")
@ViewDescriptor(path = "preparat-accept-list-view.xml")
@LookupComponent("preparatAcceptsDataGrid")
@DialogMode(width = "64em")
public class PreparatAcceptListView extends StandardListView<PreparatAccept> {
    @Autowired
    private CurrentAuthentication currentAuthentication;
    @ViewComponent
    private CollectionLoader<PreparatAccept> preparatAcceptsDl;

    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {
        User user = (User) currentAuthentication.getUser();
        preparatAcceptsDl.setParameter("user1", user);
        preparatAcceptsDl.load();

    }
}