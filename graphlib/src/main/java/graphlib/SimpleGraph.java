package graphlib;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * User: Denis_Ivanov
 * Date: 12.06.2020
 * Time: 16:55
 */
public class SimpleGraph<T> implements Graph<T> {

    final Map<T, Queue<Edge<T>>> vertexToEdges = new HashMap<>();

    @Override
    public void addVertex(T userObject) {
        if (null == userObject) {
            return;
        }
        synchronized (vertexToEdges) {
            vertexToEdges.putIfAbsent(userObject, new ConcurrentLinkedQueue<>());
        }
    }

    @Override
    public void addEdge(final Edge<T> edge) throws VertexNotFoundException {
        final T to = edge.getTo();
        final T from = edge.getFrom();
        if (null == from || null == to) {
            return;
        }
        synchronized (vertexToEdges) {
            if (vertexToEdges.containsKey(from) && vertexToEdges.containsKey(to)) {
                vertexToEdges.get(from).add(edge);
            } else {
                throw new VertexNotFoundException("Adding Edge with Vertex which is not found in graph");
            }
        }
    }

    @Override
    public Edge<T> addEdge(T from, T to) throws VertexNotFoundException {
        if (null == from || null == to) {
            return null;
        }
        synchronized (vertexToEdges) {
            if (vertexToEdges.containsKey(from) && vertexToEdges.containsKey(to)) {
                final SimpleEdge<T> edge = new SimpleEdge<>(from, to);
                vertexToEdges.get(from).add(edge);
                return edge;
            } else {
                throw new VertexNotFoundException("Adding Edge with Vertex which is not found in graph");
            }
        }
    }

    @Override
    public void addBiDirectionalEdge(T from, T to) throws VertexNotFoundException {
        synchronized (vertexToEdges) {
            if (vertexToEdges.containsKey(from) && vertexToEdges.containsKey(to)) {
                final SimpleEdge<T> edge = new SimpleEdge<>(from, to);
                final SimpleEdge<T> backEdge = new SimpleEdge<>(to, from);
                vertexToEdges.get(from).add(edge);
                vertexToEdges.get(to).add(backEdge);
            } else {
                throw new VertexNotFoundException("Adding Edge with Vertex which is not found in graph");
            }
        }
    }

    @Override
    public List<Edge<T>> getPath(final T start,
                                 final T end,
                                 PathMethod pathMethod) throws VertexNotFoundException, BadBackGraphException {
        if (PathMethod.BFS == pathMethod) {
            return getPathBFS(start, end);
        }
        return null;
    }

    /**
     * Breadth First Search implementation
     * Synchronization: can do only until we do not have delete method and path not have to be optimal
     * Possible better way - to sync by envelope object of vertex object (some <code>Vertex<T></code>)
     *
     * @param start starting vertex
     * @param end   destination vertex
     * @return null if no path found or graph does not contain one of arguments,
     * empty list if start = end,
     * list of edges path
     * @throws VertexNotFoundException if attempting to add edge to nonexistent vertex
     */
    public List<Edge<T>> getPathBFS(final T start, final T end) throws VertexNotFoundException, BadBackGraphException {
        if (null == start || null == end) {
            return null;
        }
        synchronized (vertexToEdges) {
            if (!vertexToEdges.containsKey(start) || !vertexToEdges.containsKey(end)) {
                return null;
            }
        }
        if (start.equals(end)) {
            return new LinkedList<>();
        }
        final SimpleGraph<T> backPaths = new SimpleGraph<>();
        final Set<T> visitedVertices = new LinkedHashSet<>();
        final Queue<T> queue = new LinkedList<>();
        visitedVertices.add(start);
        queue.add(start);
        backPaths.addVertex(start);

        while (queue.size() != 0) {
            final T vertex = queue.poll();
            final Queue<Edge<T>> edges;
            synchronized (vertexToEdges) {
                edges = vertexToEdges.get(vertex);
            }
            for (Edge<T> edge : edges) {
                final T to = edge.getTo();
                if (end.equals(to)) {
                    backPaths.addVertex(to);
                    backPaths.addEdge(to, vertex);
                    return getPathFromBackGraph(backPaths, start, to);
                }
                if (!visitedVertices.contains(to)) {
                    backPaths.addVertex(to);
                    backPaths.addEdge(to, vertex);
                    queue.add(to);
                    visitedVertices.add(to);
                }
            }
        }
        return null;
    }

    private List<Edge<T>> getPathFromBackGraph(final SimpleGraph<T> backPaths,
                                               final T start,
                                               final T end) throws BadBackGraphException {
        final List<Edge<T>> result = new LinkedList<>();
        T currentVertex = end;
        int infinitePreventer = 0;
        while (true) {
            if (infinitePreventer > backPaths.getVertexToEdges().size()) {
                throw new BadBackGraphException("Back graph edge contains loops");
            }
            if (start.equals(currentVertex)) {
                break;
            }
            Queue<Edge<T>> edges = backPaths.getVertexToEdges().get(currentVertex);
            if (edges == null || edges.size() != 1) {
                throw new BadBackGraphException("Back graph edge contains wrong number of edges: " +
                        (edges == null ? 0 : edges.size()));
            }
            Edge<T> backEdge = edges.poll();
            result.add(new SimpleEdge<>(backEdge.getTo(), backEdge.getFrom()));
            currentVertex = backEdge.getTo();
            infinitePreventer++;
        }
        Collections.reverse(result);
        return result;
    }

    private Map<T, Queue<Edge<T>>> getVertexToEdges() {
        return vertexToEdges;
    }
}
