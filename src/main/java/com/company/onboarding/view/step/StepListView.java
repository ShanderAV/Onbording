package com.company.onboarding.view.step;

import com.company.onboarding.entity.Step;
import com.company.onboarding.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.backgroundtask.BackgroundTask;
import io.jmix.flowui.backgroundtask.BackgroundTaskHandler;
import io.jmix.flowui.backgroundtask.BackgroundWorker;
import io.jmix.flowui.backgroundtask.TaskLifeCycle;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.view.*;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.TimeUnit;


@Route(value = "steps", layout = MainView.class)
@ViewController(id = "Step.list")
@ViewDescriptor(path = "step-list-view.xml")
@LookupComponent("stepsDataGrid")
@DialogMode(width = "64em")
public class StepListView extends StandardListView<Step> {

    protected static final int ITERATION = 20;
    @ViewComponent
    private Span labelSpan;

    @ViewComponent
    private ProgressBar progressB;
    @ViewComponent
    private Span percentSpan;
    @ViewComponent
    private JmixButton controlButton;
    @Autowired
    private BackgroundWorker backgroundWorker;

    protected BackgroundTaskHandler<Void> taskHandler;

    @Subscribe(id = "controlButton", subject = "clickListener")
    public void onControlButtonClick(final ClickEvent<JmixButton> event) {
       if (taskHandler == null || taskHandler.isDone() || taskHandler.isCancelled()){
           runNewTask();
           event.getSource().setIcon(VaadinIcon.STOP.create());
       } else if( taskHandler != null || taskHandler.isAlive()){
           taskHandler.cancel();
           labelSpan.setText("Остановлено");
           event.getSource().setIcon(VaadinIcon.REFRESH.create());
       }

    }

    private void runNewTask() {
        taskHandler = backgroundWorker.handle(createBackgroundTask());
        taskHandler.execute();
    }


    protected BackgroundTask<Integer, Void> createBackgroundTask() {
      return new BackgroundTask<>(100, TimeUnit.SECONDS) {
          @Override
          public Void run(@NonNull TaskLifeCycle<Integer> taskLifeCycle) throws Exception {
              for (int i = 1; i <= ITERATION; i++) {
                  TimeUnit.SECONDS.sleep(1);
                  taskLifeCycle.publish(i);
              }
              return null;
          }

          @Override
          public void progress(@NonNull List<Integer> changes) {
              double lastVal = changes.getLast();
              double value = lastVal / ITERATION;

              if (value < 1){
                 labelSpan.setText("Выполнение...");
                  controlButton.setText("Стоп");
              } else {
                labelSpan.setText("Готово.");
                controlButton.setIcon(VaadinIcon.PLAY_CIRCLE.create());
                  controlButton.setText("Старт");
              }
              progressB.setValue(value);
              percentSpan.setText(Double.valueOf(value*100).intValue() + "%");

          }
      };
    }
}