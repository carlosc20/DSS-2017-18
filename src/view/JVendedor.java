package view;

import business.ConfiguraFacil;
import business.venda.FaltamComponenteObrigatorioException;

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
    private DefaultTableModel modelFP;

    private JTable registoProduzidasTable;
    private String[] colunasRegistoProduzidas;
    private DefaultTableModel modelRP;

    private JFrame frame;

    private ConfiguraFacil facade = ConfiguraFacil.getInstancia();

    public JVendedor() {

        frame = new JFrame("Vendedor");
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
                Object[] opcoes = {
                        "Nome:", nomeF,
                        "Nif:", nifF,
                };
                int opcao = JanelaUtil.mostrarJanelaOpcoes(frame, "Dados do cliente", opcoes);
                if (opcao == JOptionPane.OK_OPTION) {
                    String nome = nomeF.getText();
                    try {
                        int nif = Integer.parseInt(nifF.getText());
                        facade.criarEncomenda(nome, nif);
                        new JNovaEncomenda();
                        frame.dispose();
                    } catch (NumberFormatException e1) {
                        JanelaUtil.mostrarJanelaErro(frame, "Nif deve ser um número.");
                    } catch (FaltamComponenteObrigatorioException e1) {
                        JanelaUtil.mostrarJanelaErro(frame, "Não existem componentes obrigatórios suficientes.");
                    } catch (Exception e1) {
                        e1.printStackTrace();
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
            modelFP.setDataVector(facade.getFilaProducao(), colunasFilaProducao);
        } catch (Exception e) {
            JanelaUtil.mostrarJanelaErro(frame, "Não foi possível aceder à base de dados.");
        }
    }

    /**
     *  Atualiza o modelo da tabela de endomendas no registo produzidas.
     */
    private void updateRegistoProduzidas() {
        try {
            modelRP.setDataVector(facade.getRegistoProduzidas(), colunasRegistoProduzidas);
        } catch (Exception e) {
            JanelaUtil.mostrarJanelaErro(frame, "Não foi possível aceder à base de dados.");
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        updateFilaProducao();
        updateRegistoProduzidas();
    }
}
