package meupneuronio;

import TrabalhaBits.NumeroBits;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public class NeuronioAJC
        implements Serializable {

    private  ArrayList<NumeroBits> entradasTreinoBits = new ArrayList<>();

    class valorEsperado implements Serializable {

        ArrayList<Double> valorEsperado = new ArrayList<>();
    }

    ArrayList<valorEsperado> valorEsperado = new ArrayList<>();

    private String ultimaRodada = "";

    private int nentradas = 0;

    public int nBitsG;
    
    
    public void limpaValoresTreino(){
        entradasTreinoBits.clear();
    }

    public void setValorTreino(int valor, double... valoreEsperado) {

        this.entradasTreinoBits.add(new NumeroBits(this.nBitsG, 2, valor, valoreEsperado));

        for (int cont = 0; cont < entradasTreinoBits.size() - 1; cont++) {

            if (validaValores(entradasTreinoBits.get(cont),
                    entradasTreinoBits.get(entradasTreinoBits.size() - 1))) {
                JOptionPane.showMessageDialog(null, "Dados iguais ambiguidade nas imagens!");
                System.exit(0);
            }

        }

    }

    public void printaValoresPesos() {

        for (NeuronioPC a : camada1) {
            System.out.println("N1 " + a.pesos.size() + "");
            for (MutableDouble d : a.pesos) {
                System.out.print(" " + d.getValue());
            }
        }
        System.out.println("");

        for (NeuronioPC a : camada2) {
            System.out.println("N2 " + a.pesos.size() + "");
            for (MutableDouble d : a.pesos) {
                System.out.print(" " + d.getValue());
            }
        }
        System.out.println("");

        for (NeuronioPC a : camada3) {
            System.out.println("N3 " + a.pesos.size() + "");
            for (MutableDouble d : a.pesos) {
                System.out.print(" " + d.getValue());
            }
        }
        System.out.println("");

        for (NeuronioPC a : camada4) {
            System.out.println("N4 " + a.pesos.size() + "");
            for (MutableDouble d : a.pesos) {
                System.out.print(" " + d.getValue());
            }
        }
        System.out.println("");

    }

    public void printaValores() {
        for (int cont = 0; cont < entradasTreinoBits.size(); cont++) {
            System.out.println("\n" + entradasTreinoBits.get(cont).vetbits.length + "\n");
            for (int d = entradasTreinoBits.get(cont).vetbits.length - 1; d >= 0; d--) {

                System.out.print("" + (int) entradasTreinoBits.get(cont).vetbits[d] + "");
            }

        }

    }

    public NumeroBits getValorTeste(int valor) {
        return (new NumeroBits(this.nBitsG, 2, valor, 0));

    }

    public NumeroBits getValorTesteCEsperado(int valor, double... valorEsperado) {
        return (new NumeroBits(this.nBitsG, 2, valor, valorEsperado));

    }

    public static int lenghtImagem(BufferedImage img) {

        System.out.println("Lenght " + (((java.awt.image.DataBufferInt) img.getRaster().getDataBuffer()).getData()).length);
        return (((java.awt.image.DataBufferInt) img.getRaster().getDataBuffer()).getData()).length;

    }

    public static int lenghtImagem(File valor) {
        try {
            return (((DataBufferByte) (ImageIO.read(valor)).getRaster().getDataBuffer()).getData()).length;
        } catch (IOException ex) {
            Logger.getLogger(NeuronioAJC.class.getName()).log(Level.SEVERE, (String) null, ex);

            return -1;
        }
    }

    public void setValorTreino(File valor, double... valoreEsperado) {
        try {
            byte[] dados = ((DataBufferByte) MeuAJC.image2BlackWhiteTest(ImageIO.read(valor)).getRaster().getDataBuffer()).getData();

            NumeroBits nm = new NumeroBits(dados.length, 1, (byte[]) dados.clone());
            nm.setFileName(valor.getName());

            this.entradasTreinoBits.add(nm);

            for (int cont = 0; cont < valoreEsperado.length; cont++) {
                if (this.valorEsperado.size() - 1 < cont) {
                    this.valorEsperado.add(new valorEsperado());
                }
                ((valorEsperado) this.valorEsperado.get(cont)).valorEsperado.add(Double.valueOf(valoreEsperado[cont]));
            }

            for (int cont = 0; cont < entradasTreinoBits.size() - 1; cont++) {

                if (validaValores(entradasTreinoBits.get(cont),
                        entradasTreinoBits.get(entradasTreinoBits.size() - 1))) {
                    JOptionPane.showMessageDialog(null, "Dados iguais ambiguidade nas imagens!");
                    System.exit(0);
                }

            }
        } catch (IOException ex) {
            Logger.getLogger(NeuronioAJC.class.getName()).log(Level.SEVERE, (String) null, ex);
        }
    }

    public void setValorTreinoNovo(BufferedImage img, double... valoreEsperado) {

        int[] dados = (((java.awt.image.DataBufferInt) img.getRaster().getDataBuffer()).getData());

        this.entradasTreinoBits.add(new NumeroBits(dados.length, 1, (int[]) dados.clone(), valoreEsperado));

        for (int cont = 0; cont < entradasTreinoBits.size() - 1; cont++) {

            if (validaValores(entradasTreinoBits.get(cont),
                    entradasTreinoBits.get(entradasTreinoBits.size() - 1))) {
                JOptionPane.showMessageDialog(null, "Dados iguais ambiguidade nas imagens!");
                System.exit(0);
            }

        }
    }

    public void setValorTreinoNovo(File valor, double... valoreEsperado) {
        try {

            byte[] dados = ((DataBufferByte) (ImageIO.read(valor)).getRaster().getDataBuffer()).getData();

            this.entradasTreinoBits.add(new NumeroBits(dados.length, 1, (byte[]) dados.clone(), valoreEsperado));

            for (int cont = 0; cont < entradasTreinoBits.size() - 1; cont++) {

                if (validaValores(entradasTreinoBits.get(cont),
                        entradasTreinoBits.get(entradasTreinoBits.size() - 1))) {
                    JOptionPane.showMessageDialog(null, "Dados iguais ambiguidade nas imagens!");
                    System.exit(0);
                }

            }

        } catch (IOException ex) {
            Logger.getLogger(NeuronioAJC.class.getName()).log(Level.SEVERE, (String) null, ex);
        }
    }

    public void setValorTreinoNovoBytesBits(File valor, double... valoreEsperado) {
        try {

            byte[] dados = ((DataBufferByte) (ImageIO.read(valor)).getRaster().getDataBuffer()).getData();
            NumeroBits n = new NumeroBits(8, dados.length, 1, (byte[]) dados.clone(), valoreEsperado);

            n.setFileName(valor.getName());
            this.entradasTreinoBits.add(n);

            for (int cont = 0; cont < entradasTreinoBits.size() - 1; cont++) {

                if (validaValores(entradasTreinoBits.get(cont),
                        entradasTreinoBits.get(entradasTreinoBits.size() - 1))) {
                    JOptionPane.showMessageDialog(null, "Dados iguais ambiguidade nas imagens! \n"
                            + entradasTreinoBits.get(cont).getFileName() + " \n " + entradasTreinoBits.get(entradasTreinoBits.size() - 1).getFileName());
                    System.exit(0);
                }

            }

        } catch (IOException ex) {
            Logger.getLogger(NeuronioAJC.class.getName()).log(Level.SEVERE, (String) null, ex);
        }
    }

    public boolean validaValores(NumeroBits n1, NumeroBits n2) {

        double[] vet1 = n1.vetbits;
        double[] vet2 = n2.vetbits;

        boolean iguais = false;
        for (int cont = 0; cont < n1.valorEsperado.length && cont < n2.valorEsperado.length; cont++) {

            if (n1.valorEsperado[cont] == n2.valorEsperado[cont]) {

                iguais = true;
                return false;
            }
        }

        for (int cont = 0; cont < vet1.length; cont++) {

            if (vet1[cont] != vet2[cont]) {

                return false;

            }
        }

        return true;

    }

    public NumeroBits getValorTreinoNovoFileiras(File valor) {
        byte[] dados;
        try {
            dados = ((DataBufferByte) (ImageIO.read(valor)).getRaster().getDataBuffer()).getData();
            return new NumeroBits(8, dados.length, 1, (byte[]) dados.clone(), 0);
        } catch (IOException ex) {
            Logger.getLogger(NeuronioAJC.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

    public NumeroBits getValorTreinoNovo(File valor) {
        try {
            byte[] dados = ((DataBufferByte) (ImageIO.read(valor)).getRaster().getDataBuffer()).getData();

            return (new NumeroBits(dados.length, 1, (byte[]) dados.clone(), 0));

        } catch (IOException ex) {
            Logger.getLogger(NeuronioAJC.class.getName()).log(Level.SEVERE, (String) null, ex);
        }
        return null;
    }

    public NumeroBits getValorTreinoNovo(BufferedImage img) {

        int[] dados = (((java.awt.image.DataBufferInt) img.getRaster().getDataBuffer()).getData());

        return (new NumeroBits(dados.length, 1, (int[]) dados.clone(), 0));
    }

    public NeuronioAJC(int nSizeBits) {

        this.nBitsG = nSizeBits;

    }

    /*
    public boolean treino(double[] valores) {
        boolean erro = false;
        int treinou = 0;

        double[] saidas = new double[this.neuroniosC1.length];
        do {
            System.out.println();
            erro = false;
            double[] arrayOfDouble = new double[this.nentradas];
            int i;
            for (i = 0; i < valores.length; i += this.nentradas + 1) {
                for (int v = 0; v < arrayOfDouble.length; v++) {
                    System.out.print(valores[i + v] + "-");
                    arrayOfDouble[v] = valores[i + v];
                }
                for (int a = 0; a < this.neuroniosC1.length; a++) {
                    if (!this.neuroniosC1[a].dentritos(this.nBitsG,
                            arrayOfDouble.clone()).saidaTreino(valores[i + this.nentradas])) {
                        erro = true;
                        this.neuroniosC1[a].corrigePesos(this.neuroniosC1[a].saida(), valores[i + this.nentradas]);
                        treinou++;

                        break;
                    }
                    saidas[a] = this.neuroniosC1[a].saida();
                }
            }
        } while (erro);

        System.out.println("Passou");

        do {
            erro = false;
            double[] arrayOfDouble = new double[this.nentradas];
            int i;
            for (i = 0; i < valores.length; i += this.nentradas + 1) {
                for (int v = 0; v < arrayOfDouble.length;) {
                    arrayOfDouble[v] = valores[i + v];
                    v++;
                }
                for (int a = 0; a < this.neuroniosC1.length; a++) {

                    saidas[a] = this.neuroniosC1[a].dentritos(this.nBitsG, arrayOfDouble.clone()).saida();
                    System.out.print(arrayOfDouble[0] + " " + arrayOfDouble[1] + " " + saidas[a] + " \n");
                }
                System.out.print(" \n" + this.neuronioPC0
                        .dentritos(this.nBitsG, saidas).saida() + " Valores " + valores[i + this.nentradas] + "\n");

                if (!this.neuronioPC0.dentritos(this.nBitsG, saidas.clone()).saidaTreino(valores[i + this.nentradas])) {
                    this.neuronioPC0.corrigePesos(this.neuronioPC0.saida(), valores[i + this.nentradas]);
                    treinou++;
                    erro = true;
                }

            }

        } while (erro);

        System.out.println("treinou " + treinou);

        double[] ventrada = new double[this.nentradas];
        int t;
        for (t = 0; t < valores.length; t += this.nentradas + 1) {
            System.out.print(" Entrada  ");

            for (int v = 0; v < ventrada.length; v++) {

                ventrada[v] = valores[t + v];

                System.out.print(" - " + ventrada[v]);
            }
            for (int a = 0; a < this.neuroniosC1.length; a++) {
                saidas[a] = this.neuroniosC1[a].dentritos(this.nBitsG, ventrada.clone()).saida();
            }

            System.out.println(" -_" + this.neuronioPC0.dentritos(this.nBitsG, saidas.clone()).saida());
        }

        return true;
    }

    public boolean treinoEX2(ArrayList<NumeroBits> nbits, double[][] saidadesejada) {
        this.nentradas = nbits.size();
        int contI = ((NumeroBits) nbits.get(0)).vetbits.length / 2;
        int nNeuro;
        for (nNeuro = 0; nNeuro < this.neuroniosC1.length; nNeuro++) {

            this.neuroniosC1[nNeuro] = new NeuronioPC(nNeuro, nNeuro % 2 == 0);
            contI++;
            if (contI >= ((NumeroBits) nbits.get(0)).vetbits.length) {
                contI = 0;
            }

        }

        this.neuronioCF = null;
        this.neuronioCF = new NeuronioCF[saidadesejada.length];
        for (nNeuro = 0; nNeuro < this.neuronioCF.length; nNeuro++) {
            this.neuronioCF[nNeuro] = new NeuronioCF(nNeuro % 2 == 0);
        }

        boolean erro = false;

        do {
            erro = false;
            double[] saida1 = new double[this.neuroniosC1.length];
            for (int i = 0; i < nbits.size(); i++) {
                for (int j = 0; j < this.neuroniosC1.length; j++) {

                    if (!this.neuroniosC1[j].dentritos(this.nBitsG, nbits.get(i).vetbits).saidaTreino(saidadesejada[0][i])) {
                        this.neuroniosC1[j].corrigePesos(this.neuroniosC1[j].saida(), saidadesejada[0][i]);
                        erro = true;

                        break;
                    }
                }
            }
        } while (erro);

        JOptionPane.showMessageDialog(null, "SAIU");

        return true;
    }
     */
    
      public void treinarRedePSFileiraValidaExists(int tamFileira, int nNeuronios) {

        treinoPSFileira(tamFileira, nNeuronios,entradasTreinoBits, entradasTreinoBits, neuronios.size() == tamFileira);
    }
    public void treinarRedePSFileira(int tamFileira, int nNeuronios) {

        treinoPSFileira(tamFileira, nNeuronios, entradasTreinoBits, entradasTreinoBits, false);
    }

    public void treinarRedePSFileiraContinua(int tamFileira, int nNeuronios) {

        treinoPSFileira(tamFileira, nNeuronios, entradasTreinoBits, entradasTreinoBits, true);
    }

    NeuronioPC[] camada1 = null;
    NeuronioPC[] camada2 = null;
    NeuronioPC[] camada3 = null;
    NeuronioPC[] camada4 = null;

    public int limitFileira = 100;

    public int vezesTreinado = 0;

    ArrayList<NeuronioPC[]> neuronios = new ArrayList<>();

    public boolean treinoPSFileira(int tamFileira, int NS, ArrayList<NumeroBits> nbits, ArrayList<NumeroBits> nSaida, boolean continua) {

        if (!continua) {

            neuronios = new ArrayList<>();

            camada1 = new NeuronioPC[NS];
            camada2 = new NeuronioPC[NS];
            camada3 = new NeuronioPC[NS];
            camada4 = new NeuronioPC[NS];
            int nNeuro;
            for (nNeuro = 0; nNeuro < NS; nNeuro++) {
                camada1[nNeuro] = new NeuronioPC(1, nNeuro % 2 == 0);
                camada2[nNeuro] = new NeuronioPC(3, nNeuro % 2 == 0);
                camada3[nNeuro] = new NeuronioPC(2, nNeuro % 2 == 0);
                camada4[nNeuro] = new NeuronioPC(4, nNeuro % 2 == 0);
            }

            for (int nTamFileira = 0; nTamFileira < tamFileira; nTamFileira++) {

                NeuronioPC[] camada = new NeuronioPC[NS];

                for (nNeuro = 0; nNeuro < NS; nNeuro++) {
                    camada[nNeuro] = new NeuronioPC(new Random().nextInt(nBitsG), nNeuro % 2 == 0);
                }

                neuronios.add(camada);

            }
           
        }
         System.out.println("Iniciou Treino");
        boolean erro = false;

        int cont = 0;

        NumeroBits t = null;

        int nFileira = 1;
        int contTreinoFileira = 0;
        do {
            int nBitsCount = 0;
            erro = false;

            for (int l = 0; l < nbits.size(); l++) {
                NumeroBits valoresEntrada = nbits.get(l);

                nBitsCount++;

                if (t == null) {
                    t = valoresEntrada;
                }

                boolean erroLocal = false;
                do {
                    erroLocal = false;

                    //   for (int camada = 0; camada < camada1.length; camada++) {
                    // System.out.println("Entrou para " + valoresEntrada.valor + " camada1 " + camada1.length);
                    for (int index = 0; index < valoresEntrada.valorEsperado.length; index++) {

                        double r = saidaCompletaFileiras(valoresEntrada);

                        if (valoresEntrada.valorEsperado[index] < 0 && r >= 0 || valoresEntrada.valorEsperado[index] >= 0 && r < 0) {

                            treinaNeuroniosF1Fileiras(valoresEntrada, nFileira);

                            erro = true;
                            erroLocal = true;

                            contTreinoFileira++;

                            if (contTreinoFileira > limitFileira) {
                                contTreinoFileira = 0;
                                nFileira++;
                                if (nFileira > neuronios.size()) {
                                    nFileira = 1;
                                }

                                // System.out.println("Fileira acionada "+nFileira);
                            }
                        }

                    }
                    //  }
                    cont++;
                } while (false);

            }

            //   System.out.println("treinou " + cont);
        } while (erro);

        vezesTreinado = cont;
        //    System.out.println("Terminou " + cont);

        return true;
    }

    public double saidaCompletaFileiras(NumeroBits valoresEntrada) {

        Double sM = null;
        for (int ncount = 0; ncount < neuronios.size(); ncount++) {
            Double s1 = 0.0;
            NumeroBits n = sM == null ? valoresEntrada : getValorTesteCEsperado(sM.intValue(), valoresEntrada.valorEsperado);
            for (int nNeuro = 0; nNeuro < neuronios.get(ncount).length; nNeuro++) {
                s1 += neuronios.get(ncount)[nNeuro]
                        .dentritos(n.vetbits.length, n.vetbits.clone())
                        .saida();

            }
            sM = s1;

        }

        return sM;
    }

    public double treinaNeuroniosF1Fileiras(NumeroBits valoresEntrada, int camada) {

        Double sM = null;
        for (int ncount = 0; ncount < neuronios.size(); ncount++) {
            Double s1 = 0.0;
            NumeroBits n = sM == null ? valoresEntrada : getValorTesteCEsperado(sM.intValue(), valoresEntrada.valorEsperado);
            for (int nNeuro = 0; nNeuro < neuronios.get(ncount).length; nNeuro++) {
                s1 += neuronios.get(ncount)[nNeuro]
                        .dentritos(n.vetbits.length, n.vetbits.clone())
                        .saida();

            }

            if (ncount + 1 == camada) {

                for (int nNeuro = 0; nNeuro < neuronios.get(ncount).length; nNeuro++) {

                    neuronios.get(ncount)[nNeuro].corrigePesos(n.vetbits.clone(),
                            n.getValorEsperado());
                }
            }

            sM = s1;

        }

        return sM;

    }

    public ArrayList<Double> saidaPSFileira(ArrayList<NumeroBits> nbits) {
        ArrayList<Double> result = new ArrayList<>();
        for (int l = 0; l < nbits.size(); l++) {
            NumeroBits nb = nbits.get(l);
            result.add(saidaCompletaFileiras(nb));
        }

        return result;
    }

    public String stringDeDouble(double d[]) {
        String t = "";

        for (int cont = 0; cont < d.length; cont++) {

            t += d[cont] + "-";
        }
        return t;
    }

    static double PAR = -2, IMPAR = 2;

    public static void main(String[] args) {

        System.out.println("" + (2 * 0.2));

//        BitSet b = BitSet.valueOf(new byte[]{-18}).get(0, 8);
//        System.out.println("10001010 " + Integer.toBinaryString(-18));
//
//        for (int cont = 8 - 1; cont >= 0; cont--) {
//
//            System.out.print("" + (b.get(cont) ? 1 : 0));
//        }
//        System.out.println("\n---");
//        for (int cont = b.length() - 1; cont >= 0; cont--) {
//
//            System.out.print("" + (b.get(cont) ? 1 : 0));
//        }
//        System.out.println("\n---");
//
        //    nr.setValorTreino(4, PAR);
//        nr.setValorTreino(5, IMPAR);
//        nr.setValorTreino(6, PAR);
//        nr.setValorTreino(7, IMPAR);
//        nr.setValorTreino(8, PAR);
//        nr.setValorTreino(4, IMPAR);
//        nr.setValorTreino(5, PAR);
//        nr.setValorTreino(6, PAR);
//        nr.setValorTreino(7, IMPAR);
        //  nr.setValorTreino(4, PAR);
        // nr.printaValores();
        long tempoInicial = System.currentTimeMillis();

        NeuronioAJC nr = new NeuronioAJC(16);
//
//        nr.setValorTreino(0, PAR);
//        nr.setValorTreino(1, IMPAR);
//        nr.setValorTreino(2, PAR);
//        nr.setValorTreino(3, IMPAR);
// 
//        nr.limitFileira = 105;
//
//        for (int cont = 105; cont > 4; cont--) {
//            nr.limitFileira = cont;
//            nr.treinarRedePSFileira(6, 6);
//            System.out.println("VezesTreino  " + nr.vezesTreinado + " Limite " + cont + " ");
//        }
//        //nr.printaValoresPesos();
//
//        if (true) {
//            return;
//        }
        for (int cont = 105; cont > 4; cont--) {
            nr = new NeuronioAJC(16);

            nr.setValorTreino(0, PAR);
            nr.setValorTreino(1, IMPAR);
            nr.setValorTreino(2, PAR);
            nr.setValorTreino(3, IMPAR);

            nr.limitFileira = cont;
            nr.treinarRedePSFileira(8, 6);
            System.out.println("TreinoCompleto");
            int aderrado = 0;
            int c = 2;
            do {
//AdErrado 38 Errado 89 -421.6863671271541
                double s = nr.saidaCompletaFileiras(nr.getValorTeste(
                        c));
                if (c % 2 == 0 && s < 0
                        || c % 2 != 0 && s >= 0) {

//                System.out.println("Certo " + c + " " + nr.saidaCompletaFileiras(nr.getValorTeste(
//                        c)));
                } else {
                    aderrado++;
                      System.out.println("AdErrado " + aderrado + " Errado " + c + " " + s);

                    nr.setValorTreino(c, (c % 2 == 0) ? PAR : IMPAR);
                    nr.treinarRedePSFileiraContinua(4, 8);

                }

                c++;

                //System.out.println("" + c);
                if (c > 10000) {
                    break;
                }

            } while (true);

            System.out.println("AdErrado " + aderrado + " " + cont);
        }
// 100 -> 2968

        System.out.println("O método foi executado em " + (System.currentTimeMillis() - tempoInicial));

        // System.out.println(nr.saidaCompletaFileiras(nr.entradasTreinoBits.get(1)));
//        File fileN = new File("E:\\MeusProjetos com IA\\iaAprendizado\\IAaprendizadoCamera\\IMGcopo");
//        File fileS = new File("E:\\MeusProjetos com IA\\iaAprendizado\\IAaprendizadoCamera\\IMGnaocopo");
//        File[] nfile = fileN.listFiles();
//        File[] sfile = fileS.listFiles();
//
//        System.out.println("" + NeuronioAJC.lenghtImagem(sfile[0]));
//        System.out.println("" + NeuronioAJC.lenghtImagem(sfile[0]) * 8);
//
//        NeuronioAJC nr = new NeuronioAJC(NeuronioAJC.lenghtImagem(sfile[0]) * 8);
//        NeuronioAJC nr2 = new NeuronioAJC(NeuronioAJC.lenghtImagem(sfile[0]) * 8);
//
//        ArrayList<NumeroBits> aN = new ArrayList<>();
//
//        ArrayList<NumeroBits> aN2 = new ArrayList<>();
//
//        for (int cont = 0; cont < nfile.length; cont++) {
//            System.out.println(" " + nfile[cont].getPath());
//            nr.setValorTreinoNovoBytesBits(nfile[cont], PAR);
//            nr2.setValorTreinoNovoBytesBits(nfile[cont], IMPAR);
//            aN.add(nr.getValorTreinoNovoFileiras(nfile[cont]));
//            aN2.add(nr.getValorTreinoNovoFileiras(nfile[cont]));
//        }
//
//        for (int cont = 0; cont < sfile.length; cont++) {
//            System.out.println(" " + sfile[cont].getPath());
//            nr2.setValorTreinoNovoBytesBits(sfile[cont], PAR);
//            nr.setValorTreinoNovoBytesBits(sfile[cont], IMPAR);
//            aN.add(nr.getValorTreinoNovoFileiras(sfile[cont]));
//            aN2.add(nr.getValorTreinoNovoFileiras(sfile[cont]));
//        }
//
//         System.out.println(""+nr.getValorTreinoNovo(sfile[0]).paraStringCast()
//          .contentEquals(nr.getValorTreinoNovo(sfile[0]).paraStringCast()));
//        long startTime = System.nanoTime();
//
//          nr.printaValores();
//        nr.treinarRedePSFileira(1);
//         nr.printaValoresPesos();
//        nr2.treinarRedePSFileira(1);
//
//        long endTime = System.nanoTime();
//        long duration = (endTime - startTime) / 1000000;
//        System.out.println("" + duration);
//
//        System.out.println(nr.saidaPSFileira(aN).toString() + "-------");
//        System.out.println(nr2.saidaPSFileira(aN2).toString() + "-------");
//
//        System.out.println(nr.saidaCompletaFileiras(nr.entradasTreinoBits.get(0)));
//
//        System.out.println(nr2.saidaCompletaFileiras(nr.entradasTreinoBits.get(0)));
    }
}


/* Location:              /home/junior/Downloads/RedeACJLib_0.3.jar!/meupneuronio/NeuronioAJC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
