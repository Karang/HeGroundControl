package fr.heliumteam.flightcontrol.comp;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.LinearGradientPaint;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

public class Horizon extends JComponent {
	private static final long serialVersionUID = 1L;
	private static final int W = 256;
	private static final int H = 256;
	
	public enum luminosity {LIGHT, DARK};
    public luminosity brightness = luminosity.DARK;

	private static Color SKY_COLOR = new Color(63, 176, 234);
	private static Color GROUND_COLOR = new Color(106, 48, 11);
	
	private float pitch = 0;
	private float roll = 0;
	
	private BufferedImage backgroundImage = null;
	private BufferedImage horizonImage = null;
	private BufferedImage highlightImage = null;
	
	public Horizon() {
		super();
		setPreferredSize(new Dimension(W, H));
        setSize(W, H);
		init();
	}
	
	private void init() {
		if (backgroundImage != null) {
            backgroundImage.flush();
        }
        backgroundImage = createInstrumentBackground();
        if (horizonImage != null) {
        	horizonImage.flush();
        }
        horizonImage = createHorizon();
		if (highlightImage != null) {
            highlightImage.flush();
        }
        highlightImage = createHighlight();
	}
	
	private BufferedImage createInstrumentBackground() {
        GraphicsConfiguration gfxConf = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        final BufferedImage IMAGE = gfxConf.createCompatibleImage(getWidth(), getHeight(), Transparency.TRANSLUCENT);

        Graphics2D g2 = IMAGE.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        // ******************* INSTRUMENT FRAME *****************************************
        final Point2D FRAME1_CENTER = new Point2D.Double(getWidth() / 2.0f, getHeight() / 2.0f);
        final Point2D FRAME2_START = new Point2D.Double(0, (getHeight() - (getHeight() * 0.92d)) / 2);
        final Point2D FRAME2_STOP = new Point2D.Double(0, getHeight() - (getHeight() - (getHeight() * 0.92d)) / 2);
        final Point2D FRAME3_CENTER = new Point2D.Double(getWidth() / 2.0f, getHeight() / 2.0f);
        

        final float[] FRAME1_FRACTIONS = {
            0.75f,
            1.0f
        };

        final float[] FRAME2_FRACTIONS = {
            0.0f,
            1.0f
        };

        final float[] FRAME3_FRACTIONS = {
            0.94f,
            1.0f
        };


        final Color[] FRAME1_COLORS = {
            new Color(0xFFFFFF),
            new Color(0x7B7B7B)
        };

        final Color[] FRAME2_COLORS = {
            new Color(0x666666),
            new Color(0xFFFFFF)
        };

        final Color[] FRAME3_COLORS = {
            new Color(0xFFFFFF),
            new Color(0xCCCCCC)
        };

        final RadialGradientPaint FRAME1_PAINT = new RadialGradientPaint(FRAME1_CENTER, getWidth() / 2.0f, FRAME1_FRACTIONS, FRAME1_COLORS);
        final LinearGradientPaint FRAME2_PAINT = new LinearGradientPaint(FRAME2_START, FRAME2_STOP, FRAME2_FRACTIONS, FRAME2_COLORS);
        final RadialGradientPaint FRAME3_PAINT = new RadialGradientPaint(FRAME3_CENTER, (getWidth() * 0.86f) / 2.0f, FRAME3_FRACTIONS, FRAME3_COLORS);

        g2.setPaint(FRAME1_PAINT);
        g2.fill(getFrameShape1());

        g2.setPaint(FRAME2_PAINT);
        g2.fill(getFrameShape2());
        g2.setColor(new Color(0xC7C7C7));
        g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(getFrameShape2());

        g2.setPaint(FRAME3_PAINT);
        g2.fill(getFrameShape3());

        // ******************* INSTRUMENT BACKGROUND ************************************
        final Point2D BACKGROUND_START = new Point2D.Double(0, (getWidth() - (getWidth() * 0.80d)) / 2);
        final Point2D BACKGROUND_STOP = new Point2D.Double(0, getHeight() - (getWidth() - (getWidth() * 0.80d)) / 2);

        final float[] BACKGROUND_FRACTIONS = {
            0.0f,
            0.45f,
            1.0f
        };

        final Color[] BACKGROUND_COLORS;
        switch (brightness) {
            case DARK:
                BACKGROUND_COLORS = new Color[]
                {
                    new Color(0x000000),
                    new Color(0x333333),
                    new Color(0x999999)
                };
                break;
            case LIGHT:
                BACKGROUND_COLORS = new Color[]
                {
                    new Color(0xFFFFFF),
                    new Color(0xCCCCCC),
                    new Color(0x666666)
                };
                break;
            default:
                BACKGROUND_COLORS = new Color[]
                {
                    new Color(0x000000),
                    new Color(0x333333),
                    new Color(0x999999)
                };
                break;
        }

        final LinearGradientPaint BACKGROUND_PAINT = new LinearGradientPaint(BACKGROUND_START, BACKGROUND_STOP, BACKGROUND_FRACTIONS, BACKGROUND_COLORS);

        g2.setPaint(BACKGROUND_PAINT);
        g2.fill(getBackgroundShape());

        // ******************* INSTRUMENT BACKGROUND INNER SHADOW ***********************
        final Point2D INNER_SHADOW_CENTER = new Point2D.Double(getWidth() / 2.0d, getHeight() / 2.0d);        
        final float INNER_SHADOW_RADIUS = (getWidth() * 0.8f) / 2.0f;
        
        final float[] INNER_SHADOW_FRACTIONS =
        {
            0.8f,            
            1.0f
        };
        
        final Color[] INNER_SHADOW_COLORS = 
        {
            new Color(0.0f, 0.0f, 0.0f, 0.0f),
            new Color(0.0f, 0.0f, 0.0f, 0.3f)
        };
        
        final RadialGradientPaint INNER_SHADOW_PAINT = new RadialGradientPaint(INNER_SHADOW_CENTER, INNER_SHADOW_RADIUS, INNER_SHADOW_FRACTIONS, INNER_SHADOW_COLORS);

        g2.setPaint(INNER_SHADOW_PAINT);
        g2.fill(getBackgroundShape());
  
        // ******************* LENS EFFECT **************************************************
        final Point2D LENS_EFFECT_START = new Point2D.Double(0, getHeight() * 0.12037f);
        final Point2D LENS_EFFECT_STOP = new Point2D.Double(0, getHeight() * 0.12037f + getHeight() * 0.53f);

        final float[] LENS_EFFECT_FRACTIONS =
        {
            0.0f,
            0.4f
        };

        final Color[] LENS_EFFECT_COLORS =
        {
            new Color(1.0f, 1.0f, 1.0f, 0.2f),
            new Color(1.0f, 1.0f, 1.0f, 0.0f)
        };

        final LinearGradientPaint LENS_EFFECT_PAINT = new LinearGradientPaint(LENS_EFFECT_START, LENS_EFFECT_STOP, LENS_EFFECT_FRACTIONS, LENS_EFFECT_COLORS);

        g2.setPaint(LENS_EFFECT_PAINT);
        g2.fill(new Ellipse2D.Double(getWidth() * 0.25f, getHeight() * 0.12037f, getWidth() * 0.51f, getHeight() * 0.53f));

        g2.dispose();

        return IMAGE;
    }
	
