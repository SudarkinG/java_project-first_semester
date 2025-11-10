package decorators;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class CachingDecorator extends DataServiceDecorator {
    private Map<String, String> cache = new HashMap<>();

    public CachingDecorator(DataService wrappedService) {
        super(wrappedService);
    }

    @Override
    public Optional<String> findDataByKey(String key) {
        if (cache.containsKey(key)) {
            return Optional.ofNullable(cache.get(key));
        }
        
        Optional<String> result = wrappedService.findDataByKey(key);
        result.ifPresent(value -> cache.put(key, value));
        
        return result;
    }

    @Override
    public void saveData(String key, String data) {
        wrappedService.saveData(key, data);
        cache.put(key, data);
    }

    @Override
    public boolean deleteData(String key) {
        cache.remove(key);
        return wrappedService.deleteData(key);
    }
}

