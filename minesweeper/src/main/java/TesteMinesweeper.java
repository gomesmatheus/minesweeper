import org.junit.Test;

public class TesteMinesweeper {

	@Test
	public void testeZap() throws InterruptedException {
		/* trocar path para local onde o chromedriver está */
		System.setProperty("webdriver.chrome.driver", "C:\\Users\\NAJA INFORMATICA\\Downloads\\a\\chromedriver.exe");		
		Tabuleiro tabuleiro = Tabuleiro.getInstance();
		//tabuleiro.trocaDificuldade();
		//tabuleiro.inicializaMatriz(16);
		tabuleiro.inicializaMatriz(9);
		tabuleiro.run();
	}
	
}
