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
            v -> new Ellipse2D.Float(-width / 1.1f, -height / 1.1f, width, height);

    Dimension preferredSize = new Dimension(400, 400);

    AVLTreeUtil avlTreeUtil;

    public TestAVLTree() {
        avlTree = getAvlTree();

        avlTreeUtil = new AVLTreeUtil();

        createTreeView(avlTreeUtil.createForest());

        graph = createTreeGraph(avlTree);

        setLayout(new BorderLayout());
        final DefaultModalGraphMouse<?, ?> graphMouse = new DefaultModalGraphMouse<>();

        vv = createVisualizationViewer(graphMouse, graph);

        final VisualizationScrollPane panel = new VisualizationScrollPane(vv);

        createPanel(panel, graphMouse);

    }

    private Graph<String, Integer> createTreeGraph(AVLTree<String> avlTree) {
        System.out.println(avlTree.getSize());
        GraphBuilder<String, Integer, Graph<String, Integer>> graphBuilder
                = GraphTypeBuilder.<String, Integer>forGraphType(DefaultGraphType.dag())
                .edgeSupplier(SupplierUtil.createIntegerSupplier())
                .buildGraphBuilder();

        Iterator<AVLTree.AVLNode<String>> iterator = avlTree.nodeIterator();
        while(iterator.hasNext()){
            AVLTree.AVLNode<String> current = iterator.next();
            graphBuilder.addVertex(current.getValue());
            System.out.print(" current node valiue: " + current.getValue());
            if(current.left != null){
                graphBuilder.addEdge(current.getValue(), current.getLeft().getValue());

            }
            if(current.right != null){
                graphBuilder.addEdge(current.getValue(), current.getRight().getValue());

            }
        }
        return graphBuilder.build();
    }

    private void createPanel(VisualizationScrollPane panel, DefaultModalGraphMouse<?, ?> graphMouse) {
        panel.add(TreeLayoutSelector.<String, Integer>builder(vv)
                        .vertexShapeFunction(vertexShapeFunction)
                        .build());

        JButton addNode = new JButton("Add Node");
        addNode.addActionListener(e -> addNode());


        JButton deleteNode = new JButton("Delete Node");
        deleteNode.addActionListener(e -> deleteNode());


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


        String instructions = "Split Merge";
        JButton junctionNode = new JButton("Split Merge");
        junctionNode.addActionListener(e -> JOptionPane.showMessageDialog((JComponent) e.getSource(), instructions, "Split Merge", JOptionPane.PLAIN_MESSAGE));

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

//        JPanel subLayoutControls = new JPanel(new GridLayout(0, 1));
//        subLayoutControls.setBorder(BorderFactory.createTitledBorder("SubLayout"));
//        subLayoutControls.add(subLayoutTypeComboBox);
//        subLayoutControls.add(subLayoutDimensionComboBox);
//        heightConstrain(subLayoutControls);
//        controls.add(subLayoutControls);
        controls.add(Box.createRigidArea(space));

        JPanel modePanel = new JPanel(new GridLayout(1, 1));
        modePanel.setBorder(BorderFactory.createTitledBorder("Mouse Mode"));
        modePanel.add(modeBox);
        heightConstrain(modePanel);
        controls.add(modePanel);
        controls.add(Box.createRigidArea(space));

        controls.add(junctionNode);
        controls.add(Box.createVerticalGlue());
        add(controls, BorderLayout.SOUTH);
    }

    private VisualizationViewer<String, Integer> createVisualizationViewer(DefaultModalGraphMouse<?, ?> graphMouse, Graph<String, Integer> graph) {
//        VisualizationModel.builder(graph).layoutAlgorithm()
        vv = VisualizationViewer.builder(graph)
                        .layoutAlgorithm(TreeLayoutAlgorithm.<String>builder()
                                .expandLayout(true)
                                .build())
                .layoutSize(new Dimension(700,700))
                .viewSize(new Dimension(700, 700))
                .graphMouse(graphMouse)
                .build();
        vv.getRenderContext().setEdgeShapeFunction(EdgeShape.articulatedLine());
        vv.getRenderContext().setVertexLabelFunction(Object::toString);
        vv.getRenderContext().setVertexLabelPosition(Renderer.VertexLabel.Position.CNTR);
        vv.getRenderContext().setVertexLabelDrawPaintFunction(n -> Color.white);
        vv.getRenderContext().setArrowsOnUndirectedEdges(true);

        vv.setVertexToolTipFunction(Object::toString);
//
        vv.getRenderContext().setArrowFillPaintFunction(n -> Color.lightGray);

        vv.getRenderContext().setEdgeShapeFunction(EdgeShape.line());

        vv.getRenderContext()
                .setEdgeDrawPaintFunction(
                        new PickableElementPaintFunction<>(vv.getSelectedEdgeState(), Color.green, Color.red));
        vv.getRenderContext()
                .setVertexFillPaintFunction(
                        new PickableElementPaintFunction<>(
                                vv.getSelectedVertexState(), Color.black, Color.red));

        if (log.isTraceEnabled()) {
            vv.addPreRenderPaintable(
                    new LayoutPaintable.LayoutBounds(
                            vv.getVisualizationModel(), vv.getRenderContext().getMultiLayerTransformer()));
        }
        return vv;
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
        avlTree = getAvlTree();
        System.out.println(" size: " + avlTree.getSize());
        System.out.println(" node value: " + avlTree.getRoot().getValue());
        System.out.println(" min node: " + avlTree.getMax().getValue());
        System.out.println(" max node: " + avlTree.getMin().getValue());

        Point2D center = new Point2D.Double();
        double x = 0;
        double y = 0;

        AVLTree.AVLNode<String> nodeToAdd = new AVLTree.AVLNode<String>(avlTreeUtil.getRandom());

        addNodeToTree(nodeToAdd);

        Graph<String, Integer> g = createTreeGraph(avlTree);

        setLayout(new BorderLayout());

        DefaultModalGraphMouse<?, ?> graphMouse = new DefaultModalGraphMouse<>();

        vv = createVisualizationViewer(graphMouse, g);

        VisualizationScrollPane panel = new VisualizationScrollPane(vv);
        createPanel(panel, graphMouse);

        content = frame.getContentPane();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void addNodeToTree(AVLTree.AVLNode<String> nodeToAdd) {
        System.out.println("max " + avlTree.getMax().getValue());
        System.out.println("min " + avlTree.getMin().getValue());
        int maxComparison = nodeToAdd.getValue().compareTo(avlTree.getMax().getValue());
        int minComparison = nodeToAdd.getValue().compareTo(avlTree.getMin().getValue());

        if (maxComparison > 0) {
            avlTree.addMax(nodeToAdd.getValue());
        } else if (minComparison < 0) {
            avlTree.addMin(nodeToAdd.getValue());
        } else {
            Stack<AVLTree.AVLNode<String>> stack = new Stack<>();
            AVLTree.AVLNode<String> curr = avlTree.getRoot();
            while (!stack.empty() || curr != null) {
                if (curr != null) {
                    stack.push(curr);
                    curr = curr.left;
                } else {
                    curr = stack.pop();
                    if (curr.getValue().compareTo(nodeToAdd.getValue()) > 0) {
                        AVLTree<String> right = avlTree.splitBefore(curr);
                        avlTree.addMax(nodeToAdd.getValue());
                        avlTree.mergeBefore(right);
                        break;
                    }else {
                        curr = curr.right;
                    }
                }
            }
        }
    }
    private void createTreeView(Graph<String, Integer> tree) {
        tree.vertexSet().forEach(root -> {
            System.out.println(root);
                    if (avlTree.getRoot() == null) {
                        avlTree.addMin(root);
                    }
                    else {
                        int maxComparison = root.compareTo(avlTree.getMax().getValue());
                        int minComparison = root.compareTo(avlTree.getMin().getValue());
                        Iterator<AVLTree.AVLNode<String>> iterator = avlTree.nodeIterator();

                        if (maxComparison > 0) {
                            avlTree.addMax(root);
                        }
                        else if (minComparison < 0) {
                            avlTree.addMin(root);
                        }
                        else {
                            Stack<AVLTree.AVLNode<String>> stack = new Stack<>();
                            AVLTree.AVLNode<String> curr = avlTree.getRoot();
                            while (!stack.empty() || curr != null) {
                                if (curr != null) {
                                    stack.push(curr);
                                    curr = curr.left;
                                }
                                else {
                                    curr = stack.pop();
                                    if(curr.getValue().compareTo(root) > 0) {
                                        AVLTree<String> right = avlTree.splitBefore(curr);
                                        avlTree.addMax(root);
                                        avlTree.mergeBefore(right);
                                        break;
                                    }else {
                                        curr = curr.right;
                                    }
                                }
                            }
                        }
                    }
                });
    }

    private void heightConstrain (Component component){
        Dimension d =
                new Dimension(component.getMaximumSize().width, component.getMinimumSize().height);
        System.out.println(" width: " + component.getMaximumSize().width + " height: " + component.getMaximumSize().height);
        component.setMaximumSize(d);
    }

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
