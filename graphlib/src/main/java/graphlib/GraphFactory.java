package graphlib;

/**
 * User: Denis_Ivanov
 * Date: 13.06.2020
 * Time: 19:03
 */
public class GraphFactory {

    public static <T> Graph<T> createDefaultGraph() {
        return new SimpleGraph<>();
    }

}
