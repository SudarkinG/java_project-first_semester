package decorators;

import java.util.Optional;


public class LoggingDecorator extends DataServiceDecorator {

    public LoggingDecorator(DataService wrappedService) {
        super(wrappedService);
    }

    @Override
    public Optional<String> findDataByKey(String key) {
        System.out.println("LoggingDecorator: findDataByKey с ключом: " + key);
        Optional<String> result = wrappedService.findDataByKey(key);
        System.out.println("LoggingDecorator:  findDataByKey: " + (result.isPresent() ? "найдено" : "не найдено"));
        return result;
    }


    @Override
    public void saveData(String key, String data) {
        System.out.println("LoggingDecorator: saveData с ключом: " + key + ", данные: " + data);
        wrappedService.saveData(key, data);
        System.out.println("LoggingDecorator: данные успешно сохранены");
    }

    @Override
    public boolean deleteData(String key) {
        System.out.println("LoggingDecorator: deleteData с ключом: " + key);
        boolean result = wrappedService.deleteData(key);
        System.out.println("LoggingDecorator:  deleteData: " + (result ? "удалено" : "ошибка"));
        return result;
    }
}

