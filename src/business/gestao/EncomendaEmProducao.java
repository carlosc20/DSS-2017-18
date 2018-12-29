package business.gestao;

import business.produtos.Componente;
import business.produtos.Pacote;
import data.ComponenteDAO;
import data.EncomendaDAO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class EncomendaEmProducao extends Encomenda {
	public Collection<Componente> componentesEmFalta;

	public EncomendaEmProducao (int id,
								String cliente,
								int nif,
								int valor,
								LocalDate data,
								Collection<Componente> componentes,
								Collection<Pacote> pacotes,
								Collection<Componente> componentesEmFalta) {
		super(id, cliente, nif, valor, data, componentes, pacotes);
		this.componentesEmFalta = componentesEmFalta;
	}

	public Collection<Componente> getComponentesEmFalta() throws SQLException {
		if(componentesEmFalta == null){
			componentesEmFalta = new ComponenteDAO().listComponentesEmFalta(this);
		}
		return componentesEmFalta;
	}

	public void setComponentesEmFalta(Collection<Componente> componentesEmFalta) {
		this.componentesEmFalta = componentesEmFalta;
	}

	public void fornecerComponentes(List<Componente> componentes) throws SQLException {
		this.componentesEmFalta = new ComponenteDAO().atualizaStock(componentes);
		if(this.componentesEmFalta.isEmpty()) {
			new EncomendaDAO().add(this);
		}
	}

	public boolean getFinalizada(){
		return false;
	}
}