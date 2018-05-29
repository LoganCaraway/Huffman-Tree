/*
 * Author: Logan Caraway
 * Date Created: 5/24/2018
 * Purpose: It's a Huffman Tree
 */
package pkg232huffmantree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import javax.swing.JOptionPane;

public class HuffmanTree {
    //--------------------Nested-Node-Class--------------------//
    //---------------------------------------------------------//
    private class Node implements Comparable{
        private final char key;
        private int frequency;
        private Node left_child, right_child;
        
        /*Node constructor*/
        public Node(char key, int frequency) {
            this.key = key;
            this.frequency = frequency;
        }
        
        /*Methods to get the key and frequency stored in this node*/
        public char getKey() {return key;}
        public int getFrequency() {return frequency;}
        
        /*Methods for retrieving child nodes*/
        public Node getLeft() {return left_child;}
        public Node getRight() {return right_child;}
        
        /*Methods for setting frequency and child nodes*/
        public void setFrequency(int frequency) {this.frequency = frequency;}
        public void setLeft(Node left) {left_child = left;}
        public void setRight(Node right) {right_child = right;}

        @Override
        public int compareTo(Object o) {
            //if they are different classes, return -1
            if (o.getClass() != this.getClass())
                return -1;
            //compare Nodes based upon code +/- 2 depending on which is greater, 0 if same
            int cmp = this.frequency - ((Node)o).getFrequency();
            
            if (cmp > 0)
                return 2;
            else if (cmp < 0)
                return -2;
            else return 0;
        }    
    }
    //---------------------------------------------------------//
    //---------------------------------------------------------//
    
    //Huffman Tree field variables
    private boolean created;
    private Map<Character, Integer> map;
    private Map<Character, String> map2;
    private int size;
    private File f;
    
    /*Constructor*/
    public HuffmanTree() {
        created = false;
        map = new HashMap<>();
        map2 = new HashMap<>();
        size = 0;
        f = null;
    }
    
    /*Returns the number of characters in the initial message*/
    public int size() {return size;}
    
    /*Intakes a file and builds the HuffmanTree*/
    public void makeTreeFromFile() {
        //if the tree has already been builts, return
        if (created) {
            JOptionPane.showMessageDialog(null, "This Huffman tree has already been built");
            return;
        }
        Scanner fin = null;
        boolean valid = false;
        while (!valid) {
            try {
                f = new File(JOptionPane.showInputDialog("Enter input file name (default: \"input.txt\"):"));
                fin = new Scanner(f);
                valid = true;
            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(null, "file not found");
            }
        }
        
        while (fin.hasNextLine()) {
            for (char c : fin.nextLine().toCharArray()) {
                if (!map.containsKey(c))
                    map.put(c, 1);
                else {
                    int freq = (int) map.get(c);
                    map.remove(c);
                    freq++;
                    map.put(c, freq);
                }
            }
        }
        
        buildTree(map);
        created = true;
    }
    
    /*Given a filled map, this functions moves the items to a queue, builds the tree, then makes a bitmap of the characters*/
    /*Note: resets map variable*/
    private void buildTree(Map map) {
        PriorityQueue<Node> queue = new PriorityQueue<>(map.size());
        Node temp;
        
        //move all Nodes from the map to the queue then clears the map
        for(Object c : map.keySet()) {
            queue.add(new Node((char)c, (int)map.get(c)/*, -1*/));
        }
        map.clear();
        
        //combine Nodes from queue into tree
        while (queue.size() > 1) {
            //make a new Node with a key of null
            temp = new Node('\0', 0/*, -1*/);
            
            //remove the two Nodes with smallest frequencies and set them as the new Node's children
            temp.setLeft(queue.poll());
            temp.setRight(queue.poll());
            
            //set the new Node's frequency as the combined frequency of the children
            temp.setFrequency(temp.getLeft().getFrequency() + temp.getRight().getFrequency());
            queue.add(temp);
        }
        //updates size for use in size() method
        size = queue.peek().getFrequency();
        makeBitmap(queue.poll(), "");
    }
    
