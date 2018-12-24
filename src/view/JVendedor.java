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

        // TODO: atualizar tabelas

        sairButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });
        criarEncomendaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField nomeF = new JTextField();
                JTextField nifF = new JTextField();
                Object[] options = {
                        "Nome:", nomeF,
                        "Nif:", nifF,
                };

                int option = JOptionPane.showConfirmDialog(frame, options, "Dados do cliente", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (option == JOptionPane.OK_OPTION) {
                    String nome = nomeF.getText();
                    try {
                        int nif = Integer.parseInt(nifF.getText());
                        facade.criarEncomenda(nome, nif);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    new JNovaEncomenda();
                }
            }
        });
    }
}
