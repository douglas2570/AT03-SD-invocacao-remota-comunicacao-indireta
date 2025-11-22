import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface Remota que define os métodos acessíveis pelo cliente RMI.
 * Todo método que pode ser chamado remotamente DEVE lançar RemoteException.
 */
public interface ICalculadoraRemota extends Remote {

    /**
     * Avalia uma expressão aritmética complexa no servidor.
     * @param expressao A string da expressão a ser calculada.
     * @return O resultado do cálculo (double).
     * @throws RemoteException Obrigatório para comunicação de rede.
     */
    double avaliarExpressao(String expressao) throws RemoteException;
}