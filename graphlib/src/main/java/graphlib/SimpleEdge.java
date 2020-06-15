package graphlib;

import java.util.Objects;

/**
 * User: Denis_Ivanov
 * Date: 12.06.2020
 * Time: 19:12
 */
public class SimpleEdge<T> implements Edge<T> {
    private final T from;
    private final T to;

    public SimpleEdge(T from, T to) {
        this.from = from;
        this.to = to;
    }

    public T getFrom() {
        return from;
    }

    public T getTo() {
        return to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleEdge<?> that = (SimpleEdge<?>) o;
        return Objects.equals(from, that.from) &&
                Objects.equals(to, that.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }

}
