package AVLTree;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultGraphType;
import org.jgrapht.graph.builder.GraphBuilder;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.jgrapht.util.SupplierUtil;
import org.jungrapht.samples.util.ControlHelpers;
import org.jungrapht.samples.util.SubLayoutHelper;
import org.jungrapht.visualization.VisualizationModel;
import org.jungrapht.visualization.VisualizationScrollPane;
import org.jungrapht.visualization.VisualizationViewer;
import org.jungrapht.visualization.control.DefaultModalGraphMouse;
import org.jungrapht.visualization.control.modal.Modal;
import org.jungrapht.visualization.control.modal.ModeControls;
import org.jungrapht.visualization.decorators.PickableElementPaintFunction;
import org.jungrapht.visualization.layout.algorithms.*;
import org.jungrapht.visualization.layout.model.AggregateLayoutModel;
import org.jungrapht.visualization.layout.model.LayoutModel;
import org.jungrapht.visualization.layout.util.RandomLocationTransformer;
import org.jungrapht.visualization.selection.MutableSelectedState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;


import static org.jungrapht.samples.util.SubLayoutHelper.getCombos;

public class TestAVLTree extends JPanel {
    AVLTree<String> avlTree ;
    static VisualizationModel<String, Integer> visualizationModel;
    private static final Logger log = LoggerFactory.getLogger(TestAVLTree.class);
    static Graph<String, Integer> graph;
    AggregateLayoutModel<String> clusteringLayoutModel;

    Dimension subLayoutSize;
    MutableSelectedState<String> ps;

    LayoutAlgorithm<String> subLayoutType = new EdgeAwareTreeLayoutAlgorithm<>();

    VisualizationViewer<String, Integer> vv;

    AVLTreeUtil avlTreeUtil;

    public TestAVLTree() {
        if(avlTree == null){
            //instance of an avlTree, virtual root = null.
            avlTree = new AVLTree<String>();
        }
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
        GraphBuilder<String, Integer, Graph<String, Integer>> tree = GraphTypeBuilder.<String, Integer>forGraphType(DefaultGraphType.dag())
                .edgeSupplier(SupplierUtil.createIntegerSupplier())
                .buildGraphBuilder();

        Graph<String, Integer> testGraph = avlTreeUtil.createForest();
        // the actual min and max values of the tree have a hard coded expected result each iteration.
        // expected result, only values greater than the tree max or less than the tree min wil be added.
        testGraph.vertexSet().forEach(root -> {
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
                    //   AVLNode<T> predecessor = predecessor(node)
                    //            if predecessor is null, swap and create virtual root
                    //            AVLTree<T> tree = new AVLTree<T>();
                    //            swap(tree)
                    //
                    //        return splitAfter(predecessor);
                    System.out.println("Full tree: " + avlTree);
                    System.out.println("node adding: " + testAdd);

                    AVLTree<String> left = avlTree.splitBefore(testAdd);
//                    System.out.println("Size of tree After splitting before param root value: " + avlTree.getRoot().getMin().getValue());
                    System.out.println("left: " + left);
                    avlTree.addMin(root);
                    left.mergeBefore(avlTree);
                    System.out.println("new tree: " + avlTree);

                }
            }
            // subtree diagnostics
//            String rootHeight = "\nroot:" + avlTree.getRoot();
//            StringBuilder sbDiag = new StringBuilder(rootHeight);
//            sbDiag.append(" Height: ").append(avlTree.getRoot().getHeight());
//            sbDiag.append(" right subtree size: ").append(Math.max(avlTree.getRoot().getRightSubtreeSize(), 0));
//            sbDiag.append(" left subtree size: ").append(Math.max(avlTree.getRoot().getLeftSubtreeSize(), 0));
//            System.out.println(sbDiag.toString());

            //test that the key for each vertex set and the key for each AVL node map to the same Fields.
            Iterator<AVLTree.AVLNode<String>> iterator = avlTree.nodeIterator();
//            testIterateByNode(result.get(0), iterator);

            //test that AVLTree's iterator by value matches vertices by key
            Iterator<String>valueiterator = avlTree.iterator();
//            testiterateByValue(result, valueIterator);



            while(iterator.hasNext()) {
                AVLTree.AVLNode<String> current = iterator.next();
                if (current.getRightSubtreeSize() > 0) {
//                    sbDiag.append(current.getSuccessor());
                    tree.addEdge(current.getValue(), current.getRight().getValue());
                }
                if (current.getLeftSubtreeSize() > 0) {
//                    sbDiag.append(current.getPredecessor());
                    tree.addEdge(current.getValue(), current.getLeft().getValue());
                }
//                System.out.println(sbDiag.toString());
            }
        });
        graph = tree.build();

        // Test split
        //                AVLTree.AVLTree.AVLNode<T> parent = .getParent();
