package upt.lp.objetosperdidos;

public class AnuncioEncontrado extends Anuncio {
	
	private String localizacao;

	public AnuncioEncontrado() {
		
	}

	public AnuncioEncontrado(Utilizador utilizador, String titulo, Categoria categoria, String descricao, String cor) {
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
		return "AnuncioEncontrado [localizacao=" + localizacao + ", getIdAnuncio()=" + getIdAnuncio() + ", getTitulo()="
				+ getTitulo() + ", getUtilizador()=" + getUtilizador() + ", getCategoria()=" + getCategoria()
				+ ", getEstado()=" + getEstado() + ", getCor()=" + getCor() + "]";
	}
	
	
	
	
}
