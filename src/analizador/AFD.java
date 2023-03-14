/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package analizador;


import java.io.BufferedWriter;
import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author pbara
 */
public class AFD {
    private arbol expresiones;
    private String nombre;
    private ArrayList<Siguientes> ListaSiguientes = new ArrayList<>();
    private ArrayList<String> ListaTerminales = new ArrayList<>();
    private ArrayList<ArrayList> ListaTransiciones = new ArrayList<>();
    private ArrayList<Integer> Aceptado = new ArrayList<>();
    private String thmps = "";
    private int numN = 0;
    private int nodosT =1;
    private int numH = 0;

    public arbol getExpresiones() {
        return expresiones;
    }

    public AFD( String nombre, arbol expresiones) throws IOException, InterruptedException {
        arbol raiz = new arbol(".");
        arbol aceptacion = new arbol("#");
        aceptacion.setHoja(true);
        raiz.setHijoD(aceptacion);
        raiz.setHijoI(expresiones);
        
        this.expresiones = raiz;
        this.nombre = nombre;
    }

    public void asignar(arbol actual) {
        if (actual == null) {
            return;
        }
        asignar(actual.getHijoI());
        asignar(actual.getHijoD());
        if (actual.isHoja()) {
            actual.setId(numH);
            numH++;
            ListaSiguientes.add(new Siguientes(actual.getDato(), actual.getId()));
            if (!ListaTerminales.contains(actual.getDato()) && !actual.getDato().equals("#")) {
                ListaTerminales.add(actual.getDato());
            }
        }
    }

    public void metodo(arbol actual) {
        if (actual == null) {
            return;
        }

        metodo(actual.getHijoI());
        metodo(actual.getHijoD());

        if (actual.isHoja()) {
            actual.setAnulable(false);
            actual.getPrimeros().add(actual.getId());
            actual.getUltimos().add(actual.getId());
        } else {
            switch (actual.getDato()) {
                case "*" -> {
                    actual.setAnulable(true);
                    actual.getPrimeros().addAll(actual.getHijoI().getPrimeros());
                    actual.getUltimos().addAll(actual.getHijoI().getPrimeros());
                    for (int est : actual.getHijoI().getUltimos()) {
                        ListaSiguientes.get(est).getSiguientes().addAll(actual.getHijoI().getPrimeros());
                    }
                }
                case "?" -> {
                    actual.setAnulable(true);
                    actual.getPrimeros().addAll(actual.getHijoI().getPrimeros());
                    actual.getUltimos().addAll(actual.getHijoI().getPrimeros());
                }
                case "+" -> {
                    actual.setAnulable(actual.getHijoI().isAnulable());
                    actual.getPrimeros().addAll(actual.getHijoI().getPrimeros());
                    actual.getUltimos().addAll(actual.getHijoI().getPrimeros());
                    for (int est : actual.getHijoI().getUltimos()) {
                        ListaSiguientes.get(est).getSiguientes().addAll(actual.getHijoI().getPrimeros());
                    }
                }
                case "|" -> {
                    actual.setAnulable(actual.getHijoI().isAnulable() || actual.getHijoD().isAnulable());
                    actual.getPrimeros().addAll(actual.getHijoI().getPrimeros());
                    actual.getPrimeros().addAll(actual.getHijoD().getPrimeros());
                    actual.getUltimos().addAll(actual.getHijoI().getUltimos());
                    actual.getUltimos().addAll(actual.getHijoD().getUltimos());
                }
                case "." -> {
                    actual.setAnulable(actual.getHijoI().isAnulable() && actual.getHijoD().isAnulable());
                    if (actual.getHijoI().isAnulable()) {
                        actual.getPrimeros().addAll(actual.getHijoI().getPrimeros());
                        actual.getPrimeros().addAll(actual.getHijoD().getPrimeros());
                    } else {
                        actual.getPrimeros().addAll(actual.getHijoI().getPrimeros());
                    }
                    if (actual.getHijoD().isAnulable()) {
                        actual.getUltimos().addAll(actual.getHijoI().getUltimos());
                        actual.getUltimos().addAll(actual.getHijoD().getUltimos());
                    } else {
                        actual.getUltimos().addAll(actual.getHijoD().getUltimos());
                    }
                    for (int est : actual.getHijoI().getUltimos()) {
                        ListaSiguientes.get(est).getSiguientes().addAll(actual.getHijoD().getPrimeros());
                    }
                }
            }
        }
    }
    
    
    //
    public void Transiciones() {
        int indice = 0;
        ListaTransiciones.add(new ArrayList<>());
        ArrayList fila = ListaTransiciones.get(0);
        fila.add(expresiones.getPrimeros());
        while (indice < ListaTransiciones.size()) {
            fila = ListaTransiciones.get(indice);
            for (String g : ListaTerminales) {
                fila.add(new ArrayList<>());
            }
            for (int siguiente : (ArrayList<Integer>) fila.get(0)) {
                String simbolo = ListaSiguientes.get(siguiente).getSimbolo();
                if (simbolo.equals("#")) {
                    continue;
                }
                int columna = ListaTerminales.indexOf(simbolo) + 1;
                ArrayList<Integer> col_terminal = (ArrayList<Integer>) fila.get(columna);
                for (int i : ListaSiguientes.get(siguiente).getSiguientes()) {
                    if (!col_terminal.contains(i)) {
                        col_terminal.add(i);
                    }
                }
                Collections.sort(col_terminal);
            }
            boolean encontrado;
            for (int i = 1; i < fila.size(); i++) {
                encontrado = false;
                ArrayList<Integer> estado = (ArrayList<Integer>) fila.get(i);
                for (ArrayList<ArrayList> filas : ListaTransiciones) {
                    if (filas.get(0).equals(estado)) {
                        encontrado = true;
                        break;
                    }
                }
                if (!encontrado && !estado.isEmpty()) {
                    ArrayList<ArrayList> nueva_fila = new ArrayList<>();
                    nueva_fila.add(estado);
                    ListaTransiciones.add(nueva_fila);
                }
            }
            indice++;
        }
    }
    
