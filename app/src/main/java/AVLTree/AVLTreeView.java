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
import java.util.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static AVLTree.LayoutHelper.Rotation.getSplitMergeType;
import static AVLTree.LayoutHelper.getCombos;

/**
 * Modified from TreeLayoutDemo and SubClusterLayoutDemo of JUNG
 * Author of TreeLayoutDemo and SubClusterLayoutDemo:
 * @author Tim Neslon
 */
public class AVLTreeView extends JPanel {
    static AVLTree<String> avlTree ;
    static VisualizationModel<String, Integer> visualizationModel;
    private static final Logger log = LoggerFactory.getLogger(AVLTreeView.class);
    static Graph<String, Integer> graph;
    Graph<String, Integer> tree;
    VisualizationViewer<String, Integer> vv;
    int width = 20;
    int height = 20;

    Dimension preferredSize = new Dimension(900, 700);

    GraphUtil avlTreeUtil;

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
        avlTreeUtil = new GraphUtil();
        if(avlTree.getSize() == 0) {
            createTreeView(avlTreeUtil.createFirstMin());
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

        Iterator<AVLTree.TreeNode<String>> iterator = avlTree.nodeIterator();
        while(iterator.hasNext()){
            AVLTree.TreeNode<String> current = iterator.next();
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



        vv.setVertexToolTipFunction(Object::toString);
        VisualizationScrollPane visualizationScrollPane = new VisualizationScrollPane(vv);
        add(visualizationScrollPane);

//        JComboBox<?> mergeBox = ModeControls.getStandardModeComboBox(Modal.Mode.PICKING, graphMouse);
        JComboBox<LayoutHelper.Rotation> mergeBox = new JComboBox<>(getSplitMergeType());
        mergeBox.setRenderer(
                new DefaultListCellRenderer() {
                    public Component getListCellRendererComponent(
                            JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                        String valueString = value.toString();
                        valueString = valueString.substring(valueString.lastIndexOf('.') + 1);
                        return super.getListCellRendererComponent(
                                list, valueString, index, isSelected, cellHasFocus);
                    }
                });

        mergeBox.setSelectedItem(LayoutHelper.Rotation.MERGE_AFTER);
        setSplitType(LayoutHelper.Rotation.MERGE_AFTER.toString());
        mergeBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                setSplitType(e.getItem().toString());
                vv.getVisualizationModel()
                        .getLayoutModel()
                        .accept((new TreeLayoutAlgorithm<>()));
            }

        });
        Dimension space = new Dimension(20, 20);
        Box controls = Box.createHorizontalBox();
        controls.add(Box.createRigidArea(space));

        JComponent zoomControls =
                ControlHelpers.getZoomControls("Scale", vv); //, new GridLayout(0, 1));
        heightConstrain(zoomControls);
        controls.add(zoomControls);
        controls.add(Box.createRigidArea(space));

        createPanelRemove(controls,space );
        createPanelAdd(controls, space);
        createLayoutControlPanel(controls,space);
