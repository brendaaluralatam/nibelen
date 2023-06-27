package ferreira.mafer.editor;

import java.awt.BorderLayout;
import java.awt.Color;
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
		JanelaBase janela = new JanelaBase();
		janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // ao fechar, o programa � encerrado
		janela.setVisible(true);

	}
}

class JanelaBase extends JFrame {
	public JanelaBase() { // esse construtor da JanelaBase ja cria as dimens�es, seta o nome e cria/agrega
							// o painel
		setBounds(600, 600, 600, 600);
		setTitle("Nibelen Editor");
		add(new Painel());
	}
}

class Painel extends JPanel {
	
	// ---------------- Os atributos da classe Painel s�o os componentes que ser�o
	// agregados a ele //----------------
	private JPanel janela; // janela que abriga a area de texto base
	private JTextPane areaDeTexto; // area de texto base
	private JTabbedPane abaPanel; // aba do editor criadas no bot�o "novo"
	private JMenuBar menuExterno;
	private JMenu archivo, editar, seleccion, ver, aparencia;
	private JMenuItem itemDasopcoesDoMenu;
	private ArrayList<JTextPane> listaDeAreadeTexto;
	private ArrayList<UndoManager> listadeManager;
	private ArrayList<File> listaDeFiles;
	private ArrayList<JScrollPane> listaDeScroll;

	// atributos
	private int contadorPainel = 0; // vai contar quantos pain�s/aba foram criados
	private boolean existePanel = false; // informa se ja existe algum painel criado
	private String tipoFondo = "f";
	private boolean numeracion = false;
	private JToolBar herramienta;
	private URL url;

