package practicas.pr00;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class JLabelCoche extends JLabel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double miGiro = Math.PI/2;
	public static final int TAMAÑO_COCHE = 100;
	
	public JLabelCoche() {
		
		try {
			this.setIcon(new ImageIcon(JLabelCoche.class.getResource("coche.png")));
	        }
	        catch (Exception e) {
	            System.err.println("Error en carga de recurso: coche.png no encontrado");
	            e.printStackTrace();
	        }
	        this.setBounds(0, 0, 100, 100);
	    }
	
	public void setGiro(final double gradosGiro) {
        this.miGiro = gradosGiro / 180.0 * Math.PI;
        this.miGiro = -this.miGiro;
        this.miGiro += 1.5707963267948966;
    }

  @Override
  protected void paintComponent(final Graphics g) {
      final Image img = ((ImageIcon)this.getIcon()).getImage();
      final Graphics2D g2 = (Graphics2D)g;
      g2.rotate(this.miGiro, 50.0, 50.0);
      g2.drawImage(img, 0, 0, 100, 100, null);
  }
	
}
