package LCA;

import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.interfaces.LowestCommonAncestorAlgorithm;
import org.jgrapht.alg.lca.BinaryLiftingLCAFinder;
import org.jgrapht.alg.lca.EulerTourRMQLCAFinder;
import org.jgrapht.alg.lca.HeavyPathLCAFinder;
import org.jgrapht.alg.lca.TarjanLCAFinder;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.generate.BarabasiAlbertForestGenerator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.util.AVLTree;
import org.jgrapht.util.SupplierUtil;

import java.util.*;
import java.util.stream.Collectors;

public class LCAPerformance extends AVLTree<Integer>{

        public static final int PERF_BENCHMARK_VERTICES_COUNT = 100_000;
        public static final int PERF_BENCHMARK_QUERIES_COUNT = 300_000;

        public static int NUMBER_TREES = 10 * PERF_BENCHMARK_VERTICES_COUNT / 100;
        public static long SEED = 111222111;

        public static LowestCommonAncestorAlgorithm<Integer> solver;

        public static List<Pair<Integer, Integer>> queries;

        public static void main(String[] args) {
            Random random = new Random(SEED);

            Graph<Integer, DefaultEdge> forest =  new SimpleGraph<>(SupplierUtil.createIntegerSupplier(0), SupplierUtil.DEFAULT_EDGE_SUPPLIER, false);

            BarabasiAlbertForestGenerator<Integer, DefaultEdge> generator = new BarabasiAlbertForestGenerator<>(NUMBER_TREES, PERF_BENCHMARK_VERTICES_COUNT, random);
            System.out.println("BerabasiAlbertForestGenerator: " + generator.toString());

            generator.generateGraph(forest, null);
            System.out.println("After generateGraph: " + generator.toString());

            Set<Integer> roots = new ConnectivityInspector<>(forest).connectedSets().stream().map(x -> x.iterator().next()).collect(Collectors.toSet());
            System.out.println("numbers of root sets: "+ roots.size());
            assert roots.size() == NUMBER_TREES;

            solver = createSolverHeavyPathLCAFinderForest(forest, roots);
            queries = LCATreeTestBase.generateQueries(PERF_BENCHMARK_QUERIES_COUNT, new ArrayList<>(forest.vertexSet()), random);

            System.out.println("Creating from BinaryLiftingLCAFinder: " + solver.getBatchLCA(queries).size());
//            System.out.println("Generating queries: " + queries);
            HashSet<Integer> lcaNodes = new HashSet<>();
//            System.out.println("get batch LCA: " + solver.getBatchLCA(queries));
            List<Integer> s = solver.getBatchLCA(queries);
            System.out.println(s.get(1));

        }

        public static LowestCommonAncestorAlgorithm<Integer> createSolverBinaryLiftingLcaFinder(Graph<Integer, DefaultEdge> tree, Integer root) {
            return new BinaryLiftingLCAFinder<>(tree, root);
        }


        public static LowestCommonAncestorAlgorithm<Integer> createSolverEulerTourRMQLCAFinder(Graph<Integer, DefaultEdge> tree, Integer root) {
                return new EulerTourRMQLCAFinder<>(tree, root);
        }

        public  static LowestCommonAncestorAlgorithm<Integer> createSolverTarjanLCAFinder(Graph<Integer, DefaultEdge> tree, Integer root) {
            return new TarjanLCAFinder<>(tree, root);
        }

        public static LowestCommonAncestorAlgorithm<Integer> createSolverHeavyPathLCAFinder(Graph<Integer, DefaultEdge> tree, Integer root) {
            return new HeavyPathLCAFinder<>(tree, root);
        }

        public static LowestCommonAncestorAlgorithm<Integer> createSolverBinaryLiftingLCAFinder(Graph<Integer, DefaultEdge> tree, Set<Integer> roots) {
                return new BinaryLiftingLCAFinder<>(tree, roots);
        }

        public static LowestCommonAncestorAlgorithm<Integer> createSolverEulerTourRMQLCAFinderForest(Graph<Integer, DefaultEdge> tree, Set<Integer> roots) {
                return new EulerTourRMQLCAFinder<>(tree, roots);
        }

        public static LowestCommonAncestorAlgorithm<Integer> createSolverTarjanLCAFinderForest(Graph<Integer, DefaultEdge> tree, Set<Integer> roots) {
                return new TarjanLCAFinder<>(tree, roots);
        }

        public static LowestCommonAncestorAlgorithm<Integer> createSolverHeavyPathLCAFinderForest(Graph<Integer, DefaultEdge> tree, Set<Integer> roots) {
                return new HeavyPathLCAFinder<>(tree, roots);
        }

}

