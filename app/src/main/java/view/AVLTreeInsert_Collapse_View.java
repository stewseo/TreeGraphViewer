package view;

import AVLTree.AVLTree;
import AVLTree.AVLTreeUtil;
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
import javax.xml.transform.Transformer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


public class AVLTreeInsert_Collapse_View extends JApplet {


        public Forest<String,Integer> graph;

        Supplier<DirectedGraph<String,Integer>> graphFactory =
                DirectedSparseMultigraph::new;


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

        static AVLTree<String> avlTree = new AVLTree<String>();

        AVLTreeUtil avlTreeUtil;

        public AVLTreeInsert_Collapse_View() {
            //Load initial hardcoded height balanced vertices and edges
            avlTreeUtil = new AVLTreeUtil();
            avlTree = avlTreeUtil.createTestTree();
            graph = avlTreeUtil.createVerticesEdges();
            Collection<Integer> defaultEdges = graph.getEdges();
            Collection<String> defaultVertices = graph.getVertices();

            //create layout using x,y coordinates
            layout = createTreeLayout(graph);
            collapser = new TreeCollapser();
            //a map view, using polar ooordinates. Each ring will represent level of height
            radialLayout = createRadialLayout(graph);

            vv = createVisualizationViewer(layout, radialLayout, graph);
            Container content = getContentPane();
            final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);
            System.out.println(vv.getSize());
            content.add(panel);

            final DefaultModalGraphMouse<String, Integer> graphMouse
                    = new DefaultModalGraphMouse<String, Integer>();

            //MouseListener, MouseMotionListener, and MouseWheelListener
            //coverage of mouse clicks is set by x,y coordinates or layout
            vv.setGraphMouse(graphMouse);

            JComboBox<?> modeBox = graphMouse.getModeComboBox();
            modeBox.addItemListener(graphMouse.getModeListener());
            graphMouse.setMode(ModalGraphMouse.Mode.TRANSFORMING);


            final ScalingControl scaler = new CrossoverScalingControl();