    /*Makes a bitmap of the tree.*/
    private void makeBitmap(Node currentNode, String s) {
        //when the key isn't null, this is a leaf
        if (currentNode.getKey() != '\0') {
            
            try {
                map.put(currentNode.getKey(), currentNode.getFrequency());
                map2.put(currentNode.getKey(), s);
            } catch (NumberFormatException e) {
                System.out.println(s+"Error mapping Node with key: "+currentNode.getKey());
            }
            return;
        }
        //go left and add 0 to the code
        makeBitmap(currentNode.getLeft(), s + '0');
        //go right and add 1 to the code
        makeBitmap(currentNode.getRight(), s + '1');
    }
    
    /*Prints bitmappings and encoded message to an output file*/
    public void printToFile() {
        if (!created) {
            JOptionPane.showMessageDialog(null, "Huffman tree not yet created");
            return;
        }
        
        boolean valid = false;
        Scanner fin = null; //input file
        PrintWriter fout = null; //output file
        Object temp[]; //temp file to move keys from hashmap to key_array
        char key_array[] = new char[map.size()]; //holds keys from hashmap
        
        //ensures user enters valid files
        while (!valid) {
            try {
                fin = new Scanner(f);
                fout = new PrintWriter(new File(JOptionPane.showInputDialog("Enter output file name: ")));
                valid = true;
            } catch (FileNotFoundException e) {}
        }
        
        //move hashmap keys to object[], from there to char[]
        temp = map.keySet().toArray();
        for (int i = 0; (i < temp.length) && (temp[i] != null); i++) {
            key_array[i] = (char) temp[i];
        }
        
        //print bitcode mapping and frequency
        fout.println("Key|Freq|Bitcode");
        for (int i = 0; (i < key_array.length) && (key_array[i] != '\0'); i++) {
            fout.format("%2c |%3d |%s\n", key_array[i], map.get(key_array[i]), map2.get(key_array[i]));
        }
        //print translated message
        fout.println("\nMessage:");
        while (fin.hasNextLine()) {
            for (char c : fin.nextLine().toCharArray()) {
                fout.print(map2.get(c));
            }
            fout.println();
        }
        fin.close();
        fout.close();
        JOptionPane.showMessageDialog(null, "Printing Done");
    }
    
    /*Decodes a file (file must have been created by this program)*/
    public void decodeFile() {
        Map<String, Character> decoding_map = new HashMap<>();
        char letter;
        String code;
        Scanner msg = null;
        PrintWriter decoded = null;
        String line;
        boolean valid = false;
        
        //ensure valid files
        while (!valid) {
            try {
                msg = new Scanner(new File(JOptionPane.showInputDialog("File to decode: ")));
                decoded = new PrintWriter(new File(JOptionPane.showInputDialog("Write decoded message to: ")));
                valid = true;
            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(null, "file not found");
            }
        }
        valid = false;
        
        while (msg.hasNextLine() && !valid) 
            if (msg.nextLine().equals("Key|Freq|Bitcode"))
                valid = true;
        if (!valid) {
            JOptionPane.showMessageDialog(null, "File was formatted incorrectly");
            return;
        }
        
        //make a map of letters and their bitcode
        while (msg.hasNextLine()) {
            line = msg.nextLine();
            
            //an empty line signifies the end of the chart section of the file
            if (line.equals(""))
                break;
            letter = line.charAt(1);
            code = line.substring(9);
            decoding_map.put(code, letter);
        }
        
        //decode message
        msg.nextLine();
        while (msg.hasNextLine()) {
            line = msg.nextLine();
            code = "";
            for (char c : line.toCharArray()) {
                code += c;
                if (decoding_map.get(code) != null) {
                    decoded.print(decoding_map.get(code));
                    code = "";
                }
            }
            decoded.println();
        }
        decoded.close();
        JOptionPane.showMessageDialog(null, "Decoding Done");
    }
}
