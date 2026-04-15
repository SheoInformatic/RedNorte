package cl.rednorte.reasignacion.config;

import cl.rednorte.reasignacion.service.ReasignacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulerConfig {

    private final ReasignacionService reasignacionService;

    /** Escaneo proactivo diario a las 02:00 AM */
    @Scheduled(cron = "0 0 2 * * *")
    public void proactiveScanJob() {
        log.info("Job programado: iniciando escaneo proactivo de reasignación");
        reasignacionService.runProactiveScan();
    }
}
