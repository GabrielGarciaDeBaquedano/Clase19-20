package pr0304;

import java.io.File;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class FicheroMultimedia implements Serializable {
	private static final long serialVersionUID = 1L;
	public File file;          
	public boolean erroneo;    
	public String titulo;      
	public String cantante;    
	public String comentarios; 

	public FicheroMultimedia(File file, boolean erroneo, String titulo,
			String cantante, String comentarios) {
		super();
		this.file = file;
		this.erroneo = erroneo;
		this.titulo = (titulo==null?"":titulo);
		this.cantante = (cantante==null?"":cantante);
		this.comentarios = (comentarios==null?"":comentarios);
	}
	
	public FicheroMultimedia(File file) {
		this( file, false, "", "", "" );
	}

	public boolean anyadirFilaATabla( Statement st ) {
		// Adicional uno
		if (chequearYaEnTabla(st)) {  // Si está ya en la tabla
			return modificarFilaEnTabla(st);
		}
		// Inserción normal
		try {
			String sentSQL = "insert into fichero_multimedia values(" +
					"'" + file.getAbsolutePath() + "', " +
					"'" + erroneo + "', " +
					"'" + titulo + "', " +
					"'" + cantante + "', " +
					"'" + comentarios + "')";
			System.out.println( sentSQL );  // (Quitar) para ver lo que se hace
			int val = st.executeUpdate( sentSQL );
			if (val!=1) return false;  // Se tiene que añadir 1 - error si no
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean chequearYaEnTabla( Statement st ) {
		try {
			String sentSQL = "select * from fichero_multimedia " +
					"where (fichero = '" + file.getAbsolutePath() + "')";
			System.out.println( sentSQL );  // (Quitar) para ver lo que se hace
			ResultSet rs = st.executeQuery( sentSQL );
			if (rs.next()) {  // Normalmente se recorre con un while, pero aquí solo hay que ver si ya existe
				rs.close();
				return true;
			}
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean modificarFilaEnTabla( Statement st ) {
		try {
			String sentSQL = "update fichero_multimedia set " +
					"error = '" + erroneo + "', " +
					"titulo = '" + titulo + "', " +
					"cantante = '" + cantante + "', " +
					"comentarios = '" + comentarios + "' " +
					"where (fichero = '" + file.getAbsolutePath() + "')";
			System.out.println( sentSQL );  // (Quitar) para ver lo que se hace
			int val = st.executeUpdate( sentSQL );
			if (val!=1) return false;  // Se tiene que modificar 1, error si no
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void cargarDeTabla( Statement st ) {
		try {
			String sentSQL = "select * from fichero_multimedia " +
					"where (fichero = '" + this.file.getAbsolutePath() + "')";
			System.out.println( sentSQL );  // (Quitar) para ver lo que se hace
			ResultSet rs = st.executeQuery( sentSQL );
			if (rs.next()) {  // Normalmente se recorre con un while, pero aquí solo hay que ver si ya existe
				this.erroneo = rs.getBoolean( "error" );
				this.titulo = rs.getString( "titulo" );
				this.cantante = rs.getString( "cantante" );
				this.comentarios = rs.getString( "comentarios" );
				rs.close();
			}
			// else No hay ninguno en la tabla
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static FicheroMultimedia cargarDeTabla( Statement st, String nombreFichero ) {
		try {
			String sentSQL = "select * from fichero_multimedia " +
					"where (fichero = '" + nombreFichero + "')";
			System.out.println( sentSQL );  // (Quitar) para ver lo que se hace
			ResultSet rs = st.executeQuery( sentSQL );
			if (rs.next()) {  // Normalmente se recorre con un while, pero aquí solo hay que ver si ya existe
				FicheroMultimedia fm = new FicheroMultimedia( new File(nombreFichero) );
				fm.erroneo = rs.getBoolean( "error" );
				fm.titulo = rs.getString( "titulo" );
				fm.cantante = rs.getString( "cantante" );
				fm.comentarios = rs.getString( "comentarios" );
				rs.close();
				return fm;
			}
			// else No hay ninguno en la tabla
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;  // Error
		}
	}
	
	public static ArrayList<FicheroMultimedia> cargarVariosDeTabla( Statement st, String exprWhere ) {
		try {
			ArrayList<FicheroMultimedia> lista = new ArrayList<>();
			String sentSQL = "select * from fichero_multimedia" +
					((exprWhere==null||exprWhere.equals(""))?"":(" where " + exprWhere));
			System.out.println( sentSQL );  // (Quitar) para ver lo que se hace
			ResultSet rs = st.executeQuery( sentSQL );
			while (rs.next()) { 
				FicheroMultimedia fm = new FicheroMultimedia( new File(rs.getString( "fichero_multimedia" )) );
				fm.erroneo = rs.getBoolean( "error" );
				fm.titulo = rs.getString( "titulo" );
				fm.cantante = rs.getString( "cantante" );
				fm.comentarios = rs.getString( "comentarios" );
				rs.close();
				lista.add( fm );
			}
			return lista;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;  // Error
		}
	}
	
	
}
