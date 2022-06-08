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

    public enum Layouts {
        EDGEAWARE("Edge Aware", new EdgeAwareTreeLayoutAlgorithm<>()),
        TREELAYOUT("Tree Layout", new TreeLayoutAlgorithm<>()),
        RADIALTREE("Radial Layout", new RadialTreeLayoutAlgorithm<>()),
        KK("Kamada Kawai", new KKLayoutAlgorithm<>()),
        CIRCLE("Circle", new CircleLayoutAlgorithm<>()),

        SELF_ORGANIZING_MAP("Self Organizing Map", new ISOMLayoutAlgorithm<>()),
        FR("Fruchterman Reingold (FR)", new FRLayoutAlgorithm<>()),
        FR_BH_VISITOR(
                "Fruchterman Reingold",
                FRLayoutAlgorithm.builder()
                        .repulsionContractBuilder(BarnesHutFRRepulsion.builder())
                        .build()),
        SPRING("Spring", new SpringLayoutAlgorithm<>()),
        SPRING_BH_VISITOR(
                "Spring (BH Optimized)",
                SpringLayoutAlgorithm.builder()
                        .repulsionContractBuilder(StandardSpringRepulsion.builder())
                        .build());

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

    public static Layouts[] getCombos() {return LayoutHelper.Layouts.values();
    }
}
