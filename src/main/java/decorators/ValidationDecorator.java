package decorators;

import java.util.Optional;

public class ValidationDecorator extends DataServiceDecorator {

    public ValidationDecorator(DataService wrappedService) {
        super(wrappedService);
    }


    @Override
    public Optional<String> findDataByKey(String key) {
        validateKey(key);
        return wrappedService.findDataByKey(key);
    }

    @Override
    public void saveData(String key, String data) {
        validateKey(key);
        validateData(data);
        wrappedService.saveData(key, data);
    }

    @Override
    public boolean deleteData(String key) {
        validateKey(key);
        return wrappedService.deleteData(key);
    }

    private void validateKey(String key) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Ключ не может быть null или пустым");
        }
    }

    private void validateData(String data) {
        if (data == null) {
            throw new IllegalArgumentException("Данные не могут быть null");
        }
    }
}

