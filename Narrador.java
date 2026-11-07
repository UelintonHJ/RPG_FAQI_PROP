

/**
 * Centraliza as mensagens da batalha. Cada linha espera alguns segundos antes
 * de aparecer, para o jogador conseguir acompanhar o desenrolar da ação.
 */
public class Narrador {

	private static final int PAUSA_MS = 2000;

	public static void narrar(String mensagem) {
		try {
			Thread.sleep(PAUSA_MS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		System.out.println(mensagem);
	}
}
