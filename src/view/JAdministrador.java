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

    private JFrame frame;

    private ConfiguraFacil facade = ConfiguraFacil.getInstancia();

    public JAdministrador() {

        frame = new JFrame("Administrador");
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500,600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        facade.addObserver(this);


        sairButton.addActionListener(new ActionListener() {
            /**
             *  Fecha a janela atual e abre a Inicial.
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new Inicial();
            }
        });

        //---------------- Utilizadores --------------------------------------------------------------------------------

        model = new DefaultListModel<>();
        updateModel();
        utilizadoresList.setModel(model);

        removerUtilizadorButton.addActionListener(new ActionListener() {
            /**
             *  Remove o utilizador selecionado do sistema.
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = utilizadoresList.getSelectedIndex();
                utilizadoresList.ensureIndexIsVisible(index);
                facade.removerUtilizador(utilizadores.get(index));
            }
        });

        String[] tiposUtilizador = ConfiguraFacil.tiposUtilizador;
        criarUtilizadorButton.addActionListener(new ActionListener() {
            /**
             *  Abre janela de criação de utilizador.
             *  OK -> Cria utilizador com os dados fornecidos.
             */
            @Override
            public void actionPerformed(ActionEvent e) {

                JTextField nomeF = new JTextField();
                JTextField passwordF = new JPasswordField();
                JComboBox<String> tiposF = new JComboBox<>(tiposUtilizador);
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
                    String tipo = (String) tiposF.getSelectedItem();
                    try {
                        facade.criarUtilizador(nome, password, tipo);
                    } catch (Exception e1) {
                        JOptionPane.showMessageDialog(frame,
                                "Tipo de utilizador não permitido.",
                                "Erro",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        utilizadoresList.addListSelectionListener(new ListSelectionListener() {
            /**
             * Desbloqueia/bloqueia o botão de remover quando está ou não selecionado um utilizador.
             */
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


    /**
     * Preenche o modelo da lista de funcionários com os nomes deles.
     */
    private void updateModel() {
        try {
            utilizadores = facade.getUtilizadores();
            for (String u : utilizadores) {
                model.addElement(u);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame,
                    "Não foi possível aceder à base de dados.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * Limpa o modelo da lista de funcionários e preenche-o.
     * É chamada sempre que há alterações na lista de funcionários.
     */
    @Override
    public void update(Observable o, Object arg) {
        this.model.clear();
        updateModel();
    }

}
