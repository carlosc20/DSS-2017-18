package view;

import business.ConfiguraFacil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

public class JVendedor implements Observer {

    private JPanel mainPanel;
    private JButton sairButton;
    private JButton criarEncomendaButton;

    private JTable filaProducaoTable;
    private DefaultTableModel modelFP; // modelo dos conteúdos da tabela de fila de produção

    private JTable registoProduzidasTable;
    private DefaultTableModel modelRP; // modelo dos conteúdos da tabela de encomendas produzidas

    private ConfiguraFacil facade = ConfiguraFacil.getInstancia();

    // TODO: atualizar tabelas, desativar janela enquanto se cria encomenda

    public JVendedor() {

        JFrame frame = new JFrame("Vendedor");
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500,600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        facade.addObserver(this);

        //atualiza tabelas
        modelFP = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        filaProducaoTable.setModel(modelFP);
        updateFilaProducao();

        modelRP = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        registoProduzidasTable.setModel(modelRP);
        updateRegistoProduzidas();


        // fecha a janela, abre a inicial
        sairButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new Inicial();
            }
        });

        // abre janela para inserir dados do cliente e depois janela de nova encomenda
        criarEncomendaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField nomeF = new JTextField();
                JTextField nifF = new JTextField();
                Object[] options = {
                        "Nome:", nomeF,
                        "Nif:", nifF,
                };

                int option = JOptionPane.showConfirmDialog(frame,
                        options,
                        "Dados do cliente",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE);

                if (option == JOptionPane.OK_OPTION) {
                    String nome = nomeF.getText();
                    try {
                        int nif = Integer.parseInt(nifF.getText());
                        facade.criarEncomenda(nome, nif);
                        new JNovaEncomenda();
                    } catch (Exception e1) {
                        JOptionPane.showMessageDialog(frame,
                                "Erro", // TODO: informaçao sobre erro
                                "Erro",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }

    private void updateFilaProducao() {
        String[] columnNames = facade.getColunasFilaProducao();
        Object[][] data = facade.getFilaProducao();
        modelFP.setDataVector(data, columnNames);
    }

    private void updateRegistoProduzidas() {
        String[] columnNames = facade.getColunasRegistoProduzidas();
        Object[][] data = facade.getRegistoProduzidas();
        modelRP.setDataVector(data, columnNames);
    }

    @Override
    public void update(Observable o, Object arg) {
        updateFilaProducao();
        updateRegistoProduzidas();
    }
}
