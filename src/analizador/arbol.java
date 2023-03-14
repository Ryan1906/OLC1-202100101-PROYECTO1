/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package analizador;

import java.util.ArrayList;

/**
 *
 * @author pbara
 */
public class arbol {
    private String dato;
    private String titulo;
    private arbol hijoI;
    private arbol hijoD;
    private int id=0;
    private boolean hoja = false;
    private boolean anulable;
    private ArrayList<Integer> primeros = new ArrayList<>();
    private ArrayList<Integer> ultimos = new ArrayList<>();

    public arbol(String dato) {
        this.dato = dato;
    }

    public arbol getHijoI() {
        return hijoI;
    }

    public void setHijoI(arbol hijoI) {
        this.hijoI = hijoI;
    }

    public arbol getHijoD() {
        return hijoD;
    }

    public void setHijoD(arbol hijoD) {
        this.hijoD = hijoD;
    }

    public boolean isHoja() {
        return hoja;
    }

    public void setHoja(boolean hoja) {
        this.hoja = hoja;
    }

    public String getDato() {
        return dato;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isAnulable() {
        return anulable;
    }

    public void setAnulable(boolean anulabe) {
        this.anulable = anulabe;
    }

    public ArrayList<Integer> getPrimeros() {
        return primeros;
    }

    public void setPrimeros(ArrayList<Integer> primeros) {
        this.primeros = primeros;
    }

    public ArrayList<Integer> getUltimos() {
        return ultimos;
    }

    public void setUltimos(ArrayList<Integer> ultimos) {
        this.ultimos = ultimos;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }


}
