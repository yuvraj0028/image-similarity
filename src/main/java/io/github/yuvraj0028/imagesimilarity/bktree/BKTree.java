package io.github.yuvraj0028.imagesimilarity.bktree;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.yuvraj0028.imagesimilarity.utils.Hamming;

public class BKTree {
    
    // Node class to depict BKTree and its nodes [children]
    private static class Node{
        final long value;
        final Map<Integer, Node> children = new HashMap<>();
        Node(long value) {this.value = value;}
    }

    // initial state
    private Node root = null;
    public BKTree(){}

    /**
        add the hashes of the images to the BKTree 
        uses hamming dist to find the diff
        if node is found, iterate through children
        if no node found with diff, add to root -> break the loop
    */
    public void add(long value){
        if(root == null){
            root = new Node(value);
            return;
        }

        Node curr = root;
        while(true){
            int dist = Hamming.distanceLong(curr.value, value);
            Node child = curr.children.get(dist);
            if(child != null){
                curr = child;
            } else {
                Node newNode = new Node(value);
                curr.children.put(dist, newNode);
                return;
            }
        }
    }

    /**
        search for the hashes of the images in the BKTree
        using bfs for search, find hamming diff of curr Node and given val
        if diff <= maxDist, add to res
        calc high and low bound for children
        push children to stack if they fall in the range
    */
    public List<Long> search(long value, int maxDist){
        List<Long> res = new ArrayList<>();
        if(root == null) return res;

        Deque<Node> stack = new ArrayDeque<>();
        stack.push(root);
        while(!stack.isEmpty()){
            Node curr = stack.pop();
            int dist = Hamming.distanceLong(curr.value, value);
            if(dist <= maxDist){
                res.add(curr.value);
            }
            int lo = Math.max(0, dist - maxDist);
            int hi = dist + maxDist;
            for (Map.Entry<Integer, Node> e : curr.children.entrySet()) {
                int childDist = e.getKey();
                if (childDist >= lo && childDist <= hi) stack.push(e.getValue());
            }
        }
        return res;
    }

}
