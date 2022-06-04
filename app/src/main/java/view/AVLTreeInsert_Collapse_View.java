package view;

import AVLTree.AVLTree;
import BinaryTree.BinarySearchTree;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Supplier;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.PolarPoint;
import edu.uci.ics.jung.algorithms.layout.RadialTreeLayout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.EllipseVertexShapeTransformer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.subLayout.TreeCollapser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.*;


public class AVLTreeInsert_Collapse_View extends JApplet {


        public Forest<String,Integer> graph;

        Supplier<DirectedGraph<String,Integer>> graphFactory =
                DirectedSparseMultigraph::new;

        Supplier<Tree<String,Integer>> treeFactory =
                new Supplier<Tree<String,Integer>> () {

                    public Tree<String, Integer> get() {
                        return new DelegateTree<String,Integer>(graphFactory);
                    }
                };

        Supplier<Integer> edgeFactory = new Supplier<Integer>() {
            int i=0;
            public Integer get() {
                return i++;
            }};

        Supplier<String> vertexFactory = new Supplier<String>() {
            int i=0;
            public String get() {
                return "V"+i++;
            }};


        VisualizationViewer<String,Integer> vv;

        VisualizationServer.Paintable rings;

        String root;

        public TreeLayout<String,Integer> layout;
        public FRLayout<?, ?> layout1;

        TreeCollapser collapser;

        RadialTreeLayout<String,Integer> radialLayout;

        static AVLTree<String> avlTree = new AVLTree<String>();;

        private VisualizationViewer<String,Integer> createVisualizationViewer(
                TreeLayout<String, Integer> layout,
                RadialTreeLayout<String, Integer> radial,
                Forest<String, Integer> graph){
            vv =  new VisualizationViewer<String,Integer>(layout, new Dimension(600,600));
            vv.setBackground(Color.white);
            vv.getRenderContext().setEdgeShapeTransformer(EdgeShape.line(this.graph));

            vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
            vv.getRenderContext().setVertexShapeTransformer(new ClusterVertexShapeFunction<String>());
            // add a listener for ToolTips
            vv.setVertexToolTipTransformer(new ToStringLabeller());
            vv.getRenderContext().setArrowFillPaintTransformer(Functions.<Paint>constant(Color.lightGray));
            radialLayout.setSize(new Dimension(600,600));
            rings = new Rings();
            return vv;
        }

        private Forest<String, Integer> createGraph(Collection<Integer> defaultEdges, Collection<String> defaultVertices) {
            return new DelegateForest<String,Integer>();
        }
        public AVLTreeInsert_Collapse_View() {
            graph = createVerticesEdges();
            //Load hardcoded vertices and edges in AVL tree format
            Collection<Integer> defaultEdges = graph.getEdges();
            Collection<String> defaultVertices = graph.getVertices();
            System.out.println(defaultVertices);
            System.out.println(defaultEdges);
            //Initialize graph with valid height balanced nodes
            //Initialize x,y layout using graph coordinates.
            layout = createTreeLayout(graph);
            //the collapse button on will perform split and merge subtree operations
            collapser = new TreeCollapser();
            //radial map view, using  ooordinates. Each ring will represent level of height
            radialLayout = createRadialLayout(graph);

            vv = createVisualizationViewer(layout, radialLayout, graph);

            Container content = getContentPane();
            //set position, dimension for horizontal and vertical scrollbars
            final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);
            content.add(panel);

            final DefaultModalGraphMouse<String, Integer> graphMouse
                    = new DefaultModalGraphMouse<String, Integer>();

            //MouseListener, MouseMotionListener, and MouseWheelListener
            //sub matrix coverage of mouse clicks is set by x,y coordinates or layout
            vv.setGraphMouse(graphMouse);

            JComboBox<?> modeBox = graphMouse.getModeComboBox();
            modeBox.addItemListener(graphMouse.getModeListener());
            graphMouse.setMode(ModalGraphMouse.Mode.TRANSFORMING);

            final ScalingControl scaler = new CrossoverScalingControl();

