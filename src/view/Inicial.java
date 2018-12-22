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
    ConfiguraFacil facade = ConfiguraFacil.getInstancia();

    public Inicial() throws HeadlessException {
        JFrame frame = new JFrame("ConfiguraFácil");
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack(); // this.setSize(500,600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        entrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String nome =  nomeField.getText();
                String password = String.valueOf(passwordField.getPassword());

                try {
                    int cargo = facade.autenticar(nome, password);
                    switch (cargo) {
                        case 1:
                            new JAdministrador();
                            break;
                        case 2:
                            new JVendedor();
                            break;
                        case 3:
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
                } finally {
                    nomeField.requestFocusInWindow(); //??
                }
            }
        });
        // TODO: enable/disable do botao conforme preenchimento dos fields
    }

}
