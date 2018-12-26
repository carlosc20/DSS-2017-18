package view;

import business.ConfiguraFacil;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

public class JRepositor implements Observer {

    private JPanel mainPanel;
    private JButton atualizarStockButton;
    private JButton sairButton;

    private JTable componentesTable;
    private DefaultTableModel modelC; // modelo dos conteúdos da tabela de componentes

    private JTable pacotesTable;
    private DefaultTableModel modelP; // modelo dos conteúdos da tabela de pacotes

    private ConfiguraFacil facade = ConfiguraFacil.getInstancia();

    // TODO: 26/12/2018 dar sort?,  ver mais sobre o componente?, ir buscar dados por paginas
    // https://docs.oracle.com/javase/tutorial/uiswing/examples/components/TableSortDemoProject/src/components/TableSortDemo.java
    // https://docs.oracle.com/javase/tutorial/uiswing/examples/components/TableFilterDemoProject/src/components/TableFilterDemo.java
    // modelC.getValueAt(componentesTable.getSelectedRow(), 1);

    public JRepositor() {

        JFrame frame = new JFrame("Repositor");
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500,600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        facade.addObserver(this);

        //atualiza tabelas
        modelC = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        componentesTable.setModel(modelC);
        updateComponentes();

        modelP = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        pacotesTable.setModel(modelP);
        updatePacotes();


        // fecha a janela, abre a inicial
        sairButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new Inicial();
            }
        });

        // abre janela para escolher ficheiro csv
        atualizarStockButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "Ficheiros CSV", "csv");
                chooser.setFileFilter(filter);
                int returnVal = chooser.showOpenDialog(frame);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    try {
                        facade.atualizarStock(chooser.getSelectedFile());
                        JOptionPane.showMessageDialog(frame,
                                "Stock atualizado com sucesso.",
                                "Confirmação",
                                JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception e1) {
                        JOptionPane.showMessageDialog(frame,
                                "Falha ao atualizar stock.", // TODO: informaçao sobre erro
                                "Erro",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }

    private void updateComponentes() {
        String[] columnNames = facade.getColunasComponentes();
        Object[][] data = facade.getComponentes();
        modelC.setDataVector(data, columnNames);
    }

    private void updatePacotes() {
        String[] columnNames = facade.getColunasPacotes();
        Object[][] data = facade.getPacotes();
        modelP.setDataVector(data, columnNames);
    }

    @Override
    public void update(Observable o, Object arg) {
        updatePacotes();
        updateComponentes();
    }
}
