import org.junit.Test;

public class TesteMinesweeper {

	@Test
	public void testeZap() throws InterruptedException {
		/* trocar path para local onde o chromedriver está */
		System.setProperty("webdriver.chrome.driver", "C:/Users/matheus.gomes/Documents/chromedriver/chromedriver2.exe");		
		Tabuleiro tabuleiro = Tabuleiro.getInstance();
		tabuleiro.trocaDificuldade();
//		tabuleiro.trocaDificuldade();
//		tabuleiro.trocaDificuldade();
//		tabuleiro.inicializaMatriz(9);
		tabuleiro.inicializaMatriz(16);
//		tabuleiro.inicializaMatriz(50);
		tabuleiro.run();
	}
}
