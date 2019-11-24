package practicas.pr00;

public class Coche {

	private double miVelocidad; // Velocidad en pixels/segundo
	protected double miDireccionActual; // Dirección en la que estoy mirando en grados (de 0 a 360)
	protected double posX; // Posición en X (horizontal)
	protected double posY; // Posición en Y (vertical)
	private String piloto; // Nombre de piloto
	
	public Coche() {

		this.miVelocidad = 0.0;
		this.miDireccionActual = 0.0;
	    this.posX = 300.0;
	    this.posY = 300.0;
		
	}

	public double getMiVelocidad() {
		return miVelocidad;
	}

	public void setMiVelocidad(double miVelocidad) {
		this.miVelocidad = miVelocidad;
	}

	public double getMiDireccionActual() {
		return miDireccionActual;
	}

	public void setMiDireccionActual(double miDireccionActual) {
		if (miDireccionActual < 0.0) {  // De este modo hacemos que el numero no sea demasiado largo
			miDireccionActual += 360.0; // nos podria dar error
		}
		if (miDireccionActual > 360.0) { // De este modo hacemos que el numero no sea demasiado largo
			miDireccionActual -= 360.0;  // nos podria dar error
		}
		this.miDireccionActual = miDireccionActual;
	}

	public double getPosX() {
		return posX;
	}

	public void setPosX(double posX) {
		this.posX = posX;
	}

	public double getPosY() {
		return posY;
	}

	public void setPosY(double posY) {
		this.posY = posY;
	}

	public String getPiloto() {
		return piloto;
	}

	public void setPiloto(String piloto) {
		this.piloto = piloto;
	}
	
    /** Cambia la velocidad actual del coche
	 * @param aceleracion Incremento de la velocidad en pixels/segundo
	 */
	public void acelera( double aceleracion ) {
		this.miVelocidad += aceleracion;
	} 
    
	/** Cambia la dirección actual del coche
	 * @param giro Angulo de giro a sumar o restar de la dirección actual, en grados (-180 a +180)
	 */
	public void gira( double giro ) {
		this.setMiDireccionActual(this.miDireccionActual + giro);
	}
    
	/** Cambia la posición del coche dependiendo de su velocidad y dirección
	 * @param tiempoDeMovimiento Tiempo transcurrido, en segundos
	 */
	public void mueve( double tiempoDeMovimiento ) {
		this.setPosX(this.posX + this.miVelocidad * Math.cos(this.miDireccionActual / 180.0 * Math.PI) * tiempoDeMovimiento);
		this.setPosY(this.posY + this.miVelocidad * -Math.sin(this.miDireccionActual / 180.0 * Math.PI) * tiempoDeMovimiento);
	} 
	
    @Override
    public String toString() {
        return String.valueOf(this.piloto) + " (" + this.posX + "," + this.posY + ") - " + "Velocidad: " + this.miVelocidad + " ## Direccion: " + this.miDireccionActual;
    }
	
}