	public Painel() {
		setLayout(new BorderLayout());
		
		this.abaPanel = new JTabbedPane();
		// ---------------- Menu //----------------
		JPanel panelMenu = new JPanel();
		this.menuExterno = new JMenuBar();
		this.archivo = new JMenu("Archivo");
		this.editar = new JMenu("Editar");
		this.seleccion = new JMenu("Selecci�n");
		this.ver = new JMenu("Ver");
		this.aparencia = new JMenu("Aparencia");
		this.itemDasopcoesDoMenu = new JMenuItem();

		this.menuExterno.add(this.archivo);
		this.menuExterno.add(this.editar);
		this.menuExterno.add(this.seleccion);
		this.menuExterno.add(this.ver);
		
		panelMenu.setLayout((new BorderLayout()));
		add(panelMenu, BorderLayout.NORTH);
		//panelMenu.setBackground(Color.GREEN);
		
		// --------------------------- elementos do menu Archivo
		criaItem("Nuevo Archivo", "archivo", "nuevo");
		criaItem("Abrir archivo", "archivo", "abrir");
		criaItem("Guardar", "archivo", "guardar");
		criaItem("Guardar como", "archivo", "guardarComo");
		criaItem("Salir", "archivo", "salir");
		archivo.addSeparator();
		criaItem("Sobre el Nibelen", "archivo", "equipo");
		// --------------------------- elementos do menu Editar
		criaItem("Deshacer", "editar", "deshacer");
		criaItem("Rehacer", "editar", "rehacer");
		editar.addSeparator();
		criaItem("Cortar", "editar", "cortar");
		criaItem("Copiar", "editar", "copiar");
		criaItem("Pegar", "editar", "pegar");
		// --------------------------- elementos do menu Seleccion
		criaItem("Seleccionar todo", "selecci�n", "selecci�n");
		// --------------------------- elementos do menu Ver
		criaItem("Numeraci�n", "ver", "numeracion");
		ver.add(aparencia);
		criaItem("Normal", "aparencia", "normal");
		criaItem("Oscuro", "aparencia", "dark");
		criaItem("Azul", "aparencia", "blue");
		criaItem("Bosque", "aparencia", "forest");

		panelMenu.add(this.menuExterno);
		// ---------------- Depois de criado os objetos/componentes, agregar ao Painel
		// //----------------

		// ---------------- AREA DE TEXTO
		abaPanel = new JTabbedPane();
		listaDeFiles = new ArrayList<File>();
		listaDeAreadeTexto = new ArrayList<JTextPane>();
		listaDeScroll = new ArrayList<JScrollPane>();
		listadeManager = new ArrayList<UndoManager>();
		// ----------------
		//-------------------- BARRA DE HERRAMIENTAS
		herramienta = new JToolBar(JToolBar.VERTICAL);
		//herramienta = new JToolBar(JToolBar.HORIZONTAL);
		url = Principal.class.getResource("/ferreira/mafer/image/marca-x.png");
		Utilidades.addButton(url, herramienta, "Cerrar pestana actual").addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				int seleccion = abaPanel.getSelectedIndex();
				if(seleccion != -1) { //se ha abas abertas, se fecha aqui
					listaDeScroll.get(abaPanel.getSelectedIndex()).setRowHeader(null);
					abaPanel.remove(seleccion);
					listaDeAreadeTexto.remove(seleccion);
					listaDeScroll.remove(seleccion);
					listadeManager.remove(seleccion);
					listaDeFiles.remove(seleccion);
					
					contadorPainel--;
					if(abaPanel.getSelectedIndex() == -1) {
						existePanel = false; //se retorna -1 quer dizer que n�o ha panel criado
					}
				}
				
			}
			
		});
		url = Principal.class.getResource("/ferreira/mafer/image/tree-mas1.png");
		Utilidades.addButton(url, herramienta, "Nuevo Archivo").addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				criaPainel();
				
			}
		});
		

		add(this.abaPanel, BorderLayout.CENTER);
		add(herramienta, BorderLayout.WEST);
	}

	public void criaItem(String rotulo, String menu, String accion) {
		JMenuItem itemDasopcoesDoMenu = new JMenuItem(rotulo);
		if (menu.equals("archivo")) {
			this.archivo.add(itemDasopcoesDoMenu);

			if (accion.equals("nuevo")) {
				itemDasopcoesDoMenu.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						// System.out.println("hola linda");
						criaPainel(); // cria abas
					}
				});
			} else if (accion.equals("abrir")) {
				itemDasopcoesDoMenu.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						criaPainel();
						JFileChooser selectorArchivo = new JFileChooser();
						selectorArchivo.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
						int resultado = selectorArchivo
								.showOpenDialog(listaDeAreadeTexto.get(abaPanel.getSelectedIndex()));
						System.out.println(resultado); // cancelar = 1; abrir arquivo = 0

						if (resultado == JFileChooser.APPROVE_OPTION) {
							try {
								boolean existePath = false;
								// System.out.println(file.getPath());
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
											// TODO Auto-generated catch block
											e1.printStackTrace();
										} //le linha a linha do arquivo
										if(linea != null) Utilidades.append(linea + "\n", listaDeAreadeTexto.get(abaPanel.getSelectedIndex()));
									}
									Utilidades.aFondo(contadorPainel, tipoFondo, listaDeAreadeTexto);
								} else { //se essa rota dos arquivos ja est� aberta
										//vamos varrer todas as abas abertas e ver qual tem o path do arquivo e selecion�-lo
									for(int i=0; i<abaPanel.getTabCount(); i++) {
										File f = selectorArchivo.getSelectedFile();
										if(listaDeFiles.get(i).getPath().equals(f.getPath())) {
											//seleciona o panel que tem o arquivo aberto
											abaPanel.setSelectedIndex(i); //passamos como par�metro a poi��o do panel que tem o path
											
											listaDeAreadeTexto.remove(abaPanel.getTabCount() -1);
											listaDeScroll.remove(abaPanel.getTabCount() -1);
											listaDeFiles.remove(abaPanel.getTabCount() -1);
											abaPanel.remove(abaPanel.getTabCount()-1);
											contadorPainel--;
											break;
										}
									}
								}
								
							} catch (FileNotFoundException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}

						}else {
							int seleccion = abaPanel.getSelectedIndex();
							if(seleccion != -1) {
								listaDeAreadeTexto.remove(abaPanel.getTabCount() -1);
								listaDeScroll.remove(abaPanel.getTabCount() -1);
								listaDeFiles.remove(abaPanel.getTabCount() -1);
								abaPanel.remove(abaPanel.getTabCount()-1);
								contadorPainel--;
							}
						}
						
					}
				});
			}
				
			if (accion.equals("guardar")) { //salvar o arquivo se ele j� existe
				itemDasopcoesDoMenu.addActionListener(new ActionListener() {
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
									// TODO Auto-generated catch block
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
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					}
				});
			}
			if (accion.equals("guardarComo")) {
				itemDasopcoesDoMenu.addActionListener(new ActionListener() {
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
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					}
				});
			}
			if (accion.equals("salir")) {
				itemDasopcoesDoMenu.addActionListener(new java.awt.event.ActionListener() {
		            public void actionPerformed(java.awt.event.ActionEvent evt) {
		            	if(listaDeFiles.get(abaPanel.getSelectedIndex()).getPath().equals("")) {
		            		showErrorDialog();
		           
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
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
						}
		            	else {
		                    System.exit(0);   
		            	}
		            }
		        });
			}
			if (accion.equals("equipo")) {
				itemDasopcoesDoMenu.addActionListener(new java.awt.event.ActionListener() {
		            public void actionPerformed(java.awt.event.ActionEvent evt) {
		            	showAboutDialog();
		            }
		        });
			}
		} else if (menu.equals("editar")) {
			this.editar.add(itemDasopcoesDoMenu);
			if (accion.equals("deshacer")) {
				itemDasopcoesDoMenu.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if(listadeManager.get(abaPanel.getSelectedIndex()).canUndo()) listadeManager.get(abaPanel.getSelectedIndex()).undo();
					}
					
			});
		}
			else if(accion.equals("rehacer")) {
				itemDasopcoesDoMenu.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if(listadeManager.get(abaPanel.getSelectedIndex()).canRedo()) listadeManager.get(abaPanel.getSelectedIndex()).redo();
						
					}
					
			});
			}
			else if(accion.equals("cortar")) {
				// Acción de copiar texto seleccionado
				itemDasopcoesDoMenu.addActionListener(new DefaultEditorKit.CopyAction());
				// Comprueba si hay texto seleccionado y envía un mensaje de alerta si no existe
				itemDasopcoesDoMenu.addActionListener(new ActionListener() {

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
				itemDasopcoesDoMenu.addActionListener(new DefaultEditorKit.CopyAction());
				// Comprueba si hay texto seleccionado y envía un mensaje de alerta si no existe
				itemDasopcoesDoMenu.addActionListener(new ActionListener() {

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
				itemDasopcoesDoMenu.addActionListener(new DefaultEditorKit.PasteAction());
				// Comprueba si hay texto seleccionado y envía un mensaje de alerta si no existe
				itemDasopcoesDoMenu.addActionListener(new ActionListener() {

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
			
		} else if (menu.equals("selecci�n")) {
			if(accion.equals("selecci�n")) {
				this.seleccion.add(itemDasopcoesDoMenu);
				itemDasopcoesDoMenu.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						listaDeAreadeTexto.get(abaPanel.getSelectedIndex()).selectAll();
					}
				});	
			}
		} else if (menu.equals("ver")) {
			this.ver.add(itemDasopcoesDoMenu);
			if(accion.equals("numeracion")) {
				itemDasopcoesDoMenu.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						numeracion = !numeracion;
						Utilidades.viewNumeracion(contadorPainel, numeracion, listaDeAreadeTexto, listaDeScroll);
					}
				});	
			}
			
		} else if (menu.equals("aparencia")) {
			this.aparencia.add(itemDasopcoesDoMenu);
			
			if(accion.equals("normal")) {
				itemDasopcoesDoMenu.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						tipoFondo = "w";
						if(abaPanel.getTabCount() >0) Utilidades.aFondo(contadorPainel, tipoFondo, listaDeAreadeTexto);		
					}	
				});
			}
			else if(accion.equals("dark")) {
				itemDasopcoesDoMenu.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						tipoFondo = "d";
						if(abaPanel.getTabCount() >0) Utilidades.aFondo(contadorPainel, tipoFondo, listaDeAreadeTexto);		
					}	
				});
			}
			else if(accion.equals("blue")) {
				itemDasopcoesDoMenu.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						tipoFondo = "b";
						if(abaPanel.getTabCount() >0) Utilidades.aFondo(contadorPainel, tipoFondo, listaDeAreadeTexto);		
					}	
				});
			}
			else if(accion.equals("forest")) {
				itemDasopcoesDoMenu.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						tipoFondo = "f";
						if(abaPanel.getTabCount() >0) Utilidades.aFondo(contadorPainel, tipoFondo, listaDeAreadeTexto);		
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
	                - Eric Monn�
	                - Luis Puig
	                - Maria Fernada Ferreira
	                
	                Tecnolog�as usadas:
	                Java 17 y Swing
	                
	                =]
	                """;
	        String title = "Sobre el equipo Alquimista ...";
	        JOptionPane.showMessageDialog(this, message, title,
	                JOptionPane.PLAIN_MESSAGE);
	 }
	 public void showErrorDialog(){
	        String message = """
	                         �No hay fichero seleccionado!
	                         Prueba la opci�n guardar como.    
	                         """;
	            String title = "ERROR";
	            JOptionPane.showMessageDialog(this, message, title,
	                    JOptionPane.ERROR_MESSAGE);
	}
	public void criaPainel() {
		// ---------------- cria os objetos //----------------
		this.janela = new JPanel();
		janela.setLayout(new BorderLayout());
		

		listaDeFiles.add(new File(""));
		listaDeAreadeTexto.add(new JTextPane());
		listaDeScroll.add(new JScrollPane(listaDeAreadeTexto.get(contadorPainel)));
		listadeManager.add(new UndoManager()); //servir� para rastrear os cambios hechos na Area de texto
		
		listaDeAreadeTexto.get(contadorPainel).getDocument().addUndoableEditListener(listadeManager.get(contadorPainel));
		this.areaDeTexto = new JTextPane();
		// ---------------- agregar os objetos //----------------
		this.janela.add(listaDeScroll.get(contadorPainel), BorderLayout.CENTER);
		this.abaPanel.addTab("title", this.janela);
		Utilidades.viewNumerationInicio(numeracion, listaDeAreadeTexto.get(contadorPainel), listaDeScroll.get(contadorPainel));

		this.abaPanel.setSelectedIndex(contadorPainel);
		
		contadorPainel++;
		Utilidades.aFondo(contadorPainel, tipoFondo, listaDeAreadeTexto);
		existePanel = true;
		
	
	}
	

}
