package AVLTree;
import org.jungrapht.visualization.layout.algorithms.*;
import org.jungrapht.visualization.layout.algorithms.EdgeAwareTreeLayoutAlgorithm;
import org.jungrapht.visualization.layout.algorithms.TreeLayoutAlgorithm;
import org.jungrapht.visualization.layout.algorithms.RadialTreeLayoutAlgorithm;
import org.jungrapht.visualization.layout.algorithms.repulsion.BarnesHutFRRepulsion;
import org.jungrapht.visualization.layout.algorithms.repulsion.StandardSpringRepulsion;

import java.util.HashMap;
import java.util.Map;

public class LayoutHelper {
    public enum Rotation {
        SPLIT_BEFORE("Split Before", new AVLTree<String>()),
        SPLIT_AFTER("Split After", new AVLTree<String>()),
        MERGE_BEFORE("Merge Before", new AVLTree<String>()),
        MERGE_AFTER("Merge After", new AVLTree<String>());
        private static final Map<String, LayoutHelper.Rotation> BY_ROTATION = new HashMap<>();

        static {
            for (Rotation rotation : values()) {
                BY_ROTATION.put(rotation.name, rotation);
            }
        }
        @Override
        public String toString() {
            return name;
        }

        public static Rotation valueOfName(String name) {
            return BY_ROTATION.get(name);
        }
        public static Rotation[] getSplitMergeType() {return LayoutHelper.Rotation.values();}

        private final String name;
        private final AVLTree<String> tree;

        public AVLTree<String> getTree() {
            return  tree;
        }

        <T> Rotation(String s, AVLTree<String> tree) {
            this.name = s;
            this.tree = tree;
        }
    }
    public enum Layouts {
        EDGE_AWARE("Edge Aware Tree Layout", new EdgeAwareTreeLayoutAlgorithm<>()),
        TREE_LAYOUT("Tree Layout", new TreeLayoutAlgorithm<>()),
        TIDIER_TREE("Tidier Tree Layout ", new TidierTreeLayoutAlgorithm<>()),
        RADIAL_TREE("Radial Tree Layout", new RadialTreeLayoutAlgorithm<>()),
        TIDIER_RADIAL_TREE("Tidier Radial Tree Layout", new TidierRadialTreeLayoutAlgorithm<>());

        private static final Map<String, LayoutHelper.Layouts> BY_NAME = new HashMap<>();


        static {
            for (Layouts layout : values()) {
                BY_NAME.put(layout.name, layout);
            }
        }

        Layouts(String name, LayoutAlgorithm layoutAlgorithm) {
            this.name = name;
            this.layoutAlgorithm = layoutAlgorithm;
        }

        private final String name;

        private final LayoutAlgorithm layoutAlgorithm;

        public LayoutAlgorithm getLayoutAlgorithm() {
            return layoutAlgorithm;
        }

        @Override
        public String toString() {
            return name;
        }

        public static Layouts valueOfName(String name) {
            return BY_NAME.get(name);
        }
    }

    public static Layouts[] getCombos() {return LayoutHelper.Layouts.values();}

}
