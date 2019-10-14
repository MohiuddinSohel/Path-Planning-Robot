/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pathplanningrobot;


import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author mahmed27
 */
public class Utility {
    /*
    * Search the goal using A* informed search algorithm
    */
    public static SearchTreeNode<Point> searchGoalUsingAStar(Point sPoint, Point gPoint
            , ArrayList<ArrayList<Point>> polygonList, RobotEnvPanel env, int timeToSleep) throws InterruptedException {
        
        if(sPoint == null || gPoint == null) return null;
        
        SearchTreeNode<Point> startingPoint = new SearchTreeNode<>(sPoint);
        startingPoint.setStraightLinePathCostHeuristicHn((int)Utility.distanceBetweenTwoPoint(sPoint, gPoint));
        SearchTreeNode<Point> goalPoint = new SearchTreeNode<>(gPoint);
        
        ArrayList<SearchTreeNode<Point>> openNodeList = new ArrayList();
        ArrayList<SearchTreeNode<Point>> closedStateList = new ArrayList();
        openNodeList.add(startingPoint); // Add initial state to open List
        
        env.setVisitedNodeList(closedStateList);
        env.setExpandedNodeList(openNodeList);
                
        while(!openNodeList.isEmpty()) {
            if(openNodeList.get(0).getState().equals(gPoint)) {
                closedStateList.add(openNodeList.get(0));
                openNodeList.remove(0);
                return closedStateList.get(closedStateList.size() - 1);
            }
            
            // Generate all children of a node 
            ArrayList<SearchTreeNode<Point>> generatedNode = Utility.findNextState(polygonList, openNodeList.get(0)
                    , goalPoint);
            
            // Add all of the generated children to the openList(node to be expanded), if it is not already visited
            for(int i = 0; i < generatedNode.size(); i++) {
                openNodeList.add(generatedNode.get(i));
                env.drawPoint(generatedNode.get(i).getState(), null, Color.GRAY);
            }
            
            // Add the current node to closedList(list of visited node)
            closedStateList.add(openNodeList.get(0));
            env.drawPoint(openNodeList.get(0).getState(), null, Color.BLUE);
            
            //Remove the current node from openList
            openNodeList.remove(0);
            Thread.sleep(timeToSleep * 1000);
            // Sort the openList in ascending order based on compareTo() method of each SearchTreeNode which uses g(n)+h(n) to compare
            if(!openNodeList.isEmpty()) {
                Collections.sort(openNodeList);
            }
        }
        return null;
    }
    
    
    
    /*
    * Check whether a node is already visited or not
    */
    public static boolean isAlreadyVisited(ArrayList<SearchTreeNode<Point>> openStateList, ArrayList<Point> closedStateList, Point currentState) {
        if(closedStateList.size() <= 0) {
            return false;
        }
        for(int i = 0; i < closedStateList.size(); i++) {
            if(currentState.equals(closedStateList.get(i))) {
                return true;
            }
        }
        for(int i = 0; i < openStateList.size(); i++) {
            if(currentState.equals(openStateList.get(i).getState())) {
                return true;
            }
        }
        return false;
    }
    
    /*
    * Check whether two line intersect except in the endpoint of line segment
    */
    public static boolean doesIntersectLines(Point point1, Point point2, Point point3, Point point4, ArrayList<ArrayList<Point>> polygonList) {
        Line2D line1 = new Line2D.Double(point1, point2);
        Line2D line2 = new Line2D.Double(point3, point4);
        boolean result = line2.intersectsLine(line1);
        if(result) {
            result = !(point1.equals(point3) || point1.equals(point4) || point2.equals(point3) || point2.equals(point4));
            if(!result) {
                result = Utility.IsTwoPointInSamePolygon(point1, point2, polygonList);
            }
        }
        return result;
    }
    
    /*
    * check whether a line between two point is an edge of an obstacle
    */
    public static boolean IsTwoPointInSamePolygon(Point point1, Point point2, ArrayList<ArrayList<Point>> polygonList) {
        if(point1.equals(point2)) {
            return false;
        }
        for(ArrayList<Point> polygon : polygonList) {
            int point1Index = -1;
            int point2Index = -1;
            for (int i = 0; i < polygon.size(); i++) {
                if(point1.equals(polygon.get(i))) {
                    point1Index = i;
                } else if(point2.equals(polygon.get(i))) {
                    point2Index = i;
                }
            }
            if(point1Index != -1 && point2Index != -1) {
                return (Math.abs(point1Index - point2Index) > 1 || Math.abs(point1Index - point2Index) == polygon.size() - 1);
            }
        }
        return false;
    }
    
    /*
    * Calculate the distance between two point
    */
    public static double distanceBetweenTwoPoint(Point point1, Point point2) {
        return point1.distance(point2);
    }
    
    /*
    * Check whether a point is reachable from current state without intersecting any of the obstacle
    */
    public static boolean isAPointReachable(Point currentPoint, Point nextPoint, ArrayList<ArrayList<Point>> polygonList) {
        boolean result = true;
        for(ArrayList<Point> polygon : polygonList) {
            for(int i = 0; i < polygon.size(); i++) {
                if(i == 0) {
                    continue;
                }
                if(Utility.doesIntersectLines(currentPoint, nextPoint, polygon.get(i - 1), polygon.get(i), polygonList)) {
                    result = false;
                    break;
                }
            }
            if(!result) {
                break;
            }
            if(Utility.doesIntersectLines(currentPoint, nextPoint, polygon.get(0), polygon.get(polygon.size() - 1), polygonList)) {
                result = false;
                break;
            }
        }
        return result;
    }
    
    /*
    * Generate all reachable state from currentState
    */
    public static ArrayList<SearchTreeNode<Point>> findNextState(ArrayList<ArrayList<Point>> polygonList
            , SearchTreeNode<Point> currentState, SearchTreeNode<Point> goalState) {
        ArrayList<SearchTreeNode<Point>> nextStateList = new ArrayList<>();
        for(ArrayList<Point> polygon : polygonList) {
            for(Point point : polygon) {
                if(point.equals(currentState.getState())) continue;
                if(Utility.isAPointReachable(currentState.getState(), point, polygonList)) {
                    SearchTreeNode<Point> node = new SearchTreeNode(point);
                    node.setParent(currentState);
                    node.setPathCostGn(currentState.getPathCostGn() + (int)Utility.distanceBetweenTwoPoint(currentState.getState(), point));
                    node.setStraightLinePathCostHeuristicHn((int)Utility.distanceBetweenTwoPoint(goalState.getState(), point));
                    nextStateList.add(node);
                }
            }
        }
        if(Utility.isAPointReachable(currentState.getState(), goalState.getState(), polygonList)) {
            SearchTreeNode<Point> node = new SearchTreeNode(goalState.getState());
            node.setParent(currentState);
            node.setPathCostGn(currentState.getPathCostGn() + (int)Utility.distanceBetweenTwoPoint(currentState.getState(), goalState.getState()));
            node.setStraightLinePathCostHeuristicHn(0);
            nextStateList.add(node);
        }
        
        return nextStateList;
    }
  
}
