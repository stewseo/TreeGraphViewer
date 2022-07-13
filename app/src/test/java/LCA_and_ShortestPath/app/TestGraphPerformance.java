package LCA_and_ShortestPath.app;

import org.jgrapht.alg.connectivity.GabowStrongConnectivityInspector;
import org.jgrapht.alg.flow.EdmondsKarpMFImpl;
import org.jgrapht.alg.interfaces.StrongConnectivityAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.generate.GnmRandomGraphGenerator;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.util.SupplierUtil;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class TestGraphPerformance {

        public static final int PERF_BENCHMARK_VERTICES_COUNT = 1000;
        public static final int PERF_BENCHMARK_EDGES_COUNT = 100000;
        public static final long SEED = 1446523573696201013l;
        public static final int NR_GRAPHS = 5; // Number of unique graphs on which the tests are
        // repeated

        @State(Scope.Benchmark)
        private static abstract class DirectedGraphBenchmarkBase {

            private Blackhole blackhole;
            protected GnmRandomGraphGenerator<Integer, DefaultWeightedEdge> rgg;
            private SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge> graph;

            /**
             * Creates a random graph using the Random Graph Generator
             *
             * @return random graph
             */
            abstract SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge> constructGraph();

            @Setup
            public void setup()
            {
                blackhole = new Blackhole(
                        "Today's password is swordfish. I understand instantiating Blackholes directly is dangerous.");
            }

            /**
             * Benchmark 1: graph construction
             */
            @Benchmark
            public void generateGraphBenchmark()
            {
                for (int i = 0; i < NR_GRAPHS; i++) {
                    rgg = new GnmRandomGraphGenerator<>(
                            PERF_BENCHMARK_VERTICES_COUNT, PERF_BENCHMARK_EDGES_COUNT, SEED + i);
                    // Create a graph
                    graph = constructGraph();

                }
            }

            /**
             * Benchmark 2: Simulate graph usage: Create a graph, perform various algorithms, partially
             * destroy graph
             */
            @Benchmark
            public void graphPerformanceBenchmark()
            {
                for (int i = 0; i < NR_GRAPHS; i++) {
                    rgg = new GnmRandomGraphGenerator<>(
                            PERF_BENCHMARK_VERTICES_COUNT, PERF_BENCHMARK_EDGES_COUNT, SEED + i);
                    // Create a graph
                    graph = constructGraph();

                    Integer[] vertices =
                            graph.vertexSet().toArray(new Integer[graph.vertexSet().size()]);
                    Integer source = vertices[0];
                    Integer sink = vertices[vertices.length - 1];

                    // Run various algorithms on the graph
                    double length = this.calculateShorestPath(graph, source, sink);
                    blackhole.consume(length);

                    double maxFlow = this.calculateMaxFlow(graph, source, sink);
                    blackhole.consume(maxFlow);

                    boolean isStronglyConnected = this.isStronglyConnected(graph);
                    blackhole.consume(isStronglyConnected);

                    // Destroy some random edges in the graph
                    destroyRandomEdges(graph);
                }
            }

            private double calculateShorestPath(
                    SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge> graph, Integer source,
                    Integer sink)
            {
                DijkstraShortestPath<Integer, DefaultWeightedEdge> shortestPathAlg =
                        new DijkstraShortestPath<>(graph);
                return shortestPathAlg.getPath(source, sink).getWeight();
            }

            private double calculateMaxFlow(
                    SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge> graph, Integer source,
                    Integer sink)
            {
                EdmondsKarpMFImpl<Integer, DefaultWeightedEdge> maximumFlowAlg =
                        new EdmondsKarpMFImpl<>(graph);
                return maximumFlowAlg.getMaximumFlow(source, sink).getValue();
            }

            private boolean isStronglyConnected(
                    SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge> graph)
            {
                StrongConnectivityAlgorithm<Integer, DefaultWeightedEdge> strongConnectivityAlg =
                        new GabowStrongConnectivityInspector<>(graph);
                return strongConnectivityAlg.isStronglyConnected();
            }

            private void destroyRandomEdges(
                    SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge> graph)
            {
                int nrVertices = graph.vertexSet().size();
                Random rand = new Random(SEED);
                for (int i = 0; i < PERF_BENCHMARK_EDGES_COUNT / 2; i++) {
                    int u = rand.nextInt(nrVertices);
                    int v = rand.nextInt(nrVertices);
                    graph.removeEdge(u, v);
                }
            }

        }

        /**
         * Graph class which relies on the (legacy) DirectedSpecifics implementation. This class is
         * optimized for low memory usage, but performs edge retrieval operations fairly slow.
         */
        public static class MemoryEfficientDirectedGraphBenchmark
                extends
                DirectedGraphBenchmarkBase
        {
            @Override
            SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge> constructGraph()
            {
                SimpleDirectedWeightedGraph<Integer,
                        DefaultWeightedEdge> graph = new MemoryEfficientDirectedWeightedGraph<>(
                        SupplierUtil.createIntegerSupplier(1),
                        SupplierUtil.DEFAULT_WEIGHTED_EDGE_SUPPLIER);
                rgg.generateGraph(graph);
                return graph;
            }
        }

        /**
         * Graph class which relies on the FastLookupDirectedSpecifics. This class is optimized to
         * perform quick edge retrievals.
         */
        public static class FastLookupDirectedGraphBenchmark
                extends
                DirectedGraphBenchmarkBase
        {
            @Override
            SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge> constructGraph()
            {
                SimpleDirectedWeightedGraph<Integer,
                        DefaultWeightedEdge> graph = new SimpleDirectedWeightedGraph<>(
                        SupplierUtil.createIntegerSupplier(1),
                        SupplierUtil.DEFAULT_WEIGHTED_EDGE_SUPPLIER);
                rgg.generateGraph(graph);
                return graph;
            }
        }

        @Test
        public void testRandomGraphBenchmark()
                throws RunnerException
        {
            Options opt = new OptionsBuilder()
                    .include(".*" + MemoryEfficientDirectedGraphBenchmark.class.getSimpleName() + ".*")
                    .include(".*" + FastLookupDirectedGraphBenchmark.class.getSimpleName() + ".*")

                    .mode(Mode.AverageTime).timeUnit(TimeUnit.MILLISECONDS)
                    // .warmupTime(TimeValue.seconds(1))
                    .warmupIterations(3)
                    // .measurementTime(TimeValue.seconds(1))
                    .measurementIterations(5).forks(1).shouldFailOnError(true).shouldDoGC(true).build();

            new Runner(opt).run();
        }


        public static class MemoryEfficientDirectedWeightedGraph<V, E>
                extends
                SimpleDirectedWeightedGraph<V, E>
        {
            private static final long serialVersionUID = -1826738982402033648L;

            public MemoryEfficientDirectedWeightedGraph(
                    Supplier<V> vertexSupplier, Supplier<E> edgeSupplier)
            {
                super(vertexSupplier, edgeSupplier);
            }
        }
    }

