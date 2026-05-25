package com.company.onboarding.messaging;
import com.company.onboarding.dto.JobMessage;
import io.jmix.core.DataManager;
import io.jmix.core.security.SystemAuthenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JobConsumer {

    private static final Logger log = LoggerFactory.getLogger(JobConsumer.class);

    @Autowired
    private DataManager dataManager;

    @Autowired
    private SystemAuthenticator systemAuthenticator;

    @RabbitListener(queues = "test_queue")
    public void processJob(JobMessage message) {
        // Запускаем с системной аутентификацией для доступа к БД
        systemAuthenticator.runWithSystem(() -> {
            log.info("1. слушатель. Получена задача: {}, параметр: {}", message.getJobType(), message.getParameter());

            try {
                // Выполняем длительные вычисления
                performHeavyComputation(message);

                // Сохраняем результат (опционально)
                log.info("Задача {} успешно выполнена", message.getJobId());

            } catch (Exception e) {
                log.error("Ошибка при выполнении задачи {}", message.getJobId(), e);
                // Здесь можно отправить сообщение в очередь ошибок (DLQ)
            }
        });
    }

    @RabbitListener(queues = "test_queue")
    public void processJob1(JobMessage message) {
        // Запускаем с системной аутентификацией для доступа к БД
        systemAuthenticator.runWithSystem(() -> {
            log.info("2. слушатель. Получена задача: {}, параметр: {}", message.getJobType(), message.getParameter());

            try {
                // Выполняем длительные вычисления
                performHeavyComputation(message);

                // Сохраняем результат (опционально)
                log.info("Задача {} успешно выполнена", message.getJobId());

            } catch (Exception e) {
                log.error("Ошибка при выполнении задачи {}", message.getJobId(), e);
                // Здесь можно отправить сообщение в очередь ошибок (DLQ)
            }
        });
    }
    private void performHeavyComputation(JobMessage message) {
        // Симуляция долгой работы
        try {
            Thread.sleep(10000); // 10 секунд
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Ваша бизнес-логика здесь
        // Например, работа с DataManager, вызов внешних API и т.д.
    }
}