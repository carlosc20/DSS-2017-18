package view;

import business.ConfiguraFacil;
import business.venda.ComponenteJaExisteNaConfiguracaoException;
import business.venda.ComponenteNaoExisteNaConfiguracao;
import business.venda.PacoteGeraConflitosException;
import business.venda.PacoteJaExisteNaConfiguracaoException;
import javafx.util.Pair;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.*;

public class JNovaEncomenda implements Observer {

    private JPanel mainPanel;
    private JButton configOtimaButton;
    private JButton finalizarButton;
    private JButton adicionarPacoteButton; // botão responsável por adicionar pacotes, está sempre ativo
    private JButton adicionarOpcButton;
    private JButton obrigatorioButton; // botão responsável por adicionar/remover componentes obrigatórios, está sempre ativo

    private JTable obrigatoriosTable;
    private DefaultTableModel modelObr;

    private JTable dependenciasTable;
    private DefaultTableModel modelDep;

    private JTable opcionaisTable;
    private JButton adicionarDepButton;
    private JButton cancelarButton;
    private JButton removerOpcButton;
    private DefaultTableModel modelOpc;


    private ConfiguraFacil facade = ConfiguraFacil.getInstancia();

    public JNovaEncomenda() {

        JFrame frame = new JFrame("Nova encomenda");
        frame.setContentPane(mainPanel);
        frame.setSize(500,600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        facade.addObserver(this);

        DefaultListModel<String> modelCatOpc = new DefaultListModel<>();
        for (String u : facade.getCategoriasOpcionais()) {
            modelCatOpc.addElement(u);
        }

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

        modelOpc = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        opcionaisTable.setModel(modelOpc);
        updateOpcionais();

        modelDep = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        dependenciasTable.setModel(modelDep);
        updateDependencias();

        // TODO: dar enable/disable no finalizar e configOtima

        //---------------- Listeners -----------------------------------------------------------------------------------

        // fecha a janela
        cancelarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });


    // ----------- Componentes obrigatorios ----------------------------------------------------------------------------

        // abre janela de adicionar componente da categoria selecionada ou remove componente selecionado
        obrigatorioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = obrigatoriosTable.getSelectedRow();
                String cat = (String) obrigatoriosTable.getValueAt(row, 0);
                Integer id = (Integer) obrigatoriosTable.getValueAt(row, 1);

