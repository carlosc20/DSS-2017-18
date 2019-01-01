package view;

import business.ConfiguraFacil;
import business.venda.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

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
    private JLabel precoLabel;
    private JLabel descontoLabel;


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
             *  Fecha a janela atual e abre a Vendedor.
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                facade.deleteObserver(isto);
                new JVendedor();
            }
        });

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            /**
             *  Ao fechar a janela abre a Vendedor.
             */
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
                Integer id = (Integer) obrigatoriosTable.getValueAt(row, 1);
                if(id == null) { // se o componente dessa categoria não está escolhido abre a janela de adicionar
                    String cat = (String) obrigatoriosTable.getValueAt(row, 0);
                    if(mostraAdicionarComponente(cat) == JOptionPane.OK_OPTION) {
                        obrigatorioButton.setText("Remover componente");
                    }
                } else { // se está escolhido remove-o
                    try {
                        Set<Integer> pacotes = facade.removeComponente(id);
                        mostraPacotesDesfeitos(pacotes);
                        obrigatorioButton.setText("Adicionar componente");
                    } catch (Exception e1) {
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
                } catch (Exception e1) {
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

        dependenciasTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            /**
             * Ativa o botão de adicionar dependencia quando um componente é selecionado.
             */
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    adicionarDepButton.setEnabled(true);
                }
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
                            int option = mostrarIncompatibilidades(facade.getIncompatibilidadesPacote(id));
                            if(option == JOptionPane.OK_OPTION) {
                                option = mostrarDependencias(facade.getDependenciasPacote(id));
                            }
                            if(option == JOptionPane.OK_OPTION) {
                                Set<Integer> pacotes =  facade.adicionaPacote(id);
                                mostraPacotesDesfeitos(pacotes);
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
                } catch (FaltamDependentesException e1) {
                    JanelaUtil.mostrarJanelaErro(frame,"Existem dependências não adicionadas: "
                            +  e1.getMessage());
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
                JList<String> list = new JList<>(modelCatOpc);
                list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                list.setSelectedIndex(0);
                JTextField precoF = new JTextField();
                int opcao = JOptionPane.showConfirmDialog(frame,
                        new Object[] {"Componentes opcionais:", new JScrollPane(list), "Preço total máximo:", precoF},
                        "Configuração ótima",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE);
                if(opcao == JOptionPane.OK_OPTION) {
                    try {
                        int precoMax = Integer.parseInt(precoF.getText());
                        String[] cats = list.getSelectedValuesList().toArray(new String[0]);
                        int n = cats.length;
                        JSlider[] opcoes = new JSlider[n];
                        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
                        labelTable.put( 0, new JLabel("0") );
                        labelTable.put( precoMax, new JLabel(Integer.toString(precoMax)));
                        for (int i = 0; i < n; i++) {
                            opcoes[i] = new JSlider(JSlider.HORIZONTAL, 0, precoMax, 0); // TODO: dizer a qual corresponde
                            opcoes[i].setLabelTable(labelTable);
                            opcoes[i].setPaintLabels(true);
                        }
                        opcao = JanelaUtil.mostrarJanelaOpcoes(frame, "Configuração ótima", opcoes);
                        if (opcao == JanelaUtil.OK) {
                            Map<String, Integer> catMax = new HashMap<>();
                            for (int i = 0; i < n; i++) {
                                catMax.put(cats[i], opcoes[i].getValue());
                            }
                            try {
                                facade.criarConfiguracaoOtima(catMax, precoMax);
                            } catch (FaltamComponenteObrigatorioException e1) {
                                JanelaUtil.mostrarJanelaErro(frame,
                                        "Todos os componentes obrigatórios devem estar escolhidos");
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                    } catch (NumberFormatException e1) {
                        JanelaUtil.mostrarJanelaErro(frame, "O preço máximo deve ser um número");
                    }
                }
            }
        });



        // -------------------------------------------------------------------------------------------------------------
    }


    /**
     * Abre janela que mostra os ids dos pacotes desfeitos com a remoção de componentes.
     *
     * @param pacotes Set com os ids dos pacotes desfeitos
     */
    private void mostraPacotesDesfeitos(Set<Integer> pacotes) throws Exception {
        if(!pacotes.isEmpty()) {
            JanelaUtil.mostraJanelaInformacao(frame,
                    "Devido à remoção de componentes foram desfeitos os seguintes pacotes: "
                    + setToStringPacotes(pacotes));
        }
    }


    /**
     * Abre janela para adicionar componente da categoria indicada.
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
     * Abra janela de adicionar componente e adiciona-o se forem aceites as possíveis condições.
     *
     * @param id     categoria do componente a adicionar
     *
     * @return 0 se o componente for adicionado
     */
    private int adicionaComponente(int id) {
        try {
            int option = mostrarIncompatibilidades(facade.getIncompatibilidadesComponente(id));
            if(option == JOptionPane.OK_OPTION) {
                option = mostrarDependencias(facade.getDependenciasComponente(id));
            }
            if(option == JOptionPane.OK_OPTION) {
                Set<Integer> pacotes = facade.adicionaComponente(id);
                mostraPacotesDesfeitos(pacotes);
            }
        } catch (SQLException e) {
            JanelaUtil.mostrarJanelaErro(frame, "Não foi possível aceder à base de dados.");
        } catch (ComponenteJaExisteNaConfiguracaoException e) {
            JanelaUtil.mostrarJanelaErro(frame, "Componente já foi adicionado.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Mostra uma janela que informa sobre as incompatibilidades da operação que deu origem ao parâmetro
     * incompatibilidades.
     *
     * @return 0 se OK
     */
    private int mostrarIncompatibilidades(Set<Integer> incompatibiliddades) throws Exception {
        int option = JOptionPane.OK_OPTION;
        if (!incompatibiliddades.isEmpty()) {
            return  JOptionPane.showConfirmDialog(frame,
                    "Serão removidos os seguintes componentes incompatíveis: "
                            + setToStringComponentes(incompatibiliddades),
                    "Aviso",
                    JOptionPane.OK_CANCEL_OPTION);
        }
        return option;
    }

    /**
     * Mostra uma janela que informa sobre as dependências que serão criadas pela operação que deu origem ao parâmetro
     * dependencias.
     *
     * @return 0 se OK
     */
    private int mostrarDependencias(Set<Integer> dependencias) throws Exception {
        int option = JOptionPane.OK_OPTION;
        if (!dependencias.isEmpty()) {
            return  JOptionPane.showConfirmDialog(frame,
                    "Serão formadas as seguintes dependências: "
                            + setToStringComponentes(dependencias),
                    "Aviso",
                    JOptionPane.OK_CANCEL_OPTION);
        }
        return option;
    }

    /**
     * Converte um set para uma string de números separados por vírgula, exemplo: "1, 2, 3"
     */
    private String setToStringPacotes(Set<Integer> set) throws Exception {
        StringBuilder builder = new StringBuilder();
        Iterator it = set.iterator();
        Integer id = (Integer) it.next();
        String d = facade.getDesignacaoPacote(id);
        builder.append(d);
        builder.append(" (");
        builder.append(id);
        builder.append(")");
        while(it.hasNext()) {
            builder.append(", ");
            id = (Integer) it.next();
            d = facade.getDesignacaoPacote(id);
            builder.append(d);
            builder.append(" (");
            builder.append(id);
            builder.append(")");
        }
        return builder.toString();
    }

    private String setToStringComponentes(Set<Integer> set) throws Exception {
        StringBuilder builder = new StringBuilder();
        Iterator it = set.iterator();
        Integer id = (Integer) it.next();
        String d = facade.getDesignacaoComponente(id);
        builder.append(d);
        builder.append(" (");
        builder.append(id);
        builder.append(")");
        while(it.hasNext()) {
            builder.append(", ");
            id = (Integer) it.next();
            d = facade.getDesignacaoComponente(id);
            builder.append(d);
            builder.append(" (");
            builder.append(id);
            builder.append(")");
        }
        return builder.toString();
    }

    /**
     *  Atualiza o modelo da tabela de componentes obrigatórios, seleciona o primeiro elemento da tabela.
     */
    private void updateObrigatorios() {
        modelObr.setDataVector(facade.getComponentesObgConfig(), colunasComponentes);
        obrigatoriosTable.setRowSelectionInterval(0, 0);
    }

    /**
     *  Atualiza o modelo da tabela de componentes opcionais.
     */
    private void updateOpcionais() {
        modelOpc.setDataVector(facade.getComponentesOpcConfig(), colunasComponentes);
    }

    /**
     *  Atualiza o modelo da tabela de pacotes, desativa o botão de remover pacote.
     */
    private void updatePacotes() {
        try {
            modelPac.setDataVector(facade.getPacotesConfig(), colunasPacotes);
            removerPacoteButton.setEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
            JanelaUtil.mostrarJanelaErro(frame, "Não foi possível aceder à base de dados (Pacotes).");
        }
    }

    /**
     *  Atualiza o modelo da tabela de dependencias, desativa o botão de adicionar componente das dependências.
     */
    private void updateDependencias() {
        try {
            modelDep.setDataVector(facade.getComponentesDepConfig(), colunasComponentes);
            adicionarDepButton.setEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
            JanelaUtil.mostrarJanelaErro(frame, "Não foi possível aceder à base de dados (Dependencias).");
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        updateObrigatorios();
        updateOpcionais();
        updateDependencias();
        updatePacotes();
        precoLabel.setText("Preco: " + facade.getValor() + "€");
        descontoLabel.setText("Desconto: " + facade.getDesconto() + "€");
    }
}
