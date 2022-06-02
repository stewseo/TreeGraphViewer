package LCA_and_ShortestPath.app;

import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.interfaces.LowestCommonAncestorAlgorithm;
import org.jgrapht.alg.lca.BinaryLiftingLCAFinder;
import org.jgrapht.alg.lca.EulerTourRMQLCAFinder;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.generate.BarabasiAlbertForestGenerator;
import org.jgrapht.generate.GraphGenerator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.util.SupplierUtil;

import java.util.*;
import java.util.stream.Collectors;

public abstract class LCATreeTestBase {

    abstract <V, E> LowestCommonAncestorAlgorithm<V> createSolver(Graph<V, E> graph, Set<V> roots);

    public void testInvalidNode() {
            Graph<String, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
            g.addVertex("b");
            g.addVertex("a");
            g.addVertex("c");

            g.addEdge("a", "b");
            g.addEdge("a", "c");

            createSolver(g, Collections.singleton("a")).getLCA("d", "d");
        }

        public void testNotExploredNode() {
            Graph<String, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
            g.addVertex("b");
            g.addVertex("a");
            g.addVertex("c");
            g.addVertex("d");

            g.addEdge("a", "b");
            g.addEdge("a", "c");

            System.out.println(createSolver(g, Collections.singleton("a")).getLCA("a", "d"));
        }


        public void testOneNode() {
            Graph<String, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
            g.addVertex("a");

            System.out.println("a" + " " + createSolver(g, Collections.singleton("a")).getLCA("a", "a"));
        }


        public void testTwoRootsInTheSameTree() {
            Graph<String, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
            g.addVertex("b");
            g.addVertex("a");
            g.addVertex("c");
            g.addVertex("d");

            g.addEdge("a", "b");
            g.addEdge("c", "d");

            LinkedHashSet<String> roots = new LinkedHashSet<>();
            roots.add("a");
            roots.add("b");

            createSolver(g, roots).getLCA("a", "b");
        }

        public void testTwoRootsInTheSameTree2() {
            Graph<String, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
            g.addVertex("b");
            g.addVertex("a");
            g.addVertex("c");
            g.addVertex("d");

            g.addEdge("a", "b");
            g.addEdge("c", "d");

            LinkedHashSet<String> roots = new LinkedHashSet<>();
            roots.add("b");
            roots.add("a");

            createSolver(g, roots).getLCA("a", "b");
        }


        public void testDisconnectSmallGraph() {
            Graph<String, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
            g.addVertex("a");
            g.addVertex("b");

            LowestCommonAncestorAlgorithm<String> lcaAlgorithm =
                    createSolver(g, Collections.singleton("a"));

            System.out.println(lcaAlgorithm.getLCA("a", "b"));
            System.out.println(lcaAlgorithm.getLCA("b", "a"));
            System.out.println("a" + " " + lcaAlgorithm.getLCA("a", "a"));
            System.out.println("b" + " " + lcaAlgorithm.getLCA("b", "b"));
        }

        public void testDisconnectSmallGraph2() {
            Graph<String, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
            g.addVertex("a");
            g.addVertex("b");
            g.addVertex("c");

            LowestCommonAncestorAlgorithm<String> lcaAlgorithm =
                    createSolver(g, Collections.singleton("a"));

            System.out.println(lcaAlgorithm.getLCA("b", "c"));
            System.out.println(lcaAlgorithm.getLCA("c", "b"));
            System.out.println(lcaAlgorithm.getLCA("c", "a"));
            System.out.println(lcaAlgorithm.getLCA("a", "c"));
            System.out.println(lcaAlgorithm.getLCA("a", "b"));
            System.out.println(lcaAlgorithm.getLCA("b", "a"));
            System.out.println("a" + " " + lcaAlgorithm.getLCA("a", "a"));
            System.out.println("b" + " " + lcaAlgorithm.getLCA("b", "b"));
        }

        public void testEmptyGraph() {
            Graph<String, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
            createSolver(g, Collections.singleton("a"));
        }


