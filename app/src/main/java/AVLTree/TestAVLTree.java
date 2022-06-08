package AVLTree;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultGraphType;
import org.jgrapht.graph.builder.GraphBuilder;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.jgrapht.util.SupplierUtil;
import org.jungrapht.samples.util.ControlHelpers;
import org.jungrapht.samples.util.TreeLayoutSelector;
import org.jungrapht.visualization.VisualizationModel;
import org.jungrapht.visualization.VisualizationScrollPane;
import org.jungrapht.visualization.VisualizationViewer;
import org.jungrapht.visualization.control.DefaultModalGraphMouse;
import org.jungrapht.visualization.control.modal.Modal;
import org.jungrapht.visualization.control.modal.ModeControls;
import org.jungrapht.visualization.decorators.EdgeShape;
import org.jungrapht.visualization.decorators.PickableElementPaintFunction;
import org.jungrapht.visualization.layout.algorithms.*;
import org.jungrapht.visualization.renderers.Renderer;
import org.jungrapht.visualization.util.AWT;
import org.jungrapht.visualization.util.LayoutPaintable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;
import java.util.function.Function;

import static AVLTree.LayoutHelper.getCombos;

public class TestAVLTree extends JPanel {
    static AVLTree<String> avlTree ;
    static VisualizationModel<String, Integer> visualizationModel;
    private static final Logger log = LoggerFactory.getLogger(TestAVLTree.class);
    static Graph<String, Integer> graph;
    Graph<String, Integer> tree;
    VisualizationViewer<String, Integer> vv;
    int width = 20;
    int height = 20;
    Function<String, Shape> vertexShapeFunction =
            v -> new Ellipse2D.Float(-width / 2.f, -height / 2.f, width, height);

    Dimension preferredSize = new Dimension(300, 300);
    Dimension preferredSizeRect = new Dimension(1100, 300);

    Dimension subLayoutSize;



    AVLTreeUtil avlTreeUtil;

    public TestAVLTree() {

            //instance of an avlTree, virtual root = null.
        avlTree = getAvlTree();

        // add edge v, e. first param will add vertices to test graph
        // hard coded Strings of V,E loaded to graph, in binary tree format.
        //        tree.addEdge( "V44", "V22");
        //        tree.addEdge("V44", "V88");
        //        tree.addEdge( "V22", "V11");
        //        tree.addEdge("V22", "V33");
        //        tree.addEdge( "V88", "V66");
        //        tree.addEdge( "V88", "V99");
        //        tree.addEdge( "V66", "V55");
        //        tree.addEdge( "V66", "V77");
        //        tree.addEdge( "V99", "V100");

        avlTreeUtil = new AVLTreeUtil();
        //Create tree from AVLTree instance
        GraphBuilder<String, Integer, Graph<String, Integer>> testTreeBuilder
                = GraphTypeBuilder.<String, Integer>forGraphType(DefaultGraphType.dag())
                .edgeSupplier(SupplierUtil.createIntegerSupplier())
                .buildGraphBuilder();
        graph = avlTreeUtil.createForest();
        tree = avlTreeUtil.createForest();
        // the actual min and max values of the tree have a hard coded expected result each iteration.
        // expected result, only values greater than the tree max or less than the tree min wil be added.
        tree.vertexSet().forEach(root -> {
            // Create instance of a node to be added using testGraph vertex Set
            AVLTree.AVLNode<String> testAdd;
            if(avlTree.getRoot() == null) {
                //  root.left = newMin;
                //  newMin.parent = virtualRoot;
                testAdd = avlTree.addMin(root);
            }
            else{
                // search(seek?) for a valid location for this value to be added as
                // the root of a new subtree to this tree.
                testAdd = new AVLTree.AVLNode<String>(root);
                //BST add for min/max of Tree ( i dont know what to say now that I cannot use root in this format)
                int maxComparison = root.compareTo(avlTree.getMax().getValue());
                int minComparison = root.compareTo(avlTree.getMin().getValue());

                if (maxComparison >= 0) {
                    avlTree.addMax(root);
                }
                else if (minComparison <= 0) {
                    avlTree.addMin(root);
                }
                else {
                    // If predecessor is null, swap roots
                    // AVLNode<T> t = virtualRoot.left;
                    // this.makeRoot(tree.virtualRoot.left);
                    // tree.makeRoot(t);

                    System.out.println("Full tree: " + avlTree);
                    System.out.println("node adding: " + testAdd);
                    AVLTree<String> left = avlTree.splitBefore(testAdd);
                    System.out.println("left: " + left);

                }
            }

            Iterator<AVLTree.AVLNode<String>> iterator = avlTree.nodeIterator();
//            testIterateByNode(result.get(0), iterator);

            Iterator<String>valueiterator = avlTree.iterator();
//            testiterateByValue(result, valueIterator);

            while(iterator.hasNext()) {
                AVLTree.AVLNode<String> current = iterator.next();
                graph.addVertex(current.getValue());

                if (current.getRight() != null) {
                    testTreeBuilder.addEdge(current.getValue(), current.getRight().getValue());
                }
                if (current.getLeft() != null) {
                    testTreeBuilder.addEdge(current.getValue(), current.getLeft().getValue());
                }
            }
        });
        System.out.println(graph.edgeSet());
        System.out.println(graph.vertexSet());

        setLayout(new BorderLayout());
        final DefaultModalGraphMouse<?, ?> graphMouse = new DefaultModalGraphMouse<>();

        vv = createVisualizationViewer(graphMouse);

        final VisualizationScrollPane panel = new VisualizationScrollPane(vv);
        createPanel(panel, graphMouse);
//        createComboBoxes;
//        createButtons;

    }