	private BufferedImage createHighlight() {
        GraphicsConfiguration gfxConf = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        final BufferedImage IMAGE = gfxConf.createCompatibleImage(getWidth(), getHeight(), Transparency.TRANSLUCENT);

        Graphics2D g2 = IMAGE.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        Shape highlightShape = getHighlightShape();
        final Point2D HIGHLIGHT_START = new Point2D.Double(0, (getHeight() - (getHeight() * 0.92d)) / 2);
        final Point2D HIGHLIGHT_STOP = new Point2D.Double(0, HIGHLIGHT_START.getY() + highlightShape.getBounds2D().getHeight());

        final float[] HIGHLIGHT_FRACTIONS = {
            0.0f,
            1.0f
        };

        final Color[] HIGHLIGHT_COLORS = {
            new Color(1.0f, 1.0f, 1.0f, 0.25f),
            new Color(1.0f, 1.0f, 1.0f, 0.05f)
        };
        LinearGradientPaint HIGHLIGHT_PAINT = new LinearGradientPaint(HIGHLIGHT_START, HIGHLIGHT_STOP, HIGHLIGHT_FRACTIONS, HIGHLIGHT_COLORS);
        g2.setPaint(HIGHLIGHT_PAINT);
        g2.fill(highlightShape);
        
        // Paint lines
        
        g2.setColor(Color.WHITE);
        
        int half_height = (int)(getHeight()*0.40);
        int large = (int)(getWidth()/12.8);
        int medium = (int)(getWidth()/25.6);
        for (int angle = -90 ; angle < 90 ; angle+=5) {
        	int yPos = (int)(getHeight()/2 + Math.sin(Math.toRadians(angle))*half_height);
        	if (angle%45==0) {
        		g2.drawLine(getWidth()/2-large, yPos, getWidth()/2+large, yPos);
        	} else {
        		g2.drawLine(getWidth()/2-medium, yPos, getWidth()/2+medium, yPos);
        	}
        }

        g2.dispose();

        return IMAGE;
    }
	
