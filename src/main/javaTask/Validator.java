import java.lang.reflect.Field;
import java.util.regex.Pattern;

public class Validator {

    // Регулярное выражение для валидации email согласно стандарту
    private static final String EMAIL_PATTERN =
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final Pattern EMAIL_REGEX = Pattern.compile(EMAIL_PATTERN);

    public static ValidatonResult.ValidationResult validate(Object object) {
        ValidatonResult.ValidationResult result = new ValidatonResult.ValidationResult();

        if (object == null) {
            result.addError("Validated object cannot be null");
            return result;
        }

        Class<?> clazz = object.getClass();

        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            // Разрешаем доступ к приватным полям (лекция - setAccessible(true))
            field.setAccessible(true);

            try {
                // Получаем значение поля через Reflection (лекция - field.get())
                Object fieldValue = field.get(object);

                // Проверяем аннотации валидации на поле
                validateField(field, fieldValue, result);

            } catch (IllegalAccessException e) {
                result.addError("Cannot access field: " + field.getName());
            }
        }

        return result;
    }

    /**
     * Валидация отдельного поля на основе аннотаций
     */
    private static void validateField(Field field, Object value, ValidatonResult.ValidationResult result) {

        // Проверка @NotNull аннотации
        if (field.isAnnotationPresent(validationAnnotations.NotNull.class)) {
            validationAnnotations.NotNull notNull = field.getAnnotation(validationAnnotations.NotNull.class);
            if (value == null) {
                result.addError(notNull.message());
            }
        }

        // Если значение null, пропускаем остальные проверки (кроме @NotNull)
        if (value == null) {
            return;
        }

        // Проверка @Size аннотации для строк
        if (field.isAnnotationPresent(validationAnnotations.Size.class)) {
            validationAnnotations.Size size = field.getAnnotation(validationAnnotations.Size.class);
            if (value instanceof String) {
                String stringValue = (String) value;
                int length = stringValue.length();
                if (length < size.min() || length > size.max()) {
                    result.addError(size.message());
                }
            }
        }

        // Проверка @Range аннотации для чисел
        if (field.isAnnotationPresent(validationAnnotations.Range.class)) {
            validationAnnotations.Range range = field.getAnnotation(validationAnnotations.Range.class);
            if (value instanceof Number) {
                Number number = (Number) value;
                long longValue = number.longValue();
                if (longValue < range.min() || longValue > range.max()) {
                    result.addError(range.message());
                }
            } else if (value instanceof Integer) {
                Integer intValue = (Integer) value;
                if (intValue < range.min() || intValue > range.max()) {
                    result.addError(range.message());
                }
            }
        }

        // Проверка @Email аннотации
        if (field.isAnnotationPresent(validationAnnotations.Email.class)) {
            validationAnnotations.Email email = field.getAnnotation(validationAnnotations.Email.class);
            if (value instanceof String) {
                String emailValue = (String) value;
                if (!isValidEmail(emailValue)) {
                    result.addError(email.message());
                }
            }
        }
    }

    /**
     * Валидация email с использованием регулярного выражения
     */
    private static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_REGEX.matcher(email).matches();
    }
}
