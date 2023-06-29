package sanchez.jose.editor;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.text.DefaultEditorKit;
import javax.swing.undo.UndoManager;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;


public class Principal {
	public static void main(String[] args) {
		VentanaBase ventana = new VentanaBase();
		ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // al cerrar, el programa se cierra
		ventana.setVisible(true);

	}
}

class VentanaBase extends JFrame {
	/* Ese constructor de VentanaBase ya crea las dimensiones, flecha el nombre y crea/agrega el panel  */
	public VentanaBase() { 
		setBounds(600, 600, 600, 600);
		setTitle("Nibelen Editor");
		add(new Painel());
	}
}

class Painel extends JPanel {
	
	/* Los atributos de la clase Panel son los componentes que se agregarán a la misma */
	private JPanel ventana; // ventana que alberga el área de texto base
	private JTextPane areaDeTexto; // área de texto base
	private JTabbedPane abaPanel; // pestaña del editor creado en el botón "nuevo"
	private JMenuBar menuExterno;
	private JMenu archivo, editar, seleccion, ver, aparencia;
	private JMenuItem elementoDeLasOpcionesDelMenu; 
	private ArrayList<JTextPane> listaDeAreadeTexto;
	private ArrayList<UndoManager> listadeManager;
	private ArrayList<File> listaDeFiles;
	private ArrayList<JScrollPane> listaDeScroll;

	// atributos
	private int contadorPanel = 0; // contará cuántos paneles/pestañas se han creado
	private boolean existePanel = false; // informa si ya existe algún panel creado
	private String tipoFondo = "f";
	private boolean numeracion = false;
	private JToolBar herramienta;
	private URL url;