            JButton plus = new JButton("+");
            plus.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    scaler.scale(vv, 1.1f, vv.getCenter());
                }
            });
            JButton minus = new JButton("-");
            minus.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    scaler.scale(vv, 1 / 1.1f, vv.getCenter());
                }
            });

            JToggleButton radial = new JToggleButton("Radial");
            radial.addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        vv.setGraphLayout(radialLayout);
                        vv.getRenderContext().getMultiLayerTransformer().setToIdentity();
                        vv.addPreRenderPaintable(rings);
                    } else {
                        vv.setGraphLayout(layout);
                        vv.getRenderContext().getMultiLayerTransformer().setToIdentity();
                        vv.removePreRenderPaintable(rings);
                    }
                    vv.repaint();
                }
            });

            //Display a height balanced AVL tree after a random value has been added. Traversal to a valid BST insertion location using it's super class'
            //addValue method is executed. A balance check, by level and height is initiated after each insert or delete operation.
            // The before sub tree rotation and after rotation result will be displayed
            JButton insert = new JButton("insert");

            insert.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {

                    graph = new DelegateForest<String, Integer>();

                    int leftLimit = 48, rightLimit = 122, targetStringLength = 3;
                    Random random = new Random();
                    // Random generator for alpha numeric Strings, limited to 3 characters per String.
                    String randomGenerated = random.ints(leftLimit, rightLimit + 1)
                            .filter(i -> (i <= 57 || i >= 77) && (i <= 90 || i >= 110))
                            .limit(targetStringLength)
                            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                            .toString();

                    System.out.println("generated value: " + randomGenerated);
                    // Initialize a reference to the balance checked return node
                    AVLTree.Node<String> nodeToAdd = getAvlTree().addValue(randomGenerated);
                    // Initialize a new Forest<V, E> object
//                    layout.setGraph(graph);
//                    radialLayout.setGraph(graph);
                    try {
                        if (nodeToAdd.id.hashCode() == 0) {
                            throw new NullPointerException("Invalid value generated for node key");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    System.out.println(nodeToAdd.toString());

                    // Add edges in order, by key, and fields lesser, greater ( left child, right child)
                    Arrays.stream(avlTree.getDFS(BinarySearchTree.DepthFirstSearchOrder.inOrder)).forEach(key -> {
                        AVLTree.Node<String> nodeInOrder = avlTree.getNode(key);
                        if (nodeInOrder.greater != null) {
                            graph.addEdge(edgeFactory.get(), nodeInOrder.id.toString(), nodeInOrder.greater.id.toString());
                        }
                        if (nodeInOrder.lesser != null) {
                            graph.addEdge(edgeFactory.get(), nodeInOrder.id.toString(), nodeInOrder.lesser.id.toString());
                        }
                    });

                    int testGraphSize = graph.getVertices().size();
                    content.setVisible(false);
                    content.remove(vv);
                    layout = createTreeLayout(graph);
                    collapser = new TreeCollapser();
                    radialLayout = createRadialLayout(graph);
                    vv = createVisualizationViewer(layout, radialLayout, graph);
                    Container content = getContentPane();
                    final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);
                    content.add(panel);
                    JPanel scaleGrid = new JPanel(new GridLayout(1, 0));
                    scaleGrid.setBorder(BorderFactory.createTitledBorder("Zoom"));
                    content.setVisible(true);
//                    JFrame frame = new JFrame();
//                    Container content = frame.getContentPane();

//                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//                    frame.pack();
//                    frame.setVisible(true);

                    System.out.println("DFS inorder: " + Arrays.toString(avlTree.getDFS(BinarySearchTree.DepthFirstSearchOrder.inOrder)));
                    System.out.println("Forst<V,E>: " + graph.toString());
                    System.out.println("BFS levelorder: " + Arrays.toString(avlTree.getBFS()));
                    System.out.println("Vertices " + graph.getVertices());

                }
            });

//                    vv.getRenderContext().setEdgeShapeTransformer(EdgeShape.orthogonal(graph));
//                    vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
//                    vv.getRenderContext().setVertexShapeTransformer(new ClusterVertexShapeFunction<String>());


            JButton collapse = new JButton("Collapse");

            collapse.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    Collection<String> picked
                            = new HashSet<String>(vv.getPickedVertexState().getPicked());
                    if (picked.size() == 1) {
                        Object root = picked.iterator().next();
                        Forest<String, Integer> inGraph = (Forest<String, Integer>) layout.getGraph();

                        try {
                            collapser.collapse(vv.getGraphLayout(), inGraph, root);
                        } catch (InstantiationException | IllegalAccessException e1) {
                            e1.printStackTrace();
                        }

                        vv.getPickedVertexState().clear();
                        vv.repaint();
                    }
                }
            });

            JButton expand = new JButton("Expand");
            expand.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    Collection<String> picked = vv.getPickedVertexState().getPicked();
                    for (Object v : picked) {
                        if (v instanceof Forest) {
                            Forest<String, Integer> inGraph = (Forest<String, Integer>) layout.getGraph();

                            collapser.expand(inGraph, (Forest<?, ?>) v);
                        }
                        vv.getPickedVertexState().clear();
                        vv.repaint();
                    }
                }
            });
