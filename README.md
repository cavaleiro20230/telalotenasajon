# telalotenasajon

### Sistema de Envio de Lote em Java com SQL Server e Interface Gráfica

 interface gráfica completa para o sistema de envio de lotes da contabilidade para o Nasajon, utilizando JavaFX para uma experiência moderna e responsiva.

```javascript project="Sistema de Lote Nasajon - GUI" file="InterfaceGrafica.java"
...
```

## Visão Geral da Interface Gráfica

A interface gráfica do sistema de envio de lotes da contabilidade para o Nasajon foi projetada para ser intuitiva e funcional, oferecendo uma experiência completa para o usuário. Aqui estão os principais componentes:

### 1. Dashboard Principal





O dashboard principal apresenta:

- **Painel de estatísticas** com cards mostrando o total de lotes, lotes pendentes, enviados e com erro
- **Gráfico de pizza** ilustrando a distribuição dos lotes por status
- **Tabela de lotes recentes** com informações essenciais e menu de contexto para ações rápidas
- **Barra de ferramentas** com botões para as principais operações
- **Menu superior** para acesso a todas as funcionalidades do sistema


### 2. Tela de Configurações

A tela de configurações permite personalizar todos os aspectos do sistema:

- **Configurações de banco de dados SQL Server** (servidor, porta, nome do banco, credenciais)
- **Configurações da API Nasajon** (URL base, credenciais, timeout)
- **Configurações de processamento de lotes** (intervalo, tamanho máximo, retenção de logs)
- **Opções de notificação** para alertas de erros


### 3. Visualizador de Logs

O visualizador de logs oferece:

- **Filtros** por nível de log, data e texto
- **Visualização em texto formatado** para fácil leitura
- **Exportação** de logs para análise externa
- **Atualização em tempo real** dos registros de log


### 4. Detalhes do Lote

A tela de detalhes do lote mostra:

- **Informações gerais** do lote (código, status, datas)
- **Mensagens de erro** (quando aplicável)
- **Lista de lançamentos contábeis** incluídos no lote
- **Botões de ação** para reprocessar, consultar status e exportar dados


## Funcionalidades Principais

1. **Geração de lotes** a partir de lançamentos contábeis pendentes
2. **Envio de lotes** para o sistema Nasajon
3. **Monitoramento de status** dos lotes enviados
4. **Reprocessamento** de lotes com erro
5. **Configuração completa** do sistema
6. **Visualização de logs** para diagnóstico
7. **Exportação de dados** para relatórios externos


## Tecnologias Utilizadas

- **JavaFX** para a interface gráfica
- **Spring Boot** para a lógica de negócios
- **JDBC** para conexão com SQL Server
- **REST** para comunicação com a API Nasajon


Esta interface gráfica completa proporciona uma experiência de usuário moderna e eficiente para o gerenciamento de lotes contábeis, facilitando o trabalho dos profissionais de contabilidade na integração com o sistema Nasajon.