//                boolean nextMove = node.isLeftChild();
//                AVLTree.AVLTree.AVLNode<T> left = node.getLeft();
//                AVLTree.AVLTree.AVLNode<T> right = node.getRight();

        setLayout(new BorderLayout());
        Dimension preferredSize = new Dimension(800, 800);
        LayoutAlgorithm<String> layoutAlgorithm = new EdgeAwareTreeLayoutAlgorithm<>();

        clusteringLayoutModel =
                new AggregateLayoutModel<>(
                        LayoutModel.<String>builder()
                                .graph(graph)
                                .size(preferredSize.width, preferredSize.height)
                                .build());

        System.out.print("\nlocations of cluster model: "+ clusteringLayoutModel.getLocations());
        System.out.println("View change support: " + clusteringLayoutModel.getViewChangeSupport());

        clusteringLayoutModel.accept(layoutAlgorithm);

        visualizationModel = VisualizationModel.builder(graph)
                        .layoutModel(clusteringLayoutModel)
                        .layoutAlgorithm(layoutAlgorithm)
                        .build();

        JButton addNode = new JButton("Add Node");
        addNode.addChangeListener(checkRotation());
        addNode.addActionListener(e -> addNode());
        addNode.addActionListener(e -> displayRotation());



        JButton deleteNode = new JButton("Delete Node");
        deleteNode.addActionListener(e -> deleteNode());

        final DefaultModalGraphMouse<?, ?> graphMouse = new DefaultModalGraphMouse<>();

        vv =
                VisualizationViewer.builder(visualizationModel)
                        .graphMouse(graphMouse)
                        .viewSize(preferredSize)
                        .build();

        ps = vv.getSelectedVertexState();
        vv.getRenderContext()
                .setEdgeDrawPaintFunction(
                        new PickableElementPaintFunction<>(vv.getSelectedEdgeState(), Color.black, Color.red));
        vv.getRenderContext()
                .setVertexFillPaintFunction(
                        new PickableElementPaintFunction<>(
                                vv.getSelectedVertexState(), Color.red, Color.yellow));

        // add a listener for ToolTips
        vv.setVertexToolTipFunction(Object::toString);
        VisualizationScrollPane visualizationScrollPane = new VisualizationScrollPane(vv);
        add(visualizationScrollPane);

        JComboBox<?> modeBox = ModeControls.getStandardModeComboBox(Modal.Mode.PICKING, graphMouse);

        JComboBox<SubLayoutHelper.Layouts> layoutTypeComboBox = new JComboBox<>(getCombos());

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
        layoutTypeComboBox.setSelectedItem(SubLayoutHelper.Layouts.FR);
        layoutTypeComboBox.addItemListener(
                e -> {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        vv.getVisualizationModel()
                                .getLayoutModel()
                                .accept(((SubLayoutHelper.Layouts) e.getItem()).getLayoutAlgorithm());
                    }
                });

        JComboBox<SubLayoutHelper.Layouts> subLayoutTypeComboBox = new JComboBox<>(getCombos());

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
        subLayoutTypeComboBox.addItemListener(
                e -> {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        subLayoutType = (((SubLayoutHelper.Layouts) e.getItem()).getLayoutAlgorithm());
                        deleteNode();
                        addNode();
                    }
                });

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
                        addNode();
                        deleteNode();
                    }
                });

        subLayoutDimensionComboBox.setSelectedIndex(1);
        String instructions = "placeholder for link to profile of selection";
        JButton help = new JButton("Help");
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

        JPanel clusterControls = new JPanel(new GridLayout(0, 1));
        clusterControls.setBorder(BorderFactory.createTitledBorder("Clustering"));
        clusterControls.add(addNode);
        clusterControls.add(deleteNode);
        heightConstrain(clusterControls);
        controls.add(clusterControls);
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

    private ChangeListener checkRotation() {
        System.out.println("test ");
        return null;
    }

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
        Collection<String> picked = graph.vertexSet().stream().toList();
        if (picked.size() > 1) {
            System.out.println("p "+picked.size());
            Point2D center = new Point2D.Double();
            double x = 0;
            double y = 0;
            for (String vertex : picked) {
                org.jungrapht.visualization.layout.model.Point p = clusteringLayoutModel.apply(vertex);
                System.out.println("p: "+p);
                x += p.x;
                y += p.y;
            }
            x /= picked.size();
            y /= picked.size();
            center.setLocation(x, y);

            Graph<String, Integer> subGraph;
            AVLTree.AVLNode<String> nodeToAdd = avlTree.addMin(avlTreeUtil.getRandom());
            try {
                if (nodeToAdd.value.hashCode() == 0) {
                    throw new NullPointerException("Invalid value generated for node key");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            try {
                subGraph = GraphTypeBuilder.forGraph(graph).buildGraph();
                System.out.println(subGraph);
                for (String vertex : picked) {
                    subGraph.addVertex(vertex);
                    for (Integer edge : graph.edgesOf(vertex)) {
                        String vertexU = graph.getEdgeSource(edge);
                        String vertexV = graph.getEdgeTarget(edge);
                        System.out.println(vertexU + " " + vertexV);
                        if (picked.contains(vertexU) && picked.contains(vertexV)) {
                            subGraph.addVertex(vertexU);
                            subGraph.addVertex(vertexV);
                            // put this edge into the subgraph
                            subGraph.addEdge(vertexU, vertexV, edge);
                        }
                    }
                }


                LayoutAlgorithm<String> subLayoutAlgorithm = subLayoutType;

                LayoutModel<String> newLayoutModel =
                        LayoutModel.<String>builder()
                                .graph(subGraph)
                                .size(subLayoutSize.width, subLayoutSize.height)
                                .initializer(
                                        new RandomLocationTransformer<>(subLayoutSize.width, subLayoutSize.height, 0))
                                .build();
                clusteringLayoutModel.put(newLayoutModel, org.jungrapht.visualization.layout.model.Point.of(center.getX(), center.getY()));
                newLayoutModel.accept(subLayoutAlgorithm);
                vv.repaint();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("test");
            this.clusteringLayoutModel.removeAll();
            vv.repaint();
        }

        AVLTree.AVLNode<String> s = (AVLTree.AVLNode<String>) avlTree.addMin(avlTreeUtil.getRandom());
        System.out.println("s " + s);

        if(s.right != null) {
            graph.addEdge(s.value.toString(), s.right.value.toString());
            System.out.println(graph);
        }
        if(s.left != null) {
            graph.addEdge(s.value.toString(), s.left.value.toString());
            System.out.println(graph);
        }
        this.clusteringLayoutModel.removeAll();
        LayoutAlgorithm<String> subLayoutAlgorithm = subLayoutType;

        Dimension preferredSize = new Dimension(800, 800);
        LayoutAlgorithm<String> layoutAlgorithm = new TreeLayoutAlgorithm<>();

        clusteringLayoutModel =
                new AggregateLayoutModel<>(
                        LayoutModel.<String>builder()
                                .graph(graph)
                                .size(preferredSize.width, preferredSize.height)
                                .build());

        System.out.println("location "+clusteringLayoutModel.getLocations());
        System.out.println("change support "+clusteringLayoutModel.getViewChangeSupport());
        System.out.println("graph: "+ clusteringLayoutModel.getGraph());
        System.out.println("location: " + clusteringLayoutModel.getLocations());
        System.out.println("height: "+ clusteringLayoutModel.getHeight());
        System.out.println("width: "+ clusteringLayoutModel.getWidth());
        System.out.println("center: "+ clusteringLayoutModel.getCenter());
        System.out.println("class "+clusteringLayoutModel.getClass());

        clusteringLayoutModel.accept(layoutAlgorithm);

        visualizationModel = VisualizationModel.builder(graph)
                .layoutModel(clusteringLayoutModel)
                .layoutAlgorithm(layoutAlgorithm)
                .build();



//        content.setVisible(false);
//        visualizationModel.setGraph(graph);
//        content = frame.getContentPane();
//        frame.pack();
//        content.setVisible(true);
//        vv.repaint();

    }

    private void heightConstrain (Component component){
        Dimension d =
                new Dimension(component.getMaximumSize().width, component.getMinimumSize().height);
        component.setMaximumSize(d);
    }


//            vv = VisualizationViewer.builder(graph).viewSize(new Dimension(600, 600)).build();

    public AVLTree<String> getAvlTree(){
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
