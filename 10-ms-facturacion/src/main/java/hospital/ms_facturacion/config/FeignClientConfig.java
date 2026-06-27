package hospital.ms_facturacion.config;

import feign.Logger;
import feign.Request;
import org.springframework.context.annotation.Bean;
import java.util.concurrent.TimeUnit;

public class FeignClientConfig {

    @Bean
    public Request.Options options() {
        // Control de Timeouts (5s conexión, 10s lectura)
        return new Request.Options(
            5, TimeUnit.SECONDS, 
            10, TimeUnit.SECONDS, 
            true
        );
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        // Mejora la trazabilidad de errores remotos durante la fase de depuración.
        return Logger.Level.FULL;
    }
}