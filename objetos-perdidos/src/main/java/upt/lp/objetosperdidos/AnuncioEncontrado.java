package upt.lp.objetosperdidos;

public class AnuncioEncontrado extends Anuncio {
	
	private String localizacao;

	public AnuncioEncontrado() {
		
	}

	public AnuncioEncontrado(Utilizador utilizador, String titulo, Categoria categoria, String descricao, String cor, String localizacao) {
		super(utilizador, titulo, categoria, descricao, cor);
		this.localizacao = localizacao;
	}

	public String getLocalizacao() {
		return localizacao;
	}

	public void setLocalizacao(String localizacao) {
		this.localizacao = localizacao;
	}

	@Override
	public String toString() {
		return ""+ getIdAnuncio() +"-Encontrou-se: " + getTitulo() +" "+ getCor() +" " +getCategoria() + " encontrado em " + localizacao + " por "+ getUtilizador();
	}
}