     public String crearAFND(int primero, int ultimo, arbol actual) {
        String g = "";

        if (actual.isHoja()) {
            g += "S" + primero + " -> S" + ultimo + "[label=\"" + actual.getDato().replaceAll("\"", "") + "\"];\n";
            return g;
        }

        switch (actual.getDato()) {
            case "." -> {
                nodosT += 1;
                int mitad = nodosT;
                g += "S" + nodosT+ ";\n";
                g += crearAFND(primero, mitad, actual.getHijoI());
                g += crearAFND(mitad, ultimo, actual.getHijoD());
            }
            case "|" -> {
                nodosT += 1;
                int pos_izq = nodosT;
                g += "S" + pos_izq + ";\n";
                g += "S" + primero + " -> S" + pos_izq + "[label=\"ε\"];\n";
                nodosT += 1;
                int pos_der = nodosT;
                g += "S" + pos_der + ";\n";
                g += "S" + pos_der + " -> S" + ultimo + "[label=\"ε\"];\n";
                g += crearAFND(pos_izq, pos_der, actual.getHijoI());
                nodosT += 1;
                pos_izq = nodosT;
                g += "S" + pos_izq + ";\n";
                g += "S" + primero + " -> S" + pos_izq + "[label=\"ε\"];\n";
                nodosT += 1;
                pos_der = nodosT;
                g += "S" + pos_der + ";\n";
                g += "S" + pos_der + " -> S" + ultimo + "[label=\"ε\"];\n";
                g += crearAFND(pos_izq, pos_der, actual.getHijoD());
            }
            case "+" -> {
                nodosT += 1;
                int pos_izq = nodosT;
                g += "S" + pos_izq + ";\n";
                g += "S" + primero + " -> S" + pos_izq + "[label=\"ε\"];\n";
                nodosT += 1;
                int pos_der = nodosT;
                g += "S" + pos_der + ";\n";
                g += "S" + pos_der + " -> S" + ultimo + "[label=\"ε\"];\n";
                g += "S" + pos_der + " -> S" + pos_izq + "[label=\"ε\"];\n";
                g += crearAFND(pos_izq, pos_der, actual.getHijoI());
            }
            case "*" -> {
                nodosT += 1;
                int pos_izq = nodosT;
                g += "S" + pos_izq + ";\n";
                g += "S" + primero + " -> S" + pos_izq + "[label=\"ε\"];\n";
                g += "S" + primero + " -> S" + ultimo + "[label=\"ε\"];\n";
                nodosT += 1;
                int pos_der = nodosT;
                g += "S" + pos_der + ";\n";
                g += "S" + pos_der + " -> S" + ultimo + "[label=\"ε\"];\n";
                g += "S" + pos_der + " -> S" + pos_izq + "[label=\"ε\"];\n";
                g += crearAFND(pos_izq, pos_der, actual.getHijoI());
            }
            case "?" -> {
                nodosT += 1;
                int pos_izq = nodosT;
                g += "S" + pos_izq + ";\n";
                g += "S" + primero + " -> S" + pos_izq + "[label=\"ε\"];\n";
                g += "S" + primero + " -> S" + ultimo + "[label=\"ε\"];\n";
                nodosT += 1;
                int pos_der = nodosT;
                g += "S" + pos_der + ";\n";
                g += "S" + pos_der + " -> S" + ultimo + "[label=\"ε\"];\n";
                g += crearAFND(pos_izq, pos_der, actual.getHijoI());
            }
        }
        return g;
    }
    
