package view;

import business.ConfiguraFacil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class JNovaEncomenda {

    private JPanel mainPanel;
    private JButton configOtimaButton;
    private JButton finalizarButton;
    private JButton adicionarPacoteButton;
    private JTable obrigatoriosTable;
    private JTable dependenciasTable;
    private JTable opcionaisTable;
    private JButton adicionarCategoriaButton;

    private ConfiguraFacil facade = ConfiguraFacil.getInstancia();

    public JNovaEncomenda() {

        JFrame frame = new JFrame("Nova encomenda");
        frame.setContentPane(mainPanel);
        frame.setSize(500,600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

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
                facade.criarConfiguracaoOtima();
            }
        });

        adicionarPacoteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel model;
                JTable table = new JTable();
                int option = JOptionPane.showConfirmDialog(frame,
                        new JScrollPane(table),
                        "Dados do cliente",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE);
                if (option == JOptionPane.OK_OPTION) {

                }
            }
        });

        adicionarCategoriaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    private void adicionarComponente() {
        JTextField nomeF = new JTextField();
        JTextField passwordF = new JPasswordField();
        List<String> list = facade.getFuncionarios();
        String[] tipos = list.toArray(new String[0]);
        JComboBox<String> tiposF = new JComboBox<>(tipos);
        Object[] options = {
                "Nome:", nomeF,
                "Password:", passwordF,
                "Tipo", tiposF
        };

        int option = JOptionPane.showConfirmDialog(null, options, "Criar utilizador", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {

        }
    }
}
