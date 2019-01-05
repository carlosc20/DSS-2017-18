package business.gestao;

import java.time.LocalDate;

public abstract class Encomenda {
    private int id;
    private String cliente;
    private int nif;
    private int valor;

    public Encomenda(){

    }

    public Encomenda(int id,
                     String cliente,
                     int nif,
                     int valor) {
        this.id = id;
        this.cliente = cliente;
        this.nif = nif;
        this.valor = valor;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public int getNif() {
        return nif;
    }

    public void setNif(int nif) {
        this.nif = nif;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }

}