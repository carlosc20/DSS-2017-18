package view;

import business.ConfiguraFacil;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Observable;
import java.util.Observer;

public class JRepositor implements Observer {

    private JPanel mainPanel;
    private JButton atualizarComponentesButton;
    private JButton atualizarPacotesButton;
    private JButton sairButton;

    private JTable componentesTable;
    private String[] colunasComponentes;
    private DefaultTableModel modelC; // modelo dos conteúdos da tabela de componentes

    private JTable pacotesTable;
    private String[] colunasPacotes;
    private DefaultTableModel modelP; // modelo dos conteúdos da tabela de pacotes

    private JFrame frame;

    private ConfiguraFacil facade = ConfiguraFacil.getInstancia();

    // TODO: 26/12/2018 dar sort?,  ver mais sobre o componente?, ir buscar dados por paginas
    // https://docs.oracle.com/javase/tutorial/uiswing/examples/components/TableSortDemoProject/src/components/TableSortDemo.java
    // https://docs.oracle.com/javase/tutorial/uiswing/examples/components/TableFilterDemoProject/src/components/TableFilterDemo.java
    // modelC.getValueAt(componentesTable.getSelectedRow(), 1);

    public JRepositor() {

        frame = new JFrame("Repositor");
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500,600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        facade.addObserver(this);


        sairButton.addActionListener(new ActionListener() {
            /**
             *  Fecha a janela atual e abre a Inicial.
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new Inicial();
            }
        });

        //---------------- Componentes ---------------------------------------------------------------------------------

        colunasComponentes = ConfiguraFacil.colunasComponentes;
        modelC = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        componentesTable.setModel(modelC);
        updateComponentes();

        atualizarComponentesButton.addActionListener(new ActionListener() {
            /**
             *  Abre janela para escolher ficheiro CSV para atualizar stock de componentes.
             */
            @Override
            public void actionPerformed(ActionEvent e) {

                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "Ficheiros CSV", "csv");
                chooser.setFileFilter(filter);
                int returnVal = chooser.showOpenDialog(frame);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    try {
                        facade.atualizaComponentes(chooser.getSelectedFile());
                        JOptionPane.showMessageDialog(frame,
                                "Componentes atualizados com sucesso.",
                                "Confirmação",
                                JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception e1) {
                        JOptionPane.showMessageDialog(frame,
                                "Falha ao atualizar.", // TODO: informaçao sobre erro
                                "Erro",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });



        //---------------- Pacotes -------------------------------------------------------------------------------------

        colunasPacotes = ConfiguraFacil.colunasPacotes;
        modelP = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        pacotesTable.setModel(modelP);
        updatePacotes();

        atualizarPacotesButton.addActionListener(new ActionListener() {
            /**
             *  Abre janela para escolher ficheiro CSV para atualizar os pacotes.
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "Ficheiros CSV", "csv");
                chooser.setFileFilter(filter);
                int returnVal = chooser.showOpenDialog(frame);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    try {
                        facade.atualizaPacotes(chooser.getSelectedFile());
                        JOptionPane.showMessageDialog(frame,
                                "Pacotes atualizados com sucesso.",
                                "Confirmação",
                                JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception e1) {
                        JOptionPane.showMessageDialog(frame,
                                "Falha ao atualizar.", // TODO: informaçao sobre erro
                                "Erro",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }

    /**
     *  Atualiza o modelo da tabela de componentes.
     */
    private void updateComponentes() {
        Object[][] data = new Object[0][];
        try {
            data = facade.getComponentes();
        } catch (Exception e) {
            e.printStackTrace(); // TODO: 29/12/2018 erro
        }
        modelC.setDataVector(data, colunasComponentes);
    }

    /**
     *  Atualiza o modelo da tabela de pacotes.
     */
    private void updatePacotes() {
        Object[][] data = new Object[0][];
        try {
            data = facade.getPacotes();
        } catch (Exception e) {
            e.printStackTrace();    // TODO: 29/12/2018 erro
        }
        modelP.setDataVector(data, colunasPacotes);
    }

    @Override
    public void update(Observable o, Object arg) {
        if((int) arg == 0) {
            updateComponentes();
        } else {
            updatePacotes();
        }
    }

}
