/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pathplanningrobot;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 *
 * @author mahmed27
 * Draw the JPanel for obstacle drawing
 */
public class RobotEnvPanel extends JPanel implements MouseListener, MouseMotionListener {
    
    private ArrayList<Point> currentPolygon;
    private ArrayList<ArrayList<Point>> polygonsList;
    private SearchTreeNode<Point> finalShortestPath;
    public static ArrayList<SearchTreeNode<Point>> visitedNode;
    public static ArrayList<SearchTreeNode<Point>> expandedNode;
    private Point startingPoint;
    private Point goalPosition;
    
    private boolean isPolygon;
    private boolean isPosition;
    
    public RobotEnvPanel(Dimension panelSize) {
        currentPolygon = new ArrayList<Point>();
        polygonsList = new ArrayList<ArrayList<Point>>();
        visitedNode = new ArrayList<>();
        expandedNode = new ArrayList<>();
        finalShortestPath = null;
        this.isPolygon = false;
        this.isPosition =false;
        this.startingPoint = null;
        this.goalPosition = null;
        this.setSize(panelSize);
        this.setMinimumSize(panelSize);
        this.setPreferredSize(panelSize);
        this.setMaximumSize(panelSize);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.setVisible(true);
    }
    
    public void setVisitedNodeList(ArrayList<SearchTreeNode<Point>> nodelist) {
        visitedNode = nodelist;
    }
    
    public void setExpandedNodeList(ArrayList<SearchTreeNode<Point>> nodelist) {
        expandedNode = nodelist;
    }
    
    public void setDrawingOfPolygon(boolean val) {
        this.isPolygon = val;
    }
    
    public void setDrawingOfPoint(boolean val) {
        this.isPosition = val;
    }
    
    public Point getStartingPoint() {
        return this.startingPoint;
    }
    
    public void resetStartingPoint() {
        this.startingPoint = null;
    }
    
    public void resetGoalPoint() {
        this.goalPosition = null;
    }
    
    public Point getGoalPoint() {
        return this.goalPosition;
    }
    
    public ArrayList<ArrayList<Point>> gelAllObstacle() {
        return this.polygonsList;
    }
    
    public void resetPosition() {
        this.startingPoint = null;
        this.goalPosition = null;
        this.visitedNode = new ArrayList<>();
        this.expandedNode = new ArrayList<>();
    }
    
    public void resetAllObstacle() {
        this.polygonsList = new ArrayList<>();
        this.currentPolygon = new ArrayList<>();
        this.visitedNode = new ArrayList<>();
        this.expandedNode = new ArrayList<>();
    }
    
    public void setFinalShortestPath(SearchTreeNode<Point> finalShortestPath) {
        this.finalShortestPath = finalShortestPath;
    }
    
    public void addNewPolygon() {
        Graphics2D g2d = (Graphics2D)this.getGraphics();
        if (currentPolygon.size() > 2) {
            drawPolygon(currentPolygon, g2d);
            finishPolygon(currentPolygon, g2d);
            polygonsList.add(currentPolygon);
        }
        currentPolygon = new ArrayList<>();
    }
    
    public void finishPolygon(ArrayList<Point> polygon, Graphics2D g) {
        if (polygon.size() > 2) {
            Point firstVertex = polygon.get(0);
            Point lastVertex = polygon.get(polygon.size() - 1);

            g.setColor(Color.GREEN);
            g.setStroke(new BasicStroke(3));
            g.drawLine((int)firstVertex.getX(), (int)firstVertex.getY(),
                (int)lastVertex.getX(), (int)lastVertex.getY());
        }
    }
    
    public void drawPolygon(ArrayList<Point> polygon, Graphics2D g) {
        g.setColor(Color.GREEN);
        g.setStroke(new BasicStroke(3));
        for (int i = 0; i < polygon.size(); i++) {
            Point currentVertex = polygon.get(i);
            if (i != 0) {
                Point prevVertex = polygon.get(i - 1);
                g.drawLine((int)prevVertex.getX(), (int)prevVertex.getY(),
                        (int)currentVertex.getX(), (int)currentVertex.getY());
            }
        }
    }
    
    public void drawFinalPath(Graphics2D g) {
        if(g == null) {
            g = (Graphics2D)this.getGraphics();
        }
        g.setColor(Color.ORANGE);
        g.setStroke(new BasicStroke(5));
        SearchTreeNode<Point> path = this.finalShortestPath;
        Point currentVertex = path.getState();
        while(path.getParent() != null) {
            Point nextVertex = (Point)path.getParent().getState();
            g.drawLine((int)currentVertex.getX(), (int)currentVertex.getY(),
                    (int)nextVertex.getX(), (int)nextVertex.getY());
            currentVertex = nextVertex;
            path = path.getParent();
        }
    }
    
    public void drawPoint(Point point, Graphics2D g, Color c) {
        if(g == null) {
            g = (Graphics2D)this.getGraphics();
        }
        g.setColor(c);
        g.setStroke(new BasicStroke(2));
        g.fillOval((int)point.getX() - 4, (int)point.getY() - 4, 8, 8);
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        for(ArrayList<Point> polygon : polygonsList) {
            drawPolygon(polygon, g2d);
            finishPolygon(polygon, g2d);
        }
        
        if(currentPolygon.size() > 2) {
            this.polygonsList.add(currentPolygon);
            drawPolygon(currentPolygon, g2d);
            finishPolygon(currentPolygon, g2d);
            currentPolygon = new ArrayList<>();
        }
        
        if(this.startingPoint != null) {
            g2d.setColor(Color.BLUE);
            g2d.fillOval((int)this.startingPoint.getX() - 10, (int)this.startingPoint.getY() - 10, 20, 20);
        }
        if(this.goalPosition != null) {
            g2d.setColor(Color.RED);
            g2d.fillOval((int)this.goalPosition.getX() - 10, (int)this.goalPosition.getY() - 10, 20, 20);
        }
        
        if(this.finalShortestPath != null) {
            this.drawFinalPath(g2d);
        }
        
        for(SearchTreeNode<Point> node : expandedNode) {
            this.drawPoint(node.getState(), g2d, Color.GRAY);
        }
        
        for(SearchTreeNode<Point> node : visitedNode) {
            this.drawPoint(node.getState(), g2d, Color.BLUE);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        if(x > this.getWidth() || y > this.getHeight()) {
           return;
        }
        Graphics2D g = (Graphics2D) this.getGraphics();
        if(isPolygon) {
            g.setColor(Color.GREEN);
            g.fillOval(x - 2, y - 2, 4, 4);
            if (currentPolygon.size() > 0) {
                Point lastVertex = currentPolygon.get(currentPolygon.size() - 1);
                g.setStroke(new BasicStroke(3));
                g.drawLine((int)lastVertex.getX(), (int)lastVertex.getY(), x, y);
            }
            currentPolygon.add(new Point(x, y));
        } else if(isPosition) {
            if(this.startingPoint == null) {
                g.setColor(Color.BLUE);
                g.fillOval(x - 10, y - 10, 20, 20);
                this.startingPoint = new Point(x, y);
            } else if(this.goalPosition == null) {
                g.setColor(Color.red);
                g.fillOval(x - 10, y - 10, 20, 20);
                this.goalPosition = new Point(x, y);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    
}
