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
            /**
             * Abre a janela do menu correspondente ao tipo do utilizador cujos dados foram inseridos.
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                String nome =  nomeField.getText();
                String password = String.valueOf(passwordField.getPassword());
                try {
                    String cargo = facade.autenticar(nome, password);
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
                            JanelaUtil.mostrarJanelaErro(frame, "Cargo não atribuído.");
                    }
                } catch (Exception e1) {
                    JanelaUtil.mostrarJanelaErro(frame, "Dados incorretos.");
                    nomeField.requestFocusInWindow();
                    e1.printStackTrace();
                }
            }
        });
    }

}