    public String crearArbol(arbol nodo, int padre) {
        String g = "";
        numN += 1;

        int actual = numN;
        if (nodo == null) {
            numN -= 1;
            return g;
        }

        if (nodo.isHoja()) {
            String anulable = "Anulable";
            if (!nodo.isAnulable()) {
                anulable = "No Anulable";
            }

            g += "N_" + actual + "[shape = circle, label=<"
                + "<FONT POINT-SIZE=\"10\">"
                + "<I>" + anulable + "</I>"
                + "<BR/>"
                + "<B>" 
                + "<FONT POINT-SIZE=\"16\">" + nodo.getDato() + "</FONT>"
                + "</B><BR/>"
                + "<FONT COLOR=\"green\"><B>" + nodo.getPrimeros() + "</B></FONT>"
                + "    "
                + "<FONT COLOR=\"red\"><B>" + nodo.getUltimos() + "</B></FONT>"
                + "<BR/>"
                + nodo.getId()
                + "</FONT>"
                + ">]";
        } else {
            String anulable = "Anulable";
            if (!nodo.isAnulable()) {
                anulable = "No Anulable";
            }

            g += "N_" + actual + "[shape = circle, label=<"
                + "<FONT POINT-SIZE=\"12\">"
                + "<I>" + anulable + "</I>"
                + "<BR/>"
                + "<FONT POINT-SIZE=\"16\">" + nodo.getDato() + "</FONT>"
                + "<BR/>"
                + "<FONT COLOR=\"green\"><B>" + nodo.getPrimeros() + "</B></FONT>"
                + "    "
                + "<FONT COLOR=\"red\"><B>" + nodo.getUltimos() + "</B></FONT>"
                + "</FONT>"
                + ">]";
        }

        if (padre != 0) {
            g += "N_" + padre + " -> N_" + actual + ";\n";
        }

        g += crearArbol(nodo.getHijoI(), actual);
        g += crearArbol(nodo.getHijoD(), actual);

        return g;
    }
    
