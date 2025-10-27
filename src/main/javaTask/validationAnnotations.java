import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class validationAnnotations {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface NotNull {
        String message();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Size {
        int min() default 0;
        int max() default Integer.MAX_VALUE;
        String message();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Range {
        long min() default Long.MIN_VALUE;
        long max() default Long.MAX_VALUE;
        String message();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Email {
        String message();
    }
}