//        createModePanel();
        createSplitPanel(controls,space);

        JButton merge = new JButton("Merge");
        merge.addActionListener(e -> {
            System.out.println(e);
            mergeView(getRight(), getJunctionNode(), getSplitType());
        });

        JPanel mergePanel = new JPanel(new GridLayout(0, 1));
        mergePanel.setBorder(BorderFactory.createTitledBorder("Merge"));
        mergePanel.add(mergeBox);
        mergePanel.add(merge);
        heightConstrain(mergePanel);
        controls.add(mergePanel);
        controls.add(Box.createRigidArea(space));
    }

    AVLTree.TreeNode<String> junctionNode;
    private void setJunctionNode(String separatorValue) {
        junctionNode = new AVLTree.TreeNode<String>(separatorValue);
    }
    private AVLTree.TreeNode<String> getJunctionNode() {
        return junctionNode;
    }

    static AVLTree<String> rightT = new AVLTree<String>();

    public AVLTree<String> getRight() {
        return rightT;
    }

    public void setRight(AVLTree<String> right){
        rightT = right;
    }
    private void createSplitPanel(Box controls, Dimension space) {

        JButton split = new JButton("Split");
        split.addActionListener(e -> {
            splitView(getInsertValue(), getSplitType());
        });

        int max = Integer.parseInt(getAvlTree().getMax().getValue());
        int min = Integer.parseInt(getAvlTree().getMin().getValue());
        JTextField input = new JTextField();
        input.addActionListener(e-> {
            AVLTree<String> right = splitView(e.getActionCommand(), getSplitType());
            setRight(right);
        });



        JComboBox<LayoutHelper.Rotation> splitMergeComboBox = new JComboBox<>(getSplitMergeType());
        splitMergeComboBox.setRenderer(
                new DefaultListCellRenderer() {
                    public Component getListCellRendererComponent(
                            JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                        String valueString = value.toString();
                        valueString = valueString.substring(valueString.lastIndexOf('.') + 1);
                        return super.getListCellRendererComponent(
                                list, valueString, index, isSelected, cellHasFocus);
                    }
                });

        splitMergeComboBox.setSelectedItem(LayoutHelper.Rotation.SPLIT_BEFORE);
        setSplitType(LayoutHelper.Rotation.SPLIT_BEFORE.toString());
        splitMergeComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                setSplitType(e.getItem().toString());
                vv.getVisualizationModel()
                        .getLayoutModel()
                        .accept((new TreeLayoutAlgorithm<>()));
            }

        });
        JPanel splitMergeControls = new JPanel(new GridLayout(0, 1));
        splitMergeControls.setBorder(BorderFactory.createTitledBorder("Split Tree"));
        splitMergeControls.add(splitMergeComboBox);
        splitMergeControls.add(split);
        splitMergeControls.add(input);
        heightConstrain(splitMergeControls);
        controls.add(splitMergeControls);
        controls.add(Box.createRigidArea(space));
        controls.add(Box.createVerticalGlue());
        add(controls, BorderLayout.SOUTH);

    }
    private Timer timer;
    final JRadioButton animateLayoutTransition = new JRadioButton("Animate");

    private void mergeAfterView(){

        SwingUtilities.invokeLater(
                () -> {
                    avlTree.mergeAfter(getRight());
                    content.setVisible(false);
                    content.removeAll();
                    AVLTreeView newDemo = new AVLTreeView();
                    content.add(newDemo);
                    content.setVisible(true);
                });
    }
    private void mergeView(AVLTree<String> right, AVLTree.TreeNode<String> junctionNode, String mergeType) {

        if (mergeType.substring(splitType.length()-11, splitType.length()).equalsIgnoreCase("merge after") ||
                mergeType.substring(splitType.length()-12, splitType.length()).equalsIgnoreCase("merge before")) {
            System.out.println("right size " + right.getSize() + " merge: " + mergeType);
            avlTree = getAvlTree();
            right.removeMin();

            content.setVisible(false);
            content.removeAll();
            AVLTreeView demo = new AVLTreeView();
            content.add(demo);
            content.setVisible(true);

            timer = new Timer();
            timer.schedule(new AddRemoveTask(this::mergeAfterView), 4000);
//            vv.repaint();

        }
    }
    private static final int MAX_VERTICES = 30;
    private String previousVertex = null;
    private LayoutAlgorithm<String> layoutAlgorithm;

    private void processAdd() {
        vv.getRenderContext().getSelectedVertexState().clear();
        vv.getRenderContext().getSelectedEdgeState().clear();
        try {
            Iterator<String> it = graph.vertexSet().stream().iterator();
            if (graph.vertexSet().size() < MAX_VERTICES) {

                SwingUtilities.invokeLater(
                        () -> {
                            if(it.hasNext()) {
                                String v1 = it.next();
                                graph.addVertex(v1);
                                vv.getRenderContext().getSelectedVertexState().select(v1);

                                int edge = graph.edgeSet().size();
                                vv.getRenderContext().getSelectedEdgeState().select(edge);
                                graph.addEdge(String.valueOf(previousVertex), String.valueOf(v1), edge);

                                String rand = String.valueOf(new Random().nextInt(Integer.parseInt(v1)));
                                vv.getRenderContext().getSelectedEdgeState().select(edge);
                                graph.addEdge(v1, rand, ++edge);
                                vv.getVisualizationModel().getLayoutModel().accept(layoutAlgorithm);
                                vv.repaint();

                                previousVertex = v1;
                            }
                        });

            } else {
                timer.cancel();
                timer = new Timer();
            }

        } catch (Exception e) {
            log.warn("exception:", e);
        }
    }


    private static class AddRemoveTask extends TimerTask {
        Runnable runnable;

        AddRemoveTask(Runnable runnable) {
            this.runnable = runnable;
        }

        public void run() {
            runnable.run();
        }
    }

    private AVLTree<String> splitView(String insertValue, String splitType) {
        avlTree = getAvlTree();
        AVLTree<String> right = new AVLTree<String>();
        if(splitType.substring(0, 12).equalsIgnoreCase("split before")) {
            Iterator<AVLTree.TreeNode<String>> iterator = avlTree.nodeIterator();
            while(iterator.hasNext()){
                AVLTree.TreeNode<String> node = iterator.next();
                if(node.getValue().equals(insertValue)){
                    right = avlTree.splitBefore(node);

                    content.setVisible(false);
                    content.removeAll();
                    AVLTreeView demo = new AVLTreeView();
                    content.add(demo);
                    content.setVisible(true);

                    return right;
                }
            }
        }return null;
    }

    String splitType = null;
    private void setSplitType(String value) {
        this.splitType = value;
    }
    private String getSplitType() {
        return splitType;
    }

    String insertValue = null;
    private void setInsertValue(String value) {
        insertValue = value;
    }
    public String getInsertValue(){
        return insertValue;
    }

    private void createLayoutControlPanel(Box controls, Dimension space) {
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
        layoutTypeComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                vv.getVisualizationModel()
                        .getLayoutModel()
                        .accept(((LayoutHelper.Layouts) e.getItem()).getLayoutAlgorithm());
            }
        });
        JPanel layoutControls = new JPanel(new GridLayout(0, 1));
        layoutControls.setBorder(BorderFactory.createTitledBorder("Layout"));
        layoutControls.add(layoutTypeComboBox);
        heightConstrain(layoutControls);
        controls.add(layoutControls);

        controls.add(Box.createRigidArea(space));
    }


    private void createPanelRemove(Box controls, Dimension space) {

        JButton removeMin = new JButton("Remove Min");
        removeMin.addActionListener(e -> addMinMax(AddRemove.REMOVE_MIN));

        JButton removeMax = new JButton("Remove Max");
        removeMax.addActionListener(e -> addMinMax(AddRemove.REMOVE_MAX));

        JPanel panelRemove = new JPanel(new GridLayout(0, 1));
        panelRemove.setBorder(BorderFactory.createTitledBorder("Remove"));
        panelRemove.add(removeMin);
        panelRemove.add(removeMax);
        heightConstrain(panelRemove);
        controls.add(panelRemove);
        controls.add(Box.createRigidArea(space));

    }

    private void createPanelAdd(Box controls, Dimension space) {
        JButton addMin = new JButton("Add Min");
        addMin.addActionListener(e -> addMinMax(AddRemove.ADD_MIN));

        JButton addMax = new JButton("Add Max");
        addMax.addActionListener(e -> addMinMax(AddRemove.ADD_MAX));

        JPanel addPanel = new JPanel(new GridLayout(0, 1));
        addPanel.setBorder(BorderFactory.createTitledBorder("Remove"));
        addPanel.add(addMin);
        addPanel.add(addMax);
        heightConstrain(addPanel);
        controls.add(addPanel);
        controls.add(Box.createRigidArea(space));
    }



    private VisualizationViewer<String, Integer> createVisualizationViewer(DefaultModalGraphMouse<?, ?> graphMouse, Graph<String, Integer> graph) {
        Function<String, Shape> vertexShapeFunction =
                v -> new Ellipse2D.Float(-width / 3.f, -height / 3.f, width, height);

        vv =
                VisualizationViewer.builder(graph)
                        .layoutAlgorithm(
                                TreeLayoutAlgorithm.<String>builder()
                                        .vertexBoundsFunction(
                                                vertexShapeFunction.andThen(s -> AWT.convert(s.getBounds2D())))
                                        .build())
                        .viewSize(new Dimension(900, 700))
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

//        if (log.isTraceEnabled()) {
//            vv.addPreRenderPaintable(
//                    new LayoutPaintable.LayoutBounds(
//                            vv.getVisualizationModel(), vv.getRenderContext().getMultiLayerTransformer()));
//        }
        return vv;
    }

    private void removeRandom() {
        avlTree = getAvlTree();
        int rand = RANDOM.nextInt(avlTree.getSize());
        int c = 1;

        Stack<AVLTree.TreeNode<String>> stack = new Stack<>();
        AVLTree.TreeNode<String> curr = avlTree.getRoot();
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
            AVLTreeView demo = new AVLTreeView();
            content.add(demo);
            content.setVisible(true);
        }
    }
    private void removeMinMax(AddRemove minMax) {
        avlTree = getAvlTree();
        if(minMax.equals(AddRemove.REMOVE_MAX)) {
            avlTree.removeMax();
        }else {
            avlTree.removeMin();
        }
        content.setVisible(false);
        content.removeAll();
        AVLTreeView demo = new AVLTreeView();
        content.add(demo);
        content.setVisible(true);
    }


    private static final Random RANDOM = new Random(17L);


    private void addMinMax(AddRemove minMax) {
        var result = new AVLTree.TreeNode<String>(null);
        boolean added = false;
        switch(minMax) {
            case ADD_MAX -> {
                added = true;
                result = new AVLTree.TreeNode<String>(Objects.requireNonNull(getMaxValues().pollFirst()).toString());
            }
            case ADD_MIN -> {
                added = true;
                result = new AVLTree.TreeNode<String>(Objects.requireNonNull(getMinValues().pollLast()).toString());
            }
            case REMOVE_MIN -> {
               minValueArrayDeque.offerLast(Integer.parseInt(getAvlTree().removeMin().getValue()));
            }
            case REMOVE_MAX -> {
                maxValueDeque.offerFirst(Integer.parseInt(getAvlTree().removeMax().getValue()));
            }
        }
        if(added) {
            addNodeToTree(result);
        }
        content.setVisible(false);
        content.removeAll();
        AVLTreeView demo = new AVLTreeView();
        content.add(demo);
        content.setVisible(true);

    }

    private static Deque<Integer> minValueArrayDeque = new ArrayDeque<>();
    public Deque<Integer> getMinValues(){
        if(minValueArrayDeque.isEmpty()) {
            minValueArrayDeque = IntStream.range(1, 256).boxed().collect(Collectors.toCollection(ArrayDeque::new));
        }
        return minValueArrayDeque;
    }

    private static Deque<Integer> maxValueDeque = new ArrayDeque<>();
    public Deque<Integer> getMaxValues(){
        if(maxValueDeque.isEmpty()) {
            maxValueDeque = IntStream.range(257, 513).boxed().collect(Collectors.toCollection(LinkedList::new));
        }System.out.println(maxValueDeque.size());
        return maxValueDeque;
    }

    private void addNodeToTree(AVLTree.TreeNode<String> nodeToAdd) {
        if(nodeToAdd == null) {
            return;
        }
        int maxComparison = nodeToAdd.getValue().compareTo(avlTree.getMax().getValue());
        int minComparison = nodeToAdd.getValue().compareTo(avlTree.getMin().getValue());

        String valueToAdd = nodeToAdd.getValue();

        if (maxComparison > 0) {
            avlTree.addMax(valueToAdd);
            assert avlTree.getMax().getValue().equals(valueToAdd);
        }
        else if (minComparison < 0) {
            avlTree.addMin(valueToAdd);
            assert avlTree.getMin().getValue().equals(valueToAdd);
        }
        else {
            avlTree.insert(valueToAdd);
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
                Iterator<AVLTree.TreeNode<String>> iterator = avlTree.nodeIterator();
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
