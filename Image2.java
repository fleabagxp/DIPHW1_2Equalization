/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package image2;

/**
 *
 * @author fleabag
 */
import java.awt.Color;
import java.awt.Graphics;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import javax.swing.JFrame;
import java.awt.*;
import static java.lang.Math.round;

/**
 *
 * @author fleabag
 */
public class Image2 extends JFrame {

    /**
     * @param args the command line arguments
     */
    static double[] histogram = new double[256];
    static double[] haa0 = new double[256];
    static double[] CDF = new double[256];
    static int[] hiseqz = new int[256];
    static int[] histogram_eqzed = new int[256];

    public static void main(String[] args) throws FileNotFoundException, IOException {
        // TODO code application logic here
        String filePath = "D:\\Users\\fleabag\\Documents\\NetBeansProjects\\Image1\\src\\image1\\Image\\2\\Cameraman.pgm";
        //String filePath = "D:\\Users\\fleabag\\Documents\\NetBeansProjects\\Image1\\src\\image1\\Image\\2\\SEM256_256.pgm";
        FileInputStream fileInputStream = new FileInputStream(filePath);

        Scanner scan = new Scanner(fileInputStream);
        scan.nextLine(); //Header Line 1
        scan.nextLine();//Header Line 2

        int picHeight = scan.nextInt(); //Header Line 3
        int picWidth = scan.nextInt();  //Header Line 3
        int maxvalue = scan.nextInt(); //Header Line 4

        fileInputStream = new FileInputStream(filePath);
        DataInputStream dis = new DataInputStream(fileInputStream);

        int numnewlines = 4;
        while (numnewlines > 0) {
            char c;
            do {
                c = (char) (dis.readUnsignedByte());
                System.out.print(c);
            } while (c != '\n');
            numnewlines--;
        }

        int a;
        int area = picWidth*picHeight;
        int[][] data2D = new int[picWidth][picHeight];
        for (int row = 0; row < picWidth; row++) {
            for (int col = 0; col < picHeight; col++) {
                data2D[row][col] = dis.readUnsignedByte();
                a = data2D[row][col];
                histogram[a]++;
            }
        }
  
        System.out.println("Area = "+area);
        for(int i=0;i<=255;i++)
        {
            haa0[i]=histogram[i]/area;
        }
        
        for (int i = 0; i <= 255; i++) 
        {
            if (i == 0) {
                CDF[0] = haa0[0];
            } else {
                CDF[i] = (CDF[i - 1] + haa0[i]);
            }
            System.out.println(i + " : " + histogram[i] + " CDF : " + CDF[i]);
        }

        for(int i=0;i<=255;i++)
        {
            hiseqz[i]=(int) round(maxvalue*CDF[i]);
        }
        for (int row = 0; row < picWidth; row++) 
        {
            for (int col = 0; col < picHeight; col++) {
                data2D[row][col] = hiseqz[data2D[row][col]];   
            }
        }
        for (int row = 0; row < picWidth; row++) 
        {
            for (int col = 0; col < picHeight; col++) {
                if (data2D[row][col] >= 0){
                    a = data2D[row][col];
                    histogram_eqzed[a]++;
                }
                System.out.print(data2D[row][col] + " ");
            }
            System.out.println();
        }
        
        fileInputStream.close();

        Image2 image = new Image2();
        showPGM pgm = new showPGM(filePath);
    }

    public Image2() {
        super("Histogram");
        Dimension size = new Dimension(300, 350);
        Toolkit tk = getToolkit();
        Dimension screen = tk.getScreenSize();
        setBounds((screen.width - size.width) / 2, (screen.height - size.height) / 2, size.width, size.height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.GRAY);
        setVisible(true);
    }
    
    @Override
    public void paint(Graphics g) {

        super.paint(g);

        g.setColor(Color.WHITE);

        if (histogram_eqzed != null) {
            int widths = 45;
            int height = 350;
            int HhPos = (widths - (widths / 2));
            int HvPos = 342;
            for (int i = 0; i <= 255; i++) {
                int[] h = histogram_eqzed;
                h[i] = (h[i] * 100) / 300;
                g.fillRect(i + HhPos, HvPos, 1, -h[i]);
            }
        }
    }
}