            //Display a height balanced AVL tree after a random value has been added. Traversal to a valid insertion location uses it's super
            //addValue method. A balance check, by level and height is initiated after each insert or delete operation.
            // A radial and tree graph, before and after a rotation, will be displayed. Bfs and Dfs inorder traversals are printed.
            // Predecessor, Successor and height are the toString() diagnostics
            JButton insert = new JButton("insert");
            insert.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Stack<List<String>>stack = new Stack<>();
                    stack.push(Arrays.stream(avlTree.getBFS()).collect(Collectors.toList()));
                    // Initialize a reference to the balance checked return node
                    AVLTree.Node<String> nodeToAdd = getAvlTree().addValue(avlTreeUtil.getRandom());
                    try {
                        if (nodeToAdd.id.hashCode() == 0) {
                            throw new NullPointerException("Invalid value generated for node key");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    graph = new DelegateForest<>();

                    // Create an edge for each child using BST's lookup, get(nodeId) = node.
                    // The current inorder traversal. The most recent version of this tree, post height-balancing rotation
                    Arrays.stream(avlTree.getDFS(BinarySearchTree.DepthFirstSearchOrder.preOrder)).forEach(nodeId -> {
                        AVLTree.Node<String> current = avlTree.getNode(nodeId);

                        if (current.lesser != null && current.lesser.id.equals(avlTree.getNode(nodeId).lesser.id)) {
                            int edge = current.id.toString().concat(current.lesser.id.toString()).hashCode();
                            graph.addEdge(edge, current.id.toString(), current.lesser.id.toString());
                        }

                        if (current.greater != null && current.greater.id.equals(avlTree.getNode(nodeId).greater.id)) {
                            int edge = current.id.toString().concat(current.greater.id.toString()).hashCode();
                            graph.addEdge(edge, current.id.toString(), current.greater.id.toString());
                        }
                    });

                    if(avlTree.getBalance() == AVLTree.Balance.LEFT_LEFT ||
                            avlTree.getBalance() == AVLTree.Balance.LEFT_RIGHT || avlTree.getBalance() == AVLTree.Balance.RIGHT_LEFT){
                        System.out.println("Last rotation to balance tree by height: " + avlTree.getBalance() + " balance factor: " + avlTree.getBalanceFactor());
                        stack.peek().forEach(System.out::println);
                        System.out.println(stack.peek().size());
                    }

                    Arrays.stream(avlTree.getBFS()).forEach(System.out::println);
                    System.out.println(avlTree.size());

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
                        content.setVisible(false);
                        content.setVisible(true);
                        JFrame frame = new JFrame();
                        Container cont = frame.getContentPane();
                        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        frame.pack();
                        frame.setVisible(true);


                    System.out.println("DFS inorder: " + Arrays.toString(avlTree.getDFS(BinarySearchTree.DepthFirstSearchOrder.inOrder)));
                    System.out.println("Forst<V,E>: " + graph.toString());
                    System.out.println("BFS levelorder: " + Arrays.toString(avlTree.getBFS()));
                    System.out.println("Vertices " + graph.getVertices());
                }
            });

            //collapse selected nodes
            JButton collapse = new JButton("Collapse");
            // when mode = PICKING, selected items with update fireItemStateChanged:
            // ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED, mode, ItemEvent.SELECTED));
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

            JButton plus = buildPlusButton(scaler);
            JButton minus = buildMinusButton(scaler);
            JToggleButton radial = buildRadialButton(scaler);
            JToggleButton rotation = buildRotationButton(scaler);
            JPanel scaleGrid = new JPanel(new GridLayout(1, 0));
            scaleGrid.setBorder(BorderFactory.createTitledBorder("Zoom"));

                JPanel controls = new JPanel();
                scaleGrid.add(plus);
                scaleGrid.add(minus);
                controls.add(rotation);
                controls.add(insert);
                controls.add(radial);
                controls.add(scaleGrid);
                controls.add(modeBox);
                controls.add(collapse);
                controls.add(expand);
                content.add(controls, BorderLayout.SOUTH);
            }


    private TreeLayout<String, Integer> createTreeLayout(Forest<String, Integer> graph) {
            return new TreeLayout<String,Integer>(graph);
    }

    private RadialTreeLayout<String, Integer> createRadialLayout(Forest<String, Integer> graph) {
            return new RadialTreeLayout<String, Integer>(graph);
    }

    private VisualizationViewer<String,Integer> createVisualizationViewer(
            TreeLayout<String, Integer> layout,
            RadialTreeLayout<String, Integer> radial,
            Forest<String, Integer> graph){
        vv =  new VisualizationViewer<String,Integer>(layout, new Dimension(600,600));
        vv.setBackground(Color.white);
        vv.getRenderContext().setEdgeShapeTransformer(EdgeShape.line(this.graph));

        vv.getRenderContext().setVertexFillPaintTransformer(Functions.constant(Color.magenta));

        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        vv.getRenderContext().setVertexShapeTransformer(new ClusterVertexShapeFunction<String>());
        // add a listener for ToolTips
        vv.setVertexToolTipTransformer(new ToStringLabeller());
        vv.getRenderContext().setArrowFillPaintTransformer(Functions.<Paint>constant(Color.lightGray));
        radialLayout.setSize(new Dimension(600,600));
        rings = new Rings();
        return vv;
    }

    private JButton buildPlusButton(ScalingControl scaler) {
        JButton plus = new JButton("+");
        plus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaler.scale(vv, 1.1f, vv.getCenter());
            }
        });
        return plus;
    }
    private JButton buildMinusButton(ScalingControl scaler){
        JButton minus = new JButton("-");
        minus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaler.scale(vv, 1 / 1.1f, vv.getCenter());
            }
        });
        return minus;
    }
    private JToggleButton buildRadialButton(ScalingControl scaler) {
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
        return radial;
    }
    private JToggleButton buildRotationButton(ScalingControl scaler) {
        JToggleButton rotation = new JToggleButton("last rotation");
        rotation.addItemListener(new ItemListener() {
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
        return rotation;
    }

    public AVLTree<String> getAvlTree(){
            return avlTree;
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

        public boolean useTransform() {return true;}
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