    private void createPanel(VisualizationScrollPane panel, DefaultModalGraphMouse<?, ?> graphMouse) {
        panel.add(TreeLayoutSelector.<String, Integer>builder(vv)
                        .vertexShapeFunction(vertexShapeFunction)
                        .build());

        JButton addNode = new JButton("Add Node");
        addNode.addActionListener(e -> addNode());
//        addNode.addActionListener(e -> displayRotation());


        JButton deleteNode = new JButton("Delete Node");
        deleteNode.addActionListener(e -> deleteNode());

        vv.getRenderContext().setEdgeShapeFunction(EdgeShape.articulatedLine());

        vv.getRenderContext()
                .setEdgeDrawPaintFunction(
                        new PickableElementPaintFunction<>(vv.getSelectedEdgeState(), Color.black, Color.red));
        vv.getRenderContext()
                .setVertexFillPaintFunction(
                        new PickableElementPaintFunction<>(
                                vv.getSelectedVertexState(), Color.red, Color.blue));

        // add a listener for ToolTips
        vv.setVertexToolTipFunction(Object::toString);
        VisualizationScrollPane visualizationScrollPane = new VisualizationScrollPane(vv);
        add(visualizationScrollPane);

        JComboBox<?> modeBox = ModeControls.getStandardModeComboBox(Modal.Mode.PICKING, graphMouse);

        JComboBox<LayoutHelper.Layouts> layoutTypeComboBox = new JComboBox<>(getCombos());

        layoutTypeComboBox.setRenderer(
                new DefaultListCellRenderer() {
                    public Component getListCellRendererComponent(
                            JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                        String valueString = value.toString();
                        valueString = valueString.substring(valueString.lastIndexOf('.') + 1);
                        return super.getListCellRendererComponent(
                                list, valueString, index, isSelected, cellHasFocus);
                    }
                });
        layoutTypeComboBox.setSelectedItem(LayoutHelper.Layouts.FR);
        layoutTypeComboBox.addItemListener(
                e -> {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        vv.getVisualizationModel()
                                .getLayoutModel()
                                .accept(((LayoutHelper.Layouts) e.getItem()).getLayoutAlgorithm());
                    }
                });

        JComboBox<LayoutHelper.Layouts> subLayoutTypeComboBox = new JComboBox<LayoutHelper.Layouts>(getCombos());

