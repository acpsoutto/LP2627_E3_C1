import java.time.LocalDate;
import java.util.ArrayList;

public class AnuncioPerdido {
	private String titulo;
	private Utilizador utilizador;
	private Categoria categoria;
	private Estado estado;
	private String descricao;
	private LocalDate dataPublicacao;
	private String cor;
	private LocalDate dataResolucao;
	private ArrayList<Chat> chats;
	
	
	public AnuncioPerdido(Utilizador utilizador, String titulo, Categoria categoria, Estado estado, String descricao, LocalDate dataPublicacao, String cor) {
        this.utilizador = utilizador;
        this.titulo = titulo;
        this.categoria = categoria;
        this.estado = estado;
        this.descricao = descricao;
        this.dataPublicacao = dataPublicacao;
        this.cor = cor;
        this.chats = new ArrayList<>();
    }
	
	public String getTitulo() {
		return titulo;
	}
	
	public Utilizador getUtilizador() {
		return utilizador;
	}
	
	public Categoria getCategoria() {
		return categoria;
	}
	
	public Estado getEstado() {
		return estado;
	}
	
	
	public String getDescricao() {
		return descricao;
	}
	
	public LocalDate getDataResolucao() {
		return dataResolucao;
	}
	
	public String getCor() {
		return cor;
	}
	
	public LocalDate getDataPublicacao() {
		return dataPublicacao;
	}
	
	public ArrayList<Chat> getChats(){
		return chats;
	}
	
	public void setUtilizador(Utilizador utilizador) {
		this.utilizador = utilizador;
	}
	
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	
	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}
	
	public void setEstado(Estado estado) {
		this.estado = estado;
	}
	
	public void setDescricao (String descricao) {
		this.descricao = descricao;
	}
	
	public void setDataPublicacao(LocalDate dataPublicacao) {
		this.dataPublicacao = dataPublicacao;
	}
	
	public void setCor(String cor) {
		this.cor = cor;
	}
	
	public void setDataResolucao(LocalDate dataResolucao) {
		this.dataResolucao = dataResolucao;
	}
	
	public void setChats(ArrayList<Chat> chats) {
		this.chats = chats;
	}
	
	public void adicionarChat(Chat chat) {
        this.chats.add(chat);
    }
	
	
}
