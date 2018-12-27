package business.venda;

import business.produtos.Componente;
import data.ComponenteDAO;
import data.PacoteDAO;
import javafx.util.Pair;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Set;

public class Encomenda {
	private int id;
	private String cliente;
	private int nif;
	private int valor;
	private LocalDate data;
	private Configuracao configuracao;

	public Encomenda() {}

	public Encomenda(int _id, String _cliente, int _nif, ComponenteDAO cDAO, PacoteDAO pDAO) {
		this.id = _id;
		this.cliente = _cliente;
		this.nif = _nif;
		this.valor = 0;
		this.configuracao = new Configuracao(cDAO, pDAO);
	}

	public Pair<Set<Integer>,Set<Integer>> getEfeitosAdicionarComponente(int idComponente) throws ComponenteJaExisteNaConfiguracaoException, SQLException {
		return configuracao.getEfeitosSecundariosAdicionarComponente(idComponente);
	}
	public Pair<Set<Integer>,Set<Integer>> getEfeitosAdicionarPacote(int idPacote) throws PacoteJaExisteNaConfiguracaoException, SQLException, PacoteGeraConflitosException {
		return configuracao.getEfeitosSecundariosAdicionarPacote(idPacote);
	}

	public Set<Integer> adicionaComponente(int idComponente) throws SQLException {
		Pair <Integer,Set<Integer>> temp =  configuracao.adicionarComponente(idComponente);
		this.valor += temp.getKey();
		return temp.getValue();
	}

	public Set<Integer> removeComponente(int idComponente) throws ComponenteNaoExisteNaConfiguracao, SQLException {
		Pair <Integer,Set<Integer>> temp =  configuracao.adicionarComponente(idComponente);
		this.valor += temp.getKey();
		return temp.getValue();
	}

	public Set<Integer> adicionaPacote(int idPacote) throws PacoteGeraConflitosException, SQLException {
		Pair <Integer,Set<Integer>> temp =  configuracao.adicionarPacote(idPacote);
		this.valor += temp.getKey();
		return temp.getValue();
	}

	public void removePacote(int idPacote) throws PacoteNaoExisteNaConfiguracaoException, SQLException {
		Pair <Integer,Set<Integer>> temp =  configuracao.adicionarPacote(idPacote);
		this.valor += temp.getKey();
		//return temp.getValue();
	}
	public Set<Integer> finalizarEncomenda() throws SQLException, FaltamDependentesException {
		setData(LocalDate.now());
		return configuracao.atualizaStock();
	}
	public void configuracaoOtima() {
		throw new UnsupportedOperationException();
	}

	public Set<Componente> getComponentes(){
		return configuracao.getComponentes();
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

	public void setValor(int valor) {
		this.valor = valor;
	}

	public Configuracao getConfiguracao() {
		return configuracao;
	}

	public void setData(LocalDate data) {
		this.data = data;
	}

	public void setConfiguracao(Configuracao configuracao) {
		this.configuracao = configuracao;
	}

    public LocalDate getData() {
        return data;
    }
}