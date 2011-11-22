package net.guto;

import java.awt.Button;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import net.guto.loader.SourceLoader;

public class Main extends JFrame implements ActionListener {

	private static final long serialVersionUID = -8000316696290413473L;

	public Main() {
		init();
	}

	public void init() {
		Button b = new Button("Run");
		b.addActionListener(this);
		add(b);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(100, 100);
		setVisible(true);
	}

	public static void main(String[] args) {
		new Main();
	}

	public void actionPerformed(ActionEvent e) {
		//Thread.currentThread().setContextClassLoader(sl);
		try {
			SourceLoader sl = new SourceLoader(ClassLoader.getSystemClassLoader(), "target",
					"src/main/resources", "target");
			
			Class.forName("net.guto.test.Test", false, sl).newInstance();
			
			
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
}
