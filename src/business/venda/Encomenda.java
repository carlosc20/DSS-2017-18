package business.venda;


import business.venda.excecoes.*;
import data.ComponenteDAO;
import data.PacoteDAO;

import java.util.Set;

public class Encomenda {
	private int id;
	private String cliente;
	private int nif;
	private float valor;
	private Configuracao configuracao;

	public Encomenda() {}

	public Encomenda(int _id, String _cliente, int _nif, ComponenteDAO cDAO, PacoteDAO pDAO) {
		this.id = _id;
		this.cliente = _cliente;
		this.nif = _nif;
		this.valor = 0;
		this.configuracao = new Configuracao(cDAO, pDAO);
	}
/*
	public Set<Integer> veIncompatibilidades() {

	}
*/
	public void resolveIncompatibilidades(int aId) {
		throw new UnsupportedOperationException();
	}

	public void adicionaComponente(int idComponente) throws ComponenteJaExisteNaConfiguracao {
		this.valor = configuracao.adicionarComponente(idComponente);
	}

	public void removeComponente(int idComponente) throws ComponenteNaoExisteNaConfiguracao {
		this.valor = configuracao.removerComponente(idComponente);
	}

	public void adicionaPacote(int idPacote) throws PacoteJaExisteNaConfiguracao, PacoteGeraConflitos {
		this.valor = configuracao.adicionarPacote(idPacote);
	}

	public void removePacote(int idPacote) throws PacoteNaoExisteNaConfiguracao {
		this.valor = configuracao.removerPacote(idPacote);
	}

	public void configuracaoOtima() {
		throw new UnsupportedOperationException();
	}

	public void otimizaPacotes() {
		throw new UnsupportedOperationException();
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

	public float getValor() {
		return valor;
	}

	public void setValor(float valor) {
		this.valor = valor;
	}

	public Configuracao getConfiguracao() {
		return configuracao;
	}

	public void setConfiguracao(Configuracao configuracao) {
		this.configuracao = configuracao;
	}
}