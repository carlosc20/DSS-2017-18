package business.produtos;

import java.util.Set;

public class Pacote {
	private int id;
	private String designacao;
	private int desconto;
	private Set<Integer> componentes;

	public Pacote(int id, String designacao, int desconto, Set<Integer> componentes) {
		this.id = id;
		this.designacao = designacao;
		this.desconto = desconto;
		this.componentes = componentes;
	}

	public Set<Integer> getComponentes() {
		return componentes;
	}

	public int getId() {
		return id;
	}

	public String getDesignacao() {
		return designacao;
	}

	public int getDesconto() {
		return desconto;
	}
}