package business.gestao;

import business.produtos.Componente;
import business.produtos.Pacote;
import data.ComponenteDAO;
import data.PacoteDAO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;

public class Encomenda {
    private int id;
    private String cliente;
    private int nif;
    private int valor;
    private LocalDate data;
    private Collection<Componente> componentes;
    private Collection<Pacote> pacotes;

    public Encomenda(int id,
                     String cliente,
                     int nif,
                     int valor,
                     LocalDate data,
                     Collection<Componente> componentes,
                     Collection<Pacote> pacotes) {
        this.id = id;
        this.cliente = cliente;
        this.nif = nif;
        this.valor = valor;
        this.data = data;
        this.componentes = componentes;
        this.pacotes = pacotes;
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

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public Collection<Componente> getComponentes() throws SQLException {
        if(componentes == null){
            componentes = new ComponenteDAO().list(this);
        }
        return componentes;
    }

    public void setComponentes(Collection<Componente> componentes) {
        this.componentes = componentes;
    }

    public Collection<Pacote> getPacotes() throws SQLException {
        if(pacotes == null){
            pacotes = new PacoteDAO().list(this);
        }
        return pacotes;
    }

    public void setPacotes(Collection<Pacote> pacotes) {
        this.pacotes = pacotes;
    }
}
