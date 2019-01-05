package view;

import business.ConfiguraFacil;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

public class JRepositor implements Observer {

    private JPanel mainPanel;
    private JButton sairButton;

    private JTable componentesTable;
    private String[] colunasComponentes = ConfiguraFacil.colunasComponentes;
    private DefaultTableModel modelC;
    private JButton atualizarComponentesButton;

    private JTable pacotesTable;
    private String[] colunasPacotes = ConfiguraFacil.colunasPacotes;
    private DefaultTableModel modelP;
    private JButton atualizarPacotesButton;

    private JFrame frame;

    private ConfiguraFacil facade = ConfiguraFacil.getInstancia();

    // TODO: ver mais sobre o componente?, ir buscar dados por paginas
    // https://docs.oracle.com/javase/tutorial/uiswing/examples/components/TableFilterDemoProject/src/components/TableFilterDemo.java
    // modelC.getValueAt(componentesTable.getSelectedRow(), 1);

    public JRepositor() {

        frame = new JFrame("Repositor");
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000,600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        Observer isto = this;
        facade.addObserver(isto);


        sairButton.addActionListener(new ActionListener() {
            /**
             *  Fecha a janela atual e abre a Inicial.
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                facade.deleteObserver(isto);
                new Inicial();
            }
        });

        //---------------- Componentes ---------------------------------------------------------------------------------

        modelC = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        componentesTable.setModel(modelC);
        updateComponentes();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modelC) {
            @Override
            public boolean isSortable(int column) {
                return column == 0;
            }
        };
        componentesTable.setRowSorter(sorter);

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
                if(chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                    try {
                        facade.atualizaComponentes(chooser.getSelectedFile());
                        JanelaUtil.mostraJanelaInformacao(frame, "Componentes atualizados com sucesso");
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        JanelaUtil.mostraJanelaErro(frame, "Erro ao atualizar.");
                    }
                }
            }
        });



        //---------------- Pacotes -------------------------------------------------------------------------------------

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
                if(chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                    try {
                        facade.atualizaPacotes(chooser.getSelectedFile());
                        JanelaUtil.mostraJanelaInformacao(frame, "Pacotes atualizados com sucesso");
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        JanelaUtil.mostraJanelaErro(frame, "Erro ao atualizar.");
                    }
                }
            }
        });
    }


    /**
     *  Atualiza o modelo da tabela de componentes.
     */
    private void updateComponentes() {
        try {
            modelC.setDataVector(facade.getComponentes(), colunasComponentes);
        } catch (Exception e) {
            e.printStackTrace();
            JanelaUtil.mostraJanelaErro(frame, "Não foi possível aceder à base de dados (Componentes).");
        }
    }

    /**
     *  Atualiza o modelo da tabela de pacotes.
     */
    private void updatePacotes() {
        try {
            modelP.setDataVector(facade.getPacotes(), colunasPacotes);
        } catch (Exception e) {
            e.printStackTrace();
            JanelaUtil.mostraJanelaErro(frame, "Não foi possível aceder à base de dados (Pacotes).");
        }
    }

    /**
     * @param arg do tipo int, se for 0 os componentes foram modificados, se 1 os pacotes foram modificados
     */
    @Override
    public void update(Observable o, Object arg) {
        if((int) arg == 0) {
            updateComponentes();
        } else {
            updatePacotes();
        }
    }
}
