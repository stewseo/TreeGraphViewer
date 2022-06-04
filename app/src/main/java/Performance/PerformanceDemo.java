package Performance;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Pseudograph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;

import java.io.IOException;
import java.util.Iterator;

public class PerformanceDemo {
    public static void main(String[] args)
    {
        long time = System.currentTimeMillis();

        reportPerformanceFor("starting at", time);

        Graph<Object, DefaultEdge> g = new Pseudograph<>(DefaultEdge.class);
        Object prev;
        Object curr;

        curr = prev = new Object();
        g.addVertex(prev);

        int numVertices = 10000;
        int numEdgesPerVertex = 200;
        int numElements = numVertices * (1 + numEdgesPerVertex);

        System.out
                .println(
                        "\n" + "allocating graph with " + numElements
                                + " elements (may take a few tens of seconds)...");

        for (int i = 0; i < numVertices; i++) {
            curr = new Object();
            g.addVertex(curr);

            for (int j = 0; j < numEdgesPerVertex; j++) {
                g.addEdge(prev, curr);
            }

            prev = curr;
        }

        reportPerformanceFor("graph allocation", time);

        time = System.currentTimeMillis();

        for (Iterator<Object> i = new BreadthFirstIterator<>(g); i.hasNext();) {
            i.next();
        }

        reportPerformanceFor("breadth traversal", time);

        time = System.currentTimeMillis();

        for (Iterator<Object> i = new DepthFirstIterator<>(g); i.hasNext();) {
            i.next();
        }

        reportPerformanceFor("depth traversal", time);

        System.out.println("\n" + "Paused: graph is still in memory (to check mem consumption).");
        System.out.print("press enter to free memory and finish...");

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("done.");
    }

    private static void reportPerformanceFor(String msg, long refTime)
    {
        double time = (System.currentTimeMillis() - refTime) / 1000.0;
        double mem = usedMemory() / (1024.0 * 1024.0);
        mem = Math.round(mem * 100) / 100.0;
        System.out.println(msg + " (" + time + " sec, " + mem + "MB)");
    }

    private static long usedMemory() {
        Runtime rt = Runtime.getRuntime();

        return rt.totalMemory() - rt.freeMemory();
    }
}

