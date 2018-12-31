package business.venda;

import business.gestao.EncomendaEmProducao;
import business.gestao.Encomenda;
import business.produtos.Componente;
import business.produtos.Pacote;
import business.venda.categorias.Categoria;
import data.CategoriaDAO;
import data.ComponenteDAO;
import javafx.util.Pair;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class EncomendaAtual {
	private int id;
	private String cliente;
	private int nif;
	private int valor;
	private Configuracao configuracao;

	public EncomendaAtual(int id, String cliente, int nif) {
		this.id = id;
		this.cliente = cliente;
		this.nif = nif;
		this.valor = 0;
		this.configuracao = new Configuracao();
	}

	public Set<Integer> getIncompatibilidades(int idComponente) throws ComponenteJaExisteNaConfiguracaoException, SQLException {
		return configuracao.getIncompatibilidadesComponente(idComponente);
	}
	public Set<Integer> getIncompatibilidadesPacote(int idPacote) throws PacoteJaExisteNaConfiguracaoException, SQLException, PacoteGeraConflitosException {
		return configuracao.getIncompatibilidadesPacote(idPacote);
	}

	public Set<Integer> adicionaComponente(int idComponente) throws SQLException {
		Set<Integer> pac =  configuracao.adicionarComponente(idComponente);
		this.valor = configuracao.getValorConfiguracao();
		return pac;
	}

	public Set<Integer> removeComponente(int idComponente) throws ComponenteNaoExisteNaConfiguracao, SQLException {
		Set<Integer> pac = configuracao.removerComponente(idComponente);
		this.valor = configuracao.getValorConfiguracao();
		return pac;
	}

	public Set<Integer> adicionaPacote(int idPacote) throws PacoteGeraConflitosException, SQLException {
		Set<Integer> pac =  configuracao.adicionarPacote(idPacote);
		this.valor = configuracao.getValorConfiguracao();
		return pac;
	}

	public void removePacote(int idPacote) throws PacoteNaoExisteNaConfiguracaoException, SQLException {
		configuracao.removerPacote(idPacote);
		this.valor = configuracao.getValorConfiguracao();
	}

	public Encomenda finalizarEncomenda() throws SQLException, FaltamDependentesException {
		Set<Componente> componentesEmFalta = configuracao.atualizaStock();
		if(componentesEmFalta.isEmpty()) {
			return new Encomenda(id, cliente, nif, valor, LocalDate.now(), configuracao.getComponentes(), configuracao.getPacotes());
		} else {
			return new EncomendaEmProducao(id, cliente, nif, valor, LocalDate.now(), configuracao.getComponentes(), configuracao.getPacotes(), componentesEmFalta);
		}
	}

	public boolean configuracaoOtima(Map<Categoria, Integer> precoMaximoCategorias, int precoMaximoTotal) throws SQLException {
		return configuracao.configuracaoOtima(precoMaximoCategorias, precoMaximoTotal);
	}

	public List<Componente> getComponentes(){
		return configuracao.getComponentes();
	}

	public Set<Integer> getDependentes(){return configuracao.getDependentes();}

	public List<Componente> getComponetesOpcionais() {
		return configuracao.getComponentesOpcionais();
	}
	public List<Componente> getComponetesObrigatorios() {
		return configuracao.getComponentesObrigatórios();
	}

	public int getDesconto(){
		return configuracao.getDesconto();
	}
	public List<Pacote> getPacotes(){
		return configuracao.getPacotes();
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

	public Configuracao getConfiguracao() {
		return configuracao;
	}

	public void setConfiguracao(Configuracao configuracao) {
		this.configuracao = configuracao;
	}
}