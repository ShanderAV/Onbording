package com.company.onboarding.quartz;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

// Импортируйте любые ваши сервисы, например:
// import com.yourcompany.yourproject.service.MyHeavyService;
//@DisallowConcurrentExecution // Запрещает параллельное выполнение этого Job
public class MyFirstJob implements Job {
      // Запрещает параллельное выполнение этого Job
    private static final Logger log = LoggerFactory.getLogger(MyFirstJob.class);

    // Вы можете внедрять любые Spring-бины, как в обычном сервисе [citation:2][citation:4]
    // @Autowired
    // private MyHeavyService myHeavyService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // Quartz будет ждать, пока предыдущий запуск завершится
        // --- Здесь, будет ваша продолжительная задача ---
        log.info("===> Моя первая Quartz задача успешно запущена!!! <===");
        for (int i=0;i<=10;i++){
            try {
                Thread.sleep(2000);
                log.info("Шаг {} из 10", i);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }

        // Представьте, что здесь происходит долгий процесс, например, вызов API, обработка файлов и т.д.
        // myHeavyService.doHeavyWork();

        // Если во время работы что-то пошло не так, можно выбросить исключение
        // throw new JobExecutionException("Критическая ошибка во время выполнения задачи!");
    }
}