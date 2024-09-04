/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IAAprender;

import Biblis.MemoryUtils;
import OpIO.Dados;
import OpIO.IO;
import TrabalhaBits.NumeroBits;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
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
import meupneuronio.NeuronioAJC;

/**
 *
 * @author junior
 */
class IA implements Serializable {

    Thread treino = null;

    int xP = 160;
    int yP = 120;

    int ini = 0;

    int sizeTam = 3, size = 3;

    public int nresult = -1;

    ArrayList<String> itens = new ArrayList<>();
    ArrayList<NeuronioAJC> nr = new ArrayList<>();

    public void zera() {
        nr = new ArrayList<>();
    }

    public NeuronioAJC getNR(int cont, int size) {

        if (cont >= nr.size()) {

            nr.add(new NeuronioAJC(size));

        }

        return nr.get(cont);
    }

    public void limpaDadosTreino() {

        for (NeuronioAJC n : nr) {

            n.limpaValoresTreino();

        }
    }

    public void treinar(JProgressBar progresso, int sizeTam, int size, JButton btreino) {

        this.sizeTam = sizeTam;
        this.size = size;
        int valueInc = 100 / nr.size();
        progresso.setValue(0);
        ini = 0;

        for (NeuronioAJC n : nr) {

            System.gc();
            n.treinarRedePSFileiraValidaExists(sizeTam, size);

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

        for (NeuronioAJC n : nr) {

            n.treinarRedePSFileira(Integer.valueOf(sizeTam), size);

            progresso.setValue((ini += valueInc));

        }

        progresso.setValue(100);
        btreino.setEnabled(true);

    }

    public String getResult(JComboBox combo, File img) {

        ArrayList<NumeroBits> ar = new ArrayList<>();
        ar.add(nr.get(0).getValorTreinoNovoFileiras(img));

        for (int cont = 0; cont < nr.size(); cont++) {

            //   System.out.println("Results " + nr.get(cont).saidaPSFileira(ar).toString());
            if (nr.get(cont).saidaPSFileira(ar).get(0) < 0) {

                nresult = cont;
                return combo.getItemAt(cont) + "";
            }
        }
        return null;
    }

    public int getResultCont(JComboBox combo, File img) {

        if (nr == null || nr.size() == 0) {
            return -1;
        }

        ArrayList<NumeroBits> ar = new ArrayList<>();
        ar.add(nr.get(0).getValorTreinoNovoFileiras(img));

        for (int cont = 0; cont < nr.size(); cont++) {

            //System.out.println("Results " + nr.get(cont).saidaPSFileira(ar).toString());
            if (nr.get(cont).saidaPSFileira(ar).get(0) < 0) {

                nresult = cont;
                return cont;
            }
        }
        return -1;
    }
}

public class RedesACJWebcam extends javax.swing.JFrame {

    int limitcor = 255;

    Image fundoL = null;
    BufferedImage fundo = null;

    static int SIM = -2;
    static int NAO = 2;
    IA ia = new IA();
    int sizeReal = -1;

    int xI = 0, yI = 0;
    BufferedImage imagem = null;
    Webcam webcam = null;

    String result = "";
    int nresult = 1000;
    boolean coletaid0 = false, coletaid1 = false;
    boolean reconhece = false;
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

    class prancha_camera_impl extends JPanel {

        Color[] colors = {Color.YELLOW, Color.GREEN, Color.BLUE, Color.RED, Color.WHITE};
        BufferedImage local = null;
        boolean setFont = true;
        private static final double CANNY_THRESHOLD_RATIO = .2; //Suggested range .2 - .4
        private static final int CANNY_STD_DEV = 1;

        public prancha_camera_impl() {

        }

