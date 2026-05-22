package com.company.onboarding.view.user;

import com.company.onboarding.entity.User;
import com.company.onboarding.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import io.jmix.core.FileRef;
import io.jmix.core.FileStorage;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.flowui.UiComponents;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.download.Downloader;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.view.*;
import io.jmix.reports.entity.ReportOutputType;
import io.jmix.reportsflowui.runner.ParametersDialogShowMode;
import io.jmix.reportsflowui.runner.UiReportRunner;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "users", layout = MainView.class)
@ViewController(id = "User.list")
@ViewDescriptor(path = "user-list-view.xml")
@LookupComponent("usersDataGrid")
@DialogMode(width = "64em")
public class UserListView extends StandardListView<User> {
    @ViewComponent
    private DataGrid<User> usersDataGrid;
    @Autowired
    private FileStorage fileStorage;
    @Autowired
    private UiComponents uiComponents;
    @Autowired
    private Downloader downloader;

    @Autowired
    private CurrentAuthentication currentAuthentication;

    @Autowired
    private UiReportRunner uiReportRunner;

    @Subscribe(id = "print", subject = "clickListener")
    public void onPrintClick(final ClickEvent<JmixButton> event) {
        //final User user = (User) currentAuthentication.getUser();
        uiReportRunner.byReportCode("user-preparat")
                .withParametersDialogShowMode(ParametersDialogShowMode.YES)
                .withOutputType(ReportOutputType.DOCX)
                .withOutputNamePattern("user-preparat_report.docx")
                .runAndShow();
    }

    /*@Supply(to = "usersDataGrid.picture", subject = "renderer")
    private Renderer<User> usersDataGridPictureRenderer() {
        return new ComponentRenderer<>(user -> {
            FileRef fileRef = user.getPicture();
            if (fileRef == null) {
                return null;
            }

            Image image = uiComponents.create(Image.class);
            image.setWidth("30px");
            image.setHeight("30px");
            //fileRef.getFileName();
            StreamResource streamResource = new StreamResource(
                    fileRef.getFileName(),
                    () -> fileStorage.openStream(fileRef)
            );
            image.setSrc(streamResource);
            image.setClassName("user-picture");
             //StreamResource streamResource = new StreamResource(fileRef.getFileName(), () -> fileStorage.openStream(fileRef));
            return image;
        });

    }*/
}