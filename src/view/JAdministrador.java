package view;

import business.ConfiguraFacil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JAdministrador {
    ConfiguraFacil facade = ConfiguraFacil.getInstancia();
    private JPanel mainPanel;
    private JButton sairButton;
    private JButton criarUtilizadorButton;
    private JButton removerUtilizadorButton;
    private JList utilizadoresList;

    public JAdministrador() {
        JFrame frame = new JFrame("ConfiguraFácil");
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack(); // this.setSize(500,600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // TODO: atualizar lista

        sairButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: acabar
            }
        });
        removerUtilizadorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: remover
                // facade.removerUtilizador();
            }
        });
        criarUtilizadorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField nomeF = new JTextField();
                JTextField passwordF = new JPasswordField();
                JTextField tipoF = new JTextField();
                Object[] options = {
                        "Nome:", nomeF,
                        "Password:", passwordF,
                        "Tipo", tipoF
                };

                int option = JOptionPane.showConfirmDialog(frame, options, "Criar utilizador", JOptionPane.OK_CANCEL_OPTION);

                if (option == JOptionPane.OK_OPTION) {
                    String nome = nomeF.getText();
                    String password = passwordF.getText();
                    int tipo = Integer.parseInt(tipoF.getText());
                    // TODO: erros
                    facade.criarUtilizador(nome, password, tipo);
                }
            }
        });
    }
}
