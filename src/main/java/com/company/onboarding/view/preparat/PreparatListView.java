package com.company.onboarding.view.preparat;

import com.company.onboarding.entity.Preparat;
import com.company.onboarding.entity.User;
import com.company.onboarding.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;


@Route(value = "preparats", layout = MainView.class)
@ViewController(id = "Preparat.list")
@ViewDescriptor(path = "preparat-list-view.xml")
@LookupComponent("preparatsDataGrid")
@DialogMode(width = "64em")
public class PreparatListView extends StandardListView<Preparat> {

}