                if(id == null) {
                    // se o componente dessa categoria não está escolhido abre a janela de adicionar
                    if(mostraAdicionarComponente(frame, cat) == JOptionPane.OK_OPTION) {
                        obrigatorioButton.setText("Remover componente");
                    }
                } else {
                    // se está escolhido remove-o
                    try {
                        Set<Integer> pacotes = facade.removeComponente(id);
                        mostraPacotesDesfeitos(frame, pacotes);
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
                int row = obrigatoriosTable.getSelectedRow();
                if (!e.getValueIsAdjusting() && row != -1) {
                    Integer id = (Integer) obrigatoriosTable.getValueAt(row, 1);
                    if(id == null) {
                        obrigatorioButton.setText("Adicionar componente");
                    } else {
                        obrigatorioButton.setText("Remover componente");
                    }
                }
            }
        });




        // ----------- Componentes opcionais ---------------------------------------------------------------------------

        // abre janela para escolher categoria, se OK abre janela de adicionar componente
        adicionarOpcButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JList<String> list = new JList<>(modelCatOpc);
                list.setSelectedIndex(0);
                int option = JOptionPane.showConfirmDialog(frame,
                        new JScrollPane(list),
                        "Escolher categoria",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE);
                if (option == JOptionPane.OK_OPTION) {
                    mostraAdicionarComponente(frame, list.getSelectedValue());
                }
            }
        });


        // remove componente selecionado
        removerOpcButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = opcionaisTable.getSelectedRow();
                Integer id = (Integer) opcionaisTable.getValueAt(row, 1);
                try {
                    Set<Integer> pacotes = facade.removeComponente(id);
                    mostraPacotesDesfeitos(frame, pacotes);
                    removerOpcButton.setEnabled(false);
                } catch (ComponenteNaoExisteNaConfiguracao e1) {
                    e1.printStackTrace();
                }
            }
        });


        // ativa o botão de remover componente quando um componente é selecionado
        opcionaisTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    removerOpcButton.setEnabled(true);
                }
            }
        });



        // ----------- Componentes Dependencias ------------------------------------------------------------------------

        // adiciona componente opcional selecionado
        adicionarDepButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = dependenciasTable.getSelectedRow();
                Integer id = (Integer) dependenciasTable.getValueAt(row, 1);
                    if (adicionaComponente(frame, id) == 0) {
                        adicionarDepButton.setEnabled(false);
                    }
            }
        });


        // ativa/desativa o botão de adicionar dependencia consoante o comp selecionado estiver ou não adicionado
        dependenciasTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    // TODO: 28/12/2018 ve se está adicionado e da enable
                    if(true) {
                        adicionarDepButton.setEnabled(true);
                    } else {
                        adicionarDepButton.setEnabled(false);
                    }
                }
            }
        });



        // ----------- Pacotes -----------------------------------------------------------------------------------------

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
                        // TODO: 28/12/2018 faz cenas
                        e1.printStackTrace();
                    } catch (PacoteGeraConflitosException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });



        // ----------- Outros ------------------------------------------------------------------------------------------

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
    }


    /**
     * Abre janela que mostra os ids dos pacotes desfeitos com a remoção de componentes
     *
     * @param pacotes Set com os ids dos pacotes desfeitos
     */
    private void mostraPacotesDesfeitos(JFrame frame, Set<Integer> pacotes) {
        if(pacotes.size() > 0) {
            Integer[] ids = new Integer[pacotes.size()];
            int i = 0;
            for (Integer p : pacotes) {
                ids[i++] = p;
            }

            JOptionPane.showMessageDialog(frame,
                    "Devido à remoção de componentes foram desfeitos os seguintes pacotes: "
                            + Arrays.toString(ids),
                    "Pacotes desfeitos",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }


    /**
     * Abre janela para adicionar componente da categoria indicada
     *
     * @param categoria     categoria do componente a adicionar
     */
    private int mostraAdicionarComponente(JFrame frame, String categoria) {

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
                "Escolher  " + categoria,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);


        if (option == 0) {
            int id = (int) model.getValueAt(table.getSelectedRow(), 1);
            return adicionaComponente(frame, id);
        }
        return option;
    }


    /**
     * Abre janela para
     *
     * @param id     categoria do componente a adicionar
     *
     * @return 0 se o componente for adicionado
     */
    private int adicionaComponente(JFrame frame, int id) {
        try {
            Pair<Set<Integer>,Set<Integer>> efeitos = facade.getEfeitosAdicionarComponente(id);

            // TODO: 28/12/2018 verificar outras cenas

            Set<Integer> pacotes = facade.adicionaComponente(id);
            mostraPacotesDesfeitos(frame, pacotes);
        } catch (SQLException| ComponenteJaExisteNaConfiguracaoException e) {
            e.printStackTrace();
        }
        return 0;
    }


    private void updateObrigatorios() {
        String[] columnNames = ConfiguraFacil.colunasComponentes;
        Object[][] data = facade.getComponentesObgConfig();
        modelObr.setDataVector(data, columnNames);
    }

    private void updateOpcionais() {
        String[] columnNames = ConfiguraFacil.colunasComponentes;
        Object[][] data = facade.getComponentesOpcConfig();
        modelOpc.setDataVector(data, columnNames);
    }

    private void updateDependencias() {
        String[] columnNames = ConfiguraFacil.colunasComponentes;
        Object[][] data = facade.getComponentesDepConfig();
        modelDep.setDataVector(data, columnNames);
    }

    @Override
    public void update(Observable o, Object arg) {
        updateObrigatorios();
        updateOpcionais();
        updateDependencias();
    }
}
