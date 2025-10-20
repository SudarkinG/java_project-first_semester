import java.util.Iterator;
import java.util.Objects;

public interface CustomList<A> extends Iterator<A>{
    int size();
    boolean isEmpty();
    A get(int ind);
    A remove(int ind);
    boolean add(A element);

    Iterator<A> iterator();
}
