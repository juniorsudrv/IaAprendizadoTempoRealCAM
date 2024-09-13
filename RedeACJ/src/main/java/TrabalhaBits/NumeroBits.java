package TrabalhaBits;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NumeroBits
        implements Serializable, Cloneable {

    public String fileName;
    public ArrayList<Double> valoresSaidaEsperados = new ArrayList<Double>();
    public byte valorEsperado[];
    long basemulti = 10000000L;
    BitSet bits = null;
    int maxbits = 22;
    public byte[] vetbits = new byte[this.maxbits];
    public long valor = 0L;

    public NumeroBits(int maxbits, int basemulti, String frase) {
        this.valor = this.valor;

        this.basemulti = basemulti;

        this.maxbits = maxbits;
        byte[] l = new byte[frase.length()];
        for (int cont = 0; cont < frase.length(); cont++) {
            l[cont] = (byte) frase.charAt(cont);
        }

        this.vetbits = new byte[maxbits];
        NumeroParaBits(l[0]);

        //  System.out.println();
        // System.out.print(":" + paraString());
        // System.out.println();
    }

    public NumeroBits(int maxbits, int basemulti, int[] valor, byte... valorEsperado) {
        this.valorEsperado = valorEsperado;
        this.basemulti = basemulti;

        this.maxbits = maxbits;
        this.vetbits = new byte[maxbits];
        NumeroParaBits(valor);
//     System.out.println();
//     System.out.println("Travou Aqui " + this.vetbits.length);
//     System.out.print(paraStringCast());
//     System.out.println();
    }

    public NumeroBits(int maxbits, int basemulti, byte[] valor, byte... valorEsperado) {
        this.valorEsperado = valorEsperado;
        this.basemulti = basemulti;

        this.maxbits = maxbits;
        this.vetbits = new byte[maxbits];
        NumeroParaBits(valor);
        //   System.out.println();
//     System.out.println("Travou Aqui " + this.vetbits.length);
        // System.out.print(paraStringCast());
        //   System.out.println();
    }

    public NumeroBits(int mult, int maxbits, int basemulti, int[] valor, byte... valorEsperado) {
        this.valorEsperado = valorEsperado;
        this.basemulti = basemulti;

        this.maxbits = maxbits = mult * maxbits;
        this.vetbits = new byte[maxbits];
        NumeroParaBytesBits(mult, valor);
        //   System.out.println();
//     System.out.println("Travou Aqui " + this.vetbits.length);
        // System.out.print(paraStringCast());
        //   System.out.println();
    }

    public NumeroBits(int maxbits, int basemulti, long valor, byte... valorEsperado) {
        this.valor = valor;
        this.valorEsperado = valorEsperado;

        this.basemulti = basemulti;

        this.maxbits = maxbits;
        this.vetbits = new byte[maxbits];
        NumeroParaBits(valor);
//     System.out.println();
//     
        // System.out.print(":" + paraStringCast());
//     System.out.println();
    }

    
     public NumeroBits(int maxbits, int basemulti, String valor, byte ...valorEsperado) {
    //    this.valor = valor;
        this.valorEsperado = valorEsperado;

        this.basemulti = basemulti;

        this.maxbits = maxbits;
        this.vetbits = new byte[maxbits];
        NumeroParaBits(valor);
//     System.out.println();
//     
        // System.out.print(":" + paraStringCast());
//     System.out.println();
    }
    
    
    public NumeroBits(int maxbits, int basemulti, long valor) {
        this.valor = valor;

        this.basemulti = basemulti;

        this.maxbits = maxbits;
        this.vetbits = new byte[maxbits];
        NumeroParaBits(valor);
//     System.out.println();
//     
//     System.out.print(":" + paraString());
//     System.out.println();
    }

    public NumeroBits() {
    }
    
    
    
     public NumeroBits NumeroParaBits(String valor) {
       

        for (int cont = 0; cont < vetbits.length; cont++) {
            
            if(cont<valor.length())
            {
                vetbits[cont]=(byte)(valor.charAt(cont));
                
            }else{
                
                vetbits[cont]=0;
            }
          

        }

        return this;
    }
    
    

    public NumeroBits NumeroParaBits(long valor) {
        boolean menor = (valor < 0L);
        if (menor) {
            valor *= -1L;
        }
        if (valor != 0L) {

            this.bits = fromLong(valor);
        } else {

            this.bits = new BitSet(this.maxbits);

            for (int i = 0; i < this.maxbits; i++) {
                this.vetbits[i] = 0;
            }

            return this;
        }

        for (int cont = 0; cont < this.maxbits; cont++) {
            if (this.bits.length() > cont) {
                this.vetbits[cont] =(byte) (this.bits.get(cont) ? 1 : 0);
            } else if (menor && cont + 1 == this.maxbits) {
                this.vetbits[cont] = 0;
            } else {
                this.vetbits[cont] = 0;
            }

        }

        return this;
    }

    public NumeroBits NumeroParaBits(byte[] valor) {
        //  System.out.println("MaxBits " + this.maxbits + " " + valor.length);
        int m = 0, n = 222222;
        for (int cont = 0; cont < this.maxbits; cont++) {
            if (valor.length > cont) {
                if (m < valor[cont]) {
                    m = valor[cont];
                }

                if (n > valor[cont]) {
                    n = valor[cont];
                }

                // this.vetbits[cont] = (valor[cont] == 0) ? 1.0D : -1.0D;
                this.vetbits[cont] = valor[cont];

            } else if (cont + 1 == this.maxbits) {
                this.vetbits[cont] = 0;
            } else {
                this.vetbits[cont] = 0;
            }

        }

        // System.out.println("M " + m + " N" + n);
        return this;
    }

    public NumeroBits NumeroParaBytesBits(int mult, int[] valor) {
        int m = 0, n = 222222;

        int index = 0;

        for (int b : valor) {

            BitSet bits = fromLong(b);

            for (int cont = 0; cont < mult; cont++) {
                if (bits.length() > cont) {
                    this.vetbits[index] =(byte) (bits.get(cont) ? 1 : 0);
                } else {
                    this.vetbits[index] = 0;
                }

                index++;
            }

        }

        return this;
    }

    public NumeroBits NumeroParaBits(int[] valor) {

        for (int cont = 0; cont < this.maxbits; cont++) {
            if (valor.length > cont) {
                //  this.vetbits[cont] = (valor[cont] == 0) ? 0.0D : 1.0D; 
            } else if (cont + 1 == this.maxbits) {
                this.vetbits[cont] = 0;
            } else {
                this.vetbits[cont] = 0;
            }

        }

        return this;
    }

    private static BitSet fromLong(long l) {
        return BitSet.valueOf(new long[]{l});
    }

    private static BitSet fromString(String s) {
        return BitSet.valueOf(new long[]{Long.parseLong(s, 2)});
    }

    private static String toString(BitSet bs) {
        return Long.toString(bs.toLongArray()[0], 2);
    }

    private static String toStringChar(BitSet bs) {
        return Long.toUnsignedString(bs.toLongArray()[0], 2);
    }

    public static long toLong(BitSet bits) {
        long value = 0L;
        for (int i = 0; i < bits.length(); i++) {
            value += bits.get(i) ? (1L << i) : 0L;
        }
        return value;
    }

    public long toLong2() {
        long value = 0L;
        for (int i = 0; this.bits != null && i < this.bits.length(); i++) {
            value += this.bits.get(i) ? (1L << i) : 0L;
        }
        return value;
    }

    public String paraStringCast() {
        StringBuffer acum = new StringBuffer();

        for (int cont = 0; cont < vetbits.length; cont++) {
            if (cont < this.vetbits.length) {
                acum.append(this.vetbits[cont]);
            }
        }
        return acum.toString();
    }

    public String paraString() {
        StringBuffer acum = new StringBuffer("Valor:" + this.valor + "Dec->' ");
        int cont = 0;
        if (cont < this.vetbits.length) {
            acum.append(this.vetbits[this.vetbits.length - cont - 1] + " ");
        }

        return acum + "' Bin ";
    }

    public void exibeVet() {
        int cont = 0;
        if (cont < this.vetbits.length) {
            System.out.print((int) this.vetbits[this.vetbits.length - cont - 1]);
        }
    }

    public static void main(String[] args) {
        System.out.println("Letra:" + (char) (byte) (int) toLong((new NumeroBits(11, 1, "S")).bits));
        (new NumeroBits(2, 1, 2L)).NumeroParaBits(2L).exibeVet();
    }

    public byte[] getValorEsperado() {
        return valorEsperado;
    }

    public void setValorEsperado(byte[] valorEsperado) {
        this.valorEsperado = valorEsperado;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    
    

    @Override
    public NumeroBits clone() {

        try {
            return (NumeroBits) super.clone();
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(NumeroBits.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}


/* Location:              /home/junior/Downloads/RedeACJLib_0.3.jar!/TrabalhaBits/NumeroBits.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
