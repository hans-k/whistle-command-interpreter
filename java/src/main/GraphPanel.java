package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JPanel;

/**
 * A Swing UI showing a graph. The input is a Map containing frequency 
 * measurements (x-axis) and how often they have been seen in the last 
 * 20 measurements (y-axis).
 * 
 * @author Hans Kruijsse
 */
@SuppressWarnings("serial")
public class GraphPanel extends JPanel {
	
    private int padding = 25;
    private int labelPadding = 25;
    private Color lineColor = new Color(44, 102, 230, 180);
    private Color gridColor = new Color(200, 200, 200, 200);
    private static final Stroke GRAPH_STROKE = new BasicStroke(2f);
    private int pointWidth = 4;
    private int numberYDivisions = 10;
    
    // First one holds the pseudo-frequency, the second the y-amount.
    private Map<Integer, Integer> valueMap = new ConcurrentHashMap<Integer, Integer>();
	private TriggerCombinationManager triggerCombinationManager;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double xScale = (getWidth() - 2*padding - labelPadding) / 300.0;
        double yScale = (getHeight() - 2*padding - labelPadding) / 10.0;

        List<Point> graphPoints = new ArrayList<>();

        for (Entry<Integer, Integer> entry : valueMap.entrySet()) {
            int x1 = padding + labelPadding + (int)(entry.getKey() * xScale);
            int y1 = (int) ((10 - entry.getValue()) * yScale) + padding;
            graphPoints.add(new Point(x1, y1));
        }
        
        // draw white background
        g2.setColor(Color.WHITE);
        g2.fillRect(padding + labelPadding, padding, getWidth() - (2 * padding) - labelPadding, getHeight() - 2 * padding - labelPadding);
        g2.setColor(Color.BLACK);

        // create hatch marks and grid lines for y axis
        for (int i = 0; i <= numberYDivisions; i++) {
            int x0 = padding + labelPadding;
            int x1 = pointWidth + padding + labelPadding;
            int y0 = getHeight() - ((i * (getHeight() - padding * 2 - labelPadding)) / numberYDivisions + padding + labelPadding);
            int y1 = y0;
            
            //gridline
            g2.setColor(Color.BLACK);
            String yLabel = i + "x";
            FontMetrics metrics = g2.getFontMetrics();
            int labelWidth = metrics.stringWidth(yLabel);
            g2.drawString(yLabel, x0 - labelWidth - 5, y0 + (metrics.getHeight() / 2) - 3);
            
            //hatchmark
            g2.drawLine(x0, y0, x1, y1);
        }
        
        // create hatch marks and grid lines for x axis
        for (int i = 0; i <= 300; i+=10) {
            int x0 = i * (getWidth() - padding * 2 - labelPadding) / (300 - 1) + padding + labelPadding;
            int x1 = x0;
            int y0 = getHeight() - padding - labelPadding;
            int y1 = y0 - pointWidth;
            
            //gridline
            g2.setColor(gridColor);
            g2.drawLine(x0, getHeight() - padding - labelPadding - 1 - pointWidth, x1, padding);
            
            //label
            g2.setColor(Color.BLACK);
            String xLabel = i + "";
            FontMetrics metrics = g2.getFontMetrics();
            int labelWidth = metrics.stringWidth(xLabel);
            g2.drawString(xLabel, x0 - labelWidth / 2, y0 + metrics.getHeight() + 3);
        
            //hatchmark
            g2.drawLine(x0, y0, x1, y1);
        }

        // create x and y axes 
        g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, padding + labelPadding, padding);
        g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, getWidth() - padding, getHeight() - padding - labelPadding);

        if (triggerCombinationManager != null ) {
        	g2.setStroke(GRAPH_STROKE);
        	for (TriggerCombination trc : triggerCombinationManager.triggerCombinationList) {
        		g2.setColor(Color.GREEN);
        		for (int i = 0; i < trc.triggers.size(); i++) {
        			Trigger t = trc.triggers.get(i);
        			if (i >= trc.currentTriggerIndex) {
    					g2.setColor(trc.getColor());
        			}
        			int triggerX = padding + labelPadding + (int)(t.getFrequency() * xScale);
        			int triggerY = (int) ((10 - t.getLevel()) * yScale) + padding;
        			g2.drawLine(triggerX-7, triggerY, triggerX+7, triggerY);
        		}
        	}
        }
        
        g2.setColor(lineColor);
        g2.setStroke(GRAPH_STROKE);
        for (int i = 0; i < graphPoints.size(); i++) {
            int x = graphPoints.get(i).x;
            int y = graphPoints.get(i).y;
            g2.drawLine(x, y, x, getHeight()-50);
        }
    }

    public void setValuesAndRepaint(Map<Integer, Integer> valueMap) {
        this.valueMap = valueMap;
        invalidate();
        this.repaint();
    }

	public void setTriggerCombinationManager(TriggerCombinationManager tcManager) {
		this.triggerCombinationManager = tcManager;
	}
    
}
