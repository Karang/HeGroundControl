package fr.heliumteam.flightcontrol.comp;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

import javax.swing.JPanel;

public class PIDMeter extends JPanel {

	private static final long serialVersionUID = 1L;

	private final int PAD = 20;
	private final Color plot_color;

	private int cur_data = 0;
	private final double[] buffer;
	
	public PIDMeter(int width, int height, Color color, int buffer_size) {
		this.setPreferredSize(new Dimension(width, height));
		this.plot_color = color;
		this.buffer = new double[buffer_size];
	}
	
	protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();
        int mid = h/2;
        // Draw ordinate.
        g2.draw((Shape) new Line2D.Double(PAD, PAD, PAD, h-PAD));

        // Draw abcissa.
        g2.draw(new Line2D.Double(PAD, mid, w-PAD, mid));
        
        double xInc = (double)(w - 2*PAD)/(buffer.length-1);
        double scale = (double)(h - 2*PAD)/360;
        
        // Mark data points.
        g2.setPaint(plot_color);
        double x = PAD;
        double y = h - PAD - scale*buffer[0] - h/2;
        for (int i = 1; i < cur_data; i++) {
            double x2 = PAD + i*xInc;
            double y2 = h - scale*buffer[i] - mid;
            g2.drawLine((int)x, (int)y, (int)x2, (int)y2);
            x = x2;
            y = y2;
        }
	}
	
	public void addData(double d) {
		if (cur_data==buffer.length) {
			for (int i=1 ; i<buffer.length ; i++) {
				buffer[i-1] = buffer[i];
			}
			cur_data--;
		}
		this.buffer[cur_data++] = d;
		//System.out.println(cur_data);
		repaint();
	}
	
}
