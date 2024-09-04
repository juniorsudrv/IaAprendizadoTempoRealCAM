/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import OpIO.IO;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import meupneuronio.NeuronioAJC;

/**
 *
 * @author junior
 */
public class Testes {

    static int PAR = 0, IMPAR = 1;

    public static void main(String[] args) {

     
            //Sa�das 0 ou 1
            //0 Par
            //1 Impar
            //Numeros Imagens, vais convertido pra bits 2decimal 10bits 0 ou 1
            //2 000000000010

            NeuronioAJC nr1 = new NeuronioAJC(5); 

            nr1.setValorTreino(0, 2);
            nr1.setValorTreino(1, -2);
            nr1.setValorTreino(2, -2);
            nr1.setValorTreino(3, -2);

              
        
    }
}