        subLayoutTypeComboBox.setRenderer(
                new DefaultListCellRenderer() {
                    public Component getListCellRendererComponent(
                            JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                        String valueString = value.toString();
                        valueString = valueString.substring(valueString.lastIndexOf('.') + 1);
                        return super.getListCellRendererComponent(
                                list, valueString, index, isSelected, cellHasFocus);
                    }
                });
        subLayoutTypeComboBox.addItemListener(e -> {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        deleteNode();
                        addNode();
                    }});

        JComboBox<?> subLayoutDimensionComboBox =
                new JComboBox<Object>(
                        new Dimension[]{
                                new Dimension(75, 75),
                                new Dimension(100, 100),
                                new Dimension(150, 150),
                                new Dimension(200, 200),
                                new Dimension(250, 250),
                                new Dimension(300, 300)
                        });
        subLayoutDimensionComboBox.setRenderer(
                new DefaultListCellRenderer() {
                    public Component getListCellRendererComponent(
                            JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                        String valueString = value.toString();
                        valueString = valueString.substring(valueString.lastIndexOf('['));
                        valueString = valueString.replaceAll("idth", "");
                        valueString = valueString.replaceAll("eight", "");
                        return super.getListCellRendererComponent(
                                list, valueString, index, isSelected, cellHasFocus);
                    }
                });
        subLayoutDimensionComboBox.addItemListener(
                e -> {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        subLayoutSize = (Dimension) e.getItem();
                    }
                });
        subLayoutDimensionComboBox.setSelectedIndex(1);
        String instructions = "Split Merge";
        JButton help = new JButton("Split Merge");
        help.addActionListener(
                e ->
                        JOptionPane.showMessageDialog(
                                (JComponent) e.getSource(), instructions, "Help", JOptionPane.PLAIN_MESSAGE));

        Dimension space = new Dimension(20, 20);
        Box controls = Box.createHorizontalBox();
        controls.add(Box.createRigidArea(space));

        JComponent zoomControls =
                ControlHelpers.getZoomControls("Scale", vv); //, new GridLayout(0, 1));
        heightConstrain(zoomControls);
        controls.add(zoomControls);
        controls.add(Box.createRigidArea(space));

        JPanel addRemove = new JPanel(new GridLayout(0, 1));
        addRemove.setBorder(BorderFactory.createTitledBorder("Insert Remove"));
        addRemove.add(addNode);
        addRemove.add(deleteNode);
        heightConstrain(addRemove);
        controls.add(addRemove);
        controls.add(Box.createRigidArea(space));

        JPanel layoutControls = new JPanel(new GridLayout(0, 1));
        layoutControls.setBorder(BorderFactory.createTitledBorder("Layout"));
        layoutControls.add(layoutTypeComboBox);
        heightConstrain(layoutControls);
        controls.add(layoutControls);

        JPanel subLayoutControls = new JPanel(new GridLayout(0, 1));
        subLayoutControls.setBorder(BorderFactory.createTitledBorder("SubLayout"));
        subLayoutControls.add(subLayoutTypeComboBox);
        subLayoutControls.add(subLayoutDimensionComboBox);
        heightConstrain(subLayoutControls);
        controls.add(subLayoutControls);
        controls.add(Box.createRigidArea(space));

        JPanel modePanel = new JPanel(new GridLayout(1, 1));
        modePanel.setBorder(BorderFactory.createTitledBorder("Mouse Mode"));
        modePanel.add(modeBox);
        heightConstrain(modePanel);
        controls.add(modePanel);
        controls.add(Box.createRigidArea(space));

        controls.add(help);
        controls.add(Box.createVerticalGlue());
        add(controls, BorderLayout.SOUTH);
    }

    private VisualizationViewer<String, Integer> createVisualizationViewer(DefaultModalGraphMouse<?, ?> graphMouse) {
        vv =
                VisualizationViewer.builder(graph)
                        .layoutAlgorithm(
                                TreeLayoutAlgorithm.<String>builder()
                                        .vertexBoundsFunction(
                                                vertexShapeFunction.andThen(s -> AWT.convert(s.getBounds2D())))
                                        .build())
                        .viewSize(new Dimension(600, 600))
                        .graphMouse(graphMouse)
                        .build();


        vv.getRenderContext().setEdgeShapeFunction(EdgeShape.line());
        vv.getRenderContext().setVertexLabelFunction(Object::toString);
        vv.getRenderContext().setVertexLabelPosition(Renderer.VertexLabel.Position.CNTR);
        vv.getRenderContext().setVertexLabelDrawPaintFunction(n -> Color.white);
        vv.getRenderContext().setVertexShapeFunction(vertexShapeFunction);
        // add a listener for ToolTips
        vv.setVertexToolTipFunction(Object::toString);
        vv.getRenderContext().setArrowFillPaintFunction(n -> Color.lightGray);
        if (log.isTraceEnabled()) {
            vv.addPreRenderPaintable(
                    new LayoutPaintable.LayoutBounds(
                            vv.getVisualizationModel(), vv.getRenderContext().getMultiLayerTransformer()));
        }
        return vv;
    }

    private void testiterateByValue(List<AVLTree.AVLNode<String>> listAvlNodes, Iterator<String> valueIterator) {
       for(int i=0; i<avlTree.getRoot().getSubtreeSize();i++) {
           assert avlTree.iterator().next().equals(listAvlNodes.get(i).getValue());
       }
    }

    private void displayRotation() {
    }

    private void testIterateByNode(AVLTree.AVLNode<String> actual,  Iterator<AVLTree.AVLNode<String>> expected) {
        //test root
        while(expected.hasNext()) {
            AVLTree.AVLNode<String> expectedNode = expected.next();
            assert expectedNode.getHeight() == actual.getHeight();
            assert expectedNode.getSubtreeSize() == actual.getSubtreeSize();
            assert expectedNode.getValue().equals(actual.getValue());

            // max.getLeftChild(newMax);
            // balance(max);
            // node.parent = this;
            // setPredecessor(node.subtreeMax);
            // subtreeMin = node.subtreeMin;
            if (expectedNode.getLeft() != null && actual.getLeft() != null) {
                assert expectedNode.getPredecessor().equals(actual.getPredecessor());
                assert expectedNode.getLeftSubtreeSize() == actual.getLeftSubtreeSize();
                assert expectedNode.getLeftHeight() == actual.getLeftHeight();
                assert expectedNode.getLeft().getValue().equals(actual.getLeft().getValue());
            }
            // max.setRightChild(newMax);
            //balance(max);
            //node.parent = this;
            //setSuccessor(node.getSubtreeMin());
            //subtreeMax = node.getSubtreeMax();
            if (expectedNode.getRight() != null && actual.getRight() != null) {
                assert expectedNode.getSuccessor().equals(actual.getSuccessor());
                assert expectedNode.getRightHeight() == actual.getLeftHeight();
                assert expectedNode.getRightSubtreeSize() == actual.getRightSubtreeSize();
                assert expectedNode.getRight().getValue().equals(actual.getRight().getValue());
            }
        }


    }

