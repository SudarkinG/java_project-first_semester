import java.util.Iterator;
import java.util.NoSuchElementException;

public class CustomArrayList<A> implements CustomList<A> {

    private static final int DEFAULT_CAPACITY = 10;
    private static final double EXPANSION_FACTOR = 1.5;
    private int size;
    private Object[] elements;

    public CustomArrayList() {
        this.elements = new Object[DEFAULT_CAPACITY];
        this.size = 0;
    }
    @Override
    public boolean add(A element) {
        if(size == elements.length){
            int oldCapacity = elements.length;
            Object[] elements = new Object[oldCapacity+1];
            System.arraycopy(elements,0,elements,0,elements.length);
        }
        elements[size] = element;
        size++;
        return true;
    }

    @Override
    public A get(int index) {
        return (A) elements[index];
    }

    @Override
    public A remove(int index) {
        A removedElement = (A) elements[index];
        for (int i = index; i < size - 1; i++) {
            elements[i] = elements[i + 1];
        }

        elements[size - 1] = null;
        size--;

        return removedElement;
    }
    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public Iterator<A> iterator() {
        return new CustomArrayListIterator();
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public A next() {
        return null;
    }

    private class CustomArrayListIterator implements Iterator<A>{
        private int currPos = 0;

        @Override
        public boolean hasNext(){
            return currPos<size;
        }
        @Override
        public A next(){
            if(!hasNext()) {
                throw new NoSuchElementException("No more elements");
            }
            return (A) elements[currPos++];
        }
        @Override
        public void remove() {
            CustomArrayList.this.remove(currPos - 1);
            currPos--;
        }
    }
}
