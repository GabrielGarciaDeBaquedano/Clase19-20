package pr0304;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.media.ParseApi;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.fullscreen.FullScreenStrategy;
import uk.co.caprica.vlcj.player.embedded.fullscreen.windows.Win32FullScreenStrategy;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;

//  - Añadido evento de doble click en lista para seleccionar el fichero directamente
//    (Nuevo método irA(int) en clase ListaDeReproduccion)

/** Ventana principal de reproductor de vídeo
 * Utiliza la librería VLCj que debe estar instalada y configurada
 *     (http://www.capricasoftware.co.uk/projects/vlcj/index.html)
 * @author Andoni Eguíluz Morán
 * Facultad de Ingeniería - Universidad de Deusto
 */
public class VideoPlayer extends JFrame {
	private static final long serialVersionUID = 1L;
	
	// Varible de ventana principal de la clase
	static VideoPlayer miVentana;

	// Atributo de VLCj
	EmbeddedMediaPlayerComponent mediaPlayerComponent;
	EmbeddedMediaPlayer mediaPlayer;
	// Atributos manipulables de swing
	private JList<String> lCanciones = null;  // Lista vertical de vídeos del player
	private JProgressBar pbVideo = null;      // Barra de progreso del vídeo en curso
	private JCheckBox cbAleatorio = null;     // Checkbox de reproducción aleatoria
	private JLabel lMensaje = null;           // Label para mensaje de reproducción
	private JLabel lMensaje2 = null;          // Label para mensaje de reproducción 2
	private JTextField tfPropTitulo = null;   // Label para propiedades - título
	private JTextField tfPropCantante = null; // Label para propiedades - cantante
	private JTextField tfPropComentarios=null;// Label para propiedades - comentarios
	private JPanel pBotonera;                 // Panel botonera (superior)
	private JPanel pBotoneraLR;               // Panel botonera (lista de reproducción)
	private JPanel pPropiedades;              // Panel de propiedades
	private JPanel pInferior;                 // Panel inferior
	private JPanel pIzquierda;                // Panel izquierdo
	ArrayList<JButton> botones;               // Lista de botones
	ArrayList<JButton> botonesLR;             // Lista de botones (lista de reproducción)
	JScrollPane spLCanciones;                 // Scrollpane de lista de repr (izquierda)
	// Datos asociados a la ventana
	private ListaDeReproduccion listaRepVideos;  // Modelo para la lista de vídeos
	private String ultimoFicheroLR = null;       // Nombre del fichero de la lista de reproducción actual
	private Properties misProperties;  // Paso 6 - Propiedades
	private static String ultimaCarpeta = null;
	private static String ultimoPatronFicheros = null;
	private int ultimaXVentana = -1;
	private int ultimaYVentana = -1;
	private int ultimoAnchoVentana = -1;
	private int ultimoAltoVentana = -1;

	// Array auxiliar y enumerado para gestión de botones
	static String[] ficsBotones = new String[] { "Button Add", "Button Rewind", "Button Play Pause", "Button Fast Forward", "Button Maximize" };
	static enum BotonDe { ANYADIR, ATRAS, PLAY_PAUSA, AVANCE, MAXIMIZAR };  // Mismo orden que el array
	static String[] ficsBotonesLR = new String[] { "open", "save", "saveas" /*paso5*/, "database" };
	static enum BotonDeLR { LOAD, SAVE, SAVEAS /*paso5*/, BASEDEDATOS };  // Mismo orden que el array

