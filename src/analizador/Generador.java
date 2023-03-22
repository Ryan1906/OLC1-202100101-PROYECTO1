/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package analizador;

/**
 *
 * @author rjrsigue
 */
public class Generador {
        public static void main(String[] args) {
        try {
            String ruta = "src/analizador/";
            String[] opcFlex = {ruta + "lexico.jflex", "-d", ruta};
            jflex.Main.generate(opcFlex);
            String[] opcCUP = {"-destdir", ruta, "-parser", "sintaxis", ruta + "sintactico.cup"};
            java_cup.Main.main(opcCUP);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
