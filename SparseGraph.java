import java.util.LinkedList;

/**
    A directed graph implementation optimized for sparse graphs.

    @param <V> Type of vertex element
    @param <E> Type of edge element
*/
public class SparseGraph<V, E> implements Graph<V, E> {
    private class IncidenceVertex implements Vertex<V> {
        public IncidenceVertex(V d) {
            this.outgoing = new LinkedList<DirectedEdge>();
            this.incoming = new LinkedList<DirectedEdge>();
            this.data = d;
            this.label = null;
            this.removed = false;
        }

        public LinkedList<DirectedEdge> outgoing;
        public LinkedList<DirectedEdge> incoming;

        public V data;
        public Object label;
        public boolean removed;

        @Override
        public V get() { return data; }
        @Override
        public void put(V v) { data = v; }

        public SparseGraph<V, E> manufacturer() {
            return removed ? null : SparseGraph.this;
        }
    }

    private class DirectedEdge implements Edge<E> {
        private DirectedEdge(IncidenceVertex f, IncidenceVertex t, E d) {
            this.from = f;
            this.to = t;
            this.data = d;
            this.label = null;
            this.removed = false;
        }

        public IncidenceVertex from;
        public IncidenceVertex to;

        public E data;
        public Object label;
        public boolean removed;

        @Override
        public boolean equals(Object obj) { // easier to check for duplicate edges
            if (obj == null)
                return false;
            if (obj == this)
                return true;
            if (!(obj instanceof SparseGraph.DirectedEdge))
                return false;

            DirectedEdge e = (DirectedEdge) obj;
            return e.from.equals(this.from) && e.to.equals(this.to);
        }

        @Override
        public E get() { return data; }
        @Override
        public void put(E e) { data = e; }

        public SparseGraph<V, E> manufacturer() {
            return removed ? null : SparseGraph.this;
        }
    }

    private LinkedList<IncidenceVertex> vertices;
    private LinkedList<DirectedEdge> edges;

    public SparseGraph() {
        this.vertices = new LinkedList<IncidenceVertex>();
        this.edges = new LinkedList<DirectedEdge>();
    } 

    private IncidenceVertex validateVertex(Vertex<V> v)
        throws IllegalArgumentException {
        if (v == null)
            throw new IllegalArgumentException("Inputted Vertex is null.");
        else if (!(v instanceof SparseGraph.IncidenceVertex))
            throw new IllegalArgumentException("Inputted Vertex unusable by SparseGraph.");
        IncidenceVertex validee = (IncidenceVertex) v;
        if (!(validee.manufacturer() == this))
            throw new IllegalArgumentException("Inputted Vertex does not"
                + "belong to this SparseGraph.");
        return validee;
    }

    private DirectedEdge validateEdge(Edge<E> e)
        throws IllegalArgumentException {
        if (e == null)
            throw new IllegalArgumentException("Inputted Edge is null.");
        else if (!(e instanceof SparseGraph.DirectedEdge))
            throw new IllegalArgumentException("Inputted Edge unusable by SparseGraph.");
        DirectedEdge validee = (DirectedEdge)e;
        if (!(validee.manufacturer() == this))
            throw new IllegalArgumentException("Inputted Edge does not"
                + "belong to this SparseGraph.");
        return validee;
    }

    @Override
    public Vertex<V> insert(V v) {
        IncidenceVertex newVertex = new IncidenceVertex(v);
        this.vertices.add(newVertex);
        return newVertex;
    }

    @Override
    public Edge<E> insert(Vertex<V> from, Vertex<V> to, E e) {
        DirectedEdge insert;
        IncidenceVertex f;
        IncidenceVertex t;
        
        // validation
        LinkedList<DirectedEdge> search;
        if (from == to)
            throw new IllegalArgumentException("Can't create self-loops.");
        f = this.validateVertex(from);
        t = this.validateVertex(to);
        insert = new DirectedEdge(f, t, e);
        search = f.outgoing.size() <= t.incoming.size() ? f.outgoing : t.incoming;
        if (search.contains(insert))
            throw new IllegalArgumentException("Can't insert duplicate edges.");

        f.outgoing.add(insert);
        t.incoming.add(insert);
        this.edges.add(insert);
        
        return insert;
    }

    @Override
    public V remove(Vertex<V> vertex) {
        // validation
        IncidenceVertex v = this.validateVertex(vertex);
        if (!(v.outgoing.isEmpty() && v.incoming.isEmpty()))
            throw new IllegalArgumentException("Can't remove Vertex with incoming or outgoing edges");

        v.removed = true;
        this.vertices.remove(v);

        return v.data;
    }

    @Override
    public E remove(Edge<E> edge) {
        DirectedEdge e = this.validateEdge(edge);

        e.removed = true;
        e.from.outgoing.remove(e);
        e.to.incoming.remove(e);
        this.edges.remove(e);
        
        return e.data;
    }

    @Override
    public Iterable<Vertex<V>> vertices() {
        LinkedList<Vertex<V>> list = new LinkedList<Vertex<V>>();
        list.addAll(this.vertices);
        return list;
    }

    @Override
    public Iterable<Edge<E>> edges() {
        LinkedList<Edge<E>> list = new LinkedList<Edge<E>>();
        list.addAll(this.edges);
        return list;
    }

    @Override
    public Iterable<Edge<E>> outgoing(Vertex<V> vertex) {
        IncidenceVertex v = this.validateVertex(vertex);
        LinkedList<Edge<E>> list = new LinkedList<Edge<E>>();
        list.addAll(v.outgoing);
        return list;
    }

    @Override
    public Iterable<Edge<E>> incoming(Vertex<V> vertex) {
        IncidenceVertex v = this.validateVertex(vertex);
        LinkedList<Edge<E>> list = new LinkedList<Edge<E>>();
        list.addAll(v.incoming);
        return list;
    }

    @Override
    public Vertex<V> from(Edge<E> edge) {
        DirectedEdge e = this.validateEdge(edge);
        return e.from;
    }

    @Override
    public Vertex<V> to(Edge<E> edge) {
        DirectedEdge e = this.validateEdge(edge);
        return e.to;
    }

    @Override
    public void label(Vertex<V> vertex, Object l) {
        IncidenceVertex v = this.validateVertex(vertex);
        if (l == null)
            throw new IllegalArgumentException("Can't label null.");
        v.label = l;
        return;
    }

    @Override
    public void label(Edge<E> edge, Object l) {
        DirectedEdge e = this.validateEdge(edge);
        if (l == null)
            throw new IllegalArgumentException("Can't label null.");
        e.label = l;
        return;
    }

    @Override
    public Object label(Vertex<V> vertex) {
        IncidenceVertex v = this.validateVertex(vertex);
        return v.label;
    }

    @Override
    public Object label(Edge<E> edge) {
        DirectedEdge e = this.validateEdge(edge);
        return e.label;
    }

    @Override
    public void clearLabels() {
        for (IncidenceVertex v : this.vertices)
            v.label = null;
        for (DirectedEdge e : this.edges)
            e.label = null;
        return;
    }

    /**
        Converts to GraphViz-compatible string format.
    */
    public String toString() {
        return null;
    }
}
