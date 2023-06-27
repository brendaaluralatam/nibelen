package ferreira.mafer.editor;

import java.awt.Color;
import java.awt.Container;
import java.awt.Image;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

public class Utilidades {
	//------------------- adiciona texto ao final
	public static void append(String linea, JTextPane areaTexto) {
		 try {
			 Document doc = areaTexto.getDocument();
			 doc.insertString(doc.getLength(), linea, null);
		 } catch(BadLocationException exc) {
			 exc.printStackTrace();
		 }
	}
	//-------------------  mostrar a numeração da aba
	public static void viewNumerationInicio(boolean numeracion, JTextPane textArea, JScrollPane scroll) {
		if(numeracion) {
			scroll.setRowHeaderView(new TextLineNumber(textArea));
		}else {
			scroll.setRowHeaderView(null);
		}
	}
	public static void viewNumeracion(int contador, boolean numeracion, ArrayList<JTextPane> textArea, ArrayList<JScrollPane> scroll ) {
		if(numeracion) {
			for(int i =0 ; i<contador; i++) {
				scroll.get(i).setRowHeaderView(new TextLineNumber(textArea.get(i)));
			}
		}
		else {
			for(int i=0; i<contador; i++) {
				scroll.get(i).setRowHeaderView(null);
			}
		}
	}
	//------------------- aparencia
	public static void aFondo(int contador, String tipo, ArrayList<JTextPane> list ) {
		if(tipo.equals("w")) {
			for(int i=0; i<contador; i++) {
				
				list.get(i).selectAll();
				
				StyleContext sc = StyleContext.getDefaultStyleContext();
				
				//essa ação define a cor do texto:
				AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.BLACK);
				//essa ação define o estilo do texto:
				aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Arial");
				
				list.get(i).setCharacterAttributes(aset, false);
				list.get(i).setBackground(Color.WHITE);
			}
		}
		else if(tipo.equals("d")) {
			for(int i=0; i<contador; i++) {
				list.get(i).selectAll();
				StyleContext sc = StyleContext.getDefaultStyleContext();
				
				//essa ação define a cor do texto:
				AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, new Color(224, 224, 224));
				//essa ação define o estilo do texto:
				aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Arial");
				
				list.get(i).setCharacterAttributes(aset, false);
				//list.get(i).setBackground(new Color(161, 145, 123));
				list.get(i).setBackground(new Color(96, 96, 96));
			}
		}
		else if(tipo.equals("b")) {
			for(int i=0; i<contador; i++) {
				list.get(i).selectAll();
				StyleContext sc = StyleContext.getDefaultStyleContext();
				
				//essa ação define a cor do texto:
				AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, new Color(0, 0, 153));
				//essa ação define o estilo do texto:
				aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Arial");
				
				list.get(i).setCharacterAttributes(aset, false);
				//list.get(i).setBackground(new Color(161, 145, 123));
				list.get(i).setBackground(new Color(204, 229, 255));
			}
		}
		else if(tipo.equals("f")) {
			for(int i=0; i<contador; i++) {
				list.get(i).selectAll();
				StyleContext sc = StyleContext.getDefaultStyleContext();
				
				//essa ação define a cor do texto:
				AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, new Color(129, 32, 13));
				//essa ação define o estilo do texto:
				aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Verdana");
				
				list.get(i).setCharacterAttributes(aset, false);
				list.get(i).setBackground(new Color(101, 156, 97));
			}
		}
		
	}
	//------------------- botão
	public static JButton addButton(URL url, Object objContenedor, String rotulo) {
		JButton button = new JButton(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(20,20, Image.SCALE_SMOOTH)));
		button.setToolTipText(rotulo);
		((Container) objContenedor).add(button);
		return button;
	}
}

