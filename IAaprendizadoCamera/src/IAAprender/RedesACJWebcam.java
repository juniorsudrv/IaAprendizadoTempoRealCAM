/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IAAprender;

import Biblis.MemoryUtils;
import OpIO.IO;
import TrabalhaBits.NumeroBits;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import jcanny.JCanny;
import meupneuronio.OpenAJC;

/**
 *
 * @author junior
 */
class IA implements Serializable {

    int sizeRecX = 16;
    int sizeRecY = 16;
    int qtdQdRec = 4;
    int bdiv = 2;

    Thread treino = null;

    int xP = 80;
    int yP = 80;

    int ini = 0;

    int sizeTam = 2, size = 2;

    public int nresult = -1;

    ArrayList<String> itens = new ArrayList<>();
    ArrayList<ArrayList<OpenAJC>> nr = new ArrayList<>();

    public void zera() {
        nr = new ArrayList<>();
    }

    public void setValorTreinoByte(int index, BufferedImage img, byte result) {

        atualizaCoord(img);

        int size = -1;
        if (index >= nr.size()) {

            if (size == -1) {

                File temp = new File("tempTreino" + 0 + ".png");
                try {
                    ImageIO.write(convertScaled(img.getSubimage(cdC.get(0).xI, cdC.get(0).yI,
                            cdC.get(0).largX, cdC.get(0).altX)),
                            "png", temp);
                    size = OpenAJC.lenghtImagem(temp) * 8 * 4;
                } catch (IOException ex) {
                    Logger.getLogger(IA.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

            ArrayList<OpenAJC> nnr = new ArrayList<OpenAJC>();

            for (int cont = 0; cont < qtdQdRec; cont++) {

                nnr.add(new OpenAJC(size));
            }

            nr.add(nnr);
        }

        for (int cont = 0; cont < cdC.size(); cont++) {

            //  File temp = new File("tempTreino" + cont + ".png");
//                ImageIO.write(convertScaled(img.getSubimage(cdC.get(cont).xI, cdC.get(cont).yI,
//                        cdC.get(cont).largX, cdC.get(cont).altX)),
//                        "png", temp);
            nr.get(index).get(cont).setValorTreinoNovoBytesBits(convertScaled(img.getSubimage(cdC.get(cont).xI, cdC.get(cont).yI,
                    cdC.get(cont).largX, cdC.get(cont).altX)), result);

        }

    }

    public void limpaDadosTreino() {

        for (ArrayList<OpenAJC> nR : nr) {
            for (OpenAJC n : nR) {

                n.limpaValoresTreino();

            }
        }
    }

    public void treinar(JProgressBar progresso, int sizeTam, int size, JButton btreino) {

        this.sizeTam = sizeTam;
        this.size = size;
        int valueInc = 100 / nr.size();
        progresso.setValue(0);
        ini = 0;

        for (ArrayList<OpenAJC> nR : nr) {
            for (OpenAJC n : nR) {
                System.gc();
                n.treinarRedePSFileiraValidaExists(sizeTam, size);
            }
            progresso.setValue((ini += valueInc));
        }
        System.gc();
        progresso.setValue(100);
        btreino.setEnabled(true);

    }

    public void treinarNew(JProgressBar progresso, int sizeTam, int size, JButton btreino) {

        this.sizeTam = sizeTam;
        this.size = size;
        int valueInc = 100 / nr.size();
        progresso.setValue(0);
        ini = 0;

        for (ArrayList<OpenAJC> nR : nr) {
            for (OpenAJC n : nR) {
                System.gc();
                n.treinarRedePSFileira(sizeTam, size);
            }
            progresso.setValue((ini += valueInc));
        }

        progresso.setValue(100);
        btreino.setEnabled(true);

    }

    public String getResult(JComboBox combo, BufferedImage img) {
        atualizaCoord(img);
        String result = null;
        int conCertResult = -1;
        for (int cont = 0; cont < nr.size(); cont++) {

            ArrayList<OpenAJC> nR = nr.get(cont);
            int contCerto = 0;
            for (int cNr = 0; cNr < nR.size(); cNr++) {

                OpenAJC n = nR.get(cNr);

                //File temp = new File("temp" + cNr + ".png");
//                    ImageIO.write(convertScaled(img.getSubimage(cdC.get(cNr).xI, cdC.get(cNr).yI,
//                            cdC.get(cNr).largX, cdC.get(cNr).altX)),
//                            "png", temp);
                ArrayList<NumeroBits> ar = new ArrayList<>();
                ar.add(nr.get(0).get(0).getValorTreinoNovoFileiras(convertScaled(img.getSubimage(cdC.get(cNr).xI, cdC.get(cNr).yI,
                        cdC.get(cNr).largX, cdC.get(cNr).altX))));
                System.gc();
                if (n.saidaPSFileira(ar).get(0) >= 0) {
                    contCerto++;
                }
            }
            System.out.println("Concerto2 " + combo.getItemAt(cont) + " " + contCerto);

            if (conCertResult < contCerto) {
                conCertResult = contCerto;
                result = combo.getItemAt(cont) + "";

            }

        }

        return result;
    }

    public int[] getResultCont(JComboBox combo, BufferedImage img) {

        atualizaCoord(img);
        int conCertResult = -1;
        int contresult = 0;
        for (int cont = 0; cont < nr.size(); cont++) {

            ArrayList<OpenAJC> nR = nr.get(cont);
            int contCerto = 0;
            for (int cNr = 0; cNr < nR.size(); cNr++) {

                OpenAJC n = nR.get(cNr);

                //  File temp = new File("temp" + cNr + ".png");
//                    ImageIO.write(convertScaled(img.getSubimage(cdC.get(cNr).xI, cdC.get(cNr).yI,
//                            cdC.get(cNr).largX, cdC.get(cNr).altX)),
//                            "png", temp);
                ArrayList<NumeroBits> ar = new ArrayList<>();
                ar.add(nr.get(0).get(0).getValorTreinoNovoFileiras(convertScaled(img.getSubimage(cdC.get(cNr).xI, cdC.get(cNr).yI,
                        cdC.get(cNr).largX, cdC.get(cNr).altX))));
                System.gc();
                if (n.saidaPSFileira(ar).get(0) >= 0) {
                    contCerto++;
                }
            }

            if (conCertResult < contCerto) {
                conCertResult = contCerto;
                contresult = cont;

            }

        }
        System.out.println("Concerto " + combo.getItemAt(contresult) + " " + conCertResult + " " + img.getWidth() + " " + img.getHeight());
        return new int[]{contresult, conCertResult};
    }

    public int getResultCont(int indexFind, BufferedImage img) {
        atualizaCoord(img);
        int acertos = 0;
        ArrayList<OpenAJC> nR = nr.get(indexFind);

        for (int cNr = 0; cNr < nR.size(); cNr++) {

            OpenAJC n = nR.get(cNr);

            ArrayList<NumeroBits> ar = new ArrayList<>();
            ar.add(nr.get(0).get(0).getValorTreinoNovoFileiras(convertScaled(img.getSubimage(cdC.get(cNr).xI, cdC.get(cNr).yI,
                    cdC.get(cNr).largX, cdC.get(cNr).altX))));

            if (n.saidaPSFileira(ar).get(0) >= 0) {
                acertos++;
            }
        }
        System.out.println("Concerto  " + indexFind + " " + acertos + " " + img.getWidth() + " " + img.getHeight());
        System.gc();
        return acertos;
    }

    public BufferedImage convertScaled(BufferedImage img) {
        BufferedImage bi = new BufferedImage(
                sizeRecX, sizeRecY,
                BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics2D = bi.createGraphics();
        graphics2D.drawImage(img.getScaledInstance(sizeRecX, sizeRecY, Image.SCALE_DEFAULT), 0, 0, null);
        graphics2D.dispose();
        return bi;
    }

    public void atualizaCoord(BufferedImage img) {
        cdC.clear();

        int xqMin = img.getWidth() / (qtdQdRec / bdiv);
        int yqMin = img.getHeight() / (qtdQdRec / bdiv);

        int xIIC = 0;
        int yIIC = 0;

        xqMin = xqMin - 1;

        for (int cont = 0; cont < qtdQdRec; cont++) {

            cdC.add(coordenadasCorte(xIIC, yIIC, xqMin, yqMin));

            xIIC += xqMin;

            if (xIIC + xqMin > img.getWidth()) {
                yIIC += yqMin;
                xIIC = 0;

                if (yIIC + yqMin > img.getHeight()) {

                    yIIC = yIIC - ((yIIC + yqMin) - img.getHeight());

                }
            }

        }
    }

    public coordenadasCorte coordenadasCorte(int xI, int yI, int largX, int altX) {
        return new coordenadasCorte(xI, yI, largX, altX);

    }

    public class coordenadasCorte implements Serializable {

        int xI, yI;
        int largX, altX;

        public coordenadasCorte(int xI, int yI, int largX, int altX) {
            this.xI = xI;
            this.yI = yI;
            this.largX = largX;
            this.altX = altX;
        }

    }

    ArrayList<coordenadasCorte> cdC = new ArrayList();

}

public class RedesACJWebcam extends javax.swing.JFrame {

    class analisaResultImgBufferedImg implements Cloneable {

        Color cor;
        BufferedImage imgRec = null;
        int iIndexResult;
        int acertosResult;
        String result;
        int xR, yR, lR, aR;
        public int area;

        public analisaResultImgBufferedImg(BufferedImage imgRec, int xR, int yR, int lR, int aR, Color cor) {
            this.xR = xR;
            this.yR = yR;
            this.lR = lR;
            this.aR = aR;
            this.imgRec = imgRec;
            this.cor = cor;

        }

        public int area() {
            return aR * lR;
        }

        @Override
        public analisaResultImgBufferedImg clone() {
            try {
                return (analisaResultImgBufferedImg) super.clone();
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(RedesACJWebcam.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }

        @Override
        public String toString() {
            return "analisaResultImgBufferedImg{" + "cor=" + cor + ", imgRec=" + imgRec + ", iIndexResult=" + iIndexResult + ", acertosResult=" + acertosResult + ", result=" + result + ", xR=" + xR + ", yR=" + yR + ", lR=" + lR + ", aR=" + aR + ", area=" + area + '}';
        }

    }

    ArrayList<analisaResultImgBufferedImg> anR = new ArrayList<>();

    ArrayList<analisaResultImgBufferedImg> anRTemp = new ArrayList<>();

    int limitcor = 255;

    Image fundoL = null;
    BufferedImage fundo = null;

    static byte SIM = 2;
    static byte NAO = -2;
    IA ia = new IA();
    int sizeReal = -1;

    int xI = 0, yI = 0;
    BufferedImage imagem = null;
    Webcam webcam = null;

    String result = "";
    int nresult = 1000;
    boolean coletaid0 = false, coletaid1 = false;
    boolean reconhece = false;
    boolean reconheceSimples = false;
    boolean reconheceMulti = false;
    boolean treinaTempoReal = false;
    File temp = null;
    RecorteAtual rec = null;

    public boolean usarCamera = true;

    public RedesACJWebcam() {
        rec = new RecorteAtual();
        rec.setVisible(true);
        initComponents();
        painel.setVisible(false);
        temp = new File("temp.png");
//        try {
//            temp.createNewFile();
//        } catch (IOException ex) {
//            Logger.getLogger(RedesACJWebcam.class.getName()).log(Level.SEVERE, null, ex);
//        }
        System.out.println("" + temp.getAbsolutePath());
        new Thread(new Runnable() {
            @Override
            public void run() {
                bt_treinar.setEnabled(false);
                Webcam fw = null;
                for (Webcam w : Webcam.getWebcams()) {
                    fw = w;
                    //  break;
                }

                webcam = fw;
                webcam.setViewSize(WebcamResolution.VGA.getSize());

                treino_progress.setValue(10);
                webcam.open();

                if (ia == null) {
                    ia = new IA();
                }

                largura.setText(ia.xP + "");
                altura.setText(ia.yP + "");

                tamFileira.setText(ia.sizeTam + "");
                neuronios.setText(ia.size + "");

                treino_progress.setValue(20);
                atualizaColetas();
                treino_progress.setValue(60);
                bt_reconhecer.setBackground(Color.YELLOW);

                preencheRedes();
                treino_progress.setValue(100);

                bt_treinar.setEnabled(true);

                painel.setVisible(true);

            }
        }
        ).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    memoria.setText((int) MemoryUtils.usedMemory() + "  " + MemoryUtils.maxMemory());

                    try {
                        Thread.sleep(800);
                    } catch (InterruptedException ex) {

                    }
                }
            }
        }).start();

        comb_redes.removeAllItems();

        // valor.setText(limitcor + "");
    }

    public static BufferedImage image2BlackWhite(BufferedImage image1) {

        int w = image1.getWidth();
        int h = image1.getHeight();
        byte[] comp = {0, -1};
        IndexColorModel cm = new IndexColorModel(2, 2, comp, comp, comp);
        BufferedImage image2 = new BufferedImage(w, h,
                BufferedImage.TYPE_BYTE_INDEXED, cm);
        Graphics2D g = image2.createGraphics();
        g.drawRenderedImage(image1, null);
        g.dispose();

        return image2;
    }

    private BufferedImage toBinary(BufferedImage image, int t) {
        int BLACK = Color.BLACK.getRGB();
        int WHITE = Color.WHITE.getRGB();

        BufferedImage output = new BufferedImage(image.getWidth(),
                image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

        // Percorre a imagem definindo na saída o pixel como branco se o valor
        // na entrada for menor que o threshold, ou como preto se for maior.
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color pixel = new Color(image.getRGB(x, y));
                output.setRGB(x, y, pixel.getRed() < t ? BLACK : WHITE);
            }
        }

        return output;
    }

    private BufferedImage toGrayscale(BufferedImage image) {
        BufferedImage output = new BufferedImage(image.getWidth(),
                image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2d = output.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();
        return output;
    }

    ArrayList<Color> cores = new ArrayList<>();

    class prancha_camera_impl extends JPanel {

        BufferedImage local = null;
        boolean setFont = true;
        private static final double CANNY_THRESHOLD_RATIO = .2; //Suggested range .2 - .4
        private static final int CANNY_STD_DEV = 1;

        public prancha_camera_impl() {

        }

        int vcont = 0;

        @Override
        public void paintComponent(Graphics g) {

            if (getWidth() > 5 && fundo == null) {

                try {
                    fundoL = ImageIO.read(new File("images.jpeg"));
                    fundo = toBufferedImage(fundoL.getScaledInstance(getWidth(), getHeight(), Image.SCALE_DEFAULT));
                } catch (IOException ex) {
                    Logger.getLogger(RedesACJWebcam.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            if (webcam != null && (local = webcam.getImage()) != null) {

                //imagem = MeuAJC.image2BlackWhiteTest(local);
                imagem = usarCamera ? local : fundo;//local;//toGrayscale(local);  //local;//toGrayscale(local);   // toBinary(local,limitcor );
                //JCanny.CannyEdges(local, 1, 0.55);//  MeuAJC.image2BlackWhiteTest(local) ;

                g.drawImage(imagem, 0, 0, pracha_camera);

            } else {

                try {
                    Thread.sleep(800);
                } catch (InterruptedException ex) {
                    Logger.getLogger(RedesACJWebcam.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            try {
                if (reconhece||reconheceSimples) {
                    g.setFont(new Font(Font.SERIF, Font.BOLD, 18));
                    g.setColor(((nresult == -1) ? Color.red
                            : getCor(nresult)));

                    g.drawString(resultado.getText(), xI, yI - 20);

                } else {
                    g.setColor(Color.DARK_GRAY);
                }

                g.drawRect(xI, yI, Integer.parseInt(largura.getText()), Integer.parseInt(altura.getText()));

                for (int cont = 0; cont < anR.size(); cont++) {

                    analisaResultImgBufferedImg an = anR.get(cont);

                    g.setColor(an.cor);
                    g.drawString(an.iIndexResult + "_" + an.acertosResult, an.xR, an.yR - 10);
                    g.drawRect(an.xR, an.yR, an.lR, an.aR);

                }

//                 
//                           g.drawImage(rotateImageByDegrees(imagem.getSubimage(xI, yI, Integer.parseInt(largura.getText()), Integer.parseInt(altura.getText())),
//                                         vcont++), 0, 0, rec)  ;
//                                
//                             
//                             if(vcont>360)vcont=0;
//                for (int cont = 0; cont <= 25; cont += 5) {
//                    g.drawRect(xI + cont, yI + cont, Integer.parseInt(largura.getText()), Integer.parseInt(altura.getText()));
//                    g.drawRect(xI + cont, yI, Integer.parseInt(largura.getText()), Integer.parseInt(altura.getText()));
//                    g.drawRect(xI, yI + cont, Integer.parseInt(largura.getText()), Integer.parseInt(altura.getText()));
//                    g.drawRect(xI - cont, yI - cont, Integer.parseInt(largura.getText()), Integer.parseInt(altura.getText()));
//                    g.drawRect(xI - cont, yI, Integer.parseInt(largura.getText()), Integer.parseInt(altura.getText()));
//                    g.drawRect(xI, yI - cont, Integer.parseInt(largura.getText()), Integer.parseInt(altura.getText()));
//
//                }
//        int xqMin = Integer.parseInt(largura.getText()) / (ia.qtdQdRec / ia.bdiv);
//        int yqMin = Integer.parseInt(altura.getText()) / (ia.qtdQdRec / ia.bdiv);
//
//        int xIIC = 0;
//        int yIIC = 0;
//
//        xqMin = xqMin - 1;
//
//        for (int cont = 0; cont < ia.qtdQdRec; cont++) {
//
//         g.drawRect(xIIC+xI, yI+yIIC, xqMin, yqMin);
//            
//
//            xIIC += xqMin;
//
//            if (xIIC + xqMin  > Integer.parseInt(largura.getText()) ) {
//                yIIC += yqMin;
//                xIIC = 0;
//
//                if (yIIC + yqMin >  Integer.parseInt(altura.getText()) ) {
//
//                    yIIC = yIIC - ((yIIC + yqMin) -  Integer.parseInt(altura.getText()));
//
//                }
//            }
//
//        }
//                for (float cont = 0.05f; cont < 0.40f; cont += 0.05f) {
//
//                    if (0.05f == cont) {
//                        geraCorteMetade(g);
//                    }
//                    geraCortes(cont, g);
//                    geraCortesX0(cont, g);
//                }
            } catch (Exception e) {
            }

            pracha_camera.repaint();

        }

    }

    public void geraCorteMetade(BufferedImage g) {

        int divs = 2;

        int xqMin = ((Integer.parseInt(largura.getText())) / (divs)) - 1;
        int yqMin = ((Integer.parseInt(altura.getText())) / (1)) - 1;

        int xII = xI;
        int yII = yI;

        anRTemp.add(new analisaResultImgBufferedImg(g.getSubimage(xII, yII, (Integer.parseInt(largura.getText())), ((Integer.parseInt(altura.getText())) / (1))), xII, yII, xqMin, yqMin, gerarCorAleatoriamente()));

        for (int cont = 0; cont < divs; cont++) {
            //   anRTemp.add(new analisaResultImgBufferedImg(g.getSubimage(xII, yII, xqMin, yqMin), xII, yII, xqMin, yqMin, gerarCorAleatoriamente()));

            xII += xqMin;

            if (xII + xqMin >= 5 + (xI + Integer.parseInt(largura.getText()))) {
                xII = xI;
                yII += yqMin;

            }

        }
    }

    public void geraCortes(float perc, BufferedImage g) {

        int divs = 1;

        int xqMin = ((Integer.parseInt(largura.getText())) - (int) ((Integer.parseInt(largura.getText())) * perc)) - 1;
        int yqMin = ((Integer.parseInt(altura.getText())) / (1)) - 1;

        int xII = xI + (int) ((Integer.parseInt(largura.getText())) * perc);
        int yII = yI;

        for (int cont = 0; cont < divs; cont++) {

            anRTemp.add(new analisaResultImgBufferedImg(g.getSubimage(xII, yII, xqMin, yqMin), xII, yII, xqMin, yqMin, gerarCorAleatoriamente()));

            System.out.println(perc + " -- " + xqMin + " -- " + yqMin);
            xII += xqMin;

            if (xII + xqMin >= (xI + Integer.parseInt(largura.getText()))) {
                xII = xI;
                yII += yqMin;

            }

        }

    }

    public void geraCortesX0(float perc, BufferedImage g) {

        int divs = 1;

        int xqMin = (Integer.parseInt(largura.getText())) - (int) ((Integer.parseInt(largura.getText())) * perc);
        int yqMin = (Integer.parseInt(altura.getText())) / (1);

        int xII = xI;
        int yII = yI;

        for (int cont = 0; cont < divs; cont++) {

            anRTemp.add(new analisaResultImgBufferedImg(g.getSubimage(xII, yII, xqMin, yqMin), xII, yII, xqMin, yqMin, gerarCorAleatoriamente()));

            xII += xqMin;

            if (xII + xqMin >= (xI + Integer.parseInt(largura.getText()))) {
                xII = xI;
                yII += yqMin;

            }

        }

    }

    public BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    public void preencheRedes() {
        comb_redes.removeAllItems();

        for (String s : ia.itens) {

            comb_redes.addItem(s);
        }
    }

    public Color getCor(int index) {

        if (index >= cores.size()) {

            cores.add(gerarCorAleatoriamente());
        }
        return cores.get(index);
    }

    private Color gerarCorAleatoriamente() {
        Random randColor = new Random();
        int r = randColor.nextInt(256);
        int g = randColor.nextInt(256);
        int b = randColor.nextInt(256);
        return new Color(r, g, b);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pracha_camera = new prancha_camera_impl();
        painel = new javax.swing.JPanel();
        jButton4 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        largura = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        altura = new javax.swing.JTextField();
        memoria = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        comb_redes = new javax.swing.JComboBox<>();
        bt_reconhecer = new javax.swing.JButton();
        resultado = new javax.swing.JTextField();
        numero_coletaid1 = new javax.swing.JLabel();
        bt_coletarid1 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        neuronios = new javax.swing.JTextField();
        tamFileira = new javax.swing.JTextField();
        jButton5 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        bt_reconhecer1 = new javax.swing.JButton();
        treino_progress = new javax.swing.JProgressBar();
        bt_treinar = new javax.swing.JButton();
        bt_treinar1 = new javax.swing.JButton();
        bt_treinar2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        pracha_camera.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pracha_camera.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                pracha_cameraMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout pracha_cameraLayout = new javax.swing.GroupLayout(pracha_camera);
        pracha_camera.setLayout(pracha_cameraLayout);
        pracha_cameraLayout.setHorizontalGroup(
            pracha_cameraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pracha_cameraLayout.setVerticalGroup(
            pracha_cameraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 536, Short.MAX_VALUE)
        );

        painel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        jButton4.setText("Limpar imagens armazenadas");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        largura.setText("100");

        jLabel1.setText("Largura");

        jLabel2.setText("Altura");

        altura.setText("100");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel1)
                .addGap(4, 4, 4)
                .addComponent(largura, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(altura, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(26, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(largura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(altura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton1.setText("Add RedeN");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Del RedeN");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        comb_redes.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comb_redes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comb_redesActionPerformed(evt);
            }
        });

        bt_reconhecer.setText("Reconhecer");
        bt_reconhecer.setEnabled(false);
        bt_reconhecer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt_reconhecerActionPerformed(evt);
            }
        });

        numero_coletaid1.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        numero_coletaid1.setText("0");
        numero_coletaid1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        bt_coletarid1.setText("Coletar Treino");
        bt_coletarid1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt_coletarid1ActionPerformed(evt);
            }
        });

        jLabel3.setText("Neuronios");

        neuronios.setText("2");

        tamFileira.setText("3");
        tamFileira.setMinimumSize(new java.awt.Dimension(100, 22));

        jButton5.setText("Reiniciar Neuronios");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton3.setText("Salvar Neuronios");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton7.setText("Usar Camera");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setText("Usar Imagem");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton9.setText("Carregar Rede Salva");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        bt_reconhecer1.setText("Reconhecer Simples");
        bt_reconhecer1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt_reconhecer1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout painelLayout = new javax.swing.GroupLayout(painel);
        painel.setLayout(painelLayout);
        painelLayout.setHorizontalGroup(
            painelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(painelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(painelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, painelLayout.createSequentialGroup()
                        .addGroup(painelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(painelLayout.createSequentialGroup()
                                .addGroup(painelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(painelLayout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(tamFileira, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(neuronios, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton9))
                            .addComponent(jButton4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(painelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(painelLayout.createSequentialGroup()
                                .addComponent(jButton7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton8))
                            .addGroup(painelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, painelLayout.createSequentialGroup()
                                    .addComponent(jButton1)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButton2)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(numero_coletaid1, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, painelLayout.createSequentialGroup()
                                    .addComponent(comb_redes, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(bt_coletarid1)))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, painelLayout.createSequentialGroup()
                        .addComponent(memoria)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bt_reconhecer)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(painelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(bt_reconhecer1)
                            .addComponent(resultado, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        painelLayout.setVerticalGroup(
            painelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(painelLayout.createSequentialGroup()
                .addGroup(painelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(painelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(painelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(painelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(tamFileira, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(neuronios, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(painelLayout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(painelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(numero_coletaid1)
                            .addComponent(jButton2)
                            .addComponent(jButton1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(painelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(comb_redes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(bt_coletarid1))))
                .addGroup(painelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(painelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(painelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton4)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, painelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jButton7)
                                .addComponent(jButton8)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(memoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(painelLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(bt_reconhecer1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(painelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton5)
                            .addComponent(jButton3)
                            .addComponent(bt_reconhecer)
                            .addComponent(resultado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(0, 10, Short.MAX_VALUE))
        );

        bt_treinar.setText("Treinar Imagens Armazenadas");
        bt_treinar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt_treinarActionPerformed(evt);
            }
        });

        bt_treinar1.setText("Treinar Tempo Real Camera");
        bt_treinar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt_treinar1ActionPerformed(evt);
            }
        });

        bt_treinar2.setText("Limpar e Treinar Tempo Real");
        bt_treinar2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt_treinar2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pracha_camera, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(painel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(bt_treinar, javax.swing.GroupLayout.DEFAULT_SIZE, 390, Short.MAX_VALUE)
                    .addComponent(treino_progress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(bt_treinar2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(bt_treinar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pracha_camera, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(painel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(bt_treinar1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(bt_treinar2)
                        .addGap(11, 11, 11)
                        .addComponent(bt_treinar, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(treino_progress, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void pracha_cameraMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pracha_cameraMouseReleased

        if (evt.getButton() == MouseEvent.BUTTON3) {

            largura.setText((evt.getX() - xI) + "");
            altura.setText((evt.getY() - yI) + "");

        } else if (evt.getX() + Integer.parseInt(largura.getText()) <= imagem.getWidth()
                && evt.getY() + Integer.parseInt(altura.getText()) <= imagem.getHeight()) {

            xI = evt.getX();
            yI = evt.getY();

        }


    }//GEN-LAST:event_pracha_cameraMouseReleased

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed

        if (!bt_treinar.isEnabled()) {
            return;
        }

        reconhece = false;
        if (reconhece) {

            bt_reconhecer.setText("Reconhecendo!");
            bt_reconhecer.setBackground(Color.GREEN);
        } else {
            bt_reconhecer.setText("Reconhecer!");
            bt_reconhecer.setBackground(Color.YELLOW);

        }

        try {
            for (int nredes = 0; nredes < comb_redes.getItemCount(); nredes++) {
                for (File file : new File("IMG" + comb_redes.getItemAt(nredes) + "/").listFiles()) {
                    if (!file.isDirectory()) {
                        file.delete();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        atualizaColetas();


    }//GEN-LAST:event_jButton4ActionPerformed

    private void bt_coletarid1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt_coletarid1ActionPerformed

        coletaid1 = !coletaid1;

        if (coletaid1) {
            bt_coletarid1.setText("Parar...Coleta");
        } else {

            bt_coletarid1.setText("Coletar Treino");
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                comb_redes.setEnabled(false);

                new File("IMG" + comb_redes.getSelectedItem()).mkdir();

                while (coletaid1) {

                    try {
                        Thread.sleep(300);
                        ImageIO.write(imagem.getSubimage(xI, yI,
                                Integer.parseInt(largura.getText()), Integer.parseInt(altura.getText())),
                                "png", new File("IMG" + comb_redes.getSelectedItem() + "/_" + comb_redes.getSelectedItem() + "_" + System.currentTimeMillis() + ".png"));
                    } catch (IOException ex) {
                        Logger.getLogger(RedesACJWebcam.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(RedesACJWebcam.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    atualizaColetas();
                }

                comb_redes.setEnabled(true);

            }
        }).start();

    }//GEN-LAST:event_bt_coletarid1ActionPerformed

    private void bt_treinarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt_treinarActionPerformed

        IA ial = new IA();
        ial.itens = ia.itens;
        ia = ial;

        reconhece = false;
        if (reconhece) {

            bt_reconhecer.setText("Reconhecendo!");
            bt_reconhecer.setBackground(Color.GREEN);
        } else {
            bt_reconhecer.setText("Reconhecer!");
            bt_reconhecer.setBackground(Color.YELLOW);

        }
        bt_treinar.setEnabled(false);

        new Thread(new Runnable() {
            @Override
            public void run() {

                bt_treinar.setText("Treinando Rede...");

                treino_progress.setValue(0);

                int size = -1;

                for (int c_redes = 0; c_redes < comb_redes.getItemCount(); c_redes++) {

                    for (int cont = 0; cont < new File("IMG" + comb_redes.getItemAt(c_redes) + "/").listFiles().length; cont++) {
                        File file = new File("IMG" + comb_redes.getItemAt(c_redes) + "/").listFiles()[cont];

                        //22500
                        System.out.println(c_redes + " ValorTreino " + file.getName() + " " + SIM);

                        try {
                            ia.setValorTreinoByte(c_redes, ImageIO.read(file), SIM);
                        } catch (IOException ex) {
                            Logger.getLogger(RedesACJWebcam.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                    for (int nredes = 0; nredes < comb_redes.getItemCount(); nredes++) {

                        if (c_redes != nredes) {
                            for (int cont = 0; cont < new File("IMG" + comb_redes.getItemAt(nredes) + "/").listFiles().length; cont++) {
                                File file = new File("IMG" + comb_redes.getItemAt(nredes) + "/").listFiles()[cont];
                                if (size == -1) {
                                    size = OpenAJC.lenghtImagem(file) * 8;
                                }
                                System.out.println(c_redes + " ValorTreino " + file.getName() + " " + NAO);

                                try {
                                    ia.setValorTreinoByte(c_redes, ImageIO.read(file), NAO);
                                } catch (IOException ex) {
                                    Logger.getLogger(RedesACJWebcam.class.getName()).log(Level.SEVERE, null, ex);
                                }

                            }
                        }
                    }
                }

                ia.treinar(treino_progress, Integer.valueOf(tamFileira.getText()), Integer.valueOf(neuronios.getText()), bt_treinar);
                try {
                    System.out.println(ia.getResult(comb_redes, ImageIO.read(new File("IMG" + comb_redes.getItemAt(0) + "/").listFiles()[0])));
                    System.out.println(ia.getResult(comb_redes, ImageIO.read(new File("IMG" + comb_redes.getItemAt(1) + "/").listFiles()[0])));
                } catch (IOException ex) {
                    Logger.getLogger(RedesACJWebcam.class.getName()).log(Level.SEVERE, null, ex);
                }

                salvarREDE();
                bt_treinar.setText("Treino Completo");
                bt_treinar.setEnabled(true);

            }
        }).start();


    }//GEN-LAST:event_bt_treinarActionPerformed

    private void bt_reconhecerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt_reconhecerActionPerformed

        reconheceMulti = !reconheceMulti;

        new Thread(new Runnable() {
            @Override
            public void run() {

                ArrayList<analisaResultImgBufferedImg> anRTempLocal = new ArrayList<>();

                while (reconheceMulti) {

                    anRTemp.clear();

                    BufferedImage bi = imagem;

                    // rec.setImagem_recorte(ImageIO.read(temp));
                    for (float cont = 0.10f; cont <= 0.50f; cont += 0.20f) {

                        if (0.10f == cont) {
                            geraCorteMetade(bi);
                        }
                        geraCortes(cont, bi);
                        geraCortesX0(cont, bi);;

                    }

                    for (int indexFind = 0; indexFind < comb_redes.getItemCount() ; indexFind++) {
                        analisaResultImgBufferedImg resp = null;
                        for (analisaResultImgBufferedImg ar : anRTemp) {

                            BufferedImage bf = bi.getSubimage(ar.xR, ar.yR, ar.lR, ar.aR);

                            int acertos = ia.getResultCont(indexFind, bf);

                            if (resp == null || resp.acertosResult <= acertos || resp.acertosResult <= acertos && resp.area() >= ar.area()) {
                                resp = ar.clone();
                                resp.iIndexResult = indexFind;
                                resp.acertosResult = acertos;
                                resp.area = ar.area();
                            }
                        }

                        anRTempLocal.add(resp);

                    }

                    anR = anRTempLocal;

                    for (int t = 0; t < anR.size(); t++) {

                        System.out.println("" + anR.get(t).toString());
                    }

                    try {
                        Thread.sleep(800);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(RedesACJWebcam.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    break;

                }

            }
        }).start();

        if (reconheceMulti) {

            bt_reconhecer.setText("Reconhecendo!");
            bt_reconhecer.setBackground(Color.GREEN);
        } else {
            bt_reconhecer.setText("Reconhecer!");
            bt_reconhecer.setBackground(Color.YELLOW);
            nresult = -1;
            anR.clear();
        }


    }//GEN-LAST:event_bt_reconhecerActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

        ia.itens.remove(comb_redes.getSelectedItem());

        preencheRedes();
        //  salvarREDE();

        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        String n1 = JOptionPane.showInputDialog("Digite o nome da rede");

        ia.itens.add(n1);
        preencheRedes();

        // salvarREDE();
// TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void comb_redesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comb_redesActionPerformed
        atualizaColetas_Item_Rede();

        // TODO add your handling code here:
    }//GEN-LAST:event_comb_redesActionPerformed

    private void bt_treinar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt_treinar1ActionPerformed
        // TODO add your handling code here:

        treinaTempoReal = !treinaTempoReal;

        new Thread(new Runnable() {
            @Override
            public void run() {

                bt_treinar1.setText("Treinando ...");
                while (treinaTempoReal) {

                    BufferedImage bimg = imagem.getSubimage(xI, yI,
                            Integer.parseInt(largura.getText()), Integer.parseInt(altura.getText()));

                    rec.setImagem_recorte(bimg);

                    int result[] = ia.getResultCont(comb_redes, bimg);

                    System.out.println(" result " + result[0] + " " + comb_redes.getSelectedIndex());

                    if (comb_redes.getSelectedIndex() == result[0] && result[1] >= ia.qtdQdRec - 1) {

                        resultado.setText(comb_redes.getItemAt(result[0]));
                        nresult = ia.nresult;

                    } else {

                        resultado.setText("---------------");
                        nresult = -1;

                        for (int cont = 0; cont < comb_redes.getItemCount(); cont++) {

                            bimg = imagem.getSubimage(xI, yI, Integer.parseInt(largura.getText()), Integer.parseInt(altura.getText()));
                            ia.setValorTreinoByte(cont, bimg, cont == comb_redes.getSelectedIndex() ? SIM : NAO);

                            for (int vcont = 0; vcont <= 360; vcont += 20) {

                                bimg = rotateImageByDegrees(imagem.getSubimage(xI, yI, Integer.parseInt(largura.getText()), Integer.parseInt(altura.getText())),
                                        vcont);
                                ia.setValorTreinoByte(cont, bimg, cont == comb_redes.getSelectedIndex() ? SIM : NAO);
                                //                                bimg = imagem.getSubimage(xI + cont, yI, Integer.parseInt(largura.getText()), Integer.parseInt(altura.getText()));
                                //                                ia.setValorTreinoByte(cont, bimg, cont == comb_redes.getSelectedIndex() ? SIM : NAO);
                                //
                                //                                bimg = imagem.getSubimage(xI, yI + cont, Integer.parseInt(largura.getText()), Integer.parseInt(altura.getText()));
                                //                                ia.setValorTreinoByte(cont, bimg, cont == comb_redes.getSelectedIndex() ? SIM : NAO);
                                //
                                //                                bimg = imagem.getSubimage(xI - cont, yI - cont, Integer.parseInt(largura.getText()), Integer.parseInt(altura.getText()));
                                //                                ia.setValorTreinoByte(cont, bimg, cont == comb_redes.getSelectedIndex() ? SIM : NAO);
                                //
                                //                                bimg = imagem.getSubimage(xI - cont, yI, Integer.parseInt(largura.getText()), Integer.parseInt(altura.getText()));
                                //                                ia.setValorTreinoByte(cont, bimg, cont == comb_redes.getSelectedIndex() ? SIM : NAO);
                                //
                                //                                bimg = imagem.getSubimage(xI, yI - cont, Integer.parseInt(largura.getText()), Integer.parseInt(altura.getText()));
                                //                                ia.setValorTreinoByte(cont, bimg, cont == comb_redes.getSelectedIndex() ? SIM : NAO);
                            }

                        }

                        ia.treinar(treino_progress, Integer.valueOf(tamFileira.getText()), Integer.valueOf(neuronios.getText()), bt_treinar);

                    }

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(RedesACJWebcam.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }

                bt_treinar1.setEnabled(true);
                bt_treinar1.setText("Parou Treinar Tempo Real");
                bt_treinar1.setBackground(Color.YELLOW);

            }
        }).start();

        if (treinaTempoReal) {

            // bt_treinar1.setEnabled(false);
            bt_treinar1.setBackground(Color.GREEN);
        } else {

            nresult = -1;
        }
    }//GEN-LAST:event_bt_treinar1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed

        salvarREDE();
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        IA ial = new IA();
        ial.itens = ia.itens;
        ia = ial;
    }//GEN-LAST:event_jButton5ActionPerformed

    private void bt_treinar2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt_treinar2ActionPerformed

        // TODO add your handling code here:
        // TODO add your handling code here:
        treinaTempoReal = !treinaTempoReal;

        new Thread(new Runnable() {
            @Override
            public void run() {

                bt_treinar2.setText("Treinando ...");
                while (treinaTempoReal) {
                    try {
                        ImageIO.write(imagem.getSubimage(xI, yI,
                                Integer.parseInt(largura.getText()), Integer.parseInt(altura.getText())),
                                "png", temp);
                        rec.setImagem_recorte(ImageIO.read(temp));
                    } catch (IOException ex) {
                        Logger.getLogger(RedesACJWebcam.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    if (sizeReal < 10) {

                        sizeReal = OpenAJC.lenghtImagem(temp) * 8;
                    }

                    int result = ia.getResultCont(comb_redes, imagem.getSubimage(xI, yI,
                            Integer.parseInt(largura.getText()), Integer.parseInt(altura.getText())))[0];

                    System.out.println(" result " + result + " " + comb_redes.getSelectedIndex());

                    if (comb_redes.getSelectedIndex() == result) {

                        resultado.setText(comb_redes.getItemAt(result));
                        nresult = ia.nresult;

                    } else {

                        resultado.setText("---------------");
                        nresult = -1;
                        for (int cont = 0; cont < comb_redes.getItemCount(); cont++) {

                            ia.setValorTreinoByte(cont, imagem.getSubimage(xI, yI,
                                    Integer.parseInt(largura.getText()), Integer.parseInt(altura.getText())), cont == comb_redes.getSelectedIndex() ? SIM : NAO);

                        }

                        ia.treinarNew(treino_progress, Integer.valueOf(tamFileira.getText()), Integer.valueOf(neuronios.getText()), bt_treinar2);

                    }

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(RedesACJWebcam.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }

                bt_treinar2.setEnabled(true);
                bt_treinar2.setText("Parou Treinar Tempo Real");
                bt_treinar2.setBackground(Color.YELLOW);

            }
        }).start();

        if (treinaTempoReal) {

            // bt_treinar1.setEnabled(false);
            bt_treinar2.setBackground(Color.GREEN);
        } else {

            nresult = -1;
        }

    }//GEN-LAST:event_bt_treinar2ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        JFileChooser fc = new JFileChooser();
        int result = fc.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {

            try {
                ImageIO.write(ImageIO.read(fc.getSelectedFile()),
                        "png", new File("images.png"));
            } catch (IOException ex) {
                Logger.getLogger(RedesACJWebcam.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        try {
            Image fundoL = ImageIO.read(new File("images.png"));
            fundo = toBufferedImage(fundoL.getScaledInstance(pracha_camera.getWidth(), pracha_camera.getHeight(), Image.SCALE_DEFAULT));

            usarCamera = false;
        } catch (IOException ex) {
            Logger.getLogger(RedesACJWebcam.class.getName()).log(Level.SEVERE, null, ex);
        }

        // TODO add your handling code here:
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:

        usarCamera = true;

    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        try {
            treino_progress.setValue(20);
            ia = (IA) IO.ler("minharede");
        } catch (Exception e) {

            e.printStackTrace();
        }

        if (ia == null) {
            ia = new IA();
        }

        preencheRedes();
        treino_progress.setValue(100);

// TODO add your handling code here:
    }//GEN-LAST:event_jButton9ActionPerformed

    private void bt_reconhecer1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt_reconhecer1ActionPerformed
        reconheceSimples = !reconheceSimples;

        new Thread(new Runnable() {
            @Override
            public void run() {
                do {
                    if (reconheceSimples) {
                        BufferedImage bimg = imagem.getSubimage(xI, yI,
                                Integer.parseInt(largura.getText()), Integer.parseInt(altura.getText()));

                        rec.setImagem_recorte(bimg);

                        int result[] = ia.getResultCont(comb_redes, bimg);

                        System.out.println(" result " + result[0] + " " + comb_redes.getSelectedIndex());

                        resultado.setText(comb_redes.getItemAt(result[0]));
                        nresult = result[0];
                    }

                    try {
                        Thread.sleep(800);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(RedesACJWebcam.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } while (reconheceSimples);

            }
        }).start();

        if (reconheceSimples) {

            bt_reconhecer1.setText("Reconhecendo!");
            bt_reconhecer1.setBackground(Color.GREEN);
        } else {
            bt_reconhecer1.setText("Reconhecer!");
            bt_reconhecer1.setBackground(Color.YELLOW);
            nresult = -1;
        }

        // TODO add your handling code here:
    }//GEN-LAST:event_bt_reconhecer1ActionPerformed

    public void atualizaColetas() {

        try {
            numero_coletaid1.setText(new File("IMG" + comb_redes.getSelectedItem()).listFiles().length + "");
        } catch (Exception e) {
            // e.printStackTrace();
            numero_coletaid1.setText("0");
        }
    }

    public void atualizaColetas_Item_Rede() {

        atualizaColetas();
    }

    public void iniciaNr(int nrede) {

    }

    public void salvarREDE() {

        try {
            //ia.limpaDadosTreino();
            ia.xP = Integer.valueOf(largura.getText());
            ia.yP = Integer.valueOf(altura.getText());

            IO.inserir("minharede", ia);
        } catch (IOException ex) {
            Logger.getLogger(RedesACJWebcam.class.getName()).log(Level.SEVERE, null, ex);
        }

        JOptionPane.showMessageDialog(null, "Salvo");
    }

    public BufferedImage rotateImageByDegrees(BufferedImage img, double angle) {

        double rads = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
        int w = img.getWidth();
        int h = img.getHeight();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotated.createGraphics();
        AffineTransform at = new AffineTransform();
        at.translate((newWidth - w) / 2, (newHeight - h) / 2);

        int x = w / 2;
        int y = h / 2;

        at.rotate(rads, x, y);
        g2d.setTransform(at);
        g2d.drawImage(img, 0, 0, this);
        g2d.dispose();

        return rotated;
    }

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(RedesACJWebcam.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RedesACJWebcam.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RedesACJWebcam.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RedesACJWebcam.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RedesACJWebcam().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField altura;
    private javax.swing.JButton bt_coletarid1;
    private javax.swing.JButton bt_reconhecer;
    private javax.swing.JButton bt_reconhecer1;
    private javax.swing.JButton bt_treinar;
    private javax.swing.JButton bt_treinar1;
    private javax.swing.JButton bt_treinar2;
    private javax.swing.JComboBox<String> comb_redes;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField largura;
    private javax.swing.JTextField memoria;
    private javax.swing.JTextField neuronios;
    private javax.swing.JLabel numero_coletaid1;
    private javax.swing.JPanel painel;
    private javax.swing.JPanel pracha_camera;
    private javax.swing.JTextField resultado;
    private javax.swing.JTextField tamFileira;
    private javax.swing.JProgressBar treino_progress;
    // End of variables declaration//GEN-END:variables
}