		// Renderer para la lista vertical de vídeos (colorea diferente los elementos erróneos)
		private DefaultListCellRenderer miListRenderer = new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public Component getListCellRendererComponent(
					JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				JLabel miComp = new JLabel( "prueba" );
//				JLabel miComp = (JLabel) 
//					super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
//				if (listaRepVideos.isErroneo( index )) 
//					miComp.setForeground( java.awt.Color.RED );
//				miComp.setText( "* " + miComp.getText());
				return miComp;
			}
		};
	
	public VideoPlayer() {
		// Creación de datos asociados a la ventana (lista de reproducción)
		listaRepVideos = new ListaDeReproduccion();
		
		// Creación de componentes/contenedores de swing
		lCanciones = new JList<String>( listaRepVideos );
		pbVideo = new JProgressBar( 0, 10000 );
		cbAleatorio = new JCheckBox("Rep. aleatoria");
		lMensaje = new JLabel( "" );
		lMensaje2 = new JLabel( "" );
		tfPropTitulo = new JTextField( "", 10 );
		tfPropCantante = new JTextField( "", 10 );
		tfPropComentarios = new JTextField( "", 30 );
		pBotonera = new JPanel();
		pBotoneraLR = new JPanel();
		pPropiedades = new JPanel();
		pInferior = new JPanel();
		pIzquierda = new JPanel();
		// En vez de "a mano":
		// JButton bAnyadir = new JButton( new ImageIcon( VideoPlayer.class.getResource("img/Button Add.png")) );
		// JButton bAtras = new JButton( new ImageIcon( VideoPlayer.class.getResource("img/Button Rewind.png")) );
		// JButton bPausaPlay = new JButton( new ImageIcon( VideoPlayer.class.getResource("img/Button Play Pause.png")) );
		// JButton bAdelante = new JButton( new ImageIcon( VideoPlayer.class.getResource("img/Button Fast Forward.png")) );
		// JButton bMaximizar = new JButton( new ImageIcon( VideoPlayer.class.getResource("img/Button Maximize.png")) );
		// Lo hacemos con un bucle porque mucho de la creación se repite y lo del formato que hagamos luego también	
		// (ver array ficsBotones en lista de atributos)
		botones = new ArrayList<>();
		for (String fic : ficsBotones) {
			JButton boton = new JButton( new ImageIcon( VideoPlayer.class.getResource( "src/img/" + fic + ".png" )) );
			botones.add( boton );
			boton.setName(fic);  // Pone el nombre al botón del fichero (útil para testeo o depuración)
		}
		botonesLR = new ArrayList<>();
		for (String fic : ficsBotonesLR) {
			JButton boton = new JButton( new ImageIcon( VideoPlayer.class.getResource( "src/img/" + fic + ".png" )) );
			botonesLR.add( boton );
			boton.setName(fic);  // Pone el nombre al botón del fichero (útil para testeo o depuración)
		}
		
		// Componente de VCLj
        mediaPlayerComponent = new EmbeddedMediaPlayerComponent(); 
        mediaPlayer = mediaPlayerComponent.mediaPlayer();

		// Configuración de componentes/contenedores
        int indBoton = 0;
        for (JButton boton : botones) {  // Formato de botones para que se vea solo el gráfico
        	boton.setOpaque(false);            // Fondo Transparente (los gráficos son png transparentes)
        	boton.setContentAreaFilled(false); // No rellenar el área
        	boton.setBorderPainted(false);     // No pintar el borde
        	boton.setBorder(null);             // No considerar el borde (el botón se hace sólo del tamaño del gráfico)
        	boton.setRolloverIcon(             // Pone imagen de rollover
        		new ImageIcon( VideoPlayer.class.getResource( "img/" + ficsBotones[indBoton] + "-RO.png" ) ) );
        	boton.setPressedIcon(             // Pone imagen de click
            		new ImageIcon( VideoPlayer.class.getResource( "img/" + ficsBotones[indBoton] + "-CL.png" ) ) );
        	indBoton++;
        }
        indBoton = 0;
        for (JButton boton : botonesLR) {  // Formato de botones para que se vea solo el gráfico
        	boton.setOpaque(false);            // Fondo Transparente (los gráficos son png transparentes)
        	boton.setContentAreaFilled(false); // No rellenar el área
        	boton.setBorderPainted(false);     // No pintar el borde
        	boton.setBorder(null);             // No considerar el borde (el botón se hace sólo del tamaño del gráfico)
        	indBoton++;
        }
        lMensaje2.setForeground( Color.white );
        lMensaje2.setFont( new Font( "Arial", Font.BOLD, 18 ));
		setTitle("Video Player - Deusto Ingeniería");
		setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		setSize( 800, 600 );
		lCanciones.setCellRenderer( miListRenderer );
		spLCanciones = new JScrollPane( lCanciones );
		spLCanciones.setPreferredSize( new Dimension( 200,  5000 ) );  // Coge el ancho de 200 píxels en lugar del del string más largo
			// (Cambiado sobre primera versión: Ojo que si se pone el tamaño de la JList en lugar del ScrollPane luego el scroll no se hace bien)
		pBotonera.setLayout( new FlowLayout( FlowLayout.LEFT ));
		pPropiedades.setLayout( new FlowLayout( FlowLayout.LEFT ));
		pInferior.setLayout( new BorderLayout() );
		pIzquierda.setLayout( new BorderLayout() );
		// Paso 2: propiedades visibles - pPropiedades.setVisible( false );
		// Paso 3: botones visibles - pBotoneraLR.setVisible( false );
		
		// Enlace de componentes y contenedores
		for (JButton boton : botones ) pBotonera.add( boton );
		for (JButton boton : botonesLR ) pBotoneraLR.add( boton );
		pBotonera.add( lMensaje2 );
		pBotonera.add( cbAleatorio );
		pBotonera.add( lMensaje );
		pPropiedades.add( new JLabel("Tit:") );
		pPropiedades.add( tfPropTitulo );
		pPropiedades.add( new JLabel("Cant:") );
		pPropiedades.add( tfPropCantante );
		pPropiedades.add( new JLabel("Coms:") );
		pPropiedades.add( tfPropComentarios );
		pInferior.add( pPropiedades, BorderLayout.NORTH );
		pInferior.add( pbVideo, BorderLayout.SOUTH );
		pIzquierda.add( spLCanciones, BorderLayout.CENTER );
		pIzquierda.add( pBotoneraLR, BorderLayout.SOUTH );

		getContentPane().add( mediaPlayerComponent, BorderLayout.CENTER );
		getContentPane().add( pBotonera, BorderLayout.NORTH );
		getContentPane().add( pInferior, BorderLayout.SOUTH );
		getContentPane().add( pIzquierda, BorderLayout.WEST );
		
		// Escuchadores
		// Añadir ficheros
		botones.get(BotonDe.ANYADIR.ordinal()).addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File fPath = pedirCarpeta();
				if (fPath==null) return;
				path = fPath.getAbsolutePath();
				ultimaCarpeta = path;
				if (ultimoPatronFicheros==null)  // Paso 6
					ficheros = JOptionPane.showInputDialog( null,
							"Nombre de ficheros a elegir (* para cualquier cadena)",
							"Selección de ficheros dentro de la carpeta", JOptionPane.QUESTION_MESSAGE );
				else
					ficheros = JOptionPane.showInputDialog( null,
							"Nombre de ficheros a elegir (* para cualquier cadena)",
							ultimoPatronFicheros );
				ultimoPatronFicheros = ficheros;
				listaRepVideos.add( path, ficheros, true );
				lCanciones.repaint();
			}
		});
		// Canción anterior
		botones.get(BotonDe.ATRAS.ordinal()).addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				paraVideo();
				if (cbAleatorio.isSelected()) {
					listaRepVideos.irARandom();
				} else {
					listaRepVideos.irAAnterior();
				}
				lanzaVideo();
			}
		});
		// Pausa / Play
		botones.get(BotonDe.PLAY_PAUSA.ordinal()).addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (mediaPlayer.status().isPlayable()) {
					if (mediaPlayer.status().isPlaying())
						mediaPlayer.controls().pause();
					else
						mediaPlayer.controls().play();
				} else {
					lanzaVideo();
				}
			}
		});
		// Canción siguiente
		botones.get(BotonDe.AVANCE.ordinal()).addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				paraVideo();
				if (cbAleatorio.isSelected()) {
					listaRepVideos.irARandom();
				} else {
					listaRepVideos.irASiguiente();
				}
				lanzaVideo();
			}
		});
		// Maximizar / desmaximizar
		botones.get(BotonDe.MAXIMIZAR.ordinal()).addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (mediaPlayer.fullScreen().isFullScreen()) {
					mediaPlayer.fullScreen().set(false);
			        // Añadido para dejar más espacio en la pantalla maximizada
					pIzquierda.setVisible( true );
					pBotonera.setBackground( Color.LIGHT_GRAY );
				} else {
					mediaPlayer.fullScreen().set(true);
			        // Añadido para dejar más espacio en la pantalla maximizada
					pIzquierda.setVisible( false );
					pBotonera.setBackground( Color.BLACK );
				}
			}
		});
		// Doble click en lista para saltar a reproducir directamente
		lCanciones.addMouseListener( new MouseAdapter() {  
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
		            int posi = lCanciones.locationToIndex( e.getPoint() );
		            paraVideo();
		            listaRepVideos.irA( posi );
		            lanzaVideo();
		        }
			}
		});
		// Click en barra de progreso para saltar al tiempo del vídeo de ese punto
		pbVideo.addMouseListener( new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (mediaPlayer.status().isPlayable()) {
					// Seek en el vídeo
					float porcentajeSalto = (float)e.getX() / pbVideo.getWidth();
					mediaPlayer.status().position();
			    	visualizaTiempoRep();
					// Otra manera de hacerlo con los milisegundos:
					// long milisegsSalto = mediaPlayer.getLength();
					// milisegsSalto = Math.round( milisegsSalto * porcentajeSalto );
					// mediaPlayer.setTime( milisegsSalto );
				}
			}
		});
		// Cierre del player cuando se cierra la ventana
		addWindowListener( new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				mediaPlayer.controls().stop();
				mediaPlayer.release();
				BaseDeDatos.close();  // Paso 5
				salvaProperties();  // Paso 6
			}
		});
		
		// Eventos del propio player
		mediaPlayer.events().addMediaPlayerEventListener( 
			new MediaPlayerEventAdapter() {
				// El vídeo se acaba
				@Override
				public void finished(MediaPlayer mediaPlayer) {
					listaRepVideos.irASiguiente();
					lanzaVideo();
				}
				// Hay error en el formato o en el fichero del vídeo
				@Override
				public void error(MediaPlayer mediaPlayer) {
					listaRepVideos.setFicErroneo( listaRepVideos.getFicSeleccionado(), true );
					listaRepVideos.irASiguiente();
					lanzaVideo();
					lCanciones.repaint();
				}
				// Evento que ocurre al cambiar el tiempo (cada 3 décimas de segundo aproximadamente
			    @Override
			    public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
			    	visualizaTiempoRep();
			    }
		});
		
		// Paso 2: Evento de edición de las propiedades
		FocusListener fl = new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				FicheroMultimedia ficVideo = listaRepVideos.getFicMM(listaRepVideos.getFicSeleccionado());
				if (ficVideo!=null) {
					if (e.getSource()==tfPropTitulo)
						ficVideo.titulo = tfPropTitulo.getText();
					else if (e.getSource()==tfPropCantante)
						ficVideo.cantante = tfPropCantante.getText();
					else if (e.getSource()==tfPropComentarios)
						ficVideo.comentarios = tfPropComentarios.getText();
				}
			}
		};
		tfPropTitulo.addFocusListener( fl );
		tfPropCantante.addFocusListener( fl );
		tfPropComentarios.addFocusListener( fl );

		// Paso 3 - eventos de botón de carga/guardado de lista de reproducción
		botonesLR.get(BotonDeLR.LOAD.ordinal()).addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File fPath = pedirFicheroVPD(); if (fPath==null) return;
				path = fPath.getAbsolutePath();
				// Intenta cargar el fichero
				try {
					ObjectInputStream ois = new ObjectInputStream( new FileInputStream(path) );
					ListaDeReproduccion lr = (ListaDeReproduccion) ois.readObject();
					ois.close();
					lr.initDataListeners();
					ultimoFicheroLR = path;
					listaRepVideos = lr;
					lCanciones.setModel( listaRepVideos );
					lanzaVideo();
				} catch (Exception e2) {
					e2.printStackTrace();
					JOptionPane.showMessageDialog( VideoPlayer.this, "El fichero " + 
							path + " no ha podido cargarse.", "Fichero incorrecto", JOptionPane.ERROR_MESSAGE );
				}
			}
		} );
		botonesLR.get(BotonDeLR.SAVEAS.ordinal()).addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File fPath = pedirFicheroVPD(); if (fPath==null) return;
				path = fPath.getAbsolutePath();
				if (!path.toUpperCase().endsWith("VPD")) {
					path = path + ".vpd";
					fPath = new File(path);
				}
				if (fPath.exists()) {  // Pide confirmación de sobreescritura
					int conf = JOptionPane.showConfirmDialog( VideoPlayer.this,
							"¡Atención! El fichero indicado ya existe. ¿Quieres sobreescribirlo?", 
							"Confirmación de fichero ya existente", JOptionPane.YES_NO_OPTION );
					if (conf!=0) return;  // si no hay confirmación no seguimos
				}
				// Intenta salvar la lista al fichero
				try {
					ObjectOutputStream oos = new ObjectOutputStream( new FileOutputStream(path) );
					oos.writeObject( listaRepVideos );
					oos.close();
					ultimoFicheroLR = path;
				} catch (Exception e2) {
					e2.printStackTrace();
					JOptionPane.showMessageDialog( VideoPlayer.this, "El fichero " + 
							path + " no ha podido salvarse.", "Fichero incorrecto", JOptionPane.ERROR_MESSAGE );
				}
			}
		} );
		botonesLR.get(BotonDeLR.SAVE.ordinal()).addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (ultimoFicheroLR==null) {  // Si no hay fichero cargado, se hace save as
					botonesLR.get(BotonDeLR.SAVEAS.ordinal()).doClick();
					return;
				}
				// Intenta salvar la lista al fichero
				try {
					ObjectOutputStream oos = new ObjectOutputStream( new FileOutputStream(ultimoFicheroLR) );
					oos.writeObject( listaRepVideos );
					oos.close();
				} catch (Exception e2) {
					e2.printStackTrace();
					JOptionPane.showMessageDialog( VideoPlayer.this, "El fichero " + 
							ultimoFicheroLR + " no ha podido salvarse.", "Fichero incorrecto", JOptionPane.ERROR_MESSAGE );
				}
			}
		} );
		
		// Paso 5 - BD
		botonesLR.get(BotonDeLR.BASEDEDATOS.ordinal()).addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i=0; i<listaRepVideos.size(); i++) {
					FicheroMultimedia fm = listaRepVideos.getFicMM(i);
					fm.anyadirFilaATabla( BaseDeDatos.getStatement() );
				}
			}
		});
		
		// Paso 7 - Ordenación
		lCanciones.addKeyListener( new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode()==KeyEvent.VK_CONTROL) controlPulsado = false;
				else if (e.getKeyCode()==KeyEvent.VK_DELETE) {  // Paso adicional 3
					int seleccionada = lCanciones.getSelectedIndex();
					if (seleccionada!=-1) {
						if (seleccionada == listaRepVideos.ficheroEnCurso) {
							// Se quiere borrar el que se está reproduciendo
							paraVideo();
							listaRepVideos.irASiguiente();
							lanzaVideo();
						}
						listaRepVideos.removeFic( seleccionada );
						if (seleccionada < listaRepVideos.size())
							lCanciones.setSelectedIndex( seleccionada );  // Selecciona la misma posición
						else if (seleccionada > 0)
							lCanciones.setSelectedIndex( seleccionada-1 );  // Selecciona la posición anterior
					}
				}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode()==KeyEvent.VK_CONTROL) controlPulsado = true;
				else if (controlPulsado) {
					Comparator<FicheroMultimedia> cfm = null;
					if (e.getKeyCode() == KeyEvent.VK_N) {  // Ordenar por nombre
						cfm = new Comparator<FicheroMultimedia>() {
							@Override
							public int compare(FicheroMultimedia o1,
									FicheroMultimedia o2) {
								return o1.file.getName().compareTo( o2.file.getName() );
							}
						};
					} else if (e.getKeyCode() == KeyEvent.VK_F) {  // Ordenar por fecha
						cfm = new Comparator<FicheroMultimedia>() {
							@Override
							public int compare(FicheroMultimedia o1,
									FicheroMultimedia o2) {
								return (int)(o1.file.lastModified()-o2.file.lastModified());
							}
						};
					} else if (e.getKeyCode() == KeyEvent.VK_T) {  // Ordenar por tamaño
						cfm = new Comparator<FicheroMultimedia>() {
							@Override
							public int compare(FicheroMultimedia o1,
									FicheroMultimedia o2) {
								return (int)(o1.file.length()-o2.file.length());
							}
						};
					}
					listaRepVideos.mergeSort( cfm );
					if (listaRepVideos.getFicSeleccionado()!=-1)
						lCanciones.setSelectedIndex( listaRepVideos.getFicSeleccionado() );
				}
			}
		});
	}
		private boolean controlPulsado = false;  // Paso 7
		
		private void visualizaTiempoRep() {
			pbVideo.setValue( (int) (10000.0 * 
					mediaPlayer.status().time() /
					mediaPlayer.status().length()) );
			pbVideo.repaint();
			lMensaje2.setText( formatoHora.format( new Date(mediaPlayer.status().time()-3600000L) ) );
		}

	//
	// Métodos sobre el player de vídeo
	//
	
	// Para la reproducción del vídeo en curso
	private void paraVideo() {
		if (mediaPlayer!=null)
			mediaPlayer.controls().stop();
	}
	
		private static DateFormat formatoFechaLocal = 
			DateFormat.getDateInstance( DateFormat.SHORT, Locale.getDefault() );
		private static DateFormat formatoHora = new SimpleDateFormat( "HH:mm:ss" );
	private void lanzaVideo() {
		if (mediaPlayer!=null &&
			listaRepVideos.getFicSeleccionado()!=-1) {
			FicheroMultimedia ficVideo = listaRepVideos.getFicMM(listaRepVideos.getFicSeleccionado());
			mediaPlayer.media().play( 
				ficVideo.file.getAbsolutePath() );
			Date fechaVideo = new Date( ficVideo.file.lastModified() );
			lMensaje.setText( "Fecha fichero: " + formatoFechaLocal.format( fechaVideo ) );
			lMensaje.repaint();
			lCanciones.setSelectedIndex( listaRepVideos.getFicSeleccionado() );
			lCanciones.ensureIndexIsVisible( listaRepVideos.getFicSeleccionado() );  // Asegura que se vea en pantalla
			// Paso 2: Propiedades
			tfPropTitulo.setText( ficVideo.titulo );
			tfPropCantante.setText( ficVideo.cantante );
			tfPropComentarios.setText( ficVideo.comentarios);
		} else {
			lCanciones.setSelectedIndices( new int[] {} );
		}
	}
	
	// Pide interactivamente una carpeta para coger vídeos
	// (null si no se selecciona)
	private static File pedirCarpeta() {
		String carp = ultimaCarpeta;  // Paso 6
		if (ultimaCarpeta==null) carp = System.getProperty("user.dir");
		File dirActual = new File( carp );
		JFileChooser chooser = new JFileChooser( dirActual );
		chooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
		int returnVal = chooser.showOpenDialog( null );
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile();
		} else 
			return null;
	}
	// Pide interactivamente un fichero existente de extensión vpd
	// (null si no se selecciona)
	private File pedirFicheroVPD() {
		File dirActual = new File( System.getProperty("user.dir") );
		JFileChooser chooser = new JFileChooser( dirActual );
		chooser.setFileSelectionMode( JFileChooser.FILES_ONLY );
		chooser.setFileFilter( new FileNameExtensionFilter( 
				"Ficheros lista de reproducción", "vpd" ) );
		int returnVal = chooser.showOpenDialog( null );
		if (returnVal == JFileChooser.APPROVE_OPTION)
			return chooser.getSelectedFile();
		else 
			return null;
	}

	// Paso 6: carga y guardado de las propiedades
	private void cargaProperties() {
		misProperties = new Properties();
		try {
			FileInputStream fis = new FileInputStream( new File ( "videoplayer.ini" ));
			misProperties.loadFromXML( fis );
			ultimoFicheroLR = misProperties.getProperty( "ultimoFicheroLR" );
			ultimaCarpeta = misProperties.getProperty( "ultimaCarpeta" );
			ultimoPatronFicheros = misProperties.getProperty( "ultimoPatronFicheros" );
			ultimaXVentana = Integer.parseInt( misProperties.getProperty( "ultimaXVentana" ) );
			ultimaYVentana = Integer.parseInt( misProperties.getProperty( "ultimaYVentana" ) );
			ultimoAnchoVentana = Integer.parseInt( misProperties.getProperty( "ultimoAnchoVentana" ) );
			ultimoAltoVentana = Integer.parseInt( misProperties.getProperty( "ultimoAltoVentana" ) );
			if (ultimoAnchoVentana>0 && ultimoAltoVentana>0) {
				setSize( ultimoAnchoVentana, ultimoAltoVentana );
				setLocation( ultimaXVentana, ultimaYVentana );
			}
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void salvaProperties() {
		PrintStream ps;
		try {
			ps = new PrintStream( new File( "videoplayer.ini" ) );
			if (ultimoFicheroLR!=null) misProperties.setProperty( "ultimoFicheroLR", ultimoFicheroLR );
			if (ultimaCarpeta!=null) misProperties.setProperty( "ultimaCarpeta", ultimaCarpeta );
			if (ultimoPatronFicheros!=null) misProperties.setProperty( "ultimoPatronFicheros", ultimoPatronFicheros );
			misProperties.setProperty( "ultimaXVentana", ""+ getX() );
			misProperties.setProperty( "ultimaYVentana", ""+ getY() );
			misProperties.setProperty( "ultimoAnchoVentana", ""+getWidth() );
			misProperties.setProperty( "ultimoAltoVentana", ""+getHeight() );
			misProperties.storeToXML( ps, "Video Player Deusto" );
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
		private static String ficheros;
		private static String path;
	/** Ejecuta una ventana de VideoPlayer.
	 * El path de VLC debe estar en la variable de entorno "vlc".
	 * Comprobar que la versión de 32/64 bits de Java y de VLC es compatible.
	 * @param args	Un array de dos strings. El primero es el nombre (con comodines) de los ficheros,
	 * 				el segundo el path donde encontrarlos.  Si no se suministran, se piden de forma interactiva. 
	 */
	public static void main(String[] args) {
		// Para probar con otro directorio descomentar estas dos líneas y poner los valores deseados:
		// (Si se pasan argumentos al main, los usará)
		if (args==null || args.length==0) 
			args = new String[] { "*Pentatonix*.mp4", "test/res/" };
		if (args.length < 2) {
			// No hay argumentos: selección manual
			File fPath = pedirCarpeta();
			if (fPath==null) return;
			path = fPath.getAbsolutePath();
			ficheros = JOptionPane.showInputDialog( null,
					"Nombre de ficheros a elegir (* para cualquier cadena)",
					"Selección de ficheros dentro de la carpeta", JOptionPane.QUESTION_MESSAGE );
			ultimoPatronFicheros = ficheros;
		} else {
			ficheros = args[0];
			path = args[1];
		}
		// Buscar vlc como variable de entorno
		String vlcPath = System.getenv().get( "vlc" );
		if (vlcPath==null)
			// Poner VLC a mano
			System.setProperty("jna.library.path", "c:\\Archivos de programa\\videolan\\vlc-2.1.5");
		else
			// Poner VLC desde la variable de entorno
			System.setProperty( "jna.library.path", vlcPath );
		// Paso 5: Inicializar BD
		BaseDeDatos.initBD( "vpd.bd" );
		BaseDeDatos.crearTablaBD();
		try {
			SwingUtilities.invokeAndWait( new Runnable() {
				@Override
				public void run() {
					miVentana = new VideoPlayer();
					miVentana.cargaProperties();  // Paso 6
					miVentana.setVisible( true );
					miVentana.listaRepVideos.add( path, ficheros, true );
					miVentana.listaRepVideos.irAPrimero();
					miVentana.lanzaVideo();
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
