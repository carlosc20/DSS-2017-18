package view;

import business.ConfiguraFacil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Inicial {

    private JTextField nomeField;
    private JPasswordField passwordField;
    private JButton entrarButton;
    private JPanel mainPanel;

    private ConfiguraFacil facade = ConfiguraFacil.getInstancia();

    public Inicial() {

        JFrame frame = new JFrame("ConfiguraFácil");
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300,200);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        entrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String nome =  nomeField.getText();
                String password = String.valueOf(passwordField.getPassword());

                try {
                    String cargo = facade.autenticar(nome, password);
                    // new JNovaEncomenda();
                    // TODO: 27/12/2018
                    switch (cargo) {
                        case "Administrador":
                            frame.dispose();
                            new JAdministrador();
                            break;
                        case "Vendedor":
                            frame.dispose();
                            new JVendedor();
                            break;
                        case "Repositor":
                            frame.dispose();
                            new JRepositor();
                            break;
                        default:
                            JOptionPane.showMessageDialog(frame,
                                    "Cargo não atribuído.",
                                    "Erro",
                                    JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e1) {
                    JOptionPane.showMessageDialog(frame,
                            "Dados incorretos, tente novamente.",
                            "Erro",
                            JOptionPane.ERROR_MESSAGE);
                    nomeField.requestFocusInWindow();
                }
            }
        });
        // TODO: enable/disable do botao conforme preenchimento dos fields
    }

}