    public String crearSiguientes() {
        String g = "label=<\n"
                + " <TABLE border=\"1\" cellspacing=\"0\" cellpadding=\"10\"  >\n"
                + "  <TR>\n"
                + "  <TD bgcolor=\"#c5e1a5\"> <font color=\"black\"><b> Simbolo </b></font></TD>\n"
                + "  <TD bgcolor=\"#c5e1a5\"> <font color=\"black\"><b> Hoja </b></font></TD>\n"
                + "  <TD bgcolor=\"#c5e1a5\"> <font color=\"black\"><b> Siguientes </b></font></TD>\n"
                + "  </TR>\n";
        for (Siguientes t : ListaSiguientes) {
            g += " <TR>\n"
                    + "  <TD>" + t.getSimbolo() + "</TD>\n"
                    + "  <TD>" + t.getHoja() + "</TD>\n"
                    + "  <TD> " + t.getSiguientes() + "</TD>\n"
                    + "  </TR>\n";
        }
        g += " </TABLE>>";
        return g;
    }
    
        public String crearTransiciones() {
        String g = "label=<\n"
                + " <TABLE border=\"1\" cellspacing=\"2\" cellpadding=\"10\"  >\n"
                + "  <TR>\n"
                + "  <TD rowspan=\"2\" bgcolor=\"#c5e1a5\"> <font color=\"black\"><b> Estado </b></font></TD>\n"
                + "  <TD colspan=\"" + ListaTerminales.size() + "\" bgcolor=\"#c5e1a5\"> <font color=\"black\"><b> Terminales </b></font></TD>\n"
                + "  </TR>\n"
                + "  <TR>\n";

        for (String terminal : ListaTerminales) {
            g += "  <TD bgcolor=\"#c5e1a5\"> <font color=\"black\"><b> " + terminal + " </b></font></TD>\n";
        }
        g += "  </TR>\n"
                + "  \n";
        for (ArrayList<ArrayList> fila : ListaTransiciones) {
            g += "  <TR>\n"
                    + "  <TD>S" + ListaTransiciones.indexOf(fila) + " " + fila.get(0) + "</TD>\n";
            for (int i = 1; i < fila.size(); i++) {
                ArrayList<Integer> actual = fila.get(i);
                if (actual.isEmpty()) {
                    g += "  <TD> [] </TD>\n";
                }
                for (ArrayList<ArrayList> estados : ListaTransiciones) {
                    if (estados.get(0).equals(actual)) {
                        g += "  <TD>S" + ListaTransiciones.indexOf(estados) + "</TD>\n";
                        break;
                    }
                }
            }
            g += "  </TR>\n";
        }
        g += " </TABLE>>";
        return g;
    }
        
        public String crearAFD() {
        String g = "rankdir=\"LR\";\n"
                + "node [shape=\"circle\"];\n"
                + "SI[shape = none, ];";
        for (int i = 0; i < ListaTransiciones.size(); i++) {
            if (((ArrayList<ArrayList>) ListaTransiciones.get(i)).get(0).contains(ListaSiguientes.size() - 1)) {
                g += "S" + i + "[shape=\"doublecircle\"];\n";
                Aceptado.add(i);
            } else {
                g += "S" + i + ";\n";
            }
        }
        g += "SI->S0[label=\"Inicio\"];\n";
        for (ArrayList<ArrayList> f : ListaTransiciones) {
            for (int indice = 1; indice < f.size(); indice++) {
                ArrayList<Integer> actual = f.get(indice);
                for (ArrayList<ArrayList> estados : ListaTransiciones) {
                    if (estados.get(0).equals(actual)) {
                        g += "  S" + ListaTransiciones.indexOf(f) + "->S" + ListaTransiciones.indexOf(estados) + "[label=\"" + ListaTerminales.get(indice - 1).replaceAll("\"", "") + "\"]";
                        break;
                    }
                }
            }
        }
        return g;
    }
    
