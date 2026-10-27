package upt.lp.objetosperdidos;

import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * Classe principal da aplicação de objetos perdidos: contém o {@code main} e os
 * menus de consola.
 * <p>
 * O programa começa num menu de login, onde o utilizador se regista ou se autentica.
 * Depois do login, o menu depende do tipo de utilizador:
 * <ul>
 *   <li>{@link TipoUtilizador#NORMAL}: pesquisa, criação e eliminação de anúncios,
 *       reivindicação de objetos, chats e dados pessoais;</li>
 *   <li>{@link TipoUtilizador#GESTOR}: consulta de estatísticas.</li>
 * </ul>
 * Toda a lógica e o acesso à base de dados estão na classe {@link Gerir}. Esta classe
 * só lê as opções do utilizador e chama os métodos de {@link Gerir}.
 */
public class Teste {

	/**
	 * Ponto de entrada do programa.
	 * <p>
	 * Fluxo geral:
	 * <ol>
	 *   <li>prepara a ligação à base de dados com {@link Gerir#setup()};</li>
	 *   <li>mostra o menu de login (novo utilizador, utilizador existente ou fechar);</li>
	 *   <li>no registo, pede nome, email (repete o pedido enquanto o formato for inválido)
	 *       e password (repete o pedido enquanto não cumprir as regras);</li>
	 *   <li>no login, permite até 3 tentativas e depois volta ao menu de login;</li>
	 *   <li>depois de autenticado, mostra o menu de utilizador ou o menu de gestor,
	 *       consoante o tipo, até o utilizador fazer logout;</li>
	 *   <li>ao fechar o programa, termina a ligação com {@link Gerir#exit()}.</li>
	 * </ol>
	 * Ao criar um anúncio, o programa mostra primeiro os anúncios do tipo oposto que
	 * possam corresponder, para o utilizador poder reivindicar um deles em vez de criar
	 * um anúncio novo.
	 *
	 * @param args argumentos da linha de comandos (não são usados)
	 */
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		Gerir gr = new Gerir();
		gr.setup();
		int opL= 1;
		
        //login
		while(opL != 0) {
			System.out.println("Login:");
			System.out.println("1- Novo utilizador.");
			System.out.println("2- Utilizador existente.");
			System.out.println("0- Fechar programa");
			System.out.println("Opção");
			opL = inserirInt(sc);
			
			if(opL == 0) {
				break;
			}
			if(opL < 1 || opL > 2 ) {
				System.out.println("Opção Invalida!");
				continue;
			}
			
			switch (opL) {
			case 1:
					System.out.print("Nome: ");
					String nome = sc.nextLine();
					if(nome.trim().isEmpty()){
						System.out.println("ERRO: Campo preenchido incorretamente!");
			        	continue;
			        }

					System.out.print("Email: ");
					String email = sc.nextLine();

			        while (!gr.emailValido(email)) {
			            System.out.println("Erro: Formato de email inválido (exemplo: nome@dominio.pt).");
			            System.out.print("Email: ");
			            email = sc.nextLine();
			        }

					if (gr.procurarPorEmail(email) != null) {
						System.out.println("Erro: Este email já está registado no sistema!");
						continue;
					}
					boolean registado = false;
					while (!registado) {
						System.out.print("Palavra-Passe (mínimo 1 maiúscula e 1 carácter especial): ");
						String pass = sc.nextLine();
						try {
							gr.registrar(nome, email, pass);
							System.out.println("Utilizador registado com sucesso! Faça agora o seu login.");
							registado = true;
						} catch (Exception e) {
							System.out.println("Erro: " + e.getMessage());
						}
					}
			continue;
			case 2:
				System.out.print("Email: ");
				email = sc.nextLine();

				boolean autenticado = false;
				for (int tentativa = 1; tentativa <= 3; tentativa++) {
					System.out.print("Palavra-Passe: ");
					String pass = sc.nextLine();
					try {
						gr.autenticar(email, pass);
						autenticado = true;
						System.out.println("Login efetuado com sucesso! Bem-vindo(a), " + gr.getPerfil().getNome());
						break;
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
				}

				if (!autenticado) {
					continue; // Se falhou as 3 tentativas, regressa ao menu de Login
				}
			break;
			}
			
			 //Menu de utilizador
			int op = 1;
			if(gr.getPerfil().getTipo() != TipoUtilizador.GESTOR) {
				while(op != 0) {
					System.out.println("Menu");
					System.out.println("1- Mostrar anuncios de Procura-se.");
					System.out.println("2- Mostrar anunicos de Encontrados.");
					System.out.println("3- Criar anuncio.");
					System.out.println("4- Visualizar mensagens.");
					System.out.println("5- Ver e Atualizar os meus dados pessoais.");
					System.out.println("6- Eliminar anúncio");
					System.out.println("0- Logout.");
					System.out.println("Opção: ");
						
					op = inserirInt(sc);
						
					switch(op) {
					case 1:
					case 2:
							
						gr.imprimirAnuncios(gr.filtrarAnuncios(op,null,null,null));
						int opt = 2;
						
						while(opt == 2) {
							System.out.println("");
							System.out.println("1- Reivindicar Anuncio");
							System.out.println("2- Filtrar Pesquisa");
							System.out.println("Outra opção- Voltar");
							System.out.println("Opção");
							opt = sc.nextInt();
						
						if(opt == 1) {
							System.out.println("Qual anuncio quer reivindicar:");
							int Rid = inserirInt(sc);
							gr.reivindicar(Rid, gr.getPerfil().getIdUtilizador(), op);
						}
						if(opt == 2){
							Categoria categoriaFiltro = null;
							while(true) {
								System.out.println("Categoria (Enter para não inserir):");
								System.out.println("1-Eletronicos | 2-Livros | 3-Material Escrita | 4-Roupa | 5-Outros");
								System.out.println("Categoria (0 para não inserir):");
		
								int catnum = inserirInt(sc);
								switch(catnum) {
								case 1:
									categoriaFiltro = Categoria.ELETRONICOS;
								break;
								case 2:
									categoriaFiltro = Categoria.LIVROS;
								break;
								case 3:
									categoriaFiltro = Categoria.MATERIAL_ESCRITA;
								break;
								case 4:
									categoriaFiltro = Categoria.ROUPA;
								break;
								case 5:
									categoriaFiltro = Categoria.OUTROS;
								case 0:
									categoriaFiltro = null;
								default:
									System.out.println("Categoria invalida");
								}
								if(catnum >= 0 && catnum<=5) {
									break;
								}
							}
							
							System.out.println("Cor (Enter para não inserir):");
							String cor = sc.nextLine().trim();
							
							 System.out.println("Texto no título (Enter para ignorar, mínimo 3 letras):");
					         String titulo = sc.nextLine().trim();
					            if (titulo.length() > 0 && titulo.length() < 3) {
					                System.out.println("Menos de 3 letras, o título foi ignorado.");
					                titulo = "";
					            }
					            gr.imprimirAnuncios(gr.filtrarAnuncios(op,categoriaFiltro, cor,titulo));
							}
						}	
							
					
					break;
						
					case 3:
						System.out.println("Tipo de anúncio:");
					    System.out.println("1- Procura-se (Perdido).");
					    System.out.println("2- Encontrado.");
					    System.out.println("Opção: ");
					    int tipoAnuncio = inserirInt(sc);
					    
					    if(tipoAnuncio<1 || tipoAnuncio >2) {
					    	System.out.println("Erro- Tipo Invalido!");
					    	break;
					    }
	
					    System.out.println("Título: ");
					    String titulo = sc.nextLine();
	
					    System.out.println("Descrição: ");
					    String descricao = sc.nextLine();
	
					    Categoria categoria = null;
						while(true) {
							System.out.println("Categoria (Enter para não inserir):");
							System.out.println("1-Eletronicos | 2-Livros | 3-Material Escrita | 4-Roupa | 5-Outros");
							System.out.println("Categoria (0 para não inserir):");
	
							int catnum = inserirInt(sc);
							switch(catnum) {
							case 1:
								categoria = Categoria.ELETRONICOS;
							break;
							case 2:
								categoria = Categoria.LIVROS;
							break;
							case 3:
								categoria= Categoria.MATERIAL_ESCRITA;
							break;
							case 4:
								categoria = Categoria.ROUPA;
							break;
							case 5:
								categoria = Categoria.OUTROS;
							case 0:
								categoria = null;
							default:
								System.out.println("Categoria invalida");
							}
							if(catnum >= 0 && catnum<=5) {
								break;
							}
						}
	
					    System.out.println("Cor: ");
					    String cor = sc.nextLine();
					    
					    int tipoAnuncioTemp;
					    int opcao = -1;
					    while(opcao > 1 || opcao < 0) {
					    	
					    	if(tipoAnuncio == 1) {
					    		tipoAnuncioTemp = 2;
					    	}else {
					    		tipoAnuncioTemp = 1;
					    	}
					    	List<Anuncio> possiveisAnuncios = gr.filtrarAnuncios(tipoAnuncioTemp,categoria,cor ,titulo);
					    	if(possiveisAnuncios.isEmpty()) {
					    		opcao = 0;	
					    	}else {
							    System.out.println("Possiveis anunicos:");
								gr.imprimirAnuncios(gr.filtrarAnuncios(tipoAnuncioTemp,categoria,cor ,titulo));
								
								System.out.println("Um destes anuncios corresponde ao que queria criar? (1-Sim / 0-Não).");
								System.out.println("Opção: ");
								opcao = inserirInt(sc);
					    	}
					    }
					    if(tipoAnuncio==1) {
					    	tipoAnuncioTemp = 2;
					    }
					    else {
					    	tipoAnuncioTemp = 1;
					    }
					    switch (opcao) {
					    case 1:
					    	System.out.println("Qual anuncio quer reivindicar:");
							int Rid = inserirInt(sc);
							gr.reivindicar(Rid, gr.getPerfil().getIdUtilizador(), tipoAnuncioTemp);
					    break;
					    
					    case 0:
						    if (tipoAnuncio == 1) {
						        System.out.print("Data da perda (AAAA-MM-DD) NOTA: Se foi hoje insira ENTER ");
						        String dataStr =sc.nextLine().trim();
						        
						        LocalDate dataP;
						        if ( dataStr.isEmpty()) {
						        	dataP = LocalDate.now();
						        }
						        else {
						        	dataP = LocalDate.parse(dataStr);
						        }
						        gr.criarAnuncioPerdido(titulo, categoria, descricao, cor, dataP);
						        
						    } 
						    
						    else if (tipoAnuncio == 2) {
						        System.out.println("Onde foi encontrado (Localização): ");
						        String localizacao = sc.nextLine();
						        gr.criarAnuncioEncontrado(titulo, categoria, descricao, cor, localizacao);
						        
						    } 
					    break;
					    }
					break;	
					    
					case 4:
						List <Chat> meusChats = gr.listarChats(gr.getPerfil());
						gr.imprimirChats(meusChats);
						
						if(!meusChats.isEmpty()) {
							System.out.print("\n Digite o ID do CHAT para abrir (0 para voltar) : ");
							int idChat = inserirInt(sc);
							
							if (idChat !=0 && gr.verificarIdChat(idChat,meusChats) ) {

								gr.mostrarMensagensChat(idChat);
								System.out.println("(Nota: Se for o dono do anúncio, escreva 'Este anuncio ficou resolvido' para fechar o anúncio)");
								System.out.print("Escreva a sua mensagem (Enter para cancelar): ");
								String msg = sc.nextLine();
								if (!msg.isBlank()) {
									gr.enviarMensagem(idChat, gr.getPerfil().getIdUtilizador(), msg);
							}
						}
							else {
								System.out.println("Chat Invalido!");
							}
					}
					break;
					
					case 5:
						Utilizador u = gr.getPerfil();
						System.out.println("\n OS MEUS DADOS PESSOAIS");
						System.out.println("Id   : " + u.getIdUtilizador());
						System.out.println("Nome : " + u.getNome());
						System.out.println("Email: " + u.getEmail());
	
						System.out.print("\nDeseja atualizar os seus dados? (1- Sim / 0- Não): ");
						int atualizar = inserirInt(sc);
	
						if (atualizar == 1) {
							System.out.println("Nos campos que não pretender alterar, prima ENTER para avançar.");
							System.out.println("---------------------------------------------------------------");
							System.out.print("Novo Nome: ");
							String novoNome = sc.nextLine();
							System.out.print("Novo Email: ");
							String novoEmail = sc.nextLine();
							System.out.print("Nova Password: ");
							String novaPass = sc.nextLine();
							gr.atualizarDadosPessoais(u.getIdUtilizador(), novoNome, novoEmail, novaPass);
						}
						
					break;
					
						
					case 6:
						List <Anuncio> meusAnuncios = gr.listarAnuncios(gr.getPerfil());
	                    gr.imprimirAnuncios(meusAnuncios);
	                    if(!meusAnuncios.isEmpty()) {
	                        System.out.print("Id do anúncio a eliminar: ");
	                        int idEliminar = inserirInt(sc);
	                        gr.eliminarAnuncio(idEliminar,gr.getPerfil().getIdUtilizador());
	                    }
						break;
						
					case 0:
						gr.logout();
						System.out.println("A fechar");
						break;
						
					default:
						System.out.println("Opção Invalida! Tente novamente!");
					}
				}
			}
		        
			// Menu do gestor
			else {
	            while(op != 0) {
	                System.out.println("Menu Gestor");
	                System.out.println("1- Visualizar estatísticas.");
	                System.out.println("0- Logout");
	                System.out.println("Opção: ");
	                op = inserirInt(sc);
	                switch (op) {
	                    case 1:
	                        System.out.print("Data de início (AAAA-MM-DD) ou Enter para últimos 30 dias: ");
	                        String dInicioStr = sc.nextLine().trim();
	                        LocalDate inicio = dInicioStr.isEmpty() ? LocalDate.now().minusDays(30) : LocalDate.parse(dInicioStr);
	                        
	                        LocalDate fim;
	                        if(!dInicioStr.isEmpty()) {
	                        	System.out.print("Data de fim (AAAA-MM-DD) ou Enter para hoje: ");
	 	                        String dFimStr = sc.nextLine().trim();
	 	                        fim = dFimStr.isEmpty() ? LocalDate.now() : LocalDate.parse(dFimStr);
	                        }else {
	                        	fim = LocalDate.now();
	                        }

	                        gr.mostrarEstatisticas(inicio, fim);
	                    break;
	                    case 0:
	                        gr.logout();
	                        System.out.println("A fechar");
	                    break;
	                    default:
	                        System.out.println("Opção Invalida! Tente novamente!");
	                    break;
	                }
                
	            }
            
	        }
			
		}
		
		gr.exit();
		sc.close();
		System.out.println("Programa encerrado.");
	}
	
	public static int inserirInt(Scanner sc) {
		while(true) {
			try {
				int a=sc.nextInt();
				sc.nextLine();
				return a;
			}catch(InputMismatchException e) {
				System.out.println("So e valido numeros inteiros, insira de novo:\n");
				sc.nextLine();
				continue;
			}
		}
	}
}
