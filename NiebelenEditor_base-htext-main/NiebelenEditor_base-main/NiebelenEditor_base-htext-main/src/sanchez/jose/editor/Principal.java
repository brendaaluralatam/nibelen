package sanchez.jose.editor;

import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.DefaultEditorKit.CopyAction;
import javax.swing.text.DefaultEditorKit.CutAction;
import javax.swing.undo.UndoManager;

import java.awt.Color;
import java.awt.Image;
import java.awt.BorderLayout;

import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;



public class Principal {
	public static void main(String [] args) {
		Marco marco = new Marco();
		marco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		marco.setVisible(true);
	}
}


class Marco extends JFrame{
	public Marco() {
		setBounds(300,300,600,600);
		setTitle("Nibelen");
		add(new Panel(this));
	}
}


class Panel extends JPanel{
	public Panel(JFrame marco) {
		setLayout(new BorderLayout());
		
		//----------- Menu ------------------------------------
		JPanel panelMenu = new JPanel();
		items = new JMenuItem[8];
		
		panelMenu.setLayout(new BorderLayout());
		menu = new JMenuBar();
		archivo = new JMenu("Archivo");
		editar = new JMenu("Editar");
		seleccion = new JMenu("");
		ver = new JMenu("");
		apariencia = new JMenu("");
		
		menu.add(archivo);
		menu.add(editar);
		
		
		//---------------- ELEMENTOS DEL MENU ARCHIVO---------------
		creaItem("Nuevo Archivo", "archivo", "nuevo");
		creaItem("Abrir Archivo", "archivo", "abrir");
		creaItem("Guardar", "archivo", "guardar");
		creaItem("Guardar Como", "archivo", "guardarComo");
		creaItem("Salir", "archivo", "salir");

		//--------------------------------------------------------
		
		//------------------ ELEMENTOS DEL MENU EDITAR ----------------
		creaItem("Deshacer", "editar", "deshacer");
		creaItem("Rehacer", "editar", "rehacer");
		editar.addSeparator();
		creaItem("", "editar", "cortar");
		creaItem("", "editar", "copiar");
		creaItem("", "editar", "pegar");
		//-------------------------------------------------------------
		
		//----------------- ELEMENTOS DEL MENU SELECCION ---------------
		creaItem("", "seleccion", "seleccion");
		//--------------------------------------------------------------
		
		//------------------- ELEMENTOS DEL MENU VER -------------------
		
		//--------------------------------------------------------------
		
		
		panelMenu.add(menu, BorderLayout.NORTH);
		//-----------------------------------------------------
		
		
		//----------- Area de Texto --------------------------
		tPane = new JTabbedPane();
		
		listFile = new ArrayList<File>();
		listAreaTexto = new ArrayList<JTextPane>();
		listScroll = new ArrayList<JScrollPane>();
		listManager = new ArrayList<UndoManager>();
		
		Utilidades.desactivaItem(items);
		
		//----------------------------------------------------
		
		//--------------- Barra de Herramientas ------------------
		
		herramientas = new JToolBar(JToolBar.VERTICAL);
		url = Principal.class.getResource("/sanchez/jose/img/marca-x.png");
		Utilidades.addButton(url, herramientas, "Cerrar Pestana Actual").addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				int seleccion = tPane.getSelectedIndex();
				if(seleccion != -1) {
					//Si existen pestañas abiertas eliminamos la pestaña que tengamos seleccionada
					listScroll.get(tPane.getSelectedIndex()).setRowHeader(null);
					tPane.remove(seleccion);
					listAreaTexto.remove(seleccion);
					listScroll.remove(seleccion);
					listManager.remove(seleccion);
					listFile.remove(seleccion);
					
					contadorPanel--;
					
					if(tPane.getSelectedIndex() == -1) {
						existePanel = false; //Si tPane retorna -1 quiere decir que no exiten paneles creados
						Utilidades.desactivaItem(items);
					}
				}
			}
			
		});
		
		url = Principal.class.getResource("/sanchez/jose/img/mas (1).png");
		Utilidades.addButton(url, herramientas, "Nuevo Archivo").addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				creaPanel();
				if(existePanel) Utilidades.activaItems(items);
			}
			
		});
		
		//--------------------------------------------------------
		
		// ------------------------- Panel Extra --------------------------------
		panelExtra = new JPanel();
		panelExtra.setLayout(new BorderLayout());
		
		JPanel panelIzquierdo = new JPanel();
		labelAlfiler = new JLabel();
		url = Principal.class.getResource("/sanchez/jose/img/alfiler.png");
		//labelAlfiler.setIcon(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(20,20, Image.SCALE_SMOOTH)));
		labelAlfiler.addMouseListener(new MouseAdapter() {
			//al pasar el cursor por encima del alfiler cambia a esta imagen
			public void mouseEntered(MouseEvent e) {
				url = Principal.class.getResource("/sanchez/jose/img/alfilerseleccion.png");
				labelAlfiler.setIcon(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(20,20, Image.SCALE_SMOOTH)));
			}
			
			//si estadoAncla es verdadero cambia la imagen del jlabel si es false tambien la cambia por otra imagen
			public void mouseExited(MouseEvent e) {
				if(estadoAlfiler) {
					url = Principal.class.getResource("/sanchez/jose/img/alfilerseleccion.png");
					labelAlfiler.setIcon(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(20,20, Image.SCALE_SMOOTH)));
				}else {
					url = Principal.class.getResource("/sanchez/jose/img/alfiler.png");
					labelAlfiler.setIcon(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(20,20, Image.SCALE_SMOOTH)));
				}
			}
			
			//al dar click sobre el jLabel invertimos el valor de estadoAncla y se lo pasamos a setAlwaysOnTop que nos permite mantener la ventana por encima de todo
			public void mousePressed(MouseEvent e) {
				estadoAlfiler = !estadoAlfiler;
				marco.setAlwaysOnTop(estadoAlfiler);
			}
		});
		
		panelIzquierdo.add(labelAlfiler);
		
		JPanel panelCentro = new JPanel();
		slider = new JSlider(8,38,14);
		slider.setMajorTickSpacing(6); //La separacion entre las barritas grandes sera de 12 en 12
		slider.setMinorTickSpacing(2); //Indica que la separacion entre las barras pequeñas es de 2
		//slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				Utilidades.tamTexto(slider.getValue(), contadorPanel, listAreaTexto);
			}
		});
		
		panelCentro.add(slider);
		
		panelExtra.add(panelIzquierdo, BorderLayout.WEST);
		panelExtra.add(panelCentro,BorderLayout.CENTER);
		//------------------------------------------------------------------------
		
		//------------------ Menu Emergente --------------------------------------
		menuEmergente = new JPopupMenu();
		
		//------------------------------------------------------------------------
				
		add(panelMenu, BorderLayout.NORTH);
		add(tPane, BorderLayout.CENTER);
		add(herramientas, BorderLayout.WEST);
		add(panelExtra, BorderLayout.SOUTH);
	}
	
	public void creaItem(String rotulo, String menu, String accion) {
		elementoItem = new JMenuItem(rotulo);
		
		if(menu.equals("archivo")) {
			archivo.add(elementoItem);
			if(accion.equals("nuevo")) {
				elementoItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						creaPanel();
						if(existePanel) Utilidades.activaItems(items);
					}
				});
			}
			else if(accion.equals("abrir")) {
				elementoItem.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						creaPanel();
						
						JFileChooser selectorArchivos = new JFileChooser();
						selectorArchivos.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
						int resultado = selectorArchivos.showOpenDialog(listAreaTexto.get(tPane.getSelectedIndex()));
						
						if (resultado == JFileChooser.APPROVE_OPTION) {
							if(existePanel) Utilidades.activaItems(items);
							try {
								boolean existePath = false;
								for (int i = 0; i < tPane.getTabCount(); i++) {
									File f = selectorArchivos.getSelectedFile();
									if (listFile.get(i).getPath().equals(f.getPath()))
										existePath = true;
								}

								if (!existePath) {
									File archivo = selectorArchivos.getSelectedFile();
									listFile.set(tPane.getSelectedIndex(), archivo);

									FileReader entrada = new FileReader(
											listFile.get(tPane.getSelectedIndex()).getPath());
									
									BufferedReader miBuffer = new BufferedReader(entrada);
									String linea = "";
									
									String titulo = listFile.get(tPane.getSelectedIndex()).getName();
									//El titulo se le agrega a la pestaña del panel que se crea, donde se encuentra
									//nuestra area de texto, lugar donde ira el texto del archivo que el usuario ha seleccionado
									tPane.setTitleAt(tPane.getSelectedIndex(), titulo);
									
									while(linea != null) {
										linea = miBuffer.readLine(); //Lee linea a linea cada linea del archivo y la almacena en el string
										if(linea !=null) Utilidades.append(linea+"\n", listAreaTexto.get(tPane.getSelectedIndex()));
										
									}
									Utilidades.aFondo(contadorPanel, tipoFondo, slider.getValue(), listAreaTexto);
								}else {
									//si la ruta del fichero ya existe y esta abierto
									//vamos a recorrer todos los paneles para ver cual es el que tiene el path del
									// fichero y seleccionar ese fichero y ese panel
									
									for(int i = 0; i<tPane.getTabCount(); i++) {
										File f = selectorArchivos.getSelectedFile();
										if(listFile.get(i).getPath().equals(f.getPath())) {
											//Seleccionamos el panel que ya tiene el archivo abierto
											tPane.setSelectedIndex(i); //le pasamos por parametro la posicion del panel que tiene el path
											
											listAreaTexto.remove(tPane.getTabCount()-1);
											listScroll.remove(tPane.getTabCount()-1);
											listFile.remove(tPane.getTabCount()-1);
											tPane.remove(tPane.getTabCount()-1);
											contadorPanel--;
											break;
										}
									}
								}
								
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} 
						}else {
							//Si se oprime el boton cancelar en la ventana de abrir archivo
							//eliminamos el panel del area de texto que se crea por defecto
							
							int seleccion = tPane.getSelectedIndex();
							if(seleccion != -1) {
								listAreaTexto.remove(tPane.getTabCount()-1);
								listScroll.remove(tPane.getTabCount()-1);
								listFile.remove(tPane.getTabCount()-1);
								tPane.remove(tPane.getTabCount()-1);
								contadorPanel--;
							}
							
						}

					}
							
					
				});
			}
			else if(accion.equals("guardar")) {
				items[0] = elementoItem;
				elementoItem.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						//Guardar Como si el archivo no existe 
						if(listFile.get(tPane.getSelectedIndex()).getPath().equals("")) {
							JFileChooser guardarArchivos = new JFileChooser();
							int opc = guardarArchivos.showSaveDialog(null);
							
							if(opc == JFileChooser.APPROVE_OPTION) {
								File archivo = guardarArchivos.getSelectedFile();
								listFile.set(tPane.getSelectedIndex(), archivo);
								tPane.setTitleAt(tPane.getSelectedIndex(), archivo.getName());
								
								try {
									FileWriter fw = new FileWriter(listFile.get(tPane.getSelectedIndex()).getPath());
									String texto = listAreaTexto.get(tPane.getSelectedIndex()).getText();

									for(int i = 0; i<texto.length(); i++) {
										fw.write(texto.charAt(i));
									}
									
									fw.close();
									
									
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								
							}
							
						}
						else {
							try {
								FileWriter fw = new FileWriter(listFile.get(tPane.getSelectedIndex()).getPath());
								String texto = listAreaTexto.get(tPane.getSelectedIndex()).getText();

								for(int i = 0; i<texto.length(); i++) {
									fw.write(texto.charAt(i));
								}
								
								fw.close();
								
								
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					}
					
				});
			}
			else if(accion.equals("guardarComo")) {
				items[1] = elementoItem;
				elementoItem.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						JFileChooser guardarArchivos = new JFileChooser();
						int opc = guardarArchivos.showSaveDialog(null);
						
						if(opc == JFileChooser.APPROVE_OPTION) {
							File archivo = guardarArchivos.getSelectedFile();
							listFile.set(tPane.getSelectedIndex(), archivo);
							tPane.setTitleAt(tPane.getSelectedIndex(), archivo.getName());
							
							try {
								FileWriter fw = new FileWriter(listFile.get(tPane.getSelectedIndex()).getPath());
								String texto = listAreaTexto.get(tPane.getSelectedIndex()).getText();

								for(int i = 0; i<texto.length(); i++) {
									fw.write(texto.charAt(i));
								}
								
								fw.close();
								
								
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							
						}
						
						
					}
					
				});			
				
				
			}
			
				else if(accion.equals("salir")){	
				
					elementoItem.addActionListener(new ActionListener() {

					private JFileChooser selectorArchivos;

					@Override
					public void actionPerformed(ActionEvent e) {
						//si el archivo no se guardo
						
						if(listFile.get(tPane.getSelectedIndex()).getPath().equals("")){
							showErrorDialog();
							JFileChooser salirGuardar = new JFileChooser();
							int opc = selectorArchivos.showSaveDialog(null);
							
							if(opc == JFileChooser.APPROVE_OPTION) {
								
								File archivo = selectorArchivos.getSelectedFile();
								listFile.set(tPane.getSelectedIndex(), archivo);
								tPane.setTitleAt(tPane.getSelectedIndex(), archivo.getName());
								
								try {
									FileWriter fw = new FileWriter(listFile.get(tPane.getSelectedIndex()).getPath());
									String texto = listAreaTexto.get(tPane.getSelectedIndex()).getText();
									for(int i=0; i<texto.length()-1; i++) {
										fw.write(texto.charAt(i));
									}
									fw.close();
;								}catch(IOException e1){
									
									e1.printStackTrace();									
}
							}
							else {
								System.exit(0);
							}
						}
						
						
						
					}
					
					
					
			});
				
				
			
					
				
					
			}
			
		}
		else if(menu.equals("editar")) {
			editar.add(elementoItem);
			if(accion.equals("deshacer")) {
				items[2]=elementoItem;
				elementoItem.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						if(listManager.get(tPane.getSelectedIndex()).canUndo()) listManager.get(tPane.getSelectedIndex()).undo();
					}
				});
			}
			else if(accion.equals("rehacer")) {
				items[3]=elementoItem;
				elementoItem.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						if(listManager.get(tPane.getSelectedIndex()).canRedo()) listManager.get(tPane.getSelectedIndex()).redo();
					}
					
				});
			}
			else if(accion.equals("cortar")) {
				items[4]=elementoItem;
			}
			else if(accion.equals("copiar")) {
				items[5] = elementoItem;
				
			}
			else if(accion.equals("pegar")) {
				items[6] = elementoItem;
				
			}
			
		}
		else if(menu.equals("seleccion")) {
			seleccion.add(elementoItem);

			if(accion.equals("seleccion")) {
				items[7] = elementoItem;
			//
			}

		}
		
		
	}
	
	public void showErrorDialog(){
        String message = """
                         ¡Desea salir sin guardar?    
                         """;
            String title = "ERROR";
            JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
	}
	
	public void creaPanel() {
		ventana = new JPanel();
		ventana.setLayout(new BorderLayout());
		listFile.add(new File(""));
		listAreaTexto.add(new JTextPane());
		listScroll.add(new JScrollPane(listAreaTexto.get(contadorPanel)));
		listManager.add(new UndoManager()); //Para rastrear los cambios del Area de texto
		
		listAreaTexto.get(contadorPanel).getDocument().addUndoableEditListener(listManager.get(contadorPanel));
		
		listAreaTexto.get(contadorPanel).setComponentPopupMenu(menuEmergente);
		
		ventana.add(listScroll.get(contadorPanel), BorderLayout.CENTER);
		
		tPane.addTab("title",ventana);
		
		
		Utilidades.viewNumeracionInicio(numeracion, listAreaTexto.get(contadorPanel), listScroll.get(contadorPanel));
		tPane.setSelectedIndex(contadorPanel);
		contadorPanel++;
		Utilidades.aFondo(contadorPanel, tipoFondo,slider.getValue(), listAreaTexto);
		existePanel = true;
	}
	private static String tipoFondo = "w";
	private boolean numeracion = false;
	private int contadorPanel = 0; //Nos va a contar cuantos paneles se han creado
	private boolean existePanel = false; // nos va a decir si inicialmente existe un panel creado
	private JTabbedPane tPane;
	private JPanel ventana;
	private JPanel panelExtra;
	//private JTextPane areaTexto;
	private ArrayList<JTextPane> listAreaTexto;
	private ArrayList<JScrollPane> listScroll;
	private ArrayList<UndoManager> listManager;
	private ArrayList<File> listFile;
	private JMenuBar menu;
	private JMenu archivo, editar, seleccion, ver, apariencia;
	private JMenuItem elementoItem;
	private JToolBar herramientas;
	private URL url;
	
	private boolean estadoAlfiler=false;
	private JLabel labelAlfiler;
	private JSlider slider;
	
	private JPopupMenu menuEmergente;
	private JMenuItem items[];
}


