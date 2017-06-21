/*Do folderu z projektem nale¿y umieœciæ zdjêcie, najlepiej wa¿¹ce ok. 200KB, nazwane "source.jpg"
 W za³¹czniku do³¹czone jest przyk³adowe zdjêcie, na którym dobrze widaæ zmiany
 Najpierw generuje wykres wskazowy symbolicznej modulacji QPSK
 Nastêpnia pobiera zdjêcie (source.jpg) i zamienia je na bajty
 Przerabia czêœæ tych bajtów wed³ug wspó³czynnika stworzonego na podstawie odsetku b³êdów z wykresu
 Na koñcu zapisuje to zdjêcie w formacie .jpg aby zobaczyæ skale zepsucia pliku
 Dla niewielkiego wspó³czynnika bêdzie niemal bez zmian
 Dla du¿ego - zdjêcie bêdzie nie do rozró¿nienia
*/

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;



class point{
	int X;
	int Y;
	int x;
	int y;
	boolean failed;
	static Random random = new Random();
	
    void move(float dx, float dy){ X+=dx; Y+=dy; }

	point(int setvalue){
				
		do{
		failed=false;
		x=random.nextInt();
		if (x>=0) break;
		}while(true);
		this.X=x%2;
		do{
		y=random.nextInt();
		if (y>=0) break;
		}while(true);
		this.Y=y%2;
		
		if (X==0) X=-1;
		if (Y==0) Y=-1;
		
		X=(X*(-200))+400;
		Y=(Y*(-200))+400;
	
		double rad,ang,difx,dify;
		ang=random.nextInt()%360;
		if (ang<0) ang=(-ang);
		rad = random.nextDouble();
		double RAD = Math.log(1-rad);
		RAD=RAD*setvalue;
		double ANG = ang;
		double angleInRadian = Math.toRadians(ANG);
		double cos = Math.cos(angleInRadian); 
		double sin = Math.sin(angleInRadian);
		difx=cos*RAD;
		dify=sin*RAD;
		x=(int) difx;	if(Math.abs(x)>=200) failed=true;
		y=(int) dify;	if(Math.abs(y)>=200) failed=true;
		X=X+x;
		Y=Y+y;
	}
	
	void draw (Graphics g){
		g.fillOval((X+3),(Y+3), 6,6);
	}
	
};

class question extends JFrame implements ActionListener{
	
	private static final long serialVersionUID = -5279663807534107286L;
	
	int width=400;
	int height=250;
	
	private final JComboBox<Integer> selection = new JComboBox<Integer>();
	JButton button=new JButton("OK");
    JTextArea textarea=new JTextArea();
    
	private JPanel panel;
	
	public question(){
		
		panel = new JPanel();
		panel.setPreferredSize(new Dimension(width,height));
				
		Container cp=getContentPane();
		cp.add(panel);
		
		this.textarea.setEditable(false);
        this.textarea.setColumns(35);
        this.textarea.setRows(6);
        JScrollPane scrollPane = new JScrollPane(this.textarea);
        panel.add(scrollPane);
		
		textarea.setText("Symulator modulacji QPSK\n"
				+ "Czarne kropki symbolizuj¹ sygna³y otrzymane poprawnie\n"
				+ "Czerwone - sygna³y otrzymane b³êdnie\n"
				+ "Wybierz wielkoœæ b³êdu\n"
				+ "(0 - przekaz bezb³êdny)\n"
				+ "Po wciœniêciu OK zaczekaj moment");
        
		this.selection.addActionListener(this);
		panel.add(selection);
		
		this.selection.addItem(0);
		this.selection.addItem(30);
		this.selection.addItem(60);
		this.selection.addItem(100);
		this.selection.addItem(150);
		this.selection.addItem(200);
		
		button.addActionListener(this);
		panel.add(button);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setTitle("Wykres");
		setVisible(true);
		
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		
		Object source=evt.getSource();
		if (source==button){
		int b=(int) this.selection.getSelectedItem();	
		new wykres(b);
		setVisible(false);
		dispose();		
		}
	}
}

public class wykres extends JFrame{
	
	int ile=1000;
	int width=800;
	int height=800;
	Vector<point> points= new Vector<point>();		

	private DrawCanvas canvas;
	
	public wykres(int entry){
		canvas=new DrawCanvas();
		canvas.setPreferredSize(new Dimension(width,height));
				
		Container cp=getContentPane();
		cp.add(canvas);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setTitle("Wykres");
		
		int broken=0;
		int n;
		int perint;
		
		for (n=0;n<ile;n++){
		point p=new point(entry);
		points.addElement(p);
		if (p.failed==true) broken++;
		}
		
		double percentage=broken;
		percentage=n/percentage;
		perint=(int) percentage;

		try {
		int count=0;
		byte[] imageInByte; 
		int binary;
	        
		BufferedImage Image = ImageIO.read(new File("source.jpg"));
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ImageIO.write(Image, "jpg", byteArrayOutputStream);
		imageInByte = byteArrayOutputStream.toByteArray();
		byte[] imageFromByte=imageInByte;
		for (int i = 0; i < imageInByte.length; i++)
		{
			binary=imageInByte[i];
			System.out.println(i + "/" + imageInByte.length + "  " + count);
	        	
			if (binary==70) 
			{
				count++;
				if (count%perint==0) binary++;	
			}
			imageFromByte[i]=(byte) binary;
	        }
         	        
		byteArrayOutputStream.close();
	            	        
		InputStream in = new ByteArrayInputStream(imageFromByte);
		Image = ImageIO.read(in);
		ImageIO.write(Image, "jpg", new File("output.jpg"));
		System.out.println("Gotowe");
		setVisible(true);

	    } 
	catch (IOException e) 
	{
		System.out.println(e.getMessage());
	}
};
	
	private class DrawCanvas extends JPanel{
		
		
		private static final long serialVersionUID = 3466841033283216750L;
				
		
		@Override
		public void paintComponent(Graphics g){
			super.paintComponent(g);
			g.drawLine(400, 0, 400, 800);
			g.drawLine(0, 400, 800, 400);
			int n;
	         
			for (n=0;n<ile;n++)
			{if (points.get(n).failed==true) g.setColor(Color.RED);
			else	g.setColor(Color.BLACK);
			points.get(n).draw(g);}
	        
		}		
	};
		

	private static final long serialVersionUID = 8229610608028746001L;

	public static void main(String[] args) {
			new question();
	}


}
