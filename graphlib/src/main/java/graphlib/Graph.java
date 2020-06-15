package graphlib;

import java.util.List;

/**
 * User: Denis_Ivanov
 * Date: 12.06.2020
 * Time: 16:56
 */
public interface Graph<T> {

    /**
     * Adding vertex
     *
     * @param userObject any object with equals and hashCode methods satisfying contracts to be stored as HashMap key
     */
    void addVertex(final T userObject);

    /**
     * Adding edge
     * Could be used when edges will have weights or othed additional info
     * Do nothing if one of edges is null
     *
     * @param edge Edge with vertices which should be already in graph
     * @throws VertexNotFoundException if at least one of vertices are not in graph
     */
    void addEdge(Edge<T> edge) throws VertexNotFoundException;

    /**
     * Adding edge
     * Do nothing if one of edges is null
     *
     * @param from object should be one of vertices added to graph
     * @param to   object should be one of vertices added to graph
     * @return created edge or null
     * @throws VertexNotFoundException if at least one of vertices are not in graph
     */
    Edge<T> addEdge(final T from, final T to) throws VertexNotFoundException;

    /**
     * Adding bidirectional edge - two backward edges
     *
     * @param from object should be one of vertices added to graph
     * @param to   object should be one of vertices added to graph
     * @throws VertexNotFoundException if at least one of vertices are not in graph
     */
    void addBiDirectionalEdge(final T from, final T to) throws VertexNotFoundException;


    List<Edge<T>> getPath(final T start,
                          final T end,
                          final PathMethod pathMethod) throws VertexNotFoundException, BadBackGraphException;

    enum PathMethod {
        BFS
    }
}
