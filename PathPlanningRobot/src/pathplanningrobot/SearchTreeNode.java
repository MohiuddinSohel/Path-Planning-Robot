/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pathplanningrobot;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mahmed27
 */
public class SearchTreeNode<T> implements Comparable<SearchTreeNode<T>> {
    
    private SearchTreeNode<T> parent;
    
    private T state;
    private boolean isVisited;
    private int pathCostGn;         //G(n)
    private int straightLinePathCostHeuristicHn; //h(n)
    
    private List<SearchTreeNode> children;

    public SearchTreeNode(T value) {
        state = value;
        parent = null;
        isVisited = false;
        pathCostGn = 0; // path cost from current point to next point
        straightLinePathCostHeuristicHn = 0; //Straight line path cost from a point to goal position
        children = new ArrayList<>();
    }
    
    public T getState() {
        return this.state;
    }
    
    public boolean getIsVisited() {
        return this.isVisited;
    }
    
    public void setIsVisited(boolean isVisited) {
        this.isVisited = isVisited;
    }
    
    public int getPathCostGn() {
        return this.pathCostGn;
    }
    
    public void setPathCostGn(int depth) {
        this.pathCostGn = depth;
    }
    
    public int getStraightLinePathCostHeuristicHn() {
        return this.straightLinePathCostHeuristicHn;
    }
    
    public void setStraightLinePathCostHeuristicHn(int heuristicCost) {
        this.straightLinePathCostHeuristicHn = heuristicCost;
    }
    
    public void addChildToTree(SearchTreeNode child) {
        child.setParent(this);
        this.children.add(child);
    }

    public void setParent(SearchTreeNode parent) {
        this.parent = parent;
    }

    public SearchTreeNode getParent() {
        return this.parent;
    }

    public List<SearchTreeNode> getChildren() {
        return this.children;
    }
    
    /*
    * This method will be used to sort the node in open list based on G(n) + h(n) 
    */
    @Override
    public int compareTo(SearchTreeNode<T> o) {
        return this.pathCostGn + this.straightLinePathCostHeuristicHn - o.getPathCostGn() - o.getStraightLinePathCostHeuristicHn();
    }
}
