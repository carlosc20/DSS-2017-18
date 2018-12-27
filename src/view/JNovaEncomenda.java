package view;

import business.ConfiguraFacil;
import business.venda.categorias.Categoria;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class JNovaEncomenda implements Observer {

    private JPanel mainPanel;
    private JButton configOtimaButton;
    private JButton finalizarButton;
    private JButton adicionarPacoteButton;
    private JButton adicionarCategoriaButton;
    private JButton adicionarComponenteButton;

    private JTable obrigatoriosTable;
    private DefaultTableModel modelObr;

    private JTable dependenciasTable;
    private DefaultTableModel modelDep;

    private JTable opcionaisTable;
    private JButton adicionarComponenteButton1;
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

        //atualiza tabelas
        modelObr = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        obrigatoriosTable.setModel(modelObr);

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
            }
        });

        configOtimaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: 27/12/2018 acabar
                facade.criarConfiguracaoOtima();
            }
        });

        // abre janela de adcionar componente obrigatório da categoria selecionada
        adicionarComponenteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String cat = (String) obrigatoriosTable.getValueAt(obrigatoriosTable.getSelectedRow(), 0);
                adicionaComponente(frame, cat);
            }
        });

        // abre janela para escolher pacote
        adicionarPacoteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] columnNames = facade.getColunasPacotes();
                Object[][] data = facade.getPacotes();
                DefaultTableModel model = new DefaultTableModel(data, columnNames) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };
                JTable table = new JTable(model);

                int option = JOptionPane.showConfirmDialog(frame,
                        new JScrollPane(table),
                        "Escolher pacote",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE);

                if (option == JOptionPane.OK_OPTION) {

                }
            }
        });

        // abre janela para escolher categoria, se OK abre janela de adicionar componente
        adicionarCategoriaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                DefaultListModel<String> model = new DefaultListModel<>();
                for (String u : catOpcionais) {
                    model.addElement(u);
                }
                JList<String> list = new JList<>(model);

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


    }

    /**
     * Abre janela para adicionar componente da categoria indicada
     *
     * @param categoria     categoria do componente a adicionar
     */
    private void adicionaComponente(JFrame frame, String categoria) {

        String[] columnNames = facade.getColunasComponentes();
        Object[][] data = facade.getComponentes(categoria);

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        int option = JOptionPane.showConfirmDialog(frame,
                new JScrollPane(table),
                "Escolher componente",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);


        if (option == JOptionPane.OK_OPTION) {
            int id = (int) model.getValueAt(table.getSelectedRow(), 0);
            facade.adicionaComponente(id);
        }
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