//            updateContent();
                JPanel scaleGrid = new JPanel(new GridLayout(1, 0));
                scaleGrid.setBorder(BorderFactory.createTitledBorder("Zoom"));

                JPanel controls = new JPanel();
                scaleGrid.add(plus);
                scaleGrid.add(minus);
                controls.add(insert);
                controls.add(radial);
                controls.add(scaleGrid);
                controls.add(modeBox);
                controls.add(collapse);
                controls.add(expand);
                content.add(controls, BorderLayout.SOUTH);
            }

//            public void updateContent(){
//                JPanel scaleGrid = new JPanel(new GridLayout(1, 0));
//                scaleGrid.setBorder(BorderFactory.createTitledBorder("Zoom"));
//
//                JPanel controls = new JPanel();
//                scaleGrid.add(plus);
//                scaleGrid.add(minus);
//                controls.add(insert);
//                controls.add(radial);
//                controls.add(scaleGrid);
//                controls.add(get);
//                controls.add(getCollapse);
//                controls.add(expand);
//                content.add(controls, BorderLayout.SOUTH);
//            }
    private TreeLayout<String, Integer> createTreeLayout(Forest<String, Integer> graph) {
            return new TreeLayout<String,Integer>(graph);
    }

    private RadialTreeLayout<String, Integer> createRadialLayout(Forest<String, Integer> graph) {
            return new RadialTreeLayout<String, Integer>(graph);
    }


    public AVLTree<String> getAvlTree(){
            return avlTree;
        }

        private Forest<String, Integer> createVerticesEdges() {

            avlTree  = new AVLTree<String>();
            graph = new DelegateForest<String,Integer>();
            AVLTree.AVLNode<String> node = (AVLTree.AVLNode<String>) avlTree.addValue("V11");
            System.out.println("node: " + node.id  + " " + node.toString());

            AVLTree.AVLNode<String> node2 = (AVLTree.AVLNode<String>) avlTree.addValue("V22");
            System.out.println("node2: " + node2.id  + " " + node2.toString());

            AVLTree.AVLNode<String> node3 = (AVLTree.AVLNode<String>) avlTree.addValue("V33");
            System.out.println("node3: " + node3.id  + " " + node3.toString());

            AVLTree.AVLNode<String> node4 = (AVLTree.AVLNode<String>) avlTree.addValue("V44");
            System.out.println("node4: " + node4.id  + " " + node4.toString());
            System.out.println(avlTree.toString());
            AVLTree.AVLNode<String> node5 = (AVLTree.AVLNode<String>) avlTree.addValue("V55");
            System.out.println("node5: " + node5.id  + " " + node5.toString());

            AVLTree.AVLNode<String> node6 = (AVLTree.AVLNode<String>) avlTree.addValue("V66");
            System.out.println("node6: " + node6.id  + " " + node6.toString());

            AVLTree.AVLNode<String> node7 = (AVLTree.AVLNode<String>) avlTree.addValue("V77");
            System.out.println("node7: " + node7.id  + " " + node7.toString());
            AVLTree.AVLNode<String> node8 = (AVLTree.AVLNode<String>) avlTree.addValue("V88");

            AVLTree.AVLNode<String> node9 = (AVLTree.AVLNode<String>) avlTree.addValue("V99");
            System.out.println("node9: " + node9.id  + " " + node9.toString());

            AVLTree.AVLNode<String> node10 = (AVLTree.AVLNode<String>) avlTree.addValue("v100");
            System.out.println("node10: " + node10.id  + " " + node10.toString());

            System.out.println("BFS: " + Arrays.toString(avlTree.getBFS()));
            System.out.println(avlTree.toString());

            System.out.println("DFS Preorder: " + Arrays.toString(avlTree.getDFS(BinarySearchTree.DepthFirstSearchOrder.inOrder)));

//            graph.addVertex(node.getValue());
            Stack<AVLTree.AVLNode<String>> stack = new Stack<>();

            graph.addVertex("V44");
            graph.addEdge("V44V22".hashCode(), "V44", "V22");
            graph.addEdge("V44V88".hashCode(), "V44", "V88");
            graph.addEdge("V22V11".hashCode(), "V22", "V11");
            graph.addEdge("V22V33".hashCode(), "V22", "V33");
            graph.addEdge("V88V66".hashCode(), "V88", "V66");
            graph.addEdge("V88V99".hashCode(), "V88", "V99");
            graph.addEdge("V66V55".hashCode(), "V66", "V55");
            graph.addEdge("V66V77".hashCode(), "V66", "V77");
            graph.addEdge("V99V100".hashCode(), "V99", "V100");
            System.out.println("edges " +graph.getEdges());
            System.out.println("v "+graph.getVertices().size());
            return graph;
        }
        class Rings implements VisualizationServer.Paintable {

            Collection<Double> depths;

            public Rings() {
                depths = getDepths();
            }

            private Collection<Double> getDepths() {
                Set<Double> depths = new HashSet<Double>();
                Map<String, PolarPoint> polarLocations = radialLayout.getPolarLocations();
                for(String v : graph.getVertices()) {
                    PolarPoint pp = polarLocations.get(v);
                    depths.add(pp.getRadius());
                }
                return depths;
            }

            public void paint(Graphics g) {
                g.setColor(Color.lightGray);

                Graphics2D g2d = (Graphics2D)g;
                Point2D center = radialLayout.getCenter();

                Ellipse2D ellipse = new Ellipse2D.Double();
                for(double d : depths) {
                    ellipse.setFrameFromDiagonal(center.getX()-d, center.getY()-d,
                            center.getX()+d, center.getY()+d);
                    Shape shape = vv.getRenderContext().
                            getMultiLayerTransformer().getTransformer(Layer.LAYOUT).transform(ellipse);
                    g2d.draw(shape);
                }
            }

            public boolean useTransform() {
                return true;
            }
        }


        private void createTree() {
            graph.addVertex("V0");
            graph.addEdge(edgeFactory.get(), "V0", "V1");
            graph.addEdge(edgeFactory.get(), "V0", "V2");
            graph.addEdge(edgeFactory.get(), "V1", "V4");
            graph.addEdge(edgeFactory.get(), "V2", "V3");
            graph.addEdge(edgeFactory.get(), "V2", "V5");
            graph.addEdge(edgeFactory.get(), "V4", "V6");
            graph.addEdge(edgeFactory.get(), "V4", "V7");
            graph.addEdge(edgeFactory.get(), "V3", "V8");
            graph.addEdge(edgeFactory.get(), "V6", "V9");
            graph.addEdge(edgeFactory.get(), "V4", "V10");

            graph.addVertex("A0");
            graph.addEdge(edgeFactory.get(), "A0", "A1");
            graph.addEdge(edgeFactory.get(), "A0", "A2");
            graph.addEdge(edgeFactory.get(), "A0", "A3");

            graph.addVertex("B0");
            graph.addEdge(edgeFactory.get(), "B0", "B1");
            graph.addEdge(edgeFactory.get(), "B0", "B2");
            graph.addEdge(edgeFactory.get(), "B1", "B4");
            graph.addEdge(edgeFactory.get(), "B2", "B3");
            graph.addEdge(edgeFactory.get(), "B2", "B5");
            graph.addEdge(edgeFactory.get(), "B4", "B6");
            graph.addEdge(edgeFactory.get(), "B4", "B7");
            graph.addEdge(edgeFactory.get(), "B3", "B8");
            graph.addEdge(edgeFactory.get(), "B6", "B9");

        }

        class ClusterVertexShapeFunction<V> extends EllipseVertexShapeTransformer<V> {

            ClusterVertexShapeFunction() {
                setSizeTransformer(new ClusterVertexSizeFunction<V>(20));
            }
            @Override
            public Shape apply(V v) {
                if(v instanceof Graph) {
                    @SuppressWarnings("rawtypes")
                    int size = ((Graph)v).getVertexCount();
                    if (size < 8) {
                        int sides = Math.max(size, 3);
                        return factory.getRegularPolygon(v, sides);
                    }
                    else {
                        return factory.getRegularStar(v, size);
                    }
                }
                return super.apply(v);
            }
        }


        static class ClusterVertexSizeFunction<V> implements Function<V,Integer> {
            int size;
            public ClusterVertexSizeFunction(Integer size) {
                this.size = size;
            }

            public Integer apply(V v) {
                if(v instanceof Graph) {
                    return 30;
                }
                return size;
            }
        }

        public static void main(String[] args) {
            JFrame frame = new JFrame();
            Container content = frame.getContentPane();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            content.add(new AVLTreeInsert_Collapse_View());
            frame.pack();
            frame.setVisible(true);
        }
    }


