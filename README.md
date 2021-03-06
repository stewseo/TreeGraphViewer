# AVL Tree View
### AVL Tree cases for balance, and expected location when inserting and removing with split -> min/max -> merge operations.
AVLTree Author @TimofeyChudakov of JGraphT.
Demo view Author @TomNelson of JUNG.

## Tree default visual. Buttons Add min, Add max, insert random value: split/merge before/after junction node
![AVLTreeDefaultView](https://user-images.githubusercontent.com/54422342/173126062-152b807a-3698-4619-9557-9730eb95d861.jpg)

![TestAddMinDoubleRightHeavyRemoveMinDoubleLeftHEavy](https://user-images.githubusercontent.com/54422342/173293513-8bacaf3f-1555-4089-9b82-ec5e3d59d652.jpg)

 Inserting random nodes: V47, V42, V67, V54, V95, V59, V49, V56, V42, V72 

![AddRandom9NodesV01ToV99](https://user-images.githubusercontent.com/54422342/173126082-96b397f8-7e79-4a54-945c-1a889db953a7.jpg)

Radial Tree Layout:
![RadialTreeLayout](https://user-images.githubusercontent.com/54422342/173126376-6d48fbbd-0c23-404b-aa14-8be4537f769f.jpg)

Tidier Radial Tree Layout:
![TidierRadialTreeLayout](https://user-images.githubusercontent.com/54422342/173126494-27283020-2317-4ce7-9139-6617181c6bf5.jpg)

Add single node to empty tree: 
![ResultsTestSingleNodeAdd](https://user-images.githubusercontent.com/54422342/173126455-7f9bc6b1-6279-400b-ba2e-7f72b3e38e30.jpg)

Case to produce a double right rotation ( level order print ): 20, 4, 26, 3, 9, 21, 30, 2, 7, 11
![caseInsert15RightDoubleHeavy](https://user-images.githubusercontent.com/54422342/173217422-acd1fccd-ef28-47ae-8157-722843398c16.jpg)

Case for single right rotation 

            //    2          2            4
            //   / \          \          / \
            //  1   4    =>    4    =>  2   5
            //     / \        / \        \
            //    3   5      3   5        3
            tree = new AVLTree<Integer>();
            int[] caseOneDelete = new int[]{1,2,3,4,5};
            Arrays.stream(caseOneDelete).forEach(tree::addMax);
            tree.removeMin();
            
Case for double right rotation  

            //bfs 8,4,A,2,6,9,C,1,3,5,7,B,D   bfs 8,4,A,2,6,9,C,3,5,7,B,D  bfs 8,4,A,3,6,9,C,5,7,B,D
            // removeMin() (1)                 removeMin() (2)              removeMin() (3) -> double right heavy
            //         ____8____                   ___8___                 ___8___
            //        /          \                /       \               /       \
            //     __4__        __A__            4         A             4         A
            //    /     \       /    \         /   \      /  \          / \       / \
            //   2       6     9     _C_ =>   2     6    9    C    => 3    6     9   C
            //  / \     / \         /   \      \   /  \      / \          /  \  /   /  \
            // 1   3   5   7       B     D      3 5    7    B   D        5    7    B    D
            //
            // bfs rotateLeft(node)           bfs after rotateLeft      bfs after 2nd rotateLeft
            // 8,4,A,6,9,C,5,7,B,D             8,4,A,5,9,C,B,D          8,6,A,4,7,9,C,5,B,D
            //         ____8____                   ___8___                 ___8___
            //        /          \                /       \               /       \
            //     __4__        __A__            4         A             6         A
            //          \       /    \             \     /  \           / \       / \
            //           6     9     _C_ =>         5   9    C    =>   4   7     9   C
            //          / \         /   \                   / \           /         /  \
            //         5   7       B     D                 B   D         5         B    D
            AVLTree<String> tree2 = new AVLTree<String>();
            tree2.addMin("1");
            tree2.addMax("D");
            tree2.insert("2");
            tree2.insert("3");
            tree2.insert("4");
            tree2.insert("5");
            tree2.insert("6");
            tree2.insert("7");
            tree2.insert("8");
            tree2.insert("9");
            tree2.insert("A");
            tree2.insert("B");
            tree2.insert("C");

            tree2.removeMin();
            tree2.removeMin();
            tree2.removeMin();
            
 

