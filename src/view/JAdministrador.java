package view;

import business.ConfiguraFacil;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class JAdministrador {

    private JPanel mainPanel;
    private JButton sairButton;
    private JButton criarUtilizadorButton;
    private JButton removerUtilizadorButton;
    private JList<String> utilizadoresList;
    private DefaultListModel<String> model;

    private List<String> utilizadores;

    private ConfiguraFacil facade = ConfiguraFacil.getInstancia();

    public JAdministrador() {

        JFrame frame = new JFrame("ConfiguraFácil");
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack(); // this.setSize(500,600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        model = new DefaultListModel<>();
        utilizadoresList.setModel(model);
        utilizadoresList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        utilizadoresList.setLayoutOrientation(JList.VERTICAL);


        sairButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });

        removerUtilizadorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // TODO: o que faz?
                int index = utilizadoresList.getSelectedIndex();
                utilizadoresList.setSelectedIndex(index);
                utilizadoresList.ensureIndexIsVisible(index);

                facade.removerUtilizador(utilizadores.get(index));
            }
        });

        criarUtilizadorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField nomeF = new JTextField();
                JTextField passwordF = new JPasswordField();
                JTextField tipoF = new JTextField(); // TODO: trocar por checkboxes ou slider, fazer getTipos?
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

        // desbloqueia/bloqueia o botão de remover quando existem/não existem membros
        utilizadoresList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if(!e.getValueIsAdjusting()) {
                    if(utilizadoresList.getSelectedIndex() == -1){
                        removerUtilizadorButton.setEnabled(false);
                    } else {
                        removerUtilizadorButton.setEnabled(true);
                    }
                }
            }
        });
    }


    public void update() {
        this.model.clear();

        utilizadores = facade.getFuncionarios();
        // TODO: coisas
        if(utilizadores.size() < 1) {
            removerUtilizadorButton.setEnabled(false);
        } else {
            for (String u : utilizadores) {
                model.addElement(u);
            }
        }
    }

}
