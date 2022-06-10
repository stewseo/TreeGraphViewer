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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static AVLTree.LayoutHelper.getCombos;

public class AVLTreeView extends JPanel {
    static AVLTree<String> avlTree ;
    static VisualizationModel<String, Integer> visualizationModel;
    private static final Logger log = LoggerFactory.getLogger(AVLTreeView.class);
    static Graph<String, Integer> graph;
    Graph<String, Integer> tree;
    VisualizationViewer<String, Integer> vv;
    int width = 20;
    int height = 20;

    Dimension preferredSize = new Dimension(400, 400);

    AVLTreeUtil avlTreeUtil;

    Function<Integer, String> stringer =
            e -> "Edge:" + graph.getEdgeSource(e) + "-" + graph.getEdgeTarget(e);

    String instructions =
            "<html>"
                    + "Use the Layout combo box to select the "
                    + "<p>type of tree layout."
                    + "<p>Use the Add Min button "
                    + "<p>to add a new min value node"
                    + "<p>Use the Add Max button "
                    + "<p>to add a new max value node"
                    + "<p>Use the Insert button "
                    + "<p>to add a insert a new node"
                    +"<p>Use the delete button"
                    + "<p>to delete a random node from the tree</html>";

    public AVLTreeView() {
        avlTree = getAvlTree();
        avlTreeUtil = new AVLTreeUtil();
        if(avlTree.getSize() == 0) {
            createTreeView(avlTreeUtil.createForest());
        }

        graph = createTreeGraph(avlTree);
        assert graph.vertexSet().size() == avlTree.getSize();

        setLayout(new BorderLayout());

        final DefaultModalGraphMouse<?, ?> graphMouse = new DefaultModalGraphMouse<>();

        vv = createVisualizationViewer(graphMouse, graph);

        final VisualizationScrollPane panel = new VisualizationScrollPane(vv);

        createPanel(panel, graphMouse);
    }

    private Graph<String, Integer> createTreeGraph(AVLTree<String> avlTree) {
        GraphBuilder<String, Integer, Graph<String, Integer>> graphBuilder
                = GraphTypeBuilder.<String, Integer>forGraphType(DefaultGraphType.dag())
                .edgeSupplier(SupplierUtil.createIntegerSupplier())
                .buildGraphBuilder();

        Iterator<AVLTree.AVLNode<String>> iterator = avlTree.nodeIterator();
        while(iterator.hasNext()){
            AVLTree.AVLNode<String> current = iterator.next();
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

        Function<String, Shape> vertexShapeFunction =
                v -> new Ellipse2D.Float(-width / 2.f, -height / 2.f, width, height);

        panel.add(TreeLayoutSelector.<String, Integer>builder(vv)
                        .vertexShapeFunction(vertexShapeFunction)
                        .build());

        JButton addNode = new JButton("Add Node");
        addNode.addActionListener(e -> addNode());


        JButton deleteNode = new JButton("Delete Node");
        deleteNode.addActionListener(e -> deleteNode());

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
        layoutTypeComboBox.setSelectedItem(LayoutHelper.Layouts.TIDIER_TREE);
        layoutTypeComboBox.addItemListener(
                e -> {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        vv.getVisualizationModel()
                                .getLayoutModel()
                                .accept(((LayoutHelper.Layouts) e.getItem()).getLayoutAlgorithm());
                    }
                });

        JButton junctionNode = new JButton("Button Help");
        junctionNode.addActionListener(e -> JOptionPane.showMessageDialog((JComponent) e.getSource(), instructions, "Button Help", JOptionPane.PLAIN_MESSAGE));

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
        Function<String, Shape> vertexShapeFunction =
                v -> new Ellipse2D.Float(-width / 2.f, -height / 2.f, width, height);

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
        vv.setVertexToolTipFunction(Object::toString);

        vv.getRenderContext().setArrowFillPaintFunction(n -> Color.lightGray);

        vv.getRenderContext().setEdgeShapeFunction(EdgeShape.line());

        vv.getRenderContext()
                .setEdgeDrawPaintFunction(
                        new PickableElementPaintFunction<>(vv.getSelectedEdgeState(), Color.black, Color.red));
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

    private static final Random RANDOM = new Random(17L);

    private void delete() {
        avlTree = getAvlTree();
        int rand = RANDOM.nextInt(avlTree.getSize());
        int c = 1;

        Stack<AVLTree.AVLNode<String>> stack = new Stack<>();
        AVLTree.AVLNode<String> curr = avlTree.getRoot();
        while (!stack.empty() || curr != null) {
            if (curr != null) {
                stack.push(curr);
                curr = curr.left;
            }
            else {
                curr = stack.pop();
                if (c++ == rand) {
                    AVLTree<String> right = avlTree.splitBefore(curr);
                    right.removeMin();
                    avlTree.mergeAfter(right);
                    break;
                } else {
                    curr = curr.right;
                }
            }
            content.setVisible(false);
            content.removeAll();
//            content = frame.getContentPane();
            AVLTreeView demo = new AVLTreeView();
            content.add(demo);
            content.setVisible(true);
        }
    }
    private void addNode() {
        insert();
    }

    private void insert() {
        avlTree = getAvlTree();

        Point2D center = new Point2D.Double();
        double x = 0;
        double y = 0;

        AVLTree.AVLNode<String> nodeToAdd = new AVLTree.AVLNode<String>(avlTreeUtil.getRandom());

        addNodeToTree(nodeToAdd);

        content.setVisible(false);
        content.removeAll();
//            content = frame.getContentPane();
        AVLTreeView demo = new AVLTreeView();
        content.add(demo);
        content.setVisible(true);
    }

    private void addNodeToTree(AVLTree.AVLNode<String> nodeToAdd) {
        int maxComparison = nodeToAdd.getValue().compareTo(avlTree.getMax().getValue());
        int minComparison = nodeToAdd.getValue().compareTo(avlTree.getMin().getValue());
        String vertex = nodeToAdd.getValue();
        Iterator<AVLTree.AVLNode<String>> iterator = avlTree.nodeIterator();
        if (maxComparison > 0) {
            avlTree.addMax(vertex);
            assert avlTree.getMax().getValue().equals(vertex);
        }
        else if (minComparison < 0) {
            avlTree.addMin(vertex);
            assert avlTree.getMin().getValue().equals(vertex);
        }
        else {
            avlTree.insert(nodeToAdd.getValue());
        }
    }

    private void createTreeView(Graph<String, Integer> tree) {
        tree.vertexSet().forEach(vertex -> {
            if (avlTree.getRoot() == null) {
                avlTree.addMin(vertex);
            }
            else {
                int maxComparison = vertex.compareTo(avlTree.getMax().getValue());
                int minComparison = vertex.compareTo(avlTree.getMin().getValue());
                Iterator<AVLTree.AVLNode<String>> iterator = avlTree.nodeIterator();
                if (maxComparison > 0) {
                    avlTree.addMax(vertex);
                    assert avlTree.getMax().getValue().equals(vertex);
                } else if (minComparison < 0) {
                    avlTree.addMin(vertex);
                    assert avlTree.getMin().getValue().equals(vertex);
                }
                else {
                    avlTree.insert(vertex);
                }
            }
        });
    }

    private void heightConstrain (Component component){
        Dimension d =
                new Dimension(component.getMaximumSize().width, component.getMinimumSize().height);
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
        demo = new AVLTreeView();
        content.add(demo);
        frame.pack();
        frame.setVisible(true);
        log.trace("frame width {}", frame.getWidth());
        log.trace("demo width {}", demo.getWidth());
    }
}
