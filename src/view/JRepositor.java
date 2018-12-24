package view;

import business.ConfiguraFacil;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

public class JRepositor implements Observer {

    private JPanel mainPanel;
    private JButton atualizarStockButton;
    private JTable pacotesTable;
    private JTable componentesTable;
    private JButton sairButton;

    ConfiguraFacil facade = ConfiguraFacil.getInstancia();

    public JRepositor() {

        JFrame frame = new JFrame("Repositor");
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500,600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        facade.addObserver(this);

        // TODO: atualizar as tabelas, getPacotes, getComponentes
        updatePacotes();
        updateComponentes();


        // fecha a janela, abre a inicial
        sairButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new Inicial();
            }
        });

        // abre janela para escolher ficheiro csv
        atualizarStockButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "Ficheiros CSV", "csv");
                chooser.setFileFilter(filter);
                int returnVal = chooser.showOpenDialog(frame);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    try {
                        facade.atualizarStock(chooser.getSelectedFile());
                        JOptionPane.showMessageDialog(frame,
                                "Stock atualizado com sucesso.",
                                "Confirmação",
                                JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception e1) {
                        JOptionPane.showMessageDialog(frame,
                                "Falha ao atualizar stock.", // TODO: informaçao sobre erro
                                "Erro",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });


    }

    private void updateComponentes() {
        //facade.getComponentes();
    }

    private void updatePacotes() {
        //facade.getPacotes();
    }

    @Override
    public void update(Observable o, Object arg) {
        // TODO: limpar tabelas
        updatePacotes();
        updateComponentes();
    }
}
