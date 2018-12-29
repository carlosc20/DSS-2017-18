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
    private String[] colunasFilaProducao;
    private DefaultTableModel modelFP; // modelo dos conteúdos da tabela de fila de produção

    private JTable registoProduzidasTable;
    private String[] colunasRegistoProduzidas;
    private DefaultTableModel modelRP; // modelo dos conteúdos da tabela de encomendas produzidas

    private JFrame frame;

    private ConfiguraFacil facade = ConfiguraFacil.getInstancia();

    public JVendedor() {

        frame = new JFrame("Vendedor");
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

        //---------------- Fila producao -------------------------------------------------------------------------------

        colunasFilaProducao = ConfiguraFacil.colunasFilaProducao;
        modelFP = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        filaProducaoTable.setModel(modelFP);
        updateFilaProducao();



        //---------------- Registo produzidas --------------------------------------------------------------------------

        colunasRegistoProduzidas = ConfiguraFacil.colunasRegistoProduzidas;
        modelRP = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        registoProduzidasTable.setModel(modelRP);
        updateRegistoProduzidas();



        //---------------- Criar encomenda -----------------------------------------------------------------------------

        criarEncomendaButton.addActionListener(new ActionListener() {
            /**
             * Abre janela para inserir dados do cliente e depois janela de nova encomenda, fechando a janela atual.
             */
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
                        frame.dispose();
                        new JNovaEncomenda();
                    } catch (NumberFormatException e1) {
                        JOptionPane.showMessageDialog(frame,
                                "O nif deve ser um número.",
                                "Erro",
                                JOptionPane.ERROR_MESSAGE);
                        e1.printStackTrace();
                    } catch (Exception e1) {
                        JOptionPane.showMessageDialog(frame,
                                "Mão foi possível criar encomenda", // TODO: informaçao sobre erro
                                "Erro",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }

    /**
     *  Atualiza o modelo da tabela de encomendas na fila de produção.
     */
    private void updateFilaProducao() {
        try {
            Object[][] data = facade.getFilaProducao();
            modelFP.setDataVector(data, colunasFilaProducao);
        } catch (Exception e) {
            e.printStackTrace(); // TODO: 28/12/2018 erro
        }
    }

    /**
     *  Atualiza o modelo da tabela de endomendas no registo produzidas.
     */
    private void updateRegistoProduzidas() {
        try {
            Object[][] data = facade.getRegistoProduzidas();
            modelRP.setDataVector(data, colunasRegistoProduzidas);
        } catch (Exception e) {
            e.printStackTrace(); // TODO: 28/12/2018 erro
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        updateFilaProducao();
        updateRegistoProduzidas();
    }
}
