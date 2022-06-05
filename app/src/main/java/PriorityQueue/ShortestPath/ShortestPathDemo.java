package PriorityQueue.ShortestPath;

import org.jgrapht.alg.interfaces.AStarAdmissibleHeuristic;
import org.jgrapht.alg.interfaces.ManyToManyShortestPathsAlgorithm;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.*;
import org.jgrapht.generate.GnpRandomGraphGenerator;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.jgrapht.util.CollectionUtil;
import org.jgrapht.util.SupplierUtil;

import java.util.Collections;
import java.util.Random;
import java.util.Set;

public class ShortestPathDemo {
        private static final long SEED = 19l;
        private static Random rnd = new Random(SEED);
        private static int numberOfLandmarks = 2;

        public static void main(String[] args) {
            org.jgrapht.Graph<Integer, DefaultWeightedEdge> graph = generateGraph();

            Set<Integer> landmarks = selectRandomLandmarks(graph, numberOfLandmarks);

            // create heuristic for the algorithm
            AStarAdmissibleHeuristic<Integer> heuristic = new ALTAdmissibleHeuristic<>(graph, landmarks);
            System.out.println("edge set : "+graph.edgeSet());
            System.out.println("number of generated edges  : "+graph.edgeSet().size());
            System.out.println("vertex set: "+graph.vertexSet());
            System.out.println("number of vertices   : "+graph.vertexSet().size());
            ShortestPathAlgorithm<Integer, DefaultWeightedEdge> shortestPath
                    = new BidirectionalAStarShortestPath<>(graph, heuristic);
            System.out.println("Shortest path between vertices 1 and 55:");
            System.out.println(shortestPath.getPath(1, 55));

            ContractionHierarchyPrecomputation<Integer, DefaultWeightedEdge> precomputation
                    = new ContractionHierarchyPrecomputation<>(graph);
            ContractionHierarchyPrecomputation.ContractionHierarchy<Integer, DefaultWeightedEdge> hierarchy
                    = precomputation.computeContractionHierarchy();

            ShortestPathAlgorithm<Integer, DefaultWeightedEdge> chDijkstra
                    = new ContractionHierarchyBidirectionalDijkstra<>(hierarchy);
            System.out.println("Shortest path computed by ContractionHierarchyBidirectionalDijkstra between vertices 1 and 55:");
            System.out.println(chDijkstra.getPath(1, 55));

            // it is possible to supply an already computed hierarchy to the CHManyToManyShortestPaths algorithm
            ManyToManyShortestPathsAlgorithm<Integer, DefaultWeightedEdge> chManyToMany
                    = new CHManyToManyShortestPaths<>(hierarchy);
            ManyToManyShortestPathsAlgorithm.ManyToManyShortestPaths<Integer, DefaultWeightedEdge> manyToManyShortestPaths
                    = chManyToMany.getManyToManyPaths(Collections.singleton(1), Collections.singleton(55));
            System.out.println("Shortest path computed by CHManyToManyShortestPaths between vertices 1 and 55:");
            System.out.println(manyToManyShortestPaths.getPath(1, 55));
        }


        private static Set<Integer> selectRandomLandmarks(org.jgrapht.Graph<Integer, DefaultWeightedEdge> graph, int numberOfLandmarks) {
            Object[] vertices = graph.vertexSet().toArray();

            Set<Integer> result = CollectionUtil.newHashSetWithExpectedSize(numberOfLandmarks);
            while (result.size() < numberOfLandmarks) {
                int position = rnd.nextInt(vertices.length);
                Integer vertex = (Integer) vertices[position];
                result.add(vertex);
            }

            return result;
        }


        private static org.jgrapht.Graph<Integer, DefaultWeightedEdge> generateGraph() {
            DirectedWeightedMultigraph<Integer, DefaultWeightedEdge> graph = new DirectedWeightedMultigraph<>(DefaultWeightedEdge.class);
            graph.setVertexSupplier(SupplierUtil.createIntegerSupplier());

            GnpRandomGraphGenerator<Integer, DefaultWeightedEdge> generator = new GnpRandomGraphGenerator<>(100, 0.5);
            generator.generateGraph(graph);

            for (DefaultWeightedEdge edge : graph.edgeSet()) {
                graph.setEdgeWeight(edge, rnd.nextDouble());
            }
            return graph;
        }
    }

