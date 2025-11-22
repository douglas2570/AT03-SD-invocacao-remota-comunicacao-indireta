## Visão Geral do Projeto

Este projeto implementa uma calculadora distribuída usando a tecnologia RMI (Remote Method Invocation) do Java.

### 🛠️ Pré-requisitos
Para rodar este projeto, você precisa ter o Java Development Kit (JDK) instalado no seu sistema. O projeto foi desenvolvido com o JDK 17, mas funcionará com qualquer versão moderna do Java (JDK 8 ou superior), desde que você compile o código localmente.

Verifique sua versão no terminal:

```bash
java -version
javac -version
```

### 🏃 Como Rodar o Projeto

O Servidor deve ser iniciado antes do Cliente.

#### Passo 1: Compilação

1.  Navegue até o diretório onde estão os arquivos de código-fonte (`.java`).

2. Use o compilador Java (`javac`) para transformar os arquivos .java em bytecode (``.class``):
```bash
javac *.java
```

#### Passo 2: Iniciar o Servidor e o Registry

Abra o Terminal 1 e execute a classe principal do servidor. Esta ação inicia o RMI Registry na porta ``1099`` e registra o serviço.
```bash
java ServidorRMI
```

- Saída esperada: ``Servidor RMI iniciado com sucesso! Objeto remoto 'CalculadoraRemota' registrado na porta 1099.``
- Mantenha este terminal aberto. O servidor deve estar ativo para que o cliente possa encontrá-lo.

#### Passo 3: Iniciar e Usar o Cliente

Abra o Terminal 2 e execute a classe do cliente.
```bash
java ClienteRMI
```

1. O cliente fará o lookup no Registry do servidor, obtendo o Stub (proxy).
2. O terminal pedirá a expressão (ex: ``5 + 3 * (12 / 6)``).
3. O cliente executa a chamada de método remota e exibe o resultado retornado.

