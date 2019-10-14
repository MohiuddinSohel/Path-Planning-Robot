/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pathplanningrobot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author mahmed27
 * Draw the JFrame for button and drawing
 */
public class RobotEnvironment extends JFrame {
    private RobotEnvPanel env = null;
    public RobotEnvironment() throws Exception {
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                System.exit(0);
            }
        });
        
        this.setSize(new Dimension(800, 800));
        this.setMinimumSize(new Dimension(800, 800));
        this.setPreferredSize(new Dimension(800, 800));
        this.setMaximumSize(new Dimension(800, 800));
        
        env = new RobotEnvPanel(new Dimension (this.getWidth(), this.getHeight() - 30));
        env.setOpaque(true);
        env.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        env.setBackground(Color.WHITE);
        
        JPanel toolboxPanel = new JPanel();
        toolboxPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        toolboxPanel.setSize(50, 50);
        toolboxPanel.setMinimumSize(new Dimension(this.getWidth(), 40));
        toolboxPanel.setPreferredSize(new Dimension(this.getWidth(), 40));
        toolboxPanel.setMaximumSize(new Dimension(this.getWidth(), 40));
        
        JButton markButton = new JButton("Mark Position");
        markButton.setToolTipText("Mark Start and goal position");
        markButton.addActionListener(new DrawListenerMark());
        toolboxPanel.add(markButton);
        
        JButton newPolyButton = new JButton("Draw Obstacle");
        newPolyButton.setToolTipText("Click to add new Convex obstacle");
        newPolyButton.addActionListener(new DrawListener());
        toolboxPanel.add(newPolyButton);
        
        JButton clearObstacleButton = new JButton("Clear Obstacle");
        clearObstacleButton.setToolTipText("Remove all obstacles");
        clearObstacleButton.addActionListener(new DrawListenerObstacle());
        
        JButton clearPositionButton = new JButton("Clear Position");
        clearPositionButton.setToolTipText("Clear Start and Goal Position");
        clearPositionButton.addActionListener(new DrawListenerClearPosition());
        
        JButton searchButton = new JButton("Search Goal");
        searchButton.setToolTipText("Search Goal using A* informed search algorithm");
        searchButton.addActionListener(new DrawListenerSearch());
        toolboxPanel.add(searchButton);
        
        toolboxPanel.add(clearPositionButton);
        toolboxPanel.add(clearObstacleButton);
        toolboxPanel.setBackground(Color.GRAY);
        
        Container container = this.getContentPane();
        container.setLayout(new BoxLayout(container, BoxLayout.PAGE_AXIS));
        
        container.add(toolboxPanel);
        container.add(env);
        
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
    }
    /**
     * ActionListener for Mark Position button
     */
    class DrawListenerMark implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            env.setDrawingOfPoint(true);
            env.setDrawingOfPolygon(false);
        }
    }
    
    /**
     * ActionListener for draw obstacle button
     */
    class DrawListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            env.setDrawingOfPolygon(true);
            env.setDrawingOfPoint(false);
            env.addNewPolygon();
            env.repaint();
        }
    }
    
    /**
     * ActionListener for Clear button which will clear all of the drawn obstacles
     * And prepare the environment for new position marking and obstacle drawing
     */
    class DrawListenerObstacle implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            env.resetAllObstacle();
            env.setFinalShortestPath(null);
            env.setDrawingOfPolygon(false);
            env.setDrawingOfPoint(false);
            env.repaint();
        }
    }
    
    /**
     * ActionListener for Clear button which will clear all of the drawn obstacles
     * And prepare the environment for new position marking and obstacle drawing
     */
    class DrawListenerClearPosition implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            env.resetPosition();
            env.setFinalShortestPath(null);
            env.setDrawingOfPolygon(false);
            env.setDrawingOfPoint(false);
            env.repaint();
        }
    }
    
    /**
     * ActionListener for Search Goal Button.
     * After marking initial and goal position, and drawing obstacle, one can search the goal by using this Listener 
     */
    class DrawListenerSearch implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            env.setDrawingOfPoint(false);
            env.setDrawingOfPolygon(false);
            Point startingPoint = env.getStartingPoint();
            Point goalPoint = env.getGoalPoint();
            ArrayList<ArrayList<Point>> polygonList = env.gelAllObstacle();
            
            String s = JOptionPane.showInputDialog("Input seconds to wait before generating next State(Integer)");
            int timeToSleep = 0;
            try {
                timeToSleep = Integer.parseInt(s.trim());
            } catch(Exception e) {
                timeToSleep = 0;
            }
            
            SearchTreeNode<Point> goalContainingPath = null;
            try {
                goalContainingPath = Utility.searchGoalUsingAStar(startingPoint, goalPoint, polygonList, env, timeToSleep);
            } catch (InterruptedException ex) {
                goalContainingPath = null;
            }
            if(goalContainingPath != null) {
                env.setFinalShortestPath(goalContainingPath);
                env.drawFinalPath(null);
                env.repaint();
            } else {
                JOptionPane.showMessageDialog(null, "Goal is not reachable", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}
