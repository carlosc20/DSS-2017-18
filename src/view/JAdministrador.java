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
                List<String> list = facade.getFuncionarios();
                String[] tipos = list.toArray(new String[0]);
                JComboBox<String> tiposF = new JComboBox<>(tipos);
                Object[] options = {
                        "Nome:", nomeF,
                        "Password:", passwordF,
                        "Tipo", tiposF
                };

                int option = JOptionPane.showConfirmDialog(frame, options, "Criar utilizador", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (option == JOptionPane.OK_OPTION) {
                    String nome = nomeF.getText();
                    String password = passwordF.getText();
                    String tipo = (String) tiposF.getSelectedItem(); //indice do array tipos
                    System.out.println(tipo);
                    try {
                        facade.criarUtilizador(nome, password, tipo);
                    } catch (Exception e1) {
                        JOptionPane.showMessageDialog(frame, "Erro ao criar utilizador.", "Erro", JOptionPane.ERROR_MESSAGE); // TODO: informaçao sobre erro
                    }
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
