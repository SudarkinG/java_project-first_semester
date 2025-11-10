package decorators;

import java.util.Optional;

public abstract class DataServiceDecorator implements DataService {
    protected DataService wrappedService;

    public DataServiceDecorator(DataService wrappedService) {
        this.wrappedService = wrappedService;
    }

    @Override
    public Optional<String> findDataByKey(String key) {
        return wrappedService.findDataByKey(key);
    }

    @Override
    public void saveData(String key, String data) {
        wrappedService.saveData(key, data);
    }

    @Override
    public boolean deleteData(String key) {
        return wrappedService.deleteData(key);
    }
}

