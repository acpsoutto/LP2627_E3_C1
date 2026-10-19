package upt.lp.objetosperdidos;
	
import java.time.LocalDate;
import jakarta.persistence.*;

@Entity
@Table(name = "anuncio_perdido")
public class AnuncioPerdido extends Anuncio{
	
	@Column(nullable = false)
	private LocalDate dataPerda;

	public AnuncioPerdido() {
	}

	public AnuncioPerdido(Utilizador utilizador, String titulo, Categoria categoria, String descricao, String cor, LocalDate dataPerda) {
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
		return ""+ getIdAnuncio() +"-Procura-se: " + getTitulo() +" "+ getCor() +" " +getCategoria() + " perdido no " + dataPerda + " por "+ getUtilizador();
	}

}
