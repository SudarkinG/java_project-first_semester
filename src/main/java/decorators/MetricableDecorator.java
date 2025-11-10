package decorators;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;


public class MetricableDecorator extends DataServiceDecorator {
    private MetricService metricService = new MetricService();

    public MetricableDecorator(DataService wrappedService) {
        super(wrappedService);
    }

    @Override
    public Optional<String> findDataByKey(String key) {
        Instant start = Instant.now();
        Optional<String> result = wrappedService.findDataByKey(key);
        Instant end = Instant.now();
        
        Duration duration = Duration.between(start, end);
        metricService.sendMetric(duration);
        
        return result;
    }

    @Override
    public void saveData(String key, String data) {
        Instant start = Instant.now();
        wrappedService.saveData(key, data);
        Instant end = Instant.now();
        
        Duration duration = Duration.between(start, end);
        metricService.sendMetric(duration);
    }

    @Override
    public boolean deleteData(String key) {
        Instant start = Instant.now();
        boolean result = wrappedService.deleteData(key);
        Instant end = Instant.now();
        
        Duration duration = Duration.between(start, end);
        metricService.sendMetric(duration);
        
        return result;
    }

    public static class MetricService {
        public void sendMetric(Duration duration) {
            System.out.println("Метод выполнялся: " + duration.toString());
        }
    }
}