	public Painel() {
		setLayout(new BorderLayout());
		
		this.abaPanel = new JTabbedPane();
		// ---------------- Menu ---------------- //
		JPanel panelMenu = new JPanel();
		this.menuExterno = new JMenuBar();
		this.archivo = new JMenu("Archivo");
		this.editar = new JMenu("Editar");
		this.seleccion = new JMenu("Selección");
		this.ver = new JMenu("Ver");
		this.aparencia = new JMenu("Aparencia");
		this.elementoDeLasOpcionesDelMenu = new JMenuItem();

		this.menuExterno.add(this.archivo);
		this.menuExterno.add(this.editar);
		this.menuExterno.add(this.seleccion);
		this.menuExterno.add(this.ver);
		
		panelMenu.setLayout((new BorderLayout()));
		add(panelMenu, BorderLayout.NORTH);
		//panelMenu.setBackground(Color.GREEN);
		
		// --------------------------- elementos del menu Archivo
		criaItem("Nuevo Archivo", "archivo", "nuevo");
		criaItem("Abrir archivo", "archivo", "abrir");
		criaItem("Guardar", "archivo", "guardar");
		criaItem("Guardar como", "archivo", "guardarComo");
		criaItem("Salir", "archivo", "salir");
		archivo.addSeparator();
		criaItem("Sobre el Nibelen", "archivo", "equipo");
		// --------------------------- elementos del menu Editar
		criaItem("Deshacer", "editar", "deshacer");
		criaItem("Rehacer", "editar", "rehacer");
		editar.addSeparator();
		criaItem("Cortar", "editar", "cortar");
		criaItem("Copiar", "editar", "copiar");
		criaItem("Pegar", "editar", "pegar");
		// --------------------------- elementos del menu Seleccion
		criaItem("Seleccionar todo", "selección", "selección");
		// --------------------------- elementos do menu Ver
		criaItem("Numeración", "ver", "numeracion");
		ver.add(aparencia);
		criaItem("Normal", "aparencia", "normal");
		criaItem("Oscuro", "aparencia", "dark");
		criaItem("Azul", "aparencia", "blue");
		criaItem("Bosque", "aparencia", "forest");

		panelMenu.add(this.menuExterno);
		/* Después de crear los objetos/componentes, agregar al Panel */
		// ---------------- ÁREA DE TEXTO ---------------- //
		abaPanel = new JTabbedPane();
		listaDeFiles = new ArrayList<File>();
		listaDeAreadeTexto = new ArrayList<JTextPane>();
		listaDeScroll = new ArrayList<JScrollPane>();
		listadeManager = new ArrayList<UndoManager>();
		// ---------------- BARRA DE HERRAMIENTAS ---------------- //
		herramienta = new JToolBar(JToolBar.VERTICAL);
		//herramienta = new JToolBar(JToolBar.HORIZONTAL);
		url = Principal.class.getResource("../img/marca-x.png");
		Utilidades.addButton(url, herramienta, "Cerrar pestana actual").addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				int seleccion = abaPanel.getSelectedIndex();
				if(seleccion != -1) { // si hay pestañas abiertas, se cierra aquí
					listaDeScroll.get(abaPanel.getSelectedIndex()).setRowHeader(null);
					abaPanel.remove(seleccion);
					listaDeAreadeTexto.remove(seleccion);
					listaDeScroll.remove(seleccion);
					listadeManager.remove(seleccion);
					listaDeFiles.remove(seleccion);
					
					contadorPanel--;
					if(abaPanel.getSelectedIndex() == -1) {
						existePanel = false; // si devuelve -1 significa que no ha panel creado
					}
				}
				
			}
			
		});
		url = Principal.class.getResource("../img/tree-mas1.png");
		Utilidades.addButton(url, herramienta, "Nuevo Archivo").addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				creaPanel();
				
			}
		});
		

		add(this.abaPanel, BorderLayout.CENTER);
		add(herramienta, BorderLayout.WEST);
	}

	public void criaItem(String rotulo, String menu, String accion) {
		JMenuItem elementoDeLasOpcionesDelMenu = new JMenuItem(rotulo);
		if (menu.equals("archivo")) {
			this.archivo.add(elementoDeLasOpcionesDelMenu);

			if (accion.equals("nuevo")) {
				elementoDeLasOpcionesDelMenu.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						creaPanel(); // crea pestaña
					}
				});
			} else if (accion.equals("abrir")) {
				elementoDeLasOpcionesDelMenu.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						creaPanel();
						JFileChooser selectorArchivo = new JFileChooser();
						selectorArchivo.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
						int resultado = selectorArchivo
								.showOpenDialog(listaDeAreadeTexto.get(abaPanel.getSelectedIndex()));
						System.out.println(resultado); // cancelar = 1; abrir archivo = 0

						if (resultado == JFileChooser.APPROVE_OPTION) {
							try {
								boolean existePath = false;
								for (int i = 0; i < abaPanel.getTabCount(); i++) {
									File file = selectorArchivo.getSelectedFile();
									if (listaDeFiles.get(i).getPath().equals(file.getPath()))
										existePath = true;
								}
								if (!existePath) {
									File archivo = selectorArchivo.getSelectedFile();
									listaDeFiles.set(abaPanel.getSelectedIndex(), archivo);

									FileReader entrada = new FileReader(
											listaDeFiles.get(abaPanel.getSelectedIndex()).getPath());

									BufferedReader miBuffer = new BufferedReader(entrada); //buffer armazena em mem�ria para n�o ter que ir sempre ao artigo e ler
									String linea = "";
									String titulo = listaDeFiles.get(abaPanel.getSelectedIndex()).getName();
									abaPanel.setTitleAt(abaPanel.getSelectedIndex(), titulo); //o titulo se adiciona a aba do painel que se criou 
									
									while(linea != null) {
										try {
											linea = miBuffer.readLine();
										} catch (IOException e1) {
											e1.printStackTrace();
										} // le línea la línea del archivo
										if(linea != null) Utilidades.append(linea + "\n", listaDeAreadeTexto.get(abaPanel.getSelectedIndex()));
									}
									Utilidades.aFondo(contadorPanel, tipoFondo, listaDeAreadeTexto);
								} else { // si esa ruta de los archivos ya está abierta
								// vamos a escanear todas las pestañas abiertas y ver cuál tiene la ruta del archivo y seleccionarlo
									for(int i=0; i<abaPanel.getTabCount(); i++) {
										File f = selectorArchivo.getSelectedFile();
										if(listaDeFiles.get(i).getPath().equals(f.getPath())) {
											// selecciona el panel que tiene el archivo abierto
											abaPanel.setSelectedIndex(i); // le pasamos por parametro la posicion del panel que tiene el path
											
											listaDeAreadeTexto.remove(abaPanel.getTabCount() -1);
											listaDeScroll.remove(abaPanel.getTabCount() -1);
											listaDeFiles.remove(abaPanel.getTabCount() -1);
											abaPanel.remove(abaPanel.getTabCount()-1);
											contadorPanel--;
											break;
										}
									}
								}
								
							} catch (FileNotFoundException e1) {
								e1.printStackTrace();
							}

						}else {
							int seleccion = abaPanel.getSelectedIndex();
							if(seleccion != -1) {
								listaDeAreadeTexto.remove(abaPanel.getTabCount() -1);
								listaDeScroll.remove(abaPanel.getTabCount() -1);
								listaDeFiles.remove(abaPanel.getTabCount() -1);
								abaPanel.remove(abaPanel.getTabCount()-1);
								contadorPanel--;
							}
						}
						
					}
				});
			}
				
			if (accion.equals("guardar")) { // guardar archivo si él ya existe
				elementoDeLasOpcionesDelMenu.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if(listaDeFiles.get(abaPanel.getSelectedIndex()).getPath().equals("")) {
							JFileChooser guardarArchivo = new JFileChooser();
							int opc = guardarArchivo.showSaveDialog(null);
							
							if(opc == JFileChooser.APPROVE_OPTION) {
								File archivo = guardarArchivo.getSelectedFile();
								listaDeFiles.set(abaPanel.getSelectedIndex(), archivo);
								abaPanel.setTitleAt(abaPanel.getSelectedIndex(), archivo.getName());
								
								try {
									FileWriter fw = new FileWriter(listaDeFiles.get(abaPanel.getSelectedIndex()).getPath());
									String texto = listaDeAreadeTexto.get(abaPanel.getSelectedIndex()).getText();
									
									for(int i =0; i<texto.length(); i++) {
										fw.write(texto.charAt(i));
									}
									
									fw.close();
								} catch (IOException e1) {
									e1.printStackTrace();
								}
							}
						}
						else {
							try {
								FileWriter fw = new FileWriter(listaDeFiles.get(abaPanel.getSelectedIndex()).getPath());
								String texto = listaDeAreadeTexto.get(abaPanel.getSelectedIndex()).getText();
								
								for(int i =0; i<texto.length(); i++) {
									fw.write(texto.charAt(i));
								}
								
								fw.close();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}
					}
				});
			}
			if (accion.equals("guardarComo")) {
				elementoDeLasOpcionesDelMenu.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						JFileChooser guardarArchivo = new JFileChooser();
						int opc = guardarArchivo.showSaveDialog(null);
						
						if(opc == JFileChooser.APPROVE_OPTION) {
							File archivo = guardarArchivo.getSelectedFile();
							listaDeFiles.set(abaPanel.getSelectedIndex(), archivo);
							abaPanel.setTitleAt(abaPanel.getSelectedIndex(), archivo.getName());
							
							try {
								FileWriter fw = new FileWriter(listaDeFiles.get(abaPanel.getSelectedIndex()).getPath());
								String texto = listaDeAreadeTexto.get(abaPanel.getSelectedIndex()).getText();
								
								for(int i =0; i<texto.length(); i++) {
									fw.write(texto.charAt(i));
								}
								
								fw.close();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}
					}
				});
			} else if(accion.equals("salir")){	

					elementoDeLasOpcionesDelMenu.addActionListener(new ActionListener() {

					private JFileChooser selectorArchivos;

					@Override
					public void actionPerformed(ActionEvent e) {
						//si el archivo no se guardo
						if(listaDeFiles.get(abaPanel.getSelectedIndex()).getPath().equals("")){
							showErrorDialog();
							JFileChooser salirGuardar = new JFileChooser();
							salirGuardar.setDialogTitle("Elegir/crear un archivo para guardar antes de salir...");
							
						if(salirGuardar != null){
							int opc = salirGuardar.showSaveDialog(salirGuardar);
							
							if(opc == JFileChooser.APPROVE_OPTION) {

								File archivo = salirGuardar.getSelectedFile();
								listaDeFiles.set(abaPanel.getSelectedIndex(), archivo);
								abaPanel.setTitleAt(abaPanel.getSelectedIndex(), archivo.getName());

								try {
									FileWriter fw = new FileWriter(listaDeFiles.get(abaPanel.getSelectedIndex()).getPath());
									String texto = listaDeAreadeTexto.get(abaPanel.getSelectedIndex()).getText();
									for(int i=0; i<texto.length()-1; i++) {
										fw.write(texto.charAt(i));
									}
									fw.close();
								} catch(IOException e1){
 									e1.printStackTrace();									
								}
							} else {
								JOptionPane.showMessageDialog(null, "Saliendo del programa...");
								System.exit(0);
							} 
						} 
						} 
				}
				});
			}
		} else if (accion.equals("equipo")) {
				elementoDeLasOpcionesDelMenu.addActionListener(new java.awt.event.ActionListener() {
		            public void actionPerformed(java.awt.event.ActionEvent evt) {
		            	showAboutDialog();
		            }
		        });
		} else if (menu.equals("editar")) {
			this.editar.add(elementoDeLasOpcionesDelMenu);
			if (accion.equals("deshacer")) {
				elementoDeLasOpcionesDelMenu.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if(listadeManager.get(abaPanel.getSelectedIndex()).canUndo()) listadeManager.get(abaPanel.getSelectedIndex()).undo();
					}
					
			});
		}
			else if(accion.equals("rehacer")) {
				elementoDeLasOpcionesDelMenu.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if(listadeManager.get(abaPanel.getSelectedIndex()).canRedo()) listadeManager.get(abaPanel.getSelectedIndex()).redo();
						
					}
					
			});
			}
			else if(accion.equals("cortar")) {
				// Acción de copiar texto seleccionado
				elementoDeLasOpcionesDelMenu.addActionListener(new DefaultEditorKit.CutAction());
				// Comprueba si hay texto seleccionado y envía un mensaje de alerta si no existe
				elementoDeLasOpcionesDelMenu.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						int indexPanel = abaPanel.getSelectedIndex(); // índice de la guía actual
						String textoPanel = listaDeAreadeTexto.get(indexPanel).getSelectedText(); // texto de la guía actual
						if(textoPanel == null) JOptionPane.showMessageDialog (null, "Seleccione letra o texto para cortar!");
					}
				});			
			}
			else if(accion.equals("copiar")) {
				// Acción de copiar texto seleccionado
				elementoDeLasOpcionesDelMenu.addActionListener(new DefaultEditorKit.CopyAction());
				// Comprueba si hay texto seleccionado y envía un mensaje de alerta si no existe
				elementoDeLasOpcionesDelMenu.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						int indexPanel = abaPanel.getSelectedIndex(); // índice de la guía actual
						String textoPanel = listaDeAreadeTexto.get(indexPanel).getSelectedText(); // texto de la guía actual
						if(textoPanel == null) JOptionPane.showMessageDialog (null, "Seleccione letra o texto para copiar!");
					}
				});
			}
			else if(accion.equals("pegar")) {
				// Acción de pegar texto seleccionado
				elementoDeLasOpcionesDelMenu.addActionListener(new DefaultEditorKit.PasteAction());
				// Comprueba si hay texto seleccionado y envía un mensaje de alerta si no existe
				elementoDeLasOpcionesDelMenu.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							String textoSeleccionado = Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor).toString();
							if(textoSeleccionado == null) JOptionPane.showMessageDialog (null, "Seleccione letra o texto para pegar!");
						} catch (Exception exception) {
							exception.printStackTrace();
						}
					}
				});
			}
			
		} else if (menu.equals("selección")) {
			if(accion.equals("selección")) {
				this.seleccion.add(elementoDeLasOpcionesDelMenu);
				elementoDeLasOpcionesDelMenu.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						listaDeAreadeTexto.get(abaPanel.getSelectedIndex()).selectAll();
					}
				});	
			}
		} else if (menu.equals("ver")) {
			this.ver.add(elementoDeLasOpcionesDelMenu);
			if(accion.equals("numeracion")) {
				elementoDeLasOpcionesDelMenu.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						numeracion = !numeracion;
						Utilidades.viewNumeracion(contadorPanel, numeracion, listaDeAreadeTexto, listaDeScroll);
					}
				});	
			}
			
		} else if (menu.equals("aparencia")) {
			this.aparencia.add(elementoDeLasOpcionesDelMenu);
			
			if(accion.equals("normal")) {
				elementoDeLasOpcionesDelMenu.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						tipoFondo = "w";
						if(abaPanel.getTabCount() >0) Utilidades.aFondo(contadorPanel, tipoFondo, listaDeAreadeTexto);		
					}	
				});
			}
			else if(accion.equals("dark")) {
				elementoDeLasOpcionesDelMenu.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						tipoFondo = "d";
						if(abaPanel.getTabCount() >0) Utilidades.aFondo(contadorPanel, tipoFondo, listaDeAreadeTexto);		
					}	
				});
			}
			else if(accion.equals("blue")) {
				elementoDeLasOpcionesDelMenu.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						tipoFondo = "b";
						if(abaPanel.getTabCount() >0) Utilidades.aFondo(contadorPanel, tipoFondo, listaDeAreadeTexto);		
					}	
				});
			}
			else if(accion.equals("forest")) {
				elementoDeLasOpcionesDelMenu.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						tipoFondo = "f";
						if(abaPanel.getTabCount() >0) Utilidades.aFondo(contadorPanel, tipoFondo, listaDeAreadeTexto);		
					}	
				});
			}
		}

	}
	 public void showAboutDialog() {
	        String message = """
	                Autores: Alquimistas - Alura Latam
	                - Adriana Oliveira
	                - Brenda Souza
	                - Eric Monné
	                - Luis Puig
	                - Maria Fernada Ferreira
	                
	                Tecnologías usadas:
	                Java 17 y Swing
	                
	                =]
	                """;
	        String title = "Sobre el equipo Alquimista ...";
	        JOptionPane.showMessageDialog(this, message, title,
	                JOptionPane.PLAIN_MESSAGE);
	 }
	 public void showErrorDialog(){
	        String message = """
	                         ¡No hay fichero seleccionado!
	                         Prueba la opción guardar como.    
	                         """;
	            String title = "ERROR";
	            JOptionPane.showMessageDialog(this, message, title,
	                    JOptionPane.ERROR_MESSAGE);
	}
	public void creaPanel() {
		// ---------------- crea los objetos //----------------
		this.ventana = new JPanel();
		ventana.setLayout(new BorderLayout());
		

		listaDeFiles.add(new File(""));
		listaDeAreadeTexto.add(new JTextPane());
		listaDeScroll.add(new JScrollPane(listaDeAreadeTexto.get(contadorPanel)));
		listadeManager.add(new UndoManager()); // servirá para rastrear los cambios realizados en el área de texto
		
		listaDeAreadeTexto.get(contadorPanel).getDocument().addUndoableEditListener(listadeManager.get(contadorPanel));
		this.areaDeTexto = new JTextPane();
		// ---------------- agregar los objetos //----------------
		this.ventana.add(listaDeScroll.get(contadorPanel), BorderLayout.CENTER);
		this.abaPanel.addTab("title", this.ventana);
		Utilidades.viewNumerationInicio(numeracion, listaDeAreadeTexto.get(contadorPanel), listaDeScroll.get(contadorPanel));

		this.abaPanel.setSelectedIndex(contadorPanel);
		
		contadorPanel++;
		Utilidades.aFondo(contadorPanel, tipoFondo, listaDeAreadeTexto);
		existePanel = true;
		
	
	}
	

}
