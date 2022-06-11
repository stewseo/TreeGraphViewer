# AVL Tree View
### AVL Tree add, delete using tree minimum value, tree maximum or split _and_ merge subtrees
AVLTree Author @Timofey Chudakov from JGraphT.

## Default Tree inorder V11, V22, V33, V44, V55, V66, V77, V88, V91, V99:
![AVLTreeDefaultView](https://user-images.githubusercontent.com/54422342/173126062-152b807a-3698-4619-9557-9730eb95d861.jpg)

## Inserting random nodes: V47, V42, V67, V54, V95, V59, V49, V56, V42, V72 

![AddRandom9NodesV01ToV99](https://user-images.githubusercontent.com/54422342/173126082-96b397f8-7e79-4a54-945c-1a889db953a7.jpg)

# Radial Tree Layout:
![RadialTreeLayout](https://user-images.githubusercontent.com/54422342/173126376-6d48fbbd-0c23-404b-aa14-8be4537f769f.jpg)

# Tidier Radial Tree Layout:
![TidierRadialTreeLayout](https://user-images.githubusercontent.com/54422342/173126494-27283020-2317-4ce7-9139-6617181c6bf5.jpg)

## Expected and Actual Result from adding single node to empty tree: 
![ResultsTestSingleNodeAdd](https://user-images.githubusercontent.com/54422342/173126455-7f9bc6b1-6279-400b-ba2e-7f72b3e38e30.jpg)

## Inserting 15 to tree ( inorder print): 2 3 4 7 9 11 20 21 26 30
            // insert 15:
            //       __20+__                _20++_                  __20++_                ___9___
            //      /       \              /      \                /       \              /       \
            //     4         26    =>     4-       26    =>       9+        26    =>     4+      __20__
            //    / \       /  \         / \      /  \           / \       /  \         / \     /      \
            //   3+  9    21    30      3+  9-  21    30        4+  11-  21    30      3+  7  11-       26
            //  /   / \                /   / \                 / \   \                /         \      /  \
            // 2   7   11             2   7   11-             3+  7   15             2           15  21    30
            //                                 \            /
            //                                  15         2
            tree = new AVLTree<Integer>();
            tree.addMin(30);
            tree.addMin(26);
            tree.addMin(21);
            tree.addMin(20);
            tree.addMin(11);
            tree.addMin(9);
            tree.addMin(7);
            tree.addMin(4);
            tree.addMin(3);
            tree.addMin(2);
            assert tree.getSize() == 10;
            assert tree.getRoot().getValue() == 20;

            tree.insert(15);
            assert tree.getSize() == 11;
            assert tree.getRoot().getValue() == 9;

            iterator = tree.iterator();
            count = 1;
            while (iterator.hasNext()) {
                int currentValue = iterator.next();
                if (count == 5) {
                    assert currentValue == tree.getRoot().getValue();
                }
                if (count++ == 7) {
                    assert currentValue == 15;
                }
            }


