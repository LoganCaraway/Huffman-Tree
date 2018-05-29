/*
 * Author: Logan Caraway
 * Date Created: 5/24/2018
 * Purpose: Creates a Huffman Tree and translates the text from an input file into a string of 1s and 0s
 *          representing the bit-translation of the input
 */
package pkg232huffmantree;

public class Main {
    
    public static void main(String[] args) {
        HuffmanTree tree = new HuffmanTree();
        
        //make tree
        tree.makeTreeFromFile();
        
        //print bitmap of characters and the translated message
        tree.printToFile();
        
        //translate the output of this file back to the original message
        tree.decodeFile();
    }
}