        public void testEmptySetOfRoots() {
            Graph<String, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
            g.addVertex("a");

            createSolver(g, Collections.emptySet());
        }


        public void testRootNotInGraph() {
            Graph<String, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
            g.addVertex("a");

            createSolver(g, Collections.singleton("b"));
        }


        public void testGraphAllPossibleQueries() {
            final int n = 100;

            Random random = new Random(0x88_88);

            Graph<Integer, DefaultEdge> g = new SimpleGraph<>(
                    SupplierUtil.createIntegerSupplier(0), SupplierUtil.DEFAULT_EDGE_SUPPLIER, false);

            BarabasiAlbertForestGenerator<Integer, DefaultEdge> generator =
                    new BarabasiAlbertForestGenerator<>(1, n, random);

            generator.generateGraph(g, null);

            Integer root = g.vertexSet().iterator().next();

            LowestCommonAncestorAlgorithm<Integer> lcaAlgorithm1 =
                    createSolver(g, Collections.singleton(root));
            LowestCommonAncestorAlgorithm<Integer> lcaAlgorithm2;

            if (lcaAlgorithm1 instanceof EulerTourRMQLCAFinder)
                lcaAlgorithm2 = new BinaryLiftingLCAFinder<>(g, Collections.singleton(root));
            else
                lcaAlgorithm2 = new EulerTourRMQLCAFinder<>(g, Collections.singleton(root));

            List<Pair<Integer, Integer>> queries = new ArrayList<>(n * n);

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    queries.add(Pair.of(i, j));
                }
            }

            List<Integer> lcas1 = lcaAlgorithm1.getBatchLCA(queries);
            List<Integer> lcas2 = lcaAlgorithm2.getBatchLCA(queries);

            for (int i = 0; i < queries.size(); i++) {
                System.out.println(lcas1.get(i) + " " + lcas2.get(i));
            }
        }


        public void testLongChain() {
            final int n = 2_000;
            final int q = 100_000;

            Random random = new Random(0x88);

            Graph<Integer, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);

            for (int i = 1; i <= n; i++)
                g.addVertex(i);

            for (int i = 1; i < n; i++)
                g.addEdge(i, i + 1);

            List<Pair<Integer, Integer>> queries =
                    generateQueries(q, new ArrayList<>(g.vertexSet()), random);

            LowestCommonAncestorAlgorithm<Integer> lcaAlgorithm =
                    createSolver(g, Collections.singleton(n));

            List<Integer> lcas = lcaAlgorithm.getBatchLCA(queries);

            for (int i = 0; i < q; i++) {
                int a = queries.get(i).getFirst();
                int b = queries.get(i).getSecond();

                System.out.println((int) lcas.get(i) + " " + Math.max(a, b));
            }
        }


        public void testBinaryTree() {
            Graph<String, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);

            g.addVertex("a");
            g.addVertex("b");
            g.addVertex("c");
            g.addVertex("d");
            g.addVertex("e");

            g.addEdge("a", "b");
            g.addEdge("b", "c");
            g.addEdge("b", "d");
            g.addEdge("d", "e");

            LowestCommonAncestorAlgorithm<String> lcaAlgorithm =
                    createSolver(g, Collections.singleton("a"));

            System.out.println("b" + " " + lcaAlgorithm.getLCA("c", "e"));
            System.out.println("b" + " " + lcaAlgorithm.getLCA("b", "d"));
            System.out.println("d" + " " + lcaAlgorithm.getLCA("d", "e"));
        }


        public void testSmallTree() {
            Graph<Integer, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);

            for (int i = 1; i <= 11; i++)
                graph.addVertex(i);

            graph.addEdge(1, 2);
            graph.addEdge(2, 4);
            graph.addEdge(2, 5);
            graph.addEdge(2, 6);
            graph.addEdge(4, 7);
            graph.addEdge(4, 8);
            graph.addEdge(6, 9);
            graph.addEdge(1, 3);
            graph.addEdge(3, 10);
            graph.addEdge(3, 11);

            LowestCommonAncestorAlgorithm<Integer> lcaAlgorithm =
                    createSolver(graph, Collections.singleton(1));

            System.out.println(3 + " " + (int) lcaAlgorithm.getLCA(10, 11));
            System.out.println(2 + " " + (int) lcaAlgorithm.getLCA(8, 9));
            System.out.println(1 + " " + (int) lcaAlgorithm.getLCA(5, 11));
            System.out.println(2 + " " + (int) lcaAlgorithm.getLCA(5, 6));
            System.out.println(2 + " " + (int) lcaAlgorithm.getLCA(4, 2));
            System.out.println(2 + " " + (int) lcaAlgorithm.getLCA(4, 5));
            System.out.println(2 + " " + (int) lcaAlgorithm.getLCA(2, 2));
            System.out.println(2 + " " + (int) lcaAlgorithm.getLCA(8, 6));
            System.out.println(4 + " " +  (int) lcaAlgorithm.getLCA(7, 8));
        }


        public void testSmallTree2() {
            Graph<Integer, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);

            for (int i = 1; i <= 20; i++)
                graph.addVertex(i);

            graph.addEdge(2, 1);
            graph.addEdge(3, 1);
            graph.addEdge(4, 1);
            graph.addEdge(5, 1);
            graph.addEdge(6, 2);
            graph.addEdge(7, 5);
            graph.addEdge(8, 7);
            graph.addEdge(9, 3);
            graph.addEdge(10, 2);
            graph.addEdge(11, 9);
            graph.addEdge(12, 6);
            graph.addEdge(13, 4);
            graph.addEdge(14, 6);
            graph.addEdge(15, 2);
            graph.addEdge(16, 10);
            graph.addEdge(17, 15);
            graph.addEdge(18, 6);
            graph.addEdge(19, 14);
            graph.addEdge(20, 11);

            LowestCommonAncestorAlgorithm<Integer> lcaAlgorithm =
                    createSolver(graph, Collections.singleton(1));

            System.out.println(1 + " " + (int) lcaAlgorithm.getLCA(9, 14));
            System.out.println(1 + " " + (int) lcaAlgorithm.getLCA(10, 9));
