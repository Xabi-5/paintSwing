import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;

public final class VentaPaint extends JFrame implements ActionListener {
    //Constantes
    public static final ImageIcon ICONO_PAINT = new ImageIcon(VentaPaint.class
            .getResource("icons/painting-icon.png"));

    public static final ImageIcon[] ICO_BOTONS = {
            new ImageIcon(VentaPaint.class.getResource("icons/newPainting.png")),
            new ImageIcon(VentaPaint.class.getResource("icons/save.png")),
            new ImageIcon(VentaPaint.class.getResource("icons/colourWheel.png")),
            new ImageIcon(VentaPaint.class.getResource("icons/strokeWidth.png")),
            new ImageIcon(VentaPaint.class.getResource("icons/file-manager-icon.png")),
            new ImageIcon(VentaPaint.class.getResource("icons/save-file-icon.png")),
            new ImageIcon(VentaPaint.class.getResource("icons/database.png")),
            new ImageIcon(VentaPaint.class.getResource("icons/saveDatabase.png"))
    };

    //Atributos
    private final Lienzo lienzo;
    private final JButton[] btControles = {
            new JButton(ICO_BOTONS[0]),
            new JButton(ICO_BOTONS[1]),
            new JButton(ICO_BOTONS[2]),
            new JButton(ICO_BOTONS[3]),
            new JButton(ICO_BOTONS[4]),
            new JButton(ICO_BOTONS[5]),
            new JButton(ICO_BOTONS[6]),
            new JButton(ICO_BOTONS[7])
    };
    private final JMenuItem[] mnuOpcions = {
            new JMenuItem("Novo"),
            new JMenuItem("Gardar"),
            new JMenuItem("Seleccionar cor"),
            new JMenuItem("Seleccionar grosor"),
            new JMenuItem("Cargar ficheiro"),
            new JMenuItem("Gardar en ficheiro"),
            new JMenuItem("Cargar da base de datos"),
            new JMenuItem("Gardar na base de datos"),
            new JMenuItem("Sair")
    };

    //Constructor
    public VentaPaint(String titulo, Lienzo lienzo) {
        super(titulo);
        this.lienzo = lienzo;
        crearGUI();
    }

    //Metodos

