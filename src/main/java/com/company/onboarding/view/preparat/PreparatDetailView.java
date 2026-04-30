package com.company.onboarding.view.preparat;

import com.company.onboarding.entity.Preparat;
import com.company.onboarding.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "preparats/:id", layout = MainView.class)
@ViewController(id = "Preparat.detail")
@ViewDescriptor(path = "preparat-detail-view.xml")
@EditedEntityContainer("preparatDc")
public class PreparatDetailView extends StandardDetailView<Preparat> {
}