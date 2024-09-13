package meupneuronio;

import Biblis.MemoryUtils;
import TrabalhaBits.NumeroBits;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NeuronioPC
        implements Serializable, Cloneable {

    public int countIndex = 0;
    public byte invertD, invertM;
    public int indice = 0;
    public int maxtentativas = 2000;
    public int ncorrecoes = 0;
    public byte[] aentradas = null;
    public float pesoInicial = 0.17f;
    public float aprendizado = 0.0025f;
    public float bias = -0.3f;
    public float pesos[] = null;
    public float S = 0.0f;
    public byte[] udentritos;
    double MaxM = 0.0f;

    float invertReturnN,
            invertReturnP;

    public NeuronioPC(int indice, boolean iP) {

        invertD =(byte) -3;
        invertM =(byte) 3;

        aprendizado = aprendizado * indice;
        pesoInicial = pesoInicial * indice ;

        
      bias = 0.07f;
      
        this.MaxM = MemoryUtils.maxMemory();
        this.indice = indice;
        if (indice != 0) {
           // this.maxtentativas += indice * 2;
        }
    }

    protected void iniciaPeso(int lt) {
        this.pesos=new float[lt];
        
        for (int cont = 0; cont < lt; cont++) {
            if (MemoryUtils.usedMemory() > this.MaxM - 200.0D) {
                System.out
                        .println(MemoryUtils.usedMemory() + "  " + MemoryUtils.maxMemory());
                System.gc();
            }

            this.pesos[cont]= pesoInicial ;//* ((cont+1)*0.07f);
        }
    }

    protected NeuronioPC dentritos(byte... entradas) {
        this.aentradas = entradas;
        this.udentritos = entradas;

        if (this.pesos == null) {
            iniciaPeso(entradas.length);
        }

//        if (this.ncorrecoes > this.maxtentativas) {
//            this.aentradas[this.indice] = invertEntrada(this.aentradas[this.indice]);
//        }

        float acum = 0.0f;
        try {
            for (int cont = 0; cont < entradas.length; cont++) {
                acum += this.pesos[cont] * entradas[cont];

            }
        } catch (Exception ex) {
            Logger.getLogger(NeuronioPC.class.getName()).log(Level.SEVERE, (String) null, ex);

            System.out.println(" Entrada p  " + this.pesos.length + " " + entradas.length);
            System.exit(0);
        }
         acum += (  this.bias);
        this.S = acum;
        return this;
    }

    protected NeuronioPC dentritos(int nBitsG, byte... entradas) {
        this.aentradas = entradas;
        this.udentritos = entradas;

        if (this.pesos == null) {
            iniciaPeso(nBitsG);
        }

//        if (this.ncorrecoes > this.maxtentativas) {
//            this.aentradas[this.indice] = invertEntrada(this.aentradas[this.indice]);
//        }

        float acum = 0.0f;
        try {
            for (int cont = 0; cont < nBitsG; cont++) {
                acum += this.pesos[cont] * entradas[cont];

            }
        } catch (Exception ex) {
            Logger.getLogger(NeuronioPC.class.getName()).log(Level.SEVERE, (String) null, ex);

            System.out.println(" Entrada p  " + this.pesos.length + " " + entradas.length);
            System.exit(0);
        }
         acum += (  this.bias);
        this.S = acum;
        return this;
    }

    public float saida() {
        //  System.out.println("SaidaAJ " + S);
        return S;
    }

    public int saidaS1() {
        //  System.out.println("SaidaAJ " + S);
        return S >= 0 ? 1 : 0;
    }

    protected boolean saidaTreino(double valoresperado) {
        if (this.ncorrecoes > this.maxtentativas) {
            valoresperado *= this.aentradas[0];
        }

        return (saida() == valoresperado);
    }

    protected double saidaTreinoNumero(double valoresperado) {

        // System.out.println(saida()+ " VVVV "+valoresperado);        
        return saida();
    }

    protected void corrigePesos(byte valoresTreino[], byte[] saidadesejada) {

        for (int contS = 0; contS < saidadesejada.length; contS++) {
            byte v = saidadesejada[contS];
            for (int cont = 0; cont < this.pesos.length; cont++) {
                float valoresperado = v;
                valoresperado = valoresperado * valoresTreino[cont];
                float saida = valoresperado > 0 ? 1 : 0;

                if (cont+1==pesos.length) {     
                    this.pesos[cont]=this.pesos[cont] + this.aprendizado * (valoresperado - saida) * this.bias;
                } else {

                    this.pesos[cont]=this.pesos[cont]  + this.aprendizado * (valoresperado - saida) * valoresTreino[cont];
                }
            }
        }

        fazCorrecao();
    }

    protected void fazCorrecao() {
        this.ncorrecoes++;
    }

    protected boolean ativadoAJ() {
        return (this.ncorrecoes > this.maxtentativas);
    }

    protected byte invertEntrada(byte entrada) {
        if (entrada == 1.0D) {
            return invertD;
        }
        return invertM;
    }

    protected void exibePeso() {
        for (int cont = 0; cont < this.pesos.length; cont++) {
            System.out.print("P" + cont + " " + ((Float) this.pesos[cont]));
        }
        System.out.println();
    }
}


/* Location:              /home/junior/Downloads/RedeACJLib_0.3.jar!/meupneuronio/NeuronioAJ.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
