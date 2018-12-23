package view;

import business.ConfiguraFacil;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JRepositor {

    private JPanel mainPanel;
    private JButton atualizarStockButton;
    private JTable pacotesTable;
    private JTable componentesTable;
    private JButton sairButton;

    ConfiguraFacil facade = ConfiguraFacil.getInstancia();

    public JRepositor() {
        JFrame frame = new JFrame("Repositor");
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // ?
        frame.pack(); // this.setSize(500,600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);



        // TODO: atualizar as tabelas, getPacotes, getComponentes

        atualizarStockButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "Ficheiros CSV", "csv");
                chooser.setFileFilter(filter);
                int returnVal = chooser.showOpenDialog(frame);
                if(returnVal == JFileChooser.APPROVE_OPTION)
                    try {
                        facade.atualizarStock(chooser.getSelectedFile()); // TODO: testar erros
                        JOptionPane.showMessageDialog(frame, "Stock atualizado com sucesso.", "Confirmação", JOptionPane.PLAIN_MESSAGE); // TODO: diagrama
                    } catch (Exception e1) {
                        JOptionPane.showMessageDialog(frame, "Falha ao atualizar stock.", "Erro", JOptionPane.ERROR_MESSAGE); // TODO: diagrama
                    }
            }
        });
        sairButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });
    }
}