        @Override
        public void paintComponent(Graphics g) {

            if (getWidth() > 5 && fundo == null) {

                try {
                    fundoL = ImageIO.read(new File("images.png"));
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
                if (reconhece) {
                    g.setFont(new Font(Font.SERIF, Font.BOLD, 18));
                    g.setColor(((colors.length <= nresult || nresult == -1) ? colors[new Random().nextInt(colors.length)]
                            : colors[nresult]));

                    g.drawString(resultado.getText(), xI, yI - 20);

                } else {
                    g.setColor(Color.YELLOW);
                }
                g.drawRect(xI, yI, Integer.parseInt(largura.getText()), Integer.parseInt(altura.getText()));
            } catch (Exception e) {
            }

            pracha_camera.repaint();

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
        treino_progress = new javax.swing.JProgressBar();
        bt_treinar = new javax.swing.JButton();
        bt_treinar1 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();

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

        largura.setText("160");

        jLabel1.setText("Largura");

        jLabel2.setText("Altura");

        altura.setText("120");

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

        neuronios.setText("3");

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

        javax.swing.GroupLayout painelLayout = new javax.swing.GroupLayout(painel);
        painel.setLayout(painelLayout);
        painelLayout.setHorizontalGroup(
            painelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(painelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(painelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(painelLayout.createSequentialGroup()
                        .addComponent(memoria)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bt_reconhecer)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(resultado, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                                    .addComponent(bt_coletarid1))))))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(painelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton4)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, painelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton7)
                        .addComponent(jButton8)))
                .addGroup(painelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(painelLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(painelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton5)
                            .addComponent(jButton3)
                            .addComponent(bt_reconhecer)
                            .addComponent(resultado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(painelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(memoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
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

        jButton6.setText("Limpar e Treinar Tempo Real");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
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
                    .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                        .addComponent(jButton6)
                        .addGap(11, 11, 11)
                        .addComponent(bt_treinar, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(treino_progress, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void pracha_cameraMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pracha_cameraMouseReleased
        if (evt.getX() + Integer.parseInt(largura.getText()) <= imagem.getWidth()
                && evt.getY() + Integer.parseInt(altura.getText()) <= imagem.getHeight()) {
            xI = evt.getX();
            yI = evt.getY();

        }

// TODO add your handling code here:
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
                        if (size == -1) {
                            size = NeuronioAJC.lenghtImagem(file) * 8;
                        }
                        //22500
                        System.out.println(c_redes + " ValorTreino " + file.getName() + " " + SIM);

                        ia.getNR(c_redes, size).setValorTreinoNovoBytesBits(file,
                                SIM);
                    }

                    for (int nredes = 0; nredes < comb_redes.getItemCount(); nredes++) {

                        if (c_redes != nredes) {
                            for (int cont = 0; cont < new File("IMG" + comb_redes.getItemAt(nredes) + "/").listFiles().length; cont++) {
                                File file = new File("IMG" + comb_redes.getItemAt(nredes) + "/").listFiles()[cont];
                                if (size == -1) {
                                    size = NeuronioAJC.lenghtImagem(file) * 8;
                                }
                                System.out.println(c_redes + " ValorTreino " + file.getName() + " " + NAO);

                                ia.getNR(c_redes, size).setValorTreinoNovoBytesBits(file,
                                        NAO);
                            }
                        }
                    }
                }

                ia.treinar(treino_progress, Integer.valueOf(tamFileira.getText()), Integer.valueOf(neuronios.getText()), bt_treinar);
                System.out.println(ia.getResult(comb_redes, new File("IMG" + comb_redes.getItemAt(0) + "/").listFiles()[0]));
                System.out.println(ia.getResult(comb_redes, new File("IMG" + comb_redes.getItemAt(1) + "/").listFiles()[0]));
                salvarREDE();
                bt_treinar.setText("Treino Completo");
                bt_treinar.setEnabled(true);

            }
        }).start();


    }//GEN-LAST:event_bt_treinarActionPerformed

    private void bt_reconhecerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt_reconhecerActionPerformed

        reconhece = !reconhece;

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (reconhece) {

                    try {
                        ImageIO.write(imagem.getSubimage(xI, yI,
                                Integer.parseInt(largura.getText()), Integer.parseInt(altura.getText())),
                                "png", temp);
                        rec.setImagem_recorte(ImageIO.read(temp));
                    } catch (IOException ex) {
                        Logger.getLogger(RedesACJWebcam.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    resultado.setText(ia.getResult(comb_redes, temp));
                    nresult = ia.nresult;

                    try {
                        Thread.sleep(800);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(RedesACJWebcam.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }

            }
        }).start();

        if (reconhece) {

            bt_reconhecer.setText("Reconhecendo!");
            bt_reconhecer.setBackground(Color.GREEN);
        } else {
            bt_reconhecer.setText("Reconhecer!");
            bt_reconhecer.setBackground(Color.YELLOW);
            nresult = -1;
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
                    try {
                        ImageIO.write(imagem.getSubimage(xI, yI,
                                Integer.parseInt(largura.getText()), Integer.parseInt(altura.getText())),
                                "png", temp);
                        rec.setImagem_recorte(ImageIO.read(temp));
                    } catch (IOException ex) {
                        Logger.getLogger(RedesACJWebcam.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    if (sizeReal < 10) {

                        sizeReal = NeuronioAJC.lenghtImagem(temp) * 8;
                    }

                    int result = ia.getResultCont(comb_redes, temp);

                    System.out.println(" result " + result + " " + comb_redes.getSelectedIndex());

                    if (comb_redes.getSelectedIndex() == result) {

                        resultado.setText(comb_redes.getItemAt(result));
                        nresult = ia.nresult;

                    } else {

                        resultado.setText("---------------");
                        nresult = -1;
                        for (int cont = 0; cont < comb_redes.getItemCount(); cont++) {

                            ia.getNR(cont, sizeReal).setValorTreinoNovoBytesBits(temp,
                                    cont == comb_redes.getSelectedIndex() ? SIM : NAO);

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

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:

        // TODO add your handling code here:
        treinaTempoReal = !treinaTempoReal;

        new Thread(new Runnable() {
            @Override
            public void run() {

                bt_treinar1.setText("Treinando ...");
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

                        sizeReal = NeuronioAJC.lenghtImagem(temp) * 8;
                    }

                    int result = ia.getResultCont(comb_redes, temp);

                    System.out.println(" result " + result + " " + comb_redes.getSelectedIndex());

                    if (comb_redes.getSelectedIndex() == result) {

                        resultado.setText(comb_redes.getItemAt(result));
                        nresult = ia.nresult;

                    } else {

                        resultado.setText("---------------");
                        nresult = -1;
                        for (int cont = 0; cont < comb_redes.getItemCount(); cont++) {

                            ia.getNR(cont, sizeReal).setValorTreinoNovoBytesBits(temp,
                                    cont == comb_redes.getSelectedIndex() ? SIM : NAO);

                        }

                        ia.treinarNew(treino_progress, Integer.valueOf(tamFileira.getText()), Integer.valueOf(neuronios.getText()), bt_treinar);

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

    }//GEN-LAST:event_jButton6ActionPerformed

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
            ia.limpaDadosTreino();
            ia.xP = Integer.valueOf(largura.getText());
            ia.yP = Integer.valueOf(altura.getText());

            IO.inserir("minharede", ia);
        } catch (IOException ex) {
            Logger.getLogger(RedesACJWebcam.class.getName()).log(Level.SEVERE, null, ex);
        }

        JOptionPane.showMessageDialog(null, "Salvo");
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
    private javax.swing.JButton bt_treinar;
    private javax.swing.JButton bt_treinar1;
    private javax.swing.JComboBox<String> comb_redes;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
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
