import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Scanner;

/**
 * Classe Cliente RMI.
 * Localiza o objeto remoto no Registry e chama seu método 'avaliarExpressao'.
 */
public class ClienteRMI {

    private static final String NOME_SERVICO = "CalculadoraRemota";
    private static final String ENDERECO_SERVIDOR = "localhost";
    private static final int PORTA_REGISTRY = 1099;

    public static void main(String[] args) {
        try {
            // 1. Forma o URL completo para buscar o objeto remoto.
            String url = "//" + ENDERECO_SERVIDOR + ":" + PORTA_REGISTRY + "/" + NOME_SERVICO;

            // 2. Localiza o objeto remoto (o Stub) no RMI Registry.
            // O objeto retornado é o Stub, que implementa a ICalculadoraRemota.
            ICalculadoraRemota calculadoraRemota = (ICalculadoraRemota) Naming.lookup(url);

            System.out.println("Conectado ao Servidor RMI em " + ENDERECO_SERVIDOR + ":" + PORTA_REGISTRY);
            System.out.println("Digite a expressão completa (suporta múltiplos valores e parênteses).");
            System.out.println("Exemplo: 5 + 3 * (10 / 2 - 1)");
            System.out.print("> ");

            // Lê a entrada do usuário
            try (Scanner scanner = new Scanner(System.in)) {
                String operacao = scanner.nextLine();

                // 3. Chama o método remoto. A complexidade da rede é escondida.
                double resultado = calculadoraRemota.avaliarExpressao(operacao);

                // 4. Exibe o resultado.
                System.out.println("\n--- RESULTADO DO SERVIDOR RMI ---");
                System.out.println("Resultado: " + resultado);
                System.out.println("---------------------------------\n");
            }

        } catch (RemoteException re) {
            // Captura exceções da chamada remota (ex: erro de cálculo no servidor).
            System.err.println("Erro na comunicação remota: " + re.getMessage());
        } catch (Exception e) {
            // Captura exceções gerais (ex: Registry não encontrado ou nome de serviço incorreto).
            System.err.println("Erro ao conectar ou executar o cliente RMI: " + e.getMessage());
        }
    }
}