package view;

import business.ConfiguraFacil;

import javax.swing.*;

public class JNovaEncomenda {

    ConfiguraFacil facade = ConfiguraFacil.getInstancia();
    private JPanel mainPanel;

    public JNovaEncomenda() {
        JFrame frame = new JFrame("Repositor");
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack(); // this.setSize(500,600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
