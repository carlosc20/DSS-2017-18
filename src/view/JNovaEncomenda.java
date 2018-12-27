package view;

import business.ConfiguraFacil;
import business.venda.ComponenteNaoExisteNaConfiguracao;
import business.venda.PacoteGeraConflitosException;
import business.venda.PacoteJaExisteNaConfiguracaoException;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class JNovaEncomenda implements Observer {

    private JPanel mainPanel;
    private JButton configOtimaButton;
    private JButton finalizarButton;
    private JButton adicionarPacoteButton; // botão responsável por adicionar pacotes, está sempre ativo
    private JButton opcionalButton;
    private JButton obrigatorioButton; // botão responsável por adicionar/remover componentes obrigatórios, está sempre ativo

    private JTable obrigatoriosTable;
    private DefaultTableModel modelObr;

    private JTable dependenciasTable;
    private DefaultTableModel modelDep;

    private JTable opcionaisTable;
    private JButton dependenteButton;
    private JButton cancelarButton;
    private JButton removerComponenteButton;
    private DefaultTableModel modelOpc;


    private ConfiguraFacil facade = ConfiguraFacil.getInstancia();

    private List<String> catOpcionais;
    private List<String> catObrigatorias;

    public JNovaEncomenda() {

        JFrame frame = new JFrame("Nova encomenda");
        frame.setContentPane(mainPanel);
        frame.setSize(500,600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        facade.addObserver(this);

        catObrigatorias = facade.getCategoriasObrigatorias();
        catOpcionais = facade.getCategoriasOpcionais();

        // atualiza tabelas
        modelObr = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        obrigatoriosTable.setModel(modelObr);
        updateObrigatorios();
        obrigatoriosTable.setRowSelectionInterval(0, 0);

        // TODO: 27/12/2018 seleciona o primeiro

        modelOpc = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        opcionaisTable.setModel(modelOpc);

        modelDep = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        dependenciasTable.setModel(modelDep);


        // TODO: dar enable/disable no finalizar e configOtima

        //---------------- LISTENERS ---------------------------------------------------

        // fecha a janela
        cancelarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });


        // completa a encomenda e abre uma janela a informar sobre a formação de pacotes se necessário
        finalizarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<Integer> formados = facade.finalizarEncomenda();
                if(!formados.isEmpty()) {
                    // TODO: diz os pacotes formados
                    JOptionPane.showMessageDialog(frame,
                            "A configuração foi otimizada para obter um melhor desconto, formando os pacotes:.",
                            "Configuração otimizada", JOptionPane.INFORMATION_MESSAGE);
                }
                frame.dispose();
            }
        });


        // abre janela de configuração ótima
        configOtimaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: 27/12/2018 acabar
                facade.criarConfiguracaoOtima();
            }
        });


        // abre janela de adicionar componente da categoria selecionada ou remove componente selecionado
        obrigatorioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = obrigatoriosTable.getSelectedRow();
                String cat = (String) obrigatoriosTable.getValueAt(row, 0);
                Integer id = (Integer) obrigatoriosTable.getValueAt(row, 1);

                if(id == null) {    // se o componente dessa categoria não está escolhido abre a janela de adicionar
                    if(adicionaComponente(frame, cat) == JOptionPane.OK_OPTION) {
                        obrigatorioButton.setText("Remover componente");
                    }
                } else {            // se está escolhido remove-o
                    try {
                        facade.removeComponente(id);
                    } catch (ComponenteNaoExisteNaConfiguracao e1) {
                        e1.printStackTrace();
                    }
                    obrigatorioButton.setText("Adicionar componente");
                }
            }
        });


        // muda o texto do botão entre remover componente e adicionar componente conforme a linha selecionada
        obrigatoriosTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int row = obrigatoriosTable.getSelectedRow();
                    Integer id = (Integer) obrigatoriosTable.getValueAt(row, 1);
                    if(id == null) {
                        obrigatorioButton.setText("Adicionar componente");
                    } else {
                        obrigatorioButton.setText("Remover componente");
                    }
                }
            }
        });


        // abre janela para escolher pacote
        adicionarPacoteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] columnNames = ConfiguraFacil.colunasPacotes;
                Object[][] data = facade.getPacotes();
                DefaultTableModel model = new DefaultTableModel(data, columnNames) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };
                JTable table = new JTable(model);
                table.setRowSelectionInterval(0, 0);
                int option = JOptionPane.showConfirmDialog(frame,
                        new JScrollPane(table),
                        "Escolher pacote",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE);

                if (option == JOptionPane.OK_OPTION) {
                    int row = obrigatoriosTable.getSelectedRow();
                    Integer id = (Integer) obrigatoriosTable.getValueAt(row, 0);
                    try {
                        facade.adicionaPacote(id);
                    } catch (PacoteJaExisteNaConfiguracaoException e1) {
                        e1.printStackTrace();
                    } catch (PacoteGeraConflitosException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });


        // abre janela para escolher categoria, se OK abre janela de adicionar componente
        opcionalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                DefaultListModel<String> model = new DefaultListModel<>();
                for (String u : catOpcionais) {
                    model.addElement(u);
                }
                JList<String> list = new JList<>(model);
                list.setSelectedIndex(0);
                int option = JOptionPane.showConfirmDialog(frame,
                        new JScrollPane(list),
                        "Escolher categoria",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE);


                if (option == JOptionPane.OK_OPTION) {
                    adicionaComponente(frame, list.getSelectedValue());
                }
            }
        });


        dependenteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

    }


    /**
     * Abre janela para adicionar componente da categoria indicada
     *
     * @param categoria     categoria do componente a adicionar
     */
    private int adicionaComponente(JFrame frame, String categoria) {

        String[] columnNames = ConfiguraFacil.colunasComponentes;
        Object[][] data = facade.getComponentes(categoria);

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowSelectionInterval(0, 0);
        int option = JOptionPane.showConfirmDialog(frame,
                new JScrollPane(table),
                "Escolher componente",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);


        if (option == JOptionPane.OK_OPTION) {
            int id = (int) model.getValueAt(table.getSelectedRow(), 0);
            try {
                facade.adicionaComponente(id);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return option;
    }

    private void updateObrigatorios() {
        String[] columnNames = ConfiguraFacil.colunasComponentes;
        Object[][] data = facade.getComponentesObgConfig();
        modelObr.setDataVector(data, columnNames);
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
