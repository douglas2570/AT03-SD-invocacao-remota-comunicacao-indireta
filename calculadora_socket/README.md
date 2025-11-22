## Visão Geral do Projeto
Este projeto implementa uma calculadora distribuída simples utilizando a comunicação de Socket em Java. A aplicação é dividida em dois componentes principais: um Servidor que realiza o cálculo e um Cliente que envia a expressão.

O servidor suporta expressões aritméticas complexas, respeitando a ordem de operações e parênteses (ex: `10 + 2 * (8 / 4)`).

### 🛠️ Pré-requisitos
Para rodar este projeto, você precisa ter o Java Development Kit (JDK) instalado no seu sistema. O projeto foi desenvolvido com o JDK 17, mas funcionará com qualquer versão moderna do Java (JDK 8 ou superior), desde que você compile o código localmente.

Verifique sua versão no terminal:

```bash
java -version
javac -version
```

### 🏃 Como Rodar o Projeto
Você deve compilar e executar o servidor e o cliente em terminais separados.

#### Passo 1: Compilação

1.  Navegue até o diretório onde estão os arquivos de código-fonte (.java).

2. Use o compilador Java (javac) para transformar os arquivos .java em bytecode (.class):
```bash
javac *.java
```

#### Passo 2: Iniciar o Servidor

Abra o Terminal 1 e execute a classe principal do servidor. Ele inicializará e ficará aguardando por conexões na porta `12345`.

```bash
java ServidorCalculadora
```

- Saída esperada: `Servidor iniciado na porta 12345. Aguardando conexão...`
- Mantenha este terminal aberto.

#### Passo 3: Iniciar e Usar o Cliente
Abra o Terminal 2 e execute a classe do cliente.

```bash
java ClienteCalculadora
```

1. O cliente se conectará ao servidor e pedirá a entrada.
2. Digite a expressão desejada (ex: `5 + 3 * (12 / 6)`).
3. O cliente enviará a expressão, e o resultado retornado pelo servidor será exibido no seu terminal.