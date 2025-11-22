import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Classe que implementa a Interface Remota (ICalculadoraRemota).
 * Estender UnicastRemoteObject exporta o objeto para o sistema RMI.
 */
public class CalculadoraRemota extends UnicastRemoteObject implements ICalculadoraRemota {

    // Reutiliza a lógica de cálculo existente.
    private InterpretadorAritmetico interpretador = new InterpretadorAritmetico();

    // Construtor. Deve declarar RemoteException.
    public CalculadoraRemota() throws RemoteException {
        super();
        // Chama o construtor do UnicastRemoteObject, que exporta este objeto.
    }

    /**
     * Implementação do método remoto. Chamado diretamente pelo Stub do cliente.
     */
    @Override
    public double avaliarExpressao(String expressao) throws RemoteException {
        // Log para mostrar que a chamada remota chegou.
        System.out.println("Chamada remota recebida: " + expressao);

        try {
            // Delega o cálculo à lógica local (InterpretadorAritmetico).
            return interpretador.avaliarExpressao(expressao);
        } catch (IllegalArgumentException | ArithmeticException e) {
            // Se houver um erro de cálculo, lançamos uma RemoteException para que 
            // a mensagem de erro chegue ao cliente.
            System.err.println("Erro no cálculo: " + e.getMessage());
            throw new RemoteException("ERRO NO SERVIDOR: " + e.getMessage());
        }
    }
}