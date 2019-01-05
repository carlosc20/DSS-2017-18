package business.gestao;

import business.produtos.Componente;
import business.produtos.Pacote;
import data.ComponenteDAO;
import data.PacoteDAO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;


public class EncomendaFinalizada extends Encomenda {
    private LocalDate data;
    private Collection<Componente> componentes;
    private Collection<Pacote> pacotes;

    public EncomendaFinalizada () {
        super();
        this.componentes = new HashSet<>();
        this.pacotes = new HashSet<>();
    }

    public EncomendaFinalizada(int id,
                               String cliente,
                               int nif,
                               int valor,
                               LocalDate data,
                               Collection<Componente> componentes,
                               Collection<Pacote> pacotes) {
        super(id, cliente, nif, valor);
        this.data = data;
        this.componentes = componentes;
        this.pacotes = pacotes;
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

    public LocalDate getData() {
        return data;
    }
}
