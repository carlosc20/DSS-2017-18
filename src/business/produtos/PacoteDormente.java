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

    @Override
    public boolean equals(Object o){
        if (this == o)
            return true;

        if ((o==null) || (this.getClass() != o.getClass()))
            return false;

        PacoteDormente p = (PacoteDormente) o;
        return super.equals(p);
    }
}
