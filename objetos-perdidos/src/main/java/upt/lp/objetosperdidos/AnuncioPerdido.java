package upt.lp.objetosperdidos;
	
	import java.time.LocalDate;
	
public class AnuncioPerdido extends Anuncio{
	
	private LocalDate dataPerda;

	public AnuncioPerdido() {
	}

	public AnuncioPerdido(Utilizador utilizador, String titulo, Categoria categoria, String descricao, String cor) {
		super(utilizador, titulo, categoria, descricao, cor);
		this.dataPerda = dataPerda;
	}

	public LocalDate getDataPerda() {
		return dataPerda;
	}

	public void setDataPerda(LocalDate dataPerda) {
		this.dataPerda = dataPerda;
	}

	@Override
	public String toString() {
		return "AnuncioPerdido [dataPerda=" + dataPerda + ", getIdAnuncio()=" + getIdAnuncio() + ", getTitulo()="
				+ getTitulo() + ", getUtilizador()=" + getUtilizador() + ", getCategoria()=" + getCategoria()
				+ ", getEstado()=" + getEstado() + ", getCor()=" + getCor() + "]";
	}
	
	

	

	
	
	
	
	
}
