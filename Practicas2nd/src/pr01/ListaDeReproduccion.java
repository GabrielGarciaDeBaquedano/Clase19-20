package pr01;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.logging.*;
import java.util.regex.*;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public class ListaDeReproduccion implements ListModel<String> {
	ArrayList<File> ficherosLista; // ficheros de la lista de reproduccion
	ArrayList<Boolean> ficherosErroneos; // ficheros de esa lista que son erroneos 
	int ficheroEnCurso = -1; // Fichero seleccionado (-1 si no hay ninguno seleccionado)

	private static final boolean ANYADIR_A_FIC_LOG = false; 

	// Logger de la clase
	private static Logger logger = Logger.getLogger(ListaDeReproduccion.class.getName());
	static {
		try {
			logger.setLevel(Level.FINEST);
			Formatter f = new SimpleFormatter() {
				@Override
				public synchronized String format(LogRecord record) {
					if (record.getLevel().intValue() < Level.CONFIG.intValue())
						return "\t\t(" + record.getLevel() + ") " + record.getMessage() + "\n";
					if (record.getLevel().intValue() < Level.WARNING.intValue())
						return "\t(" + record.getLevel() + ") " + record.getMessage() + "\n";
					return "(" + record.getLevel() + ") " + record.getMessage() + "\n";
				}
			};
			FileOutputStream fLog = new FileOutputStream(ListaDeReproduccion.class.getName() + ".log",
					ANYADIR_A_FIC_LOG);
			Handler h = new StreamHandler(fLog, f);
			h.setLevel(Level.FINEST);
			logger.addHandler(h); // Saca todos los errores 
			logger.addHandler(new FileHandler(ListaDeReproduccion.class.getName() + ".log.xml", ANYADIR_A_FIC_LOG));
		} catch (SecurityException | IOException e) {
			logger.log(Level.SEVERE, "No se ha podido crear fichero de log en clase ListaDeReproduccion");
		}
		logger.log(Level.INFO, "");
		logger.log(Level.INFO, DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(new Date()));
	}

	public ListaDeReproduccion() {
		ficherosLista = new ArrayList<File>();
		ficherosErroneos = new ArrayList<Boolean>();
	}

	public int add(String carpetaFicheros, String filtroFicheros) {
		int ficsAnyadidos = 0;
		if (carpetaFicheros != null) {
			logger.log(Level.INFO, "Añadiendo ficheros con filtro " + filtroFicheros);
			try {
				filtroFicheros = filtroFicheros.replaceAll("\\.", "\\\\."); 
				filtroFicheros = filtroFicheros.replaceAll("\\*", ".*"); 
				logger.log(Level.INFO, "expresi�n regular del filtro: " + filtroFicheros);
				Pattern pFics = Pattern.compile(filtroFicheros, Pattern.CASE_INSENSITIVE);
				File fInic = new File(carpetaFicheros);
				if (fInic.isDirectory()) {
					for (File f : fInic.listFiles()) {
						logger.log(Level.FINE, "Procesando fichero " + f.getName());
						if (pFics.matcher(f.getName()).matches()) {
							logger.log(Level.INFO, "Añadido video a la lista de reproduccion " + f.getName());
							add(f);
						}
					}
				}
			} catch (PatternSyntaxException e) {
				logger.log(Level.SEVERE, "Error en patr�n de expresion regular ", e);
			}
		}
		logger.log(Level.INFO, "ficheros añadidos: " + ficsAnyadidos);
		return ficsAnyadidos;
	}

	public File getFic(int posi) throws IndexOutOfBoundsException {
		return ficherosLista.get(posi);
	}

	public void intercambia(int posi1, int posi2) {
		if (posi1 < 0 || posi2 < 0 || posi1 >= ficherosLista.size() || posi2 > ficherosLista.size())
			return;
		File temp = ficherosLista.get(posi1);
		ficherosLista.set(posi1, ficherosLista.get(posi2));
		ficherosLista.set(posi2, temp);
		boolean tempB = ficherosErroneos.get(posi1);
		ficherosErroneos.set(posi1, ficherosErroneos.get(posi2));
		ficherosErroneos.set(posi2, tempB);
	}

	public int size() {
		return ficherosLista.size();
	}

	public void add(File f) {
		ficherosLista.add(f);
		ficherosErroneos.add(false);
		avisarAnyadido(ficherosLista.size() - 1);
	}

	public void removeFic(int posi) throws IndexOutOfBoundsException {
		ficherosLista.remove(posi);
		ficherosErroneos.remove(posi);
	}

	public void clear() {
		ficherosLista.clear();
		ficherosErroneos.clear();
	}

	//
	// Metodos de seleccion
	//

	public boolean irAPrimero() {
		ficheroEnCurso = 0; // Inicia
		while (ficheroEnCurso < ficherosLista.size() && ficherosErroneos.get(ficheroEnCurso))
			ficheroEnCurso++; // Y si es erroneo busca el siguiente
		if (ficheroEnCurso >= ficherosLista.size()) {
			ficheroEnCurso = -1; // Si no se encuentra, no hay seleccion
			return false; // Y devuelve error
		}
		return true;
	}

	public boolean irAUltimo() {
		ficheroEnCurso = ficherosLista.size() - 1; // Inicia al final
		while (ficheroEnCurso >= 0 && ficherosErroneos.get(ficheroEnCurso))
			ficheroEnCurso--; // Y si es erroneo busca el anterior
		if (ficheroEnCurso == -1) { // Si no se encuentra, no hay seleccion
			return false; // Y devuelve error
		}
		return true;
	}

	public boolean irAAnterior() {
		if (ficheroEnCurso >= 0)
			ficheroEnCurso--;
		while (ficheroEnCurso >= 0 && ficherosErroneos.get(ficheroEnCurso))
			ficheroEnCurso--; // Si es erroneo busca el anterior
		if (ficheroEnCurso == -1) { // Si no se encuentra, no hay seleccion
			return false; // Y devuelve error
		}
		return true;
	}

	public boolean irASiguiente() {
		ficheroEnCurso++;
		while (ficheroEnCurso < ficherosLista.size() && ficherosErroneos.get(ficheroEnCurso))
			ficheroEnCurso++; // Si es erroneo busca el siguiente
		if (ficheroEnCurso >= ficherosLista.size()) {
			ficheroEnCurso = -1; // Si no se encuentra, no hay seleccion
			return false; // Y devuelve error
		}
		return true;
	}

	public int getFicSeleccionado() {
		return ficheroEnCurso;
	}

	private static Random genAleat = new Random();

	public boolean irARandom() {
		if (ficherosLista.size() == 0) {
			ficheroEnCurso = -1;
			return false; // Error
		}
		for (int i = 0; i < 500; i++) { 
			ficheroEnCurso = genAleat.nextInt(ficherosLista.size());
			if (!ficherosErroneos.get(ficheroEnCurso))
				return true; // Si no es erroneo, se va a esta seleccion. Si lo es, se vuelve a intentar
		}
		return false;
	}

	//
	// Metodos de ficheros erroneos
	//

	public void setFicErroneo(int posi, boolean erroneo) throws IndexOutOfBoundsException {
		ficherosErroneos.set(posi, erroneo);
	}

	public boolean isErroneo(int posi) throws IndexOutOfBoundsException {
		return ficherosErroneos.get(posi);
	}

	//
	// Metodos de DefaultListModel
	//

	@Override
	public int getSize() {
		return ficherosLista.size();
	}

	@Override
	public String getElementAt(int index) {
		return ficherosLista.get(index).getName();
	}

	// Escuchadores de datos de la lista
	ArrayList<ListDataListener> misEscuchadores = new ArrayList<>();

	@Override
	public void addListDataListener(ListDataListener l) {
		misEscuchadores.add(l);
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		misEscuchadores.remove(l);
	}

	// Llamar a este metodo cuando se añada un elemento a la lista
	// (Utilizado para avisar a los escuchadores de cambio de datos de la lista)
	private void avisarAnyadido(int posi) {
		for (ListDataListener ldl : misEscuchadores) {
			ldl.intervalAdded(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, posi, posi));
		}
	}
}