import paho.mqtt.client as mqtt
import json

# ==============================================================================
# CONFIGURAÇÕES E VARIÁVEIS GLOBAIS
# ==============================================================================
# Configuração para rodar localmente com o Mosquitto
BROKER = 'localhost'
PORT = 1883
TOPIC_DADOS = 'caldeira/temperatura/dados'
TOPIC_ALERTAS = 'caldeira/temperatura/alertas'

# Estado do serviço (Memória)
historico_temperaturas = []  # Lista para armazenar as temperaturas recebidas
media_anterior = None        # Média calculada no ciclo anterior (T-1)
media_atual = None           # Média calculada no ciclo atual (T)

# ==============================================================================
# FUNÇÕES DE CALLBACK
# ==============================================================================

def on_connect(client, userdata, flags, rc, properties=None):
    """
    Callback executado quando a conexão com o broker é estabelecida.
    """
    if rc == 0:
        print(f"[CAT] Conectado ao Broker local ({BROKER}:{PORT})")
        # Subscreve ao tópico de dados para começar a receber as leituras dos sensores
        client.subscribe(TOPIC_DADOS)
        print(f"[CAT] Assinado no tópico: {TOPIC_DADOS}")
    else:
        print(f"[CAT] Falha na conexão. Código: {rc}")

def on_message(client, userdata, msg):
    """
    Callback principal: Processa cada mensagem recebida do sensor.
    Contém a lógica de cálculo de média e geração de alertas.
    """
    global media_anterior, media_atual, historico_temperaturas
    
    try:
        # a. Deserializa o payload JSON recebido
        payload_str = msg.payload.decode('utf-8')
        dados = json.loads(payload_str)
        temp_recebida = float(dados['temperatura'])
        
        print(f"\n[DADO RECEBIDO] Sensor: {dados['sensor_id']} | Temp: {temp_recebida}°C")
        
        # b. Adiciona a nova temperatura ao histórico
        historico_temperaturas.append(temp_recebida)
        
        # c. Verifica se há dados suficientes para calcular a média (janela de 120s = 2 leituras de 60s)
        if len(historico_temperaturas) >= 2:
            # Pega as duas últimas temperaturas
            ultimas_duas = historico_temperaturas[-2:]
            
            # Atualiza os ponteiros de média
            # O que era 'atual' vira 'anterior' antes do novo cálculo
            media_anterior = media_atual
            
            # Calcula a nova média atual
            media_atual = sum(ultimas_duas) / 2
            
            print(f"[PROCESSAMENTO] Média (últimos 120s): {media_atual:.2f}°C")
            
            # e. Lógica de Alerta
            if media_atual is not None:
                
                # i. Alerta de Temperatura Alta
                if media_atual > 200.0:
                    msg_alerta = f"TEMPERATURA ALTA: Média atingiu {media_atual:.2f} °C"
                    publicar_alerta(client, msg_alerta)
                
                # ii. Alerta de Aumento Repentino
                # Só calcula se tivermos uma média anterior válida para comparar
                if media_anterior is not None:
                    diferenca = abs(media_atual - media_anterior)
                    if diferenca > 5.0:
                        msg_alerta = f"AUMENTO REPENTINO: Variação de {diferenca:.2f} °C entre médias"
                        publicar_alerta(client, msg_alerta)
        
            # f. Limpeza do histórico (opcional, mas boa prática para não estourar memória em longas execuções)
            # Mantemos apenas os últimos 2 itens que são usados para o cálculo
            if len(historico_temperaturas) > 2:
                historico_temperaturas.pop(0)
                
    except Exception as e:
        print(f"[ERRO] Falha ao processar mensagem: {e}")

def publicar_alerta(client, mensagem):
    """
    Função auxiliar para formatar e enviar o alerta ao tópico específico.
    """
    print(f"[ALERTA GERADO] >>> {mensagem}")
    # Cria um JSON simples para o alerta também
    payload_alerta = json.dumps({
        "tipo": "ALARME",
        "mensagem": mensagem,
        "timestamp": 1 # Simplificado, idealmente usar datetime
    })
    client.publish(TOPIC_ALERTAS, payload_alerta, qos=1)

# ==============================================================================
# EXECUÇÃO PRINCIPAL
# ==============================================================================
if __name__ == "__main__":
    # Inicializa o cliente MQTT
    client = mqtt.Client(mqtt.CallbackAPIVersion.VERSION2)
    
    # Define os callbacks
    client.on_connect = on_connect
    client.on_message = on_message
    
    print("Iniciando Serviço CAT (Compute Average Temperature)...")
    
    try:
        # Conecta ao broker local
        client.connect(BROKER, PORT, 60)
        
        # Mantém o script rodando infinitamente escutando mensagens
        client.loop_forever()
        
    except ConnectionRefusedError:
        print("ERRO CRÍTICO: Não foi possível conectar ao 'localhost'.")
        print("Certifique-se de que o Mosquitto está rodando.")
    except KeyboardInterrupt:
        print("\nServiço CAT encerrado.")