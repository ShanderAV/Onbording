package com.company.onboarding.view.step;

import com.company.onboarding.dto.JobMessage;
import com.company.onboarding.entity.Step;
import com.company.onboarding.view.main.MainView;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.backgroundtask.BackgroundTask;
import io.jmix.flowui.backgroundtask.BackgroundTaskHandler;
import io.jmix.flowui.backgroundtask.BackgroundWorker;
import io.jmix.flowui.backgroundtask.TaskLifeCycle;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.view.*;
import io.jmix.quartz.service.QuartzService;
import org.jspecify.annotations.NonNull;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.company.onboarding.view.login.LoginView.log;


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

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private Notifications notifications;

    @Autowired
    private QuartzService quartzService;

    private RestTemplate restTemplate;

    @Subscribe(id = "controlButton", subject = "clickListener")
    public void onControlButtonClick(final ClickEvent<JmixButton> event) {
       if (taskHandler == null || taskHandler.isDone() || taskHandler.isCancelled()){
           runNewTask();
           event.getSource().setIcon(VaadinIcon.STOP.create());
       } else if( taskHandler != null || taskHandler.isAlive()){
           taskHandler.cancel();
           labelSpan.setText("Остановлено");
           controlButton.setIcon(VaadinIcon.PLAY.create());
           controlButton.setThemeName("primary");
           controlButton.setText("Старт");
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
                  controlButton.setIcon(VaadinIcon.PAUSE.create());
                  controlButton.setThemeName("primary error");
                  controlButton.setText("Стоп");
              } else {
                labelSpan.setText("Готово.");
                controlButton.setIcon(VaadinIcon.PLAY.create());
                controlButton.setThemeName("primary");
                controlButton.setText("Старт");
              }
              progressB.setValue(value);
              percentSpan.setText(Double.valueOf(value*100).intValue() + "%");

          }
      };
    }


    @Subscribe(id = "executeRabbitMQ", subject = "clickListener")
    public void onExecuteRabbitMQClick(final ClickEvent<JmixButton> event) {
        // Создаём сообщение с параметрами задачи
        JobMessage message = new JobMessage("HEAVY_COMPUTATION", "some-parameter");
        // Отправляем в очередь — метод НЕ БЛОКИРУЕТ UI
        //private static final String QUEUE_NAME = "test_queue";
        String QUEUE_NAME = "test_queue";
        rabbitTemplate.convertAndSend(QUEUE_NAME, message);

        notifications.create("Задача отправлена в очередь").show();

    }

    @Subscribe(id = "executeProcess", subject = "clickListener")
    public void onExecuteProcessClick(final ClickEvent<JmixButton> event) {
        String jobName = "MyFirstJob";  // Имя вашей задачи из UI Quartz
        String jobGroup = "DEFAULT";

        try {
            if (quartzService.checkJobExists(jobName, jobGroup)) {
                quartzService.executeNow(jobName, jobGroup);
                log.info("Задача {} успешно запущена...", jobName);
            } else {
                log.warn("Задача {} не найдена", jobName);
            }
        } catch (Exception e) {
            log.error("Ошибка при запуске задачи", e);
        }
        notifications.show("Задача стартовала...");
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Subscribe(id = "PostLK", subject = "clickListener")
    public void onPostLKClick(final ClickEvent<JmixButton> event) {

        // Данные для отправки (обычная строка JSON)
        String jsonBody ="{\"organ_guid_in_gis\": \"985781c0-2159-4831-b48b-333460f77e88\"," +
                " \"fias\": \"9007cc2d-b40b-4597-9451-35c0209a28e3\"}";
        restTemplate = new RestTemplate();
        //
        try {
            JsonNode result = postJson("http://192.168.1.41:8019/dataexporting/export-gishouse-data", jsonBody);
            //System.out.println("Ответ: " + result.toPrettyString());
            notifications.show("Ответ получен.");
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON", e);
        }

    }
    public JsonNode postJson(String url, String jsonBody) {
        // 1. Создаём заголовки
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 2. Оборачиваем JSON тело в HttpEntity с заголовками
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);

        // 3. Отправляем запрос через exchange (более гибкий метод)
        ResponseEntity<JsonNode> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                JsonNode.class
        );

        // 4. Проверяем статус ответа
        if (response.getStatusCode() == HttpStatus.CREATED ||
                response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new RuntimeException("Request failed with status: " + response.getStatusCode());
        }
    }
}