	private BufferedImage createHorizon() {
		GraphicsConfiguration gfxConf = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        final BufferedImage IMAGE = gfxConf.createCompatibleImage(getWidth(), getHeight(), Transparency.TRANSLUCENT);

        Graphics2D g2 = IMAGE.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        Area result = new Area(new Rectangle(0, 0, getWidth(), getHeight()/2));
        result.intersect(new Area(getBackgroundShape()));
        g2.setColor(SKY_COLOR);
        g2.fill(result);
        
        result = new Area(new Rectangle(0, getHeight()/2, getWidth(), getHeight()/2));
        result.intersect(new Area(getBackgroundShape()));
        g2.setColor(GROUND_COLOR);
        g2.fill(result);
        
        g2.dispose();

        return IMAGE;
	}
	
	private Shape getFrameShape1() {
        return new Ellipse2D.Double(0, 0, getWidth(), getHeight());
    }

    private Shape getFrameShape2() {
        double origin = (getWidth() - (getWidth() * 0.92d)) / 2;
        return new Ellipse2D.Double(origin, origin, getWidth() * 0.92d, getHeight() * 0.92d);
    }

    private Shape getFrameShape3() {
        double origin = (getWidth() - (getWidth() * 0.86d)) / 2;
        return new Ellipse2D.Double(origin, origin, getWidth() * 0.86d, getHeight() * 0.86d);
    }
	
    private Shape getBackgroundShape() {
        double origin = (getWidth() - (getWidth() * 0.80d)) / 2;
        return new Ellipse2D.Double(origin, origin, getWidth() * 0.80d, getHeight() * 0.80d);
    }
    
	private Shape getHighlightShape() {        
        Shape secondShape = (Shape) (new Ellipse2D.Double((getWidth() - getWidth() * 1.5f) / 2.0f, getHeight() * 0.4f, getWidth() * 1.5f, getHeight()));
        final Area result = new Area(getFrameShape2());
        result.subtract(new Area(secondShape));
        return result;
    }
	
	@Override
    protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        g2.drawImage(backgroundImage, 0, 0, this);
        
        final AffineTransform oldTransform = g2.getTransform();

        int half_height = (int)(getHeight()*0.40);
        int horizon_Y = (int)(getHeight()/2 + Math.sin(Math.toRadians(pitch))*half_height);
        
        g2.rotate(Math.toRadians(roll), getWidth()/2, getHeight()/2);
        
        Area result = new Area(new Rectangle(0, 0, getWidth(), horizon_Y));
        result.intersect(new Area(getBackgroundShape()));
        g2.setColor(SKY_COLOR);
        g2.fill(result);
        
        result = new Area(new Rectangle(0, horizon_Y, getWidth(), getHeight()-horizon_Y));
        result.intersect(new Area(getBackgroundShape()));
        g2.setColor(GROUND_COLOR);
        g2.fill(result);
        

        g2.setTransform(oldTransform);
        
        g2.drawImage(highlightImage, 0, 0, this);
        
        g2.dispose();
    }
	
	/**
	 * @param pitch (Degrees)
	 */
	public void setPitch(float pitch) {
		this.pitch = pitch;
	}
	
	/**
	 * @param roll (Degrees)
	 */
	public void setRoll(float roll) {
		this.roll = roll;
	}
	
}
