import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Classe principal do Servidor RMI. 
 * Responsável por criar a instância remota e registrá-la no RMI Registry.
 */
public class ServidorRMI {

    // Nome pelo qual o objeto será encontrado pelo cliente (chave no Registry).
    private static final String NOME_SERVICO = "CalculadoraRemota";
    private static final int PORTA_REGISTRY = 1099; // Porta padrão do RMI Registry

    public static void main(String[] args) {
        try {
            // 1. Cria ou obtém referência ao RMI Registry na porta 1099.
            // Para garantir que o Registry esteja rodando, podemos criá-lo:
            LocateRegistry.createRegistry(PORTA_REGISTRY);

            // 2. Cria a instância do objeto que fará o trabalho.
            ICalculadoraRemota calculadora = new CalculadoraRemota();

            // 3. Registra o objeto remoto no RMI Registry.
            // O cliente usará este nome ("CalculadoraRemota") para fazer o lookup.
            Naming.rebind("//localhost:" + PORTA_REGISTRY + "/" + NOME_SERVICO, calculadora);

            System.out.println("Servidor RMI iniciado com sucesso!");
            System.out.println("Objeto remoto '" + NOME_SERVICO + "' registrado na porta " + PORTA_REGISTRY + ".");

        } catch (Exception e) {
            System.err.println("Erro fatal no Servidor RMI: " + e.getMessage());
            e.printStackTrace();
        }
    }
}