package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.*;
import java.util.Timer;
import java.util.stream.Collectors;
import javax.swing.*;

import AVLTree.AVLTree;
import AVLTree.AVLTreeUtil;
import edu.uci.ics.jung.graph.Forest;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultGraphType;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.jungrapht.samples.util.LayoutFunction;
import org.jungrapht.visualization.VisualizationViewer;
import org.jungrapht.visualization.control.DefaultModalGraphMouse;
import org.jungrapht.visualization.layout.algorithms.*;
import org.jungrapht.visualization.renderers.Renderer;
import org.jungrapht.visualization.util.LayoutAlgorithmTransition;

public class AVLRebalanceTest extends JPanel {


        private static final int MAX_VERTICES = 30;

        private static Graph<String, Integer> graph;

        private VisualizationViewer<String, Integer> vv;

        private LayoutAlgorithm<String> layoutAlgorithm;

        private Timer timer;

        static AVLTree<String> avlTree;

        static ArrayDeque<String> queue;

        private AVLRebalanceTest() {
            AVLTreeUtil avlTreeUtil = new AVLTreeUtil();

            avlTree = new AVLTree<String>();

            avlTree = avlTreeUtil.createTestTree();

            queue = new ArrayDeque<>();
            queue.addAll(Arrays.stream(avlTree.getLevelOrder()).collect(Collectors.toSet()));
//            for(int i = 0; i<50;i++){
//                queue.addAll(Arrays.stream(avlTree.getLevelOrder()).collect(Collectors.toSet()));
//            }

            graph = GraphTypeBuilder.<String, Integer>forGraphType(DefaultGraphType.directedSimple()).buildGraph();

            layoutAlgorithm = new TreeLayoutAlgorithm<>();

            LayoutAlgorithm<String> EdgeAwareTreeLayoutAlgorithm = new EdgeAwareTreeLayoutAlgorithm<>();

            vv = VisualizationViewer.builder(graph)
                            .graphMouse(new DefaultModalGraphMouse<>())
                            .layoutAlgorithm(EdgeAwareTreeLayoutAlgorithm)
                            .viewSize(new Dimension(800, 800))
                            .build();

            this.setLayout(new BorderLayout());
            this.setBackground(java.awt.Color.lightGray);
            this.setFont(new Font("Serif", Font.PLAIN, 12));

            vv.getRenderContext().setVertexLabelPosition(Renderer.VertexLabel.Position.CNTR);
            vv.getRenderContext().setVertexLabelFunction(String::toString);
            vv.setForeground(Color.white);

            this.add(vv.getComponent());

            final JRadioButton animateLayoutTransition = new JRadioButton("Animate Layout Transition");

            LayoutFunction<String> layoutFunction =
                    new LayoutFunction<String>(
                            LayoutFunction.Layout.of("Tree Layout", TreeLayoutAlgorithm.<String>builder()),
                            LayoutFunction.Layout.of("Multi Row Tree", MultiRowEdgeAwareTreeLayoutAlgorithm.<String>builder()),
                            LayoutFunction.Layout.of("Mutli Row Edge Aware Tree", MultiRowTreeLayoutAlgorithm.<String>builder()));


            final JComboBox<Object> graphChooser = new JComboBox<>(layoutFunction.getNames().toArray());
            graphChooser.addActionListener(
                    e ->
                            SwingUtilities.invokeLater(
                                    () -> {
                                        LayoutAlgorithm.Builder<String, ?, ?> builder = layoutFunction.apply((String) graphChooser.getSelectedItem());
                                        layoutAlgorithm = builder.build();
                                        if (animateLayoutTransition.isSelected()) {
                                            LayoutAlgorithmTransition.animate(vv, layoutAlgorithm);
                                        } else {
                                            LayoutAlgorithmTransition.apply(vv, layoutAlgorithm);
                                        }
                                    }));
            graphChooser.setSelectedIndex(0);

            JPanel southPanel = new JPanel(new GridLayout(1, 2));
            southPanel.setBorder(BorderFactory.createTitledBorder("Layout Functions"));
            southPanel.add(animateLayoutTransition);
            southPanel.add(graphChooser);
            this.add(southPanel, BorderLayout.SOUTH);

            timer = new Timer();
            timer.schedule(new AddRemoveTask(this::processAdd), 1000, 1000); //subsequent rate
            vv.repaint();
        }
        static int i = 0;
        private void processAdd() {
            AVLTreeUtil avlTreeUtil = new AVLTreeUtil();

            vv.getRenderContext().getSelectedVertexState().clear();
            vv.getRenderContext().getSelectedEdgeState().clear();

            try {
                if(i++ < 1000) {

                    AVLTree.AVLNode<String> node = (AVLTree.AVLNode<String>) avlTree.addValue(avlTreeUtil.getRandom());

                    SwingUtilities.invokeLater(() -> {

//                        System.out.println("adding: " + node.id.toString());
//                        System.out.println("Successor: " + avlTree.getSuccessor(node).id);
//                        System.out.println("Balance: " + avlTree.getBalance());
//                        System.out.println("Balance Factor: " + avlTree.getBalanceFactor());
//                        System.out.println("Height: " + node.getHeight());
//                        System.out.println("in Order from this node: " + Arrays.toString(BinarySearchTree.getDFS(BinarySearchTree.DepthFirstSearchOrder.inOrder, node, avlTree.size())));
//                        System.out.println(avlTree);


                        if(node.lesser != null && !graph.containsVertex(node.lesser.id.toString())) {
                            if(!graph.containsVertex(node.id.toString())) {
                                graph.addVertex(node.id.toString());
                            }
                            if(!graph.containsVertex(node.lesser.id.toString())) {
                                graph.addVertex(node.lesser.id.toString());
                                graph.addEdge(node.id.toString(), node.lesser.id.toString(), (node.id.toString() + node.lesser.id.toString()).hashCode());
                            }
                        }
                        if(node.lesser != null && !graph.containsVertex(node.greater.id.toString())) {
                            if(!graph.containsVertex(node.id.toString())) {
                                graph.addVertex(node.id.toString());
                            }
                            if(!graph.containsVertex(node.greater.id.toString())) {
                                graph.addVertex(node.greater.id.toString());
                                graph.addEdge(node.id.toString(), node.greater.id.toString(), (node.id.toString() + node.greater.id.toString()).hashCode());
                            }
                        }
                        vv.getVisualizationModel().getLayoutModel().accept(layoutAlgorithm);
                        vv.repaint();
                    });

                } else {
                    System.out.println("test");
                    timer.cancel();
                    timer = new Timer();
                    timer.schedule(new AddRemoveTask(this::processRemove), 1000, 1000); //subsequent rate
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }



    static int j = 10;
        private void processRemove() {
            vv.getRenderContext().getSelectedVertexState().clear();
            vv.getRenderContext().getSelectedEdgeState().clear();
            AVLTreeUtil avlTreeUtil = new AVLTreeUtil();
            AVLTree<String>  avlTree = avlTreeUtil.createTestTree();
            Forest<String, Integer> g = avlTreeUtil.createVerticesEdges();

            try {

                if(i <  avlTree.size()) {
                    String v1 = avlTree.getBFS()[j--];

                    SwingUtilities.invokeLater(
                            () -> {
                                graph.removeVertex(v1);
                                vv.getVisualizationModel().getLayoutModel().accept(layoutAlgorithm);
                                vv.repaint();
                            });

                } else {
                    timer.cancel();
                    timer = new Timer();
                    timer.schedule(new AddRemoveTask(this::processAdd), 1000, 1000); //subsequent rate
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

            static class AddRemoveTask extends TimerTask {
            Runnable runnable;

            AddRemoveTask(Runnable runnable) {
                this.runnable = runnable;
            }

            public void run() {
                runnable.run();
            }
        }

        public static void main(String[] args) {
            AVLRebalanceTest and = new AVLRebalanceTest();
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.getContentPane().add(and);
            frame.pack();
            frame.setVisible(true);
        }
    }

