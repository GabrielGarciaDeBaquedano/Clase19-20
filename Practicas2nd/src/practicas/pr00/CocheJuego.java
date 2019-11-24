package practicas.pr00;

public class CocheJuego extends Coche {

	private JLabelCoche miGrafico;
	
    public CocheJuego() {
        this.miGrafico = new JLabelCoche();
    }
	
	public JLabelCoche getGrafico() {
        return this.miGrafico;
    }

	@Override
    public void setPosX(final double posX) {
        super.setPosX(posX);
        this.miGrafico.setLocation((int)posX, (int)this.posY);
    }
    
    @Override
    public void setPosY(final double posY) {
        super.setPosY(posY);
        this.miGrafico.setLocation((int)this.posX, (int)posY);
    }
    
    @Override
    public void setMiDireccionActual(final double dir) {
        super.setMiDireccionActual(dir);
        this.miGrafico.setGiro(this.miDireccionActual);
        this.miGrafico.repaint();
    }
	
}
