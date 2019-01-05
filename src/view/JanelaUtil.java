package view;

import javax.swing.*;

public class JanelaUtil {

    public static int OK = JOptionPane.OK_OPTION;

    public static void mostraJanelaErro(JFrame frame, String descricao) {
        JOptionPane.showMessageDialog(frame,
                descricao,
                "Erro",
                JOptionPane.ERROR_MESSAGE);
    }

    public static int mostraJanelaOpcoes(JFrame frame, String titulo, Object[] opcoes) {
        return JOptionPane.showConfirmDialog(frame, opcoes,
                titulo,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
    }

    public static int mostraJanelaTabela(JFrame frame, String titulo, JTable tabela) {
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setRowSelectionInterval(0, 0);
        return JOptionPane.showConfirmDialog(frame,
                new JScrollPane(tabela),
                titulo,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
    }

    public static int mostraJanelaLista(JFrame frame, String titulo, JList lista) {
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lista.setSelectedIndex(0);
        return JOptionPane.showConfirmDialog(frame,
                new JScrollPane(lista),
                titulo,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
    }

    public static void mostraJanelaInformacao(JFrame frame, String mensagem) {
        JOptionPane.showMessageDialog(frame,
                mensagem,
                "Informação",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
