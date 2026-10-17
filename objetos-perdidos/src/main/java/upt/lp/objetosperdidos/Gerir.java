package upt.lp.objetosperdidos;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

public class Gerir {
	
	private SessionFactory sessionFactory;

	public void setup() {
	    sessionFactory = new Configuration()
	            .configure("hibernate.cfg.xml")
	            .buildSessionFactory();
	}
	
	public List<Anuncio> filtrarAnuncios(int op, Categoria categoria, String cor,String titulo) {
		 String tipo;
		    if (op == 1) {
		        tipo = "AnuncioPerdido";
		    } else {
		        tipo = "AnuncioEncontrado";
		    }
		    String hql = "select a from " + tipo + " a join fetch a.publicador where a.estado = :estado";
		    if (categoria != null) {
		        hql = hql + " and a.categoria = :categoria";
		    }
		    if (cor != null && !cor.isEmpty()) {
		        hql = hql + " and lower(a.cor) like :cor";
		    }
		    if (titulo != null && !titulo.isEmpty()) {
		        hql = hql + " and lower(a.titulo) like :titulo";
		    }
		    hql = hql + " order by a.dataPublicacao desc";
		    try (Session session = sessionFactory.openSession()) {
		        Query<Anuncio> query = session.createQuery(hql, Anuncio.class);
		        query.setParameter("estado", Estado.Pendente);

		        if (categoria != null) {
		            query.setParameter("categoria", categoria);
		        }
		        if (cor != null && !cor.isEmpty()) {
		            query.setParameter("cor", "%" + cor.toLowerCase() + "%");
		        }
		        if (titulo != null && !titulo.isEmpty()) {
		            query.setParameter("titulo", "%" + titulo.toLowerCase() + "%");
		        }
		        return query.getResultList();
		    }
		}

		public void imprimirAnuncios(List<Anuncio> anuncios) {
		    if (anuncios.isEmpty()) {
		        System.out.println("Sem resultados.");
		    }
		    for (Anuncio a : anuncios) {
		        System.out.println(a);
		    }
		}

		public void revindicar(long anuncioID, long utiID) {
			Transaction transaction = null;

		        transaction = session.beginTransaction();
		        Anuncio anuncio = session.get(Anuncio.class, anuncioID);
		        Utilizador utilizador = session.get(Utilizador.class, utiID);

		        if (anuncio == null || utilizador == null) {
		            System.out.println("Anúncio ou utilizador não encontrado.");
		            transaction.rollback();
		            return null;
		        }

		        Utilizador dono = anuncio.getPublicador();
		        
		        if (anuncio.getEstado() == Estado.Resolvido) {
		            System.out.println("Este anúncio já foi resolvido.");
		            transaction.rollback();
		            return null;
		        }

		        List<Chat> existentes = session.createQuery("select c from Chat c join c.participantes p "
		                        + "where c.anuncio.id = :anuncioId and p.id = :utilizadorId", Chat.class)
		                .setParameter("anuncioId", anuncioId)
		                .setParameter("utilizadorId", utilizadorId)
		                .getResultList();

		        Chat chat = new Chat(anuncio);
		        chat.addParticipante(dono);
		        chat.addParticipante(utilizador);
		        anuncio.addChat(chat);

		        String text;
		        if (anuncio instanceof AnuncioEncontrado) {
		            text = "Olá, acho que encontrou o meu pertence!";
		        } else {
		            text = "Olá, eu acho que encontrei o seu pertence!";
		        }

		        Mensagem mensagem = new Mensagem(utilizador, texto);
		        chat.addMensagem(mensagem);

		        transaction.commit();
		        return chat;		
		}
	}
