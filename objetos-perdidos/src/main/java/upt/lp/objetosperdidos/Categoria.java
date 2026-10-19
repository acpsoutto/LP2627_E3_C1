package upt.lp.objetosperdidos;

/**
 * Categorias em que um objeto perdido ou encontrado pode ser classificado.
 * <p>
 * É usada na criação de anúncios e como filtro na pesquisa de anúncios
 * É guardada na base de dados como texto.
 */
public enum Categoria {
	/** Objetos eletrónicos (telemóveis, auscultadores, carregadores, etc.). */
	ELETRONICOS,
	/** Livros e cadernos de apontamentos. */	
	LIVROS,
	/** Material de escrita (canetas, lápis, estojos, etc.). */
	MATERIAL_ESCRITA,
	/** Peças de roupa e acessórios. */
	ROUPA
	
}
