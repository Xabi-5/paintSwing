import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Debuxo {
    //Constantes
    public static final String DEFAULT_NAME = "Debuxo.png";
    public static final char LINE_COMMENT = '#';

    //Atributos
    private ArrayList<Pintable> figuras;
    private String nome;
    private int idDebuxo;

    //Constructores
    public Debuxo(int idDebuxo, String nome) {
        figuras = new ArrayList<>();
        this.idDebuxo = idDebuxo;
        this.nome = nome;
    }

    public Debuxo(String nome) {
        this(0, nome);
    }

    public Debuxo(){
        this(DEFAULT_NAME);
    }

    //Metodos
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getIdDebuxo() {
        return idDebuxo;
    }

    public void setIdDebuxo(int idDebuxo) {
        this.idDebuxo = idDebuxo;
    }

    public void clear(){
        if( figuras != null){
            figuras.clear();
        }
    }

    public ArrayList<Shape> getShapes(){
        ArrayList<Shape> shapes = new ArrayList<>();
        for (Pintable figura : figuras){
            shapes.add(figura.getShape());
        }
        return shapes;
    }

    public ArrayList<Pintable> getFiguras(){
        return figuras;
    }

    public ArrayList<Color> getColors() {
        ArrayList<Color> cores = new ArrayList<>();
        for (Pintable p: figuras) {
            cores.add(p.getCor());
        }
        return cores;
    }

    public void setFiguras(List<Pintable> figuras){
        this.figuras = (ArrayList<Pintable>) figuras;
    }

    public ArrayList<Integer> getWidths() {
        ArrayList<Integer> grosores = new ArrayList<>();
        for(Pintable figura : figuras){
            grosores.add(figura.getWidth());
        }
        return grosores;
    }

    public Shape getShape(int i){
        if (figuras != null && i >= 0 && i < figuras.size()){
            return figuras.get(i).getShape();
        }
        return null;
    }

    public void addPintable(Pintable figura){
        if (figuras != null){
            figuras.add(figura);
        }
    }

    public void addPintable(String tipo, ArrayList<Point> puntos, Color cor, int grosor){
        if(figuras != null){
            figuras.add(PintableFactory.getPintable(tipo, puntos, cor, grosor));
        }
    }
    @Override
    public boolean equals(Object obj){
        if(obj == null || getClass() != obj.getClass()){
            return false;
        }
        if(this == obj){
            return true;
        }
        final Debuxo outro = (Debuxo) obj;
        return this.idDebuxo == outro.getIdDebuxo();
    }

    @Override
    public int hashCode(){
        int hash = 5;
        hash = 13 * hash + (int) (this.idDebuxo ^ (this.idDebuxo>>> 3));
        return hash;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder(nome);
        sb.append(" [").append(idDebuxo).append("]: ").append(System.lineSeparator());
        figuras.forEach(figura ->{
            sb.append(figura).append(System.lineSeparator());
        });
        return sb.toString();
    }

    public void loadDebuxoFromFile(File f){
        try(BufferedReader br = new BufferedReader(new FileReader(f))){
            ArrayList<Pintable> pintables = new ArrayList<>();
            Pintable pintable = null;
            boolean nameRead = false;
            String linha;
            while ((linha = br.readLine()) != null){
                linha = linha.trim();
                if (linha.length() > 0 && (linha.charAt(0) != LINE_COMMENT)){
                    if(!nameRead){
                        nome = linha;
                        nameRead = true;
                    }else {
                        String[] campos = linha.split(",");
                        switch (campos.length){
                            case 1 -> {
                                try{
                                    int anchura = Integer.parseInt(linha);
                                    if (pintable != null){
                                        pintable.setWidth(anchura);
                                        System.out.println("anchura: " + Integer.valueOf(linha));
                                    }
                                }catch (NumberFormatException e){
                                    pintable = PintableFactory.getPintable(linha);
                                    pintables.add(pintable);
                                    System.out.println("Tipo: "+ linha);
                                }
                            }
                            case 2 -> {
                                if (pintable != null){
                                    pintable.addPunto(Integer.parseInt(campos[0].trim()),
                                            Integer.parseInt(campos[1].trim()));
                                }
                            }
                            case 3 -> {
                                if(pintable != null){
                                    pintable.setCor(Integer.parseInt(campos[0].trim()),
                                            Integer.parseInt(campos[1].trim()),
                                            Integer.parseInt(campos[2].trim()));
                                }
                            }
                        }
                    }
                }
            }
            figuras.addAll(pintables);
        }catch (FileNotFoundException fnt){
            System.out.println("O arquivo non se puido atopar " + fnt.getMessage());
        }
        catch (IOException e) {
            System.out.println("Produciuse un erro na lectura do ficheiro " + e.getMessage());
        }
    }


    public void savePintablesToFile(File f){
        try (BufferedWriter bw = new BufferedWriter((new FileWriter(f)))){
            bw.write(LINE_COMMENT  + " Nome debuxo:" + System.lineSeparator());
            bw.write(nome + System.lineSeparator());
            int i = 0;
            for (var figura : figuras){
                bw.write(LINE_COMMENT + " Tipo de pintable " + (++i) + ":" + System.lineSeparator());
                bw.write(figura.getTipoPintable() + System.lineSeparator());
                bw.write(LINE_COMMENT + " Anchura " + (++i) + ":" + System.lineSeparator());
                bw.write(figura.getWidth() + System.lineSeparator());
                bw.write(LINE_COMMENT + " Cor " + (i) + ":" + System.lineSeparator());
                Color cor = figura.getCor();
                bw.write(cor.getRed() + "," + cor.getGreen() + "," + cor.getBlue() + System.lineSeparator());
                bw.write(LINE_COMMENT + " Puntos " + (i) + ":" + System.lineSeparator());
                ArrayList<Point> puntos = figura.getPuntos();
                for(Point punto : puntos){
                    bw.write(punto.x + "," + punto.y + System.lineSeparator());
                }
            }
        } catch (IOException e) {
            System.out.println("Produciuse un erro de E/S");
        }
    }











}
