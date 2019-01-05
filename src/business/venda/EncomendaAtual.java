package business.venda;

import business.gestao.EncomendaEmProducao;
import business.gestao.Encomenda;
import business.gestao.EncomendaFinalizada;
import business.produtos.Componente;
import business.produtos.Pacote;
import business.venda.categorias.Categoria;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class EncomendaAtual extends Encomenda {
	private Configuracao configuracao;

	public EncomendaAtual(int id, String cliente, int nif) {
		super(id, cliente, nif, 0);
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
		this.setValor(configuracao.getValorConfiguracao());
		return pac;
	}

	public Set<Integer> removeComponente(int idComponente) throws ComponenteNaoExisteNaConfiguracao, SQLException {
		Set<Integer> pac = configuracao.removerComponente(idComponente);
		this.setValor(configuracao.getValorConfiguracao());
		return pac;
	}

	public Set<Integer> adicionaPacote(int idPacote) throws PacoteGeraConflitosException, SQLException {
		Set<Integer> pac =  configuracao.adicionarPacote(idPacote);
		this.setValor(configuracao.getValorConfiguracao());
		return pac;
	}

	public void removePacote(int idPacote) throws PacoteNaoExisteNaConfiguracaoException, SQLException {
		configuracao.removerPacote(idPacote);
		this.setValor(configuracao.getValorConfiguracao());
	}

	public Encomenda finalizarEncomenda() throws SQLException, FaltamDependentesException {
		Set<Componente> componentesEmFalta = configuracao.atualizaStock();
		if(componentesEmFalta.isEmpty()) {
			return new EncomendaFinalizada(this.getId(), this.getCliente(), this.getNif(), this.getValor(), LocalDate.now(), configuracao.getComponentes(), configuracao.getPacotes());
		} else {
			return new EncomendaEmProducao(this.getId(), this.getCliente(), this.getNif(), this.getValor(),  LocalDate.now(), configuracao.getComponentes(), configuracao.getPacotes(), componentesEmFalta);
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
	public List<Componente> getComponentesObrigatorios() {
		return configuracao.getComponentesObrigatórios();
	}
	public boolean dependentesEmFalta() throws FaltamDependentesException {
		return configuracao.dependentesEmFalta();
	}
	public boolean obrigatoriosEmFalta(List<Categoria> obr) throws FaltamComponenteObrigatorioException {
		return configuracao.obrigatoriosEmFalta(obr);
	}
	public int getDesconto(){
		return configuracao.getDesconto();
	}
	public List<Pacote> getPacotes(){
		return configuracao.getPacotes();
	}

	public Configuracao getConfiguracao() {
		return configuracao;
	}

	public void setConfiguracao(Configuracao configuracao) {
		this.configuracao = configuracao;
	}
}