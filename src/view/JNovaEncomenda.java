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

    private JFrame frame;

    private String[] colunasComponentes;
    private String[] colunasPacotes;

    private ConfiguraFacil facade = ConfiguraFacil.getInstancia();

    public JNovaEncomenda() {

        frame = new JFrame("Nova encomenda");
        frame.setContentPane(mainPanel);
        frame.setSize(500,600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        facade.addObserver(this);

        colunasComponentes = ConfiguraFacil.colunasComponentes;


        cancelarButton.addActionListener(new ActionListener() {
            /**
             *  Fecha a janela atual e abre a Inicial.
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new JVendedor();
            }
        });



        // ----------- Componentes obrigatorios ------------------------------------------------------------------------

        modelObr = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        obrigatoriosTable.setModel(modelObr);
        updateObrigatorios();
        obrigatoriosTable.setRowSelectionInterval(0, 0);


        obrigatorioButton.addActionListener(new ActionListener() {
            /**
             * Abre janela de adicionar componente da categoria selecionada ou remove componente selecionado.
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = obrigatoriosTable.getSelectedRow();
                String cat = (String) obrigatoriosTable.getValueAt(row, 0);
                Integer id = (Integer) obrigatoriosTable.getValueAt(row, 1);

                if(id == null) {
                    // se o componente dessa categoria não está escolhido abre a janela de adicionar
                    if(mostraAdicionarComponente(cat) == JOptionPane.OK_OPTION) {
                        obrigatorioButton.setText("Remover componente");
                    }
                } else {
                    // se está escolhido remove-o
                    try {
                        Set<Integer> pacotes = facade.removeComponente(id);
                        mostraPacotesDesfeitos(pacotes);
                        obrigatorioButton.setText("Adicionar componente");
                    } catch (ComponenteNaoExisteNaConfiguracao e1) {
                        e1.printStackTrace();
                    }
                }
                obrigatoriosTable.setRowSelectionInterval(row, row);
            }
        });


        obrigatoriosTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            /**
             * Muda o texto do botão entre remover componente e adicionar componente conforme a linha selecionada
             */
            @Override
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

        DefaultListModel<String> modelCatOpc = new DefaultListModel<>();
        try {
            for (String u : facade.getCategoriasOpcionais()) {
                modelCatOpc.addElement(u);
            }
        } catch (Exception e) {
            e.printStackTrace(); // TODO: 29/12/2018 erro
        }

        modelOpc = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        opcionaisTable.setModel(modelOpc);
        updateOpcionais();


        adicionarOpcButton.addActionListener(new ActionListener() {
            /**
             *  Abre janela para escolher categoria, se OK abre janela de adicionar componente
             */
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
                    mostraAdicionarComponente(list.getSelectedValue());
                }
            }
        });

        removerOpcButton.addActionListener(new ActionListener() {
            /**
             * Remove componente selecionado
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = opcionaisTable.getSelectedRow();
                Integer id = (Integer) opcionaisTable.getValueAt(row, 1);
                try {
                    Set<Integer> pacotes = facade.removeComponente(id);
                    mostraPacotesDesfeitos(pacotes);
                    removerOpcButton.setEnabled(false);
                } catch (ComponenteNaoExisteNaConfiguracao e1) {
                    e1.printStackTrace();
                }
            }
        });

        opcionaisTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            /**
             * Ativa o botão de remover componente quando um componente é selecionado.
             */
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    removerOpcButton.setEnabled(true);
                }
            }
        });



        // ----------- Componentes dependencias ------------------------------------------------------------------------

        modelDep = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        dependenciasTable.setModel(modelDep);
        updateDependencias();

        adicionarDepButton.addActionListener(new ActionListener() {
            /**
             * Adiciona componente opcional selecionado
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = dependenciasTable.getSelectedRow();
                Integer id = (Integer) dependenciasTable.getValueAt(row, 1);
                    if (adicionaComponente(id) == 0) {
                        adicionarDepButton.setEnabled(false);
                    }
            }
        });

        dependenciasTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            /**
             * Ativa/desativa o botão de adicionar dependencia consoante o comp selecionado estiver ou não adicionado
             */
            @Override
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

        colunasPacotes = ConfiguraFacil.colunasPacotes;

        adicionarPacoteButton.addActionListener(new ActionListener() {
            /**
             * Abre janela para escolher pacote
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                Object[][] data = new Object[0][];
                try {
                    data = facade.getPacotes();
                } catch (Exception e1) {
                    e1.printStackTrace();// TODO: 29/12/2018 erro
                }
                DefaultTableModel model = new DefaultTableModel(data, colunasPacotes) {
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

        // TODO: dar enable/disable no finalizar e configOtima

        finalizarButton.addActionListener(new ActionListener() {
            /**
             * Completa a encomenda e abre uma janela a informar sobre a formação de pacotes se necessário
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                List<Integer> formados = null;
                try {
                    formados = facade.finalizarEncomenda();
                } catch (Exception e1) {
                    e1.printStackTrace(); // TODO: 29/12/2018 erro
                }
                if(!formados.isEmpty()) {
                    // TODO: diz os pacotes formados
                    JOptionPane.showMessageDialog(frame,
                            "A configuração foi otimizada para obter um melhor desconto, formando os pacotes:.",
                            "Configuração otimizada", JOptionPane.INFORMATION_MESSAGE);
                }
                frame.dispose();
                new JVendedor();
            }
        });


        configOtimaButton.addActionListener(new ActionListener() {
            /**
             * Abre janela de configuração ótima
             */
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
    private void mostraPacotesDesfeitos(Set<Integer> pacotes) {
        if(pacotes.size() > 0) {
            JOptionPane.showMessageDialog(frame,
                    "Devido à remoção de componentes foram desfeitos os seguintes pacotes: "
                            + setToString(pacotes),
                    "Pacotes desfeitos",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }


    /**
     * Abre janela para adicionar componente da categoria indicada
     *
     * @param categoria     categoria do componente a adicionar
     */
    private int mostraAdicionarComponente(String categoria) {

        String[] columnNames = ConfiguraFacil.colunasComponentes;
        Object[][] data = new Object[0][];
        try {
            data = facade.getComponentes(categoria);
        } catch (Exception e) {
            e.printStackTrace();    // TODO: 29/12/2018 erro
        }

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
            return adicionaComponente(id);
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
    private int adicionaComponente(int id) {
        try {
            Pair<Set<Integer>,Set<Integer>> efeitos = facade.getEfeitosAdicionarComponente(id);
            Set<Integer> incompativeis = efeitos.getKey();
            Set<Integer> dependencias = efeitos.getValue();

            // TODO: 29/12/2018 mudar com o que existir
            int option = JOptionPane.showConfirmDialog(frame,
                    "Incompatibilidades: " + setToString(incompativeis)
                            + "\nDependencias: " +setToString(dependencias),
                    "Incompatibilidades e dependencias",
                    JOptionPane.OK_CANCEL_OPTION);

            if(option == JOptionPane.OK_OPTION) {
                Set<Integer> pacotes = facade.adicionaComponente(id);
                mostraPacotesDesfeitos(pacotes);
            }
        } catch (SQLException| ComponenteJaExisteNaConfiguracaoException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // TODO: 29/12/2018 melhorar
    private String setToString(Set<Integer> set) {
        Integer[] ids = new Integer[set.size()];
        int i = 0;
        for (Integer p : set) {
            ids[i++] = p;
        }
        return Arrays.toString(ids);
    }

    private void updateObrigatorios() {
        Object[][] data = facade.getComponentesObgConfig();
        modelObr.setDataVector(data, colunasComponentes);
    }

    private void updateOpcionais() {
        Object[][] data = facade.getComponentesOpcConfig();
        modelOpc.setDataVector(data, colunasComponentes);
    }

    private void updateDependencias() {
        Object[][] data = new Object[0][];
        try {
            data = facade.getComponentesDepConfig();
        } catch (Exception e) {
            e.printStackTrace(); // TODO: 29/12/2018 erro
        }
        modelDep.setDataVector(data, colunasComponentes);
    }

    @Override
    public void update(Observable o, Object arg) {
        updateObrigatorios();
        updateOpcionais();
        updateDependencias();
    }
}
