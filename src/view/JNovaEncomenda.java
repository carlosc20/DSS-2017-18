package view;

import business.ConfiguraFacil;
import business.venda.*;
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
    private JButton cancelarButton;

    private JTable obrigatoriosTable;
    private DefaultTableModel modelObr;
    private JButton obrigatorioButton;

    private JTable dependenciasTable;
    private DefaultTableModel modelDep;
    private JButton adicionarDepButton;

    private JTable opcionaisTable;
    private DefaultTableModel modelOpc;
    private JButton removerOpcButton;
    private JButton adicionarOpcButton;

    private JTable pacotesTable;
    private DefaultTableModel modelPac;
    private JButton removerPacoteButton;
    private JButton adicionarPacoteButton;


    private JFrame frame;

    private String[] colunasComponentes;
    private String[] colunasPacotes;

    private ConfiguraFacil facade = ConfiguraFacil.getInstancia();

    public JNovaEncomenda() {

        frame = new JFrame("Nova encomenda");
        frame.setContentPane(mainPanel);
        frame.setSize(1280,720);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        Observer isto = this;
        facade.addObserver(isto);

        colunasComponentes = ConfiguraFacil.colunasComponentes;
        colunasPacotes = ConfiguraFacil.colunasPacotes;

        cancelarButton.addActionListener(new ActionListener() {
            /**
             *  Fecha a janela atual e abre a Inicial.
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                facade.deleteObserver(isto);
                new JVendedor();
            }
        });

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                facade.deleteObserver(isto);
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
        obrigatoriosTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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

                if(id == null) { // se o componente dessa categoria não está escolhido abre a janela de adicionar
                    if(mostraAdicionarComponente(cat) == JOptionPane.OK_OPTION) {
                        obrigatorioButton.setText("Remover componente");
                    }
                } else { // se está escolhido remove-o
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
            JanelaUtil.mostrarJanelaErro(frame, "Não foi possível aceder à base de dados.");
        }

        modelOpc = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        opcionaisTable.setModel(modelOpc);
        updateOpcionais();
        opcionaisTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        adicionarOpcButton.addActionListener(new ActionListener() {
            /**
             *  Abre janela para escolher categoria, se OK abre janela de adicionar componente
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                JList<String> list = new JList<>(modelCatOpc);
                int opcao = JanelaUtil.mostrarJanelaLista(frame, "Escolher categoria", list);
                if (opcao == JanelaUtil.OK) {
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
        dependenciasTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        adicionarDepButton.addActionListener(new ActionListener() {
            /**
             * Adiciona componente opcional selecionado
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = dependenciasTable.getSelectedRow();
                Integer id = (Integer) dependenciasTable.getValueAt(row, 1);
                adicionaComponente(id);
            }
        });


        // ----------- Pacotes -----------------------------------------------------------------------------------------


        modelPac = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        pacotesTable.setModel(modelPac);
        updatePacotes();
        pacotesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        adicionarPacoteButton.addActionListener(new ActionListener() {
            /**
             * Abre janela para escolher pacote
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    DefaultTableModel model = new DefaultTableModel(facade.getPacotes(), colunasPacotes) {
                        @Override
                        public boolean isCellEditable(int row, int column) {
                            return false;
                        }
                    };
                    JTable table = new JTable(model);
                    int opcao = JanelaUtil.mostrarJanelaTabela(frame, "Escolher pacote", table);
                    if (opcao == JanelaUtil.OK) {
                        int row = table.getSelectedRow();
                        Integer id = (Integer) table.getValueAt(row, 0);
                        try {
                            Pair<Set<Integer>,Set<Integer>> efeitos = facade.getEfeitosAdicionarPacote(id);
                            Set<Integer> incompativeis = efeitos.getKey();
                            Set<Integer> dependencias = efeitos.getValue();

                            // TODO: 29/12/2018 mudar com o que existir
                            int op = JOptionPane.showConfirmDialog(frame,
                                    "Incompatibilidades: " + setToString(incompativeis)
                                            + "\nDependencias: " +setToString(dependencias),
                                    "Incompatibilidades e dependencias",
                                    JOptionPane.OK_CANCEL_OPTION);

                            if(op == JOptionPane.OK_OPTION) {
                                facade.adicionaPacote(id);
                            }
                        } catch (PacoteJaExisteNaConfiguracaoException e1) {
                            JanelaUtil.mostrarJanelaErro(frame, "Pacote já existe na configuração.");
                        } catch (PacoteGeraConflitosException e1) {
                            JanelaUtil.mostrarJanelaErro(frame,
                                    "Já existem pacotes formados com componentes desse pacote.");
                        }
                    }
                } catch (Exception e1) {
                    JanelaUtil.mostrarJanelaErro(frame, "Não foi possível aceder à base de dados.");
                }
            }
        });

        removerPacoteButton.addActionListener(new ActionListener() {
            /**
             * Remove o pacote selecionado.
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = pacotesTable.getSelectedRow();
                Integer id = (Integer) pacotesTable.getValueAt(row, 0);
                    try {
                        facade.removePacote(id);
                        removerPacoteButton.setEnabled(false);
                    } catch (PacoteNaoExisteNaConfiguracaoException e1) {
                        e1.printStackTrace();
                    }
            }
        });

        pacotesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            /**
             * Ativa o botão de remover componente quando um componente é selecionado.
             */
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    removerPacoteButton.setEnabled(true);
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
                try {
                    List<Integer> formados = facade.finalizarEncomenda();
                    if(!formados.isEmpty()) {
                        JanelaUtil.mostraJanelaInformacao(frame,
                                "A configuração foi otimizada para obter um melhor desconto" +
                                        ", formando os pacotes:. " + formados.toString());
                    }
                    new JVendedor();
                    frame.dispose();
                } catch (Exception e1) {
                    e1.printStackTrace(); // TODO: 29/12/2018 erro
                }
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
        if(!pacotes.isEmpty()) {
            JanelaUtil.mostraJanelaInformacao(frame,
                    "Devido à remoção de componentes foram desfeitos os seguintes pacotes: "
                    + setToString(pacotes));
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
            JanelaUtil.mostrarJanelaErro(frame, "Não foi possível aceder à base de dados.");
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        int opcao = JanelaUtil.mostrarJanelaTabela(frame, "Escolher " + categoria, table);
        if (opcao == JanelaUtil.OK) {
            int id = (int) model.getValueAt(table.getSelectedRow(), 1);
            return adicionaComponente(id);
        }
        return opcao;
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
        } catch (SQLException e) {
            JanelaUtil.mostrarJanelaErro(frame, "Não foi possível aceder à base de dados.");
        } catch (ComponenteJaExisteNaConfiguracaoException e) {
            JanelaUtil.mostrarJanelaErro(frame, "Componente já foi adicionado.");
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
        modelObr.setDataVector(facade.getComponentesObgConfig(), colunasComponentes);
        obrigatoriosTable.setRowSelectionInterval(0, 0);
    }

    private void updateOpcionais() {
        modelOpc.setDataVector(facade.getComponentesOpcConfig(), colunasComponentes);
    }

    private void updatePacotes() {
        try {
            modelPac.setDataVector(facade.getPacotesConfig(), colunasPacotes);
            removerPacoteButton.setEnabled(false);
        } catch (Exception e) {
            JanelaUtil.mostrarJanelaErro(frame, "Não foi possível aceder à base de dados.");
        }
    }

    private void updateDependencias() {
        try {
            modelDep.setDataVector(facade.getComponentesDepConfig(), colunasComponentes);
            adicionarDepButton.setEnabled(false);
        } catch (Exception e) {
            JanelaUtil.mostrarJanelaErro(frame, "Não foi possível aceder à base de dados.");
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        updateObrigatorios();
        updateOpcionais();
        updateDependencias();
        updatePacotes();
    }
}