    private void crearGUI() {
        try {
            setIconImage(ICONO_PAINT.getImage());
        } catch (Exception e) {

        }
        JToolBar barraSup = new JToolBar();
        int separo = 0;
        for (var boton : btControles) {
            barraSup.add(boton);
            boton.addActionListener(this);
            if (separo != 0 && separo % 2 != 0) {
                barraSup.addSeparator();
            }
            separo++;
        }
        JMenuBar barra = new JMenuBar();
        JMenu mnuArquivo = new JMenu("Arquivo");
        for (int i = 0; i < mnuOpcions.length; i++) {
            if (i != 0 && i % 2 == 0) {
                mnuArquivo.addSeparator();
            }
            mnuOpcions[i].addActionListener(this);
            mnuArquivo.add(mnuOpcions[i]);
        }
        barra.add(mnuArquivo);
        setJMenuBar(barra);

        add(barraSup, BorderLayout.PAGE_START);
        add(lienzo, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                sair();
            }
        });
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void sair() {
        if (JOptionPane.showConfirmDialog(this, "Desexa sair?", "Saímos?",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public void limpar() {
        if (JOptionPane.showConfirmDialog(this, "Desexas limpar o lienzo?",
                "Estás seguro de que queres limpar o lienzo?",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            lienzo.clear();
        }
    }

    public int getSelectedID() {
        List<Integer> ids = lienzo.getIdDebuxos();
        List<String> nomes = lienzo.getNomeDebuxos();
        JComboBox<String> cbNomes;
        cbNomes = new JComboBox<>();
        for (int i = 0; i < nomes.size(); i++) {
            String nome = nomes.get(i);
            cbNomes.addItem(ids.get(i) + " " + nome);
        }
        int opt = JOptionPane.showOptionDialog(this, cbNomes, "Selecciona da base de datos",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
        if (opt == JOptionPane.OK_OPTION) {
            String[] campos = cbNomes.getSelectedItem().toString().split(" ");
            return Integer.parseInt(campos[0]);
        }
        return 0;
    }

    private static JSlider getSliderGrosor(final JOptionPane optionPane, int valorInicial) {
        JSlider sliderGrosor = new JSlider(JSlider.HORIZONTAL, 1, 10, valorInicial);
        sliderGrosor.setMajorTickSpacing(1);
        sliderGrosor.setPaintTicks(true);
        sliderGrosor.setPaintLabels(true);
        ChangeListener changeListener = (ChangeEvent changeEvent) -> {
            JSlider sliderG = (JSlider) changeEvent.getSource();
            if (!sliderG.getValueIsAdjusting()) {
                optionPane.setInputValue(sliderG.getValue());
            }
        };
        sliderGrosor.addChangeListener(changeListener);
        return sliderGrosor;
    }

    public Object showGrosor(int valorInicial) {
        JOptionPane panelDialogo = new JOptionPane();
        JSlider slider = getSliderGrosor(panelDialogo, valorInicial);
        panelDialogo.setMessage(new Object[]{"Selecciona un grosor: ", slider});
        panelDialogo.setMessageType(JOptionPane.QUESTION_MESSAGE);
        panelDialogo.setOptionType(JOptionPane.OK_CANCEL_OPTION);
        JDialog dialogo = panelDialogo.createDialog(this, "Selecciona un grosor");
        dialogo.setVisible(true);
        System.out.println("Valor: " + panelDialogo.getInputValue());
        return panelDialogo.getInputValue();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btControles[0] || e.getSource() == mnuOpcions[0]) {
            limpar();
        } else if (e.getSource() == btControles[1] || e.getSource() == mnuOpcions[1]) {
            JFileChooser venta = new JFileChooser();
            venta.setSelectedFile(new File(lienzo.getNome() + "." + Lienzo.fileExtension));
            if (venta.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File f = venta.getSelectedFile();
                setTitle(f.getAbsolutePath());
                lienzo.saveImage(f);
            }
        } else if (e.getSource() == btControles[2] || e.getSource() == mnuOpcions[2]) {
            Color cor;
            if ((cor = JColorChooser.showDialog(this, "Cor de pintura", lienzo != null ?
                    lienzo.getCor() : Lienzo.DEFAULT_COR)) != null) {
                lienzo.setCor(cor);
            }
        } else if (e.getSource() == btControles[3] || e.getSource() == mnuOpcions[3]) {
            Object grosor = showGrosor(lienzo.getGrosor());
            if (grosor != JOptionPane.UNINITIALIZED_VALUE){
                lienzo.setGrosor((int) grosor);
            }
        } else if (e.getSource() == btControles[4] || e.getSource() == mnuOpcions[4]) {
            JFileChooser venta = new JFileChooser();
            venta.setSelectedFile(new File(lienzo.getNome() + ".txt"));
            if(venta.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
                File f = venta.getSelectedFile();
                setTitle(f.getAbsolutePath());
                lienzo.loadDebuxoFromFile(f);
                lienzo.repaint();
            }
        }else if (e.getSource() == btControles[5] || e.getSource() == mnuOpcions[5]) {
            JFileChooser venta = new JFileChooser();
            venta.setSelectedFile(new File(lienzo.getNome() + ".txt"));
            if (venta.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File f = venta.getSelectedFile();
                setTitle(f.getAbsolutePath());
                lienzo.saveDebuxoToFile(f);
            }
        }else if (e.getSource() == btControles[6] || e.getSource() == mnuOpcions[6]) {
            int idDebuxo = getSelectedID();
            if (idDebuxo != 0) {
               lienzo.loadDebuxoFromDB(idDebuxo);
               lienzo.repaint();
            }
        }else if (e.getSource() == btControles[7] || e.getSource() == mnuOpcions[7]) {
            lienzo.saveDebuxoTODB();
            JOptionPane.showMessageDialog(this, "Gardado na base de datos: " +
                    lienzo.getNome());
        }else if (e.getSource() == mnuOpcions[mnuOpcions.length -1]) {
            sair();
        }
    }

}
