package business.produtos;

public class PacoteDormente extends Pacote {
    private int numCompEmFalta;

    public PacoteDormente(Pacote p, int numCompEmFalta){
        super(p);
        numCompEmFalta = numCompEmFalta;
    }

    public void incr(){
        numCompEmFalta++;
    }

    public boolean decr() {
        numCompEmFalta--;
        return numCompEmFalta==0;
    }
    public int getNumCompEmFalta() {
        return numCompEmFalta;
    }
}