//    private ChangeListener checkRotation() {
//        System.out.println("test ");
//        return null;
//    }

    private void deleteNode() {
        delete();
    }

    private void delete() {

    }

    private void addNode() {
        insert();
    }


    private void insert() {
        graph.vertexSet().forEach(System.out::print);
        avlTree = getAvlTree();
        System.out.println(avlTree.getRoot().getLeftSubtreeSize());
        System.out.println(avlTree.getRoot().getRightSubtreeSize());
        Point2D center = new Point2D.Double();
        double x = 0;
        double y = 0;

        AVLTree.AVLNode<String> nodeToAdd = new AVLTree.AVLNode<String>(avlTreeUtil.getRandom());
        AVLTree<String> left = avlTree.splitBefore(nodeToAdd);
        System.out.println(left.getRoot().getSubtreeSize());
        try {
            if (nodeToAdd.value.hashCode() == 0) {
                throw new NullPointerException("Invalid value generated for node key");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

//        Collection<String> vertexSet = graph.vertexSet();
//        Collection<Integer> edgeSet = graph.edgeSet();
//        graph.removeAllEdges(edgeSet);
//        graph.removeAllVertices(vertexSet);


        Iterator<AVLTree.AVLNode<String>> iterator = avlTree.nodeIterator();
        //add each node of the tree to a new graph
        while (iterator.hasNext()) {
            AVLTree.AVLNode<String> current = iterator.next();

            if (current.left != null) {
                if(graph.getEdge(current.getValue(),current.left.getValue()) != null) {
                    graph.addEdge(current.getValue(), current.left.getValue());
                }
            }if (current.right != null) {
                if(graph.getEdge(current.getValue(),current.right.getValue()) != null) {
                    graph.addEdge(current.getValue(), current.right.value.toString());
                }
            }

        }

        frame = new JFrame();
        content = frame.getContentPane();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());
        final DefaultModalGraphMouse<?, ?> graphMouse = new DefaultModalGraphMouse<>();
        vv = createVisualizationViewer(graphMouse);
        final VisualizationScrollPane panel = new VisualizationScrollPane(vv);
        createPanel(panel, graphMouse);

        content.add(demo);
        frame.pack();
        frame.setVisible(true);


//        frame.setVisible(false);


//        frame.setVisible(true);
    }


//        System.out.println("location "+clusteringLayoutModel.getLocations());
//        System.out.println("change support "+clusteringLayoutModel.getViewChangeSupport());
//        System.out.println("graph: "+ clusteringLayoutModel.getGraph());
//        System.out.println("location: " + clusteringLayoutModel.getLocations());
//        System.out.println("height: "+ clusteringLayoutModel.getHeight());
//        System.out.println("width: "+ clusteringLayoutModel.getWidth());
//        System.out.println("center: "+ clusteringLayoutModel.getCenter());
//        System.out.println("class "+clusteringLayoutModel.getClass());



    private void heightConstrain (Component component){
        Dimension d =
                new Dimension(component.getMaximumSize().width, component.getMinimumSize().height);
        component.setMaximumSize(d);
    }


//            vv = VisualizationViewer.builder(graph).viewSize(new Dimension(600, 600)).build();

    public AVLTree<String> getAvlTree(){
        if(avlTree == null) {
            avlTree = new AVLTree<String>();
        }
        return avlTree;
    }

    static JFrame frame;
    static Container content;
    static JPanel demo;

    public static void main(String[] args) {
        frame = new JFrame();
        content = frame.getContentPane();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        demo = new TestAVLTree();
        content.add(demo);
        frame.pack();
        frame.setVisible(true);
        log.trace("frame width {}", frame.getWidth());
        log.trace("demo width {}", demo.getWidth());
    }
}
