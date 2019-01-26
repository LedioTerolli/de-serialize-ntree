import java.io.*;
import java.util.*;

public class NTree<E> {

    protected String leaf_mark = "/"; // marks a leaf or the end of all children

    protected class Node {
        E data;
        Node parent;
        List<Node> children;

        protected Node(E data) {
            this.data = data;
            this.children = new ArrayList<Node>();
        }

        protected void addChild(Node c) { children.add(c); }
        public boolean equals(Node rhs) {
            return this.data.equals(rhs.data);
        }
    }

    protected Node root;

    public NTree() {}

    public NTree(List<E> values, List<Integer> parents) throws Exception {
        if (values.size() != parents.size()) throw new Exception();
        Map<E, Node> m = new TreeMap<>();
        for (int i = 0; i < values.size(); i++) {
            Node nd = new Node(values.get(i));
            m.put(values.get(i), nd);
            if (parents.get(i) >= 0) {		// -1 signals root
                nd.parent = m.get(values.get(parents.get(i)));
                nd.parent.addChild(nd);
            }
            else root = nd;
        }
    }

    public boolean equals(NTree<E> rhs) {
        return equals(root, rhs.root);
    }

    protected boolean equals(Node lhs, Node rhs) {
        if (lhs == null || rhs == null) return lhs == rhs;
        // no need to check if parents are equal, since we call recursively
        if (!lhs.equals(rhs)) return false;
        for (int i = 0; i < lhs.children.size(); i++) {
            if (!equals(lhs.children.get(i), rhs.children.get(i))) return false;
        }
        return true;
    }

    public void serialize(String fname) {

        StringBuilder str_b = new StringBuilder(); // outputting the serialized tree in this string
        con_str(str_b, root); // calling this recursive method

        // creating a file to store the string
        try {
            PrintWriter writer = new PrintWriter(fname + ".txt", "UTF-8");
            writer.print(str_b.toString()); // outputting the string to the new file
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // recursive method
    // takes an initial string and a node, which is the root of its subtree
    // constructs the string (the serialized tree)
    private void con_str(StringBuilder str_b, Node root) {

        // if the root is null do nothing
        if (root != null) {

            str_b.append(root.data); // adding the data of root to the string
            str_b.append("\n"); // creating new line

            // visiting each child of the current root node
            for (Node child : root.children) {
                // calling the recursive method on every child
                // now each child is the root of its subtree
                con_str(str_b, child);
            }

            // at this point the root is a leaf or all children are visited
            // we mark this point by using (/)
            str_b.append(leaf_mark);
            str_b.append("\n"); // creating new line
        }
    }

    public void deserialize(String fname) {

        String line; // current string
        boolean first_pass = true; // true signals the root
        BufferedReader reader = null; // using a BufferedReader to read the serialized tree

        // storing each newly created node in a Stack
        // using a Stack to access the top element
        Stack<Node> node_stack = new Stack<>();

        // reading the file
        try {
            File file = new File(fname + ".txt");
            reader = new BufferedReader(new FileReader(file));

            //              *** SIDE NOTE ***
            // did not create a method that specifically builds the tree
            // because we would have to store the input strings (using more space)
            // and then we would have to access each string, again
            // in the current implementation, each new line of string immediately becomes a new node of the tree

            // loops over the lines of the file
            while ((line = reader.readLine()) != null) {

                E elem; // placeholder for node's data

                // since the code should only work for Strings and Integers
                // if possible, converting String to Integer, and then Integer to E
                // otherwise, converting String to E

                if (!line.equals(leaf_mark)) {
                    try {
                        Integer number = Integer.parseInt(line);
                        elem = (E) number;
                    } catch (NumberFormatException e) {
                        elem = (E) line;
                    }

                    // creates new node
                    Node nnd = new Node(elem);

                    // checks if current new node should be the root
                    if (first_pass) {
                        root = nnd; // new node is root
                        node_stack.push(root); // add root to the stack, becoming the first element
                        first_pass = false; // now the tree has a root

                    } else {
                        Node pnd = node_stack.peek(); // getting the top element without removing it
                        nnd.parent = pnd; // new node linked to the top element
                        nnd.parent.addChild(nnd); // top element linked to the new node
                        node_stack.push(nnd); // adding the new node to the stack, becoming the top element of the stack
                    }
                }
                // checking if the top element of the stack is a leaf -or-
                // has no other children to be added to the tree
                // if so, it gets removed from the stack
                else if (line.equals(leaf_mark)) {
                    node_stack.pop(); // removing the top element
                }
            }

        } catch (IOException e) {
            e.printStackTrace();

        } finally {

            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String [] args) {
        try {
            List<String> food = Arrays.asList("Food", "Plant", "Animal", "Roots", "Leaves", "Fruits", "Fish", "Mammals", "Birds", "Potatoes", "Carrots", "Lettuce", "Cabbage", "Apples", "Pears", "Plums", "Oranges", "Salmon", "Tuna", "Beef", "Lamb", "Chicken", "Duck", "Wild", "Farm", "GrannySmith", "Gala");
            List<Integer> foodparents = Arrays.asList(-1, 0, 0, 1, 1, 1, 2, 2, 2, 3, 3, 4, 4, 5, 5, 5, 5, 6, 6, 7, 7, 8, 8, 17, 17, 13, 13);
            NTree<String> foodtree = new NTree(food, foodparents);

            foodtree.serialize("foodtree.out");
            NTree<String> foodtree2 = new NTree<>();
            foodtree2.deserialize("foodtree.out");

            System.out.println(foodtree.equals(foodtree2));

            List<Integer> intvalues = Arrays.asList(9, 6, 5, 4, 2, 10, 7, 1, 3, 8, 11, 12, 13, 14);
            List<Integer> intparents = Arrays.asList( -1, 0, 1, 1, 1, 2, 2, 2, 3, 3, 8, 8, 8, 8);
            NTree<Integer> inttree = new NTree<>(intvalues, intparents);

            NTree<Integer> inttree2 = new NTree<>();
            inttree.serialize("inttree.out");
            inttree2.deserialize("inttree.out");

            System.out.println(inttree.equals(inttree2));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