    public boolean analisis(ArrayList<Conjunto> conjuntos, String cadena) {
        int num_estado = 0;
        int caracter;
        int num_col;
        ArrayList<Integer> transicion;
        boolean encontrado = false;

        for (int indice = 1; indice < cadena.length() - 1; indice++) {
            ArrayList<ArrayList> estado = ListaTransiciones.get(num_estado);
            caracter = (int) cadena.charAt(indice);
            encontrado = false;
            for(int siguiente: (ArrayList<Integer>)estado.get(0)){
                String t = ListaSiguientes.get(siguiente).getSimbolo();
                if (t.startsWith("\"") && t.endsWith("\"")) {
                    if ((int) t.charAt(1) == caracter ) {
                        encontrado = true;
                        num_col = ListaTerminales.indexOf(t) + 1;
                        transicion = estado.get(num_col);
                        for (ArrayList<ArrayList> fila : ListaTransiciones) {
                            if (fila.get(0).equals(transicion)) {
                                num_estado = ListaTransiciones.indexOf(fila);
                                break;
                            }
                        }
                        break;
                    }
                } else if (t.startsWith("\\")) {
                    if ((int) cadena.charAt(indice + 1) == (int) t.charAt(1)) {
                        encontrado = true;
                        num_col = ListaTerminales.indexOf(t) + 1;
                        transicion = estado.get(num_col);
                        indice++;
                        for (ArrayList<ArrayList> fila : ListaTransiciones) {
                            if (fila.get(0).equals(transicion)) {
                                num_estado = ListaTransiciones.indexOf(fila);
                                break;
                            }
                        }
                        break;
                    }
                } else {
                    ArrayList<Integer> evaluado = new ArrayList<>();
                    for (Conjunto c : conjuntos) {
                        if (c.getNombre().equals(t)) {
                            evaluado = c.getCaracteres();
                            break;
                        }
                    }
                    if (evaluado.contains(caracter)) {
                        encontrado = true;
                        num_col = ListaTerminales.indexOf(t) + 1;
                        transicion = estado.get(num_col);
                        for (ArrayList<ArrayList> fila : ListaTransiciones) {
                            if (fila.get(0).equals(transicion)) {
                                num_estado = ListaTransiciones.indexOf(fila);
                                break;
                            }
                        }
                        break;
                    }
                }

            }
            if (!encontrado) {
                return false;
            }
        }

        return Aceptado.contains(num_estado);
    }

    public void generador(String dir, String nombre, String texto) throws IOException, InterruptedException {
        Path filePath = Paths.get(dir, nombre);
        File file = filePath.toFile();

        try (FileOutputStream fos = new FileOutputStream(file, true);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos))) {
            writer.write(texto);
        }

        String[] command = {"dot", "-Tpng", file.getAbsolutePath(), "-O"};
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();
        int exitCode = process.waitFor();

        if (exitCode == 0) {
            Path imagePath = Paths.get(dir, nombre + ".png");
            Files.move(Paths.get(file.getAbsolutePath() + ".png"), imagePath);
        } else {
            throw new IOException("Error generando imagen");
        }
    }

    public String getNombre() {
        return nombre;
    }
        public void Ejecutar() throws IOException, InterruptedException {
        asignar(expresiones);
        metodo(expresiones);
        generador("src/ARBOLES_202100101/", nombre, "digraph{label = \"Arbol de expresión\"\n" + crearArbol(expresiones, 0) + "}");
        thmps += "digraph {label = \"AFND " 
                + "\";\nrankdir=\"LR\";\nnode [shape=\"circle\"];"
                + "\nS0[];\n"
                + "\nS1[shape = doublecircle, ];\n" + crearAFND(0, 1, expresiones.getHijoI()) + "}";
        generador("src/AFND_202100101/", nombre, thmps);
        generador("src/SIGUIENTES_202100101/", nombre, "graph{"+crearSiguientes()+"}");
        Transiciones();
        generador("src/TRANSICIONES_202100101/",  nombre, "graph{"+crearTransiciones()+"}");
        generador("src/AFD_202100101/", nombre, "digraph {label = \"AFD " + nombre + "\"\n" + crearAFD()+"}");
    }
}