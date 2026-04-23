package com.company.onboarding.view.datagridlargedataset;


import com.company.onboarding.entity.User;
import com.company.onboarding.view.main.MainView;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.grid.FooterRow;
import com.vaadin.flow.router.Route;
import io.jmix.core.LoadContext;
import io.jmix.core.Metadata;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Route(value = "data-grid-large-dataset", layout = MainView.class)
@ViewController(id = "DataGridLargeDataset")
@ViewDescriptor(path = "data-grid-large-dataset.xml")
public class DataGridLargeDataset extends StandardView {
  protected static   final int COUNT = 200000;
    @ViewComponent
    private CollectionContainer<User> usersDc;
    @Autowired
    private Metadata metadata;
    protected Collection<User> items;
  @ViewComponent
  private DataGrid<User> usersDataGrid;

  @Subscribe
    public void onInit(final InitEvent event) {
       /* items = new ArrayList<>();
        for (int i = 0; i < COUNT; i++){
            User user = metadata.create(User.class);
            user.setUsername("userName" + i);
            user.setFirstName("First Name" + i);
            user.setLastName("LastName " + i);
            items.add(user);
        }*/
    }

  @Subscribe
  public void onBeforeShow(final BeforeShowEvent event) {
      initFooter();

  }

  private void initFooter() {
    FooterRow footerRow = usersDataGrid.appendFooterRow();
    FooterRow.FooterCell usernameCell = footerRow.getCell(usersDataGrid.getColumnByKey("username"));
    //Html html = new Html("<strong>Итого</strong>");
    usernameCell.setText("Итого: ");
    footerRow.getCell(usersDataGrid.getColumnByKey("version")).setText(getSumm());
  }

  private String getSumm() {
    long result_sum = 0;
    Collection<User> items = usersDataGrid.getGenericDataView().getItems().toList();
    for (User user : items){
      result_sum += user.getVersion();
    }
    return "" + result_sum;
  }

    /*@Install(to = "usersDl", target = Target.DATA_LOADER)
    private Collection<User> usersDlLoadDelegate(final LoadContext<User> loadContext) {

        return items;
    }*/
    
}