package business.produtos;

import java.util.Comparator;

public class ComparaPacotesByDesconto implements Comparator<Pacote> {
    public int compare(Pacote p1, Pacote p2){
        float d1 = p1.getDesconto();
        float d2 = p2.getDesconto();
        if(d1==d2) return 0;
        if(d1>d2) return 1; //confirmar se está crescente
        return -1;
    }
}
