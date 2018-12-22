package view;

import business.ConfiguraFacil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JVendedor {

    private JPanel mainPanel;
    private JButton sairButton;
    private JButton criarEncomendaButton;
    private JTable filaProducaoTable;
    private JTable registoProduzidasTable;
    ConfiguraFacil facade = ConfiguraFacil.getInstancia();

    public JVendedor() {
        JFrame frame = new JFrame("Repositor");
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack(); // this.setSize(500,600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);


        sairButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        criarEncomendaButton.addActionListener(new ActionListener() {
            // TODO: acabar

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