//            Assert.assertEquals(15, (int) lcaAlgorithm.getLCA(15, 15));
//            Assert.assertEquals(1, (int) lcaAlgorithm.getLCA(1, 17));
//            Assert.assertEquals(3, (int) lcaAlgorithm.getLCA(3, 3));
//            Assert.assertEquals(1, (int) lcaAlgorithm.getLCA(3, 1));
//            Assert.assertEquals(1, (int) lcaAlgorithm.getLCA(11, 14));
//            Assert.assertEquals(6, (int) lcaAlgorithm.getLCA(18, 19));
//            Assert.assertEquals(2, (int) lcaAlgorithm.getLCA(12, 2));
//            Assert.assertEquals(2, (int) lcaAlgorithm.getLCA(16, 14));
        }


        public void testNonBinaryTreeBatch() {
            Graph<String, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);

            g.addVertex("a");
            g.addVertex("b");
            g.addVertex("c");
            g.addVertex("d");
            g.addVertex("e");
            g.addVertex("f");
            g.addVertex("g");
            g.addVertex("h");
            g.addVertex("i");
            g.addVertex("j");

            g.addEdge("a", "b");
            g.addEdge("b", "c");
            g.addEdge("c", "d");
            g.addEdge("d", "e");
            g.addEdge("b", "f");
            g.addEdge("b", "g");
            g.addEdge("c", "h");
            g.addEdge("c", "i");
            g.addEdge("i", "j");

            LowestCommonAncestorAlgorithm<String> lcaAlgorithm =
                    createSolver(g, Collections.singleton("a"));

            System.out.println("b" + " " + lcaAlgorithm.getLCA("b", "h"));
            System.out.println("b" + " " + lcaAlgorithm.getLCA("j", "f"));
            System.out.println("c" + " " + lcaAlgorithm.getLCA("j", "h"));

            // now all together in one call

            List<Pair<String, String>> queries = new ArrayList<>();
            queries.add(Pair.of("b", "h"));
            queries.add(Pair.of("j", "f"));
            queries.add(Pair.of("j", "h"));

            List<String> lcas = lcaAlgorithm.getBatchLCA(queries);

            System.out.println(Arrays.asList("b", "b", "c") + " " + lcas);

            // test it the other way around and starting from b
            System.out.println("b" + " " + createSolver(g, Collections.singleton("b")).getLCA("h", "b"));
        }

        public void randomHugeConnectedTree() {
            final int n = 100_000;
            final int q = 200_000;

            Random random = new Random(0x88);

            Graph<Integer, DefaultEdge> g = new SimpleGraph<>(
                    SupplierUtil.createIntegerSupplier(1), SupplierUtil.DEFAULT_EDGE_SUPPLIER, false);

            BarabasiAlbertForestGenerator<Integer, DefaultEdge> generator =
                    new BarabasiAlbertForestGenerator<>(1, n, random);

            generator.generateGraph(g, null);

            List<Integer> vertexList = new ArrayList<>(g.vertexSet());

            LowestCommonAncestorAlgorithm<Integer> lcaAlgorithm1 =
                    createSolver(g, Collections.singleton(vertexList.get(0)));
            LowestCommonAncestorAlgorithm<Integer> lcaAlgorithm2;

            if (lcaAlgorithm1 instanceof EulerTourRMQLCAFinder)
                lcaAlgorithm2 = new BinaryLiftingLCAFinder<>(g, vertexList.get(0));
            else
                lcaAlgorithm2 = new EulerTourRMQLCAFinder<>(g, vertexList.get(0));

            List<Pair<Integer, Integer>> queries = generateQueries(q, vertexList, random);

            List<Integer> lcas1 = lcaAlgorithm1.getBatchLCA(queries);
            List<Integer> lcas2 = lcaAlgorithm2.getBatchLCA(queries);

            for (int i = 0; i < q; i++) {
                System.out.println((lcas1.get(i) + " test equals " + lcas2.get(i)));
            }
        }

        public static <V> List<Pair<V, V>> generateQueries(int q, List<V> vertexList, Random random) {
            List<Pair<V, V>> queries = new ArrayList<>(q);

            for (int i = 0; i < q; i++) {
                V a = vertexList.get(random.nextInt(vertexList.size()));
                V b = vertexList.get(random.nextInt(vertexList.size()));

                queries.add(Pair.of(a, b));
            }
            return queries;
        }


        public void randomHugePossiblyDisconnectedTree() {
            final int n = 100_000;
            final int q = 200_000;

            Random random = new Random(0x55);

            final int numTrees = 100 + random.nextInt(200);

            Graph<Integer, DefaultEdge> g = new SimpleGraph<>(
                    SupplierUtil.createIntegerSupplier(1), SupplierUtil.DEFAULT_EDGE_SUPPLIER, false);

            BarabasiAlbertForestGenerator<Integer, DefaultEdge> generator =
                    new BarabasiAlbertForestGenerator<>(numTrees, n, random);

            generator.generateGraph(g, null);

            List<Integer> vertexList = new ArrayList<>(g.vertexSet());

            ConnectivityInspector<Integer, DefaultEdge> connectivityInspector =
                    new ConnectivityInspector<>(g);

            List<Set<Integer>> connectedComponents = connectivityInspector.connectedSets();

            Set<Integer> roots = connectedComponents
                    .stream().map(component -> component.iterator().next()).collect(Collectors.toSet());

            LowestCommonAncestorAlgorithm<Integer> lcaAlgorithm1 = createSolver(g, roots);
            LowestCommonAncestorAlgorithm<Integer> lcaAlgorithm2;

            if (lcaAlgorithm1 instanceof EulerTourRMQLCAFinder)
                lcaAlgorithm2 = new BinaryLiftingLCAFinder<>(g, roots);
            else
                lcaAlgorithm2 = new EulerTourRMQLCAFinder<>(g, roots);

            List<Pair<Integer, Integer>> queries = generateQueries(q, vertexList, random);

            List<Integer> lcas1 = lcaAlgorithm1.getBatchLCA(queries);
            List<Integer> lcas2 = lcaAlgorithm2.getBatchLCA(queries);

            for (int i = 0; i < q; i++) {
                System.out.println(lcas1.get(i) + " test equals " + lcas2.get(i));
            }
        }


        public void testSmallConnectedTrees() {
            Random random = new Random(0x88);
            final int tests = 10_000;
            final int q = 50;

            for (int test = 0; test < tests; test++) {
                final int n = 10 + random.nextInt(100);

                GraphGenerator<Integer, DefaultEdge, Integer> gen =
                        new BarabasiAlbertForestGenerator<>(1, n, random.nextInt());

                Graph<Integer, DefaultEdge> g = new SimpleGraph<>(
                        SupplierUtil.createIntegerSupplier(1), SupplierUtil.DEFAULT_EDGE_SUPPLIER, false);

                gen.generateGraph(g);

                List<Integer> vertexList = new ArrayList<>(g.vertexSet());
                Set<Integer> roots = Collections.singleton(vertexList.get(0));

                LowestCommonAncestorAlgorithm<Integer> lcaAlgorithm1 = createSolver(g, roots);
                LowestCommonAncestorAlgorithm<Integer> lcaAlgorithm2;

                if (lcaAlgorithm1 instanceof EulerTourRMQLCAFinder)
                    lcaAlgorithm2 = new BinaryLiftingLCAFinder<>(g, roots);
                else
                    lcaAlgorithm2 = new EulerTourRMQLCAFinder<>(g, roots);

                List<Pair<Integer, Integer>> queries = generateQueries(q, vertexList, random);

                List<Integer> lcas1 = lcaAlgorithm1.getBatchLCA(queries);
                List<Integer> lcas2 = lcaAlgorithm2.getBatchLCA(queries);

                for (int i = 0; i < q; i++) {
                    System.out.println(lcas1.get(i) + " " + lcas2.get(i));
                }
            }
        }

        public void testSmallDisconnectedTrees() {
            Random random = new Random(0x88);
            final int tests = 10_000;
            final int q = 50;

            for (int test = 0; test < tests; test++) {
                final int n = 10 + random.nextInt(200);

                GraphGenerator<Integer, DefaultEdge, Integer> gen =
                        new BarabasiAlbertForestGenerator<>(n / 10, n, random.nextInt());

                Graph<Integer, DefaultEdge> g = new SimpleGraph<>(
                        SupplierUtil.createIntegerSupplier(1), SupplierUtil.DEFAULT_EDGE_SUPPLIER, false);

                gen.generateGraph(g);

                Set<Integer> roots = new ConnectivityInspector<>(g)
                        .connectedSets().stream().map(x -> x.iterator().next()).collect(Collectors.toSet());

                LowestCommonAncestorAlgorithm<Integer> lcaAlgorithm1 = createSolver(g, roots);
                LowestCommonAncestorAlgorithm<Integer> lcaAlgorithm2;

                if (lcaAlgorithm1 instanceof EulerTourRMQLCAFinder)
                    lcaAlgorithm2 = new BinaryLiftingLCAFinder<>(g, roots);
                else
                    lcaAlgorithm2 = new EulerTourRMQLCAFinder<>(g, roots);

                List<Pair<Integer, Integer>> queries =
                        generateQueries(q, new ArrayList<>(g.vertexSet()), random);

                List<Integer> lcas1 = lcaAlgorithm1.getBatchLCA(queries);
                List<Integer> lcas2 = lcaAlgorithm2.getBatchLCA(queries);

                for (int i = 0; i < q; i++) {
                    System.out.println(lcas1.get(i) + " " +  lcas2.get(i));
                }
            }
        }
    }



