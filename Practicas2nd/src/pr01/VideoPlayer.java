
package pr01;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;

public class VideoPlayer extends JFrame {
	private static final long serialVersionUID = 1L;

	// Varible de ventana principal de la clase
	private static VideoPlayer miVentana;

	// Atributo de VLCj
	private EmbeddedMediaPlayerComponent mediaPlayerComponent;
	private JList<String> lCanciones = null; 
	private JProgressBar pbVideo = null; 
	private JCheckBox cbAleatorio = null; 
	private JLabel lMensaje = null; 
	private ListaDeReproduccion listaRepVideos; 

	
	private DefaultListCellRenderer miListRenderer = new DefaultListCellRenderer() {
		private static final long serialVersionUID = 1L;

		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			JLabel miComp = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (listaRepVideos.isErroneo(index))
				miComp.setForeground(java.awt.Color.RED);
			return miComp;
		}
	};

	public VideoPlayer() {
		// Creacion de datos asociados a la ventana (lista de reproduccion)
		listaRepVideos = new ListaDeReproduccion();

		// Creacion de componentes/contenedores de swing
		lCanciones = new JList<String>(listaRepVideos);
		pbVideo = new JProgressBar(0, 10000);
		cbAleatorio = new JCheckBox("Rep. aleatoria");
		lMensaje = new JLabel("");
		JPanel pBotonera = new JPanel();
		JButton bAnyadir = new JButton(new ImageIcon(VideoPlayer.class.getResource("Button Add.png")));
		JButton bAtras = new JButton(new ImageIcon(VideoPlayer.class.getResource("Button Rewind.png")));
		JButton bPausaPlay = new JButton(new ImageIcon(VideoPlayer.class.getResource("Button Play Pause.png")));
		JButton bAdelante = new JButton(new ImageIcon(VideoPlayer.class.getResource("Button Fast Forward.png")));
		JButton bMaximizar = new JButton(new ImageIcon(VideoPlayer.class.getResource("Button Maximize.png")));

		// Componente de VCLj
		mediaPlayerComponent = new EmbeddedMediaPlayerComponent();

		// Configuracion de componentes/contenedores
		setTitle("Video Player - Deusto Ingenier�a");
		setLocationRelativeTo(null); // Centra la ventana en la pantalla
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(800, 600);
		lCanciones.setCellRenderer(miListRenderer);
		lCanciones.setPreferredSize(new Dimension(200, 500));
		pBotonera.setLayout(new FlowLayout(FlowLayout.LEFT));

		// Enlace de componentes y contenedores
		pBotonera.add(bAnyadir);
		pBotonera.add(bAtras);
		pBotonera.add(bPausaPlay);
		pBotonera.add(bAdelante);
		pBotonera.add(bMaximizar);
		pBotonera.add(cbAleatorio);
		pBotonera.add(lMensaje);
		getContentPane().add(mediaPlayerComponent, BorderLayout.CENTER);
		getContentPane().add(pBotonera, BorderLayout.NORTH);
		getContentPane().add(pbVideo, BorderLayout.SOUTH);
		getContentPane().add(new JScrollPane(lCanciones), BorderLayout.WEST);

		// Escuchadores
		bAnyadir.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File fPath = pedirCarpeta();
				if (fPath == null)
					return;
				path = fPath.getAbsolutePath();
				ficheros = JOptionPane.showInputDialog(null, "Nombre de ficheros a elegir (* para cualquier cadena)",
						"Seleccion de ficheros dentro de la carpeta", JOptionPane.QUESTION_MESSAGE);
				listaRepVideos.add(path, ficheros);
				lCanciones.repaint();
			}
		});
		bAtras.addActionListener(new ActionListener() {
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
		bAdelante.addActionListener(new ActionListener() {
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
		bPausaPlay.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (mediaPlayerComponent.mediaPlayer().media().isValid()) {
					if (mediaPlayerComponent.mediaPlayer().status().isPlaying())
						mediaPlayerComponent.mediaPlayer().controls().pause();
					else
						mediaPlayerComponent.mediaPlayer().controls().play();
				} else {
					lanzaVideo();
				}
			}
		});
		bMaximizar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (mediaPlayerComponent.mediaPlayer().fullScreen().isFullScreen())
					mediaPlayerComponent.mediaPlayer().fullScreen().set(false);
				else
					mediaPlayerComponent.mediaPlayer().fullScreen().set(true);
			}
		});
		pbVideo.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (mediaPlayerComponent.mediaPlayer().media().isValid()) {
					// Seek en el video
					double porcentajeSalto = (double) e.getX() / pbVideo.getWidth();
					long milisegsSalto = mediaPlayerComponent.mediaPlayer().status().length();
					milisegsSalto = Math.round(milisegsSalto * porcentajeSalto);
					mediaPlayerComponent.mediaPlayer().controls().setTime(milisegsSalto);
				}
			}
		});
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				mediaPlayerComponent.mediaPlayer().controls().stop();
				mediaPlayerComponent.mediaPlayer().release();
			}
		});
		mediaPlayerComponent.mediaPlayer().events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
			@Override
			public void finished(MediaPlayer mediaPlayer) {
				listaRepVideos.irASiguiente();
				lanzaVideo();
			}

			@Override
			public void error(MediaPlayer mediaPlayer) {
				listaRepVideos.setFicErroneo(listaRepVideos.getFicSeleccionado(), true);
				listaRepVideos.irASiguiente();
				lanzaVideo();
				lCanciones.repaint();
			}

			@Override
			public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
				pbVideo.setValue((int) (10000.0 * mediaPlayerComponent.mediaPlayer().status().time()
						/ mediaPlayerComponent.mediaPlayer().status().length()));
				pbVideo.repaint();
			}
		});
	}

	//
	// Metodos sobre el player de video
	//

	// Para la reproduccion del video en curso
	private void paraVideo() {
		if (mediaPlayerComponent.mediaPlayer() != null)
			mediaPlayerComponent.mediaPlayer().controls().stop();
	}

	private static DateFormat formatoFechaLocal = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());

	private void lanzaVideo() {
		if (mediaPlayerComponent.mediaPlayer() != null && listaRepVideos.getFicSeleccionado() != -1) {
			File ficVideo = listaRepVideos.getFic(listaRepVideos.getFicSeleccionado());
			mediaPlayerComponent.mediaPlayer().media().play(ficVideo.getAbsolutePath());
			Date fechaVideo = new Date(ficVideo.lastModified());
			lMensaje.setText("Fecha fichero: " + formatoFechaLocal.format(fechaVideo));
			lMensaje.repaint();
			lCanciones.setSelectedIndex(listaRepVideos.getFicSeleccionado());
		} else {
			lCanciones.setSelectedIndices(new int[] {});
		}
	}

	// Pide interactivamente una carpeta para coger videos
	// (null si no se selecciona)
	private static File pedirCarpeta() {
		File dirActual = new File(System.getProperty("user.dir"));
		JFileChooser chooser = new JFileChooser(dirActual);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = chooser.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION)
			return chooser.getSelectedFile();
		else
			return null;
	}

	private static String ficheros;
	private static String path;

	
	public static void main(String[] args) {
		args = new String[] { "*Pentatonix*.mp4", "test/res/" };
		if (args.length < 2) {
			File fPath = pedirCarpeta();
			if (fPath == null)
				return;
			path = fPath.getAbsolutePath();
			ficheros = JOptionPane.showInputDialog(null, "Nombre de ficheros a elegir (* para cualquier cadena)",
					"Seleccion de ficheros dentro de la carpeta", JOptionPane.QUESTION_MESSAGE);
		} else {
			ficheros = args[0];
			path = args[1];
		}
		String vlcPath = System.getenv().get("vlc");
		if (vlcPath == null)
			System.setProperty("jna.library.path", "c:\\Archivos de programa\\videolan\\vlc-2.1.5");
		else
			System.setProperty("jna.library.path", vlcPath);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				miVentana = new VideoPlayer();
				miVentana.setVisible(true);
				miVentana.listaRepVideos.add(path, ficheros);
				miVentana.listaRepVideos.irAPrimero();
				miVentana.lanzaVideo();
			}
		});
	}

}