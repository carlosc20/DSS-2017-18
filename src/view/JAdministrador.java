package view;

import business.ConfiguraFacil;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class JAdministrador implements Observer {

    private JPanel mainPanel;
    private JButton sairButton;
    private JButton criarUtilizadorButton;
    private JButton removerUtilizadorButton;

    private JList<String> utilizadoresList;
    private DefaultListModel<String> model;
    private List<String> utilizadores;

    private ConfiguraFacil facade = ConfiguraFacil.getInstancia();

    public JAdministrador() {

        JFrame frame = new JFrame("Administrador");
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500,600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        facade.addObserver(this);

        model = new DefaultListModel<>();
        updateModel();
        utilizadoresList.setModel(model);

        // fecha a janela, abre a inicial
        sairButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new Inicial();
            }
        });

        removerUtilizadorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = utilizadoresList.getSelectedIndex();
                utilizadoresList.ensureIndexIsVisible(index);
                facade.removerUtilizador(utilizadores.get(index));
            }
        });

        // abre janela para introduzir dados de utilizador
        criarUtilizadorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField nomeF = new JTextField();
                JTextField passwordF = new JPasswordField();
                String[] tipos = facade.getTiposFuncionarios().toArray(new String[0]);
                JComboBox<String> tiposF = new JComboBox<>(tipos);
                Object[] options = {
                        "Nome:", nomeF,
                        "Password:", passwordF,
                        "Tipo", tiposF
                };

                int option = JOptionPane.showConfirmDialog(frame, options,
                        "Criar utilizador",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE);

                if (option == JOptionPane.OK_OPTION) {
                    String nome = nomeF.getText();
                    String password = passwordF.getText();
                    String tipo = (String) tiposF.getSelectedItem(); //indice do array tipos
                    try {
                        facade.criarUtilizador(nome, password, tipo);
                    } catch (Exception e1) {
                        JOptionPane.showMessageDialog(frame,
                                "Erro ao criar utilizador.", // TODO: informaçao sobre erro
                                "Erro",
                                JOptionPane.ERROR_MESSAGE);
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

    private void updateModel() {
        utilizadores = facade.getFuncionarios();
        for (String u : utilizadores) {
            model.addElement(u);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        this.model.clear();
        updateModel();
    }

}
