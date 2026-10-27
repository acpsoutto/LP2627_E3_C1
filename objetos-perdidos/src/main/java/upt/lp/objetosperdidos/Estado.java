package upt.lp.objetosperdidos;

/**
 * Estados possíveis de um anúncio.
 * <p>
 * Um anúncio começa sempre como {@link #PENDENTE} e passa a {@link #RESOLVIDO}
 * É guardado na base de dados como texto.
 */
public enum Estado {
	/** O anúncio está ativo e o objeto ainda não foi devolvido ou recuperado. */
	PENDENTE, 
	/** O objeto foi devolvido ou recuperado, e o anúncio foi dado como concluído. */
	RESOLVIDO,
	/** O objeto foi devolvido ou recuperado, e o anúncio foi dado como apagado e resolvido . */
	APAGADO_RESOLVIDO,
	/** O anúncio está ativo e o objeto ainda não foi devolvido ou recuperado e resolvida. */
	APAGADO_PENDENTE
	
}
