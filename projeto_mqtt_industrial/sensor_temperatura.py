import paho.mqtt.client as mqtt
import random
import time
import json
from datetime import datetime

# ==============================================================================
# CONFIGURAÇÕES E VARIÁVEIS GLOBAIS
# ==============================================================================
# Este script assume que o serviço Mosquitto está rodando na máquina local.
# Certifique-se de que a porta 1883 está liberada e o serviço ativo.
BROKER = 'localhost'
PORT = 1883
TOPIC_DADOS = 'caldeira/temperatura/dados'
SENSOR_ID = 'SENSOR_CALDEIRA_01'

# ==============================================================================
# FUNÇÕES DE CALLBACK
# ==============================================================================
def on_connect(client, userdata, flags, rc, properties=None):
    """
    Função chamada quando a conexão com o broker é estabelecida.
    rc = 0 indica sucesso.
    """
    if rc == 0:
        print(f"[CONEXÃO] Conectado ao Broker MQTT local em {BROKER}:{PORT}")
    else:
        print(f"[ERRO] Falha na conexão. Código de retorno: {rc}")

# ==============================================================================
# CONFIGURAÇÃO DO CLIENTE MQTT
# ==============================================================================
# Inicializa o cliente MQTT (usando a versão de API mais recente recomendada)
client = mqtt.Client(mqtt.CallbackAPIVersion.VERSION2)

# Define a função de callback para conexão
client.on_connect = on_connect

print("Tentando conectar ao broker local...")
try:
    # Conecta ao broker (localhost, porta 1883, keepalive 60s)
    client.connect(BROKER, PORT, 60)
except ConnectionRefusedError:
    print("ERRO CRÍTICO: Não foi possível conectar ao 'localhost'.")
    print("Verifique se o serviço Mosquitto está rodando.")
    exit(1)

# Inicia o loop de rede em uma thread separada para manter a conexão ativa
client.loop_start()

# ==============================================================================
# LOOP PRINCIPAL DE SIMULAÇÃO
# ==============================================================================
try:
    print(f"Iniciando simulação do sensor {SENSOR_ID}...")
    
    while True:
        # 1. Simular leitura de temperatura (entre 180 e 250 graus)
        temperatura = random.uniform(180.0, 250.0)
        
        # 2. Capturar timestamp atual
        timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        
        # 3. Montar o payload JSON
        payload_dict = {
            "sensor_id": SENSOR_ID,
            "timestamp": timestamp,
            "temperatura": round(temperatura, 2) # Arredondando para 2 casas decimais
        }
        payload_json = json.dumps(payload_dict)
        
        # 4. Publicar no tópico com QoS 1
        info = client.publish(TOPIC_DADOS, payload_json, qos=1)
        info.wait_for_publish() # Garante que a mensagem saiu
        
        # Feedback visual no console para você saber que está funcionando
        print(f"[ENVIADO] Tópico: {TOPIC_DADOS} | Payload: {payload_json}")
        
        # 5. Aguardar 60 segundos antes da próxima leitura
        time.sleep(60)

except KeyboardInterrupt:
    print("\nSimulação encerrada pelo usuário.")
    client.loop_stop()
    client.disconnect()