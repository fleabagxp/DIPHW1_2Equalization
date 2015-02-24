/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package image2;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import static java.lang.Math.round;
import javax.swing.*;
import java.util.*;

/**
 *
 * @author fleabag
 */
public class showPGM extends Component {

    private BufferedImage img;
    private int[][] px;
    static double[] histogram = new double[256];
    static double[] haa0 = new double[256];
    static double[] CDF = new double[256];
    static int[] hiseqz = new int[256];
    static int[] histogram_eqzed = new int[256];

    private void pix2img() {
        int g;
        img = new BufferedImage(px[0].length, px.length, BufferedImage.TYPE_INT_ARGB);
        for (int row = 0; row < px.length; ++row) {
            for (int col = 0; col < px[row].length; ++col) {
                g = px[row][col];
                img.setRGB(col, row, ((255 << 24) | (g << 16) | (g << 8) | g));
            }
        }
    }

    public showPGM(String filename) {
        px = null;
        readFile(filename);
        if (px != null) {
            pix2img();
        }

        JFrame f = new JFrame("PGM");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        f.add(this);
        f.pack();
        f.setVisible(true);
    }

    public void readFile(String filename) {
        try {
            String filePath = filename;
            FileInputStream fileInputStream = new FileInputStream(filePath);

            Scanner scan = new Scanner(fileInputStream);
            scan.nextLine();
            scan.nextLine();
            int picHeight = scan.nextInt();
            int picWidth = scan.nextInt();
            int maxvalue = scan.nextInt();

            fileInputStream.close();

            fileInputStream = new FileInputStream(filePath);
            DataInputStream dis = new DataInputStream(fileInputStream);

            int numnewlines = 4;
            while (numnewlines > 0) {
                char c;
                do {
                    c = (char) (dis.readUnsignedByte());
                } while (c != '\n');
                numnewlines--;
            }

            int a;
            int area = picWidth * picHeight;
            px = new int[picWidth][picHeight];
            for (int row = 0; row < picWidth; row++) {
                for (int col = 0; col < picHeight; col++) {
                    px[row][col] = dis.readUnsignedByte();
                    a = px[row][col];
                    histogram[a]++;
                }
            }

            for (int i = 0; i <= 255; i++) {
                haa0[i] = histogram[i] / area;
            }

            for (int i = 0; i <= 255; i++) {
                if (i == 0) {
                    CDF[0] = haa0[0];
                } else {
                    CDF[i] = (CDF[i - 1] + haa0[i]);
                }
            }
            for (int i = 0; i <= 255; i++) {
                hiseqz[i] = (int) round(maxvalue * CDF[i]);
            }

            for (int row = 0; row < picWidth; row++) {
                for (int col = 0; col < picHeight; col++) {
                    px[row][col] = hiseqz[px[row][col]];
                }
            }
            for (int row = 0; row < picWidth; row++) {
                for (int col = 0; col < picHeight; col++) {
                    if (px[row][col] >= 0) {
                        a = px[row][col];
                        histogram_eqzed[a]++;
                    }
                }
            }

            fileInputStream.close();
        } catch (FileNotFoundException fe) {
            System.out.println("Had a problem opening a file.");
        } catch (Exception e) {
            System.out.println(e.toString() + " caught in readPPM.");
            e.printStackTrace();
        }
    }

    public void paint(Graphics g) {
        g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
    }

    public Dimension getPreferredSize() {
        if (img == null) {
            return new Dimension(100, 100);
        } else {
            return new Dimension(Math.max(100, img.getWidth(null)),
                    Math.max(100, img.getHeight(null)));
        }
    }
